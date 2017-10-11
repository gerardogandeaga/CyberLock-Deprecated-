package com.gerardogandeaga.cyberlock.Crypto;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Base64;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import static com.gerardogandeaga.cyberlock.Supports.Globals.CRYPT_KEY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.DIRECTORY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.ENCRYPTION_ALGO;
import static com.gerardogandeaga.cyberlock.Supports.Globals.FLAGS;

public class CryptKeyHandler
{
    private SharedPreferences mSharedPreferences;

    private final int iterations = 10000;
    private int keylength, byteKeyLength;
    private final String KeyALGO = "PBKDF2WithHmacSHA1";
    private String ALGO, CipherALGO;
    private int IVLength;

    public CryptKeyHandler(Context context)
    {
        mSharedPreferences = context.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE);
        ALGO = mSharedPreferences.getString(ENCRYPTION_ALGO, "AES");
        CipherALGO = ALGO + "/CBC/PKCS5Padding";

        switch (ALGO)
        {
            case "AES":
                IVLength = 16;
                byteKeyLength = 32;
                keylength = 256;
                break;
            case "Blowfish":
                IVLength = 8;
                byteKeyLength = 56;
                keylength = 448;
                break;
        }
    }

    // THESE FUNCTIONS GENERATE A ONE TIME RANDOM BYTE PASSWORD TO THE KEY GENERATOR (2ND FUNCTION)
    public String GENERATE_NEW_KEY(String passcode)
    {
        SecureRandom random = new SecureRandom();
        byte[] KEY = new byte[byteKeyLength];
        random.nextBytes(KEY);

        byte[] cryptKeyByteVal = GET_KEY_SPEC(KEY);
        String cryptKeyStringVal = (Base64.encodeToString(cryptKeyByteVal, FLAGS));
        String encryptedCryptKeyStringVal = ENCRYPT_KEY(cryptKeyStringVal, passcode);

        // **STORE KEY**
        mSharedPreferences.edit().putString(CRYPT_KEY, encryptedCryptKeyStringVal).apply();
        // -------------

        System.out.println("NEW KEY VALUE IN STRING: " + cryptKeyStringVal);
        System.out.println("NEW KEY LENGTH IN BYTES: " + cryptKeyByteVal.length);
        System.out.println("NEW KEY VALUE IN STRING ENCRYPTED: " + encryptedCryptKeyStringVal);

        return encryptedCryptKeyStringVal;
    }

    @Nullable
    private byte[] GET_KEY_SPEC(byte[] password)
//            throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        try
        {
            KeySpec mKeySpec = new PBEKeySpec(Base64.encodeToString(password, FLAGS).toCharArray(), GENERATE_SALT(), iterations, keylength);
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
    private byte[] GET_SYMMETRIC_KEY(String password, byte[] salt, int iterations, int derivedKeyLength)
//            throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        try
        {
            KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, iterations, derivedKeyLength);
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(KeyALGO);

            return secretKeyFactory.generateSecret(keySpec).getEncoded();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Nullable
    public String ENCRYPT_KEY(String dataToEncrypt, String key)
//            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidParameterSpecException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException
    {
        try
        {
            byte[] saltByteVal = GENERATE_SALT();

            byte[] encryptedPassword = GET_SYMMETRIC_KEY(key, saltByteVal, iterations, keylength); // GENERATE KEY

            SecretKeySpec secretKeySpec = new SecretKeySpec(encryptedPassword, ALGO);
            Cipher cipher = Cipher.getInstance(CipherALGO);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec); // START CIPHER AND SET IT TO ENCRYPT MODE

            byte[] ivByteVal = Arrays.copyOfRange(cipher.getIV(), 0, IVLength); // GET IV OF CIPHER
            byte[] encryptedTextByteVal = cipher.doFinal(dataToEncrypt.getBytes()); // ENCRYPT TEXT

            byte[] combinedByteVal = new byte[ivByteVal.length + saltByteVal.length + encryptedTextByteVal.length]; // COMBINE THE BYTES
            System.arraycopy(ivByteVal, 0, combinedByteVal, 0, ivByteVal.length); // POSITIONING THE IV BYTES
            System.arraycopy(saltByteVal, 0, combinedByteVal, ivByteVal.length, saltByteVal.length); // POSITIONING THE IV BYTES
            System.arraycopy(encryptedTextByteVal, 0, combinedByteVal, ivByteVal.length + saltByteVal.length, encryptedTextByteVal.length); // IMPLEMENTING INTO CIPHER

            return Base64.encodeToString(combinedByteVal, FLAGS);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException |
                BadPaddingException | InvalidKeyException |
                IllegalBlockSizeException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Nullable
    public String DECRYPT_KEY(String dataToDecrypt, String key)
//            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException
    {
        try
        {
            byte[] encryptedCombinedBytes = Base64.decode(dataToDecrypt, FLAGS);

            byte[] ivByteVal = Arrays.copyOfRange(encryptedCombinedBytes, 0, IVLength); // "BREAK" BYTES TO GET IV BYTES ONLY
            byte[] saltByteVal = Arrays.copyOfRange(encryptedCombinedBytes, IVLength, IVLength + 16); // "BREAK" BYTES TO GET SALT BYTES ONLY
            byte[] encryptedTextByteVal = Arrays.copyOfRange(encryptedCombinedBytes, IVLength + 16, encryptedCombinedBytes.length); // "BREAK" BYTES TO GET CIPHER TEXT BYTES ONLY

            byte[] encryptedPassword = GET_SYMMETRIC_KEY(key, saltByteVal, iterations, keylength);

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

    private byte[] GENERATE_SALT()
    {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        return salt;
    }
}