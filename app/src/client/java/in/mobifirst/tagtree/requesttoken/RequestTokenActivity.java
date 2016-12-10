package in.mobifirst.tagtree.requesttoken;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import javax.inject.Inject;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.activity.BaseActivity;
import in.mobifirst.tagtree.application.IQClientApplication;
import in.mobifirst.tagtree.util.ActivityUtilities;

public class RequestTokenActivity extends BaseActivity {

    public static final int REQUEST_ADD_TOKEN = 1;

    @Inject
    RequestTokenPresenter mRequestTokenPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_token);

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        RequestTokenFragment requestTokenFragment =
                (RequestTokenFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (requestTokenFragment == null) {
            requestTokenFragment = RequestTokenFragment.newInstance();

            actionBar.setTitle(R.string.request_token);

            ActivityUtilities.addFragmentToActivity(getSupportFragmentManager(),
                    requestTokenFragment, R.id.contentFrame);
        }

        DaggerRequestTokenComponent.builder()
                .applicationComponent(((IQClientApplication) getApplication()).getApplicationComponent())
                .addEditTokenPresenterModule(new RequestTokenPresenterModule(requestTokenFragment))
                .build()
                .inject(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
