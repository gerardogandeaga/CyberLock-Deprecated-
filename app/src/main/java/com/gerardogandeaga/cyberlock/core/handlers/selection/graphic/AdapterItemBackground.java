package com.gerardogandeaga.cyberlock.core.handlers.selection.graphic;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

public class AdapterItemBackground {

    /*
    this function returns a drawable state list, it item it gets applied to will change colour or background
    depending on its state. ie. (STATE_PRESSED, anything else)
    */
    public static StateListDrawable getItemDrawableStates(Context context, int color_unselected, int color_selected, boolean animate) {
        StateListDrawable stateList = new StateListDrawable();

        // Base colors
        // Selected state must be first
        try {
            ColorDrawable selectedDrawable = new ColorDrawable(context.getResources().getColor(color_selected));
            stateList.addState(new int[] {android.R.attr.state_selected}, selectedDrawable);
        } catch (Exception e) {
            System.out.println("could not find colour value, switching to Drawable...");
            Drawable selectedDrawable = context.getResources().getDrawable(color_selected);
            stateList.addState(new int[] {android.R.attr.state_selected}, selectedDrawable);
        }

        // Unselected state mut be last
        try {
            ColorDrawable unselectedDrawable = new ColorDrawable(context.getResources().getColor(color_unselected));
            stateList.addState(new int[] { /* any other case */ }, unselectedDrawable);
        } catch (Exception e) {
            System.out.println("could not find colour value, switching to Drawable...");
            Drawable unselectedDrawable = context.getResources().getDrawable(color_unselected);
            stateList.addState(new int[] { /* any other case */ }, unselectedDrawable);
        }

        if (animate) {
            int duration = context.getResources().getInteger(android.R.integer.config_shortAnimTime);
            stateList.setEnterFadeDuration(duration);
            stateList.setExitFadeDuration(duration);
        }

        return stateList;
    }
}
