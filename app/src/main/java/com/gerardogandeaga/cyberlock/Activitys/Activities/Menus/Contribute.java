package com.gerardogandeaga.cyberlock.Activitys.Activities.Menus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LoginActivity;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Main.MainActivity;
import com.gerardogandeaga.cyberlock.R;

import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.APP_LOGGED_IN;

public class Contribute extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contribute);

        ACTIVITY_INTENT = null;

        // ACTION BAR TITLE AND BACK BUTTON
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Contribute");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) // ACTION BAR BACK BUTTON RESPONSE
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

    // THIS IS THE START OF THE SCRIPT FOR *** THE "TO LOGIN FUNCTION" THIS DETECTS THE ON PRESSED, START, TABS AND HOME BUTTONS IN ORDER TO INITIALIZE SECURITY "FAIL-SAFE"
    @Override
    protected void onStart()
    {
        super.onStart();

        if (!APP_LOGGED_IN)
        {
            ACTIVITY_INTENT = new Intent(this, LoginActivity.class);
            this.finish(); // CLEAN UP AND END
            this.startActivity(ACTIVITY_INTENT); // GO TO LOGIN ACTIVITY
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        if (ACTIVITY_INTENT == null) // NO PENDING ACTIVITIES ???(MAIN)--->(EDIT)???
        {
            ACTIVITY_INTENT = new Intent(this, MainActivity.class);
            this.finish();
            this.startActivity(ACTIVITY_INTENT);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();

        if (!this.isFinishing()) // HOME AND TABS AND SCREEN OFF
        {
            if (ACTIVITY_INTENT == null) // NO PENDING ACTIVITIES ???(MAIN)--->(EDIT)???
            {
                new LogoutProtocol().logoutExecuteAutosaveOff(this);
            }
        }
    }

    @Override
    public void finish() // BACK BUTTON CACHES ACTIVITY ACTUAL START ---> MAIN ACTIVITY
    {
        super.finish();

        Intent i = new Intent(this, MainActivity.class);
        this.startActivity(i);
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
}
