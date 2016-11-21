package in.mobifirst.tagtree.ftu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import javax.inject.Inject;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.activity.BaseActivity;
import in.mobifirst.tagtree.application.IQStoreApplication;
import in.mobifirst.tagtree.authentication.FirebaseAuthenticationManager;
import in.mobifirst.tagtree.database.FirebaseDatabaseManager;
import in.mobifirst.tagtree.model.Store;
import in.mobifirst.tagtree.preferences.IQSharedPreferences;
import in.mobifirst.tagtree.receiver.TTLocalBroadcastManager;
import in.mobifirst.tagtree.tokens.TokensActivity;
import in.mobifirst.tagtree.util.ApplicationConstants;
import in.mobifirst.tagtree.util.NetworkConnectionUtils;
import rx.Subscriber;

public class SettingsFetcherActivity extends BaseActivity {

    @Inject
    FirebaseAuthenticationManager mAuthenticationManager;

    @Inject
    IQSharedPreferences mIQSharedPreferences;

    @Inject
    FirebaseDatabaseManager mFirebaseDatabaseManager;

    @Inject
    protected NetworkConnectionUtils mNetworkConnectionUtils;

    private ProgressBar mProgressBar;

    public static void start(Context caller) {
        Intent intent = new Intent(caller, SettingsFetcherActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        caller.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_fetcher);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);

        ((IQStoreApplication) getApplication()).getApplicationComponent()
                .inject(this);

        if (mNetworkConnectionUtils.isConnected()) {
            fetchStore();
        } else {
            showNetworkError(mProgressBar);
        }
    }

    private BroadcastReceiver mNetworkBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isConnected = intent.getBooleanExtra(TTLocalBroadcastManager.NETWORK_STATUS_KEY, false);
            if (!isConnected) {
                showNetworkError(mProgressBar);
            } else {
                fetchStore();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (!mNetworkConnectionUtils.isConnected()) {
            showNetworkError(mProgressBar);
        }
        TTLocalBroadcastManager.registerReceiver(SettingsFetcherActivity.this, mNetworkBroadcastReceiver, TTLocalBroadcastManager.NETWORK_INTENT_ACTION);
    }

    @Override
    public void onPause() {
        super.onPause();
        TTLocalBroadcastManager.unRegisterReceiver(SettingsFetcherActivity.this, mNetworkBroadcastReceiver);
    }

    private void fetchStore() {
        mFirebaseDatabaseManager.getStoreById(mAuthenticationManager
                        .getAuthInstance().getCurrentUser().getUid(),
                new Subscriber<Store>() {
                    @Override
                    public void onCompleted() {
                        mProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mProgressBar != null) {
                            mProgressBar.setVisibility(View.GONE);

//                            Snackbar.make(mProgressBar, R.string.failed_fetch_store,
//                                    Snackbar.LENGTH_INDEFINITE)
//                                    .setAction(android.R.string.ok, new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View view) {
//                                        }
//                                    }).show();
                        }
                    }

                    @Override
                    public void onNext(Store result) {
                        persistStore(result);
                    }
                });
    }

    private void persistStore(Store store) {
        if (store == null) {
            SettingsActivity.start(SettingsFetcherActivity.this);
        } else {
            store.persistStore(mIQSharedPreferences);
            mIQSharedPreferences.putBoolean(ApplicationConstants.FTU_COMPLETED_KEY, true);
            TokensActivity.start(SettingsFetcherActivity.this);
        }
        finish();
    }
}
