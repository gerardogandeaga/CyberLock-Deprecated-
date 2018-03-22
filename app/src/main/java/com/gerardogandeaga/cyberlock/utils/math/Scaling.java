package com.gerardogandeaga.cyberlock.utils.math;

import android.content.Context;
import android.util.DisplayMetrics;

public class Scaling {

    public static int dpFromPx(Context context, int dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (metrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int pxFromDp(Context context, int px) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return Math.round(px / (metrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
