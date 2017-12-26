package com.gerardogandeaga.cyberlock.support.handlers.selection.graphic;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;

public class AdapterItemGraphics {

    public static StateListDrawable getItemDrawableStates(Context context, int color_unselected, int color_selected, boolean animate) {
        StateListDrawable stateList = new StateListDrawable();

        // Base colors
        ColorDrawable unselectedDrawable = new ColorDrawable(context.getResources().getColor(color_unselected));
        ColorDrawable selectedDrawable = new ColorDrawable(context.getResources().getColor(color_selected));

        stateList.addState(new int[] {android.R.attr.state_selected}, selectedDrawable); // If state is selected
        stateList.addState(new int[] { /* any other case */ }, unselectedDrawable);

        // If animate is true
        if (animate) {
            int duration = context.getResources().getInteger(android.R.integer.config_shortAnimTime);
            stateList.setEnterFadeDuration(duration);
            stateList.setExitFadeDuration(duration);
        }

        return stateList;
    }
}
