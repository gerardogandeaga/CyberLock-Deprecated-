package com.gerardogandeaga.cyberlock.Activitys.Activities.Login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class LogoutProtocol
{
    public static Intent ACTIVITY_INTENT;
    public static boolean APP_LOGGED_IN;

    private static SharedPreferences sharedPreferences;
    private static final String NAME = "com.gerardogandeaga.cyberlock";
    private static final String TEMP_PIN = "TEMP_PIN";

    public void logoutExecute(Context context)
    {
        sharedPreferences = context.getSharedPreferences(NAME, context.MODE_PRIVATE);
        sharedPreferences.edit().remove(TEMP_PIN).apply();

        APP_LOGGED_IN = false;
    }
}