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
import rx.Subscriber;

public class SmsReceiver extends BroadcastReceiver {

    private static final String TAG = "SmsReceiver";

    @Inject
    protected FirebaseDatabaseManager mFirebaseDatabaseManager;

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
        if (message.contains("TT_ISSUE")) {
            String[] tokens = message.split(",");
            addNewToken(phoneNumber, 1 /* ToDo change later*/, tokens[1]);
        }
    }

    public void addNewToken(String phoneNumber, int counterNumber, String storeId) {
        Token token = new Token();
        int storeSwitch = Integer.parseInt(storeId);
        switch (storeSwitch) {
            case 1:
                token.setStoreId("4ayX5hricwSo6wQjUW7yUULrhZg2");
                break;
            case 2:
            default:
                token.setStoreId("LEPo6aD44vTLrqSYVJJooUgItl82");
                break;
        }
        token.setPhoneNumber(phoneNumber);
        token.setCounter(counterNumber);
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