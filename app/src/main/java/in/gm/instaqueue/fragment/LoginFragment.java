package in.gm.instaqueue.fragment;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.AuthConfig;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import java.io.IOException;

import in.gm.instaqueue.BuildConfig;
import in.gm.instaqueue.activity.LandingActivity;
import in.gm.instaqueue.activity.OnBoardingActivity;
import in.gm.instaqueue.backend.myApi.MyApi;
import in.gm.instaqueue.model.User;
import in.gm.instaqueue.prefs.SharedPrefs;
import in.gm.instaqueue.util.AppConstants;
import io.fabric.sdk.android.Fabric;

public class LoginFragment extends BaseFragment {

    private static final String TAG = "LoginFragment";
    public static final String LOGIN_FRAGMENT_TAG = LoginFragment.class.getName();
    private String mPhoneNumber;
    private String mLineNumber;

    public LoginFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (((OnBoardingActivity) getActivity()).checkPermission((OnBoardingActivity) getActivity(), AppConstants.PERMISSION_READ_PHONE_STATE)) {
            TelephonyManager tMgr = (TelephonyManager) _application.getSystemService(Context.TELEPHONY_SERVICE);
            mLineNumber = tMgr.getLine1Number();
        }

        TwitterAuthConfig authConfig = new TwitterAuthConfig(BuildConfig.TWITTER_KEY, BuildConfig.TWITTER_SECRET);
        Fabric.with(_application, new TwitterCore(authConfig), new Digits.Builder().build());

    }

    public static LoginFragment createInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AuthCallback authCallback = new AuthCallback() {
            @Override
            public void success(DigitsSession session, String phoneNumber) {
                Toast.makeText(getActivity(), "Authentication successful for "
                        + phoneNumber, Toast.LENGTH_LONG).show();

                mPhoneNumber = session.getPhoneNumber();

                //ToDo remove these.
                Log.i(TAG, "phone number = " + mPhoneNumber);
                Log.i(TAG, "email = " + session.getEmail().toString());
                Log.i(TAG, "token = " + session.getAuthToken().token + " secret = " + session.getAuthToken().secret);

                new EndpointsAsyncTask().execute(new Pair<Context, DigitsSession>(getActivity(), session));
            }

            @Override
            public void failure(DigitsException exception) {
                Log.d("Digits", "Sign in with Digits failure", exception);
            }
        };

        AuthConfig.Builder authConfigBuilder = new AuthConfig.Builder()
                .withAuthCallBack(authCallback)
                .withPhoneNumber("+91" + (!TextUtils.isEmpty(mLineNumber) ? mLineNumber : ""));
        Digits.authenticate(authConfigBuilder.build());

        return null;
    }

    private void startSignIn(String customeToken) {
        // Initiate sign in with custom token
        final FirebaseAuth firebaseAuth = mFirebaseManager.getAuthInstance();
        if (firebaseAuth != null) {
            firebaseAuth.signInWithCustomToken(customeToken)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithCustomToken:onComplete:" + task.isSuccessful());
                            if (task.isSuccessful()) {
                                FirebaseUser user = task.getResult().getUser();
                                if (user != null) {
                                    mSharedPrefs.putString(SharedPrefs.PHONE_NUMBER_KEY, mPhoneNumber);
                                    writeNewUser(user.getUid(), user.getDisplayName(), user.getEmail(), mPhoneNumber);

                                    getActivity().finish();
                                    //Launch the landing screen.
                                    LandingActivity.start(getActivity());
                                }
                            }

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "signInWithCustomToken", task.getException());
                                Toast.makeText(getActivity(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void writeNewUser(String userId, String name, String email, String phoneNumber) {
        User user = new User(name, email, phoneNumber);
        getDatabaseReference().child("users").child(userId).setValue(user);
    }

    class EndpointsAsyncTask extends AsyncTask<Pair<Context, DigitsSession>, Void, String> {
        private MyApi myApiService = null;

        @Override
        protected String doInBackground(Pair<Context, DigitsSession>... params) {
            if (myApiService == null) {  // Only do this once
                MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
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

                myApiService = builder.build();
            }
            DigitsSession digitsSession = params[0].second;

            try {
                return myApiService.createCustomToken(digitsSession.getAuthToken().token, digitsSession.getPhoneNumber()).execute().getData();
            } catch (IOException e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i(EndpointsAsyncTask.class.getSimpleName(), "Firebase JWT = " + result);
            startSignIn(result);
        }
    }

}
