package com.gerardogandeaga.cyberlock.crypto;

import android.content.Context;
import android.util.Base64;

import com.gerardogandeaga.cyberlock.crypto.key.CryptKey;
import com.gerardogandeaga.cyberlock.utils.SharedPreferences;

import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static com.gerardogandeaga.cyberlock.utils.SharedPreferences.TMP_PWD;

/**
 * @author gerardogandeaga
 *
 *
 */
public class BaseCrypt {
    private static int mIvLength;
    private static String mEncryptionAlgorithm;
    private static String mCipherAlgorithm;

    private static String mKey;

    private static void setAlgorithmPresets(Context context) {
        // since key is static it will not change, therefore we do not need to constantly decrypt
        if (mKey == null) {
            mKey = CryptKey.decrypt(context, SharedPreferences.getMasterKey(context), TMP_PWD);
        }
        mEncryptionAlgorithm = SharedPreferences.getEncryptionAlgorithm(context);
        mCipherAlgorithm = mEncryptionAlgorithm + "/CBC/PKCS5Padding";

        switch (mEncryptionAlgorithm) {
            case "AES":      mIvLength = 16; break;
            case "Blowfish": mIvLength = 8;  break;
            default:         mIvLength = 16; break;
        }
    }

    public static String encrypt(Context context, String string) {
        if (string == null || string.isEmpty()) {
            System.out.println("bad encryption!: empty string");
            return string;
        }

        try {
            setAlgorithmPresets(context);

            // random iv generation
            byte[] ivByteVal = generateIV();

            // decode master key to bytes
            byte[] keyByteVal = Base64.decode(mKey, Base64.DEFAULT);
            // generate symmetric key from master key bytes
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyByteVal, mEncryptionAlgorithm);

            // create cipher instance
            Cipher cipher = Cipher.getInstance(mCipherAlgorithm);
            // start cipher with mode : encrypt
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(ivByteVal));

            // decode string to bytes and encrypt
            byte[] encryptedStringByteVal = cipher.doFinal(string.getBytes());

            // create a new byte array of the length of iv + encrypted string
            byte[] combinedByteVal = new byte[ivByteVal.length + encryptedStringByteVal.length];
            System.arraycopy(ivByteVal, 0, combinedByteVal, 0, ivByteVal.length);
            System.arraycopy(encryptedStringByteVal, 0, combinedByteVal, ivByteVal.length, encryptedStringByteVal.length);

            // encode to string with Base64
            return Base64.encodeToString(combinedByteVal, Base64.DEFAULT);

            // catching and handling exceptions
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("bad encryption!: error");
            return string;
        }
    }
    public static String decrypt(Context context, String string) {
        if (string == null || string.isEmpty()) {

            System.out.println("bad decryption!: empty string");
            return string;
        }
        try {
            setAlgorithmPresets(context);

            // decode encrypted string to encrypted bytes
            byte[] encryptedCombinedBytes = Base64.decode(string, Base64.DEFAULT);

            // strip the bytes down, recovering the iv and encrypted bytes
            byte[] ivByteVal = Arrays.copyOfRange(encryptedCombinedBytes, 0, mIvLength); // "Break" bytes to get IV
            byte[] encryptedStringByteVal = Arrays.copyOfRange(encryptedCombinedBytes, mIvLength, encryptedCombinedBytes.length); // "Break" Bytes to get cipher text only

            // decode key to bytes
            byte[] keyByteVal = Base64.decode(mKey, Base64.DEFAULT);
            // generate parallel symmetric key used for encrypting
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyByteVal, mEncryptionAlgorithm);

            // create the cipher instance
            Cipher cipher = Cipher.getInstance(mCipherAlgorithm);
            // start cipher with mode : decrypt
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(ivByteVal)); // START CIPHER AND SET IT TO ENCRYPT MODE

            // decrypt bytes
            byte[] decryptedTextByteVal = cipher.doFinal(encryptedStringByteVal); // DECRYPT TEXT

            // build a new string object
            return new String(decryptedTextByteVal);

            // catching and handling exceptions
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("bad decryption!: error");
            return string;
        }
    }

    // random iv generation
    private static byte[] generateIV() {
        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[mIvLength];
        random.nextBytes(iv);

        return iv;
    }
}
