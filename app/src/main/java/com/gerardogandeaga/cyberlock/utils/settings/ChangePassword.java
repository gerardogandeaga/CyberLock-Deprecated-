package com.gerardogandeaga.cyberlock.utils.settings;

import android.content.Context;
import android.content.SharedPreferences;

import com.gerardogandeaga.cyberlock.activities.dialogs.DialogCustomLoad;
import com.gerardogandeaga.cyberlock.crypto.hash.Hash;
import com.gerardogandeaga.cyberlock.crypto.key.CryptKeyHandler;

import static com.gerardogandeaga.cyberlock.utils.Settings.CRYPT_KEY;
import static com.gerardogandeaga.cyberlock.utils.Settings.PASSWORD;
import static com.gerardogandeaga.cyberlock.utils.Settings.TMP_PWD;

public class ChangePassword {
    private Context mContext;
    private SharedPreferences mSharedPreferences;

    // widgets
    private DialogCustomLoad mDialogCustomLoad;

    public ChangePassword(Context context, SharedPreferences sharedPreferences, String currentPassword, String newPassword) {
        this.mContext = context;
        this.mSharedPreferences = sharedPreferences;

        onPasswordComplete(currentPassword, newPassword);
    }

    private void onPasswordComplete(final String currentPassword, final String newPassword) {
        mDialogCustomLoad = new DialogCustomLoad(mContext);
        mDialogCustomLoad.indeterminateProgress("Changing Password");

        new Thread(new Runnable() {
            @Override
            public void run() {
                // save password, re-encrypt key, set tmp_pwd.
                savePassword(currentPassword, newPassword);
                // end load
                mDialogCustomLoad.dismiss();
            }
        }).start();
    }

    private void savePassword(final String currentPassword, final String newPassword) {
        this.mSharedPreferences.edit()
                .putString(PASSWORD, Hash.generateSecurePasscode(mContext, newPassword))
                // decrypt crypt key with old password then
                .putString(CRYPT_KEY, CryptKeyHandler.replaceCryptKeyPassword(mContext, currentPassword, newPassword))
                .apply();
        TMP_PWD = newPassword;
    }
}
