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
import android.widget.TextView;
import android.widget.Toast;

import com.gerardogandeaga.cyberlock.Encryption.CryptKeyHandler;
import com.gerardogandeaga.cyberlock.Encryption.SHA256PinHash;
import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.Supports.Globals;

import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.Supports.Globals.COMPLEXPASSCODE;
import static com.gerardogandeaga.cyberlock.Supports.Globals.CRYPT_KEY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.DIRECTORY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.MASTER_KEY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.PIN;
import static com.gerardogandeaga.cyberlock.Supports.Globals.TEMP_PIN;

public class Settings_ResetComplexPasscode extends AppCompatActivity implements View.OnClickListener
{
    private Context mContext = this;
    private SharedPreferences mSharedPreferences;

    // DATA VARIABLES
    private static String mPasscodeString = "";
    private static String mPasscodeMark = "";
    private static String mCode1 = "";
    private static String mCode2 = "";

    // WIDGETS
    private Button mQ, mW, mE, mR, mT, mY, mU, mI, mO, mP, mA, mS, mD, mF, mG, mH, mJ, mK, mL, mZ, mX, mC, mV, mB, mN, mM;
    private Button m0, m1, m2, m3, m4, m5, m6, m7, m8, m9;
    private Button mSLASH, mDOT, mCOMMA;
    private Button mBtnLoginRegister;
    private ImageButton mSPACEBAR;
    private ImageButton mBtnCLEAR;
    private ProgressDialog mProgressDialog;
    private TextView mTvTextDisplay;
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
        setContentView(R.layout.activity_loginpasscode);
        ACTIVITY_INTENT = null;

        mSharedPreferences = getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE);
        //
        this.mTvPasscodeDisplay = (TextView) findViewById(R.id.tvPasscode);
        this.mTvTextDisplay = (TextView) findViewById(R.id.tvTextDisplay);
        mTvTextDisplay.setText("Please Register New Passcode");

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

        this.mBtnLoginRegister = (Button) findViewById(R.id.btnLOGINREGISTER);
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

        this.mBtnCLEAR.setOnClickListener(this);

        this.mBtnLoginRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                if (!mCode1.matches("") && mCode2.matches(""))
                    {
                        mCode2 = mPasscodeString;
                        mPasscodeString = "";
                        mPasscodeMark = "";
                        mTvPasscodeDisplay.setText("");
                        System.out.println(mCode2);

                        System.out.println("CODE 1: " + mCode1);
                        System.out.println("CODE 2: " + mCode2);
                        if (mCode1.matches(mCode2))
                            {
                                onPinsCompleted();

                            } else
                            {
                                Toast.makeText(mContext, "Please Try Again", Toast.LENGTH_SHORT).show();

                                mTvTextDisplay.setText("Please Register New Passcode");
                            }
                    }

                if (mCode1.matches("") && mCode2.matches(""))
                    {
                        mCode1 = mPasscodeString;
                        mPasscodeString = "";
                        mPasscodeMark = "";
                        mTvPasscodeDisplay.setText("");
                        mTvTextDisplay.setText("Please Confirm Passcode");
                        System.out.println(mCode1);
                    }
            }
        });
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

                case R.id.btnCLEAR:
                    mPasscodeString = "";
                    mPasscodeMark = "";
                    mTvPasscodeDisplay.setText("");
                    break;
            }
    }
    // --------

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

        mTvPasscodeDisplay.setText(mPasscodeMark);

        return mPasscodeString;
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
                final String codeFirst = mCode1;

                mCode1 = "";
                mCode2 = "";

                final String pinHash; // GENERATE THE HASH PIN
                try
                    {
                        CryptKeyHandler cryptKeyHandler = new CryptKeyHandler(mContext);
                        pinHash = cryptKeyHandler
                                .ENCRYPT_KEY(SHA256PinHash.HASH_FUNCTION(codeFirst, SHA256PinHash.GENERATE_SALT()), codeFirst);

                        mSharedPreferences.edit().putString(PIN, pinHash).apply(); // ADD HASHED PIN TO STORE
                        System.out.println("HASHED PIN :" + pinHash);
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

                        mSharedPreferences.edit().putBoolean(COMPLEXPASSCODE, true).apply();
                        ACTIVITY_INTENT = new Intent(mContext, Settings.class);

                        mPasscodeString = "";
                        mPasscodeMark = "";

                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run() {
                                mTvPasscodeDisplay.setText("");
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

                mSharedPreferences.edit().putBoolean(COMPLEXPASSCODE, true).apply();
                mProgressDialog.dismiss();
            }
        }.execute();
    }
    // ----------------
}
