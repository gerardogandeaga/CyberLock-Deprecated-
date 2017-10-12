package com.gerardogandeaga.cyberlock.Activitys.Activities.Menus;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LoginActivity;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Main.MainActivity;
import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.Supports.Globals;
import com.gerardogandeaga.cyberlock.Supports.LogoutProtocol;
import com.gerardogandeaga.cyberlock.Supports.Settings_EncryptionMethodChange;
import com.gerardogandeaga.cyberlock.Supports.Settings_ResetComplexPasscode;
import com.gerardogandeaga.cyberlock.Supports.Settings_ResetShortPasscode;
import com.gerardogandeaga.cyberlock.Supports.Settings_ScrambleKey;

import static com.gerardogandeaga.cyberlock.Supports.Globals.AUTOSAVE;
import static com.gerardogandeaga.cyberlock.Supports.Globals.COMPLEXPASSCODE;
import static com.gerardogandeaga.cyberlock.Supports.Globals.DELAY_TIME;
import static com.gerardogandeaga.cyberlock.Supports.Globals.DIRECTORY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.ENCRYPTION_ALGO;
import static com.gerardogandeaga.cyberlock.Supports.Globals.LOGOUT_DELAY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.SCHEME;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.APP_LOGGED_IN;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.mCountDownIsFinished;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.mCountDownTimer;

public class Settings extends AppCompatActivity implements View.OnClickListener
{
    private Context mContext = this;
    private SharedPreferences mSharedPreferences;

    // DATA VARIABLES
    // SPINNERS
    private boolean
            mIsAutoSave,
            mIsComplexCode;
    private ArrayAdapter<CharSequence>
            mAdapterAutoLogoutDelay,
            mAdapterEncryptionMethod;
    private String
            mAutoLogoutDelay,
            mOldEncryptionMethod;

    // WIDGETS
    private CheckBox
            mCbAutoSave,
            mCbComplexPasscode,
    // COLOR SCHEME
    mCbBlue, mCbRed, mCbGreen, mCbYellow, mCbPurple, mCbGray;
    private RelativeLayout
            mAutoSave,
            mChangePasscode,
            mComplexPasscode,
            mScrambleKey,
    // COLOR SCHEME
    mBlue, mRed, mGreen, mYellow, mPurple, mGray;
    private Spinner
            mSpAutoLogoutDelay,
            mSpEncryptionMethod;

    // INITIAL ON CREATE METHODS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Globals.COLORSCHEME(this);
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        setupLayout();
    }
    private void setupLayout() {
        setContentView(R.layout.activity_settings);
        ACTIVITY_INTENT = null;
        //
        this.mSharedPreferences = getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE);
        this.mIsAutoSave = mSharedPreferences.getBoolean(AUTOSAVE, false);
        this.mIsComplexCode = mSharedPreferences.getBoolean(COMPLEXPASSCODE, false);

        // ACTION BAR TITLE AND BACK BUTTON
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setSubtitle("Functionality And Visuals");

        // SPINNER DATA
        // LOGOUT DELAY
        this.mSpAutoLogoutDelay = (Spinner) findViewById(R.id.spAutoLogoutDelay);
        mAdapterAutoLogoutDelay = ArrayAdapter.createFromResource(this, R.array.AutoLogoutDelay_array, R.layout.item_settings_spinner);
        mAdapterAutoLogoutDelay.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.mSpAutoLogoutDelay.setAdapter(mAdapterAutoLogoutDelay);
        // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        // ENCRYPTION METHOD
        this.mSpEncryptionMethod = (Spinner) findViewById(R.id.spEncryptionMethod);
        mAdapterEncryptionMethod = ArrayAdapter.createFromResource(this, R.array.CryptALGO_array, R.layout.item_settings_spinner);
        mAdapterEncryptionMethod.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.mSpEncryptionMethod.setAdapter(mAdapterEncryptionMethod);

        this.mCbAutoSave = (CheckBox) findViewById(R.id.cbAutoSave);
        this.mAutoSave = (RelativeLayout) findViewById(R.id.AutoSave);
        this.mChangePasscode = (RelativeLayout) findViewById(R.id.ChangePasscode);
        this.mCbComplexPasscode = (CheckBox) findViewById(R.id.cbComplexPasscode);
        this.mComplexPasscode = (RelativeLayout) findViewById(R.id.ComplexPasscode);
        this.mScrambleKey = (RelativeLayout) findViewById(R.id.ScrambleKey);

        // COLOR SCHEME
        this.mBlue = (RelativeLayout) findViewById(R.id.Blue);
        this.mRed = (RelativeLayout) findViewById(R.id.Red);
        this.mGreen = (RelativeLayout) findViewById(R.id.Green);
        this.mYellow = (RelativeLayout) findViewById(R.id.Yellow);
        this.mPurple = (RelativeLayout) findViewById(R.id.Purple);
        this.mGray = (RelativeLayout) findViewById(R.id.Gray);

        this.mCbBlue = (CheckBox) findViewById(R.id.cbBlue);
        this.mCbRed = (CheckBox) findViewById(R.id.cbRed);
        this.mCbGreen = (CheckBox) findViewById(R.id.cbGreen);
        this.mCbYellow = (CheckBox) findViewById(R.id.cbYellow);
        this.mCbPurple = (CheckBox) findViewById(R.id.cbPurple);
        this.mCbGray = (CheckBox) findViewById(R.id.cbGray);
        // ------------

        this.mAutoSave.setOnClickListener(this);
        this.mChangePasscode.setOnClickListener(this);
        this.mComplexPasscode.setOnClickListener(this);
        this.mScrambleKey.setOnClickListener(this);

        this.mCbAutoSave.setClickable(false);
        this.mCbAutoSave.setChecked(false);

        this.mCbComplexPasscode.setClickable(false);
        this.mCbComplexPasscode.setChecked(false);

        this.mCbBlue.setClickable(false);
        this.mCbRed.setClickable(false);
        this.mCbGreen.setClickable(false);
        this.mCbYellow.setClickable(false);
        this.mCbPurple.setClickable(false);
        this.mCbGray.setClickable(false);

        this.mBlue.setOnClickListener(this);
        this.mRed.setOnClickListener(this);
        this.mGreen.setOnClickListener(this);
        this.mYellow.setOnClickListener(this);
        this.mPurple.setOnClickListener(this);
        this.mGray.setOnClickListener(this);

        savedStates();

        this.mSpAutoLogoutDelay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object object = parent.getItemAtPosition(position);
                if (object != null) {
                    mAutoLogoutDelay = object.toString();
                    long time = 0;
                    switch (mAutoLogoutDelay) {
                        case "Immediate":
                            time = 0;
                            break;
                        case "15 Seconds":
                            time = 15000;
                            break;
                        case "30 Seconds":
                            time = 30000;
                            break;
                        case "1 Minute":
                            time = 60000;
                            break;
                        case "5 Minutes":
                            time = 300000;
                            break;
                        case "10 Minutes":
                            time = 600000;
                            break;
                        case "30 Minutes":
                            time = 1800000;
                            break;
                        case "1 Hour":
                            time = 3600000;
                            break;
                        case "2 Hours":
                            time = 7200000;
                            break;
                        case "Never":
                            break;
                    }

                    System.out.println("Time = " + time);
                    mSharedPreferences.edit().putString(LOGOUT_DELAY, mAutoLogoutDelay).putLong(DELAY_TIME, time).apply();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        this.mSpEncryptionMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object object = parent.getItemAtPosition(position);
                if (object != null) {
                    String algorithm = object.toString();
                    if (algorithm.matches("AES - 256")) {
                        String newAlgorithm = "AES";
                        if (!mSharedPreferences.getString(ENCRYPTION_ALGO, "AES").matches(newAlgorithm)) {
                            onEncryptionMethodChange(newAlgorithm);
                        }
                    } else if (algorithm.matches("Blowfish - 448")) {
                        String newAlgorithm = "Blowfish";
                        if (!mSharedPreferences.getString(ENCRYPTION_ALGO, "AES").matches(newAlgorithm)) {
                            onEncryptionMethodChange(newAlgorithm);
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    private void savedStates() {
        // CHECK BOXES
        if (!mIsAutoSave) {
            mCbAutoSave.setChecked(false);
        } else {
            mCbAutoSave.setChecked(true);
        }

        if (!mIsComplexCode) {
            mCbComplexPasscode.setChecked(false);
        } else {
            mCbComplexPasscode.setChecked(true);
        }

        // SPINNERS
        // LOGOUT DELAY
        int delaySpinnerPosition = mAdapterAutoLogoutDelay.getPosition(mSharedPreferences.getString(LOGOUT_DELAY, "Immediate"));
        mSpAutoLogoutDelay.setSelection(delaySpinnerPosition);

        // ENCRYPTION METHOD
        int algoSpinnerPosition;
        switch (mSharedPreferences.getString(ENCRYPTION_ALGO, "AES")) {
            case "AES":
                algoSpinnerPosition = mAdapterEncryptionMethod.getPosition("AES - 256");
                mSpEncryptionMethod.setSelection(algoSpinnerPosition);
                mOldEncryptionMethod = mSpEncryptionMethod.getItemAtPosition(algoSpinnerPosition).toString();
                break;
            case "Blowfish":
                algoSpinnerPosition = mAdapterEncryptionMethod.getPosition("Blowfish - 448");
                mSpEncryptionMethod.setSelection(algoSpinnerPosition);
                mOldEncryptionMethod = mSpEncryptionMethod.getItemAtPosition(algoSpinnerPosition).toString();
                break;
        }

        final String colourString = mSharedPreferences.getString(SCHEME, "SCHEME_BLUE");
        switch (colourString) {
            case "SCHEME_BLUE":
                mCbBlue.setChecked(true);
                break;
            case "SCHEME_RED":
                mCbRed.setChecked(true);
                break;
            case "SCHEME_GREEN":
                mCbGreen.setChecked(true);
                break;
            case "SCHEME_YELLOW":
                mCbYellow.setChecked(true);
                break;
            case "SCHEME_PURPLE":
                mCbPurple.setChecked(true);
                break;
            case "SCHEME_GRAY":
                mCbGray.setChecked(true);
                break;
            default:
                mCbBlue.setChecked(true);
                break;
        }
    }
    // -------------------------

    // ON CLICK
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.AutoSave:
                onAutoSave();
                break;
            case R.id.ChangePasscode:
                onResetPassword();
                break;
            case R.id.ComplexPasscode:
                onComplexPassword();
                break;
            case R.id.ScrambleKey:
                onScrambleKey();
                break;
            // COLOUR SCHEME
            case R.id.Blue:
                onColorSchemeChange(v.getId());
                break;
            case R.id.Red:
                onColorSchemeChange(v.getId());
                break;
            case R.id.Green:
                onColorSchemeChange(v.getId());
                break;
            case R.id.Yellow:
                onColorSchemeChange(v.getId());
                break;
            case R.id.Purple:
                onColorSchemeChange(v.getId());
                break;
            case R.id.Gray:
                onColorSchemeChange(v.getId());
                break;
        }
    }
    // --------

    // #########################################################################
    // SETTINGS FEATURES                                                       #
    private void onAutoSave() {
        final boolean autoSave = mSharedPreferences.getBoolean(AUTOSAVE, false);

        if (!autoSave) {
            mSharedPreferences.edit().putBoolean(AUTOSAVE, true).apply();
            mCbAutoSave.setChecked(true);
        } else {
            mSharedPreferences.edit().putBoolean(AUTOSAVE, false).apply();
            mCbAutoSave.setChecked(false);
        }
    }                                          //#
    private void onResetPassword() {
        if (!mIsComplexCode) {
            ACTIVITY_INTENT = new Intent(this, Settings_ResetShortPasscode.class);
            finish();
            startActivity(ACTIVITY_INTENT);
        } else {
            ACTIVITY_INTENT = new Intent(this, Settings_ResetComplexPasscode.class);
            finish();
            startActivity(ACTIVITY_INTENT);
        }
    }                                     //#
    private void onComplexPassword() {
        if (!mIsComplexCode) {
            // DIALOG BUILDER
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

            alertDialog.setTitle("Switch To Complex Passcode");
            alertDialog.setMessage("Switching to a complex passcode requires a password reset.");
            alertDialog.setCancelable(false);

            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog.setPositiveButton("Continue", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                    ACTIVITY_INTENT = new Intent(mContext, Settings_ResetComplexPasscode.class);
                    finish();
                    startActivity(ACTIVITY_INTENT);
                }
            });
            alertDialog.show();
        } else {
            // DIALOG BUILDER
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

            alertDialog.setTitle("Switch To Short Passcode");
            alertDialog.setMessage("Switching to a simple passcode requires a password reset.");
            alertDialog.setCancelable(false);

            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog.setPositiveButton("Continue", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                    ACTIVITY_INTENT = new Intent(mContext, Settings_ResetShortPasscode.class);
                    startActivity(ACTIVITY_INTENT);
                }
            });
            alertDialog.show();
        }
    }                                   //#
    private void onScrambleKey() {
        // DIALOG BUILDER
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("Scramble Encryption Key");
        alertDialog.setMessage(R.string.AlertDialog_ScrambleKey);
        alertDialog.setCancelable(false);

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setPositiveButton("Scramble", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                new Settings_ScrambleKey(mContext).execute();
            }
        });
        alertDialog.show();
    }                                       //#
    private void onEncryptionMethodChange(final String algorithm) {
        // DIALOG BUILDER
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("Encryption Method");
        alertDialog.setMessage(R.string.AlertDialog_EncryptionMethodChange);
        alertDialog.setCancelable(false);

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mSpEncryptionMethod.setSelection(mAdapterEncryptionMethod.getPosition(mOldEncryptionMethod));
                dialog.dismiss();
            }
        });
        alertDialog.setPositiveButton("Continue", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                new Settings_EncryptionMethodChange(mContext, algorithm).execute();
            }
        });
        alertDialog.show();
    }      //#
    private void onColorSchemeChange(int i) {
        String colourString = "";

        switch (i) {
            case R.id.Blue:
                colourString = "SCHEME_BLUE";
                mCbBlue.setChecked(true);
                mCbRed.setChecked(false);
                mCbGreen.setChecked(false);
                mCbYellow.setChecked(false);
                mCbPurple.setChecked(false);
                mCbGray.setChecked(false);
                break;
            case R.id.Red:
                colourString = "SCHEME_RED";
                mCbBlue.setChecked(false);
                mCbRed.setChecked(true);
                mCbGreen.setChecked(false);
                mCbYellow.setChecked(false);
                mCbPurple.setChecked(false);
                mCbGray.setChecked(false);
                break;
            case R.id.Green:
                colourString = "SCHEME_GREEN";
                mCbBlue.setChecked(false);
                mCbRed.setChecked(false);
                mCbGreen.setChecked(true);
                mCbYellow.setChecked(false);
                mCbPurple.setChecked(false);
                mCbGray.setChecked(false);
                break;
            case R.id.Yellow:
                colourString = "SCHEME_YELLOW";
                mCbBlue.setChecked(false);
                mCbRed.setChecked(false);
                mCbGreen.setChecked(false);
                mCbYellow.setChecked(true);
                mCbPurple.setChecked(false);
                mCbGray.setChecked(false);
                break;
            case R.id.Purple:
                colourString = "SCHEME_PURPLE";
                mCbBlue.setChecked(false);
                mCbRed.setChecked(false);
                mCbGreen.setChecked(false);
                mCbYellow.setChecked(false);
                mCbPurple.setChecked(true);
                mCbGray.setChecked(false);
                break;
            case R.id.Gray:
                colourString = "SCHEME_GRAY";
                mCbBlue.setChecked(false);
                mCbRed.setChecked(false);
                mCbGreen.setChecked(false);
                mCbYellow.setChecked(false);
                mCbPurple.setChecked(false);
                mCbGray.setChecked(true);
                break;
        }

        mSharedPreferences.edit().putString(SCHEME, colourString).apply();

        ACTIVITY_INTENT = new Intent(this, Settings.class);
        this.finish();
        this.startActivity(ACTIVITY_INTENT);
    }                            //#
    // #########################################################################


    // THIS IS THE START OF THE SCRIPT FOR *** THE "TO LOGIN FUNCTION" THIS DETECTS THE ON PRESSED, START, TABS AND HOME BUTTONS IN ORDER TO INITIALIZE SECURITY "FAIL-SAFE"
    @Override
    protected void onStart() {
        super.onStart();

        if (mCountDownIsFinished) {
            if (!APP_LOGGED_IN) {
                ACTIVITY_INTENT = new Intent(this, LoginActivity.class);
                this.finish(); // CLEAN UP AND END
                this.startActivity(ACTIVITY_INTENT); // GO TO LOGIN ACTIVITY
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
            ACTIVITY_INTENT = new Intent(this, MainActivity.class);
            this.finish();
            this.startActivity(ACTIVITY_INTENT);
        }
    }
    @Override
    public void onPause() {
        super.onPause();

        if (!this.isFinishing()) // HOME AND TABS AND SCREEN OFF
        {
            if (ACTIVITY_INTENT == null) // NO PENDING ACTIVITIES ???(MAIN)--->(EDIT)???
            {
                new LogoutProtocol().logoutExecuteAutosaveOff(mContext);
            }
        }
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
}