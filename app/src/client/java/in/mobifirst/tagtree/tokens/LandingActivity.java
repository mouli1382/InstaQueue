package in.mobifirst.tagtree.tokens;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.activity.BaseDrawerActivity;
import in.mobifirst.tagtree.util.ActivityUtilities;
import in.mobifirst.tagtree.util.ApplicationConstants;
import in.mobifirst.tagtree.util.NotificationUtil;
import io.fabric.sdk.android.Fabric;

public class LandingActivity extends BaseDrawerActivity {

    public static void start(Context caller) {
        Intent intent = new Intent(caller, LandingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        caller.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        LandingFragment landingFragment =
                (LandingFragment) getSupportFragmentManager().findFragmentById(R.id.content_base_drawer);
        if (landingFragment == null) {
            landingFragment = LandingFragment.newInstance(getIntent().getBundleExtra(ApplicationConstants.BUNDLE_KEY));
            ActivityUtilities.addFragmentToActivity(
                    getSupportFragmentManager(), landingFragment, R.id.content_base_drawer);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Cancel any notification from this app as we are coming to foreground.
        NotificationUtil.clearNotification(LandingActivity.this);
    }
}
