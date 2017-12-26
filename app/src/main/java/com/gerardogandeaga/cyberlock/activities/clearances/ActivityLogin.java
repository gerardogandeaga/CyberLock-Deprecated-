package com.gerardogandeaga.cyberlock.activities.clearances;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.activities.core.ActivityEdit;
import com.gerardogandeaga.cyberlock.activities.core.ActivityMain;
import com.gerardogandeaga.cyberlock.crypto.CryptKeyHandler;
import com.gerardogandeaga.cyberlock.sqlite.data.RawDataPackage;
import com.gerardogandeaga.cyberlock.support.KeyChecker;
import com.gerardogandeaga.cyberlock.support.LogoutProtocol;

import static com.gerardogandeaga.cyberlock.support.Globals.CRYPT_KEY;
import static com.gerardogandeaga.cyberlock.support.Globals.DIRECTORY;
import static com.gerardogandeaga.cyberlock.support.Globals.LAST_LOGIN;
import static com.gerardogandeaga.cyberlock.support.Globals.LOGGED;
import static com.gerardogandeaga.cyberlock.support.Globals.MASTER_KEY;
import static com.gerardogandeaga.cyberlock.support.Globals.PASSCODE;
import static com.gerardogandeaga.cyberlock.support.Globals.TEMP_PIN;

public class ActivityLogin extends AppCompatActivity {
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
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        // Set fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        LogoutProtocol.ACTIVITY_INTENT = null;
        this.mSharedPreferences = getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE);

        this.mIntent = getIntent();
        this.mIsEdit = getIntent().getBooleanExtra("edit?", false);
        this.mIntent.removeExtra("edit?"); // REMOVE LAST ACTIVITY INFO

        if (!isRegistered()) {
            LogoutProtocol.ACTIVITY_INTENT = new Intent(this, ActivityRegistration.class);
            this.finish();
            this.startActivity(LogoutProtocol.ACTIVITY_INTENT);
        } else {
            setContentView(R.layout.activity_login);

            this.mEtPasscode = findViewById(R.id.etPasscode);
            Button btnLogin = findViewById(R.id.btnLogin);

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
        Intent intent;
        Bundle bundle = mIntent.getExtras();
        mIntent.removeExtra("lastDB");
        clear();

        if (mIsEdit) {
            intent = new Intent(ActivityLogin.this, ActivityEdit.class);
            intent.putExtra("isNew?", (boolean) (bundle != null ? bundle.get("isNew?") : null));
            intent.putExtra("data", (RawDataPackage) (bundle != null ? bundle.get("lastDB") : null));
        } else {
            intent = new Intent(ActivityLogin.this, ActivityMain.class);
        }

        mProgressDialog.dismiss();
        finish();
        ActivityLogin.this.startActivity(intent); // MOVE TO MAIN ACTIVITY
    }
    @SuppressLint("StaticFieldLeak")
    private void onPinCompleted() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressBar();
            }
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (new KeyChecker(mContext, mSharedPreferences).keyCompare(mPasscode)) { // check if original passcode matches the one entered
                        TEMP_PIN = mPasscode;
                        MASTER_KEY = new CryptKeyHandler(mContext).DECRYPT_KEY(mSharedPreferences.getString(CRYPT_KEY, null), TEMP_PIN);
                        System.out.println("MASTER KEY: " + MASTER_KEY);

                        LogoutProtocol.APP_LOGGED_IN = true; // APP LOGGED IN
                        getLastLoginTime();
                        loginIntent();
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