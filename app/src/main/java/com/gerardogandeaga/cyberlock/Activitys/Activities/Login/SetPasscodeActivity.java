package com.gerardogandeaga.cyberlock.Activitys.Activities.Login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.gerardogandeaga.cyberlock.Encryption.AESKeyHandler;
import com.gerardogandeaga.cyberlock.Encryption.SHA256PinHash;
import com.gerardogandeaga.cyberlock.R;

import static com.gerardogandeaga.cyberlock.R.id.input1;
import static com.gerardogandeaga.cyberlock.R.id.input2;
import static com.gerardogandeaga.cyberlock.R.id.input3;
import static com.gerardogandeaga.cyberlock.R.id.input4;

public class SetPasscodeActivity extends AppCompatActivity implements  View.OnClickListener
{
    // STORED PIN
    private static String mPin = "", mPinFirst = "", mPinSecond = "";
    private static final int flags = Base64.DEFAULT;
    private static final String PIN = "PIN", KEY = "KEY", AUTOSAVE = "AUTOSAVE"; // ENTRY INFORMATION
    private SharedPreferences mSharedPreferences;
    // PIN ARRAY VARIABLES
    private static boolean mArrayFull = false;
    private static int mIndex = -1;
    private static String[] mArray = new String[4];
    // WIDGETS
    private TextView mTextView;
    private Button mBtn0, mBtn1, mBtn2, mBtn3, mBtn4, mBtn5, mBtn6, mBtn7, mBtn8, mBtn9;
    private RadioButton mInput1, mInput2, mInput3, mInput4;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setpin);
        LogoutProtocol.ACTIVITY_INTENT = null;

        mSharedPreferences = getSharedPreferences("com.gerardogandeaga.cyberlock", Context.MODE_PRIVATE);

        if (mSharedPreferences.getString(PIN, null) != null || mSharedPreferences.getString(KEY, null) != null)
        {
            LogoutProtocol.ACTIVITY_INTENT = new Intent(this, LoginActivity.class);
            this.finish();
            this.startActivity(LogoutProtocol.ACTIVITY_INTENT);
        }
        else
        {
            mTextView = (TextView) findViewById(R.id.tvInstructions);

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

            this.mTextView.setText(R.string.new_pin);
        }
    }

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

        if (mPin.length() == 4) { storePins(); }

        if (mArray[0] != null) { mInput1.setChecked(true); } else { mInput1.setChecked(false); }
        if (mArray[1] != null) { mInput2.setChecked(true); } else { mInput2.setChecked(false); }
        if (mArray[2] != null) { mInput3.setChecked(true); } else { mInput3.setChecked(false); }
        if (mArray[3] != null) { mInput4.setChecked(true); } else { mInput4.setChecked(false); }
    }

    public void storePins()
    {
        if (mPinFirst.matches("") && mPinSecond.matches("")) {
            mPinFirst = mPin;
            mTextView.setText(R.string.confirm_pin);
            clear();
        } else if (!mPinFirst.matches("") && mPinSecond.matches("")) {
            mPinSecond = mPin;
            clear();

            System.out.println("pin 1 " + mPinFirst);
            System.out.println("pin 2 " + mPinSecond);
            onPinsCompleted();
        }
    }

    private void onPinsCompleted()
    {
        final String pinFirst = mPinFirst;
        final String pinSecond = mPinSecond;

        mPinFirst = "";
        mPinSecond = "";

        if ((pinFirst.matches(pinSecond)) && (!pinFirst.matches("") || (!pinSecond.matches(""))))
        {
            final String passwordHash; // GENERATE THE HASH PIN
            try
            {
                passwordHash = AESKeyHandler.ENCRYPTKEY(SHA256PinHash.hashFunction(pinFirst, SHA256PinHash.generateSalt()), pinFirst);

                mSharedPreferences.edit().putString(PIN, passwordHash).apply(); // ADD HASHED PIN TO STORE
                System.out.println("HASHED PIN :" + passwordHash);

                byte[] KEY_Byte = AESKeyHandler.AES_MEMO_BYTE_KEY_GENERATE(); // GENERATE A NEW BYTE ARRAY AS A SYMMETRIC KEY
                byte[] ENC_DEC_KEY_ByteVal = AESKeyHandler.AES_MEMO_KEY_GENERATE(KEY_Byte);

                System.out.println("REGISTED KEY VAL :" + Base64.encodeToString(ENC_DEC_KEY_ByteVal, flags));
                System.out.println("REGISTED KEY SIZE :" + ENC_DEC_KEY_ByteVal.length);

                String ENC_DEC_KEY_StringVal = AESKeyHandler.ENCRYPTKEY(Base64.encodeToString(ENC_DEC_KEY_ByteVal, flags), pinFirst); // ENCRYPT BYTES TO STRING

                mSharedPreferences.edit().putString(KEY, ENC_DEC_KEY_StringVal).apply(); // STORE THE KEY IN THE KEY STORE
                System.out.println("REGISTER ENCRYPTED KEY VAL :" + ENC_DEC_KEY_StringVal);

                mPinFirst = null;
                mPinSecond = null;

                // SETTINGS FEATURES
                mSharedPreferences.edit().putBoolean(AUTOSAVE, false).apply();

                Intent i = new Intent(SetPasscodeActivity.this, LoginActivity.class);
                SetPasscodeActivity.this.startActivity(i);

            } catch (Exception e) {
                e.printStackTrace();

                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        } else
        {
            Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
            mTextView.setText(R.string.new_pin);
        }
    }

    // PIN KEYBOARD AND REGISTRATION
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
}