package in.mobifirst.tagtree.tokens;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.MediaRouteButton;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.cast.CastRemoteDisplayLocalService;
import com.google.android.gms.common.api.Status;

import javax.inject.Inject;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.activity.BaseDrawerActivity;
import in.mobifirst.tagtree.activity.CreditsActivity;
import in.mobifirst.tagtree.application.IQStoreApplication;
import in.mobifirst.tagtree.display.MediaRouterButtonView;
import in.mobifirst.tagtree.display.PresentationService;
import in.mobifirst.tagtree.preferences.IQSharedPreferences;
import in.mobifirst.tagtree.util.ActivityUtilities;
import in.mobifirst.tagtree.util.ApplicationConstants;

public class TokensActivity extends BaseDrawerActivity {
    private static final String TAG = "TokensActivity";
    private static final String CURRENT_FILTERING_KEY = "CURRENT_FILTERING_KEY";

    @Inject
    TokensPresenter mTokensPresenter;

    private IQSharedPreferences mIQSharedPreferences;

    private MediaRouteButton mMediaRouteButton;
    private MediaRouterButtonView mMediaRouterButtonView;
    private int mRouteCount = 0;
    private MediaRouter mMediaRouter;
    private MediaRouteSelector mMediaRouteSelector;
    private CastDevice mCastDevice;

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

        mIQSharedPreferences = ((IQStoreApplication) getApplication()).getApplicationComponent().getIQSharedPreferences();

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

        mMediaRouter = android.support.v7.media.MediaRouter.getInstance(getApplicationContext());
        mMediaRouteSelector = new MediaRouteSelector.Builder()
                .addControlCategory(
                        CastMediaControlIntent.categoryForCast(getString(R.string.app_id)))
                .build();

        mMediaRouterButtonView = (MediaRouterButtonView) findViewById(R.id.media_route_button_view);
        if (mMediaRouterButtonView != null) {
            mMediaRouteButton = mMediaRouterButtonView.getMediaRouteButton();
            mMediaRouteButton.setRouteSelector(mMediaRouteSelector);
        }
        mMediaRouteButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mMediaRouteButton.performClick();
                return false;
            }
        });

        if (isRemoteDisplaying()) {
            // The Activity has been recreated and we have an active remote display session,
            // so we need to set the selected device instance
            CastDevice castDevice = CastDevice
                    .getFromBundle(mMediaRouter.getSelectedRoute().getExtras());
            mCastDevice = castDevice;

            if (mMediaRouterButtonView != null) {
                mMediaRouterButtonView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMediaRouter.addCallback(mMediaRouteSelector, mMediaRouterCallback,
                MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMediaRouter.removeCallback(mMediaRouterCallback);
    }

    private boolean isRemoteDisplaying() {
        return CastRemoteDisplayLocalService.getInstance() != null;
    }

    private void initError() {
        Toast toast = Toast.makeText(
                getApplicationContext(), R.string.init_error, Toast.LENGTH_SHORT);
        mMediaRouter.selectRoute(mMediaRouter.getDefaultRoute());
        toast.show();
    }

    private final MediaRouter.Callback mMediaRouterCallback =
            new MediaRouter.Callback() {
                @Override
                public void onRouteAdded(MediaRouter router, MediaRouter.RouteInfo route) {
                    if (++mRouteCount == 1) {
                        // Show the button when a device is discovered.
                        if (mMediaRouterButtonView != null) {
                            mMediaRouterButtonView.setVisibility(View.VISIBLE);
                            introduceCast(mMediaRouteButton);
                        }
                    }
                }

                @Override
                public void onRouteRemoved(MediaRouter router, MediaRouter.RouteInfo route) {
                    if (--mRouteCount == 0) {
                        // Hide the button if there are no devices discovered.
                        if (mMediaRouterButtonView != null) {
                            mMediaRouterButtonView.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onRouteSelected(MediaRouter router, MediaRouter.RouteInfo info) {
                    // Should not happen since this activity will be closed if there
                    // is no selected route

                    CastDevice castDevice = CastDevice.getFromBundle(info.getExtras());
                    mCastDevice = castDevice;

                    startCastService(mCastDevice);
                }

                @Override
                public void onRouteUnselected(MediaRouter router, MediaRouter.RouteInfo info) {
                    if (isRemoteDisplaying()) {
                        CastRemoteDisplayLocalService.stopService();
                    }
                    mCastDevice = null;
                }
            };

    private void startCastService(CastDevice castDevice) {
        Intent intent = new Intent(TokensActivity.this,
                TokensActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(
                TokensActivity.this, 0, intent, 0);

        CastRemoteDisplayLocalService.NotificationSettings settings =
                new CastRemoteDisplayLocalService.NotificationSettings.Builder()
                        .setNotificationPendingIntent(notificationPendingIntent).build();

        CastRemoteDisplayLocalService.startService(TokensActivity.this,
                PresentationService.class, getString(R.string.app_id),
                castDevice, settings,
                new CastRemoteDisplayLocalService.Callbacks() {
                    @Override
                    public void onServiceCreated(
                            CastRemoteDisplayLocalService service) {
                        Log.e(TAG, "onServiceCreated");
                    }

                    @Override
                    public void onRemoteDisplaySessionStarted(
                            CastRemoteDisplayLocalService service) {
                        Log.e(TAG, "onServiceStarted");
                    }

                    @Override
                    public void onRemoteDisplaySessionError(Status errorReason) {
                        Log.e(TAG, "onServiceError: " + errorReason.getStatusCode());
                        initError();

                        mCastDevice = null;
//                        TokensActivity.this.finish();
                    }
                });
    }

    private void introduceCast(View view) {
        if (!mIQSharedPreferences.getBoolean(ApplicationConstants.INTRODUCE_CAST)) {
            new ShowcaseView.Builder(this)
                    .withMaterialShowcase()
                    .setStyle(R.style.CustomShowcaseTheme2)
                    .setTarget(new ViewTarget(view))
                    .setContentTitle("TagTree Cast")
                    .setContentText("Tap to cast Queue status to TV")
                    .hideOnTouchOutside()
                    .build();
            mIQSharedPreferences.putBoolean(ApplicationConstants.INTRODUCE_CAST, true);
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
}
