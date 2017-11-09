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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LoginActivity;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Main.MainActivity;
import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.Supports.Globals;
import com.gerardogandeaga.cyberlock.Supports.LogoutProtocol;
import com.gerardogandeaga.cyberlock.Supports.Settings_EncryptionMethodChange;
import com.gerardogandeaga.cyberlock.Supports.Settings_ScrambleKey;

import static com.gerardogandeaga.cyberlock.Supports.Globals.AUTOSAVE;
import static com.gerardogandeaga.cyberlock.Supports.Globals.DELAY_TIME;
import static com.gerardogandeaga.cyberlock.Supports.Globals.DIRECTORY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.ENCRYPTION_ALGO;
import static com.gerardogandeaga.cyberlock.Supports.Globals.LOGOUT_DELAY;
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
            mIsAutoSave;
    private ArrayAdapter<CharSequence>
            mAdapterAutoLogoutDelay,
            mAdapterEncryptionMethod;
    private String
            mAutoLogoutDelay,
            mOldEncryptionMethod;

    // WIDGETS
    private CheckBox
            mCbAutoSave;
    private Spinner
            mSpAutoLogoutDelay,
            mSpEncryptionMethod;

    // INITIAL ON CREATE METHODS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Globals.COLORSCHEME(this);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        setupLayout();
    }
    private void setupLayout() {
        setContentView(R.layout.view_settings);
        ACTIVITY_INTENT = null;
        //
        this.mSharedPreferences = getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE);
        this.mIsAutoSave = mSharedPreferences.getBoolean(AUTOSAVE, false);

        // ACTION BAR TITLE AND BACK BUTTON
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setSubtitle("Functionality And Visuals");

        this.mSpAutoLogoutDelay = (Spinner) findViewById(R.id.spAutoLogoutDelay);
        mAdapterAutoLogoutDelay = ArrayAdapter.createFromResource(this, R.array.AutoLogoutDelay_array, R.layout.spinner_setting_text);
        mAdapterAutoLogoutDelay.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        this.mSpAutoLogoutDelay.setAdapter(mAdapterAutoLogoutDelay);

        this.mSpEncryptionMethod = (Spinner) findViewById(R.id.spEncryptionMethod);
        mAdapterEncryptionMethod = ArrayAdapter.createFromResource(this, R.array.CryptALGO_array, R.layout.spinner_setting_text);
        mAdapterEncryptionMethod.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        this.mSpEncryptionMethod.setAdapter(mAdapterEncryptionMethod);

        this.mCbAutoSave = (CheckBox) findViewById(R.id.cbAutoSave);
        RelativeLayout autoSave = (RelativeLayout) findViewById(R.id.AutoSave);
        RelativeLayout changePasscode = (RelativeLayout) findViewById(R.id.ChangePasscode);
        RelativeLayout scrambleKey = (RelativeLayout) findViewById(R.id.ScrambleKey);

        autoSave.setOnClickListener(this);
        changePasscode.setOnClickListener(this);
        scrambleKey.setOnClickListener(this);

        this.mCbAutoSave.setClickable(false);
        this.mCbAutoSave.setChecked(false);

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
                onChangePasscode();
                break;

            case R.id.ScrambleKey:
                onScrambleKey();
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
    private void onChangePasscode() {
        View v = View.inflate(mContext, R.layout.dialog_view_passcode_change, null);
        // Dialog primitives
        ImageView icon = (ImageView) v.findViewById(R.id.imgDialogAction);
        TextView title = (TextView) v.findViewById(R.id.tvDialogTitle);
        Button negative = (Button) v.findViewById(R.id.btnDialogNegative);
        Button positive = (Button) v.findViewById(R.id.btnDialogPositive);
        icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_passcode));
        title.setText("Change Passcode");
        negative.setText("Cancel");
        positive.setText("Change");
        // -----------------
        EditText current = (EditText) v.findViewById(R.id.etCurrent);
        EditText initial = (EditText) v.findViewById(R.id.etInitial);
        EditText Final = (EditText) v.findViewById(R.id.etFinal);

        // DIALOG BUILDER
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(v);
        final AlertDialog dialog = builder.show();
        // -----------------------------------------------------------
        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                new Settings_ScrambleKey(mContext).execute();
            }
        });
        // -----------------------------------------------------------
    }                                    //#
    private void onScrambleKey() {
        View v = View.inflate(mContext, R.layout.dialog_view_alert_info, null);
        // Dialog primitives
        ImageView icon = (ImageView) v.findViewById(R.id.imgDialogAction);
        TextView title = (TextView) v.findViewById(R.id.tvDialogTitle);
        Button negative = (Button) v.findViewById(R.id.btnDialogNegative);
        Button positive = (Button) v.findViewById(R.id.btnDialogPositive);
        icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_keys));
        title.setText("Scramble Encryption Key");
        negative.setText("Cancel");
        positive.setText("Scramble");
        // -----------------
        TextView alertText = (TextView) v.findViewById(R.id.tvDialogAlertText);
        alertText.setText(R.string.AlertDialog_ScrambleKey);
        // DIALOG BUILDER
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(v);
        final AlertDialog dialog = builder.show();
        // -----------------------------------------------------------
        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                new Settings_ScrambleKey(mContext).execute();
            }
        });
        // -----------------------------------------------------------
    }                                       //#
    private void onEncryptionMethodChange(final String algorithm) {
        View v = View.inflate(mContext, R.layout.dialog_view_alert_info, null);
        // Dialog primitives
        ImageView icon = (ImageView) v.findViewById(R.id.imgDialogAction);
        TextView title = (TextView) v.findViewById(R.id.tvDialogTitle);
        Button negative = (Button) v.findViewById(R.id.btnDialogNegative);
        Button positive = (Button) v.findViewById(R.id.btnDialogPositive);
        icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_shield));
        title.setText("Change Encryption Method");
        negative.setText("Cancel");
        positive.setText("Change");
        // -----------------
        TextView alertText = (TextView) v.findViewById(R.id.tvDialogAlertText);
        alertText.setText(R.string.AlertDialog_EncryptionMethodChange);
        // DIALOG BUILDER
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(v);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mSpEncryptionMethod.setSelection(mAdapterEncryptionMethod.getPosition(mOldEncryptionMethod));
            }
        });
        final AlertDialog dialog = builder.show();
        // -----------------------------------------------------------
        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mSpEncryptionMethod.setSelection(mAdapterEncryptionMethod.getPosition(mOldEncryptionMethod));
            }
        });
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                new Settings_EncryptionMethodChange(mContext, algorithm).execute();
            }
        });
        // -----------------------------------------------------------
    }      //#
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
            overridePendingTransition(R.anim.anim_slide_inleft, R.anim.anim_slide_outright);
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