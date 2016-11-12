package in.mobifirst.tagtree.ftu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import javax.inject.Inject;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.activity.BaseDrawerActivity;
import in.mobifirst.tagtree.application.IQStoreApplication;
import in.mobifirst.tagtree.util.ActivityUtilities;

public class SettingsActivity extends BaseDrawerActivity {

    @Inject
    SettingsPresenter mSettingsPresenter;

    public static void start(Context caller) {
        Intent intent = new Intent(caller, SettingsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        caller.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        SettingsFragment settingsFragment =
                (SettingsFragment) getSupportFragmentManager().findFragmentById(R.id.content_base_drawer);

        if (settingsFragment == null) {
            settingsFragment = SettingsFragment.newInstance();

//            if (getIntent().hasExtra(SettingsFragment.ARGUMENT_EDIT_TASK_ID)) {
//                tokenId = getIntent().getStringExtra(
//                        SettingsFragment.ARGUMENT_EDIT_TASK_ID);
//                actionBar.setTitle(R.string.edit_token);
//            } else {
            actionBar.setTitle(R.string.config_store);
//            }

            ActivityUtilities.addFragmentToActivity(getSupportFragmentManager(),
                    settingsFragment, R.id.content_base_drawer);
        }

        DaggerSettingsComponent.builder()
                .applicationComponent(((IQStoreApplication) getApplication()).getApplicationComponent())
                .settingsPresenterModule(new SettingsPresenterModule(settingsFragment))
                .build()
                .inject(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
