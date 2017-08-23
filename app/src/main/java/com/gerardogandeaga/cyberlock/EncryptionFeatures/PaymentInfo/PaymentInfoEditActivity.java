package com.gerardogandeaga.cyberlock.EncryptionFeatures.PaymentInfo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LoginActivity;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Main.MainActivity;
import com.gerardogandeaga.cyberlock.Encryption.AESContent;
import com.gerardogandeaga.cyberlock.Encryption.AESKeyHandler;
import com.gerardogandeaga.cyberlock.R;

import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.APP_LOGGED_IN;
import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.mCountDownIsFinished;
import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.mCountDownTimer;
import static com.gerardogandeaga.cyberlock.EncryptionFeatures.PrivateMemo.MemoEditActivity.DIRECTORY;

public class PaymentInfoEditActivity extends AppCompatActivity
{
    // DATA
    private PaymentInfo mPaymentInfo;
    private String mCardType;
    private ArrayAdapter<CharSequence> mAdapter;
    // WIDGETS
    private EditText mEtTag, mEtCardName, mEtCardNumber, mEtCardExpire, mEtCardSecCode, mEtNotes, mEtQuestion1, mEtQuestion2, mEtAnswer1, mEtAnswer2;
    private TextView mTvDate;
    private Spinner mSpCardSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_paymentinfo);
        ACTIVITY_INTENT = null;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Card Edit");

        this.mEtTag = (EditText) findViewById(R.id.etTag);
        this.mEtCardName = (EditText) findViewById(R.id.etCardName);
        this.mEtCardNumber = (EditText) findViewById(R.id.etCardNumber);
        this.mEtCardExpire = (EditText) findViewById(R.id.etCardExpire);
        this.mEtCardSecCode = (EditText) findViewById(R.id.etCardSecCode);
        this.mEtQuestion1 = (EditText) findViewById(R.id.etQuestion1);
        this.mEtQuestion2 = (EditText) findViewById(R.id.etQuestion2);
        this.mEtAnswer1 = (EditText) findViewById(R.id.etAnswer1);
        this.mEtAnswer2 = (EditText) findViewById(R.id.etAnswer2);
        this.mEtNotes = (EditText) findViewById(R.id.etNotes);
        this.mTvDate = (TextView) findViewById(R.id.tvLastUpdated);

        this.mSpCardSelect = (Spinner) findViewById(R.id.spCardSelect);

        mAdapter = ArrayAdapter.createFromResource(this, R.array.cardtype_array, android.R.layout.simple_spinner_item);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpCardSelect.setAdapter(mAdapter);

        Bundle bundle = getIntent().getExtras();
        onInstantCreate(bundle);

        mEtCardNumber.addTextChangedListener(new TextWatcher()
        {
            int prevL = 0;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                prevL = mEtCardNumber.getText().toString().length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int length = s.length();
                if ((prevL < length) && (length == 4 || length == 9 || length == 14))
                {

                    String data = mEtCardNumber.getText().toString();
                    mEtCardNumber
                            .setText(data + " ");
                    mEtCardNumber.setSelection(length + 1);
                }
            }
        });

        mEtCardExpire.addTextChangedListener(new TextWatcher()
        {
            int prevL = 0;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                prevL = mEtCardExpire.getText().toString().length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int length = s.length();
                if ((prevL < length) && (length == 2))
                {

                    String data = mEtCardExpire.getText().toString();
                    mEtCardExpire.setText(data + "/");
                    mEtCardExpire.setSelection(length + 1);
                }
            }
        });

        mSpCardSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                Object object = parent.getItemAtPosition(position);
                if (object != null) { mCardType = object.toString(); }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        switch (id)
        {
            case (R.id.action_save): onSave(); return true;
            case (R.id.action_cancel): onCancel(); return true;
            case android.R.id.home: onBackPressed(); return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    private void onInstantCreate(Bundle bundle)
    {
        if (bundle != null)
        {
            mPaymentInfo = (PaymentInfo) bundle.get("PAYMENTINFO");
            if (mPaymentInfo != null)
            {
                try
                {
                    String ENCDEC_KEY = (AESKeyHandler.DECRYPTKEY(this.getSharedPreferences("com.gerardogandeaga.cyberlock", Context.MODE_PRIVATE).getString("KEY", null),
                            this.getSharedPreferences("com.gerardogandeaga.cyberlock", Context.MODE_PRIVATE).getString("TEMP_PIN", null)));

                    if (!mPaymentInfo.getCardName().matches("")) { this.mEtCardName.setText(AESContent.decryptContent(mPaymentInfo.getCardName(), ENCDEC_KEY)); } // DECRYPT
                    if (!mPaymentInfo.getCardNumber().matches("")) { this.mEtCardNumber.setText(AESContent.decryptContent(mPaymentInfo.getCardNumber(), ENCDEC_KEY)); } // DECRYPT
                    if (!mPaymentInfo.getCardExpire().matches("")) { this.mEtCardExpire.setText(AESContent.decryptContent(mPaymentInfo.getCardExpire(), ENCDEC_KEY)); } // DECRYPT
                    if (!mPaymentInfo.getCardSecCode().matches("")) { this.mEtCardSecCode.setText(AESContent.decryptContent(mPaymentInfo.getCardSecCode(), ENCDEC_KEY)); } // DECRYPT
                    if (!mPaymentInfo.getQuestion1().matches("")) { this.mEtQuestion1.setText(AESContent.decryptContent(mPaymentInfo.getQuestion1(), ENCDEC_KEY));}
                    if (!mPaymentInfo.getQuestion2().matches("")) { this.mEtQuestion2.setText(AESContent.decryptContent(mPaymentInfo.getQuestion2(), ENCDEC_KEY));}
                    if (!mPaymentInfo.getAnswer1().matches("")) { this.mEtAnswer1.setText(AESContent.decryptContent(mPaymentInfo.getAnswer1(), ENCDEC_KEY));}
                    if (!mPaymentInfo.getAnswer2().matches("")) { this.mEtAnswer2.setText(AESContent.decryptContent(mPaymentInfo.getAnswer2(), ENCDEC_KEY));}
                    if (!mPaymentInfo.getNotes().matches("")) { this.mEtNotes.setText(AESContent.decryptContent(mPaymentInfo.getNotes(), ENCDEC_KEY)); } // DECRYPT

                    // SET LABEL AND CARD TYPE
                    if (!mPaymentInfo.getLabel().matches("")) { this.mEtTag.setText(mPaymentInfo.getLabel()); }
                    if (!mPaymentInfo.getCardType().matches("")) {
                        int spinnerPosition = mAdapter.getPosition(mPaymentInfo.getCardType());
                        mSpCardSelect.setSelection(spinnerPosition);
                    }

                    if (!mPaymentInfo.getDate().matches("")) {
                        this.mTvDate.setText("Last Updated: " + mPaymentInfo.getDate());
                    } else {
                        this.mTvDate.setText("Last Updated: ---");
                    }

                    ENCDEC_KEY = null;
                    System.gc();

                } catch (Exception e)
                {
                    e.printStackTrace();
                    Toast.makeText(this, "Error: could not set one or more text fields", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void onSave()
    {
        PaymentInfoDatabaseAccess paymentInfoDatabaseAccess = PaymentInfoDatabaseAccess.getInstance(this);
        paymentInfoDatabaseAccess.open();

        String ENCDEC_KEY = (AESKeyHandler.DECRYPTKEY(this.getSharedPreferences("com.gerardogandeaga.cyberlock", Context.MODE_PRIVATE).getString("KEY", null),
                                                      this.getSharedPreferences("com.gerardogandeaga.cyberlock", Context.MODE_PRIVATE).getString("TEMP_PIN", null)));

        if ((!mEtTag.getText().toString().matches("")) || (!mEtCardName.getText().toString().matches("")) || (!mEtCardNumber.getText().toString().matches("")) || (!mEtCardExpire.getText().toString().matches("")) || (!mEtCardSecCode.getText().toString().matches("")) || (!mEtNotes.getText().toString().matches("")) || (!mEtQuestion1.getText().toString().matches("")) || (!mEtQuestion2.getText().toString().matches("")) || (!mEtAnswer1.getText().toString().matches("")) || (!mEtAnswer2.getText().toString().matches("")))
        {
            if (mPaymentInfo == null) // WHEN SAVING A NEW UNKNOWN MEMO
            {
                // ADD NEW PAYMENT INFO
                PaymentInfo temp = new PaymentInfo();

                // SET INFO
                temp.setCardName(AESContent.encryptContent(mEtCardName.getText().toString(), ENCDEC_KEY)); // SET NAME
                temp.setCardNumber(AESContent.encryptContent(mEtCardNumber.getText().toString(), ENCDEC_KEY)); // SET NUMBER
                temp.setCardExpire(AESContent.encryptContent(mEtCardExpire.getText().toString(), ENCDEC_KEY)); // SET EXPIRE
                temp.setCardSecCode(AESContent.encryptContent(mEtCardSecCode.getText().toString(), ENCDEC_KEY)); // SET SEC CODE
                temp.setQuestion1(AESContent.encryptContent(mEtQuestion1.getText().toString(), ENCDEC_KEY)); // SET QUESTION
                temp.setQuestion2(AESContent.encryptContent(mEtQuestion2.getText().toString(), ENCDEC_KEY)); // SET QUESTION
                temp.setAnswer1(AESContent.encryptContent(mEtAnswer1.getText().toString(), ENCDEC_KEY)); // SET ANSWER
                temp.setAnswer2(AESContent.encryptContent(mEtAnswer2.getText().toString(), ENCDEC_KEY)); // SET ANSWER
                temp.setNotes(AESContent.encryptContent(mEtNotes.getText().toString(), ENCDEC_KEY)); // SET NOTES
                if (!mEtTag.getText().toString().matches(""))
                {
                    temp.setLabel(mEtTag.getText().toString());
                } else
                {
                    temp.setLabel("");
                } // SET TAG
                temp.setCardType(mCardType); // SET CARD TYPE

                // SAVE NEW DATA TABLE
                paymentInfoDatabaseAccess.save(temp);
            } else
            {
                // UPDATE THE PAYMENT INFO
                // SET INFO
                mPaymentInfo.setCardName(AESContent.encryptContent(mEtCardName.getText().toString(), ENCDEC_KEY)); // SET NAME
                mPaymentInfo.setCardNumber(AESContent.encryptContent(mEtCardNumber.getText().toString(), ENCDEC_KEY)); // SET NUMBER
                mPaymentInfo.setCardExpire(AESContent.encryptContent(mEtCardExpire.getText().toString(), ENCDEC_KEY)); // SET EXPIRE
                mPaymentInfo.setCardSecCode(AESContent.encryptContent(mEtCardSecCode.getText().toString(), ENCDEC_KEY)); // SET SEC CODE
                mPaymentInfo.setQuestion1(AESContent.encryptContent(mEtQuestion1.getText().toString(), ENCDEC_KEY)); // SET QUESTION
                mPaymentInfo.setQuestion2(AESContent.encryptContent(mEtQuestion2.getText().toString(), ENCDEC_KEY)); // SET QUESTION
                mPaymentInfo.setAnswer1(AESContent.encryptContent(mEtAnswer1.getText().toString(), ENCDEC_KEY)); // SET ANSWER
                mPaymentInfo.setAnswer2(AESContent.encryptContent(mEtAnswer2.getText().toString(), ENCDEC_KEY)); // SET ANSWER
                mPaymentInfo.setNotes(AESContent.encryptContent(mEtNotes.getText().toString(), ENCDEC_KEY)); // SET NOTES
                if (!mEtTag.getText().toString().matches(""))
                {
                    mPaymentInfo.setLabel(mEtTag.getText().toString());
                } else
                {
                    mPaymentInfo.setLabel("");
                } // SET LABEL AND CARD TYPE
                mPaymentInfo.setCardType(mCardType);

                // UPDATE DATA TABLE
                paymentInfoDatabaseAccess.update(mPaymentInfo);
            }

            ENCDEC_KEY = null;
            System.gc(); // GARBAGE COLLECT TO TERMINATE -KEY- VARIABLE

            paymentInfoDatabaseAccess.close();
            onBackPressed();
        } else
        {
            Toast.makeText(this, "Nothing to save", Toast.LENGTH_SHORT).show();

            ENCDEC_KEY = null;
            System.gc(); // GARBAGE COLLECT TO TERMINATE -KEY- VARIABLE

            paymentInfoDatabaseAccess.close();
            onBackPressed();
        }
    }

    public void onCancel()
    {
        onBackPressed();
    }

    // THIS IS THE START OF THE SCRIPT FOR *** THE "TO LOGIN FUNCTION" THIS DETECTS THE ON PRESSED, START, TABS AND HOME BUTTONS IN ORDER TO INITIALIZE SECURITY "FAIL-SAFE"
    @Override
    protected void onStart()
    {
        super.onStart();

        if (mCountDownIsFinished)
        {
            if (!APP_LOGGED_IN)
            {
                if (this.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getBoolean("AUTOSAVE", false))
                {
                    onSave();

                    this.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).edit().remove("TEMP_PIN").apply();
                } else
                {
                    ACTIVITY_INTENT = new Intent(this, LoginActivity.class);
                    ACTIVITY_INTENT.putExtra("lastActivity", "PAYMENTINFO_EDIT");
                    ACTIVITY_INTENT.putExtra("lastDatabase", mPaymentInfo);
                }

                this.finish(); // CLEAN UP AND END
                this.startActivity(ACTIVITY_INTENT); // GO TO LOGIN ACTIVITY
            }
        } else
        {
            if (mCountDownTimer != null)
            {
                System.out.println("Cancel Called!");
                mCountDownTimer.cancel();
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        if (ACTIVITY_INTENT == null) // NO PENDING ACTIVITIES ???(MAIN)--->(EDIT)???
        {
            ACTIVITY_INTENT = new Intent(this, MainPaymentInfoActivity.class);
            finish();
            this.startActivity(ACTIVITY_INTENT);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();

        if (!this.isFinishing())
        { // HOME AND TABS AND SCREEN OFF
            if (ACTIVITY_INTENT == null) // NO PENDING ACTIVITIES ???(MAIN)--->(EDIT)???
            {
                if (!this.getSharedPreferences("com.gerardogandeaga.cyberlock", Context.MODE_PRIVATE).getBoolean("AUTOSAVE", false))
                {
                    new LogoutProtocol().logoutExecuteAutosaveOff(this);
                }
                else
                {
                    new LogoutProtocol().logoutExecuteAutosaveOn(this);
                }
            }
        }
    }

    @Override
    public void finish() // BACK BUTTON CACHES ACTIVITY ACTUAL START ---> MAIN ACTIVITY
    {
        super.finish();

        Intent i = new Intent(this, MainActivity.class);
        this.startActivity(i);
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
}