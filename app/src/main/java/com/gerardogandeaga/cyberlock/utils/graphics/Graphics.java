package com.gerardogandeaga.cyberlock.utils.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.utils.Res;
import com.gerardogandeaga.cyberlock.utils.Settings;
import com.gerardogandeaga.cyberlock.utils.math.Scaling;

import org.jetbrains.annotations.Contract;

public class Graphics {

    public static class ColourTags {

        public static int colourTagHeader(Context context, String colour) {
            if (Settings.getTaggedHeaders(context)) {
                return colourTag(context, colour);
            } else {
                return Res.getColour(context, R.color.black);
            }
        }

        public static int colourTag(Context context, String colour) {
            return baseColourSearch(context, colour, true);
        }

        public static int colourTagListView(Context context, String colour) {
            return baseColourSearch(context, colour, false);
        }

        private static int baseColourSearch(Context context, String colour, boolean darkDefault) {
            if (colour != null) {
                final int[] colours = context.getResources().getIntArray(R.array.array_tag_colours);
                final String[] colourNames = context.getResources().getStringArray(R.array.array_tag_colours_names);

                for (int i = 0; i < colourNames.length; i++) {
                    if (colour.matches(colourNames[i])) {
                        return colours[i];
                    }
                }
            }
            // light or dark default colour
            if (darkDefault) {
                return Res.getColour(context, R.color.black);
            } else {
                return Res.getColour(context, R.color.ct_default);
            }
        }
    }

    public static class CardImages {

        @Contract(pure = true)
        public static boolean isCardType(String str) {
            switch (str) {
                case ("Visa"):             return true;
                case ("Master Card"):      return true;
                case ("American Express"): return true;
                case ("Discover"):         return true;
                case ("Other"):            return true;
            }

            return false;
        }

        public static Drawable getCardImage(Context context, String cardType) {
            switch (cardType) {
                case ("Visa"):             return Res.getDrawable(context, R.drawable.card_visa);
                case ("Master Card"):      return Res.getDrawable(context, R.drawable.card_mastercard);
                case ("American Express"): return Res.getDrawable(context, R.drawable.card_americanexpress);
                case ("Discover"):         return Res.getDrawable(context, R.drawable.card_discover);
                default:                   return Res.getDrawable(context, R.drawable.card_default);
            }
        }

        @NonNull
        public static Drawable getCardImage(Context context, String cardType, int newWidth, int newHeight) {
            Drawable factoryDrawable = getCardImage(context, cardType);

            int w = Scaling.dpToPx(context, newWidth);
            int h = Scaling.dpToPx(context, newHeight);
            Bitmap bitmap = ((BitmapDrawable) factoryDrawable).getBitmap();

            return new BitmapDrawable(context.getResources(), Bitmap.createScaledBitmap(bitmap, w, h, true));
        }

        public static Drawable getCardImage(Context context, String cardType, float scalePercentage) {
            Drawable factoryDrawable = getCardImage(context, cardType);

            float w = factoryDrawable.getMinimumWidth();
            float h = factoryDrawable.getMinimumHeight();
            int ww = (int) ((int) w - (w * scalePercentage));
            int hh = (int) ((int) h - (h * scalePercentage));

            factoryDrawable.setBounds(0, 0, ww, hh);

            return factoryDrawable;
        }
    }

    public static class BasicFilter {

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
            return Res.getColour(context, R.color.black);
        }

        @Contract(pure = true)
        private static PorterDuff.Mode getMode() {
            return PorterDuff.Mode.SRC_ATOP;
        }
    }
}
