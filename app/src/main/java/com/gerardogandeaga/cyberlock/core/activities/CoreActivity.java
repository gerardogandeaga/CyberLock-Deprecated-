package com.gerardogandeaga.cyberlock.core.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.utils.Graphics;
import com.gerardogandeaga.cyberlock.utils.Resources;
import com.gerardogandeaga.cyberlock.utils.security.LogoutProtocol;

import butterknife.BindView;

import static com.gerardogandeaga.cyberlock.utils.security.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.utils.security.LogoutProtocol.APP_LOGGED_IN;
import static com.gerardogandeaga.cyberlock.utils.security.LogoutProtocol.mCountDownTimer;
import static com.gerardogandeaga.cyberlock.utils.security.LogoutProtocol.mIsCountDownTimerFinished;

/**
 * @author gerardogandeaga
 *
 * base activity which mosly control application security by ensuring logouts and as well
 * implifying the creation of fucture activities.
 */
// todo manke the activity intent an instance variable and more private
public abstract class CoreActivity extends AppCompatActivity {
    // no icon flag
    protected static final int NO_ICON = 0;

    // we bind the tool bar view in the sub class activity
    @BindView(R.id.toolbar) Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        super.onCreate(savedInstanceState);
        ACTIVITY_INTENT = null;
    }

    /**
     * we must bind the view in the sub class activity always
     */
    protected abstract void bindView();

    /**
     * binds the action bar to the toolbar if action bar is null
     * @param title toolbar main title
     * @param subTitle toolbar sub title
     * @param icon toolbar icon
     */
    protected void setupActionBar(@Nullable String title, @Nullable String subTitle, int icon) {
        if (getSupportActionBar() == null) {
            setSupportActionBar(mToolbar);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            actionBarTitle(title);
            actionBarSubTitle(subTitle);
            actionBarIcon(icon);
        }
    }

    /**
     * calls the setupActionBar() method with the expectancy that the action bar will
     * not rebind to the toolbar
     */
    protected void resetActionBar(@Nullable String title, @Nullable String subTitle, @DrawableRes int icon) {
        setupActionBar(title, subTitle, icon);
    }

    /**
     * set supportActionBar titl
     * @param title string title
     */
    protected void actionBarTitle(@Nullable String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }


    /**
     * set supportActionBar sub title
     * @param subTitle string subTitle
     */
    protected void actionBarSubTitle(@Nullable String subTitle) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(subTitle);
        }
    }


    /**
     * set supportActionBar icon
     * @param icon drawable Res to be passed in and turned into a drawable
     */
    protected void actionBarIcon(@DrawableRes int icon) {
        if (icon != NO_ICON) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setHomeAsUpIndicator(
                        Graphics.BasicFilter.mutateHomeAsUpIndicatorDrawable(this,
                                Resources.getDrawable(this, icon)));
            }
        }
    }

    /**
     * starts a new activity
     * @param cls activity class that we be opened
     */
    protected void intentGoTo(Class<?> cls) {
        newIntent(cls);
        intentGoTo();
    }

    protected void intentGoTo() {
        finish();
        startActivity(ACTIVITY_INTENT);
    }

    protected Intent getNewIntent() {
        return ACTIVITY_INTENT;
    }

    /**
     * set ACTIVITY_INTENT
     * @param cls activity class that we be opened
     */
    protected void newIntent(Class<?> cls) {
        ACTIVITY_INTENT = new Intent(this, cls);
    }

    @Override
    public void onBackPressed() {
        if (ACTIVITY_INTENT != null) {
            finish();
            startActivity(ACTIVITY_INTENT);
            System.out.println("exit");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mIsCountDownTimerFinished) {
            if (!APP_LOGGED_IN) {
                startActivity(ACTIVITY_INTENT);
            } else {
                if (mCountDownTimer != null) {
                    mCountDownTimer.cancel();
                    ACTIVITY_INTENT = null;
                }
            }
        }
    }

    /**
     * this is activated when either the home or tab button is pressed
     * or when the screen turns off
     */
    @Override
    public boolean isFinishing() {
        if (ACTIVITY_INTENT == null) {
            new LogoutProtocol().logoutImmediate(this);
        }

        return super.isFinishing();
    }
}
