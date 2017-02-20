/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package in.mobifirst.tagtree.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.internal.NonNull;
import com.google.firebase.tasks.OnSuccessListener;
import com.google.firebase.tasks.Task;

import java.util.logging.Logger;

import javax.servlet.ServletContext;

/**
 * An endpoint class we are exposing
 */
@Api(
        name = "myApi",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.tagtree.mobifirst.in",
                ownerName = "backend.tagtree.mobifirst.in",
                packagePath = ""
        )
)
public class MyEndpoint {
    static Logger Log = Logger.getLogger("in.mobifirst.tagtree.backend.myApi");
    private static final String TOKENS_CHILD = "tokens";
    private static final String STORE_CHILD = "store";
    private static final String COUNTERS_CHILD = "counters";

    /**
     * A simple endpoint method that takes a name and says Hi back
     */
//    @ApiMethod(name = "sayHi")
//    public MyBean sayHi(@Named("name") String name) {
//        MyBean response = new MyBean();
//        response.setData("Hi, " + name);
//
//        return response;
//    }
    private void decrementCredits(DatabaseReference mDatabaseReference, Token token, @NonNull Transaction.Handler handler) {
        DatabaseReference creditsRef = mDatabaseReference
                .child("store")
                .child(token.getStoreId())
                .child("credits");
        creditsRef.runTransaction(handler);

    }

//    private void incrementSMS(DatabaseReference mDatabaseReference, Token token, @NonNull Transaction.Handler handler) {
//        DatabaseReference creditsRef = mDatabaseReference
//                .child("store")
//                .child(token.getStoreId())
//                .child("smsCounter");
//        creditsRef.runTransaction(handler);
//
//    }

    private void incrementTokenCounter(DatabaseReference mDatabaseReference, Token token, @NonNull Transaction.Handler handler) {
        Log.info("passed in parameter = " + token.toMap());
        DatabaseReference tokenRef = mDatabaseReference
                .child("store")
                .child(token.getStoreId())
                .child("tokenCounter");
        tokenRef.runTransaction(handler);
    }

    private Task<Void> addTokenUnderStoreCounter(DatabaseReference mDatabaseReference, Token token) {
        //Save the token Id under the token number which makes client job easy in sorting and calculating TAT.
        return mDatabaseReference
                .child(STORE_CHILD)
                .child(token.getStoreId())
                .child(COUNTERS_CHILD)
                .child("" + token.getCounter())
                .child(TOKENS_CHILD)
                .child(token.getuId())
                .setValue(token.getTokenNumber());
    }

    @ApiMethod(name = "addNewToken")
    public AddTokenResponse addNewToken(final Token token, ServletContext context) {
        Log.info("passed in parameter = " + token.toMap());

        final AddTokenResponse addTokenResponse = new AddTokenResponse();

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setServiceAccount(context.getResourceAsStream("/WEB-INF/TagTree-Dev-5c7176a14844.json"))
                .setDatabaseUrl("https://tagtree-dev.firebaseio.com/")
                .build();

        try {
            FirebaseApp.getInstance();
        } catch (Exception error) {
            Log.info("doesn't exist...");
        }

        try {
            FirebaseApp.initializeApp(options);
        } catch (Exception error) {
            Log.info("already exists...");
        }

        // As an admin, the app has access to read and write all data, regardless of Security Rules
        final DatabaseReference mDatabaseReference = FirebaseDatabase
                .getInstance()
                .getReference();
        Log.info("DatabaseRef obtained...");

//        mDatabaseReference
//                .child("store")
//                .child(token.getStoreId())
//                .child("credits")
//                .runTransaction(new Transaction.Handler() {
//                    @Override
//                    public Transaction.Result doTransaction(MutableData mutableData) {
//                        Long currentValue = mutableData.getValue(Long.class);
//                        if (currentValue == null) {
//                            mutableData.setValue(1000);
//                        } else {
//                            mutableData.setValue(currentValue - 1);
//                        }
//                        Log.info("credits decremented..." + mutableData);
//                        return Transaction.success(mutableData);
//                    }
//
//                    @Override
//                    public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
//                    }
//                });

        mDatabaseReference
                .child("store/" + token.getStoreId() + "/tokenCounter")
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        Long currentValue = mutableData.getValue(Long.class);
                        if (currentValue == null) {
                            mutableData.setValue(1);
                        } else {
                            mutableData.setValue(currentValue + 1);
                        }
                        Log.info("token counter incremented..." + mutableData);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                        if (databaseError == null && committed) {
                            final Long currentToken = (Long) dataSnapshot.getValue();

                            DatabaseReference storeRef = mDatabaseReference.getRef()
                                    .child("store")
                                    .child(token.getStoreId());

                            storeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot storeDataSnapshot) {
                                    Log.info("store details fetched...");
                                    if (storeDataSnapshot.exists()) {
                                        String key = mDatabaseReference.child(TOKENS_CHILD)
                                                .push().getKey();
                                        Store store = storeDataSnapshot.getValue(Store.class);
                                        String areaName = store.getArea();
                                        final Token newToken = new Token(key, token.getStoreId(),
                                                token.getPhoneNumber(),
                                                currentToken,
                                                token.getSenderPic(),
                                                token.getSenderName(),
                                                token.getCounter(),
                                                areaName);

                                        mDatabaseReference.child(TOKENS_CHILD).child(key).setValue(newToken.toMap()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.info("token is created...");
                                                addTokenUnderStoreCounter(mDatabaseReference, newToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.info("token is updated under store structure too...");
                                                        addTokenResponse.setStatus(true);
                                                    }
                                                });
                                            }
                                        });
//                                        checkSMSSending(newToken, false);
                                    } else {
                                        addTokenResponse.setStatus(false);
                                        Log.info("Snapshot is null");
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    addTokenResponse.setStatus(false);
                                    Log.info("[fetch Area name] onCancelled:" + databaseError);
                                }
                            });
                        } else {
                            addTokenResponse.setStatus(false);
                            Log.info("Transaction failed" + databaseError);
                        }
                    }
                });
        return addTokenResponse;
    }
}
