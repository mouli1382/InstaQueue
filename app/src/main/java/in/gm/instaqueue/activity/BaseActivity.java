package in.gm.instaqueue.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import javax.inject.Inject;

import in.gm.instaqueue.application.IQApplication;
import in.gm.instaqueue.preferences.IQSharedPreferences;

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    public void requestPermission(final AppCompatActivity appCompatActivity, View view, final int requestCode, final String permission) {
        if (!checkPermission(appCompatActivity, permission)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(appCompatActivity,
                    permission)) {

                Snackbar.make(view, "Accept the damn permissions!",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(android.R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ActivityCompat.requestPermissions(appCompatActivity,
                                        new String[]{permission},
                                        requestCode);
                            }
                        })
                        .show();
            } else {
                ActivityCompat.requestPermissions(appCompatActivity,
                        new String[]{permission},
                        requestCode);
            }
        }
    }

    public boolean checkPermission(AppCompatActivity appCompatActivity, String permission) {
        if (ContextCompat
                .checkSelfPermission(appCompatActivity, permission) != PackageManager.PERMISSION_GRANTED)
            return false;

        return true;
    }
}
