package com.gerardogandeaga.cyberlock.support;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;

@SuppressLint("Registered")
public class Globals extends AppCompatActivity {
    public static final String DIRECTORY = "com.gerardogandeaga.cyberlock";

    // Primitives
    public static final String IS_REGISTERED = "IS_REGISTERED";
    public static final String LAST_LOGIN = "LAST_LOGIN";

    // Settings
    public static final String AUTOSAVE = "AUTO";
    public static final String LOGOUT_DELAY = "LOGOUT_DELAY";
    public static final String DELAY_TIME = "DELAY_TIME";
    // Theme
    public static final String THEME = "THEME";
    // Content list layout
    public static final String RV_FORMAT = "RV_FORMAT";
    // --
    public static final String PLAYGROUND_ALGO = "PLAYGROUND_ALGO";
    public static final String CRYPT_ALGO = "CRYPT_ALGO";
    public static final String CIPHER_ALGO = "CIPHER_ALGO";

    // Sensitives
    public static final String PASSCODE = "PASSCODE";
    public static final String CRYPT_KEY = "CRYPT_KEY";
    // ------------------

    // TMP Vars
    // Primitives
    public static final int FLAGS = Base64.DEFAULT;
    //
    public static String LOGGED;
    // Crypto
    public static String TEMP_PIN;
    public static String MASTER_KEY;
    // ----------
}