package in.gm.instaqueue.fragment;


import android.content.Context;
import android.content.Intent;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.AuthConfig;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import java.io.IOException;
import java.util.concurrent.Executor;

import in.gm.instaqueue.BuildConfig;
import in.gm.instaqueue.R;
import in.gm.instaqueue.activity.LandingActivity;
import in.gm.instaqueue.activity.OnBoardingActivity;
import in.gm.instaqueue.backend.myApi.MyApi;
import in.gm.instaqueue.model.User;
import in.gm.instaqueue.prefs.SharedPrefs;
import in.gm.instaqueue.util.AppConstants;
import io.fabric.sdk.android.Fabric;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

public class LoginFragment extends BaseFragment implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final String TAG = "LoginFragment";
    public static final String LOGIN_FRAGMENT_TAG = LoginFragment.class.getName();
    private String mPhoneNumber;
    private String mLineNumber;
    private GoogleApiClient mGoogleApiClient;
    private int RC_SIGN_IN = 1000;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public LoginFragment() {
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        Log.d(LOGIN_FRAGMENT_TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(LOGIN_FRAGMENT_TAG, "signInWithCredential", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Log.w(LOGIN_FRAGMENT_TAG, "Authentication success");
                            Toast.makeText(getActivity(), "Authentication success.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            // ...
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(LOGIN_FRAGMENT_TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (((OnBoardingActivity) getActivity()).checkPermission((OnBoardingActivity) getActivity(), AppConstants.PERMISSION_READ_PHONE_STATE)) {
            TelephonyManager tMgr = (TelephonyManager) _application.getSystemService(Context.TELEPHONY_SERVICE);
            mLineNumber = tMgr.getLine1Number();
        }
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(LOGIN_FRAGMENT_TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(LOGIN_FRAGMENT_TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
                .enableAutoManage(getActivity() /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //TwitterAuthConfig authConfig = new TwitterAuthConfig(BuildConfig.TWITTER_KEY, BuildConfig.TWITTER_SECRET);
        //Fabric.with(_application, new TwitterCore(authConfig), new Digits.Builder().build());

    }

    public static LoginFragment createInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*AuthCallback authCallback = new AuthCallback() {
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
        };*/

       // AuthConfig.Builder authConfigBuilder = new AuthConfig.Builder()
              //  .withAuthCallBack(authCallback)
              //  .withPhoneNumber("+91" + (!TextUtils.isEmpty(mLineNumber) ? mLineNumber : ""));
//        Digits.authenticate(authConfigBuilder.build());


        //return null;

        View v = inflater.inflate(R.layout.fragment_login, container, false);
        SignInButton signInButton = (SignInButton) v.findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);
        return v;

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
        ((OnBoardingActivity) getActivity()).getDatabaseReference().child("users").child(userId).setValue(user);
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
