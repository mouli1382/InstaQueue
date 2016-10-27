package in.gm.instaqueue.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import in.gm.instaqueue.R;
import in.gm.instaqueue.fragment.LoginFragment;

public class OnBoardingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);
        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_on_boarding, LoginFragment.createInstance(), LoginFragment.LOGIN_FRAGMENT_TAG)
                    .commit();
        }
    }
}
