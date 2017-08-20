package com.gerardogandeaga.cyberlock.Activitys.Activities.Main;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.gerardogandeaga.cyberlock.EncryptionFeatures.PrivateMemo.MainMemoActivity;
import com.gerardogandeaga.cyberlock.R;

import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.APP_LOGGED_IN;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    // WIDGETS
    private RelativeLayout mMemoLock, mCardLock, mLoginLock, mGalleryLock;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ACTIVITY_INTENT = null; // START ACTIVITY WITH EMPTY INTENT

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Cyber Lock");

        this.mMemoLock = (RelativeLayout) findViewById(R.id.Memo);
        this.mCardLock = (RelativeLayout) findViewById(R.id.Payment);
        this.mLoginLock = (RelativeLayout) findViewById(R.id.Login);
        this.mGalleryLock = (RelativeLayout) findViewById(R.id.Gallery);

        this.mMemoLock.setOnClickListener(this);
        this.mCardLock.setOnClickListener(this);
        this.mLoginLock.setOnClickListener(this);
        this.mGalleryLock.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.Memo: ACTIVITY_INTENT = new Intent(this, MainMemoActivity.class); finish(); startActivity(ACTIVITY_INTENT); break;
            case R.id.Payment: ACTIVITY_INTENT = new Intent(this, MainPaymentInfoActivity.class); finish(); startActivity(ACTIVITY_INTENT); break;
            case R.id.Login: ACTIVITY_INTENT = new Intent(this, MainLoginInfoActivity.class); finish(); startActivity(ACTIVITY_INTENT); break;
//            case R.id.Gallery: ACTIVITY_INTENT = new Intent(this, MainPhotoGallery.class); finish(); startActivity(ACTIVITY_INTENT); break;
        }
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

        switch (id)
        {
            case (R.id.action_settings):
                ACTIVITY_INTENT = new Intent(this, Settings.class);
                finish();
                this.startActivity(ACTIVITY_INTENT);

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

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    // THIS IS THE START OF THE SCRIPT FOR *** THE "TO LOGIN FUNCTION" THIS DETECTS THE ON PRESSED, START, TABS AND HOME BUTTONS IN ORDER TO INITIALIZE SECURITY "FAIL-SAFE"
    @Override
    public void onStart()
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
            new LogoutProtocol().logoutExecute(this);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();

        if (!this.isFinishing()) { // HOME AND TABS AND SCREEN OFF
            if (ACTIVITY_INTENT == null) // NO PENDING ACTIVITIES ???(MAIN)--->(EDIT)???
            {
                new LogoutProtocol().logoutExecute(this);
            }
        }
        // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
    }
    // -----------------------------------------------------------------------------------------------------------------------------------------------------------------------
}
