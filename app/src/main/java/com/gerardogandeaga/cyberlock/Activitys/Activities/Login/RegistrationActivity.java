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
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.gerardogandeaga.cyberlock.Encryption.CryptKeyHandler;
import com.gerardogandeaga.cyberlock.Encryption.SHA256PinHash;
import com.gerardogandeaga.cyberlock.R;

import static com.gerardogandeaga.cyberlock.R.id.input1;
import static com.gerardogandeaga.cyberlock.R.id.input2;
import static com.gerardogandeaga.cyberlock.R.id.input3;
import static com.gerardogandeaga.cyberlock.R.id.input4;
import static com.gerardogandeaga.cyberlock.Supports.Globals.AUTOSAVE;
import static com.gerardogandeaga.cyberlock.Supports.Globals.COMPLEXPASSCODE;
import static com.gerardogandeaga.cyberlock.Supports.Globals.CRYPT_KEY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.DIRECTORY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.ENCRYPTION_ALGO;
import static com.gerardogandeaga.cyberlock.Supports.Globals.FLAGS;
import static com.gerardogandeaga.cyberlock.Supports.Globals.PIN;

public class RegistrationActivity extends AppCompatActivity implements  View.OnClickListener
{
    // STORED PIN
    private static String mPin = "", mPinFirst = "", mPinSecond = "";

    private SharedPreferences mSharedPreferences;
    // PIN ARRAY VARIABLES
    private static boolean mIsArrayFull = false;
    private static int mIndex = -1;
    private static String[] mArray = new String[4];
    // WIDGETS
    private TextView mTextDisplay;
    private Button m0, m1, m2, m3, m4, m5, m6, m7, m8, m9;
    private RadioButton mInput1, mInput2, mInput3, mInput4;
    private ProgressDialog mProgressDialog;

    private Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setpin);
        LogoutProtocol.ACTIVITY_INTENT = null;

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }

        mSharedPreferences = getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE);

        if (mSharedPreferences.getString(PIN, null) != null || mSharedPreferences.getString(CRYPT_KEY, null) != null)
        {
            LogoutProtocol.ACTIVITY_INTENT = new Intent(this, LoginActivity.class);
            this.finish();
            this.startActivity(LogoutProtocol.ACTIVITY_INTENT);
        }
        else
        {
            mTextDisplay = (TextView) findViewById(R.id.tvInstructions);

            this.m0 = (Button) findViewById(R.id.btn0);
            this.m1 = (Button) findViewById(R.id.btn1);
            this.m2 = (Button) findViewById(R.id.btn2);
            this.m3 = (Button) findViewById(R.id.btn3);
            this.m4 = (Button) findViewById(R.id.btn4);
            this.m5 = (Button) findViewById(R.id.btn5);
            this.m6 = (Button) findViewById(R.id.btn6);
            this.m7 = (Button) findViewById(R.id.btn7);
            this.m8 = (Button) findViewById(R.id.btn8);
            this.m9 = (Button) findViewById(R.id.btn9);
            ImageButton btnBackspace = (ImageButton) findViewById(R.id.btnBACKSPACE);

            this.mInput1 = (RadioButton) findViewById(input1);
            this.mInput2 = (RadioButton) findViewById(input2);
            this.mInput3 = (RadioButton) findViewById(input3);
            this.mInput4 = (RadioButton) findViewById(input4);

            this.mInput1.setClickable(false);
            this.mInput2.setClickable(false);
            this.mInput3.setClickable(false);
            this.mInput4.setClickable(false);

            this.m0.setOnClickListener(this);
            this.m1.setOnClickListener(this);
            this.m2.setOnClickListener(this);
            this.m3.setOnClickListener(this);
            this.m4.setOnClickListener(this);
            this.m5.setOnClickListener(this);
            this.m6.setOnClickListener(this);
            this.m7.setOnClickListener(this);
            this.m8.setOnClickListener(this);
            this.m9.setOnClickListener(this);
            btnBackspace.setOnClickListener(this);

            this.mTextDisplay.setText(R.string.NewPin);
        }
    }

    // PIN KEYBOARD AND REGISTRATION
    @Override
    public void onClick(View v)
    {
        if (v.getId() != R.id.btnBACKSPACE)
        {
            incrementIndexNumber();
            switch (v.getId())
            {
                case R.id.btn0: addToArray(m0); break;
                case R.id.btn1: addToArray(m1); break;
                case R.id.btn2: addToArray(m2); break;
                case R.id.btn3: addToArray(m3); break;
                case R.id.btn4: addToArray(m4); break;
                case R.id.btn5: addToArray(m5); break;
                case R.id.btn6: addToArray(m6); break;
                case R.id.btn7: addToArray(m7); break;
                case R.id.btn8: addToArray(m8); break;
                case R.id.btn9: addToArray(m9); break;
            }
        } else {
            deleteFromArray();
        }

        if (mPin.length() == 4) { storePins(); }

        if (mArray[0] != null) { mInput1.setChecked(true); } else { mInput1.setChecked(false); }
        if (mArray[1] != null) { mInput2.setChecked(true); } else { mInput2.setChecked(false); }
        if (mArray[2] != null) { mInput3.setChecked(true); } else { mInput3.setChecked(false); }
        if (mArray[3] != null) { mInput4.setChecked(true); } else { mInput4.setChecked(false); }
    }

    private void storePins()
    {
        if (mPinFirst.matches("") && mPinSecond.matches("")) {
            mPinFirst = mPin;
            mTextDisplay.setText(R.string.ConfirmPin);
            clear();
        } else if (!mPinFirst.matches("") && mPinSecond.matches("")) {
            mPinSecond = mPin;
            clear();

            System.out.println("pin 1 " + mPinFirst);
            System.out.println("pin 2 " + mPinSecond);

            onPinCompleted();
        }
    }

    private void incrementIndexNumber()
    {
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

    private void addToArray(Button b)
    {
        mArray[mIndex] = b.getText().toString();

        String s = null;
        for (int i = 0; i < mArray.length; i++)
        {
            s = mArray[i];
            if (s == null) { break; }
        }

        if (s != null)
        {
            mIsArrayFull = true;

            if (mIsArrayFull)
            {
                for (int i = 0; i < mArray.length; i++) { mPin = mPin + mArray[i]; }
                System.out.println(mPin);
            }
        }
    }

    private void deleteFromArray()
    {
        if (mIndex != -1)
        {
            mArray[mIndex] = null;
            mIndex--;
        }
    }

    private void clear()
    {
        mPin = "";
        mArray = new String[mArray.length];
        mIndex = -1;
    }

    private void setUpSharedPreferences()
    {
        mSharedPreferences.edit().putString(ENCRYPTION_ALGO, "AES").apply();
        mSharedPreferences.edit().putBoolean(COMPLEXPASSCODE, false).apply();
    }
    // -----------------------------

    private void progressBar()
    {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Registering...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    private void onPinCompleted() // ASYNC TASK
    {
        new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();

                progressBar();
            }

            @Override
            protected Void doInBackground(Void... params)
            {

                final String pinFirst = mPinFirst;
                final String pinSecond = mPinSecond;

                mPinFirst = "";
                mPinSecond = "";

                if ((pinFirst.matches(pinSecond)) && (!pinFirst.matches("") || (!pinSecond.matches(""))))
                {
                    final String passwordHashAndEcnrypted; // GENERATE THE HASH PIN
                    try
                    {
                        // INITIAL ENCRYPTION ALGORITHM
                        setUpSharedPreferences();

                        CryptKeyHandler keyHandler = new CryptKeyHandler(mContext); // START THE KEY HANDLER

                        // PIN AND ENCRYPTION PROCESSES
                        String pinHash = SHA256PinHash.hashFunction(pinFirst, SHA256PinHash.generateSalt());
                        passwordHashAndEcnrypted = keyHandler.ENCRYPTKEY(pinHash, pinFirst);

                        mSharedPreferences.edit().putString(PIN, passwordHashAndEcnrypted).apply(); // ADD HASHED PIN TO STORE
                        System.out.println("HASHED PIN :" + passwordHashAndEcnrypted);

                        byte[] KEY_Byte = keyHandler.BYTE_KEY_GENERATE(); // GENERATE A NEW BYTE ARRAY AS A SYMMETRIC KEY
                        byte[] ENC_DEC_KEY_ByteVal = keyHandler.KEY_GENERATE(KEY_Byte);

                        System.out.println("REGISTED KEY VAL :" + Base64.encodeToString(ENC_DEC_KEY_ByteVal, FLAGS));
                        System.out.println("REGISTED KEY SIZE :" + ENC_DEC_KEY_ByteVal.length);

                        String ENC_DEC_KEY_StringVal = keyHandler.ENCRYPTKEY(Base64.encodeToString(ENC_DEC_KEY_ByteVal, FLAGS), pinFirst); // ENCRYPT BYTES TO STRING

                        mSharedPreferences.edit().putString(CRYPT_KEY, ENC_DEC_KEY_StringVal).apply(); // STORE THE KEY IN THE KEY STORE
                        System.out.println("REGISTER ENCRYPTED KEY VAL :" + ENC_DEC_KEY_StringVal);

                        mPinFirst = null;
                        mPinSecond = null;
                        // -----------------------------

                        // SETTINGS FEATURES
                        mSharedPreferences.edit().putBoolean(AUTOSAVE, false).apply();

                        Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
                        RegistrationActivity.this.startActivity(i);

                    } catch (Exception e) {
                        e.printStackTrace();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "Something Went Wrong...", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {

                    System.out.println("REGISTRATION SUCCESSFUL!");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "Please Try Again", Toast.LENGTH_SHORT).show();
                            mTextDisplay.setText("Please input a new pin");
                        }
                    });
                    }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid)
            {
                super.onPostExecute(aVoid);

                clear();
                mProgressDialog.dismiss();
            }
        }.execute();
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        if (mSharedPreferences.getString(PIN, null) != null || mSharedPreferences.getString(CRYPT_KEY, null) != null)
        {
            LogoutProtocol.ACTIVITY_INTENT = new Intent(this, LoginActivity.class);
            this.finish();
            this.startActivity(LogoutProtocol.ACTIVITY_INTENT);
        }
    }
}