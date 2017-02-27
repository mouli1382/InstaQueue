package in.mobifirst.tagtree.database;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import in.mobifirst.tagtree.model.Store;
import in.mobifirst.tagtree.model.StoreCounter;
import in.mobifirst.tagtree.model.Token;
import in.mobifirst.tagtree.preferences.IQSharedPreferences;
import in.mobifirst.tagtree.util.ApplicationConstants;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

public class FirebaseDatabaseManager implements DatabaseManager {

    private IQSharedPreferences mSharedPrefs;

    private static final String TAG = "FirebaseDatabaseManager";

    private static final String TOKENS_CHILD = "tokens/";
    private static final String STORE_CHILD = "store/";
    private static final String COUNTERS_CHILD = "counters/";
    private static final String COUNTERS_LAST_ACTIVE_TOKEN = "activatedToken/";
    private static final String COUNTERS_AVG_TAT_CHILD = "avgTurnAroundTime/";
    private static final String COUNTERS_USERS = "counterUserCount/";
    private long waitTimePerToken = 0;
    private DatabaseReference mDatabaseReference;

    @Inject
    public FirebaseDatabaseManager(IQSharedPreferences iqSharedPreferences) {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mSharedPrefs = iqSharedPreferences;
    }

    public DatabaseReference getDatabaseReference() {
        return mDatabaseReference;
    }

    public Query getTokenRef() {
        return mDatabaseReference
                .child(TOKENS_CHILD)
                .orderByChild("phoneNumber")
                .equalTo(mSharedPrefs.getSting(ApplicationConstants.PHONE_NUMBER_KEY));
    }

    public Observable<List<Store>> getAllStores() {
        return rx.Observable.create(new Observable.OnSubscribe<List<Store>>() {
            @Override
            public void call(final Subscriber<? super List<Store>> subscriber) {
                final Query query = mDatabaseReference
                        .child(STORE_CHILD);

                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!subscriber.isUnsubscribed()) {
                            if (dataSnapshot.exists()) {
                                HashMap<String, Store> storeHashMap = dataSnapshot.getValue(new GenericTypeIndicator<HashMap<String, Store>>() {
                                });
                                if (storeHashMap != null) {
                                    Observable.just(new ArrayList<>(storeHashMap.values()))
                                            .subscribe(new Action1<List<Store>>() {
                                                @Override
                                                public void call(List<Store> storeList) {
                                                    subscriber.onNext(storeList);
                                                }
                                            });
                                } else {
                                    subscriber.onNext(null);
                                }
                            } else {
                                subscriber.onNext(null);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "[fetch All Stores] onCancelled:" + databaseError);
                        subscriber.onError(new Exception("Empty Stores."));
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

    public void getCounterStatus(final Token token, final Subscriber<StoreCounter> subscriber) {
        Log.e(TAG, "Passed-In token = " + token.getuId());
        mDatabaseReference
                .child(STORE_CHILD)
                .child(token.getStoreId())
                .child(COUNTERS_CHILD)
                .child("" + token.getCounter())
//                .child(COUNTERS_LAST_ACTIVE_TOKEN)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.e(TAG, "Called token = " + token.getuId());
                        if (dataSnapshot != null && dataSnapshot.exists()) {
                            StoreCounter storeCounter = dataSnapshot.getValue(StoreCounter.class);
                            subscriber.onNext(storeCounter);
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

    private void decrementCredits(Token token, @NonNull Transaction.Handler handler) {
        DatabaseReference creditsRef = mDatabaseReference
                .child("store")
                .child(token.getStoreId())
                .child("credits");
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

    public void addNewToken(final Store store, final Subscriber<? super String> subscriber) {
        Token token = new Token();
        token.setPhoneNumber(mSharedPrefs.getSting(ApplicationConstants.PHONE_NUMBER_KEY));
        token.setStoreId(store.getStoreId());

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
                            String key = mDatabaseReference.child(TOKENS_CHILD)
                                    .push().getKey();
                            final Token newToken = new Token(key, store.getStoreId(),
                                    mSharedPrefs.getSting(ApplicationConstants.PHONE_NUMBER_KEY),
                                    currentToken,
                                    store.getLogoUrl(),
                                    store.getName(),
                                    1, //ToDo populate counters from the store and select from there.
                                    store.getArea());

                            mDatabaseReference.child(TOKENS_CHILD).child(key).setValue(newToken.toMap());
                            addTokenUnderStoreCounter(newToken);

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
}

