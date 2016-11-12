package in.mobifirst.tagtree.authentication;

import android.os.Bundle;

import in.mobifirst.tagtree.activity.BaseActivity;
import in.mobifirst.tagtree.authentication.digits.DigitsSignInActivity;

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
