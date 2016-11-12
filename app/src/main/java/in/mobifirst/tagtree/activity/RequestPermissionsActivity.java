package in.mobifirst.tagtree.activity;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.authentication.OnBoardingActivity;

import static in.mobifirst.tagtree.util.ApplicationConstants.PERMISSION_READ_PHONE_STATE;
import static in.mobifirst.tagtree.util.ApplicationConstants.PERMISSION_RECEIVE_SMS;
import static in.mobifirst.tagtree.util.ApplicationConstants.REQUEST_CODE_READ_PHONE_STATE;
import static in.mobifirst.tagtree.util.ApplicationConstants.REQUEST_CODE_RECEIVE_SMS;

public class RequestPermissionsActivity extends BaseDrawerActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (checkPermission(this, PERMISSION_READ_PHONE_STATE) && checkPermission(this, PERMISSION_RECEIVE_SMS)) {
            startOnBoardingActivity();
        } else {
            requestForReadPhoneStatePermission();
        }
    }

    private void requestForReceiveSMSPermission() {
        requestPermission(RequestPermissionsActivity.this, mainContentView, REQUEST_CODE_RECEIVE_SMS, PERMISSION_RECEIVE_SMS);
    }

    private void requestForReadPhoneStatePermission() {
        requestPermission(RequestPermissionsActivity.this, mainContentView, REQUEST_CODE_READ_PHONE_STATE, PERMISSION_READ_PHONE_STATE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE_RECEIVE_SMS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(mainContentView, R.string.granted_permission_read_sms,
                        Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(mainContentView, R.string.rejected_permission_read_sms,
                        Snackbar.LENGTH_SHORT).show();
            }

            //Start OnBoardingActivity irrespective of the grants
            startOnBoardingActivity();

        } else if (requestCode == REQUEST_CODE_READ_PHONE_STATE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(mainContentView, R.string.granted_permission_read_phonestate,
                        Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(mainContentView, R.string.rejected_permission_read_phonestate,
                        Snackbar.LENGTH_SHORT).show();

            }
            requestForReceiveSMSPermission();
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void startOnBoardingActivity() {
        startActivity(new Intent(RequestPermissionsActivity.this, OnBoardingActivity.class));
        finish();
    }
}


