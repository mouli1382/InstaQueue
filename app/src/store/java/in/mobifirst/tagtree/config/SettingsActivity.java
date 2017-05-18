package in.mobifirst.tagtree.config;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.activity.BaseActivity;

public class SettingsActivity extends BaseActivity {

    public static void start(Context caller) {
        Intent intent = new Intent(caller, SettingsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        caller.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle(R.string.config);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        SettingsFragment prefsFragment =
                (SettingsFragment) getFragmentManager().findFragmentById(R.id.contentFrame);

        if (prefsFragment == null) {
            prefsFragment = SettingsFragment.newInstance();

            // Display the fragment as the main content.
            getFragmentManager().beginTransaction()
                    .replace(R.id.contentFrame, prefsFragment)
                    .commit();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
