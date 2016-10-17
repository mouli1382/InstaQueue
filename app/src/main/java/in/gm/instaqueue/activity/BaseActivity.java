package in.gm.instaqueue.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import javax.inject.Inject;

import in.gm.instaqueue.app.IQApplication;
import in.gm.instaqueue.dagger.component.AppComponent;
import in.gm.instaqueue.firebase.FirebaseManager;
import in.gm.instaqueue.prefs.SharedPrefs;

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";

    @Inject
    protected SharedPrefs mSharedPrefs;

    @Inject
    protected FirebaseManager mFirebaseManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getAppComponent().inject(this);
    }

    public FirebaseAuth getAuthInstance() {
        if (mFirebaseManager != null) {
            return mFirebaseManager.getAuthInstance();
        } else {
            return null;
        }
    }

    public DatabaseReference getDatabaseReference() {
        if (mFirebaseManager != null) {
            return mFirebaseManager.getDatabaseReference();
        } else {
            return null;
        }
    }

    public String getUserPhoneNumber() {
        if (mSharedPrefs != null) {
            return mSharedPrefs.getSting(SharedPrefs.PHONE_NUMBER_KEY);
        } else {
            return "";
        }
    }

    public FirebaseUser getCurrentUser() {
        if (mFirebaseManager != null) {
            return mFirebaseManager.getCurrentUser();
        } else {
            return null;
        }
    }

    public void showMessage(View view, int resId) {
        Snackbar snackbar = Snackbar
                .make(view, getString(resId), Snackbar.LENGTH_LONG);

        snackbar.show();
    }

    public void showMessage(View view, String message) {
        Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_LONG);

        snackbar.show();
    }

    public AppComponent getAppComponent() {
        return ((IQApplication) getApplicationContext()).getAppComponent();
    }
}
