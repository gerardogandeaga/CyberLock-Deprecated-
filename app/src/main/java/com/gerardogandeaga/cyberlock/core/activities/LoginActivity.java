package com.gerardogandeaga.cyberlock.core.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gerardogandeaga.cyberlock.App;
import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.database.DatabaseOpenHelper;
import com.gerardogandeaga.cyberlock.database.objects.Note;
import com.gerardogandeaga.cyberlock.utils.Pref;
import com.gerardogandeaga.cyberlock.custom.CustomToast;

import butterknife.BindView;
import butterknife.ButterKnife;

enum LoginStates {
    LOGIN, REGISTER
}
public class LoginActivity extends SecureActivity {
    private Context mContext = this;
    private LoginStates state;

    private boolean mButtonFlag;

    @BindView(R.id.tvTitle)     TextView mTitle;
    @BindView(R.id.etPassword)  EditText mEtPassword;
    @BindView(R.id.etRegister)  EditText mEtRegister;
    @BindView(R.id.Register)    TextInputLayout mRegister;
    @BindView(R.id.btnEnter)    Button mBtnEnter;
    @BindView(R.id.progressbar) ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(View.inflate(this, R.layout.activity_login, null));
        ButterKnife.bind(this);

        if (isRegistered()) {
            beginLogin();
        } else {
            beginRegister();
        }

        super.onCreate(savedInstanceState);
    }

    private void beginRegister() {
        mProgressBar.setVisibility(View.GONE);
        mEtPassword.getText().clear();
        mEtRegister.getText().clear();
        this.mButtonFlag = true;

        // switch to register state
        this.state = LoginStates.REGISTER;

        mTitle.setText("Register");

        // enter button
        mBtnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mButtonFlag) {
                    mButtonFlag = false;
                    String pass1 = mEtPassword.getText().toString();
                    String pass2 = mEtRegister.getText().toString();

                    // check if the text fields have text
                    if (pass1.isEmpty() || pass2.isEmpty()) {
                        CustomToast.buildAndShowToast(App.getContext(), "Input Fields Cannot Be Empty", CustomToast.ERROR, CustomToast.LENGTH_SHORT);
                        mButtonFlag = true;
                        return;
                    }

                    // check if password match
                    if (!pass1.equals(pass2)) {
                        CustomToast.buildAndShowToast(App.getContext(), "Passwords Do Not Match", CustomToast.ERROR, CustomToast.LENGTH_SHORT);
                        mButtonFlag = true;
                        return;
                    }

                    // register
                    runCredentialTask(pass1);
                }
            }
        });
    }

    private void beginLogin() {
        mProgressBar.setVisibility(View.GONE);
        mEtPassword.getText().clear();
        mEtRegister.getText().clear();
        this.mButtonFlag = true;

        // switch to runCredentialTask state
        this.state = LoginStates.LOGIN;

        mRegister.setVisibility(View.GONE);
        mTitle.setText("Login");

        // enter button
        mBtnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mButtonFlag) {
                    mButtonFlag = false;
                    String pass = mEtPassword.getText().toString();

                    // check if input field is empty
                    if (pass.isEmpty()) {
                        CustomToast.buildAndShowToast(App.getContext(), "No Input Detected", CustomToast.ERROR, CustomToast.LENGTH_SHORT);
                        mEtPassword.getText().clear();
                        mButtonFlag = true;
                        return;
                    }

                    // login
                    runCredentialTask(pass);
                }
            }
        });
    }

    // runCredentialTask process
    @SuppressLint("StaticFieldLeak")
    private void runCredentialTask(final String password) {
        mProgressBar.setVisibility(View.VISIBLE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DatabaseOpenHelper database = App.getDatabase();
                    database.recycle();

                    App.getDatabase().setPassword(password);
                    database.update();

                    if (state == LoginStates.REGISTER) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initialPreferences();
                                // switch to runCredentialTask
                                beginLogin();
                            }
                        });
                        // start login
                        return;
                    }

                    // login into cyber lock
                    setIsAppLoggedIn(true);
                    loginIntent();
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // if incorrect password
                            if (state == LoginStates.LOGIN) {
                                mProgressBar.setVisibility(View.GONE);
                                CustomToast.buildAndShowToast(mContext, "Incorrect Password, Please Try Again", CustomToast.ERROR, CustomToast.LENGTH_SHORT);
                                mButtonFlag = true;
                            }
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
            setSecureIntent(new Intent(this, NoteActivity.class));
        }
        secureIntentGoTo();
    }

    private boolean isRegistered() {
        return getDatabasePath(DatabaseOpenHelper.DATABASE).exists();
    }

    // initial registration preferences and global variables
    private void initialPreferences() {
        Pref.setAutoSave(this, false);
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