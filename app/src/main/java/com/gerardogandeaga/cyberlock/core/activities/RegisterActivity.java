package com.gerardogandeaga.cyberlock.core.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.core.dialogs.LoadDialog;
import com.gerardogandeaga.cyberlock.views.CustomToast;
import com.gerardogandeaga.cyberlock.crypto.hash.Hash;
import com.gerardogandeaga.cyberlock.crypto.key.CryptKey;
import com.gerardogandeaga.cyberlock.utils.PreferencesAccessor;

import static com.gerardogandeaga.cyberlock.utils.PreferencesAccessor.AUTOSAVE;
import static com.gerardogandeaga.cyberlock.utils.PreferencesAccessor.CRYPT_KEY;
import static com.gerardogandeaga.cyberlock.utils.PreferencesAccessor.DIRECTORY;
import static com.gerardogandeaga.cyberlock.utils.PreferencesAccessor.ENCRYPTION_ALGORITHM;
import static com.gerardogandeaga.cyberlock.utils.PreferencesAccessor.PASSWORD;
import static com.gerardogandeaga.cyberlock.utils.PreferencesAccessor.THEME;
import static com.gerardogandeaga.cyberlock.utils.security.LogoutProtocol.ACTIVITY_INTENT;

/**
 * @author gerardogandeaga
 */
public class RegisterActivity extends AppCompatActivity {
    private Context mContext = this;
    private android.content.SharedPreferences mSharedPreferences;

    // widgets
    private LoadDialog mLoadDialog;

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
        if (isRegistered()) {
            ACTIVITY_INTENT = new Intent(this, LoginActivity.class);
            this.finish();
            this.startActivity(ACTIVITY_INTENT);
        } else {
            setContentView(View.inflate(this, R.layout.activity_register, null));
            final EditText tvInput1 = findViewById(R.id.etInitial);
            final EditText tvInput2 = findViewById(R.id.etFinal);
            final Button btnRegister = findViewById(R.id.btnRegister);

            // register button listener
            btnRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String[] passwords = new String[2];
                    passwords[0] = tvInput1.getText().toString();
                    passwords[1] = tvInput2.getText().toString();
                    tvInput1.getText().clear();
                    tvInput2.getText().clear();

                    if (!passwords[0].isEmpty() && !passwords[1].isEmpty()) {
                        if (passwords[0].equals(passwords[1])) {
                            register(passwords);
                        } else {
                            CustomToast.buildAndShowToast(mContext, "Passwords Do Not Match", CustomToast.INFORMATION, CustomToast.LENGTH_SHORT);
                        }
                    } else {
                        CustomToast.buildAndShowToast(mContext, "On Or More Fields Are Missing", CustomToast.INFORMATION, CustomToast.LENGTH_SHORT);
                    }
                }
            });
        }
    }

    // registration process
    private void register(final String[] passwords) {
        this.mLoadDialog = new LoadDialog(mContext);
        mLoadDialog.indeterminateProgress("Registering...");

        new Thread(new Runnable() {
            @Override
            public void run() {
                // do initial processes suc saving data and setting global variables
                initialPreferences(passwords);

                // end load
                mLoadDialog.dismiss();

                // start a new intent and exit
                RegisterActivity.this.startActivity(
                        new Intent(RegisterActivity.this, LoginActivity.class));
            }
        }).start();
    }
    // initial preferences and global variables
    private void initialPreferences(final String[] passwords) {

        final String password = passwords[0];
        this.mSharedPreferences.edit()
                // security
                .putString(PASSWORD, Hash.generateSecurePasscode(this, password))
                .putString(CRYPT_KEY, CryptKey.generateNewMasterEncryptionKey(mContext, password))
                .putString(ENCRYPTION_ALGORITHM, "AES")
                // settings
                .putBoolean(AUTOSAVE, false)
                .putString(THEME, "THEME_LIGHT")
                .apply();
    }

    private boolean isRegistered() {
        final boolean isRegistered = PreferencesAccessor.getIsRegistered(this);
        final String password = PreferencesAccessor.getPassword(this);
        final String key = PreferencesAccessor.getMasterKey(this);

        return (isRegistered && password != null && key != null);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (isRegistered()) {
            ACTIVITY_INTENT = new Intent(this, LoginActivity.class);
            this.finish();
            this.startActivity(ACTIVITY_INTENT);
        }
    }
}