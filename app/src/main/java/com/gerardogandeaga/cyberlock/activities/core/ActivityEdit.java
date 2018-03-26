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

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.activities.clearances.ActivityLogin;
import com.gerardogandeaga.cyberlock.activities.dialogs.DialogFragmentTags;
import com.gerardogandeaga.cyberlock.android.CustomToast;
import com.gerardogandeaga.cyberlock.core.handlers.extractors.NoteContentHandler;
import com.gerardogandeaga.cyberlock.database.DBNoteAccessor;
import com.gerardogandeaga.cyberlock.database.objects.NoteObject;
import com.gerardogandeaga.cyberlock.utils.Resources;
import com.gerardogandeaga.cyberlock.utils.graphics.Graphics;
import com.gerardogandeaga.cyberlock.utils.security.LogoutProtocol;

import static com.gerardogandeaga.cyberlock.utils.SharedPreferences.AUTOSAVE;
import static com.gerardogandeaga.cyberlock.utils.SharedPreferences.DIRECTORY;
import static com.gerardogandeaga.cyberlock.utils.SharedPreferences.TMP_PWD;
import static com.gerardogandeaga.cyberlock.utils.security.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.utils.security.LogoutProtocol.APP_LOGGED_IN;
import static com.gerardogandeaga.cyberlock.utils.security.LogoutProtocol.mCountDownTimer;
import static com.gerardogandeaga.cyberlock.utils.security.LogoutProtocol.mIsCountDownTimerFinished;

enum Type {
    NOTE, PAYMENT_INFO, LOGIN_INFO
}

public class ActivityEdit extends AppCompatActivity implements View.OnClickListener, DialogFragmentTags.OnColourSelected {
    @Override
    public void sendInput(String colour) {
        mColour = colour;
        setTag(mColour);
    }
    private Type enum_type;

    private View mView;
    // data package
    private NoteObject mNoteObject;
    private NoteContentHandler mNoteContentHandler;
    // data vars
    private boolean mIsNew = true;
    private boolean mIsAutoSave = false;
    // content vars
    private String mColour = "DEFAULT";

    // widgets
    private EditText mEtLabel;
    private EditText mEtNotes;
    private TextView mTvDate;
    private ImageView mImgTag;
    // notes
    private EditText mEtNote;
    // paymentinfo
    private ImageView mCardIcon;
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

        // Activity creation
        extractBundle(); // Layout
    }

    private void setupSupportActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setHomeAsUpIndicator(Graphics.BasicFilter.mutateHomeAsUpIndicatorDrawable(
                this, Resources.getDrawable(this, R.drawable.ic_back)));
    }

    private void extractBundle() {
        /*
        when activating the editor there are 3 possible states in which is will enter:
        STATE 1 : completely new (When it is called by the ADD function and  is not a data item yet)
        STATE 2 : floating raw data item (When editor is suspended by the logout protocol but has
                                          not been saved in the database master database accessoror)
        STATE 3 : saved raw data item (When the data item has already been saved and is merely
                                       going to get updated) */
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            this.mNoteObject = (NoteObject) bundle.get("data");
            this.mIsNew = (mNoteObject == null);

            if (!mIsNew) {
                this.mNoteContentHandler = new NoteContentHandler(this, mNoteObject);
                switch (mNoteObject.getType()) {
                    case NoteObject.NOTE:         setupLayoutNote();        this.enum_type = Type.NOTE; break;
                    case NoteObject.PAYMENT_INFO: setupLayoutPaymentInfo(); this.enum_type = Type.PAYMENT_INFO; break;
                    case NoteObject.LOGIN_INFO:   setupLayoutLoginInfo();   this.enum_type = Type.LOGIN_INFO; break;
                }
                // check if data item already exists
                containsData(); // Will alter between STATE 2 & 3 by switching mIsNew
                // new data override!!!
                if (!bundle.getBoolean("isNew?")) {
                    this.mIsNew = false; // will alter between STATE 2 & 3 by switching mIsNew
                }
            } else { // if data is completely new
                switch ((String) bundle.get("type")) { // STATE 1
                    case NoteObject.NOTE:         setupLayoutNote();        this.enum_type = Type.NOTE; break;
                    case NoteObject.PAYMENT_INFO: setupLayoutPaymentInfo(); this.enum_type = Type.PAYMENT_INFO; break;
                    case NoteObject.LOGIN_INFO:   setupLayoutLoginInfo();   this.enum_type = Type.LOGIN_INFO; break;
                }
            }
            bundle.remove("data");
            bundle.remove("type");
        }
    }

    private void containsData() {
        DBNoteAccessor accessor = DBNoteAccessor.getInstance(this);
        accessor.open();
        System.out.println("Is New ? 1 : " + mIsNew);
        this.mIsNew = accessor.containsData(this.mNoteObject);
        System.out.println("Is New ? 2 : " + mIsNew);
        accessor.close();
    }

    // layouts
    private void setupMainWidgets() {
        // main widgets
        this.mEtLabel = findViewById(R.id.etLabel);
        this.mTvDate = findViewById(R.id.tvSubTitle);
        this.mImgTag = findViewById(R.id.imgTag);

        this.mImgTag.setOnClickListener(this);
        this.mCardIcon = findViewById(R.id.imgCardIcon);

        mCardIcon.setVisibility(View.GONE);
    }
    //
    private void setupLayoutNote() {
        this.mView = View.inflate(this, R.layout.activity_edit_note, null);
        setContentView(mView);
        setupSupportActionBar(); // Action bar

        // widgets
        setupMainWidgets();
        this.mEtNote = findViewById(R.id.etText);

        setupDataNote();
    }
    private void setupLayoutPaymentInfo() {
        this.mView = View.inflate(this, R.layout.activity_edit_paymentinfo, null);
        setContentView(mView);
        setupSupportActionBar(); // action bar

        // widgets
        setupMainWidgets();
        this.mEtCardName = findViewById(R.id.etCardName);
        this.mEtCardNumber = findViewById(R.id.etCardNumber);
        this.mEtCardExpire = findViewById(R.id.etCardExpire);
        this.mEtCardCVV = findViewById(R.id.etCardSecCode);
        this.mEtNotes = findViewById(R.id.etNotes);

        this.mSpCardSelect = findViewById(R.id.spCardSelect);
        //
        this.mAdapter = ArrayAdapter.createFromResource(this, R.array.str_array_card_type, R.layout.spinner_setting_text);
        this.mAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        this.mSpCardSelect.setAdapter(mAdapter);

        this.mEtCardNumber.addTextChangedListener(new TextWatcher() {
            int prevL = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                prevL = mEtCardNumber.getText().toString().length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

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
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

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
                    mCardIcon.setImageDrawable(Graphics.CardImages.getCardImage(view.getContext(), mCardType));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mCardIcon.setVisibility(View.VISIBLE);
        setupDataPaymentInfo();
    }
    private void setupLayoutLoginInfo() {
        this.mView = View.inflate(this, R.layout.activity_edit_logininfo, null);
        setContentView(mView);
        setupSupportActionBar(); // action bar

        // widgets
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
            mColour = mNoteContentHandler.mTag;
            setLabel(mNoteContentHandler.mLabel);
            setDate(mNoteContentHandler.mDate);
            setTag(mNoteContentHandler.mTag);

            // set note
            mEtNote.setText(mNoteContentHandler.mNote);
        }
    }
    private void setupDataPaymentInfo() {
        if (mIsNew) {
            setLabel(null);
            setDate(null);
            setTag(null);
        } else {
            mColour = mNoteContentHandler.mTag;
            setLabel(mNoteContentHandler.mLabel);
            setDate(mNoteContentHandler.mDate);
            setTag(mNoteContentHandler.mTag);

            // Set name, number, expiry, cvv, cardType
            mEtCardName.setText(mNoteContentHandler.mHolder);
            mEtCardNumber.setText(mNoteContentHandler.mNumber);
            mEtCardExpire.setText(mNoteContentHandler.mExpiry);
            mEtCardCVV.setText(mNoteContentHandler.mCVV);
            //
            int spinnerPosition = mAdapter.getPosition(mNoteContentHandler.mCardType);
            mSpCardSelect.setSelection(spinnerPosition);
            mEtNotes.setText(mNoteContentHandler.mNote);

            mCardIcon.setImageDrawable(Graphics.CardImages.getCardImage(this, mNoteContentHandler.mCardType));
        }
    }
    private void setupDataLoginInfo() {
        if (mIsNew) {
            setLabel(null);
            setDate(null);
            setTag(null);
        } else {
            mColour = mNoteContentHandler.mTag;
            setLabel(mNoteContentHandler.mLabel);
            setDate(mNoteContentHandler.mDate);
            setTag(mNoteContentHandler.mTag);

            // Set url, email, username, password,
            mEtUrl.setText(mNoteContentHandler.mUrl);
            mEtEmail.setText(mNoteContentHandler.mEmail);
            mEtUsername.setText(mNoteContentHandler.mUsername);
            mEtPassword.setText(mNoteContentHandler.mPassword);
            mEtNotes.setText(mNoteContentHandler.mNote);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);

        Graphics.BasicFilter.mutateMenuItems(this, menu);

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
        DBNoteAccessor accessor = DBNoteAccessor.getInstance(this);
        accessor.open();

        String label = mEtLabel.getText().toString();
        String tmpContent = getViewData();

        // Saving
        if (!tmpContent.isEmpty()) {
            if (mIsNew) {
                accessor.save(getData(label, tmpContent, mColour));
            } else {
                accessor.update(getData(label, tmpContent, mColour));
            }
            accessor.close();
        } else {
            CustomToast.buildAndShowToast(this, "No Content To Save", CustomToast.INFORMATION, CustomToast.LENGTH_SHORT);
            accessor.close();
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
        mImgTag.setColorFilter(Graphics.ColourTags.colourTag(this, colour), PorterDuff.Mode.SRC_ATOP);
    }

    // Getters and setters for saving
    private String getViewData() {
        // GETTING AND FORMATTING THE COLOUR_TAGS CONDITIONALLY

        switch (enum_type) {
            case NOTE:
                final String note = mEtNote.getText().toString();

                final String format_note = "%s";

                return String.format(format_note,
                        note);

            case PAYMENT_INFO:
                final String cardName = mEtCardName.getText().toString();
                final String cardNumber = mEtCardNumber.getText().toString();
                final String cardType = mCardType;
                final String cardExpire = mEtCardExpire.getText().toString();
                final String cardSecCode = mEtCardCVV.getText().toString();
                final String notes_paymentinfo = mEtNotes.getText().toString();

                final String format_paymentinfo = "%s\n%s\n%s\n%s\n%s\n%s";

                return String.format(format_paymentinfo,
                        cardName, cardNumber, cardType, cardExpire, cardSecCode, notes_paymentinfo);

            case LOGIN_INFO:
                final String url = mEtUrl.getText().toString();
                final String email = mEtEmail.getText().toString();
                final String username = mEtUsername.getText().toString();
                final String password = mEtPassword.getText().toString();
                final String notes_logininfo = mEtNotes.getText().toString();

                final String format_logininfo = "%s\n%s\n%s\n%s\n%s";

                return String.format(format_logininfo,
                        url, email, username, password, notes_logininfo);
            default:
                return "";
        }
    }
    private NoteObject getData(String label, String content, String tag) {
        if (mIsNew) {
            NoteObject tmp = new NoteObject();

            tmp.setType("TYPE_" + enum_type.name());
            tmp.setTag(tag);
            tmp.setLabel(label);
            tmp.setContent(content);

            return tmp;
        } else {
            mNoteObject.setTag(tag);
            mNoteObject.setLabel(label);
            mNoteObject.setContent(content);

            return mNoteObject;
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

                // CustomLoad data into intent
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