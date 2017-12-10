package com.gerardogandeaga.cyberlock.support.settings;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.gerardogandeaga.cyberlock.crypto.CryptKeyHandler;
import com.gerardogandeaga.cyberlock.crypto.SHA256PinHash;

import static com.gerardogandeaga.cyberlock.support.Globals.CRYPT_KEY;
import static com.gerardogandeaga.cyberlock.support.Globals.PASSCODE;
import static com.gerardogandeaga.cyberlock.support.Globals.TEMP_PIN;

public class SettingsChangePasscode {
    private Context mContext;
    private SharedPreferences mSharedPreferences;

    // WIDGETS
    private ProgressDialog mProgressDialog;

    public SettingsChangePasscode(Context c, SharedPreferences sp, String[] p) {
        mContext = c;
        mSharedPreferences = sp;
        onPasscodeCompleted(p);
    }

    private void progressBar() {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("Changing Passcode...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    @SuppressLint("StaticFieldLeak")
    private void onPasscodeCompleted(final String[] passcodes) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar();
            }
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    final String passcode = passcodes[1]; // New passcode
                    final CryptKeyHandler keyHandler = new CryptKeyHandler(mContext); // Handler
                    final String CRYPTKEY = keyHandler.DECRYPT_KEY(mSharedPreferences.getString(CRYPT_KEY, null), passcodes[0]); // Decrypted Key

                    final String pinHash = SHA256PinHash.HASH_FUNCTION(passcodes[1], SHA256PinHash.GENERATE_SALT()); // Hash new passcode
                    final String passcodeHashAndEncrypted = keyHandler.ENCRYPT_KEY(pinHash, passcode); // Encrypt after hashing

                    // Store new values
                    mSharedPreferences.edit()
                            .putString(PASSCODE, passcodeHashAndEncrypted)
                            .putString(CRYPT_KEY, keyHandler.ENCRYPT_KEY(CRYPTKEY, passcode))
                            .apply();

                    TEMP_PIN = passcode;

                } catch (Exception e) {
                    e.printStackTrace();
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