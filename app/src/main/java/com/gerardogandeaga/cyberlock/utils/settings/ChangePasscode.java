package com.gerardogandeaga.cyberlock.utils.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.gerardogandeaga.cyberlock.crypto.key.CryptKeyHandler;
import com.gerardogandeaga.cyberlock.crypto.hash.Hash;
import com.gerardogandeaga.cyberlock.activities.dialogs.DialogCustomLoad;

import static com.gerardogandeaga.cyberlock.utils.Stored.CRYPT_KEY;
import static com.gerardogandeaga.cyberlock.utils.Stored.PASSWORD;
import static com.gerardogandeaga.cyberlock.utils.Stored.TMP_PWD;

public class ChangePasscode {
    private Context mContext;
    private SharedPreferences mSharedPreferences;

    // widgets
    private DialogCustomLoad mDialogCustomLoad;

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
                mDialogCustomLoad = new DialogCustomLoad(mContext);
                mDialogCustomLoad.indeterminateProgress("Changing Passcode");
            }

            @Override
            protected Void doInBackground(Void... voids) {
                // save password, re-encrypt key, set tmp_pwd.
                savePassword(passwords);

                // end load
                mDialogCustomLoad.dismiss();
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