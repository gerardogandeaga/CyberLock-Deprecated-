package com.gerardogandeaga.cyberlock.Activitys.Activities.Edits;

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
import com.gerardogandeaga.cyberlock.Activitys.Activities.Main.MainActivity;
import com.gerardogandeaga.cyberlock.Crypto.CryptContent;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.ContentDatabase.Data;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.ContentDatabase.MasterDatabaseAccess;
import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.Supports.EditDialogs;
import com.gerardogandeaga.cyberlock.Supports.Globals;
import com.gerardogandeaga.cyberlock.Supports.LogoutProtocol;

import java.util.NoSuchElementException;
import java.util.Scanner;

import static com.gerardogandeaga.cyberlock.Supports.Globals.AUTOSAVE;
import static com.gerardogandeaga.cyberlock.Supports.Globals.DIRECTORY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.ENCRYPTION_ALGO;
import static com.gerardogandeaga.cyberlock.Supports.Globals.MASTER_KEY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.TEMP_PIN;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.APP_LOGGED_IN;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.mCountDownIsFinished;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.mCountDownTimer;

public class PaymentInfoEditActivity extends AppCompatActivity
{
    // DATA VARIABLES
    private boolean mIsNew = true;
    private boolean mIsAutoSave = false;
    private CryptContent mCRYPTCONTENT;
    private Data mData;
    private String mCardType;
    private ArrayAdapter<CharSequence> mAdapter;

    // WIDGETS
    private EditText
            mEtLabel,
            mEtCardName,
            mEtCardNumber,
            mEtCardExpire,
            mEtCardSecCode,
            mEtNotes,
            mEtQuestion1,
            mEtQuestion2,
            mEtAnswer1,
            mEtAnswer2;
    private Spinner mSpCardSelect;
    private TextView mTvDate;

    private String mColourTag;
    private EditDialogs mEditDialogs;

    // INITIAL ON CREATE METHODS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Globals.COLORSCHEME(this);
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        setupLayout();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }
    private void setupLayout() {
        setContentView(R.layout.activity_edit_paymentinfo);
        ACTIVITY_INTENT = null;
        mEditDialogs = new EditDialogs(this);
        mIsAutoSave = getSharedPreferences(DIRECTORY, MODE_PRIVATE).getBoolean(AUTOSAVE, false);
        //
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Card Edit");
        getSupportActionBar().setSubtitle("Algorithm: " +
                getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getString(ENCRYPTION_ALGO, "---"));

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
                if ((prevL < length) && (length == 4 || length == 9 || length == 14)) {

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
                if ((prevL < length) && (length == 2)) {

                    String data = mEtCardExpire.getText().toString();
                    mEtCardExpire.setText(data + "/");
                    mEtCardExpire.setSelection(length + 1);
                }
            }
        });

        mSpCardSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object object = parent.getItemAtPosition(position);
                if (object != null) {
                    mCardType = object.toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    private void setupActivity(Bundle bundle) {
        mCRYPTCONTENT = new CryptContent(this);
        if (bundle != null) {
            mIsNew = false;
            mData = (Data) bundle.get("DATA");
            if (mData != null) {
                try {
                    String label = mCRYPTCONTENT.DECRYPT_CONTENT(mData.getLabel(), MASTER_KEY);
                    mEtLabel.setText(label);
                    mTvDate.setText("Last Updated: " + mData.getDate());
                    mColourTag = mData.getColourTag();

                    String cardName;
                    String cardNumber;
                    String cardType;
                    String cardExpire;
                    String cardSecCode;
                    String question1;
                    String question2;
                    String answer1;
                    String answer2;
                    String notes;

                    final String content = mCRYPTCONTENT.DECRYPT_CONTENT(mData.getContent(), MASTER_KEY);
                    if (content != null) {
                        Scanner scanner = new Scanner(content);

                        cardName = scanner.nextLine();
                        cardNumber = scanner.nextLine();
                        cardType = scanner.nextLine();
                        cardExpire = scanner.nextLine();
                        cardSecCode = scanner.nextLine();
                        question1 = scanner.nextLine();
                        question2 = scanner.nextLine();
                        answer1 = scanner.nextLine();
                        answer2 = scanner.nextLine();
                        try {
                            notes = scanner.nextLine();
                            while (scanner.hasNextLine()) {
                                notes += "\n";
                                notes += scanner.nextLine();
                            }
                            mEtNotes.setText(notes);
                        } catch (NoSuchElementException e) {
                            e.printStackTrace();
                        }
                        scanner.close();

                        mEtCardName.setText(cardName);
                        mEtCardNumber.setText(cardNumber);
                        mEtCardExpire.setText(cardExpire);
                        mEtCardSecCode.setText(cardSecCode);
                        mEtQuestion1.setText(question1);
                        mEtQuestion2.setText(question2);
                        mEtAnswer1.setText(answer1);
                        mEtAnswer2.setText(answer2);

                        int spinnerPosition = mAdapter.getPosition(cardType);
                        mSpCardSelect.setSelection(spinnerPosition);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error: could not set one or more text fields", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    // -------------------------

    // ON CLICK
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case (R.id.action_save):
                if (!mIsAutoSave) {
                    onSave();
                    onBackPressed();
                } else {
                    onBackPressed();
                }
            return true;
            case (R.id.action_cancel):
                onCancel();
                return true;
            case (R.id.action_colortag):
                mEditDialogs.createColourPickDialog();
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    // --------

    // ACTIVITY ACTIONS
    public void onSave() {
        MasterDatabaseAccess masterDatabaseAccess = MasterDatabaseAccess.getInstance(this);
        masterDatabaseAccess.open();

        if ((!mEtLabel.getText().toString().matches("")) || (!mEtCardName.getText().toString().matches("")) || (!mEtCardNumber.getText().toString().matches("")) || (!mEtCardExpire.getText().toString().matches("")) || (!mEtCardSecCode.getText().toString().matches("")) || (!mEtNotes.getText().toString().matches("")) || (!mEtQuestion1.getText().toString().matches("")) || (!mEtQuestion2.getText().toString().matches("")) || (!mEtAnswer1.getText().toString().matches("")) || (!mEtAnswer2.getText().toString().matches(""))) {
            final String cardName = mEtCardName.getText().toString();
            final String cardNumber = mEtCardNumber.getText().toString();
            final String cardType = mCardType;
            final String cardExpire = mEtCardExpire.getText().toString();
            final String cardSecCode = mEtCardSecCode.getText().toString();
            final String question1 = mEtQuestion1.getText().toString();
            final String question2 = mEtQuestion2.getText().toString();
            final String answer1 = mEtAnswer1.getText().toString();
            final String answer2 = mEtAnswer2.getText().toString();
            final String notes = mEtNotes.getText().toString();

            final String format = "%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s";
            final String tmpString = String.format(format,
                    cardName, cardNumber, cardType, cardExpire, cardSecCode, question1, question2, answer1, answer2, notes);

            if (mData == null) {

                Data tmp = new Data();

                tmp.setType("TYPE_PAYMENTINFO");
                tmp.setLabel(mCRYPTCONTENT.ENCRYPT_KEY(mEtLabel.getText().toString(), MASTER_KEY));
                tmp.setContent(mCRYPTCONTENT.ENCRYPT_KEY(tmpString, MASTER_KEY));
                // SET COLOUR CONDITIONALLY
                if (!mIsNew) {
                    if (mEditDialogs.getTmpColour() == null) {
                        tmp.setColourTag(mColourTag);
                    } else {
                        tmp.setColourTag(mEditDialogs.getTmpColour());
                    }
                } else {
                    tmp.setColourTag(mEditDialogs.getTmpColour());
                }
                // ------------------------
                masterDatabaseAccess.save(tmp);
            } else {
                mData.setColourTag("COL_RED");

                mData.setLabel(mCRYPTCONTENT.ENCRYPT_KEY(mEtLabel.getText().toString(), MASTER_KEY));
                mData.setContent(mCRYPTCONTENT.ENCRYPT_KEY(tmpString, MASTER_KEY));
                // SET COLOUR CONDITIONALLY
                if (!mIsNew) {
                    if (mEditDialogs.getTmpColour() == null) {
                        mData.setColourTag(mColourTag);
                    } else {
                        mData.setColourTag(mEditDialogs.getTmpColour());
                    }
                } else {
                    mData.setColourTag(mEditDialogs.getTmpColour());
                }
                // ------------------------
                masterDatabaseAccess.update(mData);
            }
            masterDatabaseAccess.close();
        } else {

            masterDatabaseAccess.close();
            Toast.makeText(this, "Nothing to save", Toast.LENGTH_SHORT).show();
        }
    }
    private void onCancel() {
        ACTIVITY_INTENT = new Intent(this, MainActivity.class);
        this.finish();
        this.startActivity(ACTIVITY_INTENT);
    }
    // ----------------

    // THIS IS THE START OF THE SCRIPT FOR *** THE "TO LOGIN FUNCTION" THIS DETECTS THE ON PRESSED, START, TABS AND HOME BUTTONS IN ORDER TO INITIALIZE SECURITY "FAIL-SAFE"
    @Override
    protected void onStart() {
        super.onStart();

        if (mCountDownIsFinished) {
            if (!APP_LOGGED_IN) {
                if (this.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getBoolean(AUTOSAVE, false)) {
                    onSave();
                    MASTER_KEY = null;
                    TEMP_PIN = null;
                }

                ACTIVITY_INTENT = new Intent(this, LoginActivity.class);
                ACTIVITY_INTENT.putExtra("lastActivity", "PAYMENTINFO_EDIT");
                ACTIVITY_INTENT.putExtra("lastDatabase", mData);

                this.finish(); // CLEAN UP AND END
                this.startActivity(ACTIVITY_INTENT); // GO TO LOGIN ACTIVITY

                System.gc();
            }
        } else {
            if (mCountDownTimer != null) {
                System.out.println("Cancel Called!");
                mCountDownTimer.cancel();
            }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (ACTIVITY_INTENT == null) // NO PENDING ACTIVITIES ???(MAIN)--->(EDIT)???
        {
            if (this.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getBoolean(AUTOSAVE, false)) {
                onSave();
            }

            ACTIVITY_INTENT = new Intent(this, MainActivity.class);
            finish();
            this.startActivity(ACTIVITY_INTENT);
        }
    }
    @Override
    public void onPause() {
        super.onPause();

        if (!this.isFinishing()) { // HOME AND TABS AND SCREEN OFF
            if (ACTIVITY_INTENT == null) // NO PENDING ACTIVITIES ???(MAIN)--->(EDIT)???
            {
                if (!this.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getBoolean(AUTOSAVE, false)) {
                    new LogoutProtocol().logoutExecuteAutosaveOff(this);
                } else {
                    new LogoutProtocol().logoutExecuteAutosaveOn(this);
                }
            }
        }
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
}