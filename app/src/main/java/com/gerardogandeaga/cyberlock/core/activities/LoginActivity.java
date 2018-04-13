package com.gerardogandeaga.cyberlock.core.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.core.dialogs.LoadDialog;
import com.gerardogandeaga.cyberlock.database.objects.Note;
import com.gerardogandeaga.cyberlock.utils.security.KeyChecker;
import com.gerardogandeaga.cyberlock.views.CustomToast;

import static com.gerardogandeaga.cyberlock.utils.PreferencesAccessor.CRYPT_KEY;
import static com.gerardogandeaga.cyberlock.utils.PreferencesAccessor.DIRECTORY;
import static com.gerardogandeaga.cyberlock.utils.PreferencesAccessor.PASSWORD;
import static com.gerardogandeaga.cyberlock.utils.PreferencesAccessor.TMP_PWD;

/**
 * @author gerardogandeaga
 */
public class LoginActivity extends SecureActivity {
    private Context mContext = this;
    private SharedPreferences mSharedPreferences;

    // widgets
    private LoadDialog mLoadDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.mSharedPreferences = getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE);
        super.onCreate(savedInstanceState);

        // check is user is already registered
        if (!isRegistered()) {
            secureIntentGoTo(new Intent(this, RegisterActivity.class));
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
        this.mLoadDialog = new LoadDialog(mContext);
        mLoadDialog.indeterminateProgress("Verifying Password, Please Wait.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (KeyChecker.comparePasswords(mContext, password)) { // check if original passcode matches the one entered
                    setGlobalVariables(password);
                    loginIntent();
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
    /**
    this function tries to pull the last edit data package that was opened and being edited
    immediately before the system auto logged out
    */
    private void loginIntent() {
        Bundle bundle = getIntent().getExtras();

        // if bundle is not null and last activity was edit then pull the last data package
        if (bundle != null && bundle.getBoolean("edit?", false)) {
            setSecureIntent(new Intent(this, NoteEditActivity.class));

            // pull isNew boolean from previous intent and place into new intent to set edit state
            getSecureIntent().putExtra("isNew?", (boolean) bundle.get("isNew?"));
            // pull the data package from previous intent and place into new intent to resume edit
            getSecureIntent().putExtra("data", (Note) bundle.get("lastDB"));

            // clean up by removing extras
            getSecureIntent().removeExtra("edit?");
            getSecureIntent().removeExtra("lastDB");
        } else {
            // empty intent to the main activity
            setSecureIntent(new Intent(this, NoteListActivity.class));
        }
        mLoadDialog.dismiss();
        secureIntentGoTo();
    }

    // set global variables necessary for the application to function properly
    private void setGlobalVariables(String password) {
        TMP_PWD = password;
        setIsAppLoggedIn(true);
    }
    private boolean isRegistered() {
        final String password = this.mSharedPreferences.getString(PASSWORD, null);
        final String key = this.mSharedPreferences.getString(CRYPT_KEY, null);

        return (password != null && key != null);
    }

    @Override
    protected void onStart() {
        setIsAppLoggedIn(false);
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        setIntent(new Intent(Intent.ACTION_MAIN));
        getSecureIntent().addCategory(Intent.CATEGORY_HOME);
        getSecureIntent().setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        secureIntentGoTo();
    }
}