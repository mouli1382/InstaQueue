package in.gm.instaqueue.util;

import android.Manifest;

public class AppConstants {

    public static final String PERMISSION_RECEIVE_SMS = Manifest.permission.RECEIVE_SMS;
    public static final String PERMISSION_READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;
    public static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;

    public static final int REQUESTCODE_RECEIVE_SMS = 1;
    public static final int REQUESTCODE_READ_PHONE_STATE = 2;
    public static final int REQUESTCODE_CAMERA = 3;
}
