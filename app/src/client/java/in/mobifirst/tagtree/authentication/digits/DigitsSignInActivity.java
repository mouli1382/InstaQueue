package in.mobifirst.tagtree.authentication.digits;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.AuthConfig;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import javax.inject.Inject;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.activity.BaseActivity;
import in.mobifirst.tagtree.activity.RequestPermissionsActivity;
import in.mobifirst.tagtree.application.IQClientApplication;
import in.mobifirst.tagtree.authentication.FirebaseAuthenticationManager;
import in.mobifirst.tagtree.database.FirebaseDatabaseManager;
import in.mobifirst.tagtree.tokens.TokensActivity;
import in.mobifirst.tagtree.util.ApplicationConstants;
import in.mobifirst.tagtree.preferences.IQSharedPreferences;

public class DigitsSignInActivity extends BaseActivity {

    private static final String TAG = "DigitsSignInActivity";
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

        ((IQClientApplication)getApplication())
                .getApplicationComponent()
                .inject(this);


        if (checkPermission(this, ApplicationConstants.PERMISSION_READ_PHONE_STATE)) {
            TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            mLineNumber = tMgr.getLine1Number();
        }

        AuthCallback authCallback = new AuthCallback() {
            @Override
            public void success(DigitsSession session, String phoneNumber) {
                Toast.makeText(DigitsSignInActivity.this, "Authentication successful for "
                        + phoneNumber, Toast.LENGTH_LONG).show();

                mPhoneNumber = session.getPhoneNumber();

                //ToDo remove these.
                Log.i(TAG, "phone number = " + mPhoneNumber);
                Log.i(TAG, "email = " + session.getEmail().toString());
                Log.i(TAG, "token = " + session.getAuthToken().token + " secret = " + session.getAuthToken().secret);

                //startSignIn(session.getAuthToken().token);
                FirebaseUser user = mAuthenticationManager.getAuthInstance().getCurrentUser();
                if (user != null) {
                    mSharedPrefs.putString(ApplicationConstants.PHONE_NUMBER_KEY, mPhoneNumber);
                    writeNewUser(user.getUid(), user.getDisplayName(), user.getEmail(), mPhoneNumber);

                    //Launch the landing screen.
                    TokensActivity.start(DigitsSignInActivity.this);
                }
                finish();
            }

            @Override
            public void failure(DigitsException exception) {
                Log.d("DigitsAuth", "Sign in with DigitsAuth failure", exception);
                Snackbar.make(findViewById(R.id.auth_button).getRootView(), "Digits authentication faied", Snackbar.LENGTH_LONG).show();
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

    private void startSignIn(String customeToken) {
        // Initiate sign in with custom token
        final FirebaseAuth firebaseAuth = mAuthenticationManager.getAuthInstance();
        if (firebaseAuth != null) {
            firebaseAuth.signInWithCustomToken(customeToken)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithCustomToken:onComplete:" + task.isSuccessful());
                            if (task.isSuccessful()) {
                                FirebaseUser user = task.getResult().getUser();
                                if (user != null) {
                                    mSharedPrefs.putString(ApplicationConstants.PHONE_NUMBER_KEY, mPhoneNumber);
                                    writeNewUser(user.getUid(), user.getDisplayName(), user.getEmail(), mPhoneNumber);

                                    finish();
                                    //Launch the landing screen.
                                    TokensActivity.start(DigitsSignInActivity.this);
                                }
                            }

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "signInWithCustomToken", task.getException());
                                Toast.makeText(DigitsSignInActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void writeNewUser(String userId, String name, String email, String phoneNumber) {
        //Save the registration token in the firebase user table.
        String regId = FirebaseInstanceId.getInstance().getToken();
//        User user = new User(userId, name, phoneNumber, email, regId);
        mFirebaseDatabaseManager.getDatabaseReference().child("users").child(phoneNumber).setValue(regId);
    }
}

