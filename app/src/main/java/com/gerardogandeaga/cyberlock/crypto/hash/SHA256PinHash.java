package com.gerardogandeaga.cyberlock.crypto.hash;

import android.support.annotation.NonNull;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class SHA256PinHash {
    @NonNull
    public static String hashEncode(String string)
            throws NoSuchAlgorithmException, UnsupportedEncodingException, NegativeArraySizeException {
        byte[] salt = generateSalt();

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] textBytes = string.getBytes("UTF-8");
        digest.reset();
        digest.update(salt);
        digest.update(textBytes, 0, textBytes.length);
        byte[] pinHash = digest.digest();

        byte[] combinedByteVal = new byte[salt.length + pinHash.length]; // COMBINE THE BYTES
        System.arraycopy(salt, 0, combinedByteVal, 0, salt.length);
        System.arraycopy(pinHash, 0, combinedByteVal, salt.length, pinHash.length);

        return Base64.encodeToString(combinedByteVal, Base64.DEFAULT);
    }
    public static String hashEncode(String string, byte[] salt)
            throws NoSuchAlgorithmException, UnsupportedEncodingException, NegativeArraySizeException {

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] textBytes = string.getBytes("UTF-8");
        digest.reset();
        digest.update(salt);
        digest.update(textBytes, 0, textBytes.length);
        byte[] pinHash = digest.digest();

        byte[] combinedByteVal = new byte[salt.length + pinHash.length]; // COMBINE THE BYTES
        System.arraycopy(salt, 0, combinedByteVal, 0, salt.length);
        System.arraycopy(pinHash, 0, combinedByteVal, salt.length, pinHash.length);

        return Base64.encodeToString(combinedByteVal, Base64.DEFAULT);
    }

    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[128];
        random.nextBytes(salt);

        return salt;
    }
}