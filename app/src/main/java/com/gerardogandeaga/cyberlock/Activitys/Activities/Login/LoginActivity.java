package com.gerardogandeaga.cyberlock.Activitys.Activities.Login;

import android.annotation.SuppressLint;
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
import android.widget.TextView;
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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Context mContext = this;
    private SharedPreferences mSharedPreferences;

    // Activity intent
    private Intent mIntent;
    private boolean mIsEdit;
    private StringBuilder mStringBuilder = new StringBuilder();
    // Widgets
    private TextView mTvPasscode;
    private ProgressDialog mProgressDialog;

    // Initial on create methods
    @Override protected void onCreate(Bundle savedInstanceState) {
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
            setContentView(R.layout.test);

            mTvPasscode = (TextView) findViewById(R.id.tvPasscode);
            findViewById(R.id.btn0).setOnClickListener(this);findViewById(R.id.btn5).setOnClickListener(this);
            findViewById(R.id.btn1).setOnClickListener(this);findViewById(R.id.btn6).setOnClickListener(this);
            findViewById(R.id.btn2).setOnClickListener(this);findViewById(R.id.btn7).setOnClickListener(this);
            findViewById(R.id.btn3).setOnClickListener(this);findViewById(R.id.btn8).setOnClickListener(this);
            findViewById(R.id.btn4).setOnClickListener(this);findViewById(R.id.btn9).setOnClickListener(this);
            findViewById(R.id.btnDelete).setOnClickListener(this);findViewById(R.id.btnEnter).setOnClickListener(this);
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

    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn0: buildString("0"); break; case R.id.btn5: buildString("5"); break;
            case R.id.btn1: buildString("1"); break; case R.id.btn6: buildString("6"); break;
            case R.id.btn2: buildString("2"); break; case R.id.btn7: buildString("7"); break;
            case R.id.btn3: buildString("3"); break; case R.id.btn8: buildString("8"); break;
            case R.id.btn4: buildString("4"); break; case R.id.btn9: buildString("9"); break;
            case R.id.btnDelete: buildString("Del"); break; case R.id.btnEnter: onPinCompleted(); break;
        }
    }
    private void buildString(String s) {
        mTvPasscode.setText("");
        if (!s.matches("Del")) {
            if (mStringBuilder.length() < 16) {
                mStringBuilder.append(s);
            } else {
                Toast.makeText(this, "Passcode can not be longer than 16 characters" , Toast.LENGTH_SHORT).show();
            }
        } else {
            if (mStringBuilder.length() > 0) {
                mStringBuilder.deleteCharAt(mStringBuilder.length() - 1);
            }
        }
        for (int i = 0; i < mStringBuilder.length(); i++) {
            mTvPasscode.append("*");
        }
    }

    // WHEN LOGIN CLICK IS REGISTERED
    public void clear() {
        mStringBuilder = new StringBuilder();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTvPasscode.setText("");
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
    @SuppressLint("StaticFieldLeak") private void onPinCompleted() {
        if (mStringBuilder.length() == 0) {
            Toast.makeText(mContext, "No Passcode detected", Toast.LENGTH_SHORT).show();
            clear();
        } else {
            new AsyncTask<Void, Void, Void>() {
                @Override protected void onPreExecute() {
                    super.onPreExecute();
                    progressBar();
                }
                @Override protected Void doInBackground(Void... params) {
                    try {
                        String s = mStringBuilder.toString();
                        final String decryptedPulledPin = new CryptKeyHandler(mContext)
                                .DECRYPT_KEY(mSharedPreferences.getString(PASSCODE, null), s);
                        final String loginPinHash = SHA256PinHash
                                .HASH_FUNCTION(s, Arrays.copyOfRange(Base64.decode(decryptedPulledPin, FLAGS), 0, 128));
//                    System.out.println("LOGIN INPUT: " + loginPinHash);

//                    System.out.println("CACHED HASH: " + decryptedPulledPin);
                        if (decryptedPulledPin.equals(loginPinHash)) /// TEST PERIODICALLY INPUTTED PASSCODE AGAINST CACHED PASSCODE
                        {
                            TEMP_PIN = s;
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
                @Override protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    clear();
                    mProgressDialog.dismiss();
                }
            }.execute();
        }
    }
    // ------------------------------

    private void getLastLoginTime() {
        LOGGED = mSharedPreferences.getString(LAST_LOGIN, "");
    }

    // THIS IS THE START OF THE SCRIPT FOR *** THE "TO LOGIN FUNCTION" THIS DETECTS THE ON PRESSED, START, TABS AND HOME BUTTONS IN ORDER TO INITIALIZE SECURITY "FAIL-SAFE"
    @Override protected void onStart() {
        super.onStart();

        LogoutProtocol.APP_LOGGED_IN = false; // APP NOT LOGGED IN
        System.out.println("MASTER KEY IS EQUAL TO " + MASTER_KEY);
    } // <--- ON *LOGIN REQUIREMENT* START -- *LOGIN = FALSE*
    @Override public void onBackPressed() {
        super.onBackPressed();

        Intent homeIntent = new Intent(Intent.ACTION_MAIN); // (NEXT LINES) SCRIPT TO SEND TO THE OS HOME SCREEN
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    } // <--- BACK PRESSED *HOME*
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------

    // QUARANTINED FUNCTIONS
    private boolean isRegistered() {
        final String passcode = mSharedPreferences.getString(PASSCODE, null);
        final String key = mSharedPreferences.getString(CRYPT_KEY, null);

        return (passcode != null && key != null);
    }
}