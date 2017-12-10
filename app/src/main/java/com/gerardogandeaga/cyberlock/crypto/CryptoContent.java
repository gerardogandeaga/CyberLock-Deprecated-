package com.gerardogandeaga.cyberlock.crypto;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Base64;

import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static com.gerardogandeaga.cyberlock.support.Globals.DIRECTORY;
import static com.gerardogandeaga.cyberlock.support.Globals.ENCRYPTION_ALGO;
import static com.gerardogandeaga.cyberlock.support.Globals.FLAGS;

public class CryptoContent {

    private String ALGO, CipherALGO;
    private int IVLength;

    public CryptoContent(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE);
        ALGO = sharedPreferences.getString(ENCRYPTION_ALGO, "AES");
        CipherALGO = ALGO + "/CBC/PKCS5Padding";

//        System.out.println("CREATE ALGO " + ALGO);
        switch (ALGO) {
            case "AES":
                IVLength = 16;
                break;
            case "Blowfish":
                IVLength = 8;
                break;
        }
    }

    @Nullable
    public String ENCRYPT_CONTENT(String dataToEncrypt, String symmetricKey) {
        if (dataToEncrypt == null || dataToEncrypt.isEmpty()) {
            return null;
        }

        try {
//                System.out.println("\n" + "ORIGINAL INPUT: " + dataToEncrypt);
                byte[] encryptedPassword = Base64.decode(symmetricKey, FLAGS); // GENERATE KEY

                SecretKeySpec secretKeySpec = new SecretKeySpec(encryptedPassword, ALGO);
                Cipher cipher = Cipher.getInstance(CipherALGO);
                cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec); // START CIPHER AND SET IT TO ENCRYPT MODE

                byte[] ivByteVal = Arrays.copyOfRange(cipher.getIV(), 0, IVLength); // GET IV OF CIPHER -> LENGTH DICTATED BY ALGORITHM

                byte[] encryptedTextByteVal = cipher.doFinal(dataToEncrypt.getBytes()); // ENCRYPT TEXT

                byte[] combinedByteVal = new byte[ivByteVal.length + encryptedTextByteVal.length]; // COMBINE THE BYTES
                System.arraycopy(ivByteVal, 0, combinedByteVal, 0, ivByteVal.length); // POSITIONING THE IV BYTES
                System.arraycopy(encryptedTextByteVal, 0, combinedByteVal, ivByteVal.length, encryptedTextByteVal.length); // IMPLEMENTING INTO CIPHER

//                System.out.println("ENCRYPT_KEY: ENCRYPT DONE...");
//                System.out.println("ENCRYPTED OUTPUT: " + Base64.encodeToString(combinedByteVal, FLAGS));
                return Base64.encodeToString(combinedByteVal, FLAGS);
        } catch (Exception e) {
//            System.out.println("ENCRYPT_KEY: ENCRYPT FAILED!...");
            e.printStackTrace();

            return dataToEncrypt;
        }
    }
    @Nullable
    public String DECRYPT_CONTENT(String dataToDecrypt, String symmetricKey) {
        if (dataToDecrypt == null || dataToDecrypt.isEmpty()) {
            System.out.println("Null return!");
            return null;
        }

        try {
//            System.out.println("\n" + "ENCRYPTED INPUT: " + dataToDecrypt);
//            System.out.println("ENCRYPTION ALGO " + ALGO);
            byte[] encryptedCombinedBytes = Base64.decode(dataToDecrypt, FLAGS);

            byte[] ivByteVal = Arrays.copyOfRange(encryptedCombinedBytes, 0, IVLength); // "BREAK" BYTES TO GET IV BYTES ONLY
            byte[] encryptedTextByteVal = Arrays.copyOfRange(encryptedCombinedBytes, IVLength, encryptedCombinedBytes.length); // "BREAK" BYTES TO GET CIPHER TEXT BYTES ONLY

            byte[] encryptedPassword = Base64.decode(symmetricKey, FLAGS);

            SecretKeySpec secretKeySpec = new SecretKeySpec(encryptedPassword, ALGO);
            Cipher cipher = Cipher.getInstance(CipherALGO);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(ivByteVal)); // START CIPHER AND SET IT TO ENCYPT MODE

            byte[] decryptedTextByteVal = cipher.doFinal(encryptedTextByteVal); // DECRYPT TEXT

//            System.out.println("DECRYPTED OUTPUT: " + new String(decryptedTextByteVal));
            return new String(decryptedTextByteVal);
        } catch (Exception e) {
//            System.out.println("DECRYPT_CONTENT: DECRYPT FAILED...");
            e.printStackTrace();

            return dataToDecrypt;
        }
    }
}