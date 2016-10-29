package in.gm.instaqueue.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import in.gm.instaqueue.R;
import in.gm.instaqueue.fragment.LandingFragment;

public class LandingActivity extends BaseDrawerActivity {


    public static void start(Context caller) {
        Intent intent = new Intent(caller, LandingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        caller.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (savedInstanceState == null) {
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            fragmentManager.beginTransaction()
//                    .replace(R.id.content_base_drawer, LandingFragment.createInstance(), LandingFragment.LANDING_FRAGMENT_TAG)
//                    .commit();
//        }
    }


}
