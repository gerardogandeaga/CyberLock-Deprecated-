package com.gerardogandeaga.cyberlock.Supports;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Toast;

import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LoginActivity;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Main.MainActivity;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Menus.Settings;
import com.gerardogandeaga.cyberlock.Crypto.CryptKeyHandler;
import com.gerardogandeaga.cyberlock.Crypto.SHA256PinHash;
import com.gerardogandeaga.cyberlock.R;

import static com.gerardogandeaga.cyberlock.Supports.Globals.CRYPT_KEY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.DIRECTORY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.IS_REGISTERED;
import static com.gerardogandeaga.cyberlock.Supports.Globals.MASTER_KEY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.PASSCODE;
import static com.gerardogandeaga.cyberlock.Supports.Globals.TEMP_PIN;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.APP_LOGGED_IN;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.mCountDownIsFinished;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.mCountDownTimer;

public class Settings_ChangeComplexPasscode extends AppCompatActivity {
    private Context mContext = this;
    private SharedPreferences mSharedPreferences;

    // DATA VARIABLES
    private static String mPasscodeString = "";
    private static String mPasscodeMark = "";
    private static String mCode1 = "";
    private static String mCode2 = "";

    // WIDGETS
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
        setContentView(R.layout.activity_passcode_login);
        ACTIVITY_INTENT = null;

        mSharedPreferences = getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE);
    }
    private void progressBar() {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("Verifying...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }
    // -------------------------

    // KEYBOARD REGISTRATION
    public String addToPasscodeString(String s) {
        if (!s.matches("DEL"))
            {
                if (mPasscodeString.length() < 16) {
                    mPasscodeString = mPasscodeString + s;

                    mPasscodeMark = mPasscodeMark + "*";
                } else {
                    Toast.makeText(mContext, "Maximum Length Of 16 Characters Exceeded", Toast.LENGTH_SHORT).show();
                }
            } else
            {
                if (mPasscodeString.length() != 0)
                    {
                        mPasscodeString = mPasscodeString.substring(0, mPasscodeString.length() - 1);

                        mPasscodeMark = mPasscodeMark.substring(0, mPasscodeMark.length() - 1);
                    }
            }

        return mPasscodeString;
    }
    // ---------------------

    // PASSCODE REGISTRATION
    private void onPinsCompleted() {
        new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressBar();
            }

            @Override
            protected Void doInBackground(Void... params) {
                final String codeFirst = mCode1;

                mCode1 = "";
                mCode2 = "";

                final String pinHash; // GENERATE THE HASH PASSCODE
                try
                    {
                        CryptKeyHandler cryptKeyHandler = new CryptKeyHandler(mContext);
                        pinHash = cryptKeyHandler
                                .ENCRYPT_KEY(SHA256PinHash.HASH_FUNCTION(codeFirst, SHA256PinHash.GENERATE_SALT()), codeFirst);

                        mSharedPreferences.edit().putString(PASSCODE, pinHash).apply(); // ADD HASHED PASSCODE TO STORE
                        System.out.println("HASHED PASSCODE :" + pinHash);
                        mSharedPreferences.edit().putString(CRYPT_KEY,
                                cryptKeyHandler.ENCRYPT_KEY(
                                        cryptKeyHandler.DECRYPT_KEY(
                                                mSharedPreferences.getString(CRYPT_KEY, null), TEMP_PIN), codeFirst))
                                .apply();
                        TEMP_PIN = codeFirst;
                        MASTER_KEY = cryptKeyHandler.DECRYPT_KEY(mSharedPreferences.getString(CRYPT_KEY, null), TEMP_PIN);

                        mProgressDialog.dismiss();

                        ACTIVITY_INTENT = new Intent(mContext, Settings.class);
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "Pin Successfully Reset", Toast.LENGTH_SHORT).show();
                            }
                        });
                        mContext.startActivity(ACTIVITY_INTENT);

                    } catch (Exception e)
                    {
                        e.printStackTrace();

                        mProgressDialog.dismiss();

                        mSharedPreferences.edit().putBoolean(IS_REGISTERED, true).apply();
                        ACTIVITY_INTENT = new Intent(mContext, Settings.class);

                        mPasscodeString = "";
                        mPasscodeMark = "";

                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                            }
                        });
                        mContext.startActivity(ACTIVITY_INTENT);
                    }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                mSharedPreferences.edit().putBoolean(IS_REGISTERED, true).apply();
                mProgressDialog.dismiss();
            }
        }.execute();
    }
    // ----------------

    // THIS IS THE START OF THE SCRIPT FOR *** THE "TO LOGIN FUNCTION" THIS DETECTS THE ON PRESSED, START, TABS AND HOME BUTTONS IN ORDER TO INITIALIZE SECURITY "FAIL-SAFE"
    @Override
    protected void onStart() {
        super.onStart();

        if (mCountDownIsFinished) {
            if (!APP_LOGGED_IN) {
                ACTIVITY_INTENT = new Intent(this, LoginActivity.class);
                this.finish(); // CLEAN UP AND END
                this.startActivity(ACTIVITY_INTENT); // GO TO LOGIN ACTIVITY
            }
        } else {
            if (mCountDownTimer != null) {
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
                new LogoutProtocol().logoutExecuteAutosaveOff(mContext);
            }
        }
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
}
