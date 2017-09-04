package com.gerardogandeaga.cyberlock.Encryption;

import android.content.Context;
import android.content.SharedPreferences;
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

import static com.gerardogandeaga.cyberlock.Supports.Globals.DIRECTORY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.ENCRYPTION_ALGO;
import static com.gerardogandeaga.cyberlock.Supports.Globals.FLAGS;

public class CryptKeyHandler
{
    private static final int iterations = 10000;
    private static int keylength, byteKeyLength;
    private static final String KeyALGO = "PBKDF2WithHmacSHA1";
    private static String ALGO, CipherALGO;
    private int IVLencgth;

    private SharedPreferences mSharedPreferences;

    public CryptKeyHandler(Context context)
    {
        mSharedPreferences = context.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE);
        ALGO = mSharedPreferences.getString(ENCRYPTION_ALGO, "AES");
        CipherALGO = ALGO + "/CBC/PKCS5Padding";

        switch (ALGO)
        {
            case "AES":
                IVLencgth = 16;
                byteKeyLength = 32;
                keylength = 256;
                break;
            case "Blowfish":
                IVLencgth = 8;
                byteKeyLength = 56;
                keylength = 448;
                break;
        }
    }

    // THESE FUNCTIONS GENERATE A ONE TIME RANDOM BYTE PASSWORD TO THE KEY GENERATOR (2ND FUNCTION)
    public byte[] BYTE_KEY_GENERATE()
    {
        SecureRandom random = new SecureRandom();
        byte[] KEY = new byte[byteKeyLength];
        random.nextBytes(KEY);

        /*  FUTURE KEY GENERATE REFACTOR

        cryptKeyByteVal = KEY_GENERATE(KEY);
        cryptKeyStringVal = Base64.encodeToString(cryptKeyByteVal, FLAGS);

        return cryptKeyStringVal;

        **Change return to String**
         */

        return KEY;
    }

    @Nullable
    public byte[] KEY_GENERATE(byte[] password)
//            throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        try
        {
            KeySpec mKeySpec = new PBEKeySpec(Base64.encodeToString(password, FLAGS).toCharArray(), generateSalt(), iterations, keylength);
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
    private byte[] getSymmetricKey(String password, byte[] salt, int iterations, int derivedKeyLength)
//            throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        try
        {
            KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, iterations, derivedKeyLength);
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(KeyALGO);

            return secretKeyFactory.generateSecret(keySpec).getEncoded();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Nullable
    public String ENCRYPTKEY(String dataToEncrypt, String key)
//            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidParameterSpecException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException
    {
        try
        {
            byte[] saltByteVal = generateSalt();

            byte[] encryptedPassword = getSymmetricKey(key, saltByteVal, iterations, keylength); // GENERATE KEY

            SecretKeySpec secretKeySpec = new SecretKeySpec(encryptedPassword, ALGO);
            Cipher cipher = Cipher.getInstance(CipherALGO);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec); // START CIPHER AND SET IT TO ENCRYPT MODE

            byte[] ivByteVal = Arrays.copyOfRange(cipher.getIV(), 0, IVLencgth); // GET IV OF CIPHER
            byte[] encryptedTextByteVal = cipher.doFinal(dataToEncrypt.getBytes()); // ENCRYPT TEXT

            byte[] combinedByteVal = new byte[saltByteVal.length + ivByteVal.length + encryptedTextByteVal.length]; // COMBINE THE BYTES
            System.arraycopy(ivByteVal, 0, combinedByteVal, 0, ivByteVal.length); // POSITIONING THE IV BYTES
            System.arraycopy(saltByteVal, 0, combinedByteVal, ivByteVal.length, saltByteVal.length); // POSITIONING THE IV BYTES
            System.arraycopy(encryptedTextByteVal, 0, combinedByteVal, ivByteVal.length + saltByteVal.length, encryptedTextByteVal.length); // IMPLEMENTING INTO CIPHER

            return Base64.encodeToString(combinedByteVal, FLAGS);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Nullable
    public String DECRYPTKEY(String dataToDecrypt, String key)
//            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException
    {
        try
        {
            byte[] encryptedCombinedBytes = Base64.decode(dataToDecrypt, FLAGS);

            byte[] ivByteVal = Arrays.copyOfRange(encryptedCombinedBytes, 0, IVLencgth); // "BREAK" BYTES TO GET IV BYTES ONLY
            byte[] saltByteVal = Arrays.copyOfRange(encryptedCombinedBytes, IVLencgth, IVLencgth + 16); // "BREAK" BYTES TO GET SALT BYTES ONLY
            byte[] encryptedTextByteVal = Arrays.copyOfRange(encryptedCombinedBytes, IVLencgth + 16, encryptedCombinedBytes.length); // "BREAK" BYTES TO GET CIPHER TEXT BYTES ONLY

            byte[] encryptedPassword = getSymmetricKey(key, saltByteVal, iterations, keylength);

            SecretKeySpec secretKeySpec = new SecretKeySpec(encryptedPassword, ALGO);
            Cipher cipher = Cipher.getInstance(CipherALGO);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(ivByteVal)); // START CIPHER AND SET IT TO ENCRYPT MODE

            byte[] decryptedTextByteVal = cipher.doFinal(encryptedTextByteVal); // DECRYPT TEXT

            return new String(decryptedTextByteVal);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    private byte[] generateSalt()
    {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        return salt;
    }
}