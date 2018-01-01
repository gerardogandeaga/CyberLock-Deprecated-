package com.gerardogandeaga.cyberlock.support.graphics;

import android.content.Context;
import android.graphics.Typeface;

public class Fonts {
    private static final String cabinBold = "CabinBold.ttf",
                                cabinRegular = "CabinRegular.ttf";

    private static final String sansNarrowWebBold = "PT_SansNarrowWebBold.ttf",
                                sansNarrowWebRegular = "PT_SansNarrowWebRegular.ttf";

    private static final String robotoMonoBold    = "RobotoMonoBold.ttf",
                                robotoMonoRegular = "RobotoMonoRegular.ttf",
                                robotoMonoThin    = "RobotoMonoThin.ttf";

    public static Typeface cabinBold(Context context) {
        return Typeface.createFromAsset(context.getAssets(), cabinBold);
    }
    public static Typeface cabinRegular(Context context) {
        return Typeface.createFromAsset(context.getAssets(), cabinRegular);
    }

    public static Typeface sansNarrowWebBold(Context context) {
        return Typeface.createFromAsset(context.getAssets(), sansNarrowWebBold);
    }
    public static Typeface sansNarrowWebRegular(Context context) {
        return Typeface.createFromAsset(context.getAssets(), sansNarrowWebRegular);
    }

    public static Typeface robotoMonoBold(Context context) {
        return Typeface.createFromAsset(context.getAssets(), robotoMonoBold);
    }
    public static Typeface robotoMonoRegular(Context context) {
        return Typeface.createFromAsset(context.getAssets(), robotoMonoRegular);
    }
    public static Typeface robotoMonoThin(Context context) {
        return Typeface.createFromAsset(context.getAssets(), robotoMonoThin);
    }
}