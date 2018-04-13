package com.gerardogandeaga.cyberlock.crypto;

import android.content.Context;

import java.io.UnsupportedEncodingException;

/**
 * @author gerardogandeaga
 *
 * this class is meant to only encrypt and decrypt SQLite database content, only classes that are able
 * to accessor this class is the databaseAccess class and Note class.
 */
public class DBCrypt {

    // todo idea - implement compression and decompression
    public static byte[] encrypt(Context context, String string) throws UnsupportedEncodingException {
        String encrypted = BaseCrypt.encrypt(context, string);
        return encrypted.getBytes("UTF-8");
    }

    public static String decrypt(Context context, byte[] bytes) throws UnsupportedEncodingException {
        return BaseCrypt.decrypt(context, new String(bytes, "UTF-8"));
    }
}