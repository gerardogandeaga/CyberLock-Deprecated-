package com.gerardogandeaga.cyberlock.Activitys.Activities.Login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class LogoutProtocol
{
    public static Intent ACTIVITY_INTENT;
    public static boolean APP_LOGGED_IN;

    private static SharedPreferences mSharedPreferences;
    private static final String NAME = "com.gerardogandeaga.cyberlock";
    private static final String TEMP_PIN = "TEMP_PIN";

    public void logoutExecuteAutosaveOff(Context context)
    {
        mSharedPreferences = context.getSharedPreferences(NAME, context.MODE_PRIVATE);
        mSharedPreferences.edit().remove(TEMP_PIN).apply();

        APP_LOGGED_IN = false;
    }

    public void logoutExecuteAutosaveOn(Context context)
    {
        APP_LOGGED_IN = false;
    }
}