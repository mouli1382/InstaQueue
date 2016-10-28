package in.gm.instaqueue.util;

import android.Manifest;

public class ApplicationConstants {

    public static final String PERMISSION_RECEIVE_SMS = Manifest.permission.RECEIVE_SMS;
    public static final String PERMISSION_READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;
    public static final String PHONE_NUMBER_KEY = "phone_number_key";

    public static final int REQUEST_CODE_RECEIVE_SMS = 1;
    public static final int REQUEST_CODE_READ_PHONE_STATE = 2;
}
