package com.gerardogandeaga.cyberlock.Supports;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Button;
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

public class Settings_ChangeShortPasscode extends AppCompatActivity {
    private Context mContext = this;
    private SharedPreferences mSharedPreferences;

    // DATA VARIABLES
    // STORED PASSCODE
    private static String mPin = "", mPinFirst = "", mPinSecond = "";
    // PASSCODE ARRAY VARIABLES
    private static boolean mIsArrayFull = false;
    private static int mIndex = -1;
    private static String[] mArray = new String[4];

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
        setContentView(R.layout.activity_passcode_set);
        ACTIVITY_INTENT = null;

        mSharedPreferences = getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE);
        //
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
    public void addToArray(Button b) {
        mArray[mIndex] = b.getText().toString();

        String s = null;
        for (int i = 0; i < mArray.length; i++)
            {
                s = mArray[i];
                if (s == null)
                    {
                        break;
                    }
            }

        if (s != null)
            {
                mIsArrayFull = true;

                if (mIsArrayFull)
                    {
                        for (int i = 0; i < mArray.length; i++)
                            {
                                mPin = mPin + mArray[i];
                            }
                        System.out.println(mPin);
                    }
            }
    }
    public void incrementIndexNumber() {
        String s;
        for (int i = 0; i < mArray.length; i++)
            {
                s = mArray[i];
                if (s == null)
                    {
                        mIndex++;
                        break;
                    }
            }
    }
    public void deleteFromArray() {
        if (mIndex != -1)
            {
                mArray[mIndex] = null;
                mIndex--;
            }
    }
    public void clear() {
        mPin = "";
        mArray = new String[mArray.length];
        mIndex = -1;
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
                final String pinFirst = mPinFirst;
                final String pinSecond = mPinSecond;

                mPinFirst = "";
                mPinSecond = "";

                if ((pinFirst.matches(pinSecond)) && (!pinFirst.matches("") || (!pinSecond.matches(""))))
                    {
                        final String pinHash; // GENERATE THE HASH PASSCODE
                        try
                            {
                                CryptKeyHandler cryptKeyHandler = new CryptKeyHandler(mContext);
                                pinHash = cryptKeyHandler.ENCRYPT_KEY(SHA256PinHash.HASH_FUNCTION(pinFirst, SHA256PinHash.GENERATE_SALT()), pinFirst);

                                mSharedPreferences.edit().putString(PASSCODE, pinHash).apply(); // ADD HASHED PASSCODE TO STORE
                                System.out.println("HASHED PASSCODE :" + pinHash);
                                mSharedPreferences.edit().putString(CRYPT_KEY,
                                        cryptKeyHandler.ENCRYPT_KEY(
                                                cryptKeyHandler.DECRYPT_KEY(mSharedPreferences.getString(CRYPT_KEY, null), TEMP_PIN), pinFirst))
                                        .apply();
                                TEMP_PIN = pinFirst;
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

                                mSharedPreferences.edit().putBoolean(IS_REGISTERED, false).apply();
                                ACTIVITY_INTENT = new Intent(mContext, Settings.class);
                                runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run() {
                                        Toast.makeText(mContext, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                mContext.startActivity(ACTIVITY_INTENT);
                            }
                    } else
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "Please Try Again", Toast.LENGTH_SHORT).show();
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

                mSharedPreferences.edit().putBoolean(IS_REGISTERED, false).apply();
                mProgressDialog.dismiss();
            }
        }.execute();
    }
    public void storePins() {
        if (mPinFirst.matches("") && mPinSecond.matches(""))
            {
                mPinFirst = mPin;
                clear();
            } else if (!mPinFirst.matches("") && mPinSecond.matches(""))
            {
                mPinSecond = mPin;
                clear();

                System.out.println("pin 1 " + mPinFirst);
                System.out.println("pin 2 " + mPinSecond);
                onPinsCompleted();
            }
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
