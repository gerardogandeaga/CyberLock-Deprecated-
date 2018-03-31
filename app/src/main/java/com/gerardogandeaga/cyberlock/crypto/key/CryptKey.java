package com.gerardogandeaga.cyberlock.crypto.key;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Base64;

import com.gerardogandeaga.cyberlock.utils.SharedPreferences;

import java.security.InvalidAlgorithmParameterException;
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

import static com.gerardogandeaga.cyberlock.utils.SharedPreferences.TMP_PWD;

// todo java docs this class
/**
 * @author gerardogandeaga
 */
public class CryptKey {
    private static int mIvLength;
    private static String mEncryptionAlgorithm;
    private static String mCipherAlgorithm;

    private static int mKeyLength;
    private static int mKeyByteLength;
    private static final int mSaltLength = 16;
    private static final int mIterations = 10000;
    private static final String mKeyAlgorithm = "PBKDF2WithHmacSHA1";

    private static String mKey;

    private static void setAlgorithmPresets(Context context) {
        mKey = TMP_PWD;
        mEncryptionAlgorithm = SharedPreferences.getEncryptionAlgorithm(context);
        mCipherAlgorithm = mEncryptionAlgorithm + "/CBC/PKCS5Padding";

        switch (mEncryptionAlgorithm) {
            case "AES":
                mKeyLength = 256;
                mKeyByteLength = 32;
                mIvLength = 16;
                break;
            case "Blowfish":
                mKeyLength = 448;
                mKeyByteLength = 56;
                mIvLength = 8;
                break;
        }
    }
    // called once, when the program is registering the user in all other cases this will not be called
    private static void checkKeyOverride(String password) {
        if (password != null) {
            mKey = password;
            System.out.println("OVERRIDDEN PASSWORD KEY FOR MASTER KEY: " + mKey);
        }
    }

    /*
    generate a new encryption key encrypting and decrypting content in the sqlite database
    these 2 functions can only be called by the registration, key scramble and algorithm change classes


    NOTE: generateNewMasterKey function's output key is completely irrelevant to the rest of this class and is only
    stored in shared preferences to later be applied by the DBCrypt class
    */
    public static String generateNewMasterEncryptionKey(Context context, String password) {
        setAlgorithmPresets(context);

        String secretKey = Base64.encodeToString(randomBytes(mKeyByteLength), Base64.DEFAULT);
        // generate new random secret key
        String cryptKeyStringVal = generateSecretKey(secretKey);
        // encrypt key with user inputted password
        String encryptedCryptKeyStringVal = encrypt(context, cryptKeyStringVal, password);

        System.out.println("NEW KEY VALUE IN STRING: " + cryptKeyStringVal);
        System.out.println("NEW KEY LENGTH IN BYTES: " + (cryptKeyStringVal != null ? cryptKeyStringVal.getBytes().length : 0));
        System.out.println("NEW KEY VALUE IN STRING ENCRYPTED: " + encryptedCryptKeyStringVal);

        return encryptedCryptKeyStringVal;
    }
    @Nullable
    private static String generateSecretKey(String password) {
        try {
            KeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray(), generateSalt(), mIterations, mKeyLength);
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(mKeyAlgorithm);

            return Base64.encodeToString(secretKeyFactory.generateSecret(pbeKeySpec).getEncoded(), Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // from this point downwards are the normal encryption and decryption functions for the encryption key
    @Nullable
    public static String encrypt(Context context, String string, String password) {
        setAlgorithmPresets(context);
        checkKeyOverride(password);

        try {
            // random salt and iv generation
            byte[] saltByteVal = generateSalt();
            byte[] ivByteVal = generateIV();

            // generate secret key
            byte[] secretKey = generateSecretKey(mKey, saltByteVal, mIterations, mKeyLength);
            System.out.println("GENERATED SECRET KEY - ENCRYPTION MODE: " + Base64.encodeToString(secretKey, Base64.DEFAULT));
            // generate symmetric key
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, mEncryptionAlgorithm);

            // create cipher instance
            Cipher cipher = Cipher.getInstance(mCipherAlgorithm);
            // start cipher with mode : encrypt
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(ivByteVal));

            // decode string to bytes and encrypt
            byte[] encryptedStringByteVal = cipher.doFinal(string.getBytes());

            // create a new byte array of the length of iv + salt + encrypted string
            byte[] combinedByteVal = new byte[ivByteVal.length + saltByteVal.length + encryptedStringByteVal.length];
            System.arraycopy(ivByteVal, 0, combinedByteVal, 0, ivByteVal.length);
            System.arraycopy(saltByteVal, 0, combinedByteVal, ivByteVal.length, saltByteVal.length);
            System.arraycopy(encryptedStringByteVal, 0, combinedByteVal, ivByteVal.length + saltByteVal.length, encryptedStringByteVal.length);

            // encode to string with Base64
            return Base64.encodeToString(combinedByteVal, Base64.DEFAULT);

            // catching and handling exceptions
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }

        return null;
    }
    @Nullable
    public static String decrypt(Context context, String string, String password) {
        setAlgorithmPresets(context);
        checkKeyOverride(password);

        try {
            // decode encrypted string to encrypted bytes
            byte[] encryptedCombinedBytes = Base64.decode(string, Base64.DEFAULT);

            // strip the bytes down, recovering the iv, salt and encrypted bytes
            byte[] ivByteVal = Arrays.copyOfRange(encryptedCombinedBytes, 0, mIvLength);
            byte[] saltByteVal = Arrays.copyOfRange(encryptedCombinedBytes, mIvLength, mIvLength + 16);
            byte[] encryptedStringByteVal = Arrays.copyOfRange(encryptedCombinedBytes, mIvLength + 16, encryptedCombinedBytes.length);

            // generate parallel(same key as used for encryption) secret key
            byte[] secretKey = generateSecretKey(mKey, saltByteVal, mIterations, mKeyLength);
            System.out.println("GENERATED SECRET KEY - DECRYPTION MODE: " + Base64.encodeToString(secretKey, Base64.DEFAULT));
            // generate parallel(same key as used for encryption) symmetric key
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, mEncryptionAlgorithm);

            // create cipher instance
            Cipher cipher = Cipher.getInstance(mCipherAlgorithm);
            // start cipher with mode : decrypt
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(ivByteVal));

            // decrypt bytes
            byte[] decryptedTextByteVal = cipher.doFinal(encryptedStringByteVal);

            // build a new string object
            return new String(decryptedTextByteVal);

            // catching and handling exceptions
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    private static byte[] generateSecretKey(String password, byte[] salt, int iterations, int derivedKeyLength) {
        try {
            KeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray(), salt, iterations, derivedKeyLength);
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(mKeyAlgorithm);

            return secretKeyFactory.generateSecret(pbeKeySpec).getEncoded();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    private static byte[] generateSalt() {
        return randomBytes(mSaltLength);
    }
    private static byte[] generateIV() {
        return randomBytes(mIvLength);
    }

    // random bytes generation
    private static byte[] randomBytes(int length) {
        SecureRandom random = new SecureRandom();
        byte[] bytes  = new byte[length];
        random.nextBytes(bytes);

        return bytes;
    }
}