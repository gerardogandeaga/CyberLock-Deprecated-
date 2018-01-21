package com.gerardogandeaga.cyberlock.crypto;

import android.content.Context;

public class Hash {

    public static String generateSecurePasscode(Context context, String passcode) {
        try {
            // hash the original passcode string
            final String passcodeRawHash = SHA256PinHash.hashEncode(passcode);
            // encrypt the hashed string and return the string
            return CryptKey.encrypt(context, passcodeRawHash, passcode);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
