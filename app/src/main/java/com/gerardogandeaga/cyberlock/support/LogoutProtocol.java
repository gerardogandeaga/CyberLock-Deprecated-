package com.gerardogandeaga.cyberlock.support;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;

import static com.gerardogandeaga.cyberlock.support.Stored.DELAY_TIME;
import static com.gerardogandeaga.cyberlock.support.Stored.DIRECTORY;
import static com.gerardogandeaga.cyberlock.support.Stored.TMP_PWD;

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

                TMP_PWD = null;
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
        TMP_PWD = null;
        APP_LOGGED_IN = false;
    }
}