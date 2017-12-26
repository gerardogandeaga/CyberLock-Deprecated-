package com.gerardogandeaga.cyberlock.support;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;

import static com.gerardogandeaga.cyberlock.support.Globals.DELAY_TIME;
import static com.gerardogandeaga.cyberlock.support.Globals.DIRECTORY;
import static com.gerardogandeaga.cyberlock.support.Globals.MASTER_KEY;
import static com.gerardogandeaga.cyberlock.support.Globals.TEMP_PIN;

public class LogoutProtocol {
    // DATA VARIABLES
    // APP STATE
    public static boolean APP_LOGGED_IN;
    public static Intent ACTIVITY_INTENT;
    // LOGOUT DELAY
    public static CountDownTimer mCountDownTimer;
    public static boolean mIsCountDownTimerFinished = false;

    public void logoutExecuteAutosaveOff(final Context context) {
        mCountDownTimer = new CountDownTimer(context.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getLong(DELAY_TIME, 0), 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mIsCountDownTimerFinished = false;
            }

            @Override
            public void onFinish() {
                mIsCountDownTimerFinished = true;

                TEMP_PIN = null;
                MASTER_KEY = null;
                APP_LOGGED_IN = false;
            }
        }.start();
    }

    public void logoutExecuteAutosaveOn(final Context context) {
        mCountDownTimer = new CountDownTimer(context.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getLong(DELAY_TIME, 0), 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mIsCountDownTimerFinished = false;
            }

            @Override
            public void onFinish() {
                mIsCountDownTimerFinished = true;

                APP_LOGGED_IN = false;
            }
        }.start();
    }

    public void logoutImmediate(final Context context) {
        TEMP_PIN = null;
        MASTER_KEY = null;
        APP_LOGGED_IN = false;
    }
}