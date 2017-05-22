package in.mobifirst.tagtree.addedittoken;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import javax.inject.Inject;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.activity.BaseActivity;
import in.mobifirst.tagtree.application.IQStoreApplication;
import in.mobifirst.tagtree.model.Token;
import in.mobifirst.tagtree.util.ActivityUtilities;
import in.mobifirst.tagtree.util.ApplicationConstants;

public class AddEditTokenActivity extends BaseActivity {

    public static final int REQUEST_ADD_TOKEN = 1;

    @Inject
    AddEditTokenPresenter mAddEditTokensPresenter;

    @NonNull
    private Token mToken;

//    public static void start(Context caller, @NonNull String serviceId, @NonNull String tokenId, @NonNull long date) {
//        Intent intent = new Intent(caller, TokensActivity.class);
//        intent.putExtra(ApplicationConstants.SERVICE_UID, serviceId);
//        intent.putExtra(ApplicationConstants.BOOKING_DATE, date);
//        intent.putExtra(ApplicationConstants.TOKEN_UID, tokenId);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        caller.startActivity(intent);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtoken);

        mToken = getIntent().getParcelableExtra(ApplicationConstants.TOKEN_UID);
//        mDateString = TimeUtils.getDate(mDate);

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        AddEditTokenFragment addEditTokenFragment =
                (AddEditTokenFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (addEditTokenFragment == null) {
            addEditTokenFragment = AddEditTokenFragment.newInstance();

            actionBar.setTitle(R.string.issue_token);

            ActivityUtilities.addFragmentToActivity(getSupportFragmentManager(),
                    addEditTokenFragment, R.id.contentFrame);
        }

        DaggerAddEditTokenComponent.builder()
                .applicationComponent(((IQStoreApplication) getApplication()).getApplicationComponent())
                .addEditTokenPresenterModule(new AddEditTokenPresenterModule(addEditTokenFragment, mToken))
                .build()
                .inject(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
