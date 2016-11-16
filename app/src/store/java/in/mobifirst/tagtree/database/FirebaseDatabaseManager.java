package in.mobifirst.tagtree.database;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import in.mobifirst.tagtree.BuildConfig;
import in.mobifirst.tagtree.model.Store;
import in.mobifirst.tagtree.model.Token;
import in.mobifirst.tagtree.model.User;
import in.mobifirst.tagtree.preferences.IQSharedPreferences;
import in.mobifirst.tagtree.util.ApplicationConstants;
import rx.Observable;
import rx.Subscriber;

public class FirebaseDatabaseManager implements DatabaseManager {
    private static final String TAG = "FirebaseDatabaseManager";

    private static final String TOKENS_CHILD = "tokens/";
    private static final String STORE_CHILD = "store/";
    private static final String TOPICS_CHILD = "topics/";
    private static final String TOKENS_HISTORY_CHILD = "token-history";
    private final static String mMsg91Url = "https://control.msg91.com/api/sendhttp.php?";

    private DatabaseReference mDatabaseReference;
    private IQSharedPreferences mSharedPrefs;

    @Inject
    public FirebaseDatabaseManager(IQSharedPreferences iqSharedPreferences) {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mSharedPrefs = iqSharedPreferences;
    }

    public DatabaseReference getDatabaseReference() {
        return mDatabaseReference;
    }

    public Query getTokensRef(String uId) {
        return mDatabaseReference
                .child(TOKENS_CHILD)
                .orderByChild("storeId")
                .equalTo(uId);
    }

    //ToDo limit by date and status.
    public Observable<List<Token>> getAllTokens(final String uId) {
        return rx.Observable.create(new Observable.OnSubscribe<List<Token>>() {
            @Override
            public void call(final Subscriber<? super List<Token>> subscriber) {
                final Query query = mDatabaseReference
                        .child(TOKENS_CHILD)
                        .orderByChild("storeId")
                        .equalTo(uId);
//                query.addChildEventListener(new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                        Log.e(TAG, "onChildAdded");
//                    }
//
//                    @Override
//                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                        Log.e(TAG, "onChildChanged");
//                    }
//
//                    @Override
//                    public void onChildRemoved(DataSnapshot dataSnapshot) {
//                        Log.e(TAG, "onChildRemoved");
//                    }
//
//                    @Override
//                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//                        Log.e(TAG, "onChildMoved");
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                        Log.e(TAG, "onCancelled");
//                    }
//                });

                final ValueEventListener listener = query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.e(TAG, "onDataChange --> " + subscriber.toString());
                        if (!subscriber.isUnsubscribed()) {
                            if (dataSnapshot != null) {
                                HashMap<String, Token> tokens = dataSnapshot.getValue(new GenericTypeIndicator<HashMap<String, Token>>() {
                                });
                                if (tokens != null) {
                                    subscriber.onNext(new ArrayList<>(tokens.values()));
                                    subscriber.onCompleted();
                                } else {
                                    FirebaseCrash.report(new Exception("Empty Tokens"));
                                    subscriber.onCompleted();
                                }
                            } else {
                                FirebaseCrash.report(new Exception("Empty Tokens"));
                                subscriber.onCompleted();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "[fetch All Tokens] onCancelled:" + databaseError);
                        subscriber.onError(new Exception("Empty Tokens."));
                        FirebaseCrash.report(new Exception("Empty Tokens"));
                    }
                });

//                // When the subscription is cancelled, remove the listener
//                subscriber.add(Subscriptions.create(new Action0() {
//                    @Override
//                    public void call() {
//                        query.removeEventListener(listener);
//                    }
//                }));
            }
        });
    }

    public void getTokenById(String tokenId, ValueEventListener valueEventListener) {
        DatabaseReference tokenRef = mDatabaseReference
                .child(TOKENS_CHILD)
                .child(tokenId);
        tokenRef.addListenerForSingleValueEvent(valueEventListener);
    }

    public Observable<Token> getTokenById(final String tokenId) {
        return Observable.create(new Observable.OnSubscribe<Token>() {
            @Override
            public void call(final Subscriber<? super Token> subscriber) {
                getTokenById(tokenId, new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Token token = dataSnapshot.getValue(Token.class);
                        subscriber.onNext(token);
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "[fetch Token] onCancelled:" + databaseError);
                        subscriber.onCompleted();
                    }
                });
            }
        });
    }

    public void addNewToken(final Token token, final Subscriber<? super String> subscriber) {
        incrementTokenCounter(token, new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Long currentValue = mutableData.getValue(Long.class);
                if (currentValue == null) {
                    mutableData.setValue(1);
                } else {
                    mutableData.setValue(currentValue + 1);
                }

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                if (databaseError == null) {
                    if (committed) {
                        {
                            final Long currentToken = (Long) dataSnapshot.getValue();
                            DatabaseReference storeRef = mDatabaseReference.getRef()
                                    .child("store")
                                    .child(token.getStoreId());


                            storeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot storeDataSnapshot) {
                                    if (storeDataSnapshot.exists()) {

                                        String key = mDatabaseReference.child(TOKENS_CHILD)
                                                .push().getKey();
                                        Store store = storeDataSnapshot.getValue(Store.class);
                                        String areaName = store.getArea();
                                        final Token newToken = new Token(key, token.getStoreId(),
                                                token.getPhoneNumber(),
                                                currentToken,
                                                mSharedPrefs.getSting((ApplicationConstants.PROFILE_PIC_URL_KEY)),
                                                mSharedPrefs.getSting(ApplicationConstants.DISPLAY_NAME_KEY),
                                                token.getCounter(),
                                                areaName);

                                        mDatabaseReference.child(TOKENS_CHILD).child(key).setValue(newToken.toMap());
                                        checkSMSSending(newToken, false);
                                    } else {
                                        Log.e(TAG, "Snapshot is null");
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.e(TAG, "[fetch Area name] onCancelled:" + databaseError);
                                }
                            });

                            subscriber.onNext(null);
                            subscriber.onCompleted();
                        }
                    } else {
                        subscriber.onError(databaseError.toException());
                    }
                } else {
                    subscriber.onError(databaseError.toException());
                }

            }
        });
    }

    public void getStoreById(String storeId, ValueEventListener valueEventListener) {
        DatabaseReference storeRef = mDatabaseReference
                .child(STORE_CHILD)
                .child(storeId);
        storeRef.addListenerForSingleValueEvent(valueEventListener);
    }

    public void getStoreById(String uid, final Subscriber<? super Store> subscriber) {
        getStoreById(uid, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    Store store = dataSnapshot.getValue(Store.class);
                    subscriber.onNext(store);
                } else {
                    subscriber.onNext(null);
                }
                subscriber.onCompleted();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "[fetch Store] onCancelled:" + databaseError);
                subscriber.onError(databaseError.toException());
            }
        });
    }

    public void addStore(String uid, final Store store, final Subscriber<? super String> subscriber) {
        mDatabaseReference
                .child(STORE_CHILD)
                .child(uid)
                .setValue(store.toMap())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        subscriber.onError(e);
                        FirebaseCrash.report(new Exception("On Failure"));
                    }
                });
    }


    private void checkSMSSending(final Token token, final boolean status) {

        Query query = mDatabaseReference.getRef()
                .child("users")
                .orderByChild("phoneNumber")
                .equalTo(token.getPhoneNumber());
        query.addListenerForSingleValueEvent(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot usersnapshot) {
                if (usersnapshot == null || !usersnapshot.exists()) {
                    //user is not present
                    sendSMS(token, status);
                } else {
                    //User user = usersnapshot.getValue(User.class);
                    //User is already present
                }
            }

            @Override
            public void onCancelled(DatabaseError userdatabaseerror) {
                Log.e(TAG, "[fetch User] onCancelled:" + userdatabaseerror);

            }

        });
    }

    private void sendSMS(Token token, boolean status) {


        //Android SMS API integration code

        //Your authentication key
        String authkey = BuildConfig.SMS91_SECRET;
        //Multiple mobiles numbers separated by comma
        String mobiles = token.getPhoneNumber();
        //Sender ID,While using route4 sender id should be 6 characters long.
        String senderId = "TagTre";
        String message = "";
        //Your message to send, Add URL encoding here.
        if (status == false) {
            message = "You have received a token from " + token.getSenderName() + " "
                    + token.getAreaName() + " branch" + ". Token number = " + (token.getTokenNumber()) + ", Counter Number = " + token.getCounter()
                    + ". Please download https://play.google.com/apps/testing/in.mobifirst.tagtree.client for real time updates on Android.";
        } else {
            message = "Please report at the counter in " + token.getSenderName() + " " + token.getAreaName() + " branch."
                    + "Token number = " + token.getTokenNumber() + ", Counter Number = " + token.getCounter()
                    + ". Get more real time updates through app @ https://play.google.com/apps/testing/in.mobifirst.tagtree.client";
        }
        //define route
        String route = "4"; //4 For transaction, check with msg91

        URLConnection myURLConnection = null;
        URL myURL = null;
        BufferedReader reader = null;

        //encoding message
        String encoded_message = URLEncoder.encode(message);

        //Send SMS API
        String mainUrl = mMsg91Url;

        //Prepare parameter string
        StringBuilder sbPostData = new StringBuilder(mainUrl);
        sbPostData.append("authkey=" + authkey);
        sbPostData.append("&mobiles=" + mobiles);
        sbPostData.append("&message=" + encoded_message);
        sbPostData.append("&route=" + route);
        sbPostData.append("&sender=" + senderId);

        //final string
        mainUrl = sbPostData.toString();
        class SendSMSTask extends AsyncTask<String, Integer, Long> {
            protected Long doInBackground(String... urls) {
                try {
                    //prepare connection
                    URL myURL = new URL(urls[0]);
                    URLConnection myURLConnection = myURL.openConnection();
                    myURLConnection.connect();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));

                    //reading response
                    String response;
                    while ((response = reader.readLine()) != null)
                        //print response
                        Log.d("RESPONSE", "" + response);

                    //finally close connection
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    FirebaseCrash.report(new Exception("Error in Sending SMS: " + e.getMessage()));
                }
                return 0L;
            }
        }
        //Uncomment this  to execute the send sms
        //new SendSMSTask().execute(mainUrl);
        incrementSMS(token, new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Long currentValue = mutableData.getValue(Long.class);
                if (currentValue == null) {
                    mutableData.setValue(1);
                } else {
                    mutableData.setValue(currentValue + 1);
                }


                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {

            }
        });
    }

    private void updateTopicsForPushNotification(Token token) {
        HashMap<String, String> result = new HashMap<>();
        result.put("tokenId", token.getuId());
        result.put("phoneNumber", token.getPhoneNumber());
//        result.put("tokenNumber", token.getTokenNumber());
        mDatabaseReference.child(TOPICS_CHILD).child(token.getuId()).setValue(result);
    }

    public void updateToken(Token token) {
        if (token.isCompleted()) {
            //Move it to token-history table.
            mDatabaseReference
                    .child(TOKENS_HISTORY_CHILD)
                    .push()
                    .setValue(token.toMap());

            //Remove it from the token table
            mDatabaseReference
                    .child(TOKENS_CHILD)
                    .child(token.getuId())
                    .removeValue();
        } else {
            //Check for network connectivity
            if (token.getBuzzCount() == 1) {
                //Inform user if he is not present in the user table
                checkSMSSending(token, true);
            }
            mDatabaseReference
                    .child(TOKENS_CHILD)
                    .child(token.getuId())
                    .setValue(token);
        }
    }

    private void decrementCredits(Token token, @NonNull Transaction.Handler handler) {
        DatabaseReference creditsRef = mDatabaseReference
                .child("store")
                .child(token.getStoreId())
                .child("credits");
        creditsRef.runTransaction(handler);

    }

    private void incrementSMS(Token token, @NonNull Transaction.Handler handler) {
        DatabaseReference creditsRef = mDatabaseReference
                .child("store")
                .child(token.getStoreId())
                .child("smsCounter");
        creditsRef.runTransaction(handler);

    }

    private void incrementTokenCounter(Token token, @NonNull Transaction.Handler handler) {
        DatabaseReference tokenCounterRef = mDatabaseReference
                .child("store")
                .child(token.getStoreId())
                .child("tokenCounter");
        tokenCounterRef.runTransaction(handler);


        decrementCredits(token, new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Long currentValue = mutableData.getValue(Long.class);
                if (currentValue == null) {
                    mutableData.setValue(1000);
                } else {
                    mutableData.setValue(currentValue - 1);
                }


                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {

            }
        });


    }
}

