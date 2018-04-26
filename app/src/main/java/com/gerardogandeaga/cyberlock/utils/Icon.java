package com.gerardogandeaga.cyberlock.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;

import com.gerardogandeaga.cyberlock.App;

/**
 * @author gerardogandeaga
 */
public class Icon {
    private Drawable mDrawable;

    /**
     * drawable from resources
     * @param drawableRes int resource tag
     */
    public Icon(@DrawableRes int drawableRes) {
        this.mDrawable = Res.getDrawable(drawableRes);
    }

    /**
     * drawable from passed drawable
     * @param drawable drawable for mutating
     */
    public Icon(Drawable drawable) {
        this.mDrawable = drawable;
    }

    /**
     * squared resizing, ie the icon will always keep a 1:1 ratio
     * @param dp size to resize to
     * @return resized icon
     */
    public Drawable resize(int dp) {
        // turn drawable into a bitmap
        Bitmap bitmap = ((BitmapDrawable) mDrawable).getBitmap();
        // resize bitmap
        Bitmap bitmapResized = Bitmap.createScaledBitmap(bitmap, dp, dp, false);
        // create drawable from bitmap
        this.mDrawable = new BitmapDrawable(App.getContext().getResources(), bitmapResized);

        return mDrawable;
    }
}
