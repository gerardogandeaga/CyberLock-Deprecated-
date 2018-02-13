package com.gerardogandeaga.cyberlock.activities.core;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.activities.clearances.ActivityLogin;
import com.gerardogandeaga.cyberlock.utils.LogoutProtocol;
import com.gerardogandeaga.cyberlock.utils.Stored;
import com.gerardogandeaga.cyberlock.utils.graphics.DrawableColours;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.gerardogandeaga.cyberlock.utils.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.utils.LogoutProtocol.APP_LOGGED_IN;
import static com.gerardogandeaga.cyberlock.utils.LogoutProtocol.mCountDownTimer;
import static com.gerardogandeaga.cyberlock.utils.LogoutProtocol.mIsCountDownTimerFinished;

public class ActivityOptions extends AppCompatActivity implements View.OnClickListener {
    Context mContext = this;

    @BindView(R.id.toolbar) Toolbar mToolbar;

    @BindView(R.id.AutoSave)        LinearLayout mLinAutoSave;
    @BindView(R.id.AutoLogoutDelay) LinearLayout mLinAutoLogoutDelay;
    @BindView(R.id.ChangePassword)  LinearLayout mLinChangePassword;
    @BindView(R.id.ListFormat)      LinearLayout mLinListFormat;
    @BindView(R.id.GitHub)          LinearLayout mLinGitHub;
    @BindView(R.id.About)           LinearLayout mLinAbout;

    @BindView(R.id.swAutoSave)          SwitchCompat mSwAutoSave;
    @BindView(R.id.spAutoLogoutDelay)   Spinner mSpLogoutDelay;
    @BindView(R.id.inputChangePassword) LinearLayout mInputChangePassword;
    @BindView(R.id.imgDirection)        ImageView mImgDirection;
    @BindView(R.id.imgListFormat)       ImageView mImgListFormat;

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
        getSupportActionBar().setHomeAsUpIndicator(DrawableColours.mutateHomeAsUpIndicatorDrawable(
                this, this.getResources().getDrawable(R.drawable.ic_back)));
    }

    private void widgets() {
        mLinAutoSave.setOnClickListener(this);
        mLinAutoLogoutDelay.setOnClickListener(this);
        mLinChangePassword.setOnClickListener(this);
        mLinListFormat.setOnClickListener(this);
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
    }
    private void iniAutoSave() {
        mSwAutoSave.setChecked(Stored.getAutoSave(this));
    }
    private void iniLogoutDelay() {
        ArrayAdapter<CharSequence> adapterLogoutDelay = ArrayAdapter.createFromResource(
                this,
                R.array.AutoLogoutDelay_array,
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
                    Stored.setLogoutDelay(mContext, logoutDelay);
                    Stored.setLogoutDelayTime(mContext, time);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // saved state
        int spinnerPosition = adapterLogoutDelay.getPosition(Stored.getLogoutDelay(this));
        mSpLogoutDelay.setSelection(spinnerPosition);
    }
    private void iniChangePassword() {
        mInputChangePassword.setVisibility(View.GONE);
        mImgDirection.setRotation(-90);
    }
    private void iniListFormat() {
        if (Stored.getListFormat(this).matches("RV_STAGGEREDGRID")) {
            mImgListFormat.setImageDrawable(this.getResources().getDrawable(R.drawable.graphic_list_grid));
        } else {
            mImgListFormat.setImageDrawable(this.getResources().getDrawable(R.drawable.graphic_list_linear));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // settings
            case R.id.AutoSave: onAutoSave(); break;
            case R.id.AutoLogoutDelay: onLogoutDelay(); break;
            case R.id.ChangePassword: onChangePassword(); break;
            case R.id.ListFormat: onListFormat(); break;
        }
    }

    // on clicks
    private void onAutoSave() {
        Stored.setAutoSave(this, !Stored.getAutoSave(this));
        mSwAutoSave.setChecked(Stored.getAutoSave(this));
    }
    private void onLogoutDelay() {
        mSpLogoutDelay.performClick();
    }
    private void onChangePassword() {
        if (mInputChangePassword.getVisibility() != View.GONE) {
            mInputChangePassword.setVisibility(View.GONE);
            mImgDirection.setRotation(-90);
        } else {
            mInputChangePassword.setVisibility(View.VISIBLE);
            mImgDirection.setRotation(90);
        }
    }
    private void onListFormat() {
        if (Stored.getListFormat(this).matches("RV_STAGGEREDGRID")) {
            Stored.setListFormat(this, "RV_LINEAR");
            mImgListFormat.setImageDrawable(this.getResources().getDrawable(R.drawable.graphic_list_linear));
            Toast.makeText(this, "Linear list format", Toast.LENGTH_SHORT).show();
        } else {
            Stored.setListFormat(this, "RV_STAGGEREDGRID");
            mImgListFormat.setImageDrawable(this.getResources().getDrawable(R.drawable.graphic_list_grid));
            Toast.makeText(this, "Grid list format", Toast.LENGTH_SHORT).show();
        }
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
}
