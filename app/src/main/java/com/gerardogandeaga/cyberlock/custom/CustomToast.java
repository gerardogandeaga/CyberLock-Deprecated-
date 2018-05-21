package com.gerardogandeaga.cyberlock.custom;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.utils.Res;

/**
 * @author gerardogandeaga
 */
public class CustomToast {
    /*
    default = 1
    Alert state conditions :
    1 = information / notification
    2 = warning -> something potentially went wrong or can go wrong
    3 = error -> incorrect input or data loss
    4 = success -> when a task has been successfully completed */
    public static final int INFORMATION = 1;
    public static final int WARNING = 2;
    public static final int ERROR = 3;
    public static final int SUCCESS = 4;

    public static final int LENGTH_SHORT = Toast.LENGTH_SHORT;
    public static final int LENGTH_LONG = Toast.LENGTH_LONG;

    public static void buildAndShowToast(Context context, String message, int priority, int duration) {
        final Toast toast = initializeToast(context, duration);

        // custom toast view
        View view = View.inflate(context, R.layout.custom_toast_view, null);

        ((CardView) view.findViewById(R.id.container)).setCardBackgroundColor(priorityColour(priority));
        ((ImageView) view.findViewById(R.id.imgIcon)).setImageDrawable(priorityIcon(priority));
        ((TextView) view.findViewById(R.id.tvMessage)).setText(message);

        // dismiss toast when clicked
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast.cancel();
            }
        });
        toast.setView(view);
        toast.show();
    }

    public static void buildAndShowToast(Context context, String message, int duration) {
        buildAndShowToast(context, message, INFORMATION, duration);
    }

    public static void buildAndShowToast(Context context, String message) {
        buildAndShowToast(context, message, INFORMATION, LENGTH_SHORT);
    }

    private static Toast initializeToast(Context context, int duration) {
        Toast toast = new Toast(context);
        toast.setDuration(duration);

        return toast;
    }

    private static int priorityColour(int priority) {
        switch (priority) {
            case INFORMATION:
                return Res.getColour(R.color.black);
            case WARNING:
                return Res.getColour(R.color.c_orange_50);
            case ERROR:
                return Res.getColour(R.color.c_red_60);
            case SUCCESS:
                return Res.getColour(R.color.c_green_40);
            default:
                return Res.getColour(R.color.c_blue_40);
        }
    }

    private static Drawable priorityIcon(int priority) {
        switch (priority) {
            case INFORMATION:
                return Res.getDrawable(R.drawable.ic_info);
            case WARNING:
                return Res.getDrawable(R.drawable.ic_warning);
            case ERROR:
                return Res.getDrawable(R.drawable.ic_error);
            case SUCCESS:
                return Res.getDrawable(R.drawable.ic_check);
            default:
                return Res.getDrawable(R.drawable.ic_options_hor);
        }
    }
}
