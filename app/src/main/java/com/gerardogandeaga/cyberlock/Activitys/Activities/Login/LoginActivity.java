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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.gerardogandeaga.cyberlock.Activitys.Activities.Main.MainActivity;
import com.gerardogandeaga.cyberlock.Encryption.AESKeyHandler;
import com.gerardogandeaga.cyberlock.Encryption.SHA256PinHash;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.LoginInfo.LoginInfo;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.LoginInfo.LoginInfoEditActivity;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.PaymentInfo.PaymentInfo;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.PaymentInfo.PaymentInfoEditActivity;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.PrivateMemo.Memo;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.PrivateMemo.MemoEditActivity;
import com.gerardogandeaga.cyberlock.R;

import java.util.Arrays;

import static com.gerardogandeaga.cyberlock.R.id.input1;
import static com.gerardogandeaga.cyberlock.R.id.input2;
import static com.gerardogandeaga.cyberlock.R.id.input3;
import static com.gerardogandeaga.cyberlock.R.id.input4;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener
{
    // STORED PIN
    private static String mPin = "";
    private static final int FLAGS = Base64.DEFAULT;
    private static final String PIN = "PIN", KEY = "KEY", TEMP_PIN = "TEMP_PIN";
    private SharedPreferences mSharedPreferences;
    // INTENT
    private Intent mIntent;
    private String mLastActivity;
    // PIN ARRAY VARIABLES
    private static boolean mArrayFull = false;
    private static int mIndex = -1;
    private static String[] mArray = new String[4];
    // WIDGETS
    private Button mBtn0, mBtn1, mBtn2, mBtn3, mBtn4, mBtn5, mBtn6, mBtn7, mBtn8, mBtn9;
    private RadioButton mInput1, mInput2, mInput3, mInput4;
    private ProgressDialog mProgressDialog;

    private Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LogoutProtocol.ACTIVITY_INTENT = null;

        mSharedPreferences = getSharedPreferences("com.gerardogandeaga.cyberlock", Context.MODE_PRIVATE);

        mIntent = getIntent();
        mLastActivity = getIntent().getStringExtra("lastActivity");
        mIntent.removeExtra("lastActivity"); // REMOVE LAST ACTIVITY INFO

        if (mSharedPreferences.getString(PIN, null) == null || mSharedPreferences.getString(KEY, null) == null)
        {
            LogoutProtocol.ACTIVITY_INTENT = new Intent(this, RegistrationActivity.class);
            this.finish();
            this.startActivity(LogoutProtocol.ACTIVITY_INTENT);
        }
        else // CREATE ALL THE WIDGETS
        {
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
            ImageButton btnBackspace = (ImageButton) findViewById(R.id.btnBackspace);

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
        }
    }

    // PIN KEYBOARD AND REGISTRATION
    @Override
    public void onClick(View v)
    {
        if (v.getId() != R.id.btnBackspace)
        {
            incrementIndexNumber();
            switch (v.getId())
            {
                case R.id.btn0: addToArray(mBtn0); break;
                case R.id.btn1: addToArray(mBtn1); break;
                case R.id.btn2: addToArray(mBtn2); break;
                case R.id.btn3: addToArray(mBtn3); break;
                case R.id.btn4: addToArray(mBtn4); break;
                case R.id.btn5: addToArray(mBtn5); break;
                case R.id.btn6: addToArray(mBtn6); break;
                case R.id.btn7: addToArray(mBtn7); break;
                case R.id.btn8: addToArray(mBtn8); break;
                case R.id.btn9: addToArray(mBtn9); break;
            }
        } else {
            deleteFromArray();
        }

        if (mPin.length() == 4) { onPinCompleted(); }

        if (mArray[0] != null) { mInput1.setChecked(true); } else { mInput1.setChecked(false); }
        if (mArray[1] != null) { mInput2.setChecked(true); } else { mInput2.setChecked(false); }
        if (mArray[2] != null) { mInput3.setChecked(true); } else { mInput3.setChecked(false); }
        if (mArray[3] != null) { mInput4.setChecked(true); } else { mInput4.setChecked(false); }
    }

    public void incrementIndexNumber()
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

    public void addToArray(Button b)
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
            mArrayFull = true;

            if (mArrayFull)
            {
                for (int i = 0; i < mArray.length; i++) { mPin = mPin + mArray[i]; }
                System.out.println(mPin);
            }
        }
    }

    public void deleteFromArray()
    {
        if (mIndex != -1)
        {
            mArray[mIndex] = null;
            mIndex--;
        }
    }

    public void clear()
    {
        mPin = "";
        mArray = new String[mArray.length];
        mIndex = -1;
    }
    // -----------------------------

    private void progressBar()
    {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("Verifying...");
        mProgressDialog.setProgressStyle(mProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    private void loginIntent()
    {
        Intent i;
        Bundle bundle = mIntent.getExtras();
        mIntent.removeExtra("lastDatabase");

        if (mLastActivity != null)
        {
            switch (mLastActivity)
            {
                case ("MEMO_EDIT"):
                    i = new Intent(LoginActivity.this, MemoEditActivity.class);
                    i.putExtra("MEMO", (Memo) bundle.get("lastDatabase"));
                    break;

                case ("PAYMENTINFO_EDIT"):
                    i = new Intent(LoginActivity.this, PaymentInfoEditActivity.class);
                    i.putExtra("PAYMENTINFO", (PaymentInfo) bundle.get("lastDatabase"));
                    break;

                case ("LOGININFO_EDIT"):
                    i = new Intent(LoginActivity.this, LoginInfoEditActivity.class);
                    i.putExtra("LOGININFO", (LoginInfo) bundle.get("lastDatabase"));
                    break;

                default:
                    i = new Intent(LoginActivity.this, MainActivity.class);
                    break;
            }

            mProgressDialog.dismiss();
            LoginActivity.this.startActivity(i); // MOVE TO MAIN ACTIVITY
        }
        else
        {
            i = new Intent(LoginActivity.this, MainActivity.class);
            LoginActivity.this.startActivity(i); // MOVE TO MAIN ACTIVITY
        }
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
                try // TRY LOGIN MECHANICS
                {
                    final String decryptedPulledPin = AESKeyHandler.DECRYPTKEY(mSharedPreferences.getString(PIN, null), mPin);
                    final String loginPinHash = SHA256PinHash.hashFunction(mPin, Arrays.copyOfRange(Base64.decode(decryptedPulledPin, FLAGS), 0, 128));

                    System.out.println("LOGIN INPUT: " + loginPinHash);
                    System.out.println("CACHED HASH: " + decryptedPulledPin);

                    if (decryptedPulledPin.equals(loginPinHash)) /// TEST PERIODICALLY INPUTED PIN AGAINST CACHED PIN
                    {
                        mSharedPreferences.edit().putString(TEMP_PIN, mPin).apply();

                        LogoutProtocol.APP_LOGGED_IN = true; // APP LOGGED IN

                        loginIntent();

                        mPin = null;
                        System.gc();
                    } else
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "Wrong code, please retry", Toast.LENGTH_SHORT).show(); // DISPLAY THE REJECT MESSAGE
                            }
                        });
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "Something Went Wrong...", Toast.LENGTH_SHORT).show(); // SOMETHING WENT WRONG WITH DATA COMPARISONS
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


    // THIS IS THE START OF THE SCRIPT FOR *** THE "TO LOGIN FUNCTION" THIS DETECTS THE ON PRESSED, START, TABS AND HOME BUTTONS IN ORDER TO INITIALIZE SECURITY "FAIL-SAFE"
    @Override
    protected void onStart() // <--- ON *LOGIN REQUIREMENT* START -- *LOGIN = FALSE*
    {
        super.onStart();

        LogoutProtocol.APP_LOGGED_IN = false; // APP NOT LOGGED IN
    }

    @Override
    public void onBackPressed() // <--- BACK PRESSED *HOME*
    {
        super.onBackPressed();

        Intent homeIntent = new Intent(Intent.ACTION_MAIN); // (NEXT LINES) SCRIPT TO SEND TO THE OS HOME SCREEN
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);                         // --------------------------------------------------
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
}