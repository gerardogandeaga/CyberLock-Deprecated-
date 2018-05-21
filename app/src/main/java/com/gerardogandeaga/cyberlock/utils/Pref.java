package com.gerardogandeaga.cyberlock.utils;

import android.content.Context;

/**
 * @author gerardogandeaga
 */
public class Pref {
    public static final String DIRECTORY = "com.gerardogandeaga.cyberlock";

    // shared preferences keys
    // primitives
    private static final String IS_REGISTERED = "IS_REGISTERED";

    // options
    private static final String AUTO_SAVE = "AUTO";
    private static final String LOGOUT_DELAY = "LOGOUT_DELAY";
    private static final String DELAY_TIME = "DELAY_TIME";
    private static final String TAGGED_HEADERS = "TAGGED_HEADERS";

    // saved settings

    // auto save
    public static boolean getAutoSave(Context context) {
        return context.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getBoolean(AUTO_SAVE, false);
    }
    public static void setAutoSave(Context context, boolean bool) {
        context.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).edit().putBoolean(AUTO_SAVE, bool).apply();
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

    // tagged headers
    public static boolean getTaggedHeaders(Context context) {
        return context.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getBoolean(TAGGED_HEADERS, true);
    }
    public static void setTaggedHeaders(Context context, boolean bool) {
        context.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).edit().putBoolean(TAGGED_HEADERS, bool).apply();
    }
}