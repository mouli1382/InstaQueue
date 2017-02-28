package io.swagger.api.impl;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.internal.NonNull;
import com.google.firebase.tasks.Continuation;
import com.google.firebase.tasks.OnFailureListener;
import com.google.firebase.tasks.OnSuccessListener;
import com.google.firebase.tasks.Task;
import com.google.firebase.tasks.TaskCompletionSource;
import com.google.firebase.tasks.Tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.Executor;

import io.swagger.model.RationShopItem;
import io.swagger.api.impl.model.Store;
import io.swagger.api.impl.model.Token;

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
    private static final String FP_SHOP_CHILD = "rationshopmapping/";

    private final static String CLIENT_APP_PLAYSTORE_URL = "https://goo.gl/mVAdpT";

    private DatabaseReference mDatabaseReference;
//    private Executor executor;

    public FirebaseDatabaseManager(DatabaseReference databaseReference) {
        mDatabaseReference = databaseReference;
//        int numCores = Runtime.getRuntime().availableProcessors();
//        executor = new ThreadPoolExecutor(numCores * 2, numCores * 2,
//                60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
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

    private Task<Void> addTokenUnderStoreCounter(Token token) {
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

    public Task<Boolean> createToken(final RationShopItem rationShopItem) {
        TTLogger.info(TAG + "getStore START ------");
        final TaskCompletionSource<Token> getStoreSource = new TaskCompletionSource<>();


        // final TaskCompletionSource<Boolean> getStoreSource = new TaskCompletionSource<>();

        // mDatabaseReference
        //         .child("store")
        //         .child("4ayX5hricwSo6wQjUW7yUULrhZg2")
        //         .child("myname")
        //         .setValue("CHANDRA-MOULI-NARASIMHA")
        //         .addOnSuccessListener(new OnSuccessListener<Void>() {
        //             @Override
        //             public void onSuccess(Void aVoid) {
        //                 TTLogger.info(TAG + "getStore child added SUCCESS ------");
        //                 getStoreSource.setResult(true);
        //             }
        //         })
        //         .addOnFailureListener(new OnFailureListener() {
        //             @Override
        //             public void onFailure(@NonNull Exception e) {
        //                 TTLogger.info(TAG + "getStore child added FAILURE ------");
        //                 getStoreSource.setException(e);
        //             }
        //         });
        // return getStoreSource.getTask();

        mDatabaseReference
                .child("store")
                .child("4ayX5hricwSo6wQjUW7yUULrhZg2")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Store store = dataSnapshot.getValue(Store.class);
                            Token token1 = new Token();
                            token1.setSenderName(store.getName());
                            token1.setSenderPic(store.getLogoUrl());
                            token1.setStoreId(store.getStoreId());
                            token1.setPhoneNumber(rationShopItem.getPhone());
                            token1.setCounter(1);

                            TTLogger.info(TAG + "getStore store found ------");

                            getStoreSource.setResult(token1);
                        } else {
                            TTLogger.info(TAG + "getStore Store not found ------");
                            getStoreSource.setResult(null);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        TTLogger.info(TAG + "getStore ------" + databaseError.toException());
                        getStoreSource.setException(databaseError.toException());
                    }
                });
        return getStoreSource.getTask().continueWithTask(new Continuation<Token, Task<Boolean>>() {
            @Override
            public Task<Boolean> then(@NonNull Task<Token> task) throws Exception {
                return addNewToken(task.getResult());
            }
        });
    }

    public Task<Boolean> addNewToken(final Token token) {
        TTLogger.info(TAG + "addNewToken START ------");
        final TaskCompletionSource<Boolean> addTokenSource = new TaskCompletionSource<>();

        decrementCredits(token)
                .continueWithTask(new Continuation<Boolean, Task<Long>>() {
                    @Override
                    public Task<Long> then(@NonNull Task<Boolean> task) throws Exception {
                        TTLogger.info(TAG + "decrementCredits DONE ------");
                        return incrementTokenCounter(token);
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Long>() {
                    @Override
                    public void onSuccess(final Long currentToken) {
                        TTLogger.info(TAG + "incrementTokenCounter DONE ------" + currentToken);
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
                                            token.getSenderPic(),
                                            token.getSenderName(),
                                            token.getCounter(),
                                            areaName,
                                            /*mappingId*/"");

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
                                                    return sendSMS(newToken, false);
                                                }
                                            })
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    addTokenSource.setResult(true);
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
                                TTLogger.info(TAG + "Token creation failed" + databaseError.toException());
                                addTokenSource.setException(databaseError.toException());
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        TTLogger.info(TAG + "Token creation failed" + e);
                        addTokenSource.setException(e);
                    }
                });
        return addTokenSource.getTask();
    }


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

    private Task<Void> sendSMS(final Token token, final boolean status) {
        //Android SMS API integration code
        final TaskCompletionSource<Boolean> sendSmsSource = new TaskCompletionSource<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
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
    }

    private Task<Token> tokenFromUid(String uId) {
        final TaskCompletionSource<Token> completionSource = new TaskCompletionSource<>();

        mDatabaseReference
                .child("/")
                .child(TOKENS_CHILD)
                .child(uId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Token token = dataSnapshot.getValue(Token.class);
                            completionSource.setResult(token);
                        } else {
                            completionSource.setResult(null);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        completionSource.setException(databaseError.toException());
                    }
                });
        return completionSource.getTask();
    }

    private Task<Token> tokenFromCardNumber(String cardNumber) {
        final TaskCompletionSource<Token> completionSource = new TaskCompletionSource<>();
        mDatabaseReference
                .child("/")
                .child(TOKENS_CHILD)
                .orderByChild("mappingId")
                .equalTo(cardNumber)
                // .limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            HashMap<String, Token> tokens = dataSnapshot.getValue(new GenericTypeIndicator<HashMap<String, Token>>() {
                            });
                            TTLogger.info(TAG + "tokenFromCardNumber token fetched.... ");
                            ArrayList<Token> tokenList = new ArrayList<>(tokens.values());
                            completionSource.setResult(tokenList.get(0));
                        } else {
                            completionSource.setResult(null);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        completionSource.setException(databaseError.toException());
                    }
                });
        return completionSource.getTask();
    }

//    private Task<ArrayList<String>> getAllTokenIds(String storeId) {
//        TTLogger.info(TAG + "getAllTokenIds START.... " + storeId);
//        final TaskCompletionSource<ArrayList<String>> completionSource = new TaskCompletionSource<>();
//
//        mDatabaseReference
//                .child("/")
//                .child(STORE_CHILD)
//                .child(storeId)
//                .child(COUNTERS_CHILD)
//                .child("1" /*hard coding to 1*/)
//                .child(TOKENS_CHILD)
////                .orderByValue()
////                .limitToFirst(100)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.exists()) {
//                            HashMap<String, Long> tokenIds = dataSnapshot.getValue(new GenericTypeIndicator<HashMap<String, Long>>() {
//                            });
//                            TTLogger.info(TAG + "getAllTokenIds tokenIds fetched.... ");
//                            ArrayList<String> tokenIdList = new ArrayList<>(tokenIds.keySet());
//                            completionSource.setResult(tokenIdList);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                        TTLogger.info(TAG + "getAllTokenIds fetching tokenIds failed.... ");
//                        completionSource.setException(databaseError.toException());
//                    }
//                });
//        return completionSource.getTask();
//    }

//    private Task<Boolean> noOpTask() {
//        final TaskCompletionSource<Boolean> noOp = new TaskCompletionSource<>();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                TTLogger.info(TAG + " noOpTask ");
//                noOp.setResult(true);
//            }
//        }).start();
//
//        return noOp.getTask();
//    }

    private Task<String> storeFromFPShopId(String FPShopId) {
        final TaskCompletionSource<String> completionSource = new TaskCompletionSource<>();
        mDatabaseReference
                .child("/")
                .child(FP_SHOP_CHILD)
                .child(FPShopId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String storeUid = dataSnapshot.getValue(String.class);
                            TTLogger.info(TAG + "storeFromFPShopId fetching store UID SUCCESSFUL.... ");
                            completionSource.setResult(storeUid);
                        } else {
                            TTLogger.info(TAG + "storeFromFPShopId fetching store UID FAILED.... ");
                            completionSource.setResult(null);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        TTLogger.info(TAG + "storeFromFPShopId fetching store UID FAILED.... ");
                        completionSource.setException(databaseError.toException());
                    }
                });
        return completionSource.getTask();
    }

    private Task<Token> getInactiveToken(String storeId) {
        TTLogger.info(TAG + "getInactiveToken START.... ");

        final TaskCompletionSource<ArrayList<Token>> completionSource = new TaskCompletionSource<>();

        mDatabaseReference
                .child("/")
                .child(TOKENS_CHILD)
                .orderByChild("storeId")
                .equalTo(storeId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            HashMap<String, Token> tokens = dataSnapshot.getValue(new GenericTypeIndicator<HashMap<String, Token>>() {
                            });
                            TTLogger.info(TAG + "getInactiveToken all tokens under store fetched.... ");
                            ArrayList<Token> tokenList = new ArrayList<>(tokens.values());
                            completionSource.setResult(tokenList);
                        } else {
                            completionSource.setResult(null);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        TTLogger.info(TAG + "getInactiveToken failed to get all tokens.... ");
                        completionSource.setException(databaseError.toException());
                    }
                });

        return completionSource.getTask()
                .continueWith(new Continuation<ArrayList<Token>, ArrayList<Token>>() {
                    @Override
                    public ArrayList<Token> then(@NonNull Task<ArrayList<Token>> task) throws Exception {
                        ArrayList<Token> tokenList = task.getResult();
                        if (task.getResult() != null) {
                            Collections.sort(tokenList, new Comparator<Token>() {
                                @Override
                                public int compare(Token t1, Token t2) {
                                    return Long.valueOf(t1.getTokenNumber()).compareTo(t2.getTokenNumber());
                                }
                            });
                            TTLogger.info(TAG + "getInactiveToken sorted the tokens per token number.... ");
                        }
                        return tokenList;
                    }
                })
                .continueWith(new Continuation<ArrayList<Token>, Token>() {
                    @Override
                    public Token then(@NonNull Task<ArrayList<Token>> task) throws Exception {
                        ArrayList<Token> tokenList = task.getResult();
                        Token token = null;
                        if (tokenList != null) {
                            for (int i = 0; i < tokenList.size(); ++i) {
                                token = tokenList.get(i);
                                if (token.isIssued()) {
                                    // Found the inactive token found.
                                    break;
                                }
                            }
                            TTLogger.info(TAG + "getInactiveToken found the inactive token to activate....");
                        }
                        return token;
                    }
                });
    }

    public Task<Void> activateOrComplete(final RationShopItem rationShopItem) {
        TTLogger.info(TAG + "activateOrComplete START.... ");

        // Complete the one we got kicked for in a separate async piece
        // ToDo replace tokenFromUid with tokenFromCardNumber
        Task<Boolean> completeTask = tokenFromCardNumber(rationShopItem.getCardNumber())
                .continueWithTask(new Continuation<Token, Task<Boolean>>() {
                    @Override
                    public Task<Boolean> then(@NonNull Task<Token> task) throws Exception {
                        TTLogger.info(TAG + "activateOrComplete complete token.... ");
                        if (task.getResult() == null) {
                            throw new Exception("No token to complete.");
                        } else {
                            return completeCurrentToken(task.getResult());
                        }
                    }
                });

        // Activate the next inactive token in another async piece
        Task<Boolean> activateTask = storeFromFPShopId(rationShopItem.getId())
                .continueWithTask(new Continuation<String, Task<Token>>() {
                    @Override
                    public Task<Token> then(@NonNull Task<String> task) throws Exception {
                        TTLogger.info(TAG + "activateOrComplete activate token.... ");
                        if (task.getResult() == null) {
                            throw new Exception("No token to activate");
                        } else {
                            return getInactiveToken(task.getResult());
                        }
                    }
                })
                .continueWithTask(new Continuation<Token, Task<Boolean>>() {
                    @Override
                    public Task<Boolean> then(@NonNull Task<Token> task) throws Exception {
                        TTLogger.info(TAG + "activateOrComplete activate token.... ");
                        if (task.getResult() == null) {
                            throw new Exception("No token to activate");
                        } else {
                            return activateNextToken(task.getResult());
                        }
                    }
                });
        return Tasks.whenAll(completeTask, activateTask);
    }

    private Task<Boolean> completeCurrentToken(Token token) {
        //Bingo - Mark it COMPLETED
        token.setStatus(Token.Status.COMPLETED.ordinal());
        return updateToken(token);
    }

    private Task<Boolean> activateNextToken(Token token) {
        //Bingo - Mark it READY
        token.setStatus(Token.Status.READY.ordinal());
        token.setBuzzCount(token.getBuzzCount() + 1);
        return updateToken(token);
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
                .child(TOKENS_CHILD)
                .child(token.getuId())
                .removeValue();
    }

    public Task<Boolean> updateToken(final Token token) {
        final TaskCompletionSource<Boolean> updateToken = new TaskCompletionSource<>();
        TTLogger.info(TAG + "updateToken START.... ");
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

                                            TTLogger.info(TAG + "updateToken avg burst time updated.... ");
                                            //Add it to token-history table.
                                            return moveToHistory(tokenUpdated);
                                        } else {
                                            TTLogger.info(TAG + "updateToken avg burst time failed.... ");
                                            throw new Exception("Updating Avg burst time failed.");
                                        }
                                    }
                                })
                                .continueWithTask(new Continuation<Void, Task<Void>>() {
                                    @Override
                                    public Task<Void> then(@NonNull Task<Void> task) throws Exception {
                                        TTLogger.info(TAG + "updateToken token added to history table.... ");
                                        return removeFromTokenTable(tokenUpdated);
                                    }
                                })
                                .continueWithTask(new Continuation<Void, Task<Void>>() {
                                    @Override
                                    public Task<Void> then(@NonNull Task<Void> task) throws Exception {
                                        TTLogger.info(TAG + "updateToken token removed from token table.... ");
                                        return removeTokenFromStore(tokenUpdated);
                                    }
                                })
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        TTLogger.info(TAG + "updateToken token removed from Store-Counter.... ");
                                        updateToken.setResult(true);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        TTLogger.info(TAG + "updateToken failed.... ");
                                        updateToken.setException(e);
                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    TTLogger.info(TAG + "Token update failed..." + databaseError.toException());
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
                            TTLogger.info(TAG + "updateToken token status updated to COMPLETED.... ");
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
                            TTLogger.info(TAG + "updateToken token finish time updated.... ");
                            mDatabaseReference
                                    .child(TOKENS_CHILD)
                                    .child(token.getuId()).addListenerForSingleValueEvent(completionListener);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            TTLogger.info(TAG + "updateToken token finish time updation failed.... ");
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

                            Tasks.whenAll(sendSMS(tokenUpdated, true)
                                    , incrementUserCount(tokenUpdated)
                                    , incrementAvgTAT(tokenUpdated)
                                    , setActiveTokenNumber(tokenUpdated))
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            TTLogger.info(TAG + "updateToken user count, avg TAT & token activation done.... ");
                                            updateToken.setResult(true);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            TTLogger.info(TAG + "updateToken user count, avg TAT & token activation FAILED.... ");
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
                    TTLogger.info(TAG + "SMS won't be sent" + databaseError.toException());
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
                            TTLogger.info(TAG + "updateToken status updated to READY.... ");
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
                            TTLogger.info(TAG + "updateToken token activation time updated.... ");
                            mDatabaseReference
                                    .child(TOKENS_CHILD)
                                    .child(token.getuId()).addListenerForSingleValueEvent(valueEventListener);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            TTLogger.info(TAG + "updateToken token activation time updation failed.... ");
                            updateToken.setException(e);
                        }
                    });
        }
        return updateToken.getTask();
    }

    private Task<Boolean> decrementCredits(Token token) {
        TTLogger.info(TAG + "decrementCredits START ------");
        final TaskCompletionSource<Boolean> completionSource = new TaskCompletionSource<>();
        DatabaseReference creditsRef = mDatabaseReference
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
                    TTLogger.info(TAG + "decrementCredits SUCCESS ------");
                    return Transaction.success(mutableData);
                }
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                if (databaseError == null && committed) {
                    TTLogger.info(TAG + "decrementCredits END ------");
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

    private Task<Boolean> incrementUserCount(Token token) {
        IncrementTransactionHander handler = new IncrementTransactionHander();
        mDatabaseReference
                .child(STORE_CHILD)
                .child(token.getStoreId())
                .child(COUNTERS_CHILD)
                .child("" + token.getCounter())
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

    private Task<Long> incrementTokenCounter(Token token) {
        final TaskCompletionSource<Long> incrementTokenTask = new TaskCompletionSource<>();
        TTLogger.info(TAG + "incrementTokenCounter START ------");
        DatabaseReference tokenCounterRef = mDatabaseReference
                .child("store")
                .child(token.getStoreId())
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
                    TTLogger.info(TAG + "incrementTokenCounter SUCCESS ------");
                    return Transaction.success(mutableData);
                }
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                if (databaseError == null && b) {
                    TTLogger.info(TAG + "incrementTokenCounter END ------");
                    incrementTokenTask.setResult(dataSnapshot.getValue(Long.class));
                } /*else {
                    incrementTokenTask.setException(databaseError.toException());
                }*/
            }
        });

        return incrementTokenTask.getTask();
    }

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

