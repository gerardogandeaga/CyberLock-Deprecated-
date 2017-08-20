package com.gerardogandeaga.cyberlock.Activitys.Activities.Menus;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LoginActivity;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Main.MainActivity;
import com.gerardogandeaga.cyberlock.R;

import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.APP_LOGGED_IN;

public class Settings extends AppCompatActivity implements View.OnClickListener
{
    // DATA
    private SharedPreferences mSharedPreferences;
    public static final String KEY = "KEY", PIN = "PIN", AUTOSAVE = "AUTOSAVE";
    public static final int flags = Base64.DEFAULT;

    private Context mContext = this;

    // WIDGETS
    private TextView mTvChangePassword;
    private LinearLayout mAutoSave, mScrambleKey;
    private CheckBox mCbAutoSave;
    private Spinner mSpAutoLogoutDelay, mSpEncryptionMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ACTIVITY_INTENT = null;

        // ACTION BAR TITLE AND BACK BUTTON
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");

        // SPINNER DATA
        this.mSpAutoLogoutDelay = (Spinner) findViewById(R.id.spAutoLogoutDelay);
        final ArrayAdapter<CharSequence> adapterAutoLogoutDelay = ArrayAdapter.createFromResource(this, R.array.autologoutdelay_array, android.R.layout.simple_spinner_dropdown_item);
        adapterAutoLogoutDelay.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.mSpAutoLogoutDelay.setAdapter(adapterAutoLogoutDelay);
        // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        this.mSpEncryptionMethod = (Spinner) findViewById(R.id.spEncryptionMethod);
        final ArrayAdapter<CharSequence> adapterEncryptionMethod = ArrayAdapter.createFromResource(this, R.array.encryptionALGO_array, android.R.layout.simple_spinner_dropdown_item);
        adapterEncryptionMethod.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.mSpEncryptionMethod.setAdapter(adapterEncryptionMethod);

        this.mTvChangePassword = (TextView) findViewById(R.id.tvChangePassword);
        this.mCbAutoSave = (CheckBox) findViewById(R.id.cbAutoSave);
        this.mAutoSave = (LinearLayout) findViewById(R.id.AutoSave);
        this.mScrambleKey = (LinearLayout) findViewById(R.id.ScrambleKey);


        this.mAutoSave.setOnClickListener(this);
        this.mTvChangePassword.setOnClickListener(this);
        this.mScrambleKey.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.AutoSave: onAutoSave(); break;
            case R.id.tvChangePassword: onResetPassword(); break;
            case R.id.ScrambleKey: onScrambleKey(); break;
        }
    }

    private void onAutoSave()
    {
        mSharedPreferences = getSharedPreferences("com.gerardogandeaga.cyberlock", Context.MODE_PRIVATE);
        final boolean autoSave = mSharedPreferences.getBoolean(AUTOSAVE, false);

        if (!autoSave)
        {
            if (!mCbAutoSave.isChecked())
            {
                mSharedPreferences.edit().putBoolean(AUTOSAVE, true).apply();
                mCbAutoSave.toggle();
            }
        } else {
            if (mCbAutoSave.isChecked())
            {
                mSharedPreferences.edit().putBoolean(AUTOSAVE, false).apply();
                mCbAutoSave.toggle();
            }
        }
        mSharedPreferences = null;
    }

    private void onResetPassword()
    {
        ACTIVITY_INTENT = new Intent(this, Settings_ResetPin.class);
        finish();
        startActivity(ACTIVITY_INTENT);
    }

    private void onScrambleKey()
    {
        // DIALOG BUILDER
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("Scramble Encryption Key");
        alertDialog.setMessage("Scrambling key will result in ALL your content be momentarily decrypted and re-encrypted with the new key and selected algorithm, please wait for the full process to be completed. Please do not exit or close the app or it may result in data loss.");

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        alertDialog.setPositiveButton("Scramble", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();

                new Settings_ScrambleKey(mContext).execute();
            }
        });
        alertDialog.show();

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
                new LogoutProtocol().logoutExecute(this);
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
