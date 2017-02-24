package in.mobifirst.tagtree.database;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceManager;
import android.text.TextUtils;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;

import in.mobifirst.tagtree.BuildConfig;
import in.mobifirst.tagtree.application.IQStoreApplication;
import in.mobifirst.tagtree.model.Store;
import in.mobifirst.tagtree.model.Token;
import in.mobifirst.tagtree.model.User;
import in.mobifirst.tagtree.preferences.IQSharedPreferences;
import in.mobifirst.tagtree.tokens.Snap;
import in.mobifirst.tagtree.util.ApplicationConstants;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

public class FirebaseDatabaseManager implements DatabaseManager {
    private static final String TAG = "FirebaseDatabaseManager";

    private static final String TOKENS_CHILD = "tokens/";
    private static final String STORE_CHILD = "store/";
    private static final String COUNTERS_CHILD = "counters/";
    private static final String COUNTERS_AVG_TAT_CHILD = "avgTurnAroundTime/";
    private static final String COUNTERS_AVG_BURST_CHILD = "avgBurstTime/";
    private static final String COUNTERS_LAST_ACTIVE_TOKEN = "activatedToken/";
    private static final String COUNTERS_USERS = "counterUserCount/";
    private static final String TOPICS_CHILD = "topics/";
    private static final String TOKENS_HISTORY_CHILD = "token-history";
    private final static String mMsg91Url = "https://control.msg91.com/api/sendhttp.php?";
    private final static String mMsgBulkSMSUrl = "http://login.bulksmsgateway.in/sendmessage.php?";

    private final static String CLIENT_APP_PLAYSTORE_URL = "https://goo.gl/mVAdpT";

    private DatabaseReference mDatabaseReference;
    private IQSharedPreferences mSharedPrefs;
    private IQStoreApplication mIQStoreApplication;

    @Inject
    public FirebaseDatabaseManager(IQStoreApplication application, IQSharedPreferences iqSharedPreferences) {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mIQStoreApplication = application;
        mSharedPrefs = iqSharedPreferences;
    }

    private class IncremnetTransactionHander implements Transaction.Handler {

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
    }

    ;

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
    public Observable<List<Snap>> getAllSnaps(final String uId) {
        return rx.Observable.create(new Observable.OnSubscribe<List<Snap>>() {
            @Override
            public void call(final Subscriber<? super List<Snap>> subscriber) {
                final Query query = mDatabaseReference
                        .child(TOKENS_CHILD)
                        .orderByChild("storeId")
                        .equalTo(uId);

                final ValueEventListener listener = query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.e(TAG, "onDataChange --> " + subscriber.toString());
                        if (!subscriber.isUnsubscribed()) {
                            if (dataSnapshot != null) {
                                HashMap<String, Token> tokens = dataSnapshot.getValue(new GenericTypeIndicator<HashMap<String, Token>>() {
                                });
                                if (tokens != null) {

                                    Observable.just(new ArrayList<>(tokens.values()))
                                            .flatMap(new Func1<List<Token>, Observable<Token>>() {
                                                @Override
                                                public Observable<Token> call(List<Token> tokens) {
                                                    return Observable.from(tokens);
                                                }
                                            })
                                            .toSortedList(new Func2<Token, Token, Integer>() {
                                                @Override
                                                public Integer call(Token token, Token token2) {
                                                    return new Long(token.getTokenNumber()).compareTo(token2.getTokenNumber());
                                                }
                                            })
                                            .flatMap(new Func1<List<Token>, Observable<Token>>() {
                                                @Override
                                                public Observable<Token> call(List<Token> tokens) {
                                                    return Observable.from(tokens);
                                                }
                                            })
                                            .filter(new Func1<Token, Boolean>() {
                                                @Override
                                                public Boolean call(Token token) {
                                                    return !token.isCompleted();
                                                }
                                            })
                                            .toMultimap(new Func1<Token, Integer>() {
                                                @Override
                                                public Integer call(Token token) {
                                                    return token.getCounter();
                                                }
                                            })
                                            .map(new Func1<Map<Integer, Collection<Token>>, List<Snap>>() {
                                                @Override
                                                public List<Snap> call(Map<Integer, Collection<Token>> integerCollectionMap) {
                                                    TreeMap<Integer, Collection<Token>> sortedMap = new TreeMap<>();
                                                    sortedMap.putAll(integerCollectionMap);
                                                    ArrayList<Snap> snaps = new ArrayList<>(sortedMap.size());
                                                    Iterator<Integer> keyIterator = sortedMap.keySet().iterator();
                                                    while (keyIterator.hasNext()) {
                                                        int key = keyIterator.next();
                                                        Snap snap = new Snap(key, new ArrayList<>(sortedMap.get(key)));
                                                        snaps.add(snap);
                                                    }
                                                    return snaps;
                                                }
                                            })
                                            .subscribe(new Action1<List<Snap>>() {
                                                @Override
                                                public void call(List<Snap> snapList) {
                                                    subscriber.onNext(snapList);
                                                }
                                            });
                                } else {
                                    FirebaseCrash.report(new Exception("Empty Tokens"));
                                    subscriber.onNext(null);
                                }
                            } else {
                                FirebaseCrash.report(new Exception("Empty Tokens"));
                                subscriber.onNext(null);
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
            }
        });
    }

    //ToDo limit by date and status.
    public Observable<List<Token>> getAllTokens(final String uId, final int currentCounter) {
        return rx.Observable.create(new Observable.OnSubscribe<List<Token>>() {
            @Override
            public void call(final Subscriber<? super List<Token>> subscriber) {
                final Query query = mDatabaseReference
                        .child(TOKENS_CHILD)
                        .orderByChild("storeId")
                        .equalTo(uId);

                final ValueEventListener listener = query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.e(TAG, "onDataChange --> " + subscriber.toString());
                        if (!subscriber.isUnsubscribed()) {
                            if (dataSnapshot != null) {
                                HashMap<String, Token> tokens = dataSnapshot.getValue(new GenericTypeIndicator<HashMap<String, Token>>() {
                                });
                                if (tokens != null) {
                                    Observable.just(new ArrayList<>(tokens.values()))
                                            .flatMap(new Func1<List<Token>, Observable<Token>>() {
                                                @Override
                                                public Observable<Token> call(List<Token> tokens) {
                                                    return Observable.from(tokens);
                                                }
                                            })
                                            .toSortedList(new Func2<Token, Token, Integer>() {
                                                @Override
                                                public Integer call(Token token, Token token2) {
                                                    return new Long(token.getTokenNumber()).compareTo(token2.getTokenNumber());
                                                }
                                            })
                                            .flatMap(new Func1<List<Token>, Observable<Token>>() {
                                                @Override
                                                public Observable<Token> call(List<Token> tokens) {
                                                    return Observable.from(tokens);
                                                }
                                            })
                                            .filter(new Func1<Token, Boolean>() {
                                                @Override
                                                public Boolean call(Token token) {
                                                    return token.getCounter() == currentCounter && !token.isCompleted();
                                                }
                                            })
                                            .toList()
                                            .subscribe(new Action1<List<Token>>() {
                                                @Override
                                                public void call(List<Token> tokenList) {
                                                    subscriber.onNext(tokenList);
                                                }
                                            });
                                } else {
                                    FirebaseCrash.report(new Exception("Empty Tokens"));
                                    subscriber.onNext(null);
                                }
                            } else {
                                FirebaseCrash.report(new Exception("Empty Tokens"));
                                subscriber.onNext(null);
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

    private void addTokenUnderStoreCounter(Token token) {
        //Save the token Id under the token number which makes client job easy in sorting and calculating TAT.
        mDatabaseReference
                .child(STORE_CHILD)
                .child(token.getStoreId())
                .child(COUNTERS_CHILD)
                .child("" + token.getCounter())
                .child(TOKENS_CHILD)
                .child(token.getuId())
                .setValue(token.getTokenNumber());
    }

//    private boolean createToken(Token token) {
//        if (myApiService == null) {  // Only do this once
//            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
//                    new AndroidJsonFactory(), null)
//                    // options for running against local devappserver
//                    // - 10.0.2.2 is localhost's IP address in Android emulator
//                    // - turn off compression when running against local devappserver
//                    .setRootUrl("http://192.168.1.4:8080/_ah/api/")
////                    .setRootUrl("https://tagtree-dev.appspot.com/_ah/api/")
//                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
//                        @Override
//                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
//                            abstractGoogleClientRequest.setDisableGZipContent(true);
//                        }
//                    });
//            // end options for devappserver
//            myApiService = builder.build();
//        }
//
//        try {
//            //Copy the app token model into backend model
//            in.mobifirst.tagtree.backend.myApi.model.Token token1 = new in.mobifirst.tagtree.backend.myApi.model.Token();
//            token1.setSenderName(token.getSenderName());
//            token1.setSenderPic(token.getSenderPic());
//            token1.setStoreId(token.getStoreId());
//            token1.setPhoneNumber(token.getPhoneNumber());
//            token1.setCounter(token.getCounter());
//
//            return myApiService.addNewToken(token1).execute().getStatus();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

    public void addNewToken(final Token token, final Subscriber<? super String> subscriber) {
//        token.setSenderPic(mSharedPrefs.getSting((ApplicationConstants.WEBSITE_LOGO_URL_KEY)));
//        token.setSenderName(mSharedPrefs.getSting(ApplicationConstants.DISPLAY_NAME_KEY));
//
//
//        new AsyncTask<Token, Void, Boolean>() {
//            @Override
//            protected Boolean doInBackground(Token... params) {
//                return createToken(token);
//            }
//
//            @Override
//            protected void onPostExecute(Boolean aBoolean) {
//                if (aBoolean) {
//                    subscriber.onCompleted();
//                } else {
//                    subscriber.onError(new Throwable("Failed to create token!"));
//                }
//            }
//        }.execute(token);


//        Observable.create(new Observable.OnSubscribe<Boolean>() {
//            @Override
//            public void call(Subscriber<? super Boolean> subscriber) {
//                createToken(token);
//            }
//        })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<Boolean>() {
//                    @Override
//                    public void onCompleted() {
//                        subscriber.onCompleted();
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onNext(Boolean aBoolean) {
//
//                    }
//                });

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
                                                mSharedPrefs.getSting((ApplicationConstants.WEBSITE_LOGO_URL_KEY)),
                                                mSharedPrefs.getSting(ApplicationConstants.DISPLAY_NAME_KEY),
                                                token.getCounter(),
                                                areaName);

                                        mDatabaseReference.child(TOKENS_CHILD).child(key).setValue(newToken.toMap());
                                        addTokenUnderStoreCounter(newToken);

                                        //ToDo replicate the entire tokens under store. Too much but easy.
//                                        Map<String, Object> childUpdates = new HashMap<>();
//                                        childUpdates.put(TOKENS_CHILD + key, tokenValues);
//                                        childUpdates.put(STORE_CHILD + newToken.getStoreId() + "/" + newToken.getCounter() + "/" + TOKENS_CHILD + key, tokenValues);
//
//                                        mDatabaseReference.updateChildren(childUpdates);
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

    public void addStore(final String uid, final Store store, final Subscriber<? super String> subscriber) {
        mDatabaseReference
                .child("store")
                .child(uid)
                .child("name").setValue(store.getName())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mDatabaseReference
                                .child("store")
                                .child(uid)
                                .child("area").setValue(store.getArea())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mDatabaseReference
                                                .child("store")
                                                .child(uid)
                                                .child("website").setValue(store.getWebsite())
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        mDatabaseReference
                                                                .child("store")
                                                                .child(uid)
                                                                .child("logoUrl").setValue(store.getLogoUrl())
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        mDatabaseReference
                                                                                .child("store")
                                                                                .child(uid)
                                                                                .child("numberOfCounters").setValue(store.getNumberOfCounters())
                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void aVoid) {
                                                                                        subscriber.onNext(null);
                                                                                        subscriber.onCompleted();
                                                                                    }
                                                                                });
                                                                    }
                                                                });
                                                    }
                                                });
                                    }
                                });
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
                    if (!BuildConfig.DEBUG) {
                        sendSMS(token, status);
                    }
//                    sendBulkSMS(token, status);
                } else {
                    //User present. Update token table for the counter view.
                    HashMap<String, User> userMap = usersnapshot.getValue(new GenericTypeIndicator<HashMap<String, User>>() {
                    });
                    if (userMap != null && userMap.size() > 0) {
                        User user = new ArrayList<>(userMap.values()).get(0);
                        if (user != null && !TextUtils.isEmpty(user.getName())) {
                            mDatabaseReference.child(TOKENS_CHILD).child(token.getuId()).child("userName").setValue(user.getName());
                        }
                    }
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
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mIQStoreApplication);
        String languagePrefValueString = sharedPref.getString(ApplicationConstants.LANGUAGE_PREFERENCE_KEY, "-1");
        int languagePrefValue = Integer.parseInt(languagePrefValueString);

        if (status == false) {
            switch (languagePrefValue) {
                case 0: //English
                default:
                    message = "You've received a token from " + token.getSenderName().trim() + " "
                            + token.getAreaName().trim() + ". Token= " + (token.getTokenNumber()) + ", Counter= " + token.getCounter()
                            + ". Download the app " + CLIENT_APP_PLAYSTORE_URL + " for real time updates";
                    break;
                case 1: //Telugu
                    message = "మీకు" + " " + token.getSenderName().trim() + " మరియు శాఖ" + " "
                            + token.getAreaName().trim() + " నుండి టోకెన్ అందింది" + "." + "టోకెన్ సంఖ్య" + "=" + (token.getTokenNumber())
                            + "," + "కౌంటర సంఖ్య" + "=" + token.getCounter()
                            + "." +
                            "నిజ సమయంలో టోకెన్ స్థితిని ట్రాక్ చేయడానికి PlayStore నుండి అప్లికేషన్ డౌన్లోడ్ చేయుము"
                            + "."
                            + CLIENT_APP_PLAYSTORE_URL
                            + ".";
                    break;
            }
        } else {

            switch (languagePrefValue) {
                case 0: //English
                default:
                    message = "It's your turn at " + token.getSenderName() + " " + token.getAreaName() + "."
                            + " Token = " + token.getTokenNumber() + ", Counter = " + token.getCounter()
                            + ". Download the app " + CLIENT_APP_PLAYSTORE_URL + " for real time updates.";
                    break;
                case 1: //Telugu
                    message = token.getSenderName().trim() + " మరియు శాఖ" + " "
                            + token.getAreaName().trim() + "వద్ద మీ వంతు" + "." + "టోకెన్ సంఖ్య" + "=" + (token.getTokenNumber())
                            + "," + "కౌంటర సంఖ్య" + "=" + token.getCounter()
                            + "." +
                            "నిజ సమయంలో టోకెన్ స్థితిని ట్రాక్ చేయడానికి PlayStore నుండి అప్లికేషన్ డౌన్లోడ్ చేయుము"
                            + "."
                            + CLIENT_APP_PLAYSTORE_URL
                            + ".";
                    break;
            }
        }
        //define route
        String route = "4"; //4 For transaction, check with msg91

        URLConnection myURLConnection = null;
        URL myURL = null;
        BufferedReader reader = null;

        //encoding message
        String encoded_message = null;
        try {
            encoded_message = URLEncoder.encode(message, "UTF-8");

            //Send SMS API
            String mainUrl = mMsg91Url;

            //Prepare parameter string
            StringBuilder sbPostData = new StringBuilder(mainUrl);
            sbPostData.append("authkey=" + authkey);
            sbPostData.append("&mobiles=" + mobiles);
            sbPostData.append("&message=" + encoded_message);
            sbPostData.append("&route=" + route);
            sbPostData.append("&sender=" + senderId);
            sbPostData.append("&unicode=1");

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
            new SendSMSTask().execute(mainUrl);
            incrementSMS(token, new IncremnetTransactionHander()
            );

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
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
            //Update the token completion time
            mDatabaseReference
                    .child(TOKENS_CHILD)
                    .child(token.getuId())
                    .setValue(token);
            mDatabaseReference
                    .child(TOKENS_CHILD)
                    .child(token.getuId())
                    .child("tokenFinishTime")
                    .setValue(ServerValue.TIMESTAMP);

            ValueEventListener completionListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Token tokenUpdated = dataSnapshot.getValue(Token.class);
                        incrementAvgBurstTime(tokenUpdated);

                        //Move it to token-history table.
                        mDatabaseReference
                                .child(TOKENS_HISTORY_CHILD)
                                .push()
                                .setValue(tokenUpdated.toMap());

                        //Remove it from the token table
                        mDatabaseReference
                                .child(TOKENS_CHILD)
                                .child(tokenUpdated.getuId())
                                .removeValue();

                        //Remove the activated token from the store counter so that the TAT is calculated on the issued tokens only.
                        mDatabaseReference
                                .child(STORE_CHILD)
                                .child(tokenUpdated.getStoreId())
                                .child(COUNTERS_CHILD)
                                .child("" + tokenUpdated.getCounter())
                                .child(TOKENS_CHILD)
                                .child(tokenUpdated.getuId())
                                .removeValue();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "Token completion time is not updated", databaseError.toException());
                }
            };
            mDatabaseReference
                    .child(TOKENS_CHILD)
                    .child(token.getuId()).addListenerForSingleValueEvent(completionListener);

        } else {
            mDatabaseReference
                    .child(TOKENS_CHILD)
                    .child(token.getuId())
                    .setValue(token);
            mDatabaseReference
                    .child(TOKENS_CHILD)
                    .child(token.getuId())
                    .child("activatedTokenTime")
                    .setValue(ServerValue.TIMESTAMP);

            ValueEventListener postListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Token tokenUpdated = dataSnapshot.getValue(Token.class);
                        if (tokenUpdated.getBuzzCount() == 1) {
                            //Inform user if he is not present in the user table
                            checkSMSSending(tokenUpdated, true);
                            incrementUserCount(tokenUpdated, new IncremnetTransactionHander());
                            incrementAvgTAT(tokenUpdated);
                            setActiveTokenNumber(tokenUpdated);


                            //Remove the activated token from the store counter so that the TAT is calculated on the issued tokens only.
                            mDatabaseReference
                                    .child(STORE_CHILD)
                                    .child(tokenUpdated.getStoreId())
                                    .child(COUNTERS_CHILD)
                                    .child("" + tokenUpdated.getCounter())
                                    .child(TOKENS_CHILD)
                                    .child(tokenUpdated.getuId())
                                    .removeValue();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w(TAG, "SMS won't be sent", databaseError.toException());
                    // ...
                }
            };
            mDatabaseReference
                    .child(TOKENS_CHILD)
                    .child(token.getuId()).addListenerForSingleValueEvent(postListener);
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

    private void setActiveTokenNumber(Token token) {
        mDatabaseReference
                .child(STORE_CHILD)
                .child(token.getStoreId())
                .child(COUNTERS_CHILD)
                .child("" + token.getCounter())
                .child(COUNTERS_LAST_ACTIVE_TOKEN)
                .setValue(token.getTokenNumber());
    }

    private void incrementUserCount(Token token, @NonNull Transaction.Handler handler) {
        mDatabaseReference
                .child(STORE_CHILD)
                .child(token.getStoreId())
                .child(COUNTERS_CHILD)
                .child("" + token.getCounter())
                .child(COUNTERS_USERS)
                .runTransaction(handler);

    }

    private void incrementAvgTAT(Token token) {
        long waitTimePerToken = token.getActivatedTokenTime() - token.getTimestamp();
        if (waitTimePerToken < 0) {
            waitTimePerToken = 0;
        }

        final long finalWaitTimePerToken = waitTimePerToken;
        mDatabaseReference
                .child(STORE_CHILD)
                .child(token.getStoreId())
                .child(COUNTERS_CHILD)
                .child("" + token.getCounter())
                .child(COUNTERS_AVG_TAT_CHILD)
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        Long currentValue = mutableData.getValue(Long.class);
                        if (currentValue == null) {
                            mutableData.setValue(finalWaitTimePerToken);
                        } else {
                            mutableData.setValue(currentValue + finalWaitTimePerToken);
                        }
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                    }
                });

    }

    private void incrementAvgBurstTime(Token token) {
        long burstTime = 0;
        if (token.getActivatedTokenTime() > 0) {
            burstTime = token.getTokenFinishTime() - token.getActivatedTokenTime();
        }
        if (burstTime < 0) {
            burstTime = 0;
        }

        final long finalBurstTime = burstTime;
        mDatabaseReference
                .child(STORE_CHILD)
                .child(token.getStoreId())
                .child(COUNTERS_CHILD)
                .child("" + token.getCounter())
                .child(COUNTERS_AVG_BURST_CHILD)
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        Long currentValue = mutableData.getValue(Long.class);
                        if (currentValue == null) {
                            mutableData.setValue(finalBurstTime);
                        } else {
                            mutableData.setValue(currentValue + finalBurstTime);
                        }
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                    }
                });

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

    private void sendBulkSMS(Token token, boolean status) {


        //Android SMS API integration code

        //Your authentication key
        //Multiple mobiles numbers separated by comma
        String mobiles = token.getPhoneNumber();
        //Sender ID,While using route4 sender id should be 6 characters long.
        String senderId = "TagTre";
        String message = "";
        //Your message to send, Add URL encoding here.
        if (status == false) {
            message = "You've received a token from " + token.getSenderName().trim() + " "
                    + token.getAreaName().trim() + ". Token= " + (token.getTokenNumber()) + ", Counter= " + token.getCounter()
                    + ". Download the app " + CLIENT_APP_PLAYSTORE_URL + " for real time updates";
        } else {
            message = "It's your turn at " + token.getSenderName() + " " + token.getAreaName() + "."
                    + " Token = " + token.getTokenNumber() + ", Counter = " + token.getCounter()
                    + ". Download the app " + CLIENT_APP_PLAYSTORE_URL + " for real time updates.";
        }
        //define route
        String route = "4"; //4 For transaction, check with msg91

        URLConnection myURLConnection = null;
        URL myURL = null;
        BufferedReader reader = null;

        //encoding message
        String encoded_message = URLEncoder.encode(message);

        //Send SMS API
        String mainUrl = mMsgBulkSMSUrl;

        //Prepare parameter string
        StringBuilder sbPostData = new StringBuilder(mainUrl);
        sbPostData.append("user=" + "tagtree");
        sbPostData.append("&password=" + "Tagtree@123");
        sbPostData.append("&message=" + encoded_message);
        sbPostData.append("&sender=" + "TagTre");
        sbPostData.append("&type=" + "3");
        sbPostData.append("&mobile=" + token.getPhoneNumber());


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
        new SendSMSTask().execute(mainUrl);
        incrementSMS(token, new IncremnetTransactionHander());
    }

    //Reset token counter
    public void resetTokenCounter(String storeId) {
        DatabaseReference tokenCounterRef = mDatabaseReference
                .child("store")
                .child(storeId)
                .child("tokenCounter");
        tokenCounterRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                mutableData.setValue(0);

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }
}

