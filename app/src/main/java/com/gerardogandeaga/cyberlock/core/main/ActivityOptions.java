package com.gerardogandeaga.cyberlock.core.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.core.clearances.ActivityLogin;
import com.gerardogandeaga.cyberlock.android.CustomToast;
import com.gerardogandeaga.cyberlock.utils.ListFormat;
import com.gerardogandeaga.cyberlock.utils.SharedPreferences;
import com.gerardogandeaga.cyberlock.utils.Resources;
import com.gerardogandeaga.cyberlock.utils.graphics.Graphics;
import com.gerardogandeaga.cyberlock.utils.security.KeyChecker;
import com.gerardogandeaga.cyberlock.utils.security.LogoutProtocol;
import com.gerardogandeaga.cyberlock.utils.security.options.ChangePassword;

import org.jetbrains.annotations.Contract;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.gerardogandeaga.cyberlock.utils.SharedPreferences.DIRECTORY;
import static com.gerardogandeaga.cyberlock.utils.security.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.utils.security.LogoutProtocol.APP_LOGGED_IN;
import static com.gerardogandeaga.cyberlock.utils.security.LogoutProtocol.mCountDownTimer;
import static com.gerardogandeaga.cyberlock.utils.security.LogoutProtocol.mIsCountDownTimerFinished;

/**
 * @author gerardogandeaga
 */
public class ActivityOptions extends AppCompatActivity implements View.OnClickListener {
    Context mContext = this;

    @BindView(R.id.toolbar) Toolbar mToolbar;

    @BindView(R.id.AutoSave)        LinearLayout mLinAutoSave;
    @BindView(R.id.ListFormat)      LinearLayout mLinListFormat;
    @BindView(R.id.TaggedHeaders)   LinearLayout mLinTaggedHeaders;
    @BindView(R.id.AutoLogoutDelay) LinearLayout mLinAutoLogoutDelay;
    @BindView(R.id.ChangePassword)  LinearLayout mLinChangePassword;
    @BindView(R.id.GitHub)          LinearLayout mLinGitHub;
    @BindView(R.id.About)           LinearLayout mLinAbout;

    @BindView(R.id.swAutoSave)          Switch mSwAutoSave;
    @BindView(R.id.imgDirection)        ImageView mImgDirection;
    @BindView(R.id.imgListFormat)       ImageView mImgListFormat;
    @BindView(R.id.swTaggedHeaders)     Switch mSwTaggedHeaders;
    @BindView(R.id.spAutoLogoutDelay)   Spinner mSpLogoutDelay;
    // password change widgets
    @BindView(R.id.inputChangePassword) LinearLayout mInputChangePassword;
    @BindView(R.id.btnRegister)         Button mBtnRegister;
    @BindView(R.id.etCurrent)           EditText mEtCurrentPass;
    @BindView(R.id.etInitial)           EditText mEtInitialPass;
    @BindView(R.id.etFinal)             EditText mEtFinalPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Themes.setTheme(this);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        ACTIVITY_INTENT = null;
        super.onCreate(savedInstanceState);

        View view  = View.inflate(this, R.layout.activity_options, null);
        setContentView(view);
        ButterKnife.bind(this);

        setupSupportActionBar();
        widgets();
    }
    private void setupSupportActionBar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Application Options");
        getSupportActionBar().setHomeAsUpIndicator(Graphics.BasicFilter.mutateHomeAsUpIndicatorDrawable(
                this, Resources.getDrawable(this, R.drawable.ic_back)));
    }

    private void widgets() {
        mLinAutoSave.setOnClickListener(this);
        mLinListFormat.setOnClickListener(this);
        mLinTaggedHeaders.setOnClickListener(this);
        mLinAutoLogoutDelay.setOnClickListener(this);
        mLinChangePassword.setOnClickListener(this);

        mLinGitHub.setOnClickListener(this);
        mLinAbout.setOnClickListener(this);

        savedStates();
    }

    // saved states
    private void savedStates() {
        iniAutoSave();
        iniLogoutDelay();
        iniChangePassword();
        iniListFormat();
        iniTaggedHeaders();
        iniOpenSourceLibraries();
    }
    private void iniAutoSave() {
        mSwAutoSave.setChecked(SharedPreferences.getAutoSave(this));
    }
    private void iniListFormat() {
        if (SharedPreferences.getListFormat(this).matches(ListFormat.GRID)) {
            mImgListFormat.setImageDrawable(Resources.getDrawable(this, R.drawable.graphic_list_grid));
        } else {
            mImgListFormat.setImageDrawable(Resources.getDrawable(this, R.drawable.graphic_list_linear));
        }
    }
    private void iniTaggedHeaders() {
        mSwTaggedHeaders.setChecked(SharedPreferences.getTaggedHeaders(this));
    }
    private void iniLogoutDelay() {
        ArrayAdapter<CharSequence> adapterLogoutDelay = ArrayAdapter.createFromResource(
                this,
                R.array.str_array_auto_logout_delay,
                R.layout.spinner_setting_text);
        adapterLogoutDelay.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        mSpLogoutDelay.setAdapter(adapterLogoutDelay);
        mSpLogoutDelay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object object = parent.getItemAtPosition(position);
                if (object != null) {
                    String logoutDelay = object.toString();
                    int time = 0;
                    switch (logoutDelay) {
                        case "1 Minute":   time = 60000; break;
                        case "2 Minutes":  time = 120000; break;
                        case "5 Minutes":  time = 300000; break;
                        case "10 Minutes": time = 600000; break;
                        case "15 Minutes": time = 900000; break;
                        case "30 Minutes": time = 1800000; break;
                        default:           time = 0; break;
                    }

                    System.out.println("Time = " + time);
                    SharedPreferences.setLogoutDelay(mContext, logoutDelay);
                    SharedPreferences.setLogoutDelayTime(mContext, time);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // saved state
        int spinnerPosition = adapterLogoutDelay.getPosition(SharedPreferences.getLogoutDelay(this));
        mSpLogoutDelay.setSelection(spinnerPosition);
    }
    private void iniChangePassword() {
        mBtnRegister.setOnClickListener(this);
        mInputChangePassword.setVisibility(View.GONE);
        mImgDirection.setRotation(-90);
    }
    private void iniOpenSourceLibraries() {
        mLinGitHub.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // settings
            case R.id.AutoSave:        onAutoSave(); break;
            case R.id.ListFormat:      onListFormat(); break;
            case R.id.TaggedHeaders:   onTaggedHeaders(); break;
            case R.id.AutoLogoutDelay: onLogoutDelay(); break;
            // case password change
            case R.id.ChangePassword:  onChangePasswordActivate(); break;
            case R.id.btnRegister:     onChangePasswordRegister(); break;
            //
            case R.id.GitHub: onOpenSourceLibraries(); break;
        }
    }

    // on clicks
    private void onAutoSave() {
        SharedPreferences.setAutoSave(this, !SharedPreferences.getAutoSave(this));
        mSwAutoSave.setChecked(SharedPreferences.getAutoSave(this));
    }
    private void onListFormat() {
        if (SharedPreferences.getListFormat(this).matches(ListFormat.GRID)) {
            SharedPreferences.setListFormat(this, ListFormat.LINEAR);
            mImgListFormat.setImageDrawable(Resources.getDrawable(this, R.drawable.graphic_list_linear));
            CustomToast.buildAndShowToast(this, "Linear List Format", CustomToast.INFORMATION, CustomToast.LENGTH_SHORT);
        } else {
            SharedPreferences.setListFormat(this, ListFormat.GRID);
            mImgListFormat.setImageDrawable(Resources.getDrawable(this, R.drawable.graphic_list_grid));
            CustomToast.buildAndShowToast(this, "Grid List Format", CustomToast.INFORMATION, CustomToast.LENGTH_SHORT);
        }
    }
    private void onTaggedHeaders() {
        SharedPreferences.setTaggedHeaders(this, !SharedPreferences.getTaggedHeaders(this));
        mSwTaggedHeaders.setChecked(SharedPreferences.getTaggedHeaders(this));
    }
    private void onLogoutDelay() {
        mSpLogoutDelay.performClick();
    }
    private void onChangePasswordActivate() {
        if (mInputChangePassword.getVisibility() != View.GONE) {
            mInputChangePassword.setVisibility(View.GONE);
            mImgDirection.setRotation(-90);
        } else {
            mInputChangePassword.setVisibility(View.VISIBLE);
            mImgDirection.setRotation(90);
        }
    }
    private void onChangePasswordRegister() {
        final String currentP = mEtCurrentPass.getText().toString();
        final String initialP = mEtInitialPass.getText().toString();
        final String finalP = mEtFinalPass.getText().toString();

        if (!isEmpty(currentP) && !isEmpty(initialP) && !isEmpty(finalP)) {
            if (initialP.equals(finalP)) {
                if (KeyChecker.comparePasswords(this, currentP)) {
                    new ChangePassword(this, this.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE), currentP, initialP);
                    mEtCurrentPass.getText().clear();
                    mEtInitialPass.getText().clear();
                    mEtFinalPass.getText().clear();
                    mInputChangePassword.setVisibility(View.GONE);
                } else {
                    CustomToast.buildAndShowToast(this, "Incorrect Password", CustomToast.INFORMATION, CustomToast.LENGTH_SHORT);
                }
            } else {
                CustomToast.buildAndShowToast(this, "Passwords Do Not Match", CustomToast.INFORMATION, CustomToast.LENGTH_SHORT);
            }
        } else {
            CustomToast.buildAndShowToast(this, "All Fields Need To Be Filled", CustomToast.INFORMATION, CustomToast.LENGTH_SHORT);
        }
    }
    private void onOpenSourceLibraries() {
        ACTIVITY_INTENT = new Intent(this, ActivityExternalLibs.class);
        this.finish();
        this.startActivity(ACTIVITY_INTENT);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
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
//            overridePendingTransition(R.anim.anim_slide_inleft, R.anim.anim_slide_outright);
        }
    }
    @Override public void onPause() {
        super.onPause();

        if (!this.isFinishing()) { // HOME AND TABS AND SCREEN OFF
            if (ACTIVITY_INTENT == null) { // NO PENDING ACTIVITIES ???(MAIN)--->(EDIT)???
                new LogoutProtocol().logoutExecuteAutosaveOff(this);
            }
        }
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Contract("null -> true")
    private boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
