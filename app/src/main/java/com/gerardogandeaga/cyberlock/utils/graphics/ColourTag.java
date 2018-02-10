package com.gerardogandeaga.cyberlock.utils.graphics;

import android.content.Context;

import com.gerardogandeaga.cyberlock.R;

public class ColourTag {
    private static final String DEFAULT_COLOUR = "#000000";
    private static final String DEFAULT_COLOUR_LISTVIEW = "#d5d5d5";

    public static int colourTag(Context context, String HEX) {
        switch (HEX) {
            case "ct_blue":   return context.getResources().getColor(R.color.ct_blue_4);
            case "ct_red":    return context.getResources().getColor(R.color.ct_red_2);
            case "ct_green":  return context.getResources().getColor(R.color.ct_green_2);
            case "ct_yellow": return context.getResources().getColor(R.color.ct_yellow_1);
            case "ct_purple": return context.getResources().getColor(R.color.ct_purple_1);
            case "ct_orange": return context.getResources().getColor(R.color.ct_orange_2);
            default:          return context.getResources().getColor(R.color.black);
        }
//        try {
//            return Color.parseColor(HEX);
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//            return Color.parseColor(DEFAULT_COLOUR);
//        }
    }

    public static int colourTagListView(Context context, String HEX) {
        switch (HEX) {
            case "ct_blue":   return context.getResources().getColor(R.color.ct_blue_4);
            case "ct_red":    return context.getResources().getColor(R.color.ct_red_2);
            case "ct_green":  return context.getResources().getColor(R.color.ct_green_2);
            case "ct_yellow": return context.getResources().getColor(R.color.ct_yellow_1);
            case "ct_purple": return context.getResources().getColor(R.color.ct_purple_1);
            case "ct_orange": return context.getResources().getColor(R.color.ct_orange_2);
            default:          return context.getResources().getColor(R.color.ct_default);
        }
//        try {
//            if (HEX.matches(DEFAULT_COLOUR)) {
//                return Color.parseColor(DEFAULT_COLOUR_LISTVIEW);
//            } else {
//                return Color.parseColor(HEX);
//            }
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//            return Color.parseColor(DEFAULT_COLOUR_LISTVIEW);
//        }
    }
}
