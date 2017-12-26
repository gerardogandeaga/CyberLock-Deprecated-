package com.gerardogandeaga.cyberlock.activities.core;

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

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.activities.clearances.ActivityLogin;
import com.gerardogandeaga.cyberlock.activities.core.edit.EditGraphics;
import com.gerardogandeaga.cyberlock.activities.dialogs.DialogColourTag;
import com.gerardogandeaga.cyberlock.crypto.CryptoContent;
import com.gerardogandeaga.cyberlock.sqlite.data.MasterDatabaseAccess;
import com.gerardogandeaga.cyberlock.sqlite.data.RawDataPackage;
import com.gerardogandeaga.cyberlock.support.LogoutProtocol;
import com.gerardogandeaga.cyberlock.support.graphics.DrawableColours;
import com.gerardogandeaga.cyberlock.support.handlers.extractors.ContentHandler;

import static com.gerardogandeaga.cyberlock.support.Globals.AUTOSAVE;
import static com.gerardogandeaga.cyberlock.support.Globals.DIRECTORY;
import static com.gerardogandeaga.cyberlock.support.Globals.MASTER_KEY;
import static com.gerardogandeaga.cyberlock.support.Globals.TEMP_PIN;
import static com.gerardogandeaga.cyberlock.support.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.support.LogoutProtocol.APP_LOGGED_IN;
import static com.gerardogandeaga.cyberlock.support.LogoutProtocol.mIsCountDownTimerFinished;
import static com.gerardogandeaga.cyberlock.support.LogoutProtocol.mCountDownTimer;

public class ActivityEdit extends AppCompatActivity implements View.OnClickListener {
    private Context mContext = this;
    private View mView;
    private CryptoContent cc;

    // RawDataPackage variables
    private boolean mIsNew = true;
    private boolean mIsAutoSave = false;
    private RawDataPackage mRawDataPackage;
    private ContentHandler mContentHandler;
    private EditGraphics mEditGraphics;

    private String TYPE;
    private static final String[] ARGS = new String[]{ "TYPE_NOTE", "TYPE_PAYMENTINFO", "TYPE_LOGININFO" };

    public static String mColour;

    private DialogColourTag mDialogColourTag;

    // Widgets

    // Global
    private EditText mEtLabel;
    private EditText mEtNotes;
    private TextView mTvDate;
    private ImageView mImgTag;
    // Notes
    private EditText mEtNote;
    // Paymentinfo
    private ImageView mIcon;
    private EditText mEtCardName, mEtCardNumber, mEtCardExpire, mEtCardCVV;
    private Spinner mSpCardSelect;
    //
    private String mCardType;
    private ArrayAdapter<CharSequence> mAdapter;
    // Logininfo
    private EditText mEtUrl, mEtEmail, mEtUsername, mEtPassword;

    // Create methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        ACTIVITY_INTENT = null;
        this.mIsAutoSave = getSharedPreferences(DIRECTORY, MODE_PRIVATE).getBoolean(AUTOSAVE, false);

        // Edit tools
        this.cc = new CryptoContent(this);
        this.mEditGraphics = new EditGraphics(this);

        // Activity creation
        extractBundle(); // Layout
        // Create tag dialog object
        this.mDialogColourTag = new DialogColourTag(this, mEditGraphics, mImgTag);
    }

    private void setupSupportActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setHomeAsUpIndicator(DrawableColours.mutateHomeAsUpIndicatorDrawable(
                this, this.getResources().getDrawable(R.drawable.ic_back)));
    }

    private void extractBundle() {
        /*
        When activating the editor there are 3 possible states in which is will enter:
        STATE 1 : Completely new (When it is called by the ADD function and  is not a data item yet)
        STATE 2 : Floating raw data item (When editor is suspended by the logout protocol but has
                                          not been saved in the database master database accessor)
        STATE 3 : Saved raw data item (When the data item has already been saved and is merely
                                       going to get updated)
        */
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            this.mRawDataPackage = (RawDataPackage) bundle.get("data");
            this.mIsNew = (mRawDataPackage == null);

            if (!mIsNew) {
                this.mContentHandler = new ContentHandler(this, mRawDataPackage);
                switch (mRawDataPackage.getType(cc)) {
                    case "TYPE_NOTE":        setupLayoutNote(); TYPE = ARGS[0]; break;
                    case "TYPE_PAYMENTINFO": setupLayoutPaymentInfo(); TYPE = ARGS[1]; break;
                    case "TYPE_LOGININFO":   setupLayoutLoginInfo(); TYPE = ARGS[2]; break;
                }
                // Check if data item already exists
                containsData(); // Will alter between STATE 2 & 3 by switching mIsNew
                // New data override!!!
                if (!bundle.getBoolean("isNew?")) {
                    this.mIsNew = false; // Will alter between STATE 2 & 3 by switching mIsNew
                }
            } else { // If data is completely new
                switch ((String) bundle.get("type")) { // STATE 1
                    case "TYPE_NOTE":        setupLayoutNote(); TYPE = ARGS[0]; break;
                    case "TYPE_PAYMENTINFO": setupLayoutPaymentInfo(); TYPE = ARGS[1]; break;
                    case "TYPE_LOGININFO":   setupLayoutLoginInfo(); TYPE = ARGS[2]; break;
                }
            }
            bundle.remove("data");
            bundle.remove("type");
        }
    }

    private void containsData() {
        MasterDatabaseAccess masterDatabaseAccess = MasterDatabaseAccess.getInstance(this);
        masterDatabaseAccess.open();
        System.out.println("Is New ? 1 : " + mIsNew);
        this.mIsNew = masterDatabaseAccess.containsData(this.mRawDataPackage);
        System.out.println("Is New ? 2 : " + mIsNew);
        masterDatabaseAccess.close();
    }

    // Layouts
    private void setupMainWidgets() {
        // Main widgets
        this.mEtLabel = findViewById(R.id.etLabel);
        this.mTvDate = findViewById(R.id.tvDate);
        this.mImgTag = findViewById(R.id.imgTag);

        this.mImgTag.setOnClickListener(this);
    }
    //
    private void setupLayoutNote() {
        mView = View.inflate(this, R.layout.activity_edit_note, null);
        setContentView(mView);
        setupSupportActionBar(); // Action bar
        // Widgets
        setupMainWidgets();
        mEtNote = findViewById(R.id.etText);

        setupDataNote();
    }
    private void setupLayoutPaymentInfo() {
        mView = View.inflate(this, R.layout.activity_edit_paymentinfo, null);
        setContentView(mView);
        setupSupportActionBar(); // Action bar

        this.mIcon = findViewById(R.id.imgIcon);
        // Widgets
        setupMainWidgets();
        mEtCardName = findViewById(R.id.etCardName);
        mEtCardNumber = findViewById(R.id.etCardNumber);
        mEtCardExpire = findViewById(R.id.etCardExpire);
        mEtCardCVV = findViewById(R.id.etCardSecCode);
        mEtNotes = findViewById(R.id.etNotes);

        mSpCardSelect = findViewById(R.id.spCardSelect);
        //
        mAdapter = ArrayAdapter.createFromResource(this, R.array.CardType_array, R.layout.spinner_setting_text);
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
                    mIcon.setImageDrawable(mEditGraphics.getCardImage(mCardType));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        setupDataPaymentInfo();
    }
    private void setupLayoutLoginInfo() {
        mView = View.inflate(this, R.layout.activity_edit_logininfo, null);
        setContentView(mView);
        setupSupportActionBar(); // Action bar
        // Widgets
        setupMainWidgets();
        mEtUrl = findViewById(R.id.etUrl);
        mEtUsername = findViewById(R.id.etUsername);
        mEtEmail = findViewById(R.id.etEmail);
        mEtPassword = findViewById(R.id.etPassword);
        mEtNotes = findViewById(R.id.etNotes);

        setupDataLoginInfo();
    }
    // Pull data
    private void setupDataNote() {
        if (mIsNew) {
            setLabel(null);
            setDate(null);
            setTag(null);
        } else {
            setLabel(mContentHandler.mLabel);
            setDate(mContentHandler.mDate);
            setTag(mContentHandler.mTag);

            mColour = mContentHandler.mTag;
            // Set note
            mEtNote.setText(mContentHandler.mNote);
        }
    }
    private void setupDataPaymentInfo() {
        if (mIsNew) {
            setLabel(null);
            setDate(null);
            setTag(null);
        } else {
            setLabel(mContentHandler.mLabel);
            setDate(mContentHandler.mDate);
            setTag(mContentHandler.mTag);

            mColour = mContentHandler.mTag;
            // Set name, number, expiry, cvv, cardType
            mEtCardName.setText(mContentHandler.mName);
            mEtCardNumber.setText(mContentHandler.mNumber);
            mEtCardExpire.setText(mContentHandler.mExpiry);
            mEtCardCVV.setText(mContentHandler.mCVV);
            //
            int spinnerPosition = mAdapter.getPosition(mContentHandler.mCardType);
            mSpCardSelect.setSelection(spinnerPosition);
            mEtNotes.setText(mContentHandler.mNote);

            mIcon.setImageDrawable(mEditGraphics.getCardImage(mContentHandler.mCardType));
        }
    }
    private void setupDataLoginInfo() {
        if (mIsNew) {
            setLabel(null);
            setDate(null);
            setTag(null);
        } else {
            setLabel(mContentHandler.mLabel);
            setDate(mContentHandler.mDate);
            setTag(mContentHandler.mTag);

            mColour = mContentHandler.mTag;
            // Set url, email, username, password,
            mEtUrl.setText(mContentHandler.mUrl);
            mEtEmail.setText(mContentHandler.mEmail);
            mEtUsername.setText(mContentHandler.mUsername);
            mEtPassword.setText(mContentHandler.mPassword);
            mEtNotes.setText(mContentHandler.mNote);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);

        DrawableColours.mutateMenuItems(this, menu);

        return true;
    }

    // On click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgTag:
                mDialogColourTag.initializeDialog();
                break;
        }
    }
    // --------

    // Actions
    private void onSave() {
        MasterDatabaseAccess masterDatabaseAccess = MasterDatabaseAccess.getInstance(this);
        masterDatabaseAccess.open();

        String tmpContent = getViewData();

        // Saving
        if (!tmpContent.isEmpty()) {
            if (mIsNew) {
                masterDatabaseAccess.save(getData(
                        mEtLabel.getText().toString(), tmpContent, mEditGraphics.getColourId(mIsNew, mRawDataPackage, cc)));
            } else {
                masterDatabaseAccess.update(getData(
                        mEtLabel.getText().toString(), tmpContent, mEditGraphics.getColourId(mIsNew, mRawDataPackage, cc)));
            }
            masterDatabaseAccess.close();
        } else {
            Toast.makeText(this, "No Content To Save", Toast.LENGTH_SHORT).show();
            masterDatabaseAccess.close();
        }
    }
    private void onCancel() {
        ACTIVITY_INTENT = new Intent(this, ActivityMain.class);
        finish();
        startActivity(ACTIVITY_INTENT);
    }
    // -------

    // Widget setters
    private void setLabel(String label) {
        if (label != null) {
            mEtLabel.setText(label);
        }
    }
    private void setDate(String date) {
        if (date != null) {
            mTvDate.setText("Updated: " + date);
        }
    }
    private void setTag(String colour) {
        if (colour != null) {
            mEditGraphics.alterTagColour(mImgTag, colour);
        }
    }

    // Getters and setters for saving
    private String getViewData() {
        // GETTING AND FORMATTING THE CONTENT CONDITIONALLY
        if (TYPE.matches(ARGS[0])) {
            final String note = mEtNote.getText().toString();

            final String format = "%s";

            return String.format(format,
                    note);
        } else if (TYPE.matches(ARGS[1])) {
            final String cardName = mEtCardName.getText().toString();
            final String cardNumber = mEtCardNumber.getText().toString();
            final String cardType = mCardType;
            final String cardExpire = mEtCardExpire.getText().toString();
            final String cardSecCode = mEtCardCVV.getText().toString();
            final String notes = mEtNotes.getText().toString();

            final String format = "%s\n%s\n%s\n%s\n%s\n%s";

            return String.format(format,
                    cardName, cardNumber, cardType, cardExpire, cardSecCode, notes);
        } else if (TYPE.matches(ARGS[2])) {
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
    private RawDataPackage getData(String label, String content, String colour) {
        if (mIsNew) {
            RawDataPackage tmp = new RawDataPackage();

            String tmpType;
            switch (TYPE) {
                case ("TYPE_NOTE"):        tmpType = "TYPE_NOTE"; break;
                case ("TYPE_PAYMENTINFO"): tmpType = "TYPE_PAYMENTINFO"; break;
                case ("TYPE_LOGININFO"):   tmpType = "TYPE_LOGININFO"; break;
                default:                   tmpType = "TYPE_NOTE"; break; // TODO CREATE A "COULD NOT READ TYPE" DIALOG ALLOWING FOR AN EDIT
            }
            tmp.setType(cc, tmpType);
            tmp.setColourTag(cc, colour);
            tmp.setLabel(cc, label);
            tmp.setContent(cc, content);

            return tmp;
        } else {
            mRawDataPackage.setColourTag(cc, mEditGraphics.getColourId(false, mRawDataPackage, cc));
            mRawDataPackage.setLabel(cc, label);
            mRawDataPackage.setContent(cc, content);

            return mRawDataPackage;
        }
    }
    // ----------------------------------

    // THIS IS THE START OF THE SCRIPT FOR THE "TO LOGIN FUNCTIONS" THIS DETECTS THE ON PRESSED, START, TABS AND HOME BUTTONS IN ORDER TO INITIALIZE A SECURITY "FAIL-SAFE"
    @Override protected void onStart() {
        super.onStart();

        if (mIsCountDownTimerFinished) {
            if (!APP_LOGGED_IN) {
                // If auto save
                if (getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getBoolean(AUTOSAVE, false)) {
                    onSave();
                    MASTER_KEY = null;
                    TEMP_PIN = null;
                }
                finish();
                startActivity(ACTIVITY_INTENT);
                ACTIVITY_INTENT = null;
            }
        } else {
            if (mCountDownTimer != null) {
                mCountDownTimer.cancel();
                ACTIVITY_INTENT = null;
            }
        }
    }
    @Override public void onBackPressed() {
        super.onBackPressed();
        if (ACTIVITY_INTENT == null) {
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

        if (!isFinishing()) { // HOME AND TABS AND SCREEN OFF
            if (ACTIVITY_INTENT == null) {

                // Load data into intent
                ACTIVITY_INTENT = new Intent(this, ActivityLogin.class);
                ACTIVITY_INTENT.putExtra("edit?", true);
                ACTIVITY_INTENT.putExtra("isNew?", mIsNew);
                ACTIVITY_INTENT.putExtra("lastDB", getData(
                        mEtLabel.getText().toString(),
                        getViewData(),
                        mEditGraphics.getColourId(mIsNew, mRawDataPackage, cc)));
                // ---------------------

                if (!getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getBoolean(AUTOSAVE, false)) {
                    new LogoutProtocol().logoutExecuteAutosaveOff(this);
                } else {
                    new LogoutProtocol().logoutExecuteAutosaveOn(this);
                }
            }
        }
    }
    // --------------------------------------------------------------------------------------------------------------------------------------------------------------------
}