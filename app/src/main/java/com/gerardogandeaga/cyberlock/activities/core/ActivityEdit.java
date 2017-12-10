package com.gerardogandeaga.cyberlock.activities.core;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.activities.clearances.LoginActivity;
import com.gerardogandeaga.cyberlock.crypto.CryptoContent;
import com.gerardogandeaga.cyberlock.sqlite.data.MasterDatabaseAccess;
import com.gerardogandeaga.cyberlock.sqlite.data.RawData;
import com.gerardogandeaga.cyberlock.support.Globals;
import com.gerardogandeaga.cyberlock.support.LogoutProtocol;
import com.gerardogandeaga.cyberlock.support.handlers.RawDataHandler;

import org.jetbrains.annotations.Contract;

import static com.gerardogandeaga.cyberlock.support.Globals.AUTOSAVE;
import static com.gerardogandeaga.cyberlock.support.Globals.DIRECTORY;
import static com.gerardogandeaga.cyberlock.support.Globals.MASTER_KEY;
import static com.gerardogandeaga.cyberlock.support.Globals.TEMP_PIN;
import static com.gerardogandeaga.cyberlock.support.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.support.LogoutProtocol.APP_LOGGED_IN;
import static com.gerardogandeaga.cyberlock.support.LogoutProtocol.mCountDownIsFinished;
import static com.gerardogandeaga.cyberlock.support.LogoutProtocol.mCountDownTimer;

public class ActivityEdit extends AppCompatActivity implements View.OnClickListener , View.OnLongClickListener {
    private Context mContext = this;
    private CryptoContent mCryptoContent;

    // RawData variables
    private boolean mIsNew = true;
    private boolean mIsAutoSave = false;
    private int TYPE;
    private RawData mRawData;
    private RawDataHandler mRawDataHandler;

    private String mTag;
    private CustomDialogs mCustomDialogs;

    // Widgets
    // Global
    private EditText mEtLabel;
    private EditText mEtNotes;
    private TextView mTvDate;
    // Notes
    private EditText mEtNote;
    // Paymentinfo
    private EditText mEtCardName, mEtCardNumber, mEtCardExpire, mEtCardCVV;
    private Spinner mSpCardSelect;
    //
    private String mCardType;
    private ArrayAdapter<CharSequence> mAdapter;
    // Logininfo
    private ImageButton mBtnColourTag;
    private EditText mEtUrl, mEtEmail, mEtUsername, mEtPassword;

    // Create methods
    @Override protected void onCreate(Bundle savedInstanceState) {
        Globals.COLORSCHEME(this);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        mCryptoContent = new CryptoContent(mContext);
        super.onCreate(savedInstanceState);

        setupLayoutMain();
    }
    @Override public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        //
        PorterDuff.Mode mode = PorterDuff.Mode.SRC_ATOP;
        //
        menu.findItem(R.id.miSave).getIcon().mutate().setColorFilter(setMenuItemsColour(), mode);
        menu.findItem(R.id.miCancel).getIcon().mutate().setColorFilter(setMenuItemsColour(), mode);

        return true;
    }
    private void setupLayoutMain() {
        ACTIVITY_INTENT = null;
        // RawData
        mCustomDialogs = new CustomDialogs(this);
        mIsAutoSave = getSharedPreferences(DIRECTORY, MODE_PRIVATE).getBoolean(AUTOSAVE, false);
        // View then widgets
        setupActivityMain();
    }
    private void setupGlobalWidgets() {
        mBtnColourTag = (ImageButton) findViewById(R.id.btnColourTag);
        mTvDate = (TextView) findViewById(R.id.tvDate);
        mEtLabel = (EditText) findViewById(R.id.etLabel);
        //
        mBtnColourTag.setOnClickListener(this);
        mBtnColourTag.setOnLongClickListener(this);
    }
    private void setupSupportActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //
        final Drawable drawerIcon = getResources().getDrawable(R.drawable.ic_back);
        drawerIcon.mutate().setColorFilter(setMenuItemsColour(), PorterDuff.Mode.SRC_ATOP);
        //
        getSupportActionBar().setHomeAsUpIndicator(drawerIcon);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);
    }
    private void setupActivityMain() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mIsNew = false;
            mRawData = (RawData) extras.get("data");
            mRawDataHandler = new RawDataHandler(mContext, mRawData);
            if (mRawData != null) {
                // IF ALREADY EXISTING DATABASE
                switch (mRawData.getType(mCryptoContent)) {
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
                    case (1): setupActivityNote(); break;
                    case (2): setupActivityPaymentInfo(); break;
                    case (3): setupActivityLoginInfo(); break;
                }
                extras.remove("type");
            }
        }
    }
    private int setMenuItemsColour() {
        return getResources().getColor(R.color.matLightWhiteYellow);
    }
    // Widget setters
    private String setLabel() {
        if (mRawData != null) {
            return mRawData.getLabel();
        } else {
            return "";
        }
    }                                                       // Labels
    public void setTag(String tag) {
        switch (tag){
            case "COL_BLUE": mBtnColourTag.setColorFilter(getResources().getColor(R.color.coltag_blue)); break;
            case "COL_RED": mBtnColourTag.setColorFilter(getResources().getColor(R.color.coltag_red)); break;
            case "COL_GREEN": mBtnColourTag.setColorFilter(getResources().getColor(R.color.coltag_green)); break;
            case "COL_YELLOW": mBtnColourTag.setColorFilter(getResources().getColor(R.color.coltag_yellow)); break;
            case "COL_PURPLE": mBtnColourTag.setColorFilter(getResources().getColor(R.color.coltag_purple)); break;
            case "COL_ORANGE": mBtnColourTag.setColorFilter(getResources().getColor(R.color.coltag_orange)); break;
            default: break;
        }
    }                                      // Colour tag
    public void setGlobalViews(String label, String date, String colourTag) {
        setupSupportActionBar();
        setupGlobalWidgets();

        mEtLabel.setText(label);
        setTag(colourTag);
        if (date != null) { mTvDate.setText("Updated: " + date); } else { mTvDate.setText(""); }
    }   // Everything else

    // Initialize widgets
    private void setupLayoutNote() {
        setContentView(R.layout.activity_edit_note);
        // Widgets
        mEtNote = (EditText) findViewById(R.id.etText);
    }
    private void setupLayoutPaymentInfo() {
        setContentView(R.layout.activity_edit_paymentinfo);
        // Widgets
        mEtCardName = (EditText) findViewById(R.id.etCardName);
        mEtCardNumber = (EditText) findViewById(R.id.etCardNumber);
        mEtCardExpire = (EditText) findViewById(R.id.etCardExpire);
        mEtCardCVV = (EditText) findViewById(R.id.etCardSecCode);
        mEtNotes = (EditText) findViewById(R.id.etNotes);

        mSpCardSelect = (Spinner) findViewById(R.id.spCardSelect);
        //
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
                    mEtCardNumber.setText(data + " ");
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
        // Widgets
        mEtUrl = (EditText) findViewById(R.id.etUrl);
        mEtUsername = (EditText) findViewById(R.id.etUsername);
        mEtEmail = (EditText) findViewById(R.id.etEmail);
        mEtPassword = (EditText) findViewById(R.id.etPassword);
        mEtNotes = (EditText) findViewById(R.id.etNotes);
    }
    // Pull data
    private void setupActivityNote() {
        setupLayoutNote();

        if (mIsNew) {
            setGlobalViews(setLabel(), null, "null");
        } else {
            // Set label, date, tag
            setGlobalViews(mRawDataHandler.mLabel, mRawDataHandler.mDate, mRawDataHandler.mTag);
            mTag = mRawDataHandler.mTag;
            // Set note
            mEtNote.setText(mRawDataHandler.mNote);
        }
    }
    private void setupActivityPaymentInfo() {
        setupLayoutPaymentInfo();

        if (mIsNew) {
            setGlobalViews(setLabel(), null, "null");
        } else {
            // Set label, date, tag
            setGlobalViews(mRawDataHandler.mLabel, mRawDataHandler.mDate, mRawDataHandler.mTag);
            mTag = mRawDataHandler.mTag;
            // Set name, number, expiry, cvv, cardType
            mEtCardName.setText(mRawDataHandler.mName);
            mEtCardNumber.setText(mRawDataHandler.mNumber);
            mEtCardExpire.setText(mRawDataHandler.mExpiry);
            mEtCardCVV.setText(mRawDataHandler.mCVV);
            //
            int spinnerPosition = mAdapter.getPosition(mRawDataHandler.mCardtype);
            mSpCardSelect.setSelection(spinnerPosition);
            mEtNotes.setText(mRawDataHandler.mNote);
        }
    }
    private void setupActivityLoginInfo() {
        setupLayoutLoginInfo();

        if (mIsNew) {
            setGlobalViews(setLabel(), null, "null");
        } else {
            // Set label, date, tag
            setGlobalViews(mRawDataHandler.mLabel, mRawDataHandler.mDate, mRawDataHandler.mTag);
            mTag = mRawDataHandler.mTag;
            // Set url, email, username, password,
            mEtUrl.setText(mRawDataHandler.mUrl);
            mEtEmail.setText(mRawDataHandler.mEmail);
            mEtUsername.setText(mRawDataHandler.mUsername);
            mEtPassword.setText(mRawDataHandler.mPassword);
            mEtNotes.setText(mRawDataHandler.mNote);
        }
    }
    // ----------------------------------------------------------------------------------

    // On click
    @Override public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case (R.id.miSave):
                if (!mIsAutoSave) {
                    onSave();
                    onBackPressed();
                } else {
                    onBackPressed();
                }
                return true;
            case (R.id.miCancel):
                onCancel();
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnColourTag: mCustomDialogs.createColourPickDialog(); break;
        }
    }
    @Override public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.btnColourTag: Toast.makeText(mContext, "Select Colour Tag", Toast.LENGTH_SHORT).show(); return true;
        }
        return false;
    }
    // --------

    // Actions
    private void onSave() {
        MasterDatabaseAccess masterDatabaseAccess = MasterDatabaseAccess.getInstance(this);
        masterDatabaseAccess.open();

        String tmpContent = getWidgetData();

        // SAVING
        if (dataIsValid(tmpContent)) {
            if (mRawData == null) {
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
        ACTIVITY_INTENT = new Intent(this, ActivityMain.class);
        finish();
        startActivity(ACTIVITY_INTENT);
    }
    // -------

    // Getters and setters for saving
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
            final String cardSecCode = mEtCardCVV.getText().toString();
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
    private RawData getData(String tmpContent) {
        String colourTag, label, content;

        label = mEtLabel.getText().toString();
        content = tmpContent;
        colourTag = mCustomDialogs.getTmpColour();
        // SET COLOUR CONDITIONALLY
        if (!mIsNew) {
            if (mCustomDialogs.getTmpColour() == null) {
                colourTag = mTag;
            } else {
                colourTag = mCustomDialogs.getTmpColour();
            }
        }

        return setData(colourTag, label, content);
    }
    private RawData setData(String colourTag, String label, String content) {
        if (mIsNew) {
            RawData tmp = new RawData();

            String tmpType;
            switch (TYPE) {
                case (1): tmpType = "TYPE_NOTE"; break;
                case (2): tmpType = "TYPE_PAYMENTINFO"; break;
                case (3): tmpType = "TYPE_LOGININFO"; break;
                default:  tmpType = "TYPE_NOTE"; // TODO CREATE A "COULD NOT READ TYPE" DIALOG ALLOWING FOR AN EDIT
            }
            tmp.setType(mCryptoContent, tmpType);
            tmp.setColourTag(mCryptoContent, colourTag);
            tmp.setLabel(mCryptoContent, label);
            tmp.setContent(mCryptoContent, content);

            return tmp;
        } else {
            mRawData.setColourTag(mCryptoContent, colourTag);
            mRawData.setLabel(mCryptoContent, label);
            mRawData.setContent(mCryptoContent, content);

            return mRawData;
        }
    }
    // ----------------------------------

    // THIS IS THE START OF THE SCRIPT FOR THE "TO LOGIN FUNCTIONS" THIS DETECTS THE ON PRESSED, START, TABS AND HOME BUTTONS IN ORDER TO INITIALIZE A SECURITY "FAIL-SAFE"
    @Override protected void onStart() {
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
                ACTIVITY_INTENT.putExtra("lastDB", mRawData);

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
    @Override public void onBackPressed() {
        super.onBackPressed();
        if (ACTIVITY_INTENT == null) // NO PENDING ACTIVITIES ???(MAIN)--->(EDIT)???
        {
            if (getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getBoolean(AUTOSAVE, false)) {
                onSave();
            }

            ACTIVITY_INTENT = new Intent(this, ActivityMain.class);
            finish();
            startActivity(ACTIVITY_INTENT);
            overridePendingTransition(R.anim.anim_slide_inleft, R.anim.anim_slide_outright);
        }
    }
    @Override public void onPause() {
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

    // Custom dialog class
    private class CustomDialogs implements View.OnClickListener {
        private Context mContext;
        private AlertDialog mAlertDialog;
        //
        private String mTmpColour;

        private CustomDialogs(Context context) {
            mContext = context;
        }

        private void createColourPickDialog() {
            View dv = View.inflate(mContext, R.layout.dialog_colourtag, null);
            ImageView blue = (ImageView) dv.findViewById(R.id.imgBlue);
            ImageView red = (ImageView) dv.findViewById(R.id.imgRed);
            ImageView green = (ImageView) dv.findViewById(R.id.imgGreen);
            ImageView yellow = (ImageView) dv.findViewById(R.id.imgYellow);
            ImageView purple = (ImageView) dv.findViewById(R.id.imgPurple);
            ImageView orange = (ImageView) dv.findViewById(R.id.imgOrange);

            blue.setOnClickListener(this);
            red.setOnClickListener(this);
            green.setOnClickListener(this);
            yellow.setOnClickListener(this);
            purple.setOnClickListener(this);
            orange.setOnClickListener(this);

            // Dialog builder
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setView(dv);
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override public void onCancel(DialogInterface dialog) {
                    mAlertDialog = null;
                }
            });
            // Dialog show
            mAlertDialog = builder.show();
            mAlertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        @Contract(pure = true) private String getTmpColour() {
            return mTmpColour;
        }
        private void setTmpColour(String tmpColour) {
            mTmpColour = tmpColour;
            mAlertDialog.dismiss();
            mAlertDialog = null;
            setTag(mTmpColour);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgBlue: setTmpColour("COL_BLUE"); break;
                case R.id.imgRed: setTmpColour("COL_RED"); break;
                case R.id.imgGreen: setTmpColour("COL_GREEN"); break;
                case R.id.imgYellow: setTmpColour("COL_YELLOW"); break;
                case R.id.imgPurple: setTmpColour("COL_PURPLE"); break;
                case R.id.imgOrange: setTmpColour("COL_ORANGE"); break;
                default: setTmpColour("DEFAULT"); break;
            }
        }
    }
}