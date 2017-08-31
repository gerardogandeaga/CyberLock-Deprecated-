package com.gerardogandeaga.cyberlock.EncryptionFeatures.Playground;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LoginActivity;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Main.MainActivity;
import com.gerardogandeaga.cyberlock.R;

import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.APP_LOGGED_IN;
import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.mCountDownIsFinished;
import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.mCountDownTimer;
import static com.gerardogandeaga.cyberlock.Supports.Globals.DIRECTORY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.PLAYGROUIND_ALGO;

public class MainPlaygroundActivity extends AppCompatActivity
{
    // DATA
    private SharedPreferences mSharedPreferences;
    private int CryptState = 0;
    private String ALGO;

    // WIDGETS
    private TextView mTvCrypt;
    private EditText mEtCryptPassword, mEtCryptTextInput, mEtCryptTextOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_playground);
        ACTIVITY_INTENT = null; // START ACTIVITY WITH EMPTY INTENT\

        setupActivity();
    }

    private void setupActivity()
    {
        mSharedPreferences = getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE);

        // ACTION BAR TITLE AND BACK BUTTON
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Playground");

        mTvCrypt = (TextView) findViewById(R.id.tvCrypt);

        mEtCryptPassword = (EditText) findViewById(R.id.etCryptPassword);
        mEtCryptTextInput = (EditText) findViewById(R.id.etCryptTextInput);
        mEtCryptTextOutput = (EditText) findViewById(R.id.etCryptTextOutput);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) // ACTION BAR BACK BUTTON RESPONSE
    {
        switch (item.getItemId())
        {
            // CRYPT MODES
            case R.id.EncryptText:
                CryptState = 0;
                switchCryptMode();
                break;
            case R.id.DecryptText:
                CryptState = 1;
                switchCryptMode();
                break;
            // ALGO METHODS
            case R.id.action_AES256:
                mSharedPreferences.edit().putString(PLAYGROUIND_ALGO, "AES - 256").apply();
                break;
            case R.id.action_Blowfish448:
                mSharedPreferences.edit().putString(PLAYGROUIND_ALGO, "Blowfish - 448").apply();
                break;

            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void switchCryptMode()
    {
        if (CryptState == 0)
        {
            mTvCrypt.setText("TEXT ENCRYPTOR");
            mEtCryptTextInput.setHint("Plain Text Input");
            mEtCryptTextOutput.setHint("Encrypted Output");
        } else if (CryptState == 1)
        {
            mTvCrypt.setText("TEXT DECRYPTOR");
            mEtCryptTextInput.setHint("Encrypted Text Input");
            mEtCryptTextOutput.setHint("Decrypted Output");
        }
    }

    private void onCryptButton()
    {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_playground, menu);
        return true;
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
            ACTIVITY_INTENT = new Intent(this, MainActivity.class);
            finish();
            this.startActivity(ACTIVITY_INTENT);
            overridePendingTransition(R.anim.anim_slide_inleft, R.anim.anim_slide_outright);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();

        if (!this.isFinishing())
        {
            if (ACTIVITY_INTENT == null) // NO PENDING ACTIVITIES ???(MAIN)--->(EDIT)???
            {
                new LogoutProtocol().logoutExecuteAutosaveOff(this);
            }
        }
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------------------------------

}