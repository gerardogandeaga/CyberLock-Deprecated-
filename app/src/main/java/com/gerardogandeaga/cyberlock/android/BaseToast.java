package com.gerardogandeaga.cyberlock.android;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gerardogandeaga.cyberlock.R;

public class BaseToast {
    /*
    default = 1
    Alert state conditions :
    1 = information / notification
    2 = warning -> something potentially went wrong or can go wrong
    3 = error -> incorrect input or data lose */
    public static final int INFORMATION = 1;
    public static final int WARNING = 2;
    public static final int ERROR = 3;

    public static void buildAndShowToast(Context context, String message, int priority, int duration) {
        Toast toast = initializeToast(context, priority, duration);

        // custom toast view
        View view = View.inflate(context, R.layout.base_toast_view, null);
        TextView msg = view.findViewById(R.id.tvMessage);

        ((CardView) view.findViewById(R.id.container)).setCardBackgroundColor(priorityColour(context, priority));
        msg.setText(message);

        toast.setView(view);
        toast.show();
    }

    private static Toast initializeToast(Context context, int priority, int duration) {
        Toast toast = new Toast(context);
        toast.setDuration(duration);

        return toast;
    }

    private static int priorityColour(Context context, int  priority) {
        switch (priority) {
            case INFORMATION:
                return context.getResources().getColor(R.color.c_blue_40);
            case WARNING:
                return context.getResources().getColor(R.color.c_yellow_70);
            case ERROR:
                return context.getResources().getColor(R.color.c_red_60);
            default:
                return context.getResources().getColor(R.color.c_blue_40);
        }
    }
}
