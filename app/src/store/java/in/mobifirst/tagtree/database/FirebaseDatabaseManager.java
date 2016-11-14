package in.mobifirst.tagtree.database;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import in.mobifirst.tagtree.model.Store;
import in.mobifirst.tagtree.model.Token;
import in.mobifirst.tagtree.preferences.IQSharedPreferences;
import in.mobifirst.tagtree.util.ApplicationConstants;
import rx.Observable;
import rx.Subscriber;

public class FirebaseDatabaseManager implements DatabaseManager {
    private static final String TAG = "FirebaseDatabaseManager";

    private static final String TOKENS_CHILD = "tokens/";
    private static final String STORE_CHILD = "store/";
    private static final String TOPICS_CHILD = "topics/";

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

    //ToDo limit by date and status.
    public Observable<List<Token>> getAllTokens(final String uId) {
        return rx.Observable.create(new Observable.OnSubscribe<List<Token>>() {
            @Override
            public void call(final Subscriber<? super List<Token>> subscriber) {
                Query query = mDatabaseReference
                        .child(TOKENS_CHILD)
                        .orderByChild("storeId")
                        .equalTo(uId);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            HashMap<String, Token> tokens = dataSnapshot.getValue(new GenericTypeIndicator<HashMap<String, Token>>() {
                            });
                            if (tokens != null) {
                                subscriber.onNext(new ArrayList<>(tokens.values()));
                                subscriber.onCompleted();
                            } else {
                                subscriber.onError(new Exception("Empty Tokens."));
                                FirebaseCrash.report(new Exception("Empty Tokens"));
                                subscriber.onCompleted();
                            }
                        } else {
                            subscriber.onError(new Exception("Empty Tokens."));
                            FirebaseCrash.report(new Exception("Empty Tokens"));
                            subscriber.onCompleted();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "[fetch All Tokens] onCancelled:" + databaseError);
                        subscriber.onError(new Exception("Empty Tokens."));
                        FirebaseCrash.report(new Exception("Empty Tokens"));
                        subscriber.onCompleted();
                    }
                });
            }
        });
    }

    public void getTokenById(String tokenId, ValueEventListener valueEventListener) {
        DatabaseReference tokenRef = mDatabaseReference
                .child(TOKENS_CHILD)
                .child(tokenId);
        tokenRef.addValueEventListener(valueEventListener);
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
                        Long currentToken = (Long) dataSnapshot.getValue();
                        {
                            String key = mDatabaseReference.child(TOKENS_CHILD)
                                    .push().getKey();

                            final Token newToken = new Token(key, token.getStoreId(),
                                    token.getPhoneNumber(),
                                    currentToken,
                                    mSharedPrefs.getSting((ApplicationConstants.PROFILE_PIC_URL_KEY)),
                                    mSharedPrefs.getSting(ApplicationConstants.DISPLAY_NAME_KEY),
                                    token.getCounter());

                            mDatabaseReference.child(TOKENS_CHILD).child(key).setValue(newToken.toMap());

//                            sendPushToClient(newToken.getuId(), newToken.getPhoneNumber());

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
                        FirebaseCrash.report(new Exception("On Failure");
                    }
                });
    }

    private final static String mMsg91Url = "https://control.msg91.com/api/sendhttp.php?";

    private void sendSMS(Token token) {
        //Android SMS API integration code

        //Your authentication key
        String authkey = "128441AGQNt0b0eb2q580367e2";
        //Multiple mobiles numbers separated by comma
        String mobiles = token.getPhoneNumber();
        //Sender ID,While using route4 sender id should be 6 characters long.
        String senderId = "InstaQ";
        //Your message to send, Add URL encoding here.
        String message = "Your token number = " + token.getTokenNumber();
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
    }

    private void updateTopicsForPushNotification(Token token) {
        HashMap<String, String> result = new HashMap<>();
        result.put("tokenId", token.getuId());
        result.put("phoneNumber", token.getPhoneNumber());
//        result.put("tokenNumber", token.getTokenNumber());
        mDatabaseReference.child(TOPICS_CHILD).child(token.getuId()).setValue(result);
    }

    public void updateToken(Token token) {
        mDatabaseReference
                .child(TOKENS_CHILD)
                .child(token.getuId())
                .setValue(token);
    }

    private void incrementTokenCounter(Token token, @NonNull Transaction.Handler handler) {
        DatabaseReference tokenCounterRef = mDatabaseReference
                .child("store")
                .child(token.getStoreId())
                .child("tokenCounter");
        tokenCounterRef.runTransaction(handler);
    }
}

