package com.gerardogandeaga.cyberlock.Supports;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;

import com.gerardogandeaga.cyberlock.R;

public class Globals extends AppCompatActivity
{
    // SHARED PREFERENCES
    public static final String DIRECTORY = "com.gerardogandeaga.cyberlock";

    public static final String PIN = "PIN";

    public static final String CRYPT_KEY = "CRYPT_KEY";

    public static final String AUTOSAVE = "AUTO";

    public static final String COMPLEXPASSCODE = "COMPLEXPASSCODE";

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
    public static final String SCHEME = "SCHEME";
    public static void COLORSCHEME(Context context)
    {
        String colourString = context.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getString(SCHEME, "SCHEME_BLUE");

        switch (colourString) {
            case "SCHEME_BLUE":
                context.getTheme().applyStyle(R.style.DefaultTheme, true);
                break;
            case "SCHEME_RED":
                context.getTheme().applyStyle(R.style.OverlaySchemeColorRed, true);
                break;
            case "SCHEME_GREEN":
                context.getTheme().applyStyle(R.style.OverlaySchemeColorGreen, true);
                break;
            case "SCHEME_YELLOW":
                context.getTheme().applyStyle(R.style.OverlaySchemeColorYellow, true);
                break;
            case "SCHEME_PURPLE":
                context.getTheme().applyStyle(R.style.OverlaySchemeColorPurple, true);
                break;
            case "SCHEME_GRAY":
                context.getTheme().applyStyle(R.style.OverlaySchemeColorGray, true);
                break;
            default:
                context.getTheme().applyStyle(R.style.DefaultTheme, true);
                break;
        }
    }
    // ----------

    // TODO LOG OUT THE LAST LOGGED IN TIME
    // TODO LOG OUT LAST EDITED TIME
    // TODO LAST OPENED DOCUMENT
}
