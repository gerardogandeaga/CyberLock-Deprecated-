package com.gerardogandeaga.cyberlock.Activitys.Activities.Menus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LoginActivity;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Main.MainActivity;
import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.Supports.Globals;
import com.gerardogandeaga.cyberlock.Supports.LogoutProtocol;

import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.APP_LOGGED_IN;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.mCountDownIsFinished;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.mCountDownTimer;

public class Contribute extends AppCompatActivity
{
    private TextView mTvWebPasgeURL;

    // INITIAL ON CREATE METHODS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Globals.COLORSCHEME(this);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        setupLayout();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
            {
                // Respond to the action bar's Up/Home button
                case android.R.id.home:
                    onBackPressed();
                    return true;
            }
        return super.onOptionsItemSelected(item);
    }
    private void setupLayout() {
        setContentView(R.layout.activity_contribute);
        ACTIVITY_INTENT = null;
        // ACTION BAR TITLE AND BACK BUTTON
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Contribute");

        this.mTvWebPasgeURL = (TextView) findViewById(R.id.tvURL);

        this.mTvWebPasgeURL.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
            }
        });
    }
    // -------------------------


    // THIS IS THE START OF THE SCRIPT FOR *** THE "TO LOGIN FUNCTION" THIS DETECTS THE ON PRESSED, START, TABS AND HOME BUTTONS IN ORDER TO INITIALIZE SECURITY "FAIL-SAFE"
    @Override
    protected void onStart() {
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
    public void onBackPressed() {
        super.onBackPressed();
        if (ACTIVITY_INTENT == null) // NO PENDING ACTIVITIES ???(MAIN)--->(EDIT)???
            {
                ACTIVITY_INTENT = new Intent(this, MainActivity.class);
                this.finish();
                this.startActivity(ACTIVITY_INTENT);
            }
    }
    @Override
    public void onPause() {
        super.onPause();

        if (!this.isFinishing()) // HOME AND TABS AND SCREEN OFF
            {
                if (ACTIVITY_INTENT == null) // NO PENDING ACTIVITIES ???(MAIN)--->(EDIT)???
                    {
                        new LogoutProtocol().logoutExecuteAutosaveOff(this);
                    }
            }
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
}
