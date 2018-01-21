package com.gerardogandeaga.cyberlock.support.graphics;

import android.content.Context;

import com.gerardogandeaga.cyberlock.R;

import org.jetbrains.annotations.Contract;

import static com.gerardogandeaga.cyberlock.support.Stored.DIRECTORY;
import static com.gerardogandeaga.cyberlock.support.Stored.THEME;

public class Themes {
    private static String AppTheme = "THEME_LIGHT";

    @Contract(pure = true)
    public static String getAppTheme() {
        return AppTheme;
    }

    public static void setTheme(Context context) {
        switch (context.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getString(THEME, "")) {
            case "THEME_DARK":
                AppTheme = "THEME_DARK";
                context.getTheme().applyStyle(R.style.AppTheme_Dark, true);
                break;
            default:
                AppTheme = "THEME_LIGHT";
                context.getTheme().applyStyle(R.style.AppTheme_Light, true);
                break;
        }
    }

    // Custom fixers
    public static int recyclerListItemRegular() {
        if (AppTheme.matches("THEME_LIGHT")) {
            return R.color.white;
        } else {
            return R.color.c_blue_80;
        }
    }
    public static int recyclerListItemSelected() {
        if (AppTheme.matches("THEME_LIGHT")) {
            return R.color.c_yellow_20;
        } else {
            return R.color.c_blue_60;
        }
    }

    public static int defaultTag(Context context) {
        if (AppTheme.matches("THEME_LIGHT")) {
            return context.getResources().getColor(R.color.black);
        } else {
            return context.getResources().getColor(R.color.white);
        }
    }
}
