package com.gerardogandeaga.cyberlock.utils;

import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.gerardogandeaga.cyberlock.App;

/**
 * @author gerardogandeaga
 *
 * easy (lazy) reasource accessor class
 */
public class Res {

    /**
     * get colour from colour resource
     * @param colourResId colour resource id
     * @return Colour
     */
    public static int getColour(@ColorRes int colourResId) {
        return App.getContext().getResources().getColor(colourResId);
    }

    /**
     * get drawable from drawable resource
     * @param drawableResId drawable resource id
     * @return Drawable
     */
    public static Drawable getDrawable(@DrawableRes int drawableResId) {
        return App.getContext().getResources().getDrawable(drawableResId);
    }

    /**
     * get string from String resource
     * @param stringResId string resource id
     * @return String
     */
    @NonNull
    public static String getString(@StringRes int stringResId) {
        return App.getContext().getResources().getString(stringResId);
    }
}
