package com.gerardogandeaga.cyberlock.support.graphics;

import android.content.Context;

import com.gerardogandeaga.cyberlock.R;

import static com.gerardogandeaga.cyberlock.support.Globals.DIRECTORY;
import static com.gerardogandeaga.cyberlock.support.Globals.THEME;

public class Themes {
    public static void setTheme(Context context) {
        switch (context.getSharedPreferences(DIRECTORY, 0).getInt(THEME, 0)) {
            case 1:  context.getTheme().applyStyle(R.style.AppTheme_Light, true);
            default: context.getTheme().applyStyle(R.style.AppTheme_Light, true);
        }
    }
}
