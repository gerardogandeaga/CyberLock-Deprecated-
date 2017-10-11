package com.gerardogandeaga.cyberlock.Activitys.Activities.Menus;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LoginActivity;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Main.MainActivity;
import com.gerardogandeaga.cyberlock.Crypto.CryptKeyHandler;
import com.gerardogandeaga.cyberlock.Crypto.SHA256PinHash;
import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.Supports.Globals;
import com.gerardogandeaga.cyberlock.Supports.LogoutProtocol;

import static com.gerardogandeaga.cyberlock.R.id.input1;
import static com.gerardogandeaga.cyberlock.R.id.input2;
import static com.gerardogandeaga.cyberlock.R.id.input3;
import static com.gerardogandeaga.cyberlock.R.id.input4;
import static com.gerardogandeaga.cyberlock.Supports.Globals.COMPLEXPASSCODE;
import static com.gerardogandeaga.cyberlock.Supports.Globals.CRYPT_KEY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.DIRECTORY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.MASTER_KEY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.PIN;
import static com.gerardogandeaga.cyberlock.Supports.Globals.TEMP_PIN;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.APP_LOGGED_IN;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.mCountDownIsFinished;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.mCountDownTimer;

public class Settings_ResetShortPasscode extends AppCompatActivity implements View.OnClickListener
{
    private Context mContext = this;
    private SharedPreferences mSharedPreferences;

    // DATA VARIABLES
    // STORED PIN
    private static String mPin = "", mPinFirst = "", mPinSecond = "";
    // PIN ARRAY VARIABLES
    private static boolean mIsArrayFull = false;
    private static int mIndex = -1;
    private static String[] mArray = new String[4];

    // WIDGETS
    private Button mBtn0, mBtn1, mBtn2, mBtn3, mBtn4, mBtn5, mBtn6, mBtn7, mBtn8, mBtn9;
    private ProgressDialog mProgressDialog;
    private RadioButton mInput1, mInput2, mInput3, mInput4;
    private TextView mTextDisplay;


    // INITIAL ON CREATE METHODS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Globals.COLORSCHEME(this);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        setupLayout();
    }
    private void setupLayout() {
        setContentView(R.layout.activity_loginpin);
        ACTIVITY_INTENT = null;

        mSharedPreferences = getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE);
        //
        mTextDisplay = (TextView) findViewById(R.id.tvTextDisplay);

        this.mBtn0 = (Button) findViewById(R.id.btn0);
        this.mBtn1 = (Button) findViewById(R.id.btn1);
        this.mBtn2 = (Button) findViewById(R.id.btn2);
        this.mBtn3 = (Button) findViewById(R.id.btn3);
        this.mBtn4 = (Button) findViewById(R.id.btn4);
        this.mBtn5 = (Button) findViewById(R.id.btn5);
        this.mBtn6 = (Button) findViewById(R.id.btn6);
        this.mBtn7 = (Button) findViewById(R.id.btn7);
        this.mBtn8 = (Button) findViewById(R.id.btn8);
        this.mBtn9 = (Button) findViewById(R.id.btn9);
        ImageButton btnBackspace = (ImageButton) findViewById(R.id.btnBACKSPACE);

        this.mInput1 = (RadioButton) findViewById(input1);
        this.mInput2 = (RadioButton) findViewById(input2);
        this.mInput3 = (RadioButton) findViewById(input3);
        this.mInput4 = (RadioButton) findViewById(input4);

        this.mInput1.setClickable(false);
        this.mInput2.setClickable(false);
        this.mInput3.setClickable(false);
        this.mInput4.setClickable(false);

        this.mBtn0.setOnClickListener(this);
        this.mBtn1.setOnClickListener(this);
        this.mBtn2.setOnClickListener(this);
        this.mBtn3.setOnClickListener(this);
        this.mBtn4.setOnClickListener(this);
        this.mBtn5.setOnClickListener(this);
        this.mBtn6.setOnClickListener(this);
        this.mBtn7.setOnClickListener(this);
        this.mBtn8.setOnClickListener(this);
        this.mBtn9.setOnClickListener(this);
        btnBackspace.setOnClickListener(this);

        this.mTextDisplay.setText(R.string.NewPin);
    }
    private void progressBar() {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("Verifying...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }
    // -------------------------

    // ON CLICK
    @Override
    public void onClick(View v) {
        if (v.getId() != R.id.btnBACKSPACE)
            {
                incrementIndexNumber();
                switch (v.getId())
                    {
                        case R.id.btn0:
                            addToArray(mBtn0);
                            break;
                        case R.id.btn1:
                            addToArray(mBtn1);
                            break;
                        case R.id.btn2:
                            addToArray(mBtn2);
                            break;
                        case R.id.btn3:
                            addToArray(mBtn3);
                            break;
                        case R.id.btn4:
                            addToArray(mBtn4);
                            break;
                        case R.id.btn5:
                            addToArray(mBtn5);
                            break;
                        case R.id.btn6:
                            addToArray(mBtn6);
                            break;
                        case R.id.btn7:
                            addToArray(mBtn7);
                            break;
                        case R.id.btn8:
                            addToArray(mBtn8);
                            break;
                        case R.id.btn9:
                            addToArray(mBtn9);
                            break;
                    }
            } else
            {
                deleteFromArray();
            }

        if (mPin.length() == 4)
            {
                storePins();
            }

        if (mArray[0] != null)
            {
                mInput1.setChecked(true);
            } else
            {
                mInput1.setChecked(false);
            }
        if (mArray[1] != null)
            {
                mInput2.setChecked(true);
            } else
            {
                mInput2.setChecked(false);
            }
        if (mArray[2] != null)
            {
                mInput3.setChecked(true);
            } else
            {
                mInput3.setChecked(false);
            }
        if (mArray[3] != null)
            {
                mInput4.setChecked(true);
            } else
            {
                mInput4.setChecked(false);
            }
    }
    // --------

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

    // PIN REGISTRATION
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
                        final String pinHash; // GENERATE THE HASH PIN
                        try
                            {
                                CryptKeyHandler cryptKeyHandler = new CryptKeyHandler(mContext);
                                pinHash = cryptKeyHandler.ENCRYPT_KEY(SHA256PinHash.HASH_FUNCTION(pinFirst, SHA256PinHash.GENERATE_SALT()), pinFirst);

                                mSharedPreferences.edit().putString(PIN, pinHash).apply(); // ADD HASHED PIN TO STORE
                                System.out.println("HASHED PIN :" + pinHash);
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

                                mSharedPreferences.edit().putBoolean(COMPLEXPASSCODE, false).apply();
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
                                mTextDisplay.setText(R.string.NewPin);
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

                mSharedPreferences.edit().putBoolean(COMPLEXPASSCODE, false).apply();
                mProgressDialog.dismiss();
            }
        }.execute();
    }
    public void storePins() {
        if (mPinFirst.matches("") && mPinSecond.matches(""))
            {
                mPinFirst = mPin;
                mTextDisplay.setText(R.string.ConfirmPin);
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
