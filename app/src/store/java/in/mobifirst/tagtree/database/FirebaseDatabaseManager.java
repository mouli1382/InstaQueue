package in.mobifirst.tagtree.database;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
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
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
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
import in.mobifirst.tagtree.model.StoreCounter;
import in.mobifirst.tagtree.model.Token;
import in.mobifirst.tagtree.preferences.IQSharedPreferences;
import in.mobifirst.tagtree.tokens.Snap;
import in.mobifirst.tagtree.util.ApplicationConstants;
import in.mobifirst.tagtree.util.TimeUtils;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

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

    private final static String CLIENT_APP_PLAYSTORE_URL = "https://goo.gl/4nR8wl";

    private DatabaseReference mDatabaseReference;
    private IQSharedPreferences mSharedPrefs;
    private IQStoreApplication mIQStoreApplication;

    @Inject
    public FirebaseDatabaseManager(IQStoreApplication application, IQSharedPreferences iqSharedPreferences) {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mIQStoreApplication = application;
        mSharedPrefs = iqSharedPreferences;
    }

    public DatabaseReference getDatabaseReference() {
        return mDatabaseReference;
    }

    //ToDo limit by date and status.
    public Observable<List<Snap>> getAllSnaps(final String uId, final long date, final boolean ascending) {
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
                                            .filter(new Func1<Token, Boolean>() {
                                                @Override
                                                public Boolean call(Token token) {
                                                    return TimeUtils.getDate(token.getDate())
                                                            .equalsIgnoreCase(TimeUtils.getDate(date));
                                                }
                                            })
                                            .toSortedList(new Func2<Token, Token, Integer>() {
                                                @Override
                                                public Integer call(Token token, Token token2) {
                                                    if (ascending) {
                                                        return new Long(token.getTokenNumber()).compareTo(token2.getTokenNumber());
                                                    } else {
                                                        return new Long(token2.getTokenNumber()).compareTo(token.getTokenNumber());
                                                    }
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
                                            .filter(new Func1<Token, Boolean>() {
                                                @Override
                                                public Boolean call(Token token) {
                                                    return TimeUtils.getDate(token.getDate())
                                                            .equalsIgnoreCase(TimeUtils.getDate(Calendar.getInstance().getTimeInMillis()));
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

    private Task<Void> addTokenUnderStoreCounter(Token token) {
        //Save the token Id under the token number which makes client job easy in sorting and calculating TAT.
        return mDatabaseReference
                .child(STORE_CHILD)
                .child(token.getStoreId())
                .child(COUNTERS_CHILD)
                .child("" + token.getCounter())
                .child(TimeUtils.getDate(token.getDate()))
                .child(TOKENS_CHILD)
                .child(token.getuId())
                .setValue(token.getTokenNumber());
    }

    private Task<Integer> positionInQueue(String storeId, int counter) {
        Log.e(TAG, "positionInQueue START.... ");
        final TaskCompletionSource<StoreCounter> completionSource = new TaskCompletionSource<>();

        mDatabaseReference
                .child(STORE_CHILD)
                .child(storeId)
                .child(COUNTERS_CHILD)
                .child("" + counter)
                .child(TimeUtils.getDate(Calendar.getInstance().getTimeInMillis()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            StoreCounter storeCounter = dataSnapshot.getValue(StoreCounter.class);
                            completionSource.setResult(storeCounter);
                        } else {
                            completionSource.setResult(null);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "[fetch Store] onCancelled:" + databaseError);
                        completionSource.setException(databaseError.toException());
                    }
                });

        return completionSource.getTask()
                .continueWith(new Continuation<StoreCounter, Integer>() {
                    @Override
                    public Integer then(@NonNull Task<StoreCounter> task) throws Exception {
                        StoreCounter storeCounter = task.getResult();
                        if (storeCounter == null) {
                            return -1;
                        } else {
                            return storeCounter.positionOfNewToken();
                        }
                    }
                });
    }


    public void addNewToken(final Token token, final Subscriber<? super String> subscriber) {
        decrementCredits(token)
                .continueWithTask(new Continuation<Boolean, Task<Long>>() {
                    @Override
                    public Task<Long> then(@NonNull Task<Boolean> task) throws Exception {
                        Log.e(TAG, "decrementCredits DONE ------");
                        return incrementTokenCounter(token);
                    }
                })
                .continueWithTask(new Continuation<Long, Task<Long>>() {
                    @Override
                    public Task<Long> then(@NonNull Task<Long> task) throws Exception {
                        final Long currentToken = task.getResult();
//                        if (currentToken > 2) {
//                            return positionInQueue(token.getStoreId(), token.getCounter())
//                                    .continueWithTask(new Continuation<Integer, Task<Long>>() {
//                                        @Override
//                                        public Task<Long> then(@NonNull Task<Integer> task) throws Exception {
//                                            return addNewToken(token, currentToken, task.getResult());
//                                        }
//                                    });
//
//                        } else {
                        return addNewToken(token, currentToken, -1);
//                        }
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Long>() {
                    @Override
                    public void onSuccess(Long aLong) {
                        subscriber.onNext(aLong + "");
                        subscriber.onCompleted();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        subscriber.onError(e);
                    }
                });
    }

    public Task<Long> addNewToken(final Token token, final long currentToken, final int position) {
        Log.e(TAG, "addNewToken START ------");
        final TaskCompletionSource<Long> addTokenSource = new TaskCompletionSource<>();

        DatabaseReference storeRef = mDatabaseReference.getRef()
                .child("store")
                .child(token.getStoreId());

        storeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String key = mDatabaseReference.child(TOKENS_CHILD)
                            .push().getKey();
                    Store store = dataSnapshot.getValue(Store.class);
                    String areaName = store.getArea();
                    final Token newToken = new Token(key, token.getStoreId(),
                            token.getPhoneNumber(),
                            currentToken,
                            mSharedPrefs.getSting((ApplicationConstants.WEBSITE_LOGO_URL_KEY)),
                            mSharedPrefs.getSting(ApplicationConstants.DISPLAY_NAME_KEY),
                            token.getCounter(),
                            areaName,
                            token.getMappingId(), token.getDate());

                    mDatabaseReference
                            .child(TOKENS_CHILD)
                            .child(key)
                            .setValue(newToken.toMap())
                            .continueWithTask(new Continuation<Void, Task<Void>>() {
                                @Override
                                public Task<Void> then(@NonNull Task<Void> task) throws Exception {
                                    return addTokenUnderStoreCounter(newToken);
                                }
                            })
                            .continueWithTask(new Continuation<Void, Task<Void>>() {
                                @Override
                                public Task<Void> then(@NonNull Task<Void> task) throws Exception {
                                    return sendSMS(newToken, false, position);
                                }
                            })
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    addTokenSource.setResult(currentToken);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    addTokenSource.setException(e);
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Token creation failed" + databaseError.toException());
                addTokenSource.setException(databaseError.toException());
            }
        });

        return addTokenSource.getTask();
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
                .child("storeId").setValue(uid)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
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

//
//    private void checkSMSSending(final Token token, final boolean status, final Integer position) {
//
//        Query query = mDatabaseReference.getRef()
//                .child("users")
//                .orderByChild("phoneNumber")
//                .equalTo(token.getPhoneNumber());
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot usersnapshot) {
//                if (usersnapshot == null || !usersnapshot.exists()) {
//                    //user is not present
////                    if (!BuildConfig.DEBUG) {
//                    sendSMS(token, status, position);
////                    }
////                    sendBulkSMS(token, status);
//                } else {
//                    //User present. Update token table for the counter view.
//                    HashMap<String, User> userMap = usersnapshot.getValue(new GenericTypeIndicator<HashMap<String, User>>() {
//                    });
//                    if (userMap != null && userMap.size() > 0) {
//                        User user = new ArrayList<>(userMap.values()).get(0);
//                        if (user != null && !TextUtils.isEmpty(user.getName())) {
//                            mDatabaseReference.child(TOKENS_CHILD).child(token.getuId()).child("userName").setValue(user.getName());
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError userdatabaseerror) {
//                Log.e(TAG, "[fetch User] onCancelled:" + userdatabaseerror);
//
//            }
//
//        });
//    }

    private Task<Void> sendSMS(final Token token, final boolean status, final Integer position) {
        //Android SMS API integration code
        final TaskCompletionSource<Boolean> sendSmsSource = new TaskCompletionSource<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
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

                String positionTelugu = "మీ వంతు " + position + " మంది తరువాత ఉంది" + ".";
                String positionEnglish = ". There are " + position + " members before you";

                //Calculate the appointment time from start-time and send it in SMS.
                String appointmentEnglish = "";
                String appointmentTelugu = "";
                if (!TimeUtils.getDate(Calendar.getInstance().getTimeInMillis()).equalsIgnoreCase(TimeUtils.getDate(token.getDate()))) {
                    long startTime = 28800; //8AM
                    long step = 10; // 10 min
                    long estimatedTime = /*startTime +*/ step * ((int) token.getTokenNumber() - 1);
                    String appointmentTime = TimeUtils.getDurationInHrsAndMin((int) estimatedTime);
                    Log.e(TAG, "Your appointment is scheduled at " + appointmentTime + " on " + TimeUtils.getDate(token.getDate()));
                    appointmentEnglish = "Your appointment is scheduled at " + appointmentTime + " on " + TimeUtils.getDate(token.getDate());
                    appointmentTelugu = "మీ అపాయింట్మెంట్ సమయం " + appointmentTime + " తేదీ " + TimeUtils.getDate(token.getDate()) + ",";
                }
                appointmentEnglish = TextUtils.isEmpty(appointmentEnglish) ? "Your " : appointmentEnglish + " with ";
                appointmentTelugu = TextUtils.isEmpty(appointmentTelugu) ? "మీ " : appointmentTelugu;

                if (status == false) {
                    switch (languagePrefValue) {
                        case 0: //English
                        default:
                            message = appointmentEnglish + "token=" + (token.getTokenNumber()) + " from " + token.getSenderName().trim() + ","
                                    + token.getAreaName().trim() + ","
                                    + "Counter #" + token.getCounter()
                                    + (position != -1 ? positionEnglish : "") + ". To avoid standing in Q, download TagTree app or click on the link." + CLIENT_APP_PLAYSTORE_URL + " and save your time and energy";
                            break;
                        case 1: //Telugu
                            message = appointmentTelugu + "టోకెన్=" + (token.getTokenNumber()) + "," + token.getSenderName().trim() + ","
                                    + token.getAreaName().trim() + ","
                                    + "కౌంటర్ #" + token.getCounter() + "."
                                    + (position != -1 ? positionTelugu : "") +
                                    "Q లో  నిలబడటం నివారించేందుకు, ఇప్పుడే  క్రింద లింక్ క్లిక్ చేయండి."
                                    + CLIENT_APP_PLAYSTORE_URL
                                    + "." + " TagTree app ద్వారా మీరు మీ సమయం, డబ్బు ఆదా చేయవచ్చు";
                            break;
                    }
                } else {
                    switch (languagePrefValue) {
                        case 0: //English
                        default:
                            message = "Now it's turn for Token=" + token.getTokenNumber() + "," + token.getSenderName() + " " + token.getAreaName().trim() + "."
                                    + "To avoid standing in Q, download TagTree app or click on the link." + CLIENT_APP_PLAYSTORE_URL + " and save your time and energy";
                            break;
                        case 1: //Telugu
                            message = "ఇప్పుడు మీ వంతు వచ్చింది. టోకెన్=" + (token.getTokenNumber()) + "," + token.getSenderName().trim() + " "
                                    + token.getAreaName().trim() +
                                    ". Q లో  నిలబడటం నివారించేందుకు, ఇప్పుడే  క్రింద లింక్ క్లిక్ చేయండి."
                                    + CLIENT_APP_PLAYSTORE_URL
                                    + "." + " TagTree app ద్వారా మీరు మీ సమయం, డబ్బు ఆదా చేయవచ్చు";
                            break;
                    }
                }
                //define route
                String route = "4"; //4 For transaction, check with msg91
                //encoding message
                String encoded_message = null;
                BufferedReader reader = null;
                try {
                    encoded_message = URLEncoder.encode(message, "UTF-8");

                    //Send SMS API
                    String mainUrl = mMsg91Url;

                    //Prepare parameter string
                    final StringBuilder sbPostData = new StringBuilder(mainUrl);
                    sbPostData.append("authkey=" + authkey);
                    sbPostData.append("&mobiles=" + mobiles);
                    sbPostData.append("&message=" + encoded_message);
                    sbPostData.append("&route=" + route);
                    sbPostData.append("&sender=" + senderId);
                    sbPostData.append("&unicode=1");

                    //prepare connection
                    URL myURL = new URL(sbPostData.toString());
                    URLConnection myURLConnection = myURL.openConnection();
                    myURLConnection.connect();
                    reader = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));

                    //reading response
                    while (reader.readLine() != null) ;

                    sendSmsSource.setResult(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    sendSmsSource.setException(e);
                } finally {
                    //finally close connection
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        return Tasks.whenAll(sendSmsSource.getTask(), incrementSMS(token));

//        //Your authentication key
//        String authkey = BuildConfig.SMS91_SECRET;
//        //Multiple mobiles numbers separated by comma
//        String mobiles = token.getPhoneNumber();
//        //Sender ID,While using route4 sender id should be 6 characters long.
//        String senderId = "TagTre";
//        String message = "";
//
//        //Your message to send, Add URL encoding here.
//        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mIQStoreApplication);
//        String languagePrefValueString = sharedPref.getString(ApplicationConstants.LANGUAGE_PREFERENCE_KEY, "-1");
//        int languagePrefValue = Integer.parseInt(languagePrefValueString);
//
//        String positionTelugu = "మీ వంతు " + position + " మంది తరువాత ఉంది" + ".";
//        String positionEnglish = ". There are " + position + " members before you";
//
//        if (status == false) {
//            switch (languagePrefValue) {
//                case 0: //English
//                default:
//                    message = "Your token=" + (token.getTokenNumber()) + " from " + token.getSenderName().trim() + " "
//                            + token.getAreaName().trim() + (position != -1 ? positionEnglish : "") + ". To avoid standing in Q, download TagTree app or click on the link." + CLIENT_APP_PLAYSTORE_URL + " and save your time and energy";
//                    break;
//                case 1: //Telugu
//                    message = "మీ టోకెన్=" + (token.getTokenNumber()) + "," + token.getSenderName().trim() + " "
//                            + token.getAreaName().trim() + "." + (position != -1 ? positionTelugu : "") +
//                            "Q లో  నిలబడటం నివారించేందుకు, ఇప్పుడే  క్రింద లింక్ క్లిక్ చేయండి."
//                            + CLIENT_APP_PLAYSTORE_URL
//                            + "." + " TagTree app ద్వారా మీరు మీ సమయం, డబ్బు ఆదా చేయవచ్చు";
//                    break;
//            }
//        } else {
//
//            switch (languagePrefValue) {
//                case 0: //English
//                default:
//                    message = "Now it's turn for Token=" + token.getTokenNumber() + "," + token.getSenderName() + " " + token.getAreaName().trim() + "."
//                            + "To avoid standing in Q, download TagTree app or click on the link." + CLIENT_APP_PLAYSTORE_URL + " and save your time and energy";
//                    break;
//                case 1: //Telugu
//                    message = "ఇప్పుడు మీ వంతు వచ్చింది. టోకెన్=" + (token.getTokenNumber()) + "," + token.getSenderName().trim() + " "
//                            + token.getAreaName().trim() +
//                            ". Q లో  నిలబడటం నివారించేందుకు, ఇప్పుడే  క్రింద లింక్ క్లిక్ చేయండి."
//                            + CLIENT_APP_PLAYSTORE_URL
//                            + "." + " TagTree app ద్వారా మీరు మీ సమయం, డబ్బు ఆదా చేయవచ్చు";
//                    break;
//            }
//        }
//        //define route
//        String route = "4"; //4 For transaction, check with msg91
//
//        URLConnection myURLConnection = null;
//        URL myURL = null;
//        BufferedReader reader = null;
//
//        //encoding message
//        String encoded_message = null;
//        try {
//            encoded_message = URLEncoder.encode(message, "UTF-8");
//
//            //Send SMS API
//            String mainUrl = mMsg91Url;
//
//            //Prepare parameter string
//            StringBuilder sbPostData = new StringBuilder(mainUrl);
//            sbPostData.append("authkey=" + authkey);
//            sbPostData.append("&mobiles=" + mobiles);
//            sbPostData.append("&message=" + encoded_message);
//            sbPostData.append("&route=" + route);
//            sbPostData.append("&sender=" + senderId);
//            sbPostData.append("&unicode=1");
//
//            //final string
//            mainUrl = sbPostData.toString();
//            class SendSMSTask extends AsyncTask<String, Integer, Long> {
//                protected Long doInBackground(String... urls) {
//                    try {
//                        //prepare connection
//                        URL myURL = new URL(urls[0]);
//                        URLConnection myURLConnection = myURL.openConnection();
//                        myURLConnection.connect();
//                        BufferedReader reader = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));
//
//                        //reading response
//                        String response;
//                        while ((response = reader.readLine()) != null)
//                            //print response
//                            Log.d("RESPONSE", "" + response);
//
//                        //finally close connection
//                        reader.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        FirebaseCrash.report(new Exception("Error in Sending SMS: " + e.getMessage()));
//                    }
//                    return 0L;
//                }
//            }
//
//            //Uncomment this  to execute the send sms
//            new SendSMSTask().execute(mainUrl);
//            incrementSMS(token, new IncremnetTransactionHander()
//            );
//
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
    }

    private Task<Long> incrementTokenCounter(Token token) {
        final TaskCompletionSource<Long> incrementTokenTask = new TaskCompletionSource<>();
        Log.e(TAG, "incrementTokenCounter START ------");
        DatabaseReference tokenCounterRef = mDatabaseReference
                .child("/")
                .child(STORE_CHILD)
                .child(token.getStoreId())
                .child(COUNTERS_CHILD)
                .child("" + token.getCounter())
                .child(TimeUtils.getDate(token.getDate()))
                .child("tokenCounter");
        tokenCounterRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Long currentValue = mutableData.getValue(Long.class);
                if (currentValue == null) {
                    mutableData.setValue(1);
                } else {
                    mutableData.setValue(currentValue + 1);
                }

                if (mutableData.equals(currentValue)) {
                    return Transaction.abort();
                } else {
                    Log.e(TAG, "incrementTokenCounter SUCCESS ------");
                    return Transaction.success(mutableData);
                }
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                if (databaseError == null && b) {
                    Log.e(TAG, "incrementTokenCounter END ------");
                    incrementTokenTask.setResult(dataSnapshot.getValue(Long.class));
                } /*else {
                    incrementTokenTask.setException(databaseError.toException());
                }*/
            }
        });

        return incrementTokenTask.getTask();
    }

//    private void incrementTokenCounter(Token token, @NonNull Transaction.Handler handler) {
//
//        DatabaseReference tokenCounterRef = mDatabaseReference
//                .child("/")
//                .child(STORE_CHILD)
//                .child(token.getStoreId())
//                .child(COUNTERS_CHILD)
//                .child("" + token.getCounter())
//                .child(TimeUtils.getDate(token.getDate()))
//                .child("tokenCounter");
//        tokenCounterRef.runTransaction(handler);
//
//
//        decrementCredits(token, new Transaction.Handler() {
//            @Override
//            public Transaction.Result doTransaction(MutableData mutableData) {
//                Long currentValue = mutableData.getValue(Long.class);
//                if (currentValue == null) {
//                    mutableData.setValue(1000);
//                } else {
//                    mutableData.setValue(currentValue - 1);
//                }
//
//
//                return Transaction.success(mutableData);
//            }
//
//            @Override
//            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
//
//            }
//        });
//
//    }
//
//    private void sendBulkSMS(Token token, boolean status) {
//
//
//        //Android SMS API integration code
//
//        //Your authentication key
//        //Multiple mobiles numbers separated by comma
//        String mobiles = token.getPhoneNumber();
//        //Sender ID,While using route4 sender id should be 6 characters long.
//        String senderId = "TagTre";
//        String message = "";
//        //Your message to send, Add URL encoding here.
//        if (status == false) {
//            message = "You've received a token from " + token.getSenderName().trim() + " "
//                    + token.getAreaName().trim() + ". Token= " + (token.getTokenNumber()) + ", Counter= " + token.getCounter()
//                    + ". Download the app " + CLIENT_APP_PLAYSTORE_URL + " for real time updates";
//        } else {
//            message = "It's your turn at " + token.getSenderName() + " " + token.getAreaName() + "."
//                    + " Token = " + token.getTokenNumber() + ", Counter = " + token.getCounter()
//                    + ". Download the app " + CLIENT_APP_PLAYSTORE_URL + " for real time updates.";
//        }
//        //define route
//        String route = "4"; //4 For transaction, check with msg91
//
//        URLConnection myURLConnection = null;
//        URL myURL = null;
//        BufferedReader reader = null;
//
//        //encoding message
//        String encoded_message = URLEncoder.encode(message);
//
//        //Send SMS API
//        String mainUrl = mMsgBulkSMSUrl;
//
//        //Prepare parameter string
//        StringBuilder sbPostData = new StringBuilder(mainUrl);
//        sbPostData.append("user=" + "tagtree");
//        sbPostData.append("&password=" + "Tagtree@123");
//        sbPostData.append("&message=" + encoded_message);
//        sbPostData.append("&sender=" + "TagTre");
//        sbPostData.append("&type=" + "3");
//        sbPostData.append("&mobile=" + token.getPhoneNumber());
//
//
//        //final string
//        mainUrl = sbPostData.toString();
//        class SendSMSTask extends AsyncTask<String, Integer, Long> {
//            protected Long doInBackground(String... urls) {
//                try {
//                    //prepare connection
//                    URL myURL = new URL(urls[0]);
//                    URLConnection myURLConnection = myURL.openConnection();
//                    myURLConnection.connect();
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));
//
//                    //reading response
//                    String response;
//                    while ((response = reader.readLine()) != null)
//                        //print response
//                        Log.d("RESPONSE", "" + response);
//
//                    //finally close connection
//                    reader.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    FirebaseCrash.report(new Exception("Error in Sending SMS: " + e.getMessage()));
//                }
//                return 0L;
//            }
//        }
//
//        //Uncomment this  to execute the send sms
//        new SendSMSTask().execute(mainUrl);
//        incrementSMS(token, new IncremnetTransactionHander());
//    }

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

    private Task<Void> moveToHistory(Token token) {
        return mDatabaseReference
                .child(TOKENS_HISTORY_CHILD)
                .push()
                .setValue(token.toMap());
    }

    private Task<Void> removeFromTokenTable(Token token) {
        //Remove it from the token table
        return mDatabaseReference
                .child(TOKENS_CHILD)
                .child(token.getuId())
                .removeValue();
    }

    private Task<Void> removeTokenFromStore(Token token) {
        //Remove the activated token from the store counter so that the TAT is calculated on the issued tokens only.
        return mDatabaseReference
                .child(STORE_CHILD)
                .child(token.getStoreId())
                .child(COUNTERS_CHILD)
                .child("" + token.getCounter())
                .child(TimeUtils.getDate(token.getDate()))
                .child(TOKENS_CHILD)
                .child(token.getuId())
                .removeValue();
    }

    private Task<Token> getInactiveToken(String storeId) {
        Log.e(TAG, "getInactiveToken START.... ");

        final TaskCompletionSource<Token> completionSource = new TaskCompletionSource<>();

        //ToDo Configure flic button per counter then we can pass appropriate counter number when flic button is clicked.
        getAllTokens(storeId, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<List<Token>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!completionSource.getTask().isComplete()) {
                            completionSource.setException(new Exception(e));
                        }
                    }

                    @Override
                    public void onNext(List<Token> tokenList) {
                        Token token = null;
                        if (tokenList != null) {
                            for (int i = 0; i < tokenList.size(); ++i) {
                                token = tokenList.get(i);
                                if (token.isIssued()) {
                                    // Found the inactive token.
                                    break;
                                }
                            }
                            Log.e(TAG, "getInactiveToken found the inactive token to activate....");
                        }
                        if (!completionSource.getTask().isComplete()) {
                            completionSource.setResult(token != null && token.isIssued() ? token : null);
                        }
                    }
                });

        return completionSource.getTask();
    }

    public Task<Boolean> activate(String storeId) {
        Log.e(TAG, "activateOrComplete START.... ");

        // Complete the one we got kicked for in a separate async piece
        // ToDo replace tokenFromUid with tokenFromCardNumber
//        Task<Boolean> completeTask = tokenFromCardNumber(rationShopItem.getCardNumber())
//                .continueWithTask(new Continuation<Token, Task<Boolean>>() {
//                    @Override
//                    public Task<Boolean> then(@NonNull Task<Token> task) throws Exception {
//                        Log.e(TAG, "activateOrComplete complete token.... ");
//                        if (task.getResult() == null) {
//                            throw new Exception("No token to complete.");
//                        } else {
//                            return completeCurrentToken(task.getResult());
//                        }
//                    }
//                });

        // Activate the next inactive token in another async piece
        return getInactiveToken(storeId)
                .continueWithTask(new Continuation<Token, Task<Boolean>>() {
                    @Override
                    public Task<Boolean> then(@NonNull Task<Token> task) throws Exception {
                        Log.e(TAG, "activateOrComplete activate token.... ");
                        if (task.getResult() == null) {
                            throw new Exception("No token to activate");
                        } else {
                            return activateNextToken(task.getResult());
                        }
                    }
                });
    }

//    private Task<Boolean> completeCurrentToken(Token token) {
//        //Bingo - Mark it COMPLETED
//        token.setStatus(Token.Status.COMPLETED.ordinal());
//        return updateToken(token);
//    }

    private Task<Boolean> activateNextToken(Token token) {
        //Bingo - Mark it READY
        token.setStatus(Token.Status.READY.ordinal());
        token.setBuzzCount(token.getBuzzCount() + 1);
        return updateToken(token);
    }

    public Task<Boolean> updateToken(final Token token) {
        final TaskCompletionSource<Boolean> updateToken = new TaskCompletionSource<>();
        Log.e(TAG, "updateToken START.... ");
        if (token.isCompleted()) {
            final ValueEventListener completionListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        final Token tokenUpdated = dataSnapshot.getValue(Token.class);
                        incrementAvgBurstTime(tokenUpdated)
                                .continueWithTask(new Continuation<Boolean, Task<Void>>() {
                                    @Override
                                    public Task<Void> then(@NonNull Task<Boolean> task) throws Exception {
                                        if (task.getResult()) {

                                            Log.e(TAG, "updateToken avg burst time updated.... ");
                                            //Add it to token-history table.
                                            return moveToHistory(tokenUpdated);
                                        } else {
                                            Log.e(TAG, "updateToken avg burst time failed.... ");
                                            throw new Exception("Updating Avg burst time failed.");
                                        }
                                    }
                                })
                                .continueWithTask(new Continuation<Void, Task<Void>>() {
                                    @Override
                                    public Task<Void> then(@NonNull Task<Void> task) throws Exception {
                                        Log.e(TAG, "updateToken token added to history table.... ");
                                        return removeFromTokenTable(tokenUpdated);
                                    }
                                })
                                .continueWithTask(new Continuation<Void, Task<Void>>() {
                                    @Override
                                    public Task<Void> then(@NonNull Task<Void> task) throws Exception {
                                        Log.e(TAG, "updateToken token removed from token table.... ");
                                        return removeTokenFromStore(tokenUpdated);
                                    }
                                })
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.e(TAG, "updateToken token removed from Store-Counter.... ");
                                        updateToken.setResult(true);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, "updateToken failed.... ");
                                        updateToken.setException(e);
                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "Token update failed..." + databaseError.toException());
                    updateToken.setException(databaseError.toException());
                }
            };

            //Update the token completion time
            mDatabaseReference
                    .child(TOKENS_CHILD)
                    .child(token.getuId())
                    .setValue(token)
                    .continueWithTask(new Continuation<Void, Task<Void>>() {
                        @Override
                        public Task<Void> then(@NonNull Task<Void> task) throws Exception {
                            Log.e(TAG, "updateToken token status updated to COMPLETED.... ");
                            return mDatabaseReference
                                    .child(TOKENS_CHILD)
                                    .child(token.getuId())
                                    .child("tokenFinishTime")
                                    .setValue(ServerValue.TIMESTAMP);
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.e(TAG, "updateToken token finish time updated.... ");
                            mDatabaseReference
                                    .child(TOKENS_CHILD)
                                    .child(token.getuId()).addListenerForSingleValueEvent(completionListener);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "updateToken token finish time updation failed.... ");
                            updateToken.setException(e);
                        }
                    });
        } else {
            final ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        final Token tokenUpdated = dataSnapshot.getValue(Token.class);
                        if (tokenUpdated.getBuzzCount() == 1) {

                            Tasks.whenAll(sendSMS(tokenUpdated, true, -1)
                                    , incrementUserCount(tokenUpdated)
                                    , incrementAvgTAT(tokenUpdated)
                                    , setActiveTokenNumber(tokenUpdated))
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.e(TAG, "updateToken user count, avg TAT & token activation done.... ");
                                            updateToken.setResult(true);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "updateToken user count, avg TAT & token activation FAILED.... ");
                                            updateToken.setException(e);
                                        }
                                    });

                            //Remove the activated token from the store counter so that the TAT is calculated on the issued tokens only.
//                            mDatabaseReference
//                                    .child(STORE_CHILD)
//                                    .child(tokenUpdated.getStoreId())
//                                    .child(COUNTERS_CHILD)
//                                    .child("" + tokenUpdated.getCounter())
//                                    .child(TOKENS_CHILD)
//                                    .child(tokenUpdated.getuId())
//                                    .removeValue();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.e(TAG, "SMS won't be sent" + databaseError.toException());
                    updateToken.setException(databaseError.toException());
                }
            };

            mDatabaseReference
                    .child(TOKENS_CHILD)
                    .child(token.getuId())
                    .setValue(token)
                    .continueWithTask(new Continuation<Void, Task<Void>>() {
                        @Override
                        public Task<Void> then(@NonNull Task<Void> task) throws Exception {
                            Log.e(TAG, "updateToken status updated to READY.... ");
                            return mDatabaseReference
                                    .child(TOKENS_CHILD)
                                    .child(token.getuId())
                                    .child("activatedTokenTime")
                                    .setValue(ServerValue.TIMESTAMP);
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.e(TAG, "updateToken token activation time updated.... ");
                            mDatabaseReference
                                    .child(TOKENS_CHILD)
                                    .child(token.getuId()).addListenerForSingleValueEvent(valueEventListener);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "updateToken token activation time updation failed.... ");
                            updateToken.setException(e);
                        }
                    });
        }
        return updateToken.getTask();
    }

    private Task<Boolean> decrementCredits(Token token) {
        Log.e(TAG, "decrementCredits START ------");
        final TaskCompletionSource<Boolean> completionSource = new TaskCompletionSource<>();
        DatabaseReference creditsRef = mDatabaseReference
                .child("/")
                .child("store")
                .child(token.getStoreId())
                .child("credits");
        creditsRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Long currentValue = mutableData.getValue(Long.class);
                if (currentValue == null) {
                    mutableData.setValue(1000);
                } else {
                    mutableData.setValue(currentValue - 1);
                }
                if (mutableData.equals(currentValue)) {
                    return Transaction.abort();
                } else {
                    Log.e(TAG, "decrementCredits SUCCESS ------");
                    return Transaction.success(mutableData);
                }
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                if (databaseError == null && committed) {
                    Log.e(TAG, "decrementCredits END ------");
                    completionSource.setResult(true);
                } /*else {
                    completionSource.setException(databaseError.toException());
                }*/
            }
        });
        return completionSource.getTask();
    }

    private Task<Boolean> incrementSMS(Token token) {
        IncrementTransactionHander handler = new IncrementTransactionHander();
        DatabaseReference creditsRef = mDatabaseReference
                .child("store")
                .child(token.getStoreId())
                .child("smsCounter");
        creditsRef.runTransaction(handler);
        return handler.getTask();
    }

    private Task<Void> setActiveTokenNumber(Token token) {
        return mDatabaseReference
                .child(STORE_CHILD)
                .child(token.getStoreId())
                .child(COUNTERS_CHILD)
                .child("" + token.getCounter())
                .child(COUNTERS_LAST_ACTIVE_TOKEN)
                .setValue(token.getTokenNumber());
    }

    private class IncrementTransactionHander implements Transaction.Handler {
        private TaskCompletionSource<Boolean> taskCompletionSource;

        IncrementTransactionHander() {
            taskCompletionSource = new TaskCompletionSource<>();
        }

        public Task<Boolean> getTask() {
            return taskCompletionSource.getTask();
        }

        @Override
        public Transaction.Result doTransaction(MutableData mutableData) {
            Long currentValue = mutableData.getValue(Long.class);
            if (currentValue == null) {
                mutableData.setValue(1);
            } else {
                mutableData.setValue(currentValue + 1);
            }
            if (mutableData.equals(currentValue)) {
                return Transaction.abort();
            } else {
                return Transaction.success(mutableData);
            }
        }

        @Override
        public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
            if (databaseError == null && committed) {
                taskCompletionSource.setResult(true);
            } /*else {
                taskCompletionSource.setException(databaseError.toException());
            }*/
        }
    }

    ;

    private Task<Boolean> incrementUserCount(Token token) {
        IncrementTransactionHander handler = new IncrementTransactionHander();
        mDatabaseReference
                .child(STORE_CHILD)
                .child(token.getStoreId())
                .child(COUNTERS_CHILD)
                .child("" + token.getCounter())
                .child(TimeUtils.getDate(token.getDate()))
                .child(COUNTERS_USERS)
                .runTransaction(handler);
        return handler.getTask();
    }

    private Task<Boolean> incrementAvgTAT(Token token) {
        final TaskCompletionSource<Boolean> tatSource = new TaskCompletionSource<>();
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
                .child(TimeUtils.getDate(token.getDate()))
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
                        if (mutableData.equals(currentValue)) {
                            return Transaction.abort();
                        } else {
                            return Transaction.success(mutableData);
                        }
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                        if (databaseError == null && b) {
                            tatSource.setResult(true);
                        } /*else {
                            tatSource.setException(databaseError.toException());
                        }*/
                    }
                });
        return tatSource.getTask();
    }

    private Task<Boolean> incrementAvgBurstTime(Token token) {
        final TaskCompletionSource<Boolean> burstSource = new TaskCompletionSource<>();
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
                .child(TimeUtils.getDate(token.getDate()))
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
                        if (mutableData.equals(currentValue)) {
                            return Transaction.abort();
                        } else {
                            return Transaction.success(mutableData);
                        }
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                        if (databaseError == null && b) {
                            burstSource.setResult(true);
                        } /*else {
                            burstSource.setException(databaseError.toException());
                        }*/
                    }
                });
        return burstSource.getTask();
    }

}

