package com.gerardogandeaga.cyberlock;

import android.view.View;
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

    public static class TextViews {

        /**
         * custom definition for empty string]
         */
        private static boolean isEmpty(String string) {
            return (string == null || string.isEmpty());
        }

        public static void setOrHideText(TextView textView, String string) {
            if (!isEmpty(string)) {
                textView.setText(string);
            } else {
                setVisibility(textView, false);
            }
        }

        public static void setOrHideTextOnNestedView(View view, TextView textView, String string) {
            setOrHideText(textView, string);

            // if string is empty then hide nested view
            if (isEmpty(string)) {
                setVisibility(view, false);
            }
        }
    }
}
