package com.gerardogandeaga.cyberlock.views;

import android.support.annotation.ColorRes;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.gerardogandeaga.cyberlock.utils.Resources;

/**
 * @author gerardogandeaga
 */
public class CustomSnackBar {
    public static int LENGTH_SHORT = Snackbar.LENGTH_SHORT;
    public static int LENGTH_LONG = Snackbar.LENGTH_LONG;

    public static int COLOUR_NONE = 0;

    public static void buildAndShowSnackBar(View view, String message, int duration) {
        buildAndShowSnackBar(view, message, duration, null, null);
    }

    public static void buildAndShowSnackBar(View view, String message, int duration, String buttonMessage, View.OnClickListener onClickListener) {
        buildAndShowSnackBar(view, message, duration, buttonMessage, onClickListener, COLOUR_NONE);
    }

    public static void buildAndShowSnackBar(View view, String message, int duration, String buttonMessage, View.OnClickListener onClickListener, @ColorRes int color) {
        Snackbar snackbar = Snackbar.make(view, message, duration);

        // set button message and listener
        if (onClickListener != null) {
            snackbar.setAction(buttonMessage, onClickListener);

            // colour of action text
            if (color != COLOUR_NONE) {
                snackbar.setActionTextColor(Resources.getColour(view.getContext(), color));
            }
        }

        // show
        snackbar.show();
    }
}
