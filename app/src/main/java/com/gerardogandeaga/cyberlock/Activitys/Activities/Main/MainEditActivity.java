package com.gerardogandeaga.cyberlock.Activitys.Activities.Main;

import android.app.Dialog;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LoginActivity;
import com.gerardogandeaga.cyberlock.Crypto.CryptoContent;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.ContentDatabase.Data;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.ContentDatabase.MasterDatabaseAccess;
import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.Supports.Globals;
import com.gerardogandeaga.cyberlock.Supports.LogoutProtocol;

import org.jetbrains.annotations.Contract;

import java.util.NoSuchElementException;
import java.util.Scanner;

import static com.gerardogandeaga.cyberlock.Supports.Globals.AUTOSAVE;
import static com.gerardogandeaga.cyberlock.Supports.Globals.DIRECTORY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.MASTER_KEY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.TEMP_PIN;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.APP_LOGGED_IN;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.mCountDownIsFinished;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.mCountDownTimer;

public class MainEditActivity extends AppCompatActivity {
    private Context mContext = this;
    private CryptoContent mCryptoContent;

    // DATA VARIABLES
    private boolean mIsNew = true;
    private boolean mIsAutoSave = false;
    private int TYPE;
    private Data mData;

    private String mColourTag;
    private CustomDialogs mCustomDialogs;

    // WIDGETS
    // MAIN WIDGETS
    private EditText mEtLabel;
    private EditText mEtNotes;
    private TextView mTvDate;

    // NOTE WIDGETS
    private EditText
            mEtNote;

    // PAYMENTINFO WIDGETS
    private EditText
            mEtCardName,
            mEtCardNumber,
            mEtCardExpire,
            mEtCardSecCode;
    private Spinner mSpCardSelect;
    // SUB DATA
    private String mCardType;
    private ArrayAdapter<CharSequence> mAdapter;

    // LOGININFO WIDGETS
    private EditText
            mEtUrl,
            mEtEmail,
            mEtUsername,
            mEtPassword;

    // INITIAL ON CREATE METHODS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Globals.COLORSCHEME(this);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        mCryptoContent = new CryptoContent(mContext);
        super.onCreate(savedInstanceState);

        setupLayoutMain();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);

//        for (int i = 0; i < menu.size(); i++) {
//            Drawable drawable = menu.getItem(i).getIcon();
//            if (drawable != null) {
//                drawable.mutate();
//                drawable.setColorFilter(
//                        getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
//            }
//        }

        return true;
    }
    private void setupLayoutMain() {
        ACTIVITY_INTENT = null;
        // DATA
        mCustomDialogs = new CustomDialogs(this);
        mIsAutoSave = getSharedPreferences(DIRECTORY, MODE_PRIVATE).getBoolean(AUTOSAVE, false);
        // WIDGETS
        setupActivityMain();
    }
    private void setupActivityMain() {
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            mIsNew = false;
            mData = (Data) extras.get("data");
            if (mData != null) {
                // IF ALREADY EXISTING DATABASE
                switch (mData.getType(mCryptoContent)) {
                    case ("TYPE_NOTE"):
                        TYPE = 1;
                        setupActivityNote();
                        break;
                    case ("TYPE_PAYMENTINFO"):
                        TYPE = 2;
                        setupActivityPaymentInfo();
                        break;
                    case ("TYPE_LOGININFO"):
                        TYPE = 3;
                        setupActivityLoginInfo();
                        break;
                }
                extras.remove("data");
            } else {
                // IF THIS IS A NEW DOCUMENT
                mIsNew = true;
                TYPE = (int) extras.get("type");
                switch (TYPE) {
                    case (1):
                        setupActivityNote();
                        break;
                    case (2):
                        setupActivityPaymentInfo();
                        break;
                    case (3):
                        setupActivityLoginInfo();
                        break;
                }
                extras.remove("type");
            }
        }
    }

    // SUB SETUP ACTIVITIES CONDITIONAL TO DATA TYPE (IE. NOTE, PAYMENT-INFO, LOGIN-INFO)
    // INITIALIZE WIDGETS
    private void setupLayoutNote() {
        setContentView(R.layout.activity_edit_note);
//        System.out.println("Layout Created!");
        // WIDGETS
        mTvDate = findViewById(R.id.tvDate);
        mEtLabel = findViewById(R.id.etLabel);
        mEtNote = findViewById(R.id.etText);
    }
    private void setupLayoutPaymentInfo() {
        setContentView(R.layout.activity_edit_paymentinfo);
        // WIDGETS
        mTvDate = findViewById(R.id.tvDate);
        mEtLabel = findViewById(R.id.etLabel);
        mEtCardName = findViewById(R.id.etCardName);
        mEtCardNumber = findViewById(R.id.etCardNumber);
        mEtCardExpire = findViewById(R.id.etCardExpire);
        mEtCardSecCode = findViewById(R.id.etCardSecCode);
        mEtNotes = findViewById(R.id.etNotes);

        mSpCardSelect = findViewById(R.id.spCardSelect);

        mAdapter = ArrayAdapter.createFromResource(this, R.array.CardType_array, android.R.layout.simple_spinner_item);
        mAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        mSpCardSelect.setAdapter(mAdapter);

        mEtCardNumber.addTextChangedListener(new TextWatcher() {
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
        mEtCardExpire.addTextChangedListener(new TextWatcher() {
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
        mSpCardSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
    private void setupLayoutLoginInfo() {
        setContentView(R.layout.activity_edit_logininfo);
        // WIDGETS
        mTvDate = findViewById(R.id.tvDate);
        mEtLabel = findViewById(R.id.etLabel);
        mEtUrl = findViewById(R.id.etUrl);
        mEtUsername = findViewById(R.id.etUsername);
        mEtEmail = findViewById(R.id.etEmail);
        mEtPassword = findViewById(R.id.etPassword);
        mEtNotes = findViewById(R.id.etNotes);
    }

    // SET DATA
    private void setupActivityNote() {
        setupLayoutNote();

        if (mIsNew) {
            setGlobalIdentifiers(setLabel(), null);
        } else {
            // GETTERS
            final String date = mData.getDate();
            final String colourTag = mData.getColourTag(mCryptoContent);
            final String label = mData.getLabel(mCryptoContent);
            final String content = mData.getContent(mCryptoContent);

            // SETTERS
            setGlobalIdentifiers(label, date);
            mColourTag = colourTag;

            String note;
            if (content != null) {
                Scanner scanner = new Scanner(content);

                note = scanner.nextLine();
                while (scanner.hasNextLine()) {
                    note += "\n";
                    note += scanner.nextLine();
                }
                scanner.close();
                mEtNote.setText(note); // SET THE NOTE FIELD
            }
        }
    }
    private void setupActivityPaymentInfo() {
        setupLayoutPaymentInfo();

        if (mIsNew) {
            setGlobalIdentifiers(setLabel(), null);
        } else {
            // GETTERS
            final String colourTag = mData.getColourTag(mCryptoContent);
            final String date = mData.getDate();
            final String label = mData.getLabel(mCryptoContent);
            final String content = mData.getContent(mCryptoContent);

            // SETTERS
            setGlobalIdentifiers(label, date);
            mColourTag = colourTag;

            String cardName;
            String cardNumber;
            String cardType;
            String cardExpire;
            String cardSecCode;
            String notes;

            if (content != null) {
                Scanner scanner = new Scanner(content);

                cardName = scanner.nextLine();
                cardNumber = scanner.nextLine();
                cardType = scanner.nextLine();
                cardExpire = scanner.nextLine();
                cardSecCode = scanner.nextLine();
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

                int spinnerPosition = mAdapter.getPosition(cardType);
                mSpCardSelect.setSelection(spinnerPosition);
            }
        }
    }
    private void setupActivityLoginInfo() {
        setupLayoutLoginInfo();

        if (mIsNew) {
            setGlobalIdentifiers(setLabel(), null);
        } else {
            // GETTERS
            final String date = mData.getDate();
            final String colourTag = mData.getColourTag(mCryptoContent);
            final String label = mData.getLabel(mCryptoContent);
            final String content = mData.getContent(mCryptoContent);

            // SETTERS
            setGlobalIdentifiers(label, date);
            mColourTag = colourTag;

            String url;
            String username;
            String email;
            String password;
            String notes;

            if (content != null) {
                Scanner scanner = new Scanner(content);

                url = scanner.nextLine();
                username = scanner.nextLine();
                email = scanner.nextLine();
                password = scanner.nextLine();
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

                mEtUrl.setText(url);
                mEtUsername.setText(username);
                mEtEmail.setText(email);
                mEtPassword.setText(password);
            }
        }
    }
    // ----------------------------------------------------------------------------------

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
                mCustomDialogs.createColourPickDialog();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    // --------

    // ACTIVITY ACTIONS
    private void onSave() {
        MasterDatabaseAccess masterDatabaseAccess = MasterDatabaseAccess.getInstance(this);
        masterDatabaseAccess.open();

        String tmpContent = getWidgetData();

        // SAVING
        if (dataIsValid(tmpContent)) {
            if (mData == null) {
                masterDatabaseAccess.save(getData(tmpContent)); // IF NEW
            } else {
                masterDatabaseAccess.update(getData(tmpContent)); // IF EXISTING
            }
            masterDatabaseAccess.close();
        } else {
            Toast.makeText(this, "No Content To Save", Toast.LENGTH_SHORT).show();
            masterDatabaseAccess.close(); // IF NOT CONTENT TO SAVE
        }
    }
    private void onCancel() {
        ACTIVITY_INTENT = new Intent(this, MainActivity.class);
        finish();
        startActivity(ACTIVITY_INTENT);
    }

    // DATA GETTER AND SETTERS FOR SAVING
    private String getWidgetData() {
        // GETTING AND FORMATTING THE CONTENT CONDITIONALLY
        if (TYPE == 1) {
            final String note = mEtNote.getText().toString();

            final String format = "%s";

            return String.format(format,
                    note);
        } else if (TYPE == 2) {
            final String cardName = mEtCardName.getText().toString();
            final String cardNumber = mEtCardNumber.getText().toString();
            final String cardType = mCardType;
            final String cardExpire = mEtCardExpire.getText().toString();
            final String cardSecCode = mEtCardSecCode.getText().toString();
            final String notes = mEtNotes.getText().toString();

            final String format = "%s\n%s\n%s\n%s\n%s\n%s";

            return String.format(format,
                    cardName, cardNumber, cardType, cardExpire, cardSecCode, notes);
        } else if (TYPE == 3) {
            final String url = mEtUrl.getText().toString();
            final String email = mEtEmail.getText().toString();
            final String username = mEtUsername.getText().toString();
            final String password = mEtPassword.getText().toString();
            final String notes = mEtNotes.getText().toString();

            final String format = "%s\n%s\n%s\n%s\n%s";

            return String.format(format,
                    url, email, username, password, notes);
        }
        return "";
    }
    @Contract("!null -> true")
    private boolean dataIsValid(String content) {
        return !content.matches("");
    }
    private Data getData(String tmpContent) {
        String colourTag, label, content;

        label = mEtLabel.getText().toString();
        content = tmpContent;
        colourTag = mCustomDialogs.getTmpColour();
        // SET COLOUR CONDITIONALLY
        if (!mIsNew) {
            if (mCustomDialogs.getTmpColour() == null) {
                colourTag = mColourTag;
            } else {
                colourTag = mCustomDialogs.getTmpColour();
            }
        }

        return setData(colourTag, label, content);
    }
    private Data setData(String colourTag, String label, String content) {
        if (mIsNew) {
            Data tmp = new Data();

            String tmpType;
            switch (TYPE) {
                case (1):
                    tmpType = "TYPE_NOTE";
                    break;
                case (2):
                    tmpType = "TYPE_PAYMENTINFO";
                    break;
                case (3):
                    tmpType = "TYPE_LOGININFO";
                    break;
                default:
                    tmpType = "TYPE_NOTE"; // TODO CREATE A "COULD NOT READ TYPE" DIALOG ALLOWING FOR AN EDIT
            }
            tmp.setType(mCryptoContent, tmpType);
            tmp.setColourTag(mCryptoContent, colourTag);
            tmp.setLabel(mCryptoContent, label);
            tmp.setContent(mCryptoContent, content);

            return tmp;
        } else {
            mData.setColourTag(mCryptoContent, colourTag);
            mData.setLabel(mCryptoContent, label);
            mData.setContent(mCryptoContent, content);

            return mData;
        }
    }
    // ----------------------------------

    // THIS IS THE START OF THE SCRIPT FOR THE "TO LOGIN FUNCTIONS" THIS DETECTS THE ON PRESSED, START, TABS AND HOME BUTTONS IN ORDER TO INITIALIZE A SECURITY "FAIL-SAFE"
    @Override
    protected void onStart() {
        super.onStart();

        if (mCountDownIsFinished) {
            if (!APP_LOGGED_IN) {
                if (getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getBoolean(AUTOSAVE, false)) {
                    onSave();
                    MASTER_KEY = null;
                    TEMP_PIN = null;
                }

                ACTIVITY_INTENT = new Intent(this, LoginActivity.class);
                ACTIVITY_INTENT.putExtra("edit?", true);
                ACTIVITY_INTENT.putExtra("lastDB", mData);

                finish(); // CLEAN UP AND END
                startActivity(ACTIVITY_INTENT); // GO TO LOGIN ACTIVITY

                System.gc();
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
            if (getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getBoolean(AUTOSAVE, false)) {
                onSave();
            }

            ACTIVITY_INTENT = new Intent(this, MainActivity.class);
            finish();
            startActivity(ACTIVITY_INTENT);
            overridePendingTransition(R.anim.anim_slide_inleft, R.anim.anim_slide_outright);
        }
    }
    @Override
    public void onPause() {
        super.onPause();

        if (!isFinishing()) // HOME AND TABS AND SCREEN OFF
        {
            if (ACTIVITY_INTENT == null) // NO PENDING ACTIVITIES ???(MAIN)--->(EDIT)???
            {
                if (!getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getBoolean(AUTOSAVE, false)) {
                    new LogoutProtocol().logoutExecuteAutosaveOff(this);
                } else {
                    new LogoutProtocol().logoutExecuteAutosaveOn(this);
                }
            }

        }
    }
    // --------------------------------------------------------------------------------------------------------------------------------------------------------------------

    // QUARANTINED FUNCTIONS
    private String setLabel() {
        if (mData != null) {
            return mData.getLabel();
        } else {
            return "";
        }
    }
    public void setGlobalIdentifiers(String label, String date) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);
        mEtLabel.setText(label);

        if (date != null) {
            mTvDate.setText("Updated: " + date);
        } else {
            mTvDate.setText("");
        }
    }

    // CUSTOM DIALOG INNER CLASS
    private class CustomDialogs implements View.OnClickListener {
        private Context mContext;
        private Dialog mDialog;

        private String mTmpColour;

        private CustomDialogs(Context context) {
            mContext = context;
        }

        // SET LABEL

        // COLOUR TAG CUSTOMIZATION
        private void createColourPickDialog() {
            mDialog = new Dialog(mContext);

            // BUILD DIALOG
            mDialog.setContentView(R.layout.dialog_colourtag);
            mDialog.setCanceledOnTouchOutside(true);
            // BUTTONS
            ImageView blue = mDialog.findViewById(R.id.imgBlue);
            ImageView red = mDialog.findViewById(R.id.imgRed);
            ImageView green = mDialog.findViewById(R.id.imgGreen);
            ImageView yellow = mDialog.findViewById(R.id.imgYellow);
            ImageView purple = mDialog.findViewById(R.id.imgPurple);
            ImageView orange = mDialog.findViewById(R.id.imgOrange);

            blue.setOnClickListener(this);
            red.setOnClickListener(this);
            green.setOnClickListener(this);
            yellow.setOnClickListener(this);
            purple.setOnClickListener(this);
            orange.setOnClickListener(this);

            mDialog.show();
        }
        private String getTmpColour() {
            return mTmpColour;
        }
        private void setTmpColour(String tmpColour) {
            mTmpColour = tmpColour;
            mDialog.dismiss();
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgBlue:
                    setTmpColour("COL_BLUE");
                    System.out.println("HI!");
                    break;
                case R.id.imgRed:
                    setTmpColour("COL_RED");
                    break;
                case R.id.imgGreen:
                    setTmpColour("COL_GREEN");
                    break;
                case R.id.imgYellow:
                    setTmpColour("COL_YELLOW");
                    break;
                case R.id.imgPurple:
                    setTmpColour("COL_PURPLE");
                    break;
                case R.id.imgOrange:
                    setTmpColour("COL_ORANGE");
                    break;
                default:
                    setTmpColour("DEFAULT");
                    break;
            }
        }
    }
}