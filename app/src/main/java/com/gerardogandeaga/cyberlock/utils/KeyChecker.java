package com.gerardogandeaga.cyberlock.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.gerardogandeaga.cyberlock.crypto.key.CryptKey;
import com.gerardogandeaga.cyberlock.crypto.hash.SHA256PinHash;

import java.util.Arrays;

import static com.gerardogandeaga.cyberlock.utils.Stored.DIRECTORY;
import static com.gerardogandeaga.cyberlock.utils.Stored.FLAGS;
import static com.gerardogandeaga.cyberlock.utils.Stored.PASSWORD;


public class KeyChecker {

    public static boolean comparePasswords(Context context, String password) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE);
        try {
            final String decryptedPulledPin = CryptKey.decrypt(context, sharedPreferences.getString(PASSWORD, null), password);
            final String loginPinHash = SHA256PinHash.hashEncode(password, Arrays.copyOfRange(Base64.decode(decryptedPulledPin, FLAGS), 0, 128));

            return loginPinHash.equals(decryptedPulledPin);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
