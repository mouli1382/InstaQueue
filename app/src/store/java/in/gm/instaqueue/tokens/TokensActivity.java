package in.gm.instaqueue.tokens;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import javax.inject.Inject;

import in.gm.instaqueue.R;
import in.gm.instaqueue.activity.BaseDrawerActivity;
import in.gm.instaqueue.activity.CustomFields;
import in.gm.instaqueue.activity.IDCardActivity;
import in.gm.instaqueue.application.IQStoreApplication;
import in.gm.instaqueue.util.ActivityUtilities;

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

        TokensFragment tokensFragment =
                (TokensFragment) getSupportFragmentManager().findFragmentById(R.id.content_base_drawer);
        if (tokensFragment == null) {
            tokensFragment = TokensFragment.newInstance();
            ActivityUtilities.addFragmentToActivity(
                    getSupportFragmentManager(), tokensFragment, R.id.content_base_drawer);
        }

        // Create the presenter
        DaggerTokensComponent.builder()
                .applicationComponent(((IQStoreApplication)getApplication()).getApplicationComponent())
                .tokensPresenterModule(new TokensPresenterModule(tokensFragment)).build()
                .inject(this);

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
            // Handle the camera action
            Intent intent = new Intent(this, CustomFields.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(this, IDCardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        return super.onNavigationItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(CURRENT_FILTERING_KEY, mTokensPresenter.getFiltering());

        super.onSaveInstanceState(outState);
    }
}
