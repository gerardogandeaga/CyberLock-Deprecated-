package com.gerardogandeaga.cyberlock.activities.clearances;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.crypto.CryptKey;
import com.gerardogandeaga.cyberlock.crypto.Hash;
import com.gerardogandeaga.cyberlock.support.Stored;
import com.gerardogandeaga.cyberlock.support.graphics.CustomLoadDialog;

import static com.gerardogandeaga.cyberlock.support.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.support.Stored.AUTOSAVE;
import static com.gerardogandeaga.cyberlock.support.Stored.CRYPT_KEY;
import static com.gerardogandeaga.cyberlock.support.Stored.DIRECTORY;
import static com.gerardogandeaga.cyberlock.support.Stored.ENCRYPTION_ALGORITHM;
import static com.gerardogandeaga.cyberlock.support.Stored.PASSWORD;
import static com.gerardogandeaga.cyberlock.support.Stored.THEME;

public class ActivityRegistration extends AppCompatActivity {
    private Context mContext = this;
    private SharedPreferences mSharedPreferences;

    // widgets
    private CustomLoadDialog mCustomLoadDialog;

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
                            Toast.makeText(mContext, "Passwords do not match", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mContext, "One or more fields are missing", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    // registration process
    @SuppressLint("StaticFieldLeak")
    private void register(final String[] passwords) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mCustomLoadDialog = new CustomLoadDialog(mContext);
                mCustomLoadDialog.indeterminateProgress("Registering...");
            }

            @Override
            protected Void doInBackground(Void... params) {
                // do initial processes suc saving data and setting global variables
                initialPreferences(passwords);

                // end load
                mCustomLoadDialog.dismiss();

                // start a new intent and exit
                ActivityRegistration.this.startActivity(
                        new Intent(ActivityRegistration.this, ActivityLogin.class));

                return null;
            }
        }.execute();
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
        final boolean isRegistered = Stored.getIsRegistered(this);
        final String password = Stored.getPassword(this);
        final String key = Stored.getMasterKey(this);

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