package in.mobifirst.tagtree.database;

import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import in.mobifirst.tagtree.model.Token;
import in.mobifirst.tagtree.preferences.IQSharedPreferences;
import in.mobifirst.tagtree.util.ApplicationConstants;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

public class FirebaseDatabaseManager implements DatabaseManager {

    private IQSharedPreferences mSharedPrefs;

    private static final String TAG = "FirebaseDatabaseManager";

    private static final String TOKENS_CHILD = "tokens/";

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

    //ToDo limit by date and status.
    public Observable<List<Token>> getAllTokens() {
        return rx.Observable.create(new Observable.OnSubscribe<List<Token>>() {
            @Override
            public void call(final Subscriber<? super List<Token>> subscriber) {
                getTokenRef()
                        .addValueEventListener(new ValueEventListener() {
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
                                                            return new Long(token.getTimestamp()).compareTo(token2.getTimestamp());
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
//                        switch (mCurrentFiltering) {
//                            case ACTIVE_TOKENS:
//                                return token.isActive();
//                            case COMPLETED_TOKENS:
//                                return token.isCompleted();
//                            case CANCELLED_TOKENS:
//                                return token.isCancelled();
//                            default:
//                                return true;
//                        }

                                                            return !token.isCompleted();
                                                        }
                                                    })
                                                    .toList()
                                                    .subscribe(new Action1<List<Token>>() {
                                                        @Override
                                                        public void call(List<Token> tokenList) {
                                                            subscriber.onNext(tokenList);
                                                        }
                                                    });

//                                    subscriber.onNext(new ArrayList<>(tokens.values()));
//                                    subscriber.onCompleted();
                                        } else {
                                            FirebaseCrash.report(new Exception("Empty Tokens"));
                                            subscriber.onNext(null);
//                                    subscriber.onCompleted();
                                        }
                                    } else {
                                        FirebaseCrash.report(new Exception("Empty Tokens"));
                                        subscriber.onNext(null);
//                                subscriber.onCompleted();
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
}

