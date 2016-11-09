package in.gm.instaqueue.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import in.gm.instaqueue.R;

public class IQSharedPreferences {

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    public static final String PHONE_NUMBER_KEY = "phone_number_key";
    public static final String UUID_KEY = "UUID_key";

    public IQSharedPreferences(Context context) {
        mSharedPreferences = context.getSharedPreferences(
                context.getString(R.string.application_preferences_file), Context.MODE_PRIVATE);

        mEditor = mSharedPreferences.edit();
    }

    public boolean putString(String key, String value) {
        mEditor.putString(key, value);
        return mEditor.commit();
    }

    public String getSting(String key) {
        String value = mSharedPreferences.getString(key, null);
        return TextUtils.isEmpty(value) ? "" : value;
    }
}
