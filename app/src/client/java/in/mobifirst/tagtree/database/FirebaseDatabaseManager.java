package in.mobifirst.tagtree.database;

import android.util.Log;

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

public class FirebaseDatabaseManager implements DatabaseManager {

    private IQSharedPreferences  mSharedPrefs;

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

    //ToDo limit by date and status.
    public Observable<List<Token>> getAllTokens(final String uId) {
        return Observable.create(new Observable.OnSubscribe<List<Token>>() {
            @Override
            public void call(final Subscriber<? super List<Token>> subscriber) {
                Query query = mDatabaseReference
                        .child(TOKENS_CHILD)
                        .orderByChild("phoneNumber")
                        .equalTo(mSharedPrefs.getSting(ApplicationConstants.PHONE_NUMBER_KEY));
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
                                subscriber.onCompleted();
                            }
                        } else {
                            subscriber.onError(new Exception("Empty Tokens."));
                            subscriber.onCompleted();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "[fetch All Tokens] onCancelled:" + databaseError);
                        subscriber.onError(new Exception("Empty Tokens."));
                        subscriber.onCompleted();
                    }
                });
            }
        });
    }

    public Observable<List<Token>> observeTokens() {
        return Observable.create(new Observable.OnSubscribe<List<Token>>() {
            @Override
            public void call(final Subscriber<? super List<Token>> subscriber) {
                Query query = mDatabaseReference
                        .child(TOKENS_CHILD)
                        .orderByChild("phoneNumber")
                        .equalTo(mSharedPrefs.getSting(ApplicationConstants.PHONE_NUMBER_KEY));
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
                                subscriber.onCompleted();
                            }
                        } else {
                            subscriber.onError(new Exception("Empty Tokens."));
                            subscriber.onCompleted();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "[fetch All Tokens] onCancelled:" + databaseError);
                        subscriber.onError(new Exception("Empty Tokens."));
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

