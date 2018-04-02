package com.gerardogandeaga.cyberlock.views.handlers;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gerardogandeaga.cyberlock.database.objects.NoteObject;
import com.mikepenz.materialize.holder.StringHolder;


// todo refactor
/**
 * @author gerardogandeaga
 */
public class TextViews {

    // TextView
    public static void setOrHideTextView(StringHolder string, TextView textView) {
        setOrHideTextView(string.toString(), textView);
    }
    public static void setOrHideTextView(String string, TextView textView) {
        textView.setEllipsize(TextUtils.TruncateAt.END);
        if (string == null || string.isEmpty()) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
            textView.setText(string);
        }
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
            case NoteObject.NOTE:         isNote = true; break;
            case NoteObject.CARD: isPaymentinfo = true; break;
            case NoteObject.LOGIN:   isLogininfo = true; break;
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