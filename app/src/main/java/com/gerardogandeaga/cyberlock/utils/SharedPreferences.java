package com.gerardogandeaga.cyberlock.utils;

import android.content.Context;

public class SharedPreferences {
    public static final String DIRECTORY = "com.gerardogandeaga.cyberlock";

    // shared preferences keys
    // primitives
    private static final String IS_REGISTERED = "IS_REGISTERED";

    // options
    public static final String AUTOSAVE = "AUTO";
    public static final String LOGOUT_DELAY = "LOGOUT_DELAY";
    public static final String DELAY_TIME = "DELAY_TIME";
    public static final String LIST_FORMAT = "LIST_FORMAT";
    public static final String TAGGED_HEADERS = "TAGGED_HEADERS";
    // theme
    public static final String THEME = "THEME";

    // crypto
    public static final String PLAYGROUND_ALGO = "PLAYGROUND_ALGO";
    public static final String ENCRYPTION_ALGORITHM = "ENCRYPTION_ALGORITHM";
    public static final String CIPHER_ALGORITHM = "CIPHER_ALGORITHM";

    // Sensitives
    public static final String PASSWORD = "PASSWORD";
    public static final String CRYPT_KEY = "CRYPT_KEY";


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


    // user settings

    // auto save
    public static boolean getAutoSave(Context context) {
        return context.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getBoolean(AUTOSAVE, false);
    }
    public static void setAutoSave(Context context, boolean bool) {
        context.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).edit().putBoolean(AUTOSAVE, bool).apply();
    }

    // auto logout delay
    public static String getLogoutDelay(Context context) {
        return context.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getString(LOGOUT_DELAY, "Immediate");
    }
    public static void setLogoutDelay(Context context, String str) {
        context.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).edit().putString(LOGOUT_DELAY, str).apply();
    }
    public static long getLogoutDelayTime(Context context) {
        return (long) context.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getInt(DELAY_TIME, 0);
    }
    public static void setLogoutDelayTime(Context context, int n) {
        context.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).edit().putInt(DELAY_TIME, n).apply();
    }

    // list format
    public static String getListFormat(Context context) {
        return context.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getString(LIST_FORMAT, ListFormat.LINEAR);
    }
    public static void setListFormat(Context context, String str) {
        context.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).edit().putString(LIST_FORMAT, str).apply();
    }

    // tagged headers
    public static boolean getTaggedHeaders(Context context) {
        return context.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getBoolean(TAGGED_HEADERS, true);
    }
    public static void setTaggedHeaders(Context context, boolean bool) {
        context.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).edit().putBoolean(TAGGED_HEADERS, bool).apply();
    }

    public static class Checkers {

        public static boolean isLinearFormat(String format) {
            return !format.matches(ListFormat.GRID);
        }
    }
}