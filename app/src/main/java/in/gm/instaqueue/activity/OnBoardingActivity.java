package in.gm.instaqueue.activity;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;

import in.gm.instaqueue.R;
import in.gm.instaqueue.fragment.LoginFragment;

public class OnBoardingActivity extends BaseDrawerActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_base_drawer, LoginFragment.createInstance(), LoginFragment.LOGIN_FRAGMENT_TAG)
                    .commit();
        }
    }
}
