package in.mobifirst.tagtree.addeditservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.activity.BaseActivity;
import in.mobifirst.tagtree.application.IQStoreApplication;
import in.mobifirst.tagtree.authentication.FirebaseAuthenticationManager;
import in.mobifirst.tagtree.database.FirebaseDatabaseManager;
import in.mobifirst.tagtree.model.Service;
import in.mobifirst.tagtree.preferences.IQSharedPreferences;
import in.mobifirst.tagtree.receiver.TTLocalBroadcastManager;
import in.mobifirst.tagtree.services.ServicesActivity;
import in.mobifirst.tagtree.util.ApplicationConstants;
import in.mobifirst.tagtree.util.NetworkConnectionUtils;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ServiceDetailsFetcherActivity extends BaseActivity {

    @Inject
    FirebaseAuthenticationManager mAuthenticationManager;

    @Inject
    IQSharedPreferences mIQSharedPreferences;

    @Inject
    FirebaseDatabaseManager mFirebaseDatabaseManager;

    @Inject
    protected NetworkConnectionUtils mNetworkConnectionUtils;

    private ProgressBar mProgressBar;
    private TextView mTextView;

    public static void start(Context caller) {
        Intent intent = new Intent(caller, ServiceDetailsFetcherActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        caller.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetcher);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);

        mTextView = (TextView) findViewById(R.id.progress_text);
        mTextView.setText(R.string.fetching_service_details);
        mTextView.setVisibility(View.VISIBLE);

        ((IQStoreApplication) getApplication()).getApplicationComponent()
                .inject(this);

        if (mNetworkConnectionUtils.isConnected()) {
            fetchServices();
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
                fetchServices();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (!mNetworkConnectionUtils.isConnected()) {
            showNetworkError(mProgressBar);
        }
        TTLocalBroadcastManager.registerReceiver(ServiceDetailsFetcherActivity.this, mNetworkBroadcastReceiver, TTLocalBroadcastManager.NETWORK_INTENT_ACTION);
    }

    @Override
    public void onPause() {
        super.onPause();
        TTLocalBroadcastManager.unRegisterReceiver(ServiceDetailsFetcherActivity.this, mNetworkBroadcastReceiver);
    }

    private void fetchServices() {
        mFirebaseDatabaseManager.getAllServices(mAuthenticationManager
                .getAuthInstance().getCurrentUser().getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Service>>() {
                    @Override
                    public void onCompleted() {
                        mProgressBar.setVisibility(View.GONE);
                        mTextView.setVisibility(View.GONE);
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
                        if (mTextView != null) {
                            mTextView.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onNext(List<Service> services) {
                        nextScreen(services);
                    }
                });
    }

    private void nextScreen(List<Service> services) {
//        if (services == null) {
//            AddEditServiceActivity.start(ServiceDetailsFetcherActivity.this, mAuthenticationManager.getAuthInstance().getCurrentUser().getUid());
//        } else {
            mIQSharedPreferences.putBoolean(ApplicationConstants.FTU_COMPLETED_KEY, true);
            mIQSharedPreferences.putString(ApplicationConstants.STORE_UID, mAuthenticationManager.getAuthInstance().getCurrentUser().getUid());
            ServicesActivity.start(ServiceDetailsFetcherActivity.this, mAuthenticationManager.getAuthInstance().getCurrentUser().getUid());
//        }
        finish();
    }
}
