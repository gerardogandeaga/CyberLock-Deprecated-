package com.gerardogandeaga.cyberlock.utils;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author gerardogandeaga
 */
public class Views {

    public static void setVisibility(View view, boolean visible) {
        if (visible) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    public static void setVisibility(boolean visible, View ...views) {
        for (View view : views) {
            setVisibility(view, visible);
        }
    }

    public static class TextViews {

        /**
         * custom definition for empty string]
         */
        private static boolean isEmpty(String string) {
            return string == null || string.isEmpty();
        }

        public static void setOrHideText(TextView textView, String string) {
            textView.setText(string);
            if (isEmpty(string)) {
                setVisibility(textView, false);
            } else {
                setVisibility(textView, true);
            }
        }

        public static void setOrHideTextOnNestedView(View parent, TextView textView, String string) {
            setOrHideText(textView, string);

            // if string is empty then hide nested view
            if (isEmpty(string)) {
                setVisibility(parent, false);
            }
        }
    }

    public static class ImageViews {

        public static void setOrHideImage(ImageView imageView, Drawable drawable) {
            imageView.setImageDrawable(drawable);
            if (drawable != null) {
                if (imageView.getVisibility() != View.VISIBLE) {
                    imageView.setVisibility(View.VISIBLE);
                }
            } else {
                imageView.setVisibility(View.GONE);
            }
        }
    }
}
