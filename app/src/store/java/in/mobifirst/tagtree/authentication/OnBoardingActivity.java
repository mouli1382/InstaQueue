package in.mobifirst.tagtree.authentication;

import android.os.Bundle;
import android.view.View;

import com.google.android.gms.common.SignInButton;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.activity.BaseActivity;
import in.mobifirst.tagtree.authentication.google.GoogleSignInActivity;

public class OnBoardingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadGoogleSignInActivity();
            }
        });
    }

    private void loadGoogleSignInActivity() {
        GoogleSignInActivity.start(OnBoardingActivity.this);
        finish();
    }
}
