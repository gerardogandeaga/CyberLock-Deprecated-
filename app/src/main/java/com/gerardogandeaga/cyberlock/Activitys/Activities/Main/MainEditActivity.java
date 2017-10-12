package com.gerardogandeaga.cyberlock.Activitys.Activities.Main;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LoginActivity;
import com.gerardogandeaga.cyberlock.Crypto.CryptContent;
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
    // DATA VARIABLES
    private boolean mIsNew = true;
    private boolean mIsAutoSave = false;
    private int TYPE;
    private CryptContent mCRYPTCONTENT;
    private Data mData;

    private String mColourTag;
    private CustomDialogs mCustomDialogs;

    // WIDGETS
    // MAIN WIDGETS
    private EditText mEtNotes;

    // MEMO WIDGETS
    private EditText
            mEtMemo;

    // PAYMENTINFO WIDGETS
    private EditText
            mEtCardName,
            mEtCardNumber,
            mEtCardExpire,
            mEtCardSecCode,
            mEtQuestion1,
            mEtQuestion2,
            mEtAnswer1,
            mEtAnswer2;
    private Spinner mSpCardSelect;
    // SUB DATA
    private String mCardType;
    private ArrayAdapter<CharSequence> mAdapter;

    // LOGININFO WIDGETS
    private EditText
            mEtUrl,
            mEtUsername,
            mEtEmail,
            mEtPassword;

    // INITIAL ON CREATE METHODS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Globals.COLORSCHEME(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        super.onCreate(savedInstanceState);

        setupLayoutMain();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        System.out.println("Menu Created!");
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
        mCRYPTCONTENT = new CryptContent(this);

        if (extras != null) {
            mIsNew = false;
            mData = (Data) extras.get("data");
            if (mData != null) {
                // IF ALREADY EXISTING DATABASE
                switch (mData.getType()) {
                    case ("TYPE_MEMO"):
                        TYPE = 1;
                        setupActivityMemo();
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
                        setupActivityMemo();
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

    // SUB SETUP ACTIVITIES CONDITIONAL TO DATA TYPE (IE. MEMO, PAYMENT-INFO, LOGIN-INFO)
    // INITIALIZE WIDGETS
    private void setupLayoutMemo() {
        setContentView(R.layout.activity_edit_memo);
        System.out.println("Layout Created!");
        // WIDGETS
        mEtMemo = (EditText) findViewById(R.id.etText);
    }
    private void setupLayoutPaymentInfo() {
        setContentView(R.layout.activity_edit_paymentinfo);
        // WIDGETS
        mEtCardName = (EditText) findViewById(R.id.etCardName);
        mEtCardNumber = (EditText) findViewById(R.id.etCardNumber);
        mEtCardExpire = (EditText) findViewById(R.id.etCardExpire);
        mEtCardSecCode = (EditText) findViewById(R.id.etCardSecCode);
        mEtQuestion1 = (EditText) findViewById(R.id.etQuestion1);
        mEtQuestion2 = (EditText) findViewById(R.id.etQuestion2);
        mEtAnswer1 = (EditText) findViewById(R.id.etAnswer1);
        mEtAnswer2 = (EditText) findViewById(R.id.etAnswer2);
        mEtNotes = (EditText) findViewById(R.id.etNotes);

        mSpCardSelect = (Spinner) findViewById(R.id.spCardSelect);

        mAdapter = ArrayAdapter.createFromResource(this, R.array.CardType_array, android.R.layout.simple_spinner_item);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
        mEtUrl = (EditText) findViewById(R.id.etUrl);
        mEtUsername = (EditText) findViewById(R.id.etUsername);
        mEtEmail = (EditText) findViewById(R.id.etEmail);
        mEtPassword = (EditText) findViewById(R.id.etPassword);
        mEtNotes = (EditText) findViewById(R.id.etNotes);
    }

    // SET DATA
    private void setupActivityMemo() {
        setupLayoutMemo();

        if (mIsNew) {
            setActionBarTitle(mCustomDialogs.getTmpLabel(), null);
        } else {
            // GETTERS
            final String colourTag = mData.getColourTag();
            final String date = mData.getDate();
            final String label = mCRYPTCONTENT.DECRYPT_CONTENT(mData.getLabel(), MASTER_KEY);
            final String content = mCRYPTCONTENT.DECRYPT_CONTENT(mData.getContent(), MASTER_KEY);

            // SETTERS
            setActionBarTitle(label, date);
            mColourTag = colourTag;

            String memo;
            if (content != null) {
                Scanner scanner = new Scanner(content);

                memo = scanner.nextLine();
                while (scanner.hasNextLine()) {
                    memo += "\n";
                    memo += scanner.nextLine();
                }
                scanner.close();
                mEtMemo.setText(memo); // SET THE MEMO FIELD
            }
        }
    }
    private void setupActivityPaymentInfo() {
        setupLayoutPaymentInfo();

        if (mIsNew) {
            setActionBarTitle(mCustomDialogs.getTmpLabel(), null);
        } else {
            // GETTERS
            final String colourTag = mData.getColourTag();
            final String date = mData.getDate();
            final String label = mCRYPTCONTENT.DECRYPT_CONTENT(mData.getLabel(), MASTER_KEY);
            final String content = mCRYPTCONTENT.DECRYPT_CONTENT(mData.getContent(), MASTER_KEY);

            // SETTERS
            setActionBarTitle(label, date);
            mColourTag = colourTag;

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
        }
    }
    private void setupActivityLoginInfo() {
        setupLayoutLoginInfo();

        if (mIsNew) {
            setActionBarTitle(mCustomDialogs.getTmpLabel(), null);
        } else {
            // GETTERS
            final String colourTag = mData.getColourTag();
            final String date = mData.getDate();
            final String label = mCRYPTCONTENT.DECRYPT_CONTENT(mData.getLabel(), MASTER_KEY);
            final String content = mCRYPTCONTENT.DECRYPT_CONTENT(mData.getContent(), MASTER_KEY);

            // SETTERS
            setActionBarTitle(label, date);
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
            case (R.id.action_label):
                mCustomDialogs.createLabelDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    // --------

    // ACTIVITY ACTIONS
    private void onSave() {
        MasterDatabaseAccess masterDatabaseAccess = MasterDatabaseAccess.getInstance(this);
        masterDatabaseAccess.open();

        String tmpContent = getWidgetData();
        // ----------------------------------

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
            final String memo = mEtMemo.getText().toString();
            final String format = "%s";

            final String tmpString = String.format(format,
                    memo);

            return tmpString;
        } else if (TYPE == 2) {
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

            return tmpString;
        } else if (TYPE == 3) {
            final String url = mEtUrl.getText().toString();
            final String username = mEtUsername.getText().toString();
            final String email = mEtEmail.getText().toString();
            final String password = mEtPassword.getText().toString();
            final String notes = mEtNotes.getText().toString();

            final String format = "%s\n%s\n%s\n%s\n%s";
            final String tmpString = String.format(format,
                    url, username, email, password, notes);

            return tmpString;
        }

        return "No Content To Save";
    }
    @Contract("!null -> true")
    private boolean dataIsValid(String content) {
        return content != null || !content.matches("");
    }
    private Data getData(String tmpContent) {
        String colourTag, label, content;

        label = mCustomDialogs.getTmpLabel();
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
        if (mData == null) {
            Data tmp = new Data();

            tmp.setType("TYPE_MEMO"); // TODO ENCRYPT TYPE!
            tmp.setColourTag(colourTag);
            tmp.setLabel(mCRYPTCONTENT.ENCRYPT_KEY(label, MASTER_KEY));
            tmp.setContent(mCRYPTCONTENT.ENCRYPT_KEY(content, MASTER_KEY));

            return tmp;
        } else {
            mData.setColourTag(colourTag);
            mData.setLabel(mCRYPTCONTENT.ENCRYPT_KEY(label, MASTER_KEY));
            mData.setContent(mCRYPTCONTENT.ENCRYPT_KEY(content, MASTER_KEY));

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
    public void setActionBarTitle(String label, String date) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(label);
        if (label != null) {
            getSupportActionBar().setSubtitle(date);
        } // PRIMARILY FOR NEW DOCUMENTS
    }

    // CUSTOM DIALOG INNER CLASS
    private class CustomDialogs implements View.OnClickListener {
        private Context mContext;
        private AlertDialog.Builder mAlertDialog;
        private Dialog mDialog;

        private String mCrrntLabel;
        private String mTmpLabel;
        private String mTmpColour;

        private CustomDialogs(Context context) {
            mContext = context;
        }

        // SET LABEL
        private void createLabelDialog() {
            mAlertDialog = new AlertDialog.Builder(mContext);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 10, 10, 10);

            // BUILD VIEW
            final EditText etLabel = new EditText(mContext);

            etLabel.setLayoutParams(params);

            // BUILD DIALOG
            mAlertDialog.setCancelable(false);
            mAlertDialog.setTitle("Input A New Label");
            mAlertDialog.setView(etLabel);

            mAlertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            mAlertDialog.setPositiveButton("SET", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setTmpLabel(etLabel.getText().toString());
                    dialog.dismiss();
                }
            });
            mAlertDialog.show();
        }

        @Contract(pure = true)
        private String getTmpLabel() {
            if (!mIsNew) {
                // SET THE CURRENT LABEL IF THERE IS ONE
                mCrrntLabel = getSupportActionBar().getTitle().toString();

                if (mTmpLabel != null) {
                    if (!mTmpLabel.matches("")) {
                        return mTmpLabel; // SET NEW LABEL
                    } else {
                        if (!mCrrntLabel.matches("") || mCrrntLabel != null) {
                            return mCrrntLabel; // IF THERE IS AN EXISTING LABEL
                        } else {
                            return "New Document"; // IF THERE IS NO CURRENT LABEL
                        }
                    }
                }
            }
            return "New Document";
        }

        private void setTmpLabel(String tmpLabel) {
            mTmpLabel = tmpLabel;
            getSupportActionBar().setTitle(getTmpLabel());
        }

        // COLOUR TAG CUSTOMIZATION
        private void createColourPickDialog() {
            mDialog = new Dialog(mContext);

            // BUILD DIALOG
            mDialog.setContentView(R.layout.dialog_colourtag);
            mDialog.setCanceledOnTouchOutside(true);
            // BUTTONS
            ImageView blue = (ImageView) mDialog.findViewById(R.id.imgBlue);
            ImageView red = (ImageView) mDialog.findViewById(R.id.imgRed);
            ImageView green = (ImageView) mDialog.findViewById(R.id.imgGreen);
            ImageView yellow = (ImageView) mDialog.findViewById(R.id.imgYellow);
            ImageView purple = (ImageView) mDialog.findViewById(R.id.imgPurple);
            ImageView orange = (ImageView) mDialog.findViewById(R.id.imgOrange);

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