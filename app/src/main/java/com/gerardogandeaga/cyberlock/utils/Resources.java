package com.gerardogandeaga.cyberlock.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class Resources {

    public static int getColour(Context context, int id) {
        return context.getResources().getColor(id);
    }

    public static Drawable getDrawable(Context context, int id) {
        return context.getResources().getDrawable(id);
    }
}
