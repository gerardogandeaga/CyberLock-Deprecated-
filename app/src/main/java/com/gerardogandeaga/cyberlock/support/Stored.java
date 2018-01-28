package com.gerardogandeaga.cyberlock.support;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;

@SuppressLint("Registered")
public class Stored extends AppCompatActivity {
    public static final String DIRECTORY = "com.gerardogandeaga.cyberlock";

    // Primitives
    private static final String IS_REGISTERED = "IS_REGISTERED";

    // Settings
    public static final String AUTOSAVE = "AUTO";
    public static final String LOGOUT_DELAY = "LOGOUT_DELAY";
    public static final String DELAY_TIME = "DELAY_TIME";
    // Theme
    public static final String THEME = "THEME";
    // Content list layout
    public static final String LIST_FORMAT = "LIST_FORMAT";
    // --
    public static final String PLAYGROUND_ALGO = "PLAYGROUND_ALGO";
    public static final String ENCRYPTION_ALGORITHM = "ENCRYPTION_ALGORITHM";
    public static final String CIPHER_ALGORITHM = "CIPHER_ALGORITHM";

    // Sensitives
    public static final String PASSWORD = "PASSWORD";
    public static final String CRYPT_KEY = "CRYPT_KEY";
    // ------------------

    public static final int FLAGS = Base64.DEFAULT;


    public static String TMP_PWD;

    public static boolean getIsRegistered(Context context) {
        return context.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getBoolean(IS_REGISTERED, false);
    }

    public static String getPassword(Context context) {
        return context.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getString(PASSWORD, null);
    }

    public static String getMasterKey(Context context) {
        return context.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getString(CRYPT_KEY, null);
    }

    public static String getEncryptionAlgorithm(Context context) {
        return context.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getString(ENCRYPTION_ALGORITHM, "AES");
    }


    // auto save
    public static boolean getAutoSave(Context context) {
        return context.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getBoolean(AUTOSAVE, false);
    }
    public static void setAutoSave(Context context, boolean bool) {
        context.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).edit().putBoolean(AUTOSAVE, bool).apply();
    }

    // auto logout delay
    public static long getAutoLogoutDelay(Context context) {
        return context.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getLong(DELAY_TIME, 0);
    }
    public static void setAutoLogoutDelay(Context context, long n) {
        context.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).edit().putLong(DELAY_TIME, n).apply();
    }

    // list format
    public static String getListFormat(Context context) {
        return context.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getString(LIST_FORMAT, "RV_LINEAR");
    }
    public static void setListFormat(Context context, String format) {
        context.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).edit().putString(LIST_FORMAT, format).apply();
    }
}