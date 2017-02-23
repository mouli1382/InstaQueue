package in.mobifirst.tagtree.backend;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.internal.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import in.mobifirst.tagtree.backend.model.RationShopItem;
import in.mobifirst.tagtree.backend.model.Token;

public class FirebaseDatabaseManager {
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

    public FirebaseDatabaseManager(DatabaseReference databaseReference) {
        mDatabaseReference = databaseReference;
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

//    public Query getTokensRef(String uId) {
//        return mDatabaseReference
//                .child(TOKENS_CHILD)
//                .orderByChild("storeId")
//                .equalTo(uId);
//    }

//    private Task<Void> addTokenUnderStoreCounter(Token token) {
//        //Save the token Id under the token number which makes client job easy in sorting and calculating TAT.
//        return mDatabaseReference
//                .child(STORE_CHILD)
//                .child(token.getStoreId())
//                .child(COUNTERS_CHILD)
//                .child("" + token.getCounter())
//                .child(TOKENS_CHILD)
//                .child(token.getuId())
//                .setValue(token.getTokenNumber());
//    }

//    public void addNewToken(final Token token, final AddTokenResponse addTokenResponse) {
//        incrementTokenCounter(token, new Transaction.Handler() {
//            @Override
//            public Transaction.Result doTransaction(MutableData mutableData) {
//                Long currentValue = mutableData.getValue(Long.class);
//                if (currentValue == null) {
//                    mutableData.setValue(1);
//                } else {
//                    mutableData.setValue(currentValue + 1);
//                }
//
//                return Transaction.success(mutableData);
//            }
//
//            @Override
//            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
//                if (databaseError == null) {
//                    if (committed) {
//                        {
//                            final Long currentToken = (Long) dataSnapshot.getValue();
//                            DatabaseReference storeRef = mDatabaseReference.getRef()
//                                    .child("store")
//                                    .child(token.getStoreId());
//
//
//                            storeRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot storeDataSnapshot) {
//                                    if (storeDataSnapshot.exists()) {
//
//                                        String key = mDatabaseReference.child(TOKENS_CHILD)
//                                                .push().getKey();
//                                        Store store = storeDataSnapshot.getValue(Store.class);
//                                        String areaName = store.getArea();
//                                        final Token newToken = new Token(key, token.getStoreId(),
//                                                token.getPhoneNumber(),
//                                                currentToken,
//                                                mSharedPrefs.getSting((ApplicationConstants.WEBSITE_LOGO_URL_KEY)),
//                                                mSharedPrefs.getSting(ApplicationConstants.DISPLAY_NAME_KEY),
//                                                token.getCounter(),
//                                                areaName);
//
//                                        mDatabaseReference.child(TOKENS_CHILD).child(key).setValue(newToken.toMap());
//                                        addTokenUnderStoreCounter(newToken);
//
//                                        //ToDo replicate the entire tokens under store. Too much but easy.
////                                        Map<String, Object> childUpdates = new HashMap<>();
////                                        childUpdates.put(TOKENS_CHILD + key, tokenValues);
////                                        childUpdates.put(STORE_CHILD + newToken.getStoreId() + "/" + newToken.getCounter() + "/" + TOKENS_CHILD + key, tokenValues);
////
////                                        mDatabaseReference.updateChildren(childUpdates);
//                                        checkSMSSending(newToken, false);
//                                    } else {
//                                        Log.e(TAG, "Snapshot is null");
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//                                    Log.e(TAG, "[fetch Area name] onCancelled:" + databaseError);
//                                }
//                            });
//
//                            subscriber.onNext(null);
//                            subscriber.onCompleted();
//                        }
//                    } else {
//                        subscriber.onError(databaseError.toException());
//                    }
//                } else {
//                    subscriber.onError(databaseError.toException());
//                }
//
//            }
//        });
//    }

//    public void getStoreById(String storeId, ValueEventListener valueEventListener) {
//        DatabaseReference storeRef = mDatabaseReference
//                .child(STORE_CHILD)
//                .child(storeId);
//        storeRef.addListenerForSingleValueEvent(valueEventListener);
//    }


//    private void checkSMSSending(final Token token, final boolean status) {
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
//                    if (!BuildConfig.DEBUG) {
//                        sendSMS(token, status);
//                    }
////                    sendBulkSMS(token, status);
//                }
//                else {
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

    private void sendSMS(Token token, boolean status) {
        //Android SMS API integration code

        //Your authentication key
        String authkey = "128441AGQNt0b0eb2q580367e2";
        //Multiple mobiles numbers separated by comma
        String mobiles = token.getPhoneNumber();
        //Sender ID,While using route4 sender id should be 6 characters long.
        String senderId = "TagTre";
        String message = "";

        //Your message to send, Add URL encoding here.
        if (!status) {
            switch (1 /*Defult to Telugu*/) {
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
            switch (1 /*Defult to Telugu*/) {
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
            final StringBuilder sbPostData = new StringBuilder(mainUrl);
            sbPostData.append("authkey=" + authkey);
            sbPostData.append("&mobiles=" + mobiles);
            sbPostData.append("&message=" + encoded_message);
            sbPostData.append("&route=" + route);
            sbPostData.append("&sender=" + senderId);
            sbPostData.append("&unicode=1");


            //final string
//            mainUrl = sbPostData.toString();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //prepare connection
                        URL myURL = new URL(sbPostData.toString());
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
                    }
                }
            }).start();
            incrementSMS(token, new IncremnetTransactionHander()
            );

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void activate(RationShopItem rationShopItem) {
        //Fetch the store from the given FP shop ID and lookup the current active token
//        mDatabaseReference
//                .child(STORE_CHILD)
//                .orderByChild("FPShopId")
//                .equalTo(rationShopItem.getId())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.exists()) { //Success case
//                            Store store = dataSnapshot.getValue(Store.class);
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });

        completeCurrentToken(rationShopItem);
    }

    private void completeCurrentToken(final RationShopItem rationShopItem) {
        //ToDo handle it better
        // For now assuming that there will only be one token in the Tokens table for a ration card number.
        mDatabaseReference
                .child(TOKENS_CHILD)
                .orderByChild("cardNumber")
                .equalTo(rationShopItem.getCardNumber())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Token token = dataSnapshot.getValue(Token.class);
                            if (token.getStoreId().equalsIgnoreCase(rationShopItem.getId()) && token.isActive()) {
                                String storeId = token.getStoreId();
                                Long tokenNumber = token.getTokenNumber();

                                //Bingo - Mark it COMPLETED
                                token.setStatus(Token.Status.COMPLETED.ordinal());
                                updateToken(token);

                                //Active the next token
                                activateNextToken(storeId, tokenNumber);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void activateNextToken(String storeId, Long tokenNumber) {
        mDatabaseReference
                .child(TOKENS_CHILD)
                .orderByChild("storeId")
                .equalTo(storeId)
                .startAt(tokenNumber, "tokenNumber")
                .limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Token token = dataSnapshot.getValue(Token.class);
                            if (!token.isActive()) {
                                //Bingo - Mark it READY
                                token.setStatus(Token.Status.READY.ordinal());
                                token.setBuzzCount(token.getBuzzCount() + 1);
                                updateToken(token);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

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
                    TagTreeLogger.info(TAG + "Token completion time is not updated" + databaseError.toException());
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
                            sendSMS(tokenUpdated, true);
//                            checkSMSSending(tokenUpdated, true);
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
                    TagTreeLogger.info(TAG + "SMS won't be sent" + databaseError.toException());
                }
            };
            mDatabaseReference
                    .child(TOKENS_CHILD)
                    .child(token.getuId()).addListenerForSingleValueEvent(postListener);
        }
    }

//    private void decrementCredits(Token token, Transaction.Handler handler) {
//        DatabaseReference creditsRef = mDatabaseReference
//                .child("store")
//                .child(token.getStoreId())
//                .child("credits");
//        creditsRef.runTransaction(handler);
//
//    }

    private void incrementSMS(Token token, Transaction.Handler handler) {
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

    private void incrementUserCount(Token token, Transaction.Handler handler) {
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

//    private void incrementTokenCounter(Token token, Transaction.Handler handler) {
//        DatabaseReference tokenCounterRef = mDatabaseReference
//                .child("store")
//                .child(token.getStoreId())
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
//    }

    //Reset token counter
//    public void resetTokenCounter(String storeId) {
//        DatabaseReference tokenCounterRef = mDatabaseReference
//                .child("store")
//                .child(storeId)
//                .child("tokenCounter");
//        tokenCounterRef.runTransaction(new Transaction.Handler() {
//            @Override
//            public Transaction.Result doTransaction(MutableData mutableData) {
//                mutableData.setValue(0);
//
//                return Transaction.success(mutableData);
//            }
//
//            @Override
//            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
//
//            }
//        });
//    }
}

