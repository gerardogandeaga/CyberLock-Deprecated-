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
import android.widget.AdapterView;
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
import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.mCountDownIsFinished;
import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.mCountDownTimer;

public class Settings extends AppCompatActivity implements View.OnClickListener
{
    // DATA
    private SharedPreferences mSharedPreferences;
    private static final String DIRECTORY = "com.gerardogandeaga.cyberlock";
    private static final String KEY = "KEY", PIN = "PIN", AUTOSAVE = "AUTOSAVE", DELAY_KEY = "DELAY_KEY", DELAY_TIME = "DELAY_TIME";
    private static final int flags = Base64.DEFAULT;

    private ArrayAdapter<CharSequence> mAdapterAutoLogoutDelay;
    private String mAutoLogoutDelay;

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
        mAdapterAutoLogoutDelay = ArrayAdapter.createFromResource(this, R.array.autologoutdelay_array, android.R.layout.simple_spinner_dropdown_item);
        mAdapterAutoLogoutDelay.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.mSpAutoLogoutDelay.setAdapter(mAdapterAutoLogoutDelay);
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

        savedStates();

        // TODO AUTO LOGOUT DELAY_TIME REGISTRATION
        this.mSpAutoLogoutDelay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                Object object = parent.getItemAtPosition(position);
                if (object != null)
                {
                    mAutoLogoutDelay = object.toString();
                    long time = 0;
                    switch (mAutoLogoutDelay)
                    {
                        case "Immediate": time = 0; break;
                        case "15 Seconds": time = 15000; break;
                        case "30 Seconds": time = 30000; break;
                        case "1 Minute": time = 60000; break;
                        case "5 Minutes": time = 300000; break;
                        case "10 Minutes": time = 600000; break;
                        case "30 Minutes": time = 1800000; break;
                        case "1 Hour": time = 3600000; break;
                        case "2 Hours": time = 7200000; break;
                        case "Never": ; break;
                    }

                    System.out.println("Time = " + time);
                    getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).edit().putString(DELAY_KEY, mAutoLogoutDelay).putLong(DELAY_TIME, time).apply();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });
    }

    private void savedStates()
    {
        int delaySpinnerPosition = mAdapterAutoLogoutDelay.getPosition(getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getString(DELAY_KEY, "Immediate"));
        mSpAutoLogoutDelay.setSelection(delaySpinnerPosition);
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
        alertDialog.setMessage("On selecting Scramble, ALL your content will be momentarily decrypted and re-encrypted with the new key and selected algorithm. " +
                               "Please wait for the process to be completed and response message to be successful. Once the process has started you will not be able to " +
                               "cancel it. Additionally and most importantly, do not exit or close the application during the process as it may result in data loss.");

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
                new LogoutProtocol().logoutExecuteAutosaveOff(mContext);
            }
        }
    }

    @Override
    public void finish() // BACK BUTTON CACHES ACTIVITY ACTUAL START ---> MAIN ACTIVITY
    {
        Intent i = new Intent(this, MainActivity.class);
        this.startActivity(i);
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
}
