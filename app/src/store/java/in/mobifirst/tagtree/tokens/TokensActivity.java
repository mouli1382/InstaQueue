package in.mobifirst.tagtree.tokens;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.activity.BaseDrawerActivity;
import in.mobifirst.tagtree.activity.CreditsActivity;
import in.mobifirst.tagtree.application.IQStoreApplication;
import in.mobifirst.tagtree.data.token.TokensRepository;
import in.mobifirst.tagtree.display.TokenDisplayService;
import in.mobifirst.tagtree.receiver.TTLocalBroadcastManager;
import in.mobifirst.tagtree.util.ActivityUtilities;
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

    public static void start(Context caller) {
        Intent intent = new Intent(caller, TokensActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        caller.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");

        SwitchCompat flow = (SwitchCompat) findViewById(R.id.switchCompat);
        flow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean issueFlow) {
                if (compoundButton.getId() == R.id.switchCompat) {
                    if (!issueFlow) {
                        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.content_base_drawer);
                        frameLayout.removeAllViewsInLayout();
                        SnapFragment snapFragment = SnapFragment.newInstance();
                        ActivityUtilities.replaceFragmentToActivity(
                                getSupportFragmentManager(), snapFragment, R.id.content_base_drawer);

                        // Create the presenter
                        DaggerTokensComponent.builder()
                                .applicationComponent(((IQStoreApplication) getApplication()).getApplicationComponent())
                                .tokensPresenterModule(new TokensPresenterModule(snapFragment)).build()
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
                                .tokensPresenterModule(new TokensPresenterModule(tokensFragment)).build()
                                .inject(TokensActivity.this);
                    }
                }
            }
        });
        if (!flow.isChecked()) {
            FrameLayout frameLayout = (FrameLayout) findViewById(R.id.content_base_drawer);
            frameLayout.removeAllViewsInLayout();
            SnapFragment snapFragment = SnapFragment.newInstance();
            ActivityUtilities.replaceFragmentToActivity(
                    getSupportFragmentManager(), snapFragment, R.id.content_base_drawer);

            // Create the presenter
            DaggerTokensComponent.builder()
                    .applicationComponent(((IQStoreApplication) getApplication()).getApplicationComponent())
                    .tokensPresenterModule(new TokensPresenterModule(snapFragment)).build()
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
        if(mSubscriptions != null) {
            mSubscriptions.clear();
        }
        stopService(new Intent(this, TokenDisplayService.class));
        super.onDestroy();
    }

    private void loadSnapsForSecondaryScreen() {
        mSubscriptions.clear();
        Subscription subscription = mTokensRepository
                .getSnaps()
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
