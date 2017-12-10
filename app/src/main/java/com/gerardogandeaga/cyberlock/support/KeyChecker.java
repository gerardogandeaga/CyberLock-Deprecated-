package com.gerardogandeaga.cyberlock.support;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.gerardogandeaga.cyberlock.crypto.CryptKeyHandler;
import com.gerardogandeaga.cyberlock.crypto.SHA256PinHash;

import java.util.Arrays;

import static com.gerardogandeaga.cyberlock.support.Globals.FLAGS;
import static com.gerardogandeaga.cyberlock.support.Globals.PASSCODE;

public class  KeyChecker {
    private Context mContext;
    private SharedPreferences mSharedPreferences;

    public KeyChecker(Context context, SharedPreferences sharedPreferences) {
        mContext = context;
        mSharedPreferences = sharedPreferences;
    }

    public boolean keyCompare(String x) {
        try {
            final String decryptedPulledPin = new CryptKeyHandler(mContext)
                    .DECRYPT_KEY(mSharedPreferences.getString(PASSCODE, null), x);
            final String loginPinHash = SHA256PinHash
                    .HASH_FUNCTION(x, Arrays.copyOfRange(Base64.decode(decryptedPulledPin, FLAGS), 0, 128));

            return loginPinHash.equals(decryptedPulledPin);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
