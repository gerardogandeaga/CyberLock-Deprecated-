package com.gerardogandeaga.cyberlock.utils.views;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gerardogandeaga.cyberlock.database.DataPackage;
import com.mikepenz.materialize.holder.StringHolder;

public class ViewHandler {

    // TextView
    public static void setOrHideTextView(String string, TextView textView) {
        if (string == null || string.isEmpty()) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
            textView.setText(string);
        }
    }
    public static void setOrHideTextView(StringHolder string, TextView textView) {
        String s = string.toString();
        setOrHideTextView(s, textView);
    }

    // Image View
    public static void setOrHideImageView(Drawable drawable, ImageView imageView) {
        if (drawable == null) {
            imageView.setVisibility(View.GONE);
        } else {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageDrawable(drawable);
        }
    }

    // Linear Layouts
    public static void setLinearLayoutVisibility(LinearLayout linearLayout, boolean bool) {
        if (bool) {
            linearLayout.setVisibility(View.VISIBLE);
        } else {
            linearLayout.setVisibility(View.GONE);
        }
    }
    public static void setLinearLayoutVisibility(LinearLayout note, LinearLayout paymentinfo, LinearLayout logininfo, String args) {
        boolean isNote = false,
                isPaymentinfo = false,
                isLogininfo = false;
        switch (args) {
            case DataPackage.NOTE:         isNote = true; break;
            case DataPackage.PAYMENT_INFO: isPaymentinfo = true; break;
            case DataPackage.LOGIN_INFO:   isLogininfo = true; break;
        }

        setLinearLayoutVisibility(note, isNote);
        setLinearLayoutVisibility(paymentinfo, isPaymentinfo);
        setLinearLayoutVisibility(logininfo, isLogininfo);
    }
    public static boolean setLinearLayoutVisibility(LinearLayout linearLayout, String string) {
        if (string == null || string.isEmpty()) {
            setLinearLayoutVisibility(linearLayout, false);
            return false;
        }
        return true;
    }
}