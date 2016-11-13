package in.mobifirst.tagtree.activity;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.google.android.gms.common.SignInButton;

import javax.inject.Inject;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.application.IQStoreApplication;
import in.mobifirst.tagtree.authentication.FirebaseAuthenticationManager;
import in.mobifirst.tagtree.authentication.OnBoardingActivity;
import in.mobifirst.tagtree.authentication.google.GoogleSignInActivity;
import in.mobifirst.tagtree.tokens.TokensActivity;

/**
 * ToDo Show Welcome screen explaining the app functionality in a paginated view.
 * But for now just animating the app name.*
 * Initializes Firebase while showing the splash animation.
 */
public class WelcomeActivity extends BaseActivity {

    private static final String TAG = "WelcomeActivity";

    @Inject
    protected FirebaseAuthenticationManager mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((IQStoreApplication)getApplication())
                .getApplicationComponent()
                .inject(this);
        setContentView(R.layout.activity_welcome);
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
    }

    private void loadGoogleSignInActivity() {
        GoogleSignInActivity.start(WelcomeActivity.this);
        finish();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        bootUp();
    }

    private void bootUp() {
        if (mFirebaseAuth.getAuthInstance().getCurrentUser() != null) {
            Intent intent = new Intent(this, TokensActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
            startActivity(intent);
            finish();
        }
        else {
            SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadGoogleSignInActivity();
                }
            });
        }
    }
}
