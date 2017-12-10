package com.gerardogandeaga.cyberlock.activities.clearances;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gerardogandeaga.cyberlock.crypto.CryptKeyHandler;
import com.gerardogandeaga.cyberlock.crypto.SHA256PinHash;
import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.support.Globals;

import static com.gerardogandeaga.cyberlock.support.Globals.AUTOSAVE;
import static com.gerardogandeaga.cyberlock.support.Globals.DIRECTORY;
import static com.gerardogandeaga.cyberlock.support.Globals.ENCRYPTION_ALGO;
import static com.gerardogandeaga.cyberlock.support.Globals.IS_REGISTERED;
import static com.gerardogandeaga.cyberlock.support.Globals.PASSCODE;
import static com.gerardogandeaga.cyberlock.support.LogoutProtocol.ACTIVITY_INTENT;

public class RegistrationActivity extends AppCompatActivity {
    private Context mContext = this;
    private SharedPreferences mSharedPreferences;

    // RawData variables
    private String[] mPasscodes = new String[2];

    // WIDGETS
    private EditText mEtInitial, mEtFinal;
    private ProgressDialog mProgressDialog;

    // Initial create methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Globals.COLORSCHEME(this);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        setupLayout();
    }
    private void setupLayout() {
        setContentView(R.layout.activity_register);
        ACTIVITY_INTENT = null;
        mSharedPreferences = getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE);
        boolean isRegistered = mSharedPreferences.getBoolean(IS_REGISTERED, false);

        if (isRegistered) {
            ACTIVITY_INTENT = new Intent(this, LoginActivity.class);
            this.finish();
            this.startActivity(ACTIVITY_INTENT);
        } else {
            mEtInitial = (EditText) findViewById(R.id.etInitial);
            mEtFinal = (EditText) findViewById(R.id.etFinal);
            Button btnRegister = (Button) findViewById(R.id.btnRegister);

            btnRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPasscodes[0] = mEtInitial.getText().toString();
                    mPasscodes[1] = mEtFinal.getText().toString();

                    if (!mPasscodes[0].matches("") && !mPasscodes[1].matches("")) {
                        if (mPasscodes[0].matches(mPasscodes[1])) {
                            onPasscodeCompleted();
                        } else {
                            clear();
                            Toast.makeText(mContext, "Passcodes do not match", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        clear();
                        Toast.makeText(mContext, "One or more fields are missing", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    private void progressBar() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Registering...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }
    // -------------------------

    // PASSCODE REGISTRATION
    public void clear() {
        mPasscodes = new String[2];
        mEtInitial.getText().clear();
        mEtFinal.getText().clear();
    }
    @SuppressLint("StaticFieldLeak") private void onPasscodeCompleted() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressBar();
            }
            @Override
            protected Void doInBackground(Void... params) {

                final String passcode = mPasscodes[0];
                try {
                    mSharedPreferences.edit().putString(ENCRYPTION_ALGO, "AES").apply();

                    CryptKeyHandler keyHandler = new CryptKeyHandler(mContext); // START THE KEY HANDLER

                    // PASSCODE AND ENCRYPTION PROCESSES
                    final String pinHash = SHA256PinHash.HASH_FUNCTION(passcode, SHA256PinHash.GENERATE_SALT());
                    final String passcodeHashAndEncrypted = keyHandler.ENCRYPT_KEY(pinHash, passcode);

                    mSharedPreferences.edit().putString(PASSCODE, passcodeHashAndEncrypted).apply(); // ADD HASHED passcode TO STORE
                    System.out.println("HASHED passcode :" + passcodeHashAndEncrypted);

                    String keyStringVal = keyHandler.GENERATE_NEW_KEY(passcode); // GENERATE A NEW BYTE ARRAY AS A SYMMETRIC KEY
                    keyStringVal = null; // TODO IMPROVE GENERATION VS GETTER

                    // INITIAL SETTINGS
                    mSharedPreferences.edit().putBoolean(AUTOSAVE, false).apply();

                    Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
                    RegistrationActivity.this.startActivity(i);

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "Something Went Wrong...", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                mProgressDialog.dismiss();
            }
        }.execute();
    }
    // ----------------
}