package com.gerardogandeaga.cyberlock.utils.security;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;

import com.gerardogandeaga.cyberlock.utils.PreferencesAccessor;

import static com.gerardogandeaga.cyberlock.utils.PreferencesAccessor.TMP_PWD;

/**
 * @author gerardogandeaga
 */
public class LogoutProtocol {
    // DATA VARIABLES
    // APP STATE
    public static boolean APP_LOGGED_IN;
    public static Intent ACTIVITY_INTENT;
    // LOGOUT DELAY
    public static CountDownTimer mCountDownTimer;
    public static boolean mIsCountDownTimerFinished = false;

    public void logoutExecuteAutoSaveOff(final Context context) {
        mCountDownTimer = new CountDownTimer(PreferencesAccessor.getLogoutDelayTime(context), 1000) {
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

    public void logoutExecuteAutoSaveOn(final Context context) {
        mCountDownTimer = new CountDownTimer(PreferencesAccessor.getLogoutDelayTime(context), 1000) {
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