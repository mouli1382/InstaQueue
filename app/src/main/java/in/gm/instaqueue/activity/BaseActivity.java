package in.gm.instaqueue.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import in.gm.instaqueue.fragment.StatefulFragment;
import in.gm.instaqueue.prefs.SharedPrefs;

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    private static final String STATEFUL_FRAGMENT_TAG = "stateful_fragment_tag";

    private StatefulFragment mStatefulFragment;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        FragmentManager fragmentManager = getSupportFragmentManager();
        mStatefulFragment = (StatefulFragment) fragmentManager.findFragmentByTag(STATEFUL_FRAGMENT_TAG);
        if (mStatefulFragment == null) {
            mStatefulFragment = StatefulFragment.createInstance();
            fragmentManager
                    .beginTransaction()
                    .add(mStatefulFragment, STATEFUL_FRAGMENT_TAG)
                    .commit();
        }
    }

    public FirebaseAuth getAuthInstance() {
        if (mStatefulFragment != null) {
            return mStatefulFragment.getAuthInstance();
        } else {
            return null;
        }
    }

    public DatabaseReference getDatabaseReference() {
        if (mStatefulFragment != null) {
            return mStatefulFragment.getDatabaseReference();
        } else {
            return null;
        }
    }

    public String getUserPhoneNumber() {
        if (mStatefulFragment != null) {
            return mStatefulFragment.getUserPhoneNumber();
        } else {
            return "";
        }
    }

    public SharedPrefs getSharedPrefs() {
        if (mStatefulFragment != null) {
            return mStatefulFragment.getSharedPrefs();
        } else {
            return null;
        }
    }

    public FirebaseUser getCurrentUser() {
        if (mStatefulFragment != null) {
            return mStatefulFragment.getCurrentUser();
        } else {
            return null;
        }
    }

    //    private ProgressDialog mProgressDialog;
//
//    public void showProgressDialog() {
//        if (mProgressDialog == null) {
//            mProgressDialog = new ProgressDialog(this);
//            mProgressDialog.setCancelable(false);
//            mProgressDialog.setMessage("Loading...");
//        }
//
//        mProgressDialog.show();
//    }
//
//    public void hideProgressDialog() {
//        if (mProgressDialog != null && mProgressDialog.isShowing()) {
//            mProgressDialog.dismiss();
//        }
//    }

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
}
