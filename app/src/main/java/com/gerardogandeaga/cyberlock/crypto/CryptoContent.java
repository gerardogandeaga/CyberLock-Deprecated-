package com.gerardogandeaga.cyberlock.crypto;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.widget.Toast;

import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static com.gerardogandeaga.cyberlock.support.Globals.CRYPT_ALGO;
import static com.gerardogandeaga.cyberlock.support.Globals.DIRECTORY;
import static com.gerardogandeaga.cyberlock.support.Globals.FLAGS;

public class CryptoContent {
    private Context mContext;

    private int IVLength;
    private String CryptAlgorithm;
    private String CipherAlgorithm;
    private final String CipherBody = "/CBC/PKCS5Padding";

    public CryptoContent(Context context) {
        this.mContext = context;
    }

    public String encrypt(String string, String key) {
        if (string == null || string.isEmpty()) {
            System.out.println("String is empty!");
            return "";
        }
        try {
            getAlgorithmPresets();

            byte[] encryptedKeyByteVal = Base64.decode(key, FLAGS); // Returns null pointer when there is a null MASTER KEY

            SecretKeySpec secretKeySpec = new SecretKeySpec(encryptedKeyByteVal, CryptAlgorithm);
            Cipher cipher = Cipher.getInstance(CipherAlgorithm);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec); // START CIPHER AND SET IT TO ENCRYPT MODE

            byte[] ivByteVal = Arrays.copyOfRange(cipher.getIV(), 0, IVLength); // GET IV OF CIPHER -> LENGTH DICTATED BY ALGORITHM

            byte[] encryptedTextByteVal = cipher.doFinal(string.getBytes()); // ENCRYPT TEXT

            byte[] combinedByteVal = new byte[ivByteVal.length + encryptedTextByteVal.length]; // COMBINE THE BYTES
            System.arraycopy(ivByteVal, 0, combinedByteVal, 0, ivByteVal.length); // POSITIONING THE IV BYTES
            System.arraycopy(encryptedTextByteVal, 0, combinedByteVal, ivByteVal.length, encryptedTextByteVal.length); // IMPLEMENTING INTO CIPHER

            return Base64.encodeToString(combinedByteVal, FLAGS);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, "Something went wrong with encrypting", Toast.LENGTH_SHORT).show();
            return "";
        }
    }

    public String decrypt(String string, String key) {
        if (string == null || string.isEmpty()) {
            System.out.println("String is empty!");
            return "";
        }
        try {
            getAlgorithmPresets();

            byte[] encryptedCombinedBytes = Base64.decode(string, FLAGS);

            byte[] ivByteVal = Arrays.copyOfRange(encryptedCombinedBytes, 0, IVLength); // "Break" bytes to get IV
            byte[] encryptedTextByteVal = Arrays.copyOfRange(encryptedCombinedBytes, IVLength, encryptedCombinedBytes.length); // "Break" Bytes to get cipher text only

            byte[] encryptedPassword = Base64.decode(key, FLAGS);

            SecretKeySpec secretKeySpec = new SecretKeySpec(encryptedPassword, CryptAlgorithm);
            Cipher cipher = Cipher.getInstance(CipherAlgorithm);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(ivByteVal)); // START CIPHER AND SET IT TO ENCRYPT MODE

            byte[] decryptedTextByteVal = cipher.doFinal(encryptedTextByteVal); // DECRYPT TEXT

            return new String(decryptedTextByteVal);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, "Something went wrong with decrypting", Toast.LENGTH_SHORT).show();
            return "";
        }
    }

    private void getAlgorithmPresets() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE);
        CryptAlgorithm = sharedPreferences.getString(CRYPT_ALGO, "AES");
        CipherAlgorithm = CryptAlgorithm + CipherBody;

        switch (CryptAlgorithm) {
            case "AES":      IVLength = 16; break;
            case "Blowfish": IVLength = 8; break;
        }
    }
}