package com.gerardogandeaga.cyberlock.Encryption;

import android.support.annotation.Nullable;
import android.util.Base64;

import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class AESKeyHandler
{
    private static final int flags = Base64.DEFAULT;
    private static final int iterations = 500, keylength = 128;
    private static final String ALGO = "AES", KeyALGO = "PBKDF2WithHmacSHA1", CipherALGO = "AES/CBC/PKCS5Padding";

    // THESE FUCNTIONS GENERATE A ONE TIME RANDOM BYTE PASSWORD TO THE KEY GENERATOR (2ND FUNCTION)
    public static byte[] AES_MEMO_BYTE_KEY_GENERATE()
    {
        SecureRandom r = new SecureRandom();
        byte[] AES_MEMO_KEY = new byte[32];
        r.nextBytes(AES_MEMO_KEY);

        return AES_MEMO_KEY;
    }

    @Nullable
    public static byte[] AES_MEMO_KEY_GENERATE(byte[] password)
//            throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        try
        {
            KeySpec mKeySpec = new PBEKeySpec(Base64.encodeToString(password, flags).toCharArray(), generateSalt(), iterations, keylength);
            SecretKeyFactory mSecretKeyFactory = SecretKeyFactory.getInstance(KeyALGO);

            return mSecretKeyFactory.generateSecret(mKeySpec).getEncoded();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }
    // --------------------------------------------------------------------------------------------

    @Nullable
    private static byte[] getSymmetricKey(String password, byte[] salt, int iterations, int derivedKeyLength)
//            throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        try
        {
            KeySpec mKeySpec = new PBEKeySpec(password.toCharArray(), salt, iterations, derivedKeyLength);
            SecretKeyFactory mSecretKeyFactory = SecretKeyFactory.getInstance(KeyALGO);

            return mSecretKeyFactory.generateSecret(mKeySpec).getEncoded();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Nullable
    public static String ENCRYPTKEY(String dataToEncrypt, String key)
//            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidParameterSpecException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException
    {
        try
        {
            byte[] saltByteVal = generateSalt();

            byte[] mEncryptedPassword = getSymmetricKey(key, saltByteVal, iterations, keylength); // GENERATE KEY

            SecretKeySpec secretKeySpec = new SecretKeySpec(mEncryptedPassword, ALGO);
            Cipher cipher = Cipher.getInstance(CipherALGO);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec); // START CIPHER AND SET IT TO ENCRYPT MODE

            byte[] ivByteVal = cipher.getIV(); // GET IV OF CIPHER
            byte[] encryptedTextByteVal = cipher.doFinal(dataToEncrypt.getBytes()); // ENCRYPT TEXT

            byte[] combinedByteVal = new byte[saltByteVal.length + ivByteVal.length + encryptedTextByteVal.length]; // COMBINE THE BYTES
            System.arraycopy(ivByteVal, 0, combinedByteVal, 0, ivByteVal.length); // POSITIONING THE IV BYTES
            System.arraycopy(saltByteVal, 0, combinedByteVal, ivByteVal.length, saltByteVal.length); // POSITIONING THE IV BYTES
            System.arraycopy(encryptedTextByteVal, 0, combinedByteVal, ivByteVal.length + saltByteVal.length, encryptedTextByteVal.length); // IMPLEMENTING INTO CIPHER

            return Base64.encodeToString(combinedByteVal, flags);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Nullable
    public static String DECRYPTKEY(String dataToDecrypt, String key)
//            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException
    {
        try
        {
            byte[] encryptedCombinedBytes = Base64.decode(dataToDecrypt, flags);

            byte[] ivByteVal = Arrays.copyOfRange(encryptedCombinedBytes, 0, 16); // "BREAK" BYTES TO GET IV BYTES ONLY
            byte[] saltByteVal = Arrays.copyOfRange(encryptedCombinedBytes, 16, 32); // "BREAK" BYTES TO GET SALT BYTES ONLY
            byte[] encryptedTextByteVal = Arrays.copyOfRange(encryptedCombinedBytes, 32, encryptedCombinedBytes.length); // "BREAK" BYTES TO GET CIPHER TEXT BYTES ONLY

            byte[] mEncryptedPassword = getSymmetricKey(key, saltByteVal, iterations, keylength);

            SecretKeySpec secretKeySpec = new SecretKeySpec(mEncryptedPassword, ALGO);
            Cipher cipher = Cipher.getInstance(CipherALGO);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(ivByteVal)); // START CIPHER AND SET IT TO ENCYPT MODE

            byte[] decryptedTextByteVal = cipher.doFinal(encryptedTextByteVal); // DECRYPT TEXT

            return new String(decryptedTextByteVal);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    private static byte[] generateSalt()
    {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        return salt;
    }
}