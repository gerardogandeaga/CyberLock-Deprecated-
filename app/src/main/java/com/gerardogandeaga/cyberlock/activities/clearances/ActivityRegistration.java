package com.gerardogandeaga.cyberlock.activities.clearances;

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
import com.gerardogandeaga.cyberlock.activities.dialogs.DialogCustomLoad;
import com.gerardogandeaga.cyberlock.android.CustomToast;
import com.gerardogandeaga.cyberlock.crypto.hash.Hash;
import com.gerardogandeaga.cyberlock.crypto.key.CryptKey;
import com.gerardogandeaga.cyberlock.utils.Settings;

import static com.gerardogandeaga.cyberlock.utils.Settings.AUTOSAVE;
import static com.gerardogandeaga.cyberlock.utils.Settings.CRYPT_KEY;
import static com.gerardogandeaga.cyberlock.utils.Settings.DIRECTORY;
import static com.gerardogandeaga.cyberlock.utils.Settings.ENCRYPTION_ALGORITHM;
import static com.gerardogandeaga.cyberlock.utils.Settings.PASSWORD;
import static com.gerardogandeaga.cyberlock.utils.Settings.THEME;
import static com.gerardogandeaga.cyberlock.utils.security.LogoutProtocol.ACTIVITY_INTENT;

public class ActivityRegistration extends AppCompatActivity {
    private Context mContext = this;
    private SharedPreferences mSharedPreferences;

    // widgets
    private DialogCustomLoad mDialogCustomLoad;

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
            ACTIVITY_INTENT = new Intent(this, ActivityLogin.class);
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
        this.mDialogCustomLoad = new DialogCustomLoad(mContext);
        mDialogCustomLoad.indeterminateProgress("Registering...");

        new Thread(new Runnable() {
            @Override
            public void run() {
                // do initial processes suc saving data and setting global variables
                initialPreferences(passwords);

                // end load
                mDialogCustomLoad.dismiss();

                // start a new intent and exit
                ActivityRegistration.this.startActivity(
                        new Intent(ActivityRegistration.this, ActivityLogin.class));
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
        final boolean isRegistered = Settings.getIsRegistered(this);
        final String password = Settings.getPassword(this);
        final String key = Settings.getMasterKey(this);

        return (isRegistered && password != null && key != null);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (isRegistered()) {
            ACTIVITY_INTENT = new Intent(this, ActivityLogin.class);
            this.finish();
            this.startActivity(ACTIVITY_INTENT);
        }
    }
}