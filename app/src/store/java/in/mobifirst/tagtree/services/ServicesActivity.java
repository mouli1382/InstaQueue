package in.mobifirst.tagtree.services;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import javax.inject.Inject;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.activity.BaseActivity;
import in.mobifirst.tagtree.application.IQStoreApplication;
import in.mobifirst.tagtree.preferences.IQSharedPreferences;
import in.mobifirst.tagtree.util.ActivityUtilities;
import in.mobifirst.tagtree.util.ApplicationConstants;

public class ServicesActivity extends BaseActivity {

    private IQSharedPreferences mIQSharedPreferences;

    @Inject
    ServicesPresenter mServicesPresenter;

    public static void start(Context caller, String storeId) {
        Intent intent = new Intent(caller, ServicesActivity.class);
        intent.putExtra(ApplicationConstants.STORE_UID, storeId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        caller.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        String storeId = getIntent().getStringExtra(ApplicationConstants.STORE_UID);
        mIQSharedPreferences = ((IQStoreApplication) getApplicationContext()).getApplicationComponent().getIQSharedPreferences();
        if (mIQSharedPreferences.getBoolean(ApplicationConstants.FTU_COMPLETED_KEY)) {
            actionBar.setTitle(R.string.my_service);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        } else {
            actionBar.hide();
        }

        ServicesFragment servicesFragment =
                (ServicesFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (servicesFragment == null) {
            servicesFragment = ServicesFragment.newInstance();

            ActivityUtilities.addFragmentToActivity(getSupportFragmentManager(),
                    servicesFragment, R.id.contentFrame);
        }

        DaggerServicesComponent.builder()
                .applicationComponent(((IQStoreApplication) getApplication()).getApplicationComponent())
                .servicesPresenterModule(new ServicesPresenterModule(servicesFragment, storeId))
                .build()
                .inject(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
