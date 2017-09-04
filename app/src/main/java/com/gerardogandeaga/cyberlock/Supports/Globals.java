package com.gerardogandeaga.cyberlock.Supports;

import android.util.Base64;

public class Globals
{
    // SHARED PREFERENCES
    public static final String DIRECTORY = "com.gerardogandeaga.cyberlock";

    public static final String PIN = "PIN";

    public static final String CRYPT_KEY = "CRYPT_KEY";

    public static final String AUTOSAVE = "AUTO";

    public static final String LOGOUT_DELAY = "LOGOUT_DELAY";
    public static final String DELAY_TIME = "DELAY_TIME";

    public static final String ENCRYPTION_ALGO = "ENCRYPTION_ALGO";
    public static final String CIPHER_ALGO = "CIPHER_ALGO";

    public static final String PLAYGROUIND_ALGO = "PLAYGROUND_ALGO";
    // ------------------

    // ENCRYPTION
    public static String TEMP_PIN;
    public static String MASTER_KEY;
    public static final int FLAGS = Base64.DEFAULT;
    // ----------

    // APP STATES
    // ----------
}
