package com.gerardogandeaga.cyberlock.Encryption;

import android.support.annotation.NonNull;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import static com.gerardogandeaga.cyberlock.Supports.Globals.FLAGS;

public class SHA256PinHash
{
    @NonNull
    public static String HASH_FUNCTION(String text, byte[] salt)
            throws NoSuchAlgorithmException, UnsupportedEncodingException, NegativeArraySizeException
    {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] textBytes = text.getBytes("UTF-8");
        digest.reset();
        digest.update(salt);
        digest.update(textBytes, 0, textBytes.length);
        byte[] pinHash = digest.digest();

        byte[] combinedByteVal = new byte[salt.length + pinHash.length]; // COMBINE THE BYTES
        System.arraycopy(salt, 0, combinedByteVal, 0, salt.length);
        System.arraycopy(pinHash, 0, combinedByteVal, salt.length, pinHash.length);

        return Base64.encodeToString(combinedByteVal, FLAGS);
    }

    public static byte[] GENERATE_SALT()
    {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[128];
        random.nextBytes(salt);

        return salt;
    }

    // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    @NonNull
    @Deprecated
    public static String HASH_FUNCTION(String text)
            throws NoSuchAlgorithmException, UnsupportedEncodingException, NegativeArraySizeException // <-------------- DEPRECATED HASH FUNCTION WITHOUT SALT
    {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] textBytes = text.getBytes("iso-8859-1");
        md.update(textBytes, 0, textBytes.length);
        byte[] sha1hash = md.digest();
        return CONVERT_TO_HEX(sha1hash);
    }
    @NonNull
    private static String CONVERT_TO_HEX(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data)
        {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do
            {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }
    // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
}