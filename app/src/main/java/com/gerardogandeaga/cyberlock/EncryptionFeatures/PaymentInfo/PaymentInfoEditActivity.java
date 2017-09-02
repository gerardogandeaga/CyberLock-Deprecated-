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
import com.gerardogandeaga.cyberlock.Encryption.CryptContent;
import com.gerardogandeaga.cyberlock.R;

import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.APP_LOGGED_IN;
import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.mCountDownIsFinished;
import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.mCountDownTimer;
import static com.gerardogandeaga.cyberlock.Supports.Globals.AUTOSAVE;
import static com.gerardogandeaga.cyberlock.Supports.Globals.DIRECTORY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.MASTER_KEY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.TEMP_PIN;

public class PaymentInfoEditActivity extends AppCompatActivity
{
    // DATA
    private CryptContent mContent;
    private PaymentInfo mPaymentInfo;
    private String mCardType;
    private ArrayAdapter<CharSequence> mAdapter;
    // WIDGETS
    private EditText mEtLabel, mEtCardName, mEtCardNumber, mEtCardExpire, mEtCardSecCode, mEtNotes, mEtQuestion1, mEtQuestion2, mEtAnswer1, mEtAnswer2;
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

        this.mEtLabel = (EditText) findViewById(R.id.etTag);
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

        mAdapter = ArrayAdapter.createFromResource(this, R.array.CardType_array, android.R.layout.simple_spinner_item);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpCardSelect.setAdapter(mAdapter);

        Bundle bundle = getIntent().getExtras();
        setupActivity(bundle);

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
            case (R.id.action_save): onSave(); onBackPressed(); return true;
            case (R.id.action_cancel): onCancel(); return true;
            case android.R.id.home: onBackPressed(); return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onSave()
    {
        PaymentInfoDatabaseAccess paymentInfoDatabaseAccess = PaymentInfoDatabaseAccess.getInstance(this);
        paymentInfoDatabaseAccess.open();

        if ((!mEtLabel.getText().toString().matches("")) || (!mEtCardName.getText().toString().matches("")) || (!mEtCardNumber.getText().toString().matches("")) || (!mEtCardExpire.getText().toString().matches("")) || (!mEtCardSecCode.getText().toString().matches("")) || (!mEtNotes.getText().toString().matches("")) || (!mEtQuestion1.getText().toString().matches("")) || (!mEtQuestion2.getText().toString().matches("")) || (!mEtAnswer1.getText().toString().matches("")) || (!mEtAnswer2.getText().toString().matches("")))
        {

            if (mPaymentInfo == null) // WHEN SAVING A NEW UNKNOWN MEMO
            {
                // ADD NEW PAYMENT INFO
                PaymentInfo temp = new PaymentInfo();

                // SET INFO
                temp.setCardName(mContent.encryptContent(mEtCardName.getText().toString(), MASTER_KEY)); // SET NAME
                temp.setCardNumber(mContent.encryptContent(mEtCardNumber.getText().toString(), MASTER_KEY)); // SET NUMBER
                temp.setCardExpire(mContent.encryptContent(mEtCardExpire.getText().toString(), MASTER_KEY)); // SET EXPIRE
                temp.setCardSecCode(mContent.encryptContent(mEtCardSecCode.getText().toString(), MASTER_KEY)); // SET SEC CODE
                temp.setQuestion1(mContent.encryptContent(mEtQuestion1.getText().toString(), MASTER_KEY)); // SET QUESTION
                temp.setQuestion2(mContent.encryptContent(mEtQuestion2.getText().toString(), MASTER_KEY)); // SET QUESTION
                temp.setAnswer1(mContent.encryptContent(mEtAnswer1.getText().toString(), MASTER_KEY)); // SET ANSWER
                temp.setAnswer2(mContent.encryptContent(mEtAnswer2.getText().toString(), MASTER_KEY)); // SET ANSWER
                temp.setNotes(mContent.encryptContent(mEtNotes.getText().toString(), MASTER_KEY)); // SET NOTES
                if (!mEtLabel.getText().toString().matches(""))
                {
                    temp.setLabel(mEtLabel.getText().toString());
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
                mPaymentInfo.setCardName(mContent.encryptContent(mEtCardName.getText().toString(), MASTER_KEY)); // SET NAME
                mPaymentInfo.setCardNumber(mContent.encryptContent(mEtCardNumber.getText().toString(), MASTER_KEY)); // SET NUMBER
                mPaymentInfo.setCardExpire(mContent.encryptContent(mEtCardExpire.getText().toString(), MASTER_KEY)); // SET EXPIRE
                mPaymentInfo.setCardSecCode(mContent.encryptContent(mEtCardSecCode.getText().toString(), MASTER_KEY)); // SET SEC CODE
                mPaymentInfo.setQuestion1(mContent.encryptContent(mEtQuestion1.getText().toString(), MASTER_KEY)); // SET QUESTION
                mPaymentInfo.setQuestion2(mContent.encryptContent(mEtQuestion2.getText().toString(), MASTER_KEY)); // SET QUESTION
                mPaymentInfo.setAnswer1(mContent.encryptContent(mEtAnswer1.getText().toString(), MASTER_KEY)); // SET ANSWER
                mPaymentInfo.setAnswer2(mContent.encryptContent(mEtAnswer2.getText().toString(), MASTER_KEY)); // SET ANSWER
                mPaymentInfo.setNotes(mContent.encryptContent(mEtNotes.getText().toString(), MASTER_KEY)); // SET NOTES
                if (!mEtLabel.getText().toString().matches(""))
                {
                    mPaymentInfo.setLabel(mEtLabel.getText().toString());
                } else
                {
                    mPaymentInfo.setLabel("");
                } // SET LABEL AND CARD TYPE
                mPaymentInfo.setCardType(mCardType);

                // UPDATE DATA TABLE
                paymentInfoDatabaseAccess.update(mPaymentInfo);
            }

            paymentInfoDatabaseAccess.close();
        } else
        {
            Toast.makeText(this, "Nothing to save", Toast.LENGTH_SHORT).show();

            paymentInfoDatabaseAccess.close();
        }
    }

    private void onCancel()
    {
        ACTIVITY_INTENT = new Intent(this, MainPaymentInfoActivity.class);
        finish();
        this.startActivity(ACTIVITY_INTENT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    private void setupActivity(Bundle bundle)
    {
        mContent = new CryptContent(this);
        if (bundle != null)
        {
            mPaymentInfo = (PaymentInfo) bundle.get("PAYMENTINFO");
            if (mPaymentInfo != null)
            {
                try
                {
                    String label = mContent.decryptContent(mPaymentInfo.getLabel(), MASTER_KEY);
                    String cardName = mContent.decryptContent(mPaymentInfo.getCardName(), MASTER_KEY);
                    String cardNumber = mContent.decryptContent(mPaymentInfo.getCardNumber(), MASTER_KEY);
                    String cardExpire = mContent.decryptContent(mPaymentInfo.getCardExpire(), MASTER_KEY);
                    String cardSecCode = mContent.decryptContent(mPaymentInfo.getCardSecCode(), MASTER_KEY);
                    String question1 = mContent.decryptContent(mPaymentInfo.getQuestion1(), MASTER_KEY);
                    String question2 = mContent.decryptContent(mPaymentInfo.getQuestion2(), MASTER_KEY);
                    String answer1 = mContent.decryptContent(mPaymentInfo.getAnswer1(), MASTER_KEY);
                    String answer2 = mContent.decryptContent(mPaymentInfo.getAnswer2(), MASTER_KEY);
                    String notes = mContent.decryptContent(mPaymentInfo.getNotes(), MASTER_KEY);

                    if (label != null) mEtLabel.setText(label);
                    if (cardName != null) mEtCardName.setText(cardName);
                    if (cardNumber != null) mEtCardNumber.setText(cardNumber);
                    if (cardExpire != null) mEtCardExpire.setText(cardExpire);
                    if (cardSecCode != null) mEtCardSecCode.setText(cardSecCode);
                    if (question1 != null) mEtQuestion1.setText(question1);
                    if (question2 != null) mEtQuestion2.setText(question2);
                    if (answer1 != null) mEtAnswer1.setText(answer1);
                    if (answer2 != null) mEtAnswer2.setText(answer2);
                    if (notes != null) mEtNotes.setText(notes);

                    // SET LABEL AND CARD TYPE
                    if (!mPaymentInfo.getCardType().matches("")) {
                        int spinnerPosition = mAdapter.getPosition(mPaymentInfo.getCardType());
                        mSpCardSelect.setSelection(spinnerPosition);
                    }

                    if (!mPaymentInfo.getDate().matches("")) {
                        this.mTvDate.setText("Last Updated: " + mPaymentInfo.getDate());
                    } else {
                        this.mTvDate.setText("Last Updated: ---");
                    }

                } catch (Exception e)
                {
                    e.printStackTrace();
                    Toast.makeText(this, "Error: could not set one or more text fields", Toast.LENGTH_SHORT).show();
                }
            }
        }
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
                if (this.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getBoolean(AUTOSAVE, false))
                {
                    onSave();

                    this.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).edit().remove(TEMP_PIN).apply();
                } else
                {
                    ACTIVITY_INTENT = new Intent(this, LoginActivity.class);
                    ACTIVITY_INTENT.putExtra("lastActivity", "PAYMENTINFO_EDIT");
                    ACTIVITY_INTENT.putExtra("lastDatabase", mPaymentInfo);
                }

                this.finish(); // CLEAN UP AND END
                this.startActivity(ACTIVITY_INTENT); // GO TO LOGIN ACTIVITY

                System.gc();
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
            if (this.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getBoolean(AUTOSAVE, false)) { onSave(); }

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
                if (!this.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getBoolean(AUTOSAVE, false))
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
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
}