package com.gerardogandeaga.cyberlock.Activitys.Activities.Main;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LoginActivity;
import com.gerardogandeaga.cyberlock.Supports.LogoutProtocol;
import com.gerardogandeaga.cyberlock.Encryption.CryptPlayground;
import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.Supports.Globals;

import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.APP_LOGGED_IN;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.mCountDownIsFinished;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.mCountDownTimer;
import static com.gerardogandeaga.cyberlock.Supports.Globals.DIRECTORY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.PLAYGROUIND_ALGO;

public class MainPlaygroundActivity extends AppCompatActivity
{
    private SharedPreferences mSharedPreferences;

    // DATA VARIABLES
    private int CryptState = 0;
    private String ALGO;

    // WIDGETS
    private EditText mEtCryptPassword, mEtCryptTextInput;
    private Menu mMenu;
    private MenuItem
            mImCrypt,                        // OPERATION
            mImEncryptMode, mImDecryptMode,  // MODE
            mImAES256, mImBlowfish448;       // ALGORITHM
    private TextView mTvCrypt, mTvCryptTextOutput;

    // INITIAL ON CREATE METHODS
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Globals.COLORSCHEME(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playground);
        ACTIVITY_INTENT = null;

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }

        setupActivity();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_playground, menu);
        mMenu = menu;

        mImCrypt = mMenu.findItem(R.id.acCryptAction);
        mImEncryptMode = mMenu.findItem(R.id.EncryptMode);
        mImDecryptMode = mMenu.findItem(R.id.DecryptMode);
        mImAES256 = mMenu.findItem(R.id.acAES256);
        mImBlowfish448 = mMenu.findItem(R.id.acBlowfish448);

        mImEncryptMode.setChecked(true);
        mImAES256.setChecked(true);

        return true;
    }
    private void setupActivity()
    {
        mSharedPreferences = getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE);
        mSharedPreferences.edit().putString(PLAYGROUIND_ALGO, "AES - 256").apply();

        // ACTION BAR TITLE AND BACK BUTTON
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Playground");
        getSupportActionBar().setSubtitle("Encryption Testing Interface");

        mTvCrypt = (TextView) findViewById(R.id.tvCrypt);
        mTvCryptTextOutput = (TextView) findViewById(R.id.tvCryptTextOutput);

        mEtCryptPassword = (EditText) findViewById(R.id.etCryptPassword);
        mEtCryptTextInput = (EditText) findViewById(R.id.etCryptTextInput);
    }
    // -------------------------

    // ON CLICK
    @Override
    public boolean onOptionsItemSelected(MenuItem item) // ACTION BAR BACK BUTTON RESPONSE
    {
        switch (item.getItemId())
        {
            // ACTIONS BUTTONS
            case R.id.acCryptAction:
                onCryptButton();
                break;
            case R.id.acClipboardAction:
                copyOutput();
                break;

            // CRYPT MODES
            case R.id.EncryptMode:
                CryptState = 0;
                switchCryptMode();
                break;
            case R.id.DecryptMode:
                CryptState = 1;
                switchCryptMode();
                break;
            // ALGO METHODS
            case R.id.acAES256:
                mSharedPreferences.edit().putString(PLAYGROUIND_ALGO, "AES - 256").apply();
                mImAES256.setChecked(true);
                Toast.makeText(this, "Algorithm Set to AES", Toast.LENGTH_SHORT).show();
                break;
            case R.id.acBlowfish448:
                mSharedPreferences.edit().putString(PLAYGROUIND_ALGO, "Blowfish - 448").apply();
                mImBlowfish448.setChecked(true);
                Toast.makeText(this, "Algorithm Set to Blowfish", Toast.LENGTH_SHORT).show();
                break;

            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void onCryptButton()
    {
        if (CryptState == 0)
        {
            if (!mEtCryptPassword.getText().toString().matches(""))
            {
                String key = mEtCryptPassword.getText().toString();
                CryptPlayground playgroundCrypt = new CryptPlayground(this);

                if (!mEtCryptTextInput.getText().toString().matches(""))
                {
                    String plainText = mEtCryptTextInput.getText().toString();
                    try {
                        String encryptedText = playgroundCrypt.encrypt(plainText, key);

                        mTvCryptTextOutput.setText(encryptedText);
                    } catch (Exception e) {
                        e.printStackTrace();


                    }
                } else {
                    Toast.makeText(this, "Please Input Text", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please Input A Password", Toast.LENGTH_SHORT).show();
            }
        } else
        if (CryptState == 1)
        {
            if (!mEtCryptPassword.getText().toString().matches(""))
            {
                String key = mEtCryptPassword.getText().toString();
                CryptPlayground playgroundCrypt = new CryptPlayground(this);

                if (!mEtCryptTextInput.getText().toString().matches(""))
                {
                    String plainText = mEtCryptTextInput.getText().toString();
                    try {
                        String decryptedText = playgroundCrypt.decrypt(plainText, key);

                        if (decryptedText != null) {
                            mTvCryptTextOutput.setText(decryptedText);
                        } else {
                            Toast.makeText(this, "Wrong Password", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Wrong Password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Please Input Encrypted Text", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please Input A Valid Password", Toast.LENGTH_SHORT).show();
            }
        }
    }
    // --------

    // OTHER PLAYGROUND FUNCTIONS
    private void switchCryptMode()
    {
        if (CryptState == 0) // ENCRYPT MODE
        {
            mTvCrypt.setText("{ TEXT ENCRYPTION }");
            mEtCryptTextInput.getText().clear();

            mEtCryptTextInput.setHint("Plain Text Input");
            mTvCryptTextOutput.setText("Encrypted Output");

            mImCrypt.setTitle("Encrypt");
            mImEncryptMode.setChecked(true);
        } else if (CryptState == 1) // DECRYPT MODE
        {
            mTvCrypt.setText("{ TEXT DECRYPTION }");
            mEtCryptTextInput.getText().clear();

            mEtCryptTextInput.setHint("Encrypted Text Input");
            mTvCryptTextOutput.setText("Decrypted Output");

            mImCrypt.setTitle("Decrypt");
            mImDecryptMode.setChecked(true);
        }
    }
    private void copyOutput()
    {
        if (!mTvCryptTextOutput.getText().toString().matches(""))
        {
            String clippedText = mTvCryptTextOutput.getText().toString();

            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("", clippedText);
            clipboard.setPrimaryClip(clip);

            Toast.makeText(this, "Output Copied To Clipboard", Toast.LENGTH_SHORT).show();
        } else
        {
            Toast.makeText(this, "Nothing To Copy", Toast.LENGTH_SHORT).show();
        }
    }
    // --------------------------

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

                System.gc();
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