package com.gerardogandeaga.cyberlock.views;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.View;
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
    3 = error -> incorrect input or data lose
    4 = success -> when a task has been successfully completed*/
    public static final int INFORMATION = 1;
    public static final int WARNING = 2;
    public static final int ERROR = 3;
    public static final int SUCCESS = 4;

    public static final int LENGTH_SHORT = Toast.LENGTH_SHORT;
    public static final int LENGTH_LONG = Toast.LENGTH_LONG;

    public static void buildAndShowToast(Context context, String message, int priority, int duration) {
        Toast toast = initializeToast(context, duration);

        // custom toast view
        View view = View.inflate(context, R.layout.custom_toast_view, null);
        TextView msg = view.findViewById(R.id.tvMessage);

        ((CardView) view.findViewById(R.id.container)).setCardBackgroundColor(priorityColour(priority));
        msg.setText(message);

        toast.setView(view);
        toast.show();
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
                return Res.getColour(R.color.c_yellow_70);
            case ERROR:
                return Res.getColour(R.color.c_red_60);
            case SUCCESS:
                return Res.getColour(R.color.c_green_30);
            default:
                return Res.getColour(R.color.c_blue_40);
        }
    }
}
