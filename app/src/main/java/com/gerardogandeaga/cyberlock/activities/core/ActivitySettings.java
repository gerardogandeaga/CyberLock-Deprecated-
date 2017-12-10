package com.gerardogandeaga.cyberlock.activities.core;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gerardogandeaga.cyberlock.activities.clearances.LoginActivity;
import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.support.Globals;
import com.gerardogandeaga.cyberlock.support.KeyChecker;
import com.gerardogandeaga.cyberlock.support.LogoutProtocol;
import com.gerardogandeaga.cyberlock.support.settings.SettingsChangeCryptAlgorithm;
import com.gerardogandeaga.cyberlock.support.settings.SettingsScrambleCryptKey;
import com.gerardogandeaga.cyberlock.support.settings.SettingsChangePasscode;

import static com.gerardogandeaga.cyberlock.support.Globals.AUTOSAVE;
import static com.gerardogandeaga.cyberlock.support.Globals.DELAY_TIME;
import static com.gerardogandeaga.cyberlock.support.Globals.DIRECTORY;
import static com.gerardogandeaga.cyberlock.support.Globals.ENCRYPTION_ALGO;
import static com.gerardogandeaga.cyberlock.support.Globals.LOGOUT_DELAY;
import static com.gerardogandeaga.cyberlock.support.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.support.LogoutProtocol.APP_LOGGED_IN;
import static com.gerardogandeaga.cyberlock.support.LogoutProtocol.mCountDownIsFinished;
import static com.gerardogandeaga.cyberlock.support.LogoutProtocol.mCountDownTimer;

public class ActivitySettings extends AppCompatActivity implements View.OnClickListener
{
    private Context mContext = this;
    private SharedPreferences mSharedPreferences;

    // RawData variables
    // Spinners
    private boolean mIsAutoSave;
    private ArrayAdapter<CharSequence>
            mAdapterAutoLogoutDelay,
            mAdapterEncryptionAlgorithm;
    private String
            mAutoLogoutDelay,
            mOldEncryptionAlgorithm;

    // Widgets
    private CheckBox
            mCbAutoSave;
    private Spinner
            mSpAutoLogoutDelay,
            mSpEncryptionAlgorithm;

    // Initial create methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Globals.COLORSCHEME(this);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        setupLayout();
    }
    private void setupLayout() {
        setContentView(R.layout.activity_settings);
        ACTIVITY_INTENT = null;
        //
        this.mSharedPreferences = getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE);
        this.mIsAutoSave = mSharedPreferences.getBoolean(AUTOSAVE, false);
        // Action bar
        setupSupportActionBar();
        // Widgets
        this.mSpAutoLogoutDelay = (Spinner) findViewById(R.id.spAutoLogoutDelay);
        this.mAdapterAutoLogoutDelay = ArrayAdapter.createFromResource(this, R.array.AutoLogoutDelay_array, R.layout.spinner_setting_text);
        this.mAdapterAutoLogoutDelay.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        this.mSpAutoLogoutDelay.setAdapter(mAdapterAutoLogoutDelay);

        this.mSpEncryptionAlgorithm = (Spinner) findViewById(R.id.spEncryptionAlgorithm);
        this.mAdapterEncryptionAlgorithm = ArrayAdapter.createFromResource(this, R.array.CryptALGO_array, R.layout.spinner_setting_text);
        this.mAdapterEncryptionAlgorithm.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        this.mSpEncryptionAlgorithm.setAdapter(mAdapterEncryptionAlgorithm);

        this.mCbAutoSave = (CheckBox) findViewById(R.id.cbAutoSave);
        RelativeLayout autoSave = (RelativeLayout) findViewById(R.id.AutoSave);
        RelativeLayout changePasscode = (RelativeLayout) findViewById(R.id.ChangePasscode);
        RelativeLayout scrambleKey = (RelativeLayout) findViewById(R.id.ScrambleKey);

        RelativeLayout autoLogoutDelay = (RelativeLayout) findViewById(R.id.AutoLogoutDelay);
        RelativeLayout encryptionAlgorithm = (RelativeLayout) findViewById(R.id.EncryptionAlgorithm);

        autoSave.setOnClickListener(this);
        changePasscode.setOnClickListener(this);
        scrambleKey.setOnClickListener(this);
        autoLogoutDelay.setOnClickListener(this);
        encryptionAlgorithm.setOnClickListener(this);

        this.mCbAutoSave.setClickable(false);
        this.mCbAutoSave.setChecked(false);

        savedStates();

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
        this.mSpEncryptionAlgorithm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object object = parent.getItemAtPosition(position);
                if (object != null) {
                    String algorithm = object.toString();
                    if (algorithm.matches("AES - 256")) {
                        String newAlgorithm = "AES";
                        if (!mSharedPreferences.getString(ENCRYPTION_ALGO, "AES").matches(newAlgorithm)) {
                            onEncryptionAlgorithmChange(newAlgorithm);
                        }
                    } else if (algorithm.matches("Blowfish - 448")) {
                        String newAlgorithm = "Blowfish";
                        if (!mSharedPreferences.getString(ENCRYPTION_ALGO, "AES").matches(newAlgorithm)) {
                            onEncryptionAlgorithmChange(newAlgorithm);
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
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
        getSupportActionBar().setTitle("ActivitySettings");
        getSupportActionBar().setSubtitle("Functionality And Visuals");
    }
    private void savedStates() {
        // CHECK BOXES
        if (!this.mIsAutoSave) {
            mCbAutoSave.setChecked(false);
        } else {
            mCbAutoSave.setChecked(true);
        }

        int delaySpinnerPosition = mAdapterAutoLogoutDelay.getPosition(this.mSharedPreferences.getString(LOGOUT_DELAY, "Immediate"));
        mSpAutoLogoutDelay.setSelection(delaySpinnerPosition);

        // ENCRYPTION METHOD
        int algoSpinnerPosition;
        switch (this.mSharedPreferences.getString(ENCRYPTION_ALGO, "AES")) {
            case "AES":
                algoSpinnerPosition = mAdapterEncryptionAlgorithm.getPosition("AES - 256");
                mSpEncryptionAlgorithm.setSelection(algoSpinnerPosition);
                mOldEncryptionAlgorithm = mSpEncryptionAlgorithm.getItemAtPosition(algoSpinnerPosition).toString();
                break;
            case "Blowfish":
                algoSpinnerPosition = mAdapterEncryptionAlgorithm.getPosition("Blowfish - 448");
                mSpEncryptionAlgorithm.setSelection(algoSpinnerPosition);
                mOldEncryptionAlgorithm = mSpEncryptionAlgorithm.getItemAtPosition(algoSpinnerPosition).toString();
                break;
        }
    }
    private int setMenuItemsColour() {
        return getResources().getColor(R.color.matLightWhiteYellow);
    }
    // -------------------------

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
            case R.id.AutoSave: onAutoSave(); break;
            case R.id.ChangePasscode: onChangePasscode(); break;
            case R.id.ScrambleKey: onScrambleKey(); break;
            case R.id.AutoLogoutDelay: mSpAutoLogoutDelay.performClick(); break;
            case R.id.EncryptionAlgorithm: mSpEncryptionAlgorithm.performClick(); break;
        }
    }
    // --------

    // #########################################################################
    private void onAutoSave() {
        final boolean autoSave = this.mSharedPreferences.getBoolean(AUTOSAVE, false);

        if (!autoSave) {
            this.mSharedPreferences.edit().putBoolean(AUTOSAVE, true).apply();
            mCbAutoSave.setChecked(true);
        } else {
            this.mSharedPreferences.edit().putBoolean(AUTOSAVE, false).apply();
            mCbAutoSave.setChecked(false);
        }
    }
    private void onChangePasscode() {
        View v = View.inflate(mContext, R.layout.dialog_view_passcode_change, null);
        // Dialog primitives
        final EditText current = (EditText) v.findViewById(R.id.etCurrent);
        final EditText initial = (EditText) v.findViewById(R.id.etInitial);
        final EditText Final = (EditText) v.findViewById(R.id.etFinal);

        // Dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(v);
        builder.setTitle(R.string.titleChangePasscode);
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
                        if (new KeyChecker(mContext, mSharedPreferences).keyCompare(p0)) { // check if original passcode matches the one entered
                            final String[] passcodes = new String[3];
                            passcodes[0] = p0;
                            passcodes[1] = p1;
                            passcodes[2] = p2;
                            SettingsChangePasscode obj = new SettingsChangePasscode(mContext, mSharedPreferences, passcodes);
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
    private void onScrambleKey() {
        View v = View.inflate(mContext, R.layout.dialog_view_alert_info, null);
        // Dialog primitives
        TextView alertText = (TextView) v.findViewById(R.id.tvDialogAlertText);
        alertText.setText(R.string.AlertDialog_ScrambleKey);
        // Dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(v);
        builder.setTitle(R.string.titleScrambleEncryptionKey);
        builder.setNegativeButton(R.string.btnCancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(R.string.btnScramble, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                new SettingsScrambleCryptKey(mContext).execute();
            }
        });
        // Dialog show
        final AlertDialog dialog = builder.show();
    }
    private void onEncryptionAlgorithmChange(final String algorithm) {
        View v = View.inflate(mContext, R.layout.dialog_view_alert_info, null);
        // Dialog primitives
        TextView alertText = (TextView) v.findViewById(R.id.tvDialogAlertText);
        alertText.setText(R.string.AlertDialog_EncryptionAlgorithmChange);
        // DIALOG BUILDER
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(v);
        builder.setTitle(R.string.titleChangeEncryptionAlgo);
        builder.setNegativeButton(R.string.btnCancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mSpEncryptionAlgorithm.setSelection(mAdapterEncryptionAlgorithm.getPosition(mOldEncryptionAlgorithm));
            }
        });
        builder.setPositiveButton(R.string.btnChange, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                new SettingsChangeCryptAlgorithm(mContext, algorithm).execute();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mSpEncryptionAlgorithm.setSelection(mAdapterEncryptionAlgorithm.getPosition(mOldEncryptionAlgorithm));
            }
        });
        final AlertDialog dialog = builder.show();
    }
    // #########################################################################

    // THIS IS THE START OF THE SCRIPT FOR *** THE "TO LOGIN FUNCTION" THIS DETECTS THE ON PRESSED, START, TABS AND HOME BUTTONS IN ORDER TO INITIALIZE SECURITY "FAIL-SAFE"
    @Override protected void onStart() {
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
    @Override public void onBackPressed() {
        super.onBackPressed();
        if (ACTIVITY_INTENT == null) // NO PENDING ACTIVITIES ???(MAIN)--->(EDIT)???
        {
            ACTIVITY_INTENT = new Intent(this, ActivityMain.class);
            this.finish();
            this.startActivity(ACTIVITY_INTENT);
            overridePendingTransition(R.anim.anim_slide_inleft, R.anim.anim_slide_outright);
        }
    }
    @Override public void onPause() {
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