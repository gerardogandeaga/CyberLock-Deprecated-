package com.gerardogandeaga.cyberlock.core.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.utils.Graphics;
import com.gerardogandeaga.cyberlock.utils.Resources;

import butterknife.BindView;

/**
 * @author gerardogandeaga
 *
 * base activity which mosly control application security by ensuring logouts and as well
 * implifying the creation of fucture activities.
 */
// todo make the activity intent an instance variable and more private
public abstract class CoreActivity extends SecureActivity {
    private static final String TAG = "CoreActivity";

    // no icon flag
    protected static final int NO_ICON = 0;

    // we bind the tool bar view in the sub class activity
    @BindView(R.id.toolbar) Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            getSupportActionBar().setHomeButtonEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

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
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                getSupportActionBar().setHomeAsUpIndicator(
                        Graphics.BasicFilter.mutateHomeAsUpIndicatorDrawable(this,
                                Resources.getDrawable(this, icon)));
            }
        }
    }

    /**
     * set SECURE_INTENT
     * @param cls activity class that we be opened
     */
    protected void newIntent(Class<?> cls) {
        setSecureIntent(new Intent(this, cls));
    }

    /**
     * starts a new activity
     * @param cls activity class that we be opened
     */
    protected void newIntentGoTo(Class<?> cls) {
        newIntent(cls);
        newIntentGoTo();
    }

    protected void newIntentGoTo() {
        startActivity(getSecureIntent());
    }

    protected Intent getNewIntent() {
        return getSecureIntent();
    }

    @Override
    public void onBackPressed() {
        startActivity(getSecureIntent());
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!isAppLoggedIn()) {
            if (secureIntentIsNull()) {
                newIntent(LoginActivity.class);
            }
            startActivity(getSecureIntent());
        } else {
            cancelLogoutTimer();
            Log.i(TAG, "onStart: logout timer cancelled!");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (!isFinishing()) {
            if (secureIntentIsNull()) {
                startLogoutTimer(false);
                Log.i(TAG, "onPause: starting logout timer");
            }
        }
    }
}
