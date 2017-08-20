package com.gerardogandeaga.cyberlock.Encryption;

import android.support.annotation.Nullable;
import android.util.Base64;

import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESContent
{
    private static final int flags = Base64.DEFAULT;

    private static final String ALGO = "AES";
    private static final String CipherALGO = "AES/CBC/PKCS5Padding";

    @Nullable
    public static String encryptContent(String dataToEncrypt, String symmetricKey)
//            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidParameterSpecException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException
    {
        try
        {
            if (!dataToEncrypt.matches("")) // CHECK IS THERE IS ACTULA CONTENT TO ENCRYPT
            {
                System.out.println("encryptContent: ENCRYPT STARTING...");
                byte[] encryptedPassword = Base64.decode(symmetricKey, flags); // GENERATE KEY

                SecretKeySpec secretKeySpec = new SecretKeySpec(encryptedPassword, ALGO);
                Cipher cipher = Cipher.getInstance(CipherALGO);
                cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec); // START CIPHER AND SET IT TO ENCRYPT MODE

                byte[] ivByteVal = cipher.getIV(); // GET IV OF CIPHER
                byte[] encryptedTextByteVal = cipher.doFinal(dataToEncrypt.getBytes()); // ENCRYPT TEXT

                byte[] combinedByteVal = new byte[ivByteVal.length + encryptedTextByteVal.length]; // COMBINE THE BYTES
                System.arraycopy(ivByteVal, 0, combinedByteVal, 0, ivByteVal.length); // POSITIONING THE IV BYTES
                System.arraycopy(encryptedTextByteVal, 0, combinedByteVal, ivByteVal.length, encryptedTextByteVal.length); // IMPLEMENTING INTO CIPHER

                System.out.println("encryptContent: ENCRYPT DONE...");
                return Base64.encodeToString(combinedByteVal, flags);
            } else {
                return ""; // RETURN NOTHING
            }
        } catch (Exception e)
        {
            System.out.println("encryptContent: ENCRYPT FAILED!...");
            e.printStackTrace();
        }

        return null;
    }

    @Nullable
    public static String decryptContent(String dataToDecrypt, String symmetricKey)
//            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException
    {
        try
        {
            System.out.println("decryptContent: DECRYPT STARTING...");
            byte[] encryptedCombinedBytes = Base64.decode(dataToDecrypt, flags);

            byte[] ivByteVal = Arrays.copyOfRange(encryptedCombinedBytes, 0, 16); // "BREAK" BYTES TO GET IV BYTES ONLY
            byte[] encryptedTextByteVal = Arrays.copyOfRange(encryptedCombinedBytes, 16, encryptedCombinedBytes.length); // "BREAK" BYTES TO GET CIPHER TEXT BYTES ONLY

            byte[] encryptedPassword = Base64.decode(symmetricKey, flags);

            SecretKeySpec secretKeySpec = new SecretKeySpec(encryptedPassword, ALGO);
            Cipher cipher = Cipher.getInstance(CipherALGO);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(ivByteVal)); // START CIPHER AND SET IT TO ENCYPT MODE

            byte[] decryptedTextByteVal = cipher.doFinal(encryptedTextByteVal); // DECRYPT TEXT

            System.out.println("decryptContent: DECRYPT DONE...");
            return new String(decryptedTextByteVal);
        } catch (Exception e)
        {
            System.out.println("decryptContent: DECRYPT FAILED...");
            e.printStackTrace();
        }

        return null;
    }
}