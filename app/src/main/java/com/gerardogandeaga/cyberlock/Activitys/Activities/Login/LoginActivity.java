package com.gerardogandeaga.cyberlock.Activitys.Activities.Login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gerardogandeaga.cyberlock.Activitys.Activities.Main.MainActivity;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Main.MainEditActivity;
import com.gerardogandeaga.cyberlock.Crypto.CryptKeyHandler;
import com.gerardogandeaga.cyberlock.Crypto.SHA256PinHash;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.ContentDatabase.Data;
import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.Supports.Globals;
import com.gerardogandeaga.cyberlock.Supports.LogoutProtocol;

import java.util.Arrays;

import static com.gerardogandeaga.cyberlock.Supports.Globals.CRYPT_KEY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.DIRECTORY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.FLAGS;
import static com.gerardogandeaga.cyberlock.Supports.Globals.LAST_LOGIN;
import static com.gerardogandeaga.cyberlock.Supports.Globals.LOGGED;
import static com.gerardogandeaga.cyberlock.Supports.Globals.MASTER_KEY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.PASSCODE;
import static com.gerardogandeaga.cyberlock.Supports.Globals.TEMP_PIN;

public class LoginActivity extends AppCompatActivity {
    private Context mContext = this;
    private SharedPreferences mSharedPreferences;

    private String mPasscode;
    // ACTIVITY INTENT
    private Intent mIntent;
    private boolean mIsEdit;

    // WIDGETS
    private EditText mEtPasscode;
    private ProgressDialog mProgressDialog;

    // INITIAL ON CREATE METHODS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Globals.COLORSCHEME(this);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        setupLayout();
    }
    private void setupLayout() {
        LogoutProtocol.ACTIVITY_INTENT = null;
        mSharedPreferences = getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE);

        mIntent = getIntent();
        mIsEdit = getIntent().getBooleanExtra("edit?", false);
        mIntent.removeExtra("edit?"); // REMOVE LAST ACTIVITY INFO

        if (!isRegistered()) {
            LogoutProtocol.ACTIVITY_INTENT = new Intent(this, RegistrationActivity.class);
            this.finish();
            this.startActivity(LogoutProtocol.ACTIVITY_INTENT);
        } else {
            setContentView(R.layout.activity_passcode_login);

            mEtPasscode = (EditText) findViewById(R.id.etPasscode);
            Button btnLogin = (Button) findViewById(R.id.btnLogin);

            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPasscode = mEtPasscode.getText().toString();

                    if (!mPasscode.matches("")) {
                        onPinCompleted();
                    } else {
                        Toast.makeText(mContext, "No Passcode detected", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    private void progressBar() {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("Verifying...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }
    // -------------------------

    // WHEN LOGIN CLICK IS REGISTERED
    public void clear() {
        mPasscode = "";
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mEtPasscode.getText().clear();
            }
        });
    }
    private void loginIntent() {
        Intent i;
        Bundle bundle = mIntent.getExtras();
        mIntent.removeExtra("lastDB");
        clear();

        if (mIsEdit) {
            i = new Intent(LoginActivity.this, MainEditActivity.class);
            i.putExtra("data", (Data) bundle.get("lastDB"));
        } else {
            i = new Intent(LoginActivity.this, MainActivity.class);
        }

        mProgressDialog.dismiss();
        finish();
        LoginActivity.this.startActivity(i); // MOVE TO MAIN ACTIVITY
    }
    private void onPinCompleted() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressBar();
            }
            @Override
            protected Void doInBackground(Void... params) {
                try // TRY LOGIN MECHANICS
                {
                    final String decryptedPulledPin = new CryptKeyHandler(mContext)
                            .DECRYPT_KEY(mSharedPreferences.getString(PASSCODE, null), mPasscode);
                    final String loginPinHash = SHA256PinHash
                            .HASH_FUNCTION(mPasscode, Arrays.copyOfRange(Base64.decode(decryptedPulledPin, FLAGS), 0, 128));

//                    System.out.println("LOGIN INPUT: " + loginPinHash);

//                    System.out.println("CACHED HASH: " + decryptedPulledPin);

                    if (decryptedPulledPin.equals(loginPinHash)) /// TEST PERIODICALLY INPUTTED PASSCODE AGAINST CACHED PASSCODE
                    {
                        TEMP_PIN = mPasscode;
                        MASTER_KEY = new CryptKeyHandler(mContext).DECRYPT_KEY(mSharedPreferences.getString(CRYPT_KEY, null), TEMP_PIN);
                        System.out.println("MASTER KEY: " + MASTER_KEY);

                        LogoutProtocol.APP_LOGGED_IN = true; // APP LOGGED IN
                        getLastLoginTime();
                        loginIntent();

                        System.gc();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    clear();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "Please Try Again", Toast.LENGTH_SHORT).show(); // SOMETHING WENT WRONG WITH DATA COMPARISONS
                        }
                    });
                }

                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                clear();
                mProgressDialog.dismiss();
            }
        }.execute();
    }
    // ------------------------------

    private void getLastLoginTime() {
        LOGGED = mSharedPreferences.getString(LAST_LOGIN, "");
    }

    // THIS IS THE START OF THE SCRIPT FOR *** THE "TO LOGIN FUNCTION" THIS DETECTS THE ON PRESSED, START, TABS AND HOME BUTTONS IN ORDER TO INITIALIZE SECURITY "FAIL-SAFE"
    @Override
    protected void onStart() // <--- ON *LOGIN REQUIREMENT* START -- *LOGIN = FALSE*
    {
        super.onStart();

        LogoutProtocol.APP_LOGGED_IN = false; // APP NOT LOGGED IN
        System.out.println("MASTER KEY IS EQUAL TO " + MASTER_KEY);
    }
    @Override
    public void onBackPressed() // <--- BACK PRESSED *HOME*
    {
        super.onBackPressed();

        Intent homeIntent = new Intent(Intent.ACTION_MAIN); // (NEXT LINES) SCRIPT TO SEND TO THE OS HOME SCREEN
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------

    // QUARANTINED FUNCTIONS
    private boolean isRegistered() {
        final String passcode = mSharedPreferences.getString(PASSCODE, null);
        final String key = mSharedPreferences.getString(CRYPT_KEY, null);

        return (passcode != null && key != null);
    }
}