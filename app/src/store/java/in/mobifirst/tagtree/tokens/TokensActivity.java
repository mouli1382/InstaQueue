package in.mobifirst.tagtree.tokens;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.activity.BaseDrawerActivity;
import in.mobifirst.tagtree.activity.CreditsActivity;
import in.mobifirst.tagtree.application.IQStoreApplication;
import in.mobifirst.tagtree.data.token.TokensRepository;
import in.mobifirst.tagtree.display.TokenDisplayService;
import in.mobifirst.tagtree.model.Service;
import in.mobifirst.tagtree.receiver.TTLocalBroadcastManager;
import in.mobifirst.tagtree.util.ActivityUtilities;
import in.mobifirst.tagtree.util.ApplicationConstants;
import in.mobifirst.tagtree.util.TimeUtils;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class TokensActivity extends BaseDrawerActivity {

    private static final String CURRENT_FILTERING_KEY = "CURRENT_FILTERING_KEY";

    @Inject
    TokensPresenter mTokensPresenter;

    private TokensRepository mTokensRepository;
    private CompositeSubscription mSubscriptions;

    private Button mDateButton;
    private String mDateString;
    private long mDate;

    @NonNull
    private Service mService;

    public static void start(Context caller, Service service) {
        Intent intent = new Intent(caller, TokensActivity.class);
        intent.putExtra(ApplicationConstants.SERVICE_UID, service);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        caller.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mService = getIntent().getParcelableExtra(ApplicationConstants.SERVICE_UID);
        mDate = Calendar.getInstance().getTimeInMillis();
        mDateString = TimeUtils.getDate(mDate);

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDateButton = (Button) findViewById(R.id.dateTextView);
        mDateButton.setText(mDateString);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });

        SwitchCompat flow = (SwitchCompat) findViewById(R.id.switchCompat);
        flow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean issueFlow) {
                if (compoundButton.getId() == R.id.switchCompat) {
                    if (!issueFlow) {
                        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.content_base_drawer);
                        frameLayout.removeAllViewsInLayout();
                        SnapFragment snapFragment = SnapFragment.newInstance();
                        snapFragment.setArguments(getServiceBundle());
                        ActivityUtilities.replaceFragmentToActivity(
                                getSupportFragmentManager(), snapFragment, R.id.content_base_drawer);

                        // Create the presenter
                        DaggerTokensComponent.builder()
                                .applicationComponent(((IQStoreApplication) getApplication()).getApplicationComponent())
                                .tokensPresenterModule(new TokensPresenterModule(snapFragment, mService, mDate)).build()
                                .inject(TokensActivity.this);
                    } else {
                        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.content_base_drawer);
                        frameLayout.removeAllViewsInLayout();
                        TokensFragment tokensFragment = TokensFragment.newInstance();
                        ActivityUtilities.replaceFragmentToActivity(
                                getSupportFragmentManager(), tokensFragment, R.id.content_base_drawer);

                        // Create the presenter
                        DaggerTokensComponent.builder()
                                .applicationComponent(((IQStoreApplication) getApplication()).getApplicationComponent())
                                .tokensPresenterModule(new TokensPresenterModule(tokensFragment, mService, mDate)).build()
                                .inject(TokensActivity.this);
                    }
                }
            }
        });
        if (!flow.isChecked()) {
            FrameLayout frameLayout = (FrameLayout) findViewById(R.id.content_base_drawer);
            frameLayout.removeAllViewsInLayout();
            SnapFragment snapFragment = SnapFragment.newInstance();
            snapFragment.setArguments(getServiceBundle());
            ActivityUtilities.replaceFragmentToActivity(
                    getSupportFragmentManager(), snapFragment, R.id.content_base_drawer);

            // Create the presenter
            DaggerTokensComponent.builder()
                    .applicationComponent(((IQStoreApplication) getApplication()).getApplicationComponent())
                    .tokensPresenterModule(new TokensPresenterModule(snapFragment, mService, mDate)).build()
                    .inject(TokensActivity.this);
        }

        // Load previously saved state, if available.
        if (savedInstanceState != null) {
            TokensFilterType currentFiltering =
                    (TokensFilterType) savedInstanceState.getSerializable(CURRENT_FILTERING_KEY);
            mTokensPresenter.setFiltering(currentFiltering);
        }

        mSubscriptions = new CompositeSubscription();
        mTokensRepository = ((IQStoreApplication) getApplication()).getApplicationComponent().getTokensRepository();

        startService(new Intent(this, TokenDisplayService.class));
    }

    private Bundle getServiceBundle() {
        Bundle b = new Bundle();
        b.putParcelable(ApplicationConstants.SERVICE_UID, mService);

        return b;
    }

    public void showDatePickerDialog(final View v) {
        DatePickerDialogFragment datePickerDialogFragment = new DatePickerDialogFragment();
        datePickerDialogFragment.setiDatePickerCallback(new DatePickerDialogFragment.IDatePickerCallback() {
            @Override
            public void onDatePicked(int year, int month, int day) {
                final Calendar c = Calendar.getInstance();
                c.set(year, month, day);
                mDate = c.getTimeInMillis();
                mDateString = TimeUtils.getDate(mDate);
                mDateButton.setText(mDateString);

                FrameLayout frameLayout = (FrameLayout) findViewById(R.id.content_base_drawer);
                frameLayout.removeAllViewsInLayout();
                SnapFragment snapFragment = SnapFragment.newInstance();
                snapFragment.setArguments(getServiceBundle());
                ActivityUtilities.replaceFragmentToActivity(
                        getSupportFragmentManager(), snapFragment, R.id.content_base_drawer);

                // Create the presenter
                DaggerTokensComponent.builder()
                        .applicationComponent(((IQStoreApplication) getApplication()).getApplicationComponent())
                        .tokensPresenterModule(new TokensPresenterModule(snapFragment, mService, mDate)).build()
                        .inject(TokensActivity.this);
            }
        });
        datePickerDialogFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public static class DatePickerDialogFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        private IDatePickerCallback iDatePickerCallback;

        interface IDatePickerCallback {
            void onDatePicked(int year, int month, int day);
        }

        public void setiDatePickerCallback(IDatePickerCallback iDatePickerCallback) {
            this.iDatePickerCallback = iDatePickerCallback;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            iDatePickerCallback.onDatePicked(year, month, day);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {
            Intent intent = new Intent(getApplicationContext(), CreditsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        return super.onNavigationItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mTokensPresenter != null)
            outState.putSerializable(CURRENT_FILTERING_KEY, mTokensPresenter.getFiltering());

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadSnapsForSecondaryScreen();
    }

    @Override
    protected void onDestroy() {
        if (mSubscriptions != null) {
            mSubscriptions.clear();
        }
        stopService(new Intent(this, TokenDisplayService.class));
        super.onDestroy();
    }

    private void loadSnapsForSecondaryScreen() {
        mSubscriptions.clear();
        //Always show the today's tokens on the secondary display.
        Subscription subscription = mTokensRepository
                .getSnaps(mService.getId(), Calendar.getInstance().getTimeInMillis(), true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Snap>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(List<Snap> snaps) {
                        processSnaps(snaps);
                    }
                });
        mSubscriptions.add(subscription);
    }

    private void processSnaps(List<Snap> snaps) {
        if (snaps == null || snaps.size() == 0) {
            //Send broadcast to TokenDisplayService here that there are no tokens.
            Intent intent = new Intent(TTLocalBroadcastManager.TOKEN_CHANGE_INTENT_ACTION);
            intent.putParcelableArrayListExtra(TokenDisplayService.SNAP_LIST_INTENT_KEY,
                    new ArrayList<Snap>());
            LocalBroadcastManager.getInstance(TokensActivity.this).sendBroadcast(intent);
        } else {
            //Send broadcast to TokenDisplayService here.
            Intent intent = new Intent(TTLocalBroadcastManager.TOKEN_CHANGE_INTENT_ACTION);
            intent.putParcelableArrayListExtra(TokenDisplayService.SNAP_LIST_INTENT_KEY,
                    (ArrayList<? extends Parcelable>) snaps);
            LocalBroadcastManager.getInstance(TokensActivity.this).sendBroadcast(intent);
        }
    }
}
