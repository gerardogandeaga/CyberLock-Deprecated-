package com.gerardogandeaga.cyberlock.utils.graphics;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;

import com.gerardogandeaga.cyberlock.R;

import org.jetbrains.annotations.Contract;

public class DrawableColours {
    @NonNull
    public static Drawable mutateHomeAsUpIndicatorDrawable(Context context, Drawable drawable) {
        drawable.mutate().setColorFilter(getColour(context), getMode());
        return drawable;
    }

    public static void mutateMenuItems(Context context, Menu menu) {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.getIcon() != null) {
                item.getIcon().mutate().setColorFilter(getColour(context), getMode());
            }
        }
    }

    private static int getColour(Context context) {
        return context.getResources().getColor(R.color.white);
    }

    @Contract(pure = true)
    private static PorterDuff.Mode getMode() {
        return PorterDuff.Mode.SRC_ATOP;
    }
}
