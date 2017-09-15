package com.gerardogandeaga.cyberlock.Activitys.Activities.Menus;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LoginActivity;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Main.MainActivity;
import com.gerardogandeaga.cyberlock.R;

import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.APP_LOGGED_IN;
import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.mCountDownIsFinished;
import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.mCountDownTimer;
import static com.gerardogandeaga.cyberlock.Supports.Globals.AUTOSAVE;
import static com.gerardogandeaga.cyberlock.Supports.Globals.COMPLEXPASSCODE;
import static com.gerardogandeaga.cyberlock.Supports.Globals.DELAY_TIME;
import static com.gerardogandeaga.cyberlock.Supports.Globals.DIRECTORY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.ENCRYPTION_ALGO;
import static com.gerardogandeaga.cyberlock.Supports.Globals.LOGOUT_DELAY;

public class Settings extends AppCompatActivity implements View.OnClickListener
{
    // DATA
    private SharedPreferences mSharedPreferences;
    private static final int flags = Base64.DEFAULT;

    // SPINNERS
    private ArrayAdapter<CharSequence> mAdapterAutoLogoutDelay;
    private String mAutoLogoutDelay;
    private ArrayAdapter<CharSequence> mAdapterEncryptionMethod;
    private String mOldEncryptionMethod;

    // WIDGETS
    private TextView mTvChangePassword;
    private LinearLayout mAutoSave, mComplexPasscode,mScrambleKey;
    private CheckBox mCbAutoSave, mCbComplexPasscode;
    private Spinner mSpAutoLogoutDelay, mSpEncryptionMethod;

    private Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ACTIVITY_INTENT = null;

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }

        setupLayout();
    }

    private void setupLayout()
    {
        this.mSharedPreferences = getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE);

        // ACTION BAR TITLE AND BACK BUTTON
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");

        // SPINNER DATA
        // LOGOUT DELAY
        this.mSpAutoLogoutDelay = (Spinner) findViewById(R.id.spAutoLogoutDelay);
        mAdapterAutoLogoutDelay = ArrayAdapter.createFromResource(this, R.array.AutoLogoutDelay_array, R.layout.spinner_item);
        mAdapterAutoLogoutDelay.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.mSpAutoLogoutDelay.setAdapter(mAdapterAutoLogoutDelay);
        // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        // ENCRYPTION METHOD
        this.mSpEncryptionMethod = (Spinner) findViewById(R.id.spEncryptionMethod);
        mAdapterEncryptionMethod = ArrayAdapter.createFromResource(this, R.array.CryptALGO_array, R.layout.spinner_item);
        mAdapterEncryptionMethod.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.mSpEncryptionMethod.setAdapter(mAdapterEncryptionMethod);

        this.mCbAutoSave = (CheckBox) findViewById(R.id.cbAutoSave);
        this.mAutoSave = (LinearLayout) findViewById(R.id.AutoSave);
        this.mScrambleKey = (LinearLayout) findViewById(R.id.ScrambleKey);

        this.mTvChangePassword = (TextView) findViewById(R.id.tvChangePassword);

        this.mCbComplexPasscode = (CheckBox) findViewById(R.id.cbComplexPasscode);
        this.mComplexPasscode = (LinearLayout) findViewById(R.id.ComplexPasscode);

        this.mAutoSave.setOnClickListener(this);
        this.mTvChangePassword.setOnClickListener(this);
        this.mComplexPasscode.setOnClickListener(this);
        this.mScrambleKey.setOnClickListener(this);

        this.mCbAutoSave.setChecked(false);

        this.mCbComplexPasscode.setChecked(false);

        savedStates();

        this.mSpAutoLogoutDelay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                Object object = parent.getItemAtPosition(position);
                if (object != null)
                {
                    mAutoLogoutDelay = object.toString();
                    long time = 0;
                    switch (mAutoLogoutDelay)
                    {
                        case "Immediate": time = 0; break;
                        case "15 Seconds": time = 15000; break;
                        case "30 Seconds": time = 30000; break;
                        case "1 Minute": time = 60000; break;
                        case "5 Minutes": time = 300000; break;
                        case "10 Minutes": time = 600000; break;
                        case "30 Minutes": time = 1800000; break;
                        case "1 Hour": time = 3600000; break;
                        case "2 Hours": time = 7200000; break;
                        case "Never":
                            break;
                    }

                    System.out.println("Time = " + time);
                    mSharedPreferences.edit().putString(LOGOUT_DELAY, mAutoLogoutDelay).putLong(DELAY_TIME, time).apply();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });

        this.mSpEncryptionMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                Object object = parent.getItemAtPosition(position);
                if (object != null)
                {
                    String algorithm = object.toString();
                    if (algorithm.matches("AES - 256"))
                    {
                        String newAlgorithm = "AES";
                        if (!mSharedPreferences.getString(ENCRYPTION_ALGO, "AES").matches(newAlgorithm)) { onEncrptionMethodChange(newAlgorithm); }
                    }
                    else
                    if (algorithm.matches("Blowfish - 448"))
                    {
                        String newAlgorithm = "Blowfish";
                        if (!mSharedPreferences.getString(ENCRYPTION_ALGO, "AES").matches(newAlgorithm)) { onEncrptionMethodChange(newAlgorithm); }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });
    }

    private void savedStates()
    {
        // CHECK BOXES
        final boolean autoSave = mSharedPreferences.getBoolean(AUTOSAVE, false);

        if (!autoSave)
        {
            mCbAutoSave.setChecked(false);
        } else {
            mCbAutoSave.setChecked(true);
        }

        System.out.println(autoSave);

        // SPINNERS
        // LOGOUT DELAY
        int delaySpinnerPosition = mAdapterAutoLogoutDelay.getPosition(mSharedPreferences.getString(LOGOUT_DELAY, "Immediate"));
        mSpAutoLogoutDelay.setSelection(delaySpinnerPosition);

        // ENCRYPTION METHOD
        int algoSpinnerPosition;
        switch (mSharedPreferences.getString(ENCRYPTION_ALGO, "AES"))
        {
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

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.AutoSave: onAutoSave(); break;
            case R.id.tvChangePassword: onResetPassword(); break;
            case R.id.ComplexPasscode: onComplexPassword(); break;
            case R.id.ScrambleKey: onScrambleKey(); break;
        }
    }

    private void onAutoSave()
    {
        final boolean autoSave = mSharedPreferences.getBoolean(AUTOSAVE, false);

        if (!autoSave)
        {
            mSharedPreferences.edit().putBoolean(AUTOSAVE, true).apply();
            mCbAutoSave.setChecked(true);
        } else {
            mSharedPreferences.edit().putBoolean(AUTOSAVE, false).apply();
            mCbAutoSave.setChecked(false);
        }
    }

    private void onResetPassword()
    {
        ACTIVITY_INTENT = new Intent(this, Settings_ResetPin.class);
        finish();
        startActivity(ACTIVITY_INTENT);
    }

    private void onComplexPassword()
    {
        final boolean complexPassword = mSharedPreferences.getBoolean(COMPLEXPASSCODE, false);

        if (!complexPassword)
        {
            mSharedPreferences.edit().putBoolean(COMPLEXPASSCODE, true);
            mCbComplexPasscode.setChecked(true);

            // DIALOG BUILDER
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

            alertDialog.setTitle("Switch to complex passcode");
            alertDialog.setMessage("Switching to a complex passcode requires a password reset.");
            alertDialog.setCancelable(false);

            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                }
            });
            alertDialog.setPositiveButton("Continue", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();

                    ACTIVITY_INTENT = new Intent(mContext, Settings_ComplexPasscode.class);
                    startActivity(ACTIVITY_INTENT);
                }
            });
            alertDialog.show();
        } else {
            mSharedPreferences.edit().putBoolean(COMPLEXPASSCODE, false);
            mCbComplexPasscode.setChecked(false);

            // DIALOG BUILDER
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

            alertDialog.setTitle("Switch to short pin");
            alertDialog.setMessage("Switching to a simple pin requires a password reset.");
            alertDialog.setCancelable(false);

            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                }
            });
            alertDialog.setPositiveButton("Continue", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();

                    ACTIVITY_INTENT = new Intent(mContext, Settings_ComplexPasscode.class);
                    startActivity(ACTIVITY_INTENT);
                }
            });
            alertDialog.show();
        }
    }

    private void onScrambleKey()
    {
        // DIALOG BUILDER
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("Scramble Encryption Key");
        alertDialog.setMessage(R.string.AlertDialog_ScrambleKey);
        alertDialog.setCancelable(false);

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        alertDialog.setPositiveButton("Scramble", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();

                new Settings_ScrambleKey(mContext).execute();
            }
        });
        alertDialog.show();
    }

    private void onEncrptionMethodChange(final String algorithm)
    {
        // DIALOG BUILDER
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("Encryption Method");
        alertDialog.setMessage(R.string.AlertDialog_EncryptionMethodChange);
        alertDialog.setCancelable(false);

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                mSpEncryptionMethod.setSelection(mAdapterEncryptionMethod.getPosition(mOldEncryptionMethod));
                dialog.dismiss();
            }
        });
        alertDialog.setPositiveButton("Continue", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();

                new Settings_EncryptionMethodChange(mContext, algorithm).execute();
            }
        });
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) // ACTION BAR BACK BUTTON RESPONSE
    {
        switch (item.getItemId())
        {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // THIS IS THE START OF THE SCRIPT FOR *** THE "TO LOGIN FUNCTION" THIS DETECTS THE ON PRESSED, START, TABS AND HOME BUTTONS IN ORDER TO INITIALIZE SECURITY "FAIL-SAFE"
    @Override
    protected void onStart()
    {
        super.onStart();

        if (mCountDownIsFinished)
        {
            if (!APP_LOGGED_IN)
            {
                ACTIVITY_INTENT = new Intent(this, LoginActivity.class);
                this.finish(); // CLEAN UP AND END
                this.startActivity(ACTIVITY_INTENT); // GO TO LOGIN ACTIVITY
            }
        } else
        {
            if (mCountDownTimer != null)
            {
                System.out.println("Cancel Called!");
                mCountDownTimer.cancel();
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        if (ACTIVITY_INTENT == null) // NO PENDING ACTIVITIES ???(MAIN)--->(EDIT)???
        {
            ACTIVITY_INTENT = new Intent(this, MainActivity.class);
            this.finish();
            this.startActivity(ACTIVITY_INTENT);
            overridePendingTransition(R.anim.anim_push_upin, R.anim.anim_push_upin);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();

        if (!this.isFinishing()) // HOME AND TABS AND SCREEN OFF
        {
            if (ACTIVITY_INTENT == null) // NO PENDING ACTIVITIES ???(MAIN)--->(EDIT)???
            {
                new LogoutProtocol().logoutExecuteAutosaveOff(mContext);
            }
        }
    }

    @Override
    public void finish() // BACK BUTTON CACHES ACTIVITY ACTUAL START ---> MAIN ACTIVITY
    {
        Intent i = new Intent(this, MainActivity.class);
        this.startActivity(i);
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
}
