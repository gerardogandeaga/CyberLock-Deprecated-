package com.gerardogandeaga.cyberlock.utils.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.gerardogandeaga.cyberlock.utils.math.Scaling;

public class CustomDrawable {

    public static Drawable scaleDrawable(Context context, Drawable drawable, int widthToScale, int heightToScale) {

        int w = Scaling.dpFromPx(context, widthToScale);
        int h = Scaling.dpFromPx(context, heightToScale);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        return new BitmapDrawable(context.getResources(), Bitmap.createScaledBitmap(bitmap, w, h, true));
    }
}
