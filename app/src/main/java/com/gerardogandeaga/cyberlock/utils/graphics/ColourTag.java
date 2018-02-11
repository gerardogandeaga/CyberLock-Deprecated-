package com.gerardogandeaga.cyberlock.utils.graphics;

import android.content.Context;

import com.gerardogandeaga.cyberlock.R;

public class ColourTag {

    public static int colourTag(Context context, String colour) {
        return baseColourSearch(context, colour, true);
    }

    public static int colourTagListView(Context context, String colour) {
        return baseColourSearch(context, colour, false);
    }

    private static int baseColourSearch(Context context, String colour, boolean darkDefault) {
        if (colour != null) {
            final int[] colours = context.getResources().getIntArray(R.array.array_tag_colours);
            final String[] colourNames = context.getResources().getStringArray(R.array.array_tag_colours_names);

            for (int i = 0; i < colourNames.length; i++) {
                if (colour.matches(colourNames[i])) {
                    return colours[i];
                }
            }
        }
        // light or dark default colour
        if (darkDefault) {
            return context.getResources().getColor(R.color.black);
        } else {
            return context.getResources().getColor(R.color.ct_default);
        }
    }
}
