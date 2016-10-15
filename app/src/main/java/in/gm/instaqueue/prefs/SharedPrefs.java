package in.gm.instaqueue.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import in.gm.instaqueue.R;

//ToDo use DI like dagger to provide these dependencies.
public class SharedPrefs {

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private static SharedPrefs mSharedPrefs;
    public static final String PHONE_NUMBER_KEY = "phone_number_key";

    public static synchronized SharedPrefs getInstance(Context context) {
        if (mSharedPrefs == null) {
            mSharedPrefs = new SharedPrefs(context);
        }
        return mSharedPrefs;
    }

    private SharedPrefs(Context context) {
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
