package com.gerardogandeaga.cyberlock.activities.core;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
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
import com.gerardogandeaga.cyberlock.activities.dialogs.DialogFragmentTags;
import com.gerardogandeaga.cyberlock.core.handlers.extractors.ContentHandler;
import com.gerardogandeaga.cyberlock.database.DBAccess;
import com.gerardogandeaga.cyberlock.database.DataPackage;
import com.gerardogandeaga.cyberlock.utils.LogoutProtocol;
import com.gerardogandeaga.cyberlock.utils.graphics.ColourTag;
import com.gerardogandeaga.cyberlock.utils.graphics.DrawableColours;
import com.gerardogandeaga.cyberlock.utils.graphics.EditGraphics;

import static com.gerardogandeaga.cyberlock.utils.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.utils.LogoutProtocol.APP_LOGGED_IN;
import static com.gerardogandeaga.cyberlock.utils.LogoutProtocol.mCountDownTimer;
import static com.gerardogandeaga.cyberlock.utils.LogoutProtocol.mIsCountDownTimerFinished;
import static com.gerardogandeaga.cyberlock.utils.Stored.AUTOSAVE;
import static com.gerardogandeaga.cyberlock.utils.Stored.DIRECTORY;
import static com.gerardogandeaga.cyberlock.utils.Stored.TMP_PWD;

public class ActivityEdit extends AppCompatActivity implements View.OnClickListener, DialogFragmentTags.OnInputListener {
    @Override
    public void sendInput(String colour) {
        mColour = colour;
        setTag(mColour);
    }
    private View mView;
    // data package
    private DataPackage mDataPackage;
    private ContentHandler mContentHandler;
    private EditGraphics mEditGraphics;
    // data vars
    private boolean mIsNew = true;
    private boolean mIsAutoSave = false;
    // content vars
    private static final String[] ARGS = new String[]{ "TYPE_NOTE", "TYPE_PAYMENTINFO", "TYPE_LOGININFO" };
    private String TYPE;
    private String mColour = "DEFAULT";

    // widgets
    private EditText mEtLabel;
    private EditText mEtNotes;
    private TextView mTvDate;
    private ImageView mImgTag;
    // notes
    private EditText mEtNote;
    // paymentinfo
    private ImageView mIcon;
    private EditText mEtCardName, mEtCardNumber, mEtCardExpire, mEtCardCVV;
    private Spinner mSpCardSelect;
    //
    private String mCardType;
    private ArrayAdapter<CharSequence> mAdapter;
    // logininfo
    private EditText mEtUrl, mEtEmail, mEtUsername, mEtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Themes.setTheme(this);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        ACTIVITY_INTENT = null;
        this.mIsAutoSave = getSharedPreferences(DIRECTORY, MODE_PRIVATE).getBoolean(AUTOSAVE, false);

        // Edit tools
        this.mEditGraphics = new EditGraphics(this);

        // Activity creation
        extractBundle(); // Layout
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
            this.mDataPackage = (DataPackage) bundle.get("data");
            this.mIsNew = (mDataPackage == null);

            if (!mIsNew) {
                this.mContentHandler = new ContentHandler(this, mDataPackage);
                switch (mDataPackage.getType()) {
                    case "TYPE_NOTE":        setupLayoutNote();        TYPE = ARGS[0]; break;
                    case "TYPE_PAYMENTINFO": setupLayoutPaymentInfo(); TYPE = ARGS[1]; break;
                    case "TYPE_LOGININFO":   setupLayoutLoginInfo();   TYPE = ARGS[2]; break;
                }
                // Check if data item already exists
                containsData(); // Will alter between STATE 2 & 3 by switching mIsNew
                // New data override!!!
                if (!bundle.getBoolean("isNew?")) {
                    this.mIsNew = false; // Will alter between STATE 2 & 3 by switching mIsNew
                }
            } else { // If data is completely new
                switch ((String) bundle.get("type")) { // STATE 1
                    case "TYPE_NOTE":        setupLayoutNote();        TYPE = ARGS[0]; break;
                    case "TYPE_PAYMENTINFO": setupLayoutPaymentInfo(); TYPE = ARGS[1]; break;
                    case "TYPE_LOGININFO":   setupLayoutLoginInfo();   TYPE = ARGS[2]; break;
                }
            }
            bundle.remove("data");
            bundle.remove("type");
        }
    }

    private void containsData() {
        DBAccess dbAccess = DBAccess.getInstance(this);
        dbAccess.open();
        System.out.println("Is New ? 1 : " + mIsNew);
        this.mIsNew = dbAccess.containsData(this.mDataPackage);
        System.out.println("Is New ? 2 : " + mIsNew);
        dbAccess.close();
    }

    // layouts
    private void setupMainWidgets() {
        // Main widgets
        this.mEtLabel = findViewById(R.id.etLabel);
        this.mTvDate = findViewById(R.id.tvSubTitle);
        this.mImgTag = findViewById(R.id.imgTag);

        this.mImgTag.setOnClickListener(this);
    }
    //
    private void setupLayoutNote() {
        this.mView = View.inflate(this, R.layout.activity_edit_note, null);
        setContentView(mView);
        setupSupportActionBar(); // Action bar
        // Widgets
        setupMainWidgets();
        this.mEtNote = findViewById(R.id.etText);

        setupDataNote();
    }
    private void setupLayoutPaymentInfo() {
        this.mView = View.inflate(this, R.layout.activity_edit_paymentinfo, null);
        setContentView(mView);
        setupSupportActionBar(); // Action bar

        this.mIcon = findViewById(R.id.imgIcon);
        // Widgets
        setupMainWidgets();
        this.mEtCardName = findViewById(R.id.etCardName);
        this.mEtCardNumber = findViewById(R.id.etCardNumber);
        this.mEtCardExpire = findViewById(R.id.etCardExpire);
        this.mEtCardCVV = findViewById(R.id.etCardSecCode);
        this.mEtNotes = findViewById(R.id.etNotes);

        this.mSpCardSelect = findViewById(R.id.spCardSelect);
        //
        this.mAdapter = ArrayAdapter.createFromResource(this, R.array.CardType_array, R.layout.spinner_setting_text);
        this.mAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        this.mSpCardSelect.setAdapter(mAdapter);

        this.mEtCardNumber.addTextChangedListener(new TextWatcher() {
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
        this.mEtCardExpire.addTextChangedListener(new TextWatcher() {
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
        this.mSpCardSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        this.mView = View.inflate(this, R.layout.activity_edit_logininfo, null);
        setContentView(mView);
        setupSupportActionBar(); // Action bar
        // Widgets
        setupMainWidgets();
        this.mEtUrl = findViewById(R.id.etUrl);
        this.mEtUsername = findViewById(R.id.etUsername);
        this.mEtEmail = findViewById(R.id.etEmail);
        this.mEtPassword = findViewById(R.id.etPassword);
        this.mEtNotes = findViewById(R.id.etNotes);

        setupDataLoginInfo();
    }
    // pull data
    private void setupDataNote() {
        if (mIsNew) {
            setLabel(null);
            setDate(null);
            setTag(null);
        } else {
            mColour = mContentHandler.mTag;
            setLabel(mContentHandler.mLabel);
            setDate(mContentHandler.mDate);
            setTag(mContentHandler.mTag);

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
            mColour = mContentHandler.mTag;
            setLabel(mContentHandler.mLabel);
            setDate(mContentHandler.mDate);
            setTag(mContentHandler.mTag);

            // Set name, number, expiry, cvv, cardType
            mEtCardName.setText(mContentHandler.mHolder);
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
            mColour = mContentHandler.mTag;
            setLabel(mContentHandler.mLabel);
            setDate(mContentHandler.mDate);
            setTag(mContentHandler.mTag);

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
            case (R.id.option_save):
                if (!mIsAutoSave) {
                    onSave();
                    onBackPressed();
                } else {
                    onBackPressed();
                }
                return true;
            case (R.id.option_cancel):
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
            case R.id.imgTag: DialogFragmentTags.show(this); break;
        }
    }

    // Actions
    private void onSave() {
        DBAccess dbAccess = DBAccess.getInstance(this);
        dbAccess.open();

        String label = mEtLabel.getText().toString();
        String tmpContent = getViewData();

        // Saving
        if (!tmpContent.isEmpty()) {
            if (mIsNew) {
                dbAccess.save(getData(label, tmpContent, mColour));
            } else {
                dbAccess.update(getData(label, tmpContent, mColour));
            }
            dbAccess.close();
        } else {
            Toast.makeText(this, "No Content To Save", Toast.LENGTH_SHORT).show();
            dbAccess.close();
        }
    }
    private void onCancel() {
        ACTIVITY_INTENT = new Intent(this, ActivityMain.class);
        finish();
        startActivity(ACTIVITY_INTENT);
    }

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
        mImgTag.setColorFilter(ColourTag.colourTag(this, colour), PorterDuff.Mode.SRC_ATOP);
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
    private DataPackage getData(String label, String content, String tag) {
        if (mIsNew) {
            DataPackage tmp = new DataPackage();

            String tmpType;
            switch (TYPE) {
                case ("TYPE_NOTE"):        tmpType = "TYPE_NOTE"; break;
                case ("TYPE_PAYMENTINFO"): tmpType = "TYPE_PAYMENTINFO"; break;
                case ("TYPE_LOGININFO"):   tmpType = "TYPE_LOGININFO"; break;
                default:                   tmpType = "TYPE_NOTE"; break; // TODO CREATE A "COULD NOT READ TYPE" DIALOG ALLOWING FOR AN EDIT
            }
            tmp.setType(tmpType);
            tmp.setTag(tag);
            tmp.setLabel(label);
            tmp.setContent(content);

            return tmp;
        } else {
            mDataPackage.setTag(tag);
            mDataPackage.setLabel(label);
            mDataPackage.setContent(content);

            return mDataPackage;
        }
    }

    // THIS IS THE START OF THE SCRIPT FOR THE "TO LOGIN FUNCTIONS" THIS DETECTS THE ON PRESSED, START, TABS AND HOME BUTTONS IN ORDER TO INITIALIZE A SECURITY "FAIL-SAFE"
    @Override protected void onStart() {
        super.onStart();

        if (mIsCountDownTimerFinished) {
            if (!APP_LOGGED_IN) {
                // If auto save
                if (getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getBoolean(AUTOSAVE, false)) {
                    onSave();
                    TMP_PWD = null;
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
                        mColour));
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