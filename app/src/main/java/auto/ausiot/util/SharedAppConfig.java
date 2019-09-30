package auto.ausiot.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by anu on 23/06/19.
 */

public class SharedAppConfig {
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";

    public void save(Activity activity, String key, String value) {
        sharedpreferences = activity.getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }


    public String get(Activity activity, String key) {
        String value = null;
        sharedpreferences = activity.getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        if (sharedpreferences.contains(key)) {
            value = sharedpreferences.getString(key, "");
        }
        return value;
    }

}
