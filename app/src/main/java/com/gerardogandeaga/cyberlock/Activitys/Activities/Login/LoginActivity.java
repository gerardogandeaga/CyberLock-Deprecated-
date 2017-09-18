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

import com.gerardogandeaga.cyberlock.Activitys.Activities.Edits.LoginInfoEditActivity;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Edits.MemoEditActivity;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Edits.PaymentInfoEditActivity;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Main.MainActivity;
import com.gerardogandeaga.cyberlock.Encryption.CryptKeyHandler;
import com.gerardogandeaga.cyberlock.Encryption.SHA256PinHash;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.Database.Data;
import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.Supports.Globals;
import com.gerardogandeaga.cyberlock.Supports.LogoutProtocol;

import java.util.Arrays;

import static com.gerardogandeaga.cyberlock.R.id.btnBACKSPACE;
import static com.gerardogandeaga.cyberlock.R.id.input1;
import static com.gerardogandeaga.cyberlock.R.id.input2;
import static com.gerardogandeaga.cyberlock.R.id.input3;
import static com.gerardogandeaga.cyberlock.R.id.input4;
import static com.gerardogandeaga.cyberlock.Supports.Globals.COMPLEXPASSCODE;
import static com.gerardogandeaga.cyberlock.Supports.Globals.CRYPT_KEY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.DIRECTORY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.FLAGS;
import static com.gerardogandeaga.cyberlock.Supports.Globals.MASTER_KEY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.PIN;
import static com.gerardogandeaga.cyberlock.Supports.Globals.TEMP_PIN;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener
{
    private Context mContext = this;
    private SharedPreferences mSharedPreferences;

    // DATA VARIABLES
    // ACTIVITY INTENT
    private Intent mIntent;
    private String mLastActivity;
    // SIMPLE PIN ARRAY VARIABLES
    private static boolean mIsArrayFull = false;
    private static int mIndex = -1;
    private static String[] mArray = new String[4];
    private static String mPin = "";
    // COMPLEX PASSCODE VARIABLES
    private boolean mIsComplexCode;
    private static String mPasscodeString = "";
    private static String mPasscodeMark = "";

    // WIDGETS
    private ProgressDialog mProgressDialog;
    //SIMPLES DIGIT WIDGETS
    private Button m0, m1, m2, m3, m4, m5, m6, m7, m8, m9;
    private RadioButton mInput1, mInput2, mInput3, mInput4;
    // COMPLEX WIDGETS
    private Button mQ, mW, mE, mR, mT, mY, mU, mI, mO, mP, mA, mS, mD, mF, mG, mH, mJ, mK, mL, mZ, mX, mC, mV, mB, mN, mM;
    private Button mSLASH, mDOT, mCOMMA;
    private Button mBtnLOGINREGISTER;
    private ImageButton mSPACEBAR;
    private ImageButton mBtnCLEAR;
    private TextView mTvPasscodeDisplay;

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
        mIsComplexCode = mSharedPreferences.getBoolean(COMPLEXPASSCODE, false);

        mIntent = getIntent();
        mLastActivity = getIntent().getStringExtra("lastActivity");
        mIntent.removeExtra("lastActivity"); // REMOVE LAST ACTIVITY INFO

        if (mSharedPreferences.getString(PIN, null) == null || mSharedPreferences.getString(CRYPT_KEY, null) == null)
            {
                LogoutProtocol.ACTIVITY_INTENT = new Intent(this, RegistrationActivity.class);
                this.finish();
                this.startActivity(LogoutProtocol.ACTIVITY_INTENT);
                System.out.println("CALLED!");
            } else if (!mIsComplexCode) // CREATE ALL THE WIDGETS
            {
                setContentView(R.layout.activity_loginpin);

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
            } else
            {
                setContentView(R.layout.activity_loginpasscode);

                this.mTvPasscodeDisplay = (TextView) findViewById(R.id.tvPasscode);

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
                this.mQ = (Button) findViewById(R.id.btnQ);
                this.mW = (Button) findViewById(R.id.btnW);
                this.mE = (Button) findViewById(R.id.btnE);
                this.mR = (Button) findViewById(R.id.btnR);
                this.mT = (Button) findViewById(R.id.btnT);
                this.mY = (Button) findViewById(R.id.btnY);
                this.mU = (Button) findViewById(R.id.btnU);
                this.mI = (Button) findViewById(R.id.btnI);
                this.mO = (Button) findViewById(R.id.btnO);
                this.mP = (Button) findViewById(R.id.btnP);
                this.mA = (Button) findViewById(R.id.btnA);
                this.mS = (Button) findViewById(R.id.btnS);
                this.mD = (Button) findViewById(R.id.btnD);
                this.mF = (Button) findViewById(R.id.btnF);
                this.mG = (Button) findViewById(R.id.btnG);
                this.mH = (Button) findViewById(R.id.btnH);
                this.mJ = (Button) findViewById(R.id.btnJ);
                this.mK = (Button) findViewById(R.id.btnK);
                this.mL = (Button) findViewById(R.id.btnL);
                this.mZ = (Button) findViewById(R.id.btnZ);
                this.mX = (Button) findViewById(R.id.btnX);
                this.mC = (Button) findViewById(R.id.btnC);
                this.mV = (Button) findViewById(R.id.btnV);
                this.mB = (Button) findViewById(R.id.btnB);
                this.mN = (Button) findViewById(R.id.btnN);
                this.mM = (Button) findViewById(R.id.btnM);
                this.mDOT = (Button) findViewById(R.id.btnDOT);
                this.mSLASH = (Button) findViewById(R.id.btnSLASH);
                this.mCOMMA = (Button) findViewById(R.id.btnCOMMA);
                this.mSPACEBAR = (ImageButton) findViewById(R.id.btnSPACEBAR);
                ImageButton btnBackspace = (ImageButton) findViewById(R.id.btnBACKSPACE);

                this.mBtnLOGINREGISTER = (Button) findViewById(R.id.btnLOGINREGISTER);
                this.mBtnCLEAR = (ImageButton) findViewById(R.id.btnCLEAR);

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
                this.mQ.setOnClickListener(this);
                this.mW.setOnClickListener(this);
                this.mE.setOnClickListener(this);
                this.mR.setOnClickListener(this);
                this.mT.setOnClickListener(this);
                this.mY.setOnClickListener(this);
                this.mU.setOnClickListener(this);
                this.mI.setOnClickListener(this);
                this.mO.setOnClickListener(this);
                this.mP.setOnClickListener(this);
                this.mA.setOnClickListener(this);
                this.mS.setOnClickListener(this);
                this.mD.setOnClickListener(this);
                this.mF.setOnClickListener(this);
                this.mG.setOnClickListener(this);
                this.mH.setOnClickListener(this);
                this.mJ.setOnClickListener(this);
                this.mK.setOnClickListener(this);
                this.mL.setOnClickListener(this);
                this.mZ.setOnClickListener(this);
                this.mX.setOnClickListener(this);
                this.mC.setOnClickListener(this);
                this.mV.setOnClickListener(this);
                this.mB.setOnClickListener(this);
                this.mN.setOnClickListener(this);
                this.mM.setOnClickListener(this);
                this.mDOT.setOnClickListener(this);
                this.mSLASH.setOnClickListener(this);
                this.mCOMMA.setOnClickListener(this);
                this.mSPACEBAR.setOnClickListener(this);
                btnBackspace.setOnClickListener(this);

                this.mBtnLOGINREGISTER.setOnClickListener(this);
                this.mBtnCLEAR.setOnClickListener(this);
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

    // ON CLICK
    @Override
    public void onClick(View v) {
        if (!mIsComplexCode)
            {
                onClickSimple(v);
            } else
            {
                onClickComplex(v);
            }
    }
    // --------

    // WHEN LOGIN CLICK IS REGISTERED
    private void loginIntent() {
        Intent i;
        Bundle bundle = mIntent.getExtras();
        mIntent.removeExtra("lastDatabase");
        clear();

        if (mLastActivity != null)
            {
                switch (mLastActivity)
                    {
                        case ("MEMO_EDIT"):
                            i = new Intent(LoginActivity.this, MemoEditActivity.class);
                            i.putExtra("MEMO", (Data) bundle.get("lastDatabase"));
                            break;

                        case ("PAYMENTINFO_EDIT"):
                            i = new Intent(LoginActivity.this, PaymentInfoEditActivity.class);
                            i.putExtra("PAYMENTINFO", (Data) bundle.get("lastDatabase"));
                            break;

                        case ("LOGININFO_EDIT"):
                            i = new Intent(LoginActivity.this, LoginInfoEditActivity.class);
                            i.putExtra("LOGININFO", (Data) bundle.get("lastDatabase"));
                            break;

                        default:
                            i = new Intent(LoginActivity.this, MainActivity.class);
                            break;
                    }

                mProgressDialog.dismiss();
                LoginActivity.this.startActivity(i); // MOVE TO MAIN ACTIVITY
            } else
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
            protected void onPreExecute() {
                super.onPreExecute();

                progressBar();
            }

            @Override
            protected Void doInBackground(Void... params) {
                try // TRY LOGIN MECHANICS
                    {
                        final String decryptedPulledPin;
                        if (mIsComplexCode)
                            {
                                mPin = mPasscodeString;
                            }
                        decryptedPulledPin = new CryptKeyHandler(mContext)
                                .DECRYPTKEY(mSharedPreferences.getString(PIN, null), mPin);

                        final String loginPinHash = SHA256PinHash
                                .hashFunction(mPin, Arrays.copyOfRange(Base64.decode(decryptedPulledPin, FLAGS), 0, 128));

                        System.out.println("LOGIN INPUT: " + loginPinHash);
                        System.out.println("CACHED HASH: " + decryptedPulledPin);

                        if (decryptedPulledPin.equals(loginPinHash)) /// TEST PERIODICALLY INPUTTED PIN AGAINST CACHED PIN
                            {
                                TEMP_PIN = mPin;
                                MASTER_KEY = new CryptKeyHandler(mContext).DECRYPTKEY(mSharedPreferences.getString(CRYPT_KEY, null), mPin);
                                System.out.println("MASTER KEY: " + MASTER_KEY);

                                runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run() {
                                        if (!mIsComplexCode)
                                            {
                                                mInput1.setChecked(false);
                                                mInput2.setChecked(false);
                                                mInput3.setChecked(false);
                                                mInput4.setChecked(false);
                                            } else
                                            {
                                                mTvPasscodeDisplay.setText("");
                                            }
                                    }
                                });

                                mPin = "";
                                mPasscodeString = "";
                                mPasscodeMark = "";

                                LogoutProtocol.APP_LOGGED_IN = true; // APP LOGGED IN
                                loginIntent();

                                System.gc();
                            }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        clear();

                        mPasscodeString = "";
                        mPasscodeMark = "";

                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run() {
                                mTvPasscodeDisplay.setText("");
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

    // ####################################################
    // SIMPLE DIGIT KEY REGISTRATION                      #
    private void onClickSimple(View v) {
        if (v.getId() != btnBACKSPACE)
            {
                incrementIndexNumber();
                switch (v.getId())
                    {
                        case R.id.btn0:
                            addToArray(m0);
                            break;
                        case R.id.btn1:
                            addToArray(m1);
                            break;
                        case R.id.btn2:
                            addToArray(m2);
                            break;
                        case R.id.btn3:
                            addToArray(m3);
                            break;
                        case R.id.btn4:
                            addToArray(m4);
                            break;
                        case R.id.btn5:
                            addToArray(m5);
                            break;
                        case R.id.btn6:
                            addToArray(m6);
                            break;
                        case R.id.btn7:
                            addToArray(m7);
                            break;
                        case R.id.btn8:
                            addToArray(m8);
                            break;
                        case R.id.btn9:
                            addToArray(m9);
                            break;
                    }
            } else
            {
                deleteFromArray();
            }

        if (mPin.length() == 4)
            {
                onPinCompleted();
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
    }            //#
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
    }              //#
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
    }            //#
    public void deleteFromArray() {
        if (mIndex != -1)
            {
                mArray[mIndex] = null;
                mIndex--;
            }
    }                 //#
    public void clear() {
        mPin = "";
        mArray = new String[mArray.length];
        mIndex = -1;
    }                           //#
    // -----------------------------                    //#
    //                                                    #
    // COMPLEX KEYBOARD REGISTRATION                      #
    private void onClickComplex(View v) {
        switch (v.getId())
            {
                case R.id.btn0:
                    addToPasscodeString("0");
                    break;
                case R.id.btn1:
                    addToPasscodeString("1");
                    break;
                case R.id.btn2:
                    addToPasscodeString("2");
                    break;
                case R.id.btn3:
                    addToPasscodeString("3");
                    break;
                case R.id.btn4:
                    addToPasscodeString("4");
                    break;
                case R.id.btn5:
                    addToPasscodeString("5");
                    break;
                case R.id.btn6:
                    addToPasscodeString("6");
                    break;
                case R.id.btn7:
                    addToPasscodeString("7");
                    break;
                case R.id.btn8:
                    addToPasscodeString("8");
                    break;
                case R.id.btn9:
                    addToPasscodeString("9");
                    break;

                case R.id.btnQ:
                    addToPasscodeString("Q");
                    break;
                case R.id.btnW:
                    addToPasscodeString("W");
                    break;
                case R.id.btnE:
                    addToPasscodeString("E");
                    break;
                case R.id.btnR:
                    addToPasscodeString("R");
                    break;
                case R.id.btnT:
                    addToPasscodeString("T");
                    break;
                case R.id.btnY:
                    addToPasscodeString("Y");
                    break;
                case R.id.btnU:
                    addToPasscodeString("U");
                    break;
                case R.id.btnI:
                    addToPasscodeString("I");
                    break;
                case R.id.btnO:
                    addToPasscodeString("O");
                    break;
                case R.id.btnP:
                    addToPasscodeString("P");
                    break;
                case R.id.btnA:
                    addToPasscodeString("A");
                    break;
                case R.id.btnS:
                    addToPasscodeString("S");
                    break;
                case R.id.btnD:
                    addToPasscodeString("D");
                    break;
                case R.id.btnF:
                    addToPasscodeString("F");
                    break;
                case R.id.btnG:
                    addToPasscodeString("G");
                    break;
                case R.id.btnH:
                    addToPasscodeString("H");
                    break;
                case R.id.btnJ:
                    addToPasscodeString("J");
                    break;
                case R.id.btnK:
                    addToPasscodeString("K");
                    break;
                case R.id.btnL:
                    addToPasscodeString("L");
                    break;
                case R.id.btnZ:
                    addToPasscodeString("Z");
                    break;
                case R.id.btnX:
                    addToPasscodeString("X");
                    break;
                case R.id.btnC:
                    addToPasscodeString("C");
                    break;
                case R.id.btnV:
                    addToPasscodeString("V");
                    break;
                case R.id.btnB:
                    addToPasscodeString("B");
                    break;
                case R.id.btnN:
                    addToPasscodeString("N");
                    break;
                case R.id.btnM:
                    addToPasscodeString("M");
                    break;

                case R.id.btnDOT:
                    addToPasscodeString(".");
                    break;
                case R.id.btnCOMMA:
                    addToPasscodeString(",");
                    break;
                case R.id.btnSLASH:
                    addToPasscodeString("/");
                    break;
                case R.id.btnSPACEBAR:
                    addToPasscodeString(" ");
                    break;
                case R.id.btnBACKSPACE:
                    addToPasscodeString("DEL");
                    break;

                case R.id.btnLOGINREGISTER:
                    onPinCompleted();
                    break;
                case R.id.btnCLEAR:
                    mPasscodeString = "";
                    mPasscodeMark = "";
                    mTvPasscodeDisplay.setText("");
                    break;
            }
    }           //#
    public String addToPasscodeString(String s) {
        if (!s.matches("DEL"))
            {
                mPasscodeString = mPasscodeString + s;

                mPasscodeMark = mPasscodeMark + "*";
            } else
            {
                if (mPasscodeString.length() != 0)
                    {
                        mPasscodeString = mPasscodeString.substring(0, mPasscodeString.length() - 1);

                        mPasscodeMark = mPasscodeMark.substring(0, mPasscodeMark.length() - 1);
                    }
            }

        mTvPasscodeDisplay.setText(mPasscodeMark);

        return mPasscodeString;
    }   //#
    // ####################################################

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
        startActivity(homeIntent);
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
}