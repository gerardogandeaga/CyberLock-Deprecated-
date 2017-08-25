package com.gerardogandeaga.cyberlock.Activitys.Activities.Login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;

import static com.gerardogandeaga.cyberlock.Supports.Globals.DELAY_TIME;
import static com.gerardogandeaga.cyberlock.Supports.Globals.DIRECTORY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.TEMP_PIN;

public class LogoutProtocol
{
    // APP STATE
    public static boolean APP_LOGGED_IN;
    public static Intent ACTIVITY_INTENT;

    // LOGOUT DELAY
    public static CountDownTimer mCountDownTimer;
    public static boolean mCountDownIsFinished = false;

    // SHARED PREFERENCES
    private SharedPreferences mSharedPreferences;

    public void logoutExecuteAutosaveOff(final Context context)
    {
        mCountDownTimer = new CountDownTimer(context.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getLong(DELAY_TIME, 0), 1000)
        {
            @Override
            public void onTick(long millisUntilFinished)
            {
                mCountDownIsFinished = false;
                System.out.println("Tick");
            }

            @Override
            public void onFinish()
            {
                mCountDownIsFinished = true;

                mSharedPreferences = context.getSharedPreferences(DIRECTORY, context.MODE_PRIVATE);
                mSharedPreferences.edit().remove(TEMP_PIN).apply();

                APP_LOGGED_IN = false;

                System.out.println("Timer Done!");
            }
        }.start();
    }

    public void logoutExecuteAutosaveOn(Context context)
    {
        mCountDownTimer = new CountDownTimer(context.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getLong(DELAY_TIME, 0), 1000)
        {
            @Override
            public void onTick(long millisUntilFinished)
            {
                mCountDownIsFinished = false;
                System.out.println("Tick");
            }

            @Override
            public void onFinish()
            {
                mCountDownIsFinished = true;

                APP_LOGGED_IN = false;
                System.out.println("Timer Done!");
            }
        }.start();
    }

    public void logoutImmediate(final Context context)
    {
        mSharedPreferences = context.getSharedPreferences(DIRECTORY, context.MODE_PRIVATE);
        mSharedPreferences.edit().remove(TEMP_PIN).apply();

        APP_LOGGED_IN = false;
    }
}