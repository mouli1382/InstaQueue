package in.mobifirst.tagtree.addeditservice;

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

public class AddEditServiceActivity extends BaseActivity {

    public static final int REQUEST_ADD_SERVICE = 1;

    @Inject
    AddEditServicePresenter mSettingsPresenter;

    IQSharedPreferences mIQSharedPreferences;

    public static void start(Context caller, String storeId, String serviceId) {
        Intent intent = new Intent(caller, AddEditServiceActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(ApplicationConstants.STORE_UID, storeId);
        intent.putExtra(ApplicationConstants.SERVICE_UID, serviceId);
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

        //ToDo inject it - avoid cyclic dependency.
        mIQSharedPreferences = ((IQStoreApplication) getApplicationContext()).getApplicationComponent().getIQSharedPreferences();
        if (mIQSharedPreferences.getBoolean(ApplicationConstants.FTU_COMPLETED_KEY)) {
            actionBar.setTitle(R.string.my_service);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        } else {
            actionBar.hide();
        }

        AddEditServiceFragment addEditServiceFragment =
                (AddEditServiceFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        String serviceId = getIntent().getStringExtra(ApplicationConstants.SERVICE_UID);
        if (addEditServiceFragment == null) {
            addEditServiceFragment = AddEditServiceFragment.newInstance();

            Bundle bundle = new Bundle();
            bundle.putString(ApplicationConstants.STORE_UID, getIntent().getStringExtra(ApplicationConstants.STORE_UID));
            addEditServiceFragment.setArguments(bundle);

            ActivityUtilities.addFragmentToActivity(getSupportFragmentManager(),
                    addEditServiceFragment, R.id.contentFrame);
        }

        DaggerAddEditServiceComponent.builder()
                .applicationComponent(((IQStoreApplication) getApplication()).getApplicationComponent())
                .addEditServicePresenterModule(new AddEditServicePresenterModule(addEditServiceFragment, serviceId))
                .build()
                .inject(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
