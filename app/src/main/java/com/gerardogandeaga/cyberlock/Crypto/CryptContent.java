package com.gerardogandeaga.cyberlock.Crypto;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Base64;

import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static com.gerardogandeaga.cyberlock.Supports.Globals.DIRECTORY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.ENCRYPTION_ALGO;
import static com.gerardogandeaga.cyberlock.Supports.Globals.FLAGS;

public class CryptContent
{
    private SharedPreferences mSharedPreferences;

    private String ALGO, CipherALGO;
    private int IVLength;

    public CryptContent(Context context)
    {
        mSharedPreferences = context.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE);
        ALGO = mSharedPreferences.getString(ENCRYPTION_ALGO, "AES");
        CipherALGO = ALGO + "/CBC/PKCS5Padding";

//        System.out.println("CREATE ALGO " + ALGO);
        switch (ALGO)
        {
            case "AES":
                IVLength = 16;
                break;
            case "Blowfish":
                IVLength = 8;
                break;
        }
    }

    @Nullable
    public String ENCRYPT_KEY(String dataToEncrypt, String symmetricKey)
//            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidParameterSpecException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException
    {
        if (dataToEncrypt == null || dataToEncrypt.matches("")) {
            return null;
        }

        try
        {
            if (!dataToEncrypt.matches("")) // CHECK IS THERE IS ACTULA CONTENT TO ENCRYPT
            {
//                System.out.println("ENCRYPT_KEY: ENCRYPT STARTING...");
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
                return Base64.encodeToString(combinedByteVal, FLAGS);
            } else
            {
                return ""; // RETURN NOTHING
            }
        } catch (Exception e)
        {
//            System.out.println("ENCRYPT_KEY: ENCRYPT FAILED!...");
            e.printStackTrace();

            return dataToEncrypt;
        }
    }

    @Nullable
    public String DECRYPT_CONTENT(String dataToDecrypt, String symmetricKey)
//            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException
    {
        if (dataToDecrypt == null || dataToDecrypt.matches("")) {
            return null;
        }

        try
        {
//            System.out.println("ENCRYPTION ALGO " + ALGO);
//            System.out.println("DECRYPT_CONTENT: DECRYPT STARTING...");
            byte[] encryptedCombinedBytes = Base64.decode(dataToDecrypt, FLAGS);

            byte[] ivByteVal = Arrays.copyOfRange(encryptedCombinedBytes, 0, IVLength); // "BREAK" BYTES TO GET IV BYTES ONLY
            byte[] encryptedTextByteVal = Arrays.copyOfRange(encryptedCombinedBytes, IVLength, encryptedCombinedBytes.length); // "BREAK" BYTES TO GET CIPHER TEXT BYTES ONLY

            byte[] encryptedPassword = Base64.decode(symmetricKey, FLAGS);

            SecretKeySpec secretKeySpec = new SecretKeySpec(encryptedPassword, ALGO);
            Cipher cipher = Cipher.getInstance(CipherALGO);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(ivByteVal)); // START CIPHER AND SET IT TO ENCYPT MODE

            byte[] decryptedTextByteVal = cipher.doFinal(encryptedTextByteVal); // DECRYPT TEXT

//            System.out.println("DECRYPT_CONTENT: DECRYPT DONE...");
            return new String(decryptedTextByteVal);
        } catch (Exception e)
        {
//            System.out.println("DECRYPT_CONTENT: DECRYPT FAILED...");
            e.printStackTrace();

            return dataToDecrypt;
        }
    }
}
