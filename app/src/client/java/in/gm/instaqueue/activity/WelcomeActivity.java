package in.gm.instaqueue.activity;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import javax.inject.Inject;
import javax.inject.Named;

import in.gm.instaqueue.R;
import in.gm.instaqueue.application.IQClientApplication;
import in.gm.instaqueue.authentication.FirebaseAuthenticationManager;
import in.gm.instaqueue.tokens.TokensActivity;

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
        ((IQClientApplication)getApplication())
                .getApplicationComponent()
                .inject(this);
        setContentView(R.layout.activity_welcome);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        startAnimation();
    }

    private void startAnimation() {
        AnimatorSet logoAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.entry_screen_animator);
        logoAnimator.setTarget(findViewById(R.id.welcome_text));
        logoAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                bootUp();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        logoAnimator.start();
    }

    private void bootUp() {
        if (mFirebaseAuth.getAuthInstance().getCurrentUser() != null) {
            Intent intent = new Intent(this, TokensActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(this, RequestPermissionsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
            startActivity(intent);
            finish();
        }
    }
}
