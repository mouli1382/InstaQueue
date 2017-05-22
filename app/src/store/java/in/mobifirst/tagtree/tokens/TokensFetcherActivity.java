package in.mobifirst.tagtree.tokens;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;

import javax.inject.Inject;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.activity.BaseActivity;
import in.mobifirst.tagtree.application.IQStoreApplication;
import in.mobifirst.tagtree.authentication.FirebaseAuthenticationManager;
import in.mobifirst.tagtree.database.FirebaseDatabaseManager;
import in.mobifirst.tagtree.model.Service;
import in.mobifirst.tagtree.receiver.TTLocalBroadcastManager;
import in.mobifirst.tagtree.util.ApplicationConstants;
import in.mobifirst.tagtree.util.NetworkConnectionUtils;
import in.mobifirst.tagtree.util.TimeUtils;

public class TokensFetcherActivity extends BaseActivity {

    @Inject
    FirebaseAuthenticationManager mAuthenticationManager;

    @Inject
    FirebaseDatabaseManager mFirebaseDatabaseManager;

    @Inject
    protected NetworkConnectionUtils mNetworkConnectionUtils;

    private ProgressBar mProgressBar;
    private TextView mTextView;

    @NonNull
    private Service mService;

    @NonNull
    private long mDate;

    public static void start(Context caller, @NonNull Service service, @NonNull long date) {
        Intent intent = new Intent(caller, TokensFetcherActivity.class);
        intent.putExtra(ApplicationConstants.SERVICE_UID, service);
        intent.putExtra(ApplicationConstants.BOOKING_DATE, date);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        caller.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetcher);
        mService = getIntent().getParcelableExtra(ApplicationConstants.SERVICE_UID);
        mDate = getIntent().getLongExtra(ApplicationConstants.BOOKING_DATE, Calendar.getInstance().getTimeInMillis());

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);

        mTextView = (TextView) findViewById(R.id.progress_text);
        mTextView.setText(R.string.fetching_appointment_details);
        mTextView.setVisibility(View.VISIBLE);

        ((IQStoreApplication) getApplication()).getApplicationComponent()
                .inject(this);

        if (mNetworkConnectionUtils.isConnected()) {
            checkAndGenerateAppointmentSlots();
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
                checkAndGenerateAppointmentSlots();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (!mNetworkConnectionUtils.isConnected()) {
            showNetworkError(mProgressBar);
        }
        TTLocalBroadcastManager.registerReceiver(TokensFetcherActivity.this, mNetworkBroadcastReceiver, TTLocalBroadcastManager.NETWORK_INTENT_ACTION);
    }

    @Override
    public void onPause() {
        super.onPause();
        TTLocalBroadcastManager.unRegisterReceiver(TokensFetcherActivity.this, mNetworkBroadcastReceiver);
    }

    private void checkAndGenerateAppointmentSlots() {
        String dateString = TimeUtils.getDate(mDate);
        if (!TextUtils.isEmpty(dateString) && mFirebaseDatabaseManager.isTodayAWorkingDay(dateString, mService.getDaysOfOperation()) != -1) {
            mFirebaseDatabaseManager.checkAndGenerateAppointments(mAuthenticationManager.getAuthInstance().getCurrentUser().getUid(),
                    mService.getId(),
                    TimeUtils.getDate(mDate)
            ).addOnCompleteListener(new OnCompleteListener<Boolean>() {
                @Override
                public void onComplete(@NonNull Task<Boolean> task) {
                    nextScreen(true);
                }
            });
        } else {
            nextScreen(false);
        }
    }

    private void nextScreen(boolean isOpenToday) {
        TokensActivity.start(TokensFetcherActivity.this, mService, mDate, isOpenToday);
        finish();
    }
}
