package in.gm.instaqueue.database;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import in.gm.instaqueue.backend.messaging.Messaging;
import in.gm.instaqueue.model.Token;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class FirebaseDatabaseManager implements DatabaseManager {
    private static final String TAG = "FirebaseDatabaseManager";

    private static final String TOKENS_CHILD = "tokens/";
    private static final String TOPICS_CHILD = "topics/";

    private DatabaseReference mDatabaseReference;
    private Messaging messagingApiService = null;

    @Inject
    public FirebaseDatabaseManager() {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
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
                            final Token newToken = new Token(key, token.getStoreId(), token.getPhoneNumber(), currentToken);
                            mDatabaseReference.child(TOKENS_CHILD).child(key).setValue(newToken.toMap());

                            sendPushToClient(newToken.getuId(), newToken.getPhoneNumber());

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

    //send a push message to the client.
    private void sendPushToClient(final String tokenId, final String phoneNumber) {
        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                try {
                    if (messagingApiService == null) {  // Only do this once
                        Messaging.Builder builder = new Messaging.Builder(AndroidHttp.newCompatibleTransport(),
                                new AndroidJsonFactory(), null)
                                // options for running against local devappserver
                                // - 10.0.2.2 is localhost's IP address in Android emulator
                                // - turn off compression when running against local devappserver
                                .setRootUrl("https://instaqueue-9f086.appspot.com/_ah/api/")
//                        .setRootUrl("http://192.168.1.6:8080/_ah/api/")
                                .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                                    @Override
                                    public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                        abstractGoogleClientRequest.setDisableGZipContent(true);
                                    }
                                });
                        // end options for devappserver

                        messagingApiService = builder.build();
                    }

                    messagingApiService.pushMessage(tokenId, phoneNumber).execute();

                    subscriber.onNext(null);
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "FCM push completed!");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "FCM push error!");
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Object o) {

                    }
                });
    }
}

