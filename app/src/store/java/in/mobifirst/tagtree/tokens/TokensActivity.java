package in.mobifirst.tagtree.tokens;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CompoundButton;

import javax.inject.Inject;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.activity.BaseDrawerActivity;
import in.mobifirst.tagtree.activity.CreditsActivity;
import in.mobifirst.tagtree.application.IQStoreApplication;
import in.mobifirst.tagtree.util.ActivityUtilities;

public class TokensActivity extends BaseDrawerActivity {

    private static final String CURRENT_FILTERING_KEY = "CURRENT_FILTERING_KEY";

    @Inject
    TokensPresenter mTokensPresenter;

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
                        SnapFragment snapFragment = SnapFragment.newInstance();
                        ActivityUtilities.replaceFragmentToActivity(
                                getSupportFragmentManager(), snapFragment, R.id.content_base_drawer);

                        // Create the presenter
                        DaggerTokensComponent.builder()
                                .applicationComponent(((IQStoreApplication) getApplication()).getApplicationComponent())
                                .tokensPresenterModule(new TokensPresenterModule(snapFragment)).build()
                                .inject(TokensActivity.this);

                    } else {
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
        if(!flow.isChecked()) {
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
        if(mTokensPresenter != null)
            outState.putSerializable(CURRENT_FILTERING_KEY, mTokensPresenter.getFiltering());

        super.onSaveInstanceState(outState);
    }
}
