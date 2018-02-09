package com.gerardogandeaga.cyberlock.utils;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;

import static com.gerardogandeaga.cyberlock.utils.Stored.TMP_PWD;

public class LogoutProtocol {
    // DATA VARIABLES
    // APP STATE
    public static boolean APP_LOGGED_IN;
    public static Intent ACTIVITY_INTENT;
    // LOGOUT DELAY
    public static CountDownTimer mCountDownTimer;
    public static boolean mIsCountDownTimerFinished = false;

    public void logoutExecuteAutosaveOff(final Context context) {
        mCountDownTimer = new CountDownTimer(Stored.getLogoutDelayTime(context), 1000) {
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
        mCountDownTimer = new CountDownTimer(Stored.getLogoutDelayTime(context), 1000) {
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