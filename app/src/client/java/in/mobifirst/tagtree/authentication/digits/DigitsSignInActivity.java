package in.mobifirst.tagtree.authentication.digits;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.AuthConfig;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import javax.inject.Inject;

import in.mobifirst.tagtree.activity.BaseActivity;
import in.mobifirst.tagtree.application.IQClientApplication;
import in.mobifirst.tagtree.authentication.FirebaseAuthenticationManager;
import in.mobifirst.tagtree.database.FirebaseDatabaseManager;
import in.mobifirst.tagtree.model.User;
import in.mobifirst.tagtree.preferences.IQSharedPreferences;
import in.mobifirst.tagtree.tokens.TokensActivity;
import in.mobifirst.tagtree.util.ApplicationConstants;

public class DigitsSignInActivity extends BaseActivity {

    private static final String TAG = "DigitsAuth";
    private String mPhoneNumber;
    private String mLineNumber;

    @Inject
    FirebaseAuthenticationManager mAuthenticationManager;

    @Inject
    FirebaseDatabaseManager mFirebaseDatabaseManager;

    @Inject
    IQSharedPreferences mSharedPrefs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((IQClientApplication) getApplication())
                .getApplicationComponent()
                .inject(this);


        if (checkPermission(this, ApplicationConstants.PERMISSION_READ_PHONE_STATE)) {
            TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            mLineNumber = tMgr.getLine1Number();
        }

        AuthCallback authCallback = new AuthCallback() {
            @Override
            public void success(DigitsSession session, String phoneNumber) {
                Log.d(TAG, "Sign in with DigitsAuth successful");
                mPhoneNumber = session.getPhoneNumber();

                FirebaseUser user = mAuthenticationManager.getAuthInstance().getCurrentUser();
                if (user != null) {
                    mSharedPrefs.putString(ApplicationConstants.PHONE_NUMBER_KEY, mPhoneNumber);
                    mSharedPrefs.putBoolean(ApplicationConstants.FTU_COMPLETED_KEY, true);
                    writeNewUser(user.getUid(), user.getDisplayName(), user.getEmail(), mPhoneNumber);

                    //Launch the landing screen.
                    TokensActivity.start(DigitsSignInActivity.this);
                }
                finish();
            }

            @Override
            public void failure(DigitsException exception) {
                Log.d(TAG, "Sign in with DigitsAuth failure", exception);
            }
        };

        AuthConfig.Builder authConfigBuilder = new AuthConfig.Builder()
                .withAuthCallBack(authCallback)
                .withPhoneNumber("+91" + (!TextUtils.isEmpty(mLineNumber) ? mLineNumber : ""));
        Digits.authenticate(authConfigBuilder.build());
    }

    public static void start(Context caller) {
        Intent intent = new Intent(caller, DigitsSignInActivity.class);
        caller.startActivity(intent);
    }

    private void writeNewUser(String userId, String name, String email, String phoneNumber) {
        //Save the registration token in the firebase user table.
        String regId = FirebaseInstanceId.getInstance().getToken();
        User user = new User(userId, name, phoneNumber, email, regId);
        mFirebaseDatabaseManager.getDatabaseReference().child("users").child(userId).setValue(user.toMap());
    }
}

