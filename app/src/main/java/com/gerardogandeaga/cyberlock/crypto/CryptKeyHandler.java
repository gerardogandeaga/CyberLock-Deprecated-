package com.gerardogandeaga.cyberlock.crypto;

import android.content.Context;

import static com.gerardogandeaga.cyberlock.support.Stored.CRYPT_KEY;
import static com.gerardogandeaga.cyberlock.support.Stored.DIRECTORY;

public class CryptKeyHandler {

    // decrypts key with old password and encrypt it with the new one
    public static String replaceCryptKeyPassword(Context context, String currentPassword, String newPassword) {
        String key = context.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getString(CRYPT_KEY, null);
        key = CryptKey.decrypt(context, key, currentPassword);
        key = CryptKey.encrypt(context, key, newPassword);

        // return key with new password encryption
        return key;
    }
}
