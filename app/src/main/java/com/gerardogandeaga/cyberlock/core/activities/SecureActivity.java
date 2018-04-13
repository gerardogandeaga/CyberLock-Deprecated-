package com.gerardogandeaga.cyberlock.core.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.gerardogandeaga.cyberlock.utils.PreferencesAccessor;

import org.jetbrains.annotations.NotNull;

import static com.gerardogandeaga.cyberlock.utils.PreferencesAccessor.TMP_PWD;

/**
 * @author gerardogandeaga
 */
public class SecureActivity extends AppCompatActivity {
    private static final String TAG = "SecureActivity";

    private static boolean IS_APP_LOGGED_IN;
    private static Intent SECURE_INTENT;

    private static CountDownTimer CountDownTimer;
    private static boolean IsCountDownTimerFinished = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        super.onCreate(savedInstanceState);
        nullifySecureIntent();
    }

    protected void setSecureIntent(@NotNull Intent intent) {
        SECURE_INTENT = intent;
    }

    protected Intent getSecureIntent() {
        return SECURE_INTENT;
    }

    protected void secureIntentGoTo(Intent intent) {
        setSecureIntent(intent);
        secureIntentGoTo();
    }

    protected void secureIntentGoTo() {
        startActivity(SECURE_INTENT);
    }

    protected void nullifySecureIntent() {
        SECURE_INTENT = null;
    }

    protected boolean secureIntentIsNull() {
        return SECURE_INTENT == null;
    }

    protected void startLogoutTimer(final boolean autoSave) {
        if (PreferencesAccessor.getLogoutDelayTime(this) > 0) {
            CountDownTimer = new CountDownTimer(PreferencesAccessor.getLogoutDelayTime(this), 10000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    IsCountDownTimerFinished = false;
                    System.out.println("tick");
                }

                @Override
                public void onFinish() {
                    logout(autoSave);
                }
            }.start();
        } else {
            logout(autoSave);
        }
    }

    protected void cancelLogoutTimer() {
        if (CountDownTimer != null) {
            CountDownTimer.cancel();
        }
    }

    protected void logout(boolean autoSave) {
        if (!autoSave) {
            TMP_PWD = null;
        }

        IsCountDownTimerFinished = true;
        IS_APP_LOGGED_IN = false;
    }

    protected boolean isLogoutTimerFinished() {
        return IsCountDownTimerFinished;
    }

    protected void setIsAppLoggedIn(boolean isAppLoggedIn) {
        IS_APP_LOGGED_IN = isAppLoggedIn;
    }

    protected boolean isAppLoggedIn() {
        return IS_APP_LOGGED_IN;
    }
}
