package com.gerardogandeaga.cyberlock.core.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.utils.Pref;

import org.jetbrains.annotations.Contract;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author gerardogandeaga
 */
public class OptionsActivity extends CoreActivity implements View.OnClickListener {
    Context mContext = this;

    @BindView(R.id.AutoSave)        LinearLayout mLinAutoSave;
    @BindView(R.id.TaggedHeaders)   LinearLayout mLinTaggedHeaders;
    @BindView(R.id.AutoLogoutDelay) LinearLayout mLinAutoLogoutDelay;
    @BindView(R.id.ChangePassword)  LinearLayout mLinChangePassword;
    @BindView(R.id.GitHub)          LinearLayout mLinGitHub;
    @BindView(R.id.About)           LinearLayout mLinAbout;

    @BindView(R.id.swAutoSave)          Switch mSwAutoSave;
    @BindView(R.id.imgDirection)        ImageView mImgDirection;
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
        setContentView(R.layout.activity_container_static_toolbar);
        bindView();

        mLinAutoSave.setOnClickListener(this);
        mLinTaggedHeaders.setOnClickListener(this);
        mLinAutoLogoutDelay.setOnClickListener(this);
        mLinChangePassword.setOnClickListener(this);

        mLinGitHub.setOnClickListener(this);
        mLinAbout.setOnClickListener(this);

        savedStates();

        setupActionBar("Application Options", null, R.drawable.ic_back);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void bindView() {
        ((FrameLayout) findViewById(R.id.fragment_container)).addView(View.inflate(this, R.layout.view_options, null));
        ButterKnife.bind(this);
    }

    // saved states
    private void savedStates() {
        iniAutoSave();
        iniLogoutDelay();
        iniChangePassword();
        iniTaggedHeaders();
        iniOpenSourceLibraries();
    }
    private void iniAutoSave() {
        mSwAutoSave.setChecked(Pref.getAutoSave(this));
}
    private void iniTaggedHeaders() {
        mSwTaggedHeaders.setChecked(Pref.getTaggedHeaders(this));
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
                    Pref.setLogoutDelay(mContext, logoutDelay);
                    Pref.setLogoutDelayTime(mContext, time);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // saved state
        int spinnerPosition = adapterLogoutDelay.getPosition(Pref.getLogoutDelay(this));
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
            case R.id.AutoSave:
                onAutoSave();
                break;
            case R.id.TaggedHeaders:
                onTaggedHeaders();
                break;
            case R.id.AutoLogoutDelay:
                onLogoutDelay();
                break;
            // case password change
            case R.id.ChangePassword:
                onChangePasswordActivate();
                break;
            case R.id.btnRegister:
                onChangePasswordRegister();
                break;
            //
            case R.id.GitHub:
                onOpenSourceLibraries();
                break;
        }
    }

    // on clicks
    private void onAutoSave() {
        Pref.setAutoSave(this, !Pref.getAutoSave(this));
        mSwAutoSave.setChecked(Pref.getAutoSave(this));
    }
    private void onTaggedHeaders() {
        Pref.setTaggedHeaders(this, !Pref.getTaggedHeaders(this));
        mSwTaggedHeaders.setChecked(Pref.getTaggedHeaders(this));
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
    }
    private void onOpenSourceLibraries() {
        newIntentGoTo(LibActivity.class);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        newIntent(NoteActivity.class);
        super.onBackPressed();
    }

    @Contract("null -> true")
    private boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
