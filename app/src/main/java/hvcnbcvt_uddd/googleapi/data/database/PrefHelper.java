package hvcnbcvt_uddd.googleapi.data.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PrefHelper {
    private SharedPreferences preferences;

    public PrefHelper(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void putString(String key, String value) {
        checkForNullKey(key);
        preferences.edit().putString(key, value).apply();
    }

    public String getString(String key) {
        return preferences.getString(key, "");
    }

    public void checkForNullKey(String key){
        if (key == null){
            throw new NullPointerException();
        }
    }
}
