package com.gerardogandeaga.cyberlock.activities.clearances;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.activities.core.ActivityEdit;
import com.gerardogandeaga.cyberlock.activities.core.ActivityMain;
import com.gerardogandeaga.cyberlock.activities.dialogs.DialogCustomLoad;
import com.gerardogandeaga.cyberlock.android.CustomToast;
import com.gerardogandeaga.cyberlock.database.objects.NoteObject;
import com.gerardogandeaga.cyberlock.utils.security.KeyChecker;

import static com.gerardogandeaga.cyberlock.utils.SharedPreferences.CRYPT_KEY;
import static com.gerardogandeaga.cyberlock.utils.SharedPreferences.DIRECTORY;
import static com.gerardogandeaga.cyberlock.utils.SharedPreferences.PASSWORD;
import static com.gerardogandeaga.cyberlock.utils.SharedPreferences.TMP_PWD;
import static com.gerardogandeaga.cyberlock.utils.security.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.utils.security.LogoutProtocol.APP_LOGGED_IN;

public class ActivityLogin extends AppCompatActivity {
    private Context mContext = this;
    private SharedPreferences mSharedPreferences;

    // widgets
    private DialogCustomLoad mLoadDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        // fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ACTIVITY_INTENT = null;
        this.mSharedPreferences = getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE);
        super.onCreate(savedInstanceState);

        // check is user is already registered
        if (!isRegistered()) {
            ACTIVITY_INTENT = new Intent(this, ActivityRegistration.class);
            this.finish();
            this.startActivity(ACTIVITY_INTENT);
        } else {
            setContentView(View.inflate(this, R.layout.activity_login, null));
            final EditText etInput = findViewById(R.id.etPasscode);
            final Button btnLogin = findViewById(R.id.btnLogin);

            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String password = etInput.getText().toString();

                    if (!password.matches("")) {
                        login(password);
                    } else {
                        CustomToast.buildAndShowToast(mContext, "No Password Detected", CustomToast.INFORMATION, CustomToast.LENGTH_SHORT);
                    }
                }
            });
        }
    }

    // login process
    @SuppressLint("StaticFieldLeak")
    private void login(final String password) {
        this.mLoadDialog = new DialogCustomLoad(mContext);
        mLoadDialog.indeterminateProgress("Verifying Password, Please Wait.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (KeyChecker.comparePasswords(mContext, password)) { // check if original passcode matches the one entered
                    setGlobalVariables(password);
                    loginIntent();
                    mLoadDialog.dismiss();
                } else {
                    mLoadDialog.dismiss();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CustomToast.buildAndShowToast(mContext, "Password Is Incorrect, Please Try Again", CustomToast.ERROR, CustomToast.LENGTH_LONG);
                        }
                    });
                }
            }
        }).start();
    }
    /*
    this function tries to pull the last edit data package that was opened and being edited
    immediately before the system auto logged out
    */
    private void loginIntent() {
        Intent intent;
        Bundle bundle = getIntent().getExtras();

        // if bundle is not null and last edit is true then pull the last data package
        if (bundle != null && bundle.getBoolean("edit?", false)) {
            intent = new Intent(ActivityLogin.this, ActivityEdit.class);

            // pull isNew boolean from previous intent and place into new intent to set edit state
            intent.putExtra("isNew?", (boolean) bundle.get("isNew?"));
            // pull the data package from previous intent and place into new intent to resume edit
            intent.putExtra("data", (NoteObject) bundle.get("lastDB"));

            // clean up by removing extras
            intent.removeExtra("edit?");
            intent.removeExtra("lastDB");
        } else {
            // empty intent to the main activity
            intent = new Intent(ActivityLogin.this, ActivityMain.class);
        }

        this.finish();
        ActivityLogin.this.startActivity(intent); // MOVE TO MAIN ACTIVITY
    }

    // set global variables necessary for the application to function properly
    private void setGlobalVariables(String password) {
        TMP_PWD = password;
        APP_LOGGED_IN = true;
    }

    // THIS IS THE START OF THE SCRIPT FOR *** THE "TO LOGIN FUNCTION" THIS DETECTS THE ON PRESSED, START, TABS AND HOME BUTTONS IN ORDER TO INITIALIZE SECURITY "FAIL-SAFE"
    @Override
    protected void onStart() { // <--- ON *LOGIN REQUIREMENT* START -- *LOGIN = FALSE*
        super.onStart();

        APP_LOGGED_IN = false; // APP NOT LOGGED IN
    }
    @Override
    public void onBackPressed() { // <--- BACK PRESSED *HOME*
        Intent homeIntent = new Intent(Intent.ACTION_MAIN); // (NEXT LINES) SCRIPT TO SEND TO THE OS HOME SCREEN
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------

    // QUARANTINED FUNCTIONS
    private boolean isRegistered() {
        final String passcode = this.mSharedPreferences.getString(PASSWORD, null);
        final String key = this.mSharedPreferences.getString(CRYPT_KEY, null);

        return (passcode != null && key != null);
    }
}