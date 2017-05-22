package in.mobifirst.tagtree.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

import javax.inject.Inject;

import in.mobifirst.tagtree.application.IQStoreApplication;
import in.mobifirst.tagtree.database.FirebaseDatabaseManager;
import in.mobifirst.tagtree.model.Token;
import in.mobifirst.tagtree.preferences.IQSharedPreferences;
import in.mobifirst.tagtree.util.ApplicationConstants;
import rx.Subscriber;

public class SmsReceiver extends BroadcastReceiver {

    private static final String TAG = "SmsReceiver";

    @Inject
    protected FirebaseDatabaseManager mFirebaseDatabaseManager;

    @Inject
    IQSharedPreferences mIQSharedPreferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        ((IQStoreApplication) context.getApplicationContext()).getApplicationComponent()
                .inject(this);

        Bundle intentExtras = intent.getExtras();

        if (intentExtras != null) {
            /* Get Messages */
            Object[] sms = (Object[]) intentExtras.get("pdus");

            for (int i = 0; i < sms.length; ++i) {
                /* Parse Each Message */
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                String phone = smsMessage.getOriginatingAddress();
                String message = smsMessage.getMessageBody().toString();

                Log.e(TAG, "phone number = " + phone);
                Log.e(TAG, "SMS body = " + message);

                checkAndIssueToken(phone, message);
            }
        }
    }

    private void checkAndIssueToken(String phoneNumber, String message) {
        if (TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(message)) {
            return;
        }

        String[] words = message.split("\\s");
        if (words.length == 2) {
            String keyWord = words[0];
            String rationCard = words[1];

            if (!TextUtils.isEmpty(keyWord) && keyWord.equalsIgnoreCase("token") && !TextUtils.isEmpty(rationCard)) {
                addNewTokenForThisStore(phoneNumber, rationCard, 1);
            } else {
                //Send token rejected sms
                Log.e(TAG, "INVALID INPUT RECEIVED = " + message);
            }
        } else {
            //Send token rejected sms
            Log.e(TAG, "WRONG SMS FORMAT RECEIVED = " + message);
        }
    }

    public void addNewTokenForThisStore(String phoneNumber, String metaRationCard, int counterNumber/*, String storeId*/) {
        Token token = new Token();
        /*int storeSwitch = Integer.parseInt(storeId);
        switch (storeSwitch) {
            case 1:
                token.setStoreId("4ayX5hricwSo6wQjUW7yUULrhZg2");
                break;
            case 2:
            default:
                token.setStoreId("LEPo6aD44vTLrqSYVJJooUgItl82");
                break;
        }*/

        token.setPhoneNumber(phoneNumber);
        token.setStoreId(mIQSharedPreferences.getSting(ApplicationConstants.STORE_UID));
        token.setMappingId(metaRationCard);
        saveToken(token);
    }

    private void saveToken(@NonNull Token token) {
        mFirebaseDatabaseManager.addNewToken(token, new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "Token created successfully!");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "Token creation failed!");
            }

            @Override
            public void onNext(String result) {
            }
        });

    }
}