package in.gm.instaqueue.authentication;

import android.os.Bundle;

import in.gm.instaqueue.activity.BaseActivity;
import in.gm.instaqueue.authentication.digits.DigitsSignInActivity;

public class OnBoardingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadDigitsSignInActivity();
    }

    private void loadDigitsSignInActivity() {
        DigitsSignInActivity.start(OnBoardingActivity.this);
        finish();
    }
}
