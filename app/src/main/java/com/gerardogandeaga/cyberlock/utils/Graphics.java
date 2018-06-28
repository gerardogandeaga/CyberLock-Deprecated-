package com.gerardogandeaga.cyberlock.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;

import com.gerardogandeaga.cyberlock.R;

import org.jetbrains.annotations.Contract;

/**
 * @author gerardogandeaga
 */
public class Graphics {

    public static class ColourTags {

        public static int colourTagToolbar(Context context, String colour) {
            return colourTagHeader(context, colour);
        }

        public static int colourTagHeader(Context context, String colour) {
            if (colour == null) {
                return Res.getColour(R.color.black);
            }

            if (Pref.getTaggedHeaders(context)) {
                return colourTag(context, colour);
            } else {
                return Res.getColour(R.color.black);
            }
        }

        public static int colourTag(Context context, String colour) {
            return baseColourSearch(context, colour, true);
        }

        public static int colourTagListView(Context context, String colour) {
            return baseColourSearch(context, colour, true);
        }

        private static int baseColourSearch(Context context, String colour, boolean darkDefault) {
            if (colour != null) {
                final int[] colours = context.getResources().getIntArray(R.array.arr_tag_colours);
                final String[] colourNames = context.getResources().getStringArray(R.array.arr_tag_colours_names);

                for (int i = 0; i < colourNames.length; i++) {
                    if (colour.matches(colourNames[i])) {
                        return colours[i];
                    }
                }
            }
            // light or dark default colour
            if (darkDefault) {
                return Res.getColour(R.color.black);
            } else {
                return Res.getColour(R.color.ct_default);
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

        public static Drawable getCardImage(String cardType) {
            switch (cardType) {
                case ("Visa"):             return Res.getDrawable(R.drawable.card_visa);
                case ("Master Card"):      return Res.getDrawable(R.drawable.card_mastercard);
                case ("American Express"): return Res.getDrawable(R.drawable.card_amex);
                case ("Discover"):         return Res.getDrawable(R.drawable.card_discover);
                default:                   return Res.getDrawable(R.drawable.card_default);
            }
        }

        @NonNull
        public static Drawable getCardImage(Context context, String cardType, int newWidth, int newHeight) {
            Drawable factoryDrawable = getCardImage(cardType);

            int w = Scale.dpFromPx(context, newWidth);
            int h = Scale.dpFromPx(context, newHeight);
            Bitmap bitmap = ((BitmapDrawable) factoryDrawable).getBitmap();

            return new BitmapDrawable(context.getResources(), Bitmap.createScaledBitmap(bitmap, w, h, true));
        }

        public static Drawable getCardImage(String cardType, float scalePercentage) {
            Drawable factoryDrawable = getCardImage(cardType);

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
        public static Drawable mutateHomeAsUpIndicatorDrawable(Drawable drawable) {
            drawable.mutate().setColorFilter(getColour(), getMode());
            return drawable;
        }

        public static void mutateMenuItems(Menu menu, int colour) {
            for (int i = 0; i < menu.size(); i++) {
                MenuItem item = menu.getItem(i);

                if (item.getIcon() != null) {
                    item.getIcon().mutate().setColorFilter(Res.getColour(colour), getMode());
                }
            }
        }

        public static void mutateMenuItems(Menu menu) {
            for (int i = 0; i < menu.size(); i++) {
                MenuItem item = menu.getItem(i);

                if (item.getIcon() != null) {
                    item.getIcon().mutate().setColorFilter(getColour(), getMode());
                }
            }
        }

        private static int getColour() {
            return Res.getColour(R.color.black);
        }

        @Contract(pure = true)
        private static PorterDuff.Mode getMode() {
            return PorterDuff.Mode.SRC_ATOP;
        }
    }
}
