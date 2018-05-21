package com.gerardogandeaga.cyberlock.utils;

import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;

import com.gerardogandeaga.cyberlock.App;

/**
 * @author gerardogandeaga
 *
 * easy reasource class
 */
public class Res {

    /**
     * get colour from colour resource
     * @param colorResId resource id
     * @return Color
     */
    public static int getColour(@ColorRes int colorResId) {
        return App.getContext().getResources().getColor(colorResId);
    }

    /**
     * get drawable from drawable resource
     * @param drawableResId resource id
     * @return Drawable
     */
    public static Drawable getDrawable(@DrawableRes int drawableResId) {
        return App.getContext().getResources().getDrawable(drawableResId);
    }
}
