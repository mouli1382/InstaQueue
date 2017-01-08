package in.mobifirst.tagtree.activity;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import javax.inject.Inject;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.application.IQStoreApplication;
import in.mobifirst.tagtree.config.PrefsActivity;
import in.mobifirst.tagtree.ftu.SettingsActivity;
import in.mobifirst.tagtree.preferences.IQSharedPreferences;
import in.mobifirst.tagtree.util.ApplicationConstants;

public class BaseDrawerActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ActionBarDrawerToggle mDrawerToggle;
    @Inject
    IQSharedPreferences mIQSharedPrefs;
    protected View mainContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((IQStoreApplication) getApplication())
                .getApplicationComponent()
                .inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainContentView = findViewById(R.id.content_base_drawer);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        String storeMailId = mIQSharedPrefs.getSting(ApplicationConstants.EMAIL_KEY);
        String storeName = mIQSharedPrefs.getSting(ApplicationConstants.DISPLAY_NAME_KEY);
        String profilePicUrl = mIQSharedPrefs.getSting(ApplicationConstants.PROFILE_PIC_URL_KEY);
        ImageView profilePicImageView = (ImageView) findViewById(R.id.navProfilePic);
        Glide.with(getApplicationContext())
                .load(profilePicUrl)
                .centerCrop()
                .into(profilePicImageView);
        TextView storeMailIdTextView = (TextView) findViewById(R.id.navStoreMailId);
        TextView storeNameTextView = (TextView) findViewById(R.id.navStoreName);
        if (storeMailIdTextView != null && storeNameTextView != null)
            storeMailIdTextView.setText(storeMailId);
        if (storeNameTextView != null && storeNameTextView != null)
            storeNameTextView.setText(storeName);

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();

        if (id == R.id.nav_account) {
            //Launch Account screen
            SettingsActivity.start(BaseDrawerActivity.this);
        } else if (id == R.id.nav_settings) {
            //Launch local config screen
            PrefsActivity.start(BaseDrawerActivity.this);
        } else if (id == R.id.nav_help) {
            //ToDo Handle Help here.
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
