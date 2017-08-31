package com.gerardogandeaga.cyberlock.Activitys.Activities.Main;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LoginActivity;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Menus.Contribute;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Menus.Settings;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.LoginInfo.MainLoginInfoActivity;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.PaymentInfo.MainPaymentInfoActivity;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.Playground.MainPlaygroundActivity;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.PrivateMemo.MainMemoActivity;
import com.gerardogandeaga.cyberlock.R;

import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.APP_LOGGED_IN;
import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.mCountDownIsFinished;
import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.mCountDownTimer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    // WIDGETS
    private RelativeLayout mMemoLock, mCardLock, mLoginLock, mPlayground;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;

    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ACTIVITY_INTENT = null; // START ACTIVITY WITH EMPTY INTENT

        setupLayout();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.Memo:
                ACTIVITY_INTENT = new Intent(this, MainMemoActivity.class);
                finish();
                this.startActivity(ACTIVITY_INTENT);
                overridePendingTransition(R.anim.anim_slide_inright, R.anim.anim_slide_outleft);

                break;
            case R.id.PaymentInfo:
                ACTIVITY_INTENT = new Intent(this, MainPaymentInfoActivity.class);
                finish();
                startActivity(ACTIVITY_INTENT);
                overridePendingTransition(R.anim.anim_slide_inright, R.anim.anim_slide_outleft);

                break;
            case R.id.LoginInfo:
                ACTIVITY_INTENT = new Intent(this, MainLoginInfoActivity.class);
                finish();
                startActivity(ACTIVITY_INTENT);
                overridePendingTransition(R.anim.anim_slide_inright, R.anim.anim_slide_outleft);

                break;
            case R.id.Playground:
                ACTIVITY_INTENT = new Intent(this, MainPlaygroundActivity.class);
                finish();
                startActivity(ACTIVITY_INTENT);
                overridePendingTransition(R.anim.anim_slide_inright, R.anim.anim_slide_outleft);
        }
    }

    private void setupLayout()
    {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.Content);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.Open, R.string.Close);

        mNavigationView = (NavigationView) findViewById(R.id.NavigationContent);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Cyber Lock");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.mMemoLock = (RelativeLayout) findViewById(R.id.Memo);
        this.mCardLock = (RelativeLayout) findViewById(R.id.PaymentInfo);
        this.mLoginLock = (RelativeLayout) findViewById(R.id.LoginInfo);
        this.mPlayground = (RelativeLayout) findViewById(R.id.Playground);

        calculateDrawerSize();
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        this.mMemoLock.setOnClickListener(this);
        this.mCardLock.setOnClickListener(this);
        this.mLoginLock.setOnClickListener(this);
        this.mPlayground.setOnClickListener(this);
    }

    private void calculateDrawerSize()
    {
        Resources resources = getResources();
        DisplayMetrics metrics = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;

        float screenWidth = width / resources.getDisplayMetrics().density;
        float navWidth = screenWidth - 56;

        navWidth = Math.min(navWidth, 320);

        int newWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, navWidth, resources.getDisplayMetrics());

        DrawerLayout.LayoutParams params = (android.support.v4.widget.DrawerLayout.LayoutParams) mNavigationView.getLayoutParams();
        params.width = (newWidth);
        mNavigationView.setLayoutParams(params);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        Dialog dialog = new Dialog(this);

        if (mDrawerToggle.onOptionsItemSelected(item)) return true;

        switch (id)
        {
            case (R.id.action_settings):
                ACTIVITY_INTENT = new Intent(this, Settings.class);
                finish();
                this.startActivity(ACTIVITY_INTENT);
                overridePendingTransition(R.anim.anim_push_downin, R.anim.anim_push_downout);

                return true;

            case (R.id.action_about):
                dialog.setContentView(R.layout.activity_about);
                dialog.setTitle("About Cyber Lock");
                dialog.show();

                break;


            case (R.id.action_contribute):
                ACTIVITY_INTENT = new Intent(this, Contribute.class);
                finish();
                this.startActivity(ACTIVITY_INTENT);
                overridePendingTransition(R.anim.anim_push_downin, R.anim.anim_push_downout);

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    // THIS IS THE START OF THE SCRIPT FOR *** THE "TO LOGIN FUNCTION" THIS DETECTS THE ON PRESSED, START, TABS AND HOME BUTTONS IN ORDER TO INITIALIZE SECURITY "FAIL-SAFE"
    @Override
    public void onStart()
    {
        super.onStart();

        if (mCountDownIsFinished)
        {
            if (!APP_LOGGED_IN)
            {
                ACTIVITY_INTENT = new Intent(this, LoginActivity.class);
                this.finish(); // CLEAN UP AND END
                this.startActivity(ACTIVITY_INTENT); // GO TO LOGIN ACTIVITY
            }
        } else
        {
            if (mCountDownTimer != null)
            {
                System.out.println("Cancel Called!");
                mCountDownTimer.cancel();

            }
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        if (ACTIVITY_INTENT == null) // NO PENDING ACTIVITIES ???(MAIN)--->(EDIT)???
        {
            new LogoutProtocol().logoutImmediate(this);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();

        if (!this.isFinishing()) { // HOME AND TABS AND SCREEN OFF
            if (ACTIVITY_INTENT == null) // NO PENDING ACTIVITIES ???(MAIN)--->(EDIT)???
            {
                new LogoutProtocol().logoutExecuteAutosaveOff(this);
            }
        }
        // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
    }
    // -----------------------------------------------------------------------------------------------------------------------------------------------------------------------
}
