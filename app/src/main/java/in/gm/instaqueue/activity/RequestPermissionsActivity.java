package in.gm.instaqueue.activity;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;

import in.gm.instaqueue.R;

import static in.gm.instaqueue.util.AppConstants.PERMISSION_CAMERA;
import static in.gm.instaqueue.util.AppConstants.PERMISSION_READ_PHONE_STATE;
import static in.gm.instaqueue.util.AppConstants.PERMISSION_RECEIVE_SMS;
import static in.gm.instaqueue.util.AppConstants.REQUESTCODE_CAMERA;
import static in.gm.instaqueue.util.AppConstants.REQUESTCODE_READ_PHONE_STATE;
import static in.gm.instaqueue.util.AppConstants.REQUESTCODE_RECEIVE_SMS;

public class RequestPermissionsActivity extends BaseDrawerActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (checkPermission(this, PERMISSION_READ_PHONE_STATE) && checkPermission(this, PERMISSION_RECEIVE_SMS) && checkPermission(this, PERMISSION_CAMERA)) {
            startOnBoardingActivity();
        } else {
            requestForReadPhoneStatePermission();
        }
    }

    private void requestForReceiveSMSPermission() {
        requestPermission(RequestPermissionsActivity.this, mainContentView, REQUESTCODE_RECEIVE_SMS, PERMISSION_RECEIVE_SMS);
    }

    private void requestForReadPhoneStatePermission() {
        requestPermission(RequestPermissionsActivity.this, mainContentView, REQUESTCODE_READ_PHONE_STATE, PERMISSION_READ_PHONE_STATE);
    }

    private void requestForReceiveCameraPermission() {
        requestPermission(RequestPermissionsActivity.this, mainContentView, REQUESTCODE_CAMERA, PERMISSION_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUESTCODE_CAMERA) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(mainContentView, R.string.granted_permission_read_sms,
                        Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(mainContentView, R.string.rejected_permission_read_sms,
                        Snackbar.LENGTH_SHORT).show();
            }

            //Start OnBoardingActivity irrespective of the grants
            startOnBoardingActivity();

        } else if (requestCode == REQUESTCODE_READ_PHONE_STATE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(mainContentView, R.string.granted_permission_read_phonestate,
                        Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(mainContentView, R.string.rejected_permission_read_phonestate,
                        Snackbar.LENGTH_SHORT).show();

            }
            requestForReceiveSMSPermission();
        }
        else if (requestCode == REQUESTCODE_RECEIVE_SMS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(mainContentView, R.string.granted_permission_camera,
                        Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(mainContentView, R.string.rejected_permission_camera,
                        Snackbar.LENGTH_SHORT).show();

            }
            requestForReceiveCameraPermission();
        }else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void startOnBoardingActivity() {
        startActivity(new Intent(RequestPermissionsActivity.this, OnBoardingActivity.class));
        finish();
    }
}


