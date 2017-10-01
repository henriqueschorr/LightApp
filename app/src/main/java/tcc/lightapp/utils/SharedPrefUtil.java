package tcc.lightapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Henrique on 01/10/2017.
 */

public class SharedPrefUtil {
    private static final String APP_PREFS = "application_preferences";

    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    public SharedPrefUtil(Context mContext) {
        this.mContext = mContext;
    }

    public void saveString(String key, String value) {
        mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mEditor.putString(key, value);
        mEditor.commit();
    }
}
