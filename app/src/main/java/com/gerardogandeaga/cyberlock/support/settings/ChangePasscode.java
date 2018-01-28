package com.gerardogandeaga.cyberlock.support.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.gerardogandeaga.cyberlock.crypto.CryptKeyHandler;
import com.gerardogandeaga.cyberlock.crypto.Hash;
import com.gerardogandeaga.cyberlock.support.graphics.CustomLoadDialog;

import static com.gerardogandeaga.cyberlock.support.Stored.CRYPT_KEY;
import static com.gerardogandeaga.cyberlock.support.Stored.PASSWORD;
import static com.gerardogandeaga.cyberlock.support.Stored.TMP_PWD;

public class ChangePasscode {
    private Context mContext;
    private SharedPreferences mSharedPreferences;

    // widgets
    private CustomLoadDialog mCustomLoadDialog;

    public ChangePasscode(Context context, SharedPreferences sharedPreferences, String[] passwords) {
        this.mContext = context;
        this.mSharedPreferences = sharedPreferences;
        onPasscodeCompleted(passwords);
    }

    @SuppressLint("StaticFieldLeak")
    private void onPasscodeCompleted(final String[] passwords) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mCustomLoadDialog = new CustomLoadDialog(mContext);
                mCustomLoadDialog.indeterminateProgress("Changing Passcode");
            }

            @Override
            protected Void doInBackground(Void... voids) {
                // save password, re-encrypt key, set tmp_pwd.
                savePassword(passwords);

                // end load
                mCustomLoadDialog.dismiss();
                return null;
            }
        }.execute();
    }

    private void savePassword(String[] passwords) {
        this.mSharedPreferences.edit()
                .putString(PASSWORD, Hash.generateSecurePasscode(mContext, passwords[1]))
                // decrypt crypt key with old password then
                .putString(CRYPT_KEY, CryptKeyHandler.replaceCryptKeyPassword(mContext, passwords[0], passwords[1]))
                .apply();
        TMP_PWD = passwords[1];
    }
}