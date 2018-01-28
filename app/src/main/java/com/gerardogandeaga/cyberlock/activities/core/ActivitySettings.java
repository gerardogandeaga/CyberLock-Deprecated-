package com.gerardogandeaga.cyberlock.activities.core;

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
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.activities.clearances.ActivityLogin;
import com.gerardogandeaga.cyberlock.support.KeyChecker;
import com.gerardogandeaga.cyberlock.support.LogoutProtocol;
import com.gerardogandeaga.cyberlock.support.graphics.DrawableColours;
import com.gerardogandeaga.cyberlock.support.graphics.Themes;
import com.gerardogandeaga.cyberlock.support.settings.ChangePasscode;

import static com.gerardogandeaga.cyberlock.support.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.support.LogoutProtocol.APP_LOGGED_IN;
import static com.gerardogandeaga.cyberlock.support.LogoutProtocol.mCountDownTimer;
import static com.gerardogandeaga.cyberlock.support.LogoutProtocol.mIsCountDownTimerFinished;
import static com.gerardogandeaga.cyberlock.support.Stored.AUTOSAVE;
import static com.gerardogandeaga.cyberlock.support.Stored.DELAY_TIME;
import static com.gerardogandeaga.cyberlock.support.Stored.DIRECTORY;
import static com.gerardogandeaga.cyberlock.support.Stored.LOGOUT_DELAY;
import static com.gerardogandeaga.cyberlock.support.Stored.THEME;

public class ActivitySettings extends AppCompatActivity implements View.OnClickListener {
    private Context mContext = this;
    private SharedPreferences mSharedPreferences;

    // RawDataPackage variables
    // Spinners
    private boolean mIsAutoSave;
    private ArrayAdapter<CharSequence>
            mAdapterAutoLogoutDelay,
            mAdapterEncryptionAlgorithm;
    private String
            mAutoLogoutDelay,
            mOldEncryptionAlgorithm;

    // Widgets
    private CheckBox mCbLight, mCbDark;
    private android.support.v7.widget.SwitchCompat mSwAutoSave;
    private Spinner
            mSpAutoLogoutDelay,
            mSpEncryptionAlgorithm;

    // Initial create methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Themes.setTheme(this);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        ACTIVITY_INTENT = null;
        super.onCreate(savedInstanceState);

        View view  = View.inflate(this, R.layout.activity_settings, null);
        setContentView(view);

        //
        this.mSharedPreferences = getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE);
        this.mIsAutoSave = mSharedPreferences.getBoolean(AUTOSAVE, false);
        // Action bar
        setupSupportActionBar();
        // Widgets

        // Spinners
        iniAutoSave();
        iniAutoLogoutDelay();
        iniChangePasscode();
        iniThemeSelector();

        savedStates();
    }
    private void setupSupportActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setSubtitle("Functionality And Visuals");
        getSupportActionBar().setHomeAsUpIndicator(DrawableColours.mutateHomeAsUpIndicatorDrawable(
                this, this.getResources().getDrawable(R.drawable.ic_back)));
    }
    private void savedStates() {
        actionThemeSelector(mSharedPreferences.getString(THEME, ""), false);

        // CHECK BOXES
        mSwAutoSave.setChecked(this.mIsAutoSave);

        int delaySpinnerPosition = mAdapterAutoLogoutDelay.getPosition(this.mSharedPreferences.getString(LOGOUT_DELAY, "Immediate"));
        mSpAutoLogoutDelay.setSelection(delaySpinnerPosition);

//        // ENCRYPTION METHOD
//        int algoSpinnerPosition;
//        switch (this.mSharedPreferences.getString(ENCRYPTION_ALGORITHM, "AES")) {
//            case "AES":
//                algoSpinnerPosition = mAdapterEncryptionAlgorithm.getPosition("AES - 256");
//                mSpEncryptionAlgorithm.setSelection(algoSpinnerPosition);
//                mOldEncryptionAlgorithm = mSpEncryptionAlgorithm.getItemAtPosition(algoSpinnerPosition).toString();
//                break;
//            case "Blowfish":
//                algoSpinnerPosition = mAdapterEncryptionAlgorithm.getPosition("Blowfish - 448");
//                mSpEncryptionAlgorithm.setSelection(algoSpinnerPosition);
//                mOldEncryptionAlgorithm = mSpEncryptionAlgorithm.getItemAtPosition(algoSpinnerPosition).toString();
//                break;
//        }
    }
    // -------------------------
    private void iniAutoSave() {
        this.mSwAutoSave = findViewById(R.id.swAutoSave);
        RelativeLayout autoSave = findViewById(R.id.AutoSave);

        mSwAutoSave.setClickable(false);
        mSwAutoSave.setChecked(false);

        autoSave.setOnClickListener(this);
    }
    private void iniAutoLogoutDelay() {
        this.mSpAutoLogoutDelay = findViewById(R.id.spAutoLogoutDelay);
        this.mAdapterAutoLogoutDelay = ArrayAdapter.createFromResource(this, R.array.AutoLogoutDelay_array, R.layout.spinner_setting_text);
        this.mAdapterAutoLogoutDelay.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        this.mSpAutoLogoutDelay.setAdapter(mAdapterAutoLogoutDelay);

        RelativeLayout autoLogoutDelay = findViewById(R.id.AutoLogoutDelay);

        autoLogoutDelay.setOnClickListener(this);

        this.mSpAutoLogoutDelay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object object = parent.getItemAtPosition(position);
                if (object != null) {
                    mAutoLogoutDelay = object.toString();
                    long time = 0;
                    switch (mAutoLogoutDelay) {
                        case "Immediate": time = 0; break;
                        case "15 Seconds": time = 15000; break;
                        case "30 Seconds": time = 30000; break;
                        case "1 Minute": time = 60000; break;
                        case "5 Minutes": time = 300000; break;
                        case "10 Minutes": time = 600000; break;
                        case "30 Minutes": time = 1800000; break;
                        case "1 Hour": time = 3600000; break;
                        case "2 Hours": time = 7200000; break;
                        case "Never": break;
                    }

                    System.out.println("Time = " + time);
                    mSharedPreferences.edit().putString(LOGOUT_DELAY, mAutoLogoutDelay).putLong(DELAY_TIME, time).apply();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    private void iniChangePasscode() {
        RelativeLayout changePasscode = findViewById(R.id.ChangePasscode);

        changePasscode.setOnClickListener(this);
    }
    private void iniThemeSelector() {
        this.mCbLight = findViewById(R.id.cbLight);
        this.mCbDark = findViewById(R.id.cbDark);
        RelativeLayout Light = findViewById(R.id.Light);
        RelativeLayout Dark = findViewById(R.id.Dark);

        mCbLight.setClickable(false);
        mCbDark.setClickable(false);

        Light.setOnClickListener(this);
        Dark.setOnClickListener(this);
    }

    // On click
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
            // Auto save
            case R.id.AutoSave: actionAutoSave(); break;

            // Themes
            case R.id.Light:  actionThemeSelector("THEME_LIGHT", true); break;
            case R.id.Dark:   actionThemeSelector("THEME_DARK", true); break;

            // Auto logout delay
            case R.id.AutoLogoutDelay: mSpAutoLogoutDelay.performClick(); break;

            // Change passcode
            case R.id.ChangePasscode: actionChangePasscode(); break;
        }
    }
    // --------

    // Actions
    private void actionAutoSave() {
        final boolean autoSave = this.mSharedPreferences.getBoolean(AUTOSAVE, false);

        if (!autoSave) {
            this.mSharedPreferences.edit().putBoolean(AUTOSAVE, true).apply();
            mSwAutoSave.setChecked(true);
        } else {
            this.mSharedPreferences.edit().putBoolean(AUTOSAVE, false).apply();
            mSwAutoSave.setChecked(false);
        }
    }
    private void actionThemeSelector(String theme, boolean forceRestart) {
        if (theme.matches("THEME_DARK")) {
            mCbLight.setChecked(false);
            mCbDark.setChecked(true);
        } else {
            mCbLight.setChecked(true);
            mCbDark.setChecked(false);
        }
        mSharedPreferences.edit().putString(THEME, theme).apply();
        if (forceRestart) {
            ACTIVITY_INTENT = new Intent(this, ActivitySettings.class);
            this.startActivity(ACTIVITY_INTENT);
        }
    }
    private void actionChangePasscode() {
        View v = View.inflate(mContext, R.layout.fragment_option_changepassord, null);
        // Dialog primitives
        final EditText current = v.findViewById(R.id.etCurrent);
        final EditText initial = v.findViewById(R.id.etInitial);
        final EditText Final = v.findViewById(R.id.etFinal);

        // Dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(v);
        builder.setTitle(R.string.titleChangePassword);
        builder.setNegativeButton(R.string.btnCancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(R.string.btnChange, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String p0 = current.getText().toString();
                final String p1 = initial.getText().toString();
                final String p2 = Final.getText().toString();
                if (!p0.isEmpty() || !p1.isEmpty() || !p2.isEmpty()) { // Check if any fields are empty
                    if (p1.matches(p2)) { // Check is entries match and are not empty
                        if (KeyChecker.comparePasswords(mContext, p0)) { // check if original passcode matches the one entered
                            final String[] passcodes = new String[3];
                            passcodes[0] = p0;
                            passcodes[1] = p1;
                            passcodes[2] = p2;
                            ChangePasscode obj = new ChangePasscode(mContext, mSharedPreferences, passcodes);
                            dialog.dismiss();
                        } else {
                            Toast.makeText(mContext, "Wrong passcode", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mContext, "New entries do not match", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, "One or more fields are empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // Dialog show
        final AlertDialog dialog = builder.show();
    }

    // THIS IS THE START OF THE SCRIPT FOR *** THE "TO LOGIN FUNCTION" THIS DETECTS THE ON PRESSED, START, TABS AND HOME BUTTONS IN ORDER TO INITIALIZE SECURITY "FAIL-SAFE"
    @Override protected void onStart() {
        super.onStart();

        if (mIsCountDownTimerFinished) {
            if (!APP_LOGGED_IN) {
                ACTIVITY_INTENT = new Intent(this, ActivityLogin.class);
                this.finish(); // CLEAN UP AND END
                this.startActivity(ACTIVITY_INTENT); // GO TO LOGIN ACTIVITY
            }
        } else {
            if (mCountDownTimer != null) {
                mCountDownTimer.cancel();
            }
        }
    }
    @Override public void onBackPressed() {
        super.onBackPressed();
        if (ACTIVITY_INTENT == null) { // NO PENDING ACTIVITIES ???(MAIN)--->(EDIT)???
            ACTIVITY_INTENT = new Intent(this, ActivityMain.class);
            this.finish();
            this.startActivity(ACTIVITY_INTENT);
            overridePendingTransition(R.anim.anim_slide_inleft, R.anim.anim_slide_outright);
        }
    }
    @Override public void onPause() {
        super.onPause();

        if (!this.isFinishing()) { // HOME AND TABS AND SCREEN OFF
            if (ACTIVITY_INTENT == null) { // NO PENDING ACTIVITIES ???(MAIN)--->(EDIT)???
                new LogoutProtocol().logoutExecuteAutosaveOff(mContext);
            }
        }
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
}

//
//    private void iniScrambleKey() {
//        RelativeLayout scrambleKey = findViewById(R.id.ScrambleKey);
//
//        scrambleKey.setOnClickListener(this);
//    }
//    private void iniEncryptionAlgorithmChange() {
//        this.mSpEncryptionAlgorithm = findViewById(R.id.spEncryptionAlgorithm);
//        this.mAdapterEncryptionAlgorithm = ArrayAdapter.createFromResource(this, R.array.CryptALGO_array, R.layout.spinner_setting_text);
//        this.mAdapterEncryptionAlgorithm.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
//        this.mSpEncryptionAlgorithm.setAdapter(mAdapterEncryptionAlgorithm);
//
//        RelativeLayout encryptionAlgorithm = findViewById(R.id.EncryptionAlgorithm);
//
//        encryptionAlgorithm.setOnClickListener(this);
//
//        this.mSpEncryptionAlgorithm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Object object = parent.getItemAtPosition(position);
//                if (object != null) {
//                    String algorithm = object.toString();
//                    if (algorithm.matches("AES - 256")) {
//                        String newAlgorithm = "AES";
//                        if (!mSharedPreferences.getString(ENCRYPTION_ALGORITHM, "AES").matches(newAlgorithm)) {
//                            actionEncryptionAlgorithmChange(newAlgorithm);
//                        }
//                    } else if (algorithm.matches("Blowfish - 448")) {
//                        String newAlgorithm = "Blowfish";
//                        if (!mSharedPreferences.getString(ENCRYPTION_ALGORITHM, "AES").matches(newAlgorithm)) {
//                            actionEncryptionAlgorithmChange(newAlgorithm);
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
//    }

//
//    @SuppressLint("SetTextI18n")
//    private void actionScrambleKey() {
//        View titleView = View.inflate(mContext, R.layout.fragment_dialog_preview_title, null);
//        View view = View.inflate(mContext, R.layout.fragment_dialog_alertinfo, null);
//        // Dialog primitives
//        TextView title = titleView.findViewById(R.id.tvDialogTitle);
//        titleView.findViewById(R.id.tvDate).setVisibility(View.GONE);
//        TextView alertText = view.findViewById(R.id.tvDialogAlertText);
//
//        title.setText("Scramble Encryption Key");
//        alertText.setText(R.string.AlertDialog_ScrambleKey);
//        // Dialog builder
//        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.MyDialogStyle);
//        builder.setView(view);
//        builder.setCustomTitle(titleView);
//        builder.setNegativeButton(R.string.btnCancel, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        builder.setPositiveButton(R.string.btnScramble, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                new ScrambleCryptKey(mContext).execute();
//            }
//        });
//        // Dialog show
//        final AlertDialog dialog = builder.show();
//    }
//    private void actionEncryptionAlgorithmChange(final String algorithm) {
//        View titleView = View.inflate(mContext, R.layout.fragment_dialog_preview_title, null);
//        View v = View.inflate(mContext, R.layout.fragment_dialog_alertinfo, null);
//        // Dialog primitives
//        TextView title = titleView.findViewById(R.id.tvDialogTitle);
//        titleView.findViewById(R.id.tvDate).setVisibility(View.GONE);
//        TextView alertText = v.findViewById(R.id.tvDialogAlertText);
//
//        title.setText("Change Encryption Algorithm");
//        alertText.setText(R.string.AlertDialog_EncryptionAlgorithmChange);
//        // DIALOG BUILDER
//        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.MyDialogStyle);
//        builder.setView(v);
//        builder.setCustomTitle(titleView);
//        builder.setNegativeButton(R.string.btnCancel, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                mSpEncryptionAlgorithm.setSelection(mAdapterEncryptionAlgorithm.getPosition(mOldEncryptionAlgorithm));
//            }
//        });
//        builder.setPositiveButton(R.string.btnChange, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                new ChangeCryptAlgorithm(mContext, algorithm).execute();
//            }
//        });
//        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialog) {
//                mSpEncryptionAlgorithm.setSelection(mAdapterEncryptionAlgorithm.getPosition(mOldEncryptionAlgorithm));
//            }
//        });
//        final AlertDialog dialog = builder.show();
//    }
