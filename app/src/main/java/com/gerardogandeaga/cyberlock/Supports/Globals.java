package com.gerardogandeaga.cyberlock.Supports;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;

import com.gerardogandeaga.cyberlock.R;

public class Globals extends AppCompatActivity {
    // SHARED PREFERENCES KEYS
    public static final String DIRECTORY = "com.gerardogandeaga.cyberlock";
    // PRIMES
    public static final String IS_REGISTERED = "IS_REGISTERED";
    public static final String LAST_LOGIN = "LAST_LOGIN";

    // SETTINGS
    public static final String AUTOSAVE = "AUTO";
    public static final String LOGOUT_DELAY = "LOGOUT_DELAY";
    public static final String DELAY_TIME = "DELAY_TIME";
    // --
    public static final String PLAYGROUIND_ALGO = "PLAYGROUND_ALGO";
    public static final String ENCRYPTION_ALGO = "ENCRYPTION_ALGO";
    public static final String CIPHER_ALGO = "CIPHER_ALGO";

    // SENSITIVE
    public static final String PASSCODE = "PASSCODE";
    public static final String CRYPT_KEY = "CRYPT_KEY";
    // ------------------

    // TMP VARS
    // PRIMITIVES
    public static final int FLAGS = Base64.DEFAULT;
    // PRIMES
    public static String LOGGED;
    // CRYPTO
    public static String TEMP_PIN;
    public static String MASTER_KEY;
    // ----------

    // APP STATES
    public static final String SCHEME = "SCHEME";
    public static void COLORSCHEME(Context context) {
        String colourString = context.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getString(SCHEME, "SCHEME_BLUE");

        switch (colourString) {
            case "SCHEME_BLUE":
                context.getTheme().applyStyle(R.style.DefaultTheme, true);
                break;
            default:
                context.getTheme().applyStyle(R.style.DefaultTheme, true);
                break;
        }
    }
    // ----------

    // TODO APP GOES TO REGISTER EVENT THOUGH THE IS_REGISTERED CONDITION IS SUPPOSEDLY TRUE
    // TODO LOG OUT THE LAST LOGGED IN TIME
    // TODO LOG OUT LAST EDITED TIME
    // TODO LAST OPENED DOCUMENT
}