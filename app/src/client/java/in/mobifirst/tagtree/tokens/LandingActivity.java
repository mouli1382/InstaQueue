package in.mobifirst.tagtree.tokens;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;

import javax.inject.Inject;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.activity.BaseDrawerActivity;
import in.mobifirst.tagtree.application.IQClientApplication;
import in.mobifirst.tagtree.notokens.FirstLaunchDialogFragment;
import in.mobifirst.tagtree.preferences.IQSharedPreferences;
import in.mobifirst.tagtree.util.ActivityUtilities;
import in.mobifirst.tagtree.util.ApplicationConstants;
import in.mobifirst.tagtree.util.NotificationUtil;
import io.fabric.sdk.android.Fabric;

public class LandingActivity extends BaseDrawerActivity implements FirstLaunchDialogFragment.IDialogClosedListener {

    @Inject
    IQSharedPreferences mSharedPrefs;

    public static void start(Context caller) {
        Intent intent = new Intent(caller, LandingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        caller.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((IQClientApplication) getApplication())
                .getApplicationComponent()
                .inject(this);

        Fabric.with(this, new Crashlytics());

        if (!mSharedPrefs.getBoolean(ApplicationConstants.FIRST_LAUNCH_COMPLETED_KEY)) {
            showSampleToken();
        } else {
            showLandingPage();
        }
    }

    private void showLandingPage() {
        LandingFragment landingFragment = LandingFragment.newInstance(getIntent().getBundleExtra(ApplicationConstants.BUNDLE_KEY));
        ActivityUtilities.replaceFragmentToActivity(
                getSupportFragmentManager(), landingFragment, R.id.content_base_drawer);
    }


    private void showSampleToken() {
        FirstLaunchDialogFragment firstLaunchDialogFragment = FirstLaunchDialogFragment.newInstance(new Bundle());
        ActivityUtilities.replaceFragmentToActivity(
                getSupportFragmentManager(), firstLaunchDialogFragment, R.id.content_base_drawer);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Cancel any notification from this app as we are coming to foreground.
        NotificationUtil.clearNotification(LandingActivity.this);
    }

    @Override
    public void onDialogClosed() {
        mSharedPrefs.putBoolean(ApplicationConstants.FIRST_LAUNCH_COMPLETED_KEY, true);
        showLandingPage();
    }
}
