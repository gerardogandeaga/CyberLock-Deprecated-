package com.gerardogandeaga.cyberlock.core.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.custom.CustomDialog;
import com.gerardogandeaga.cyberlock.utils.Res;
import com.gerardogandeaga.cyberlock.utils.Scale;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author gerardogandeaga
 *
 * colour picker dialog fragment
 */
public class ColourPaletteDialogFragment extends DialogFragment {
    // fragment properties
    private static final String TAG = "ColourPaletteDialogFragment";

    /**
     * interface that sends colour selection callbacks
     */
    public interface ColourSelectionCallBack {
        void onColorSelected(String colour);
    }
    private ColourSelectionCallBack mColourSelectionCallBack;

    private static final int ROWS = 4;
    private static final int COLUMNS = 4;

    @Override
    public void onStart() {
        super.onStart();
        // callback listener
        try {
            this.mColourSelectionCallBack = (ColourSelectionCallBack) getActivity();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    public static void show(Activity activity) {
        new ColourPaletteDialogFragment().show(activity.getFragmentManager(), TAG);
    }

    // fragment methods
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final CustomDialog customDialog = new CustomDialog(getActivity());
        customDialog.setContentView(buildPaletteView());
        customDialog.setTitle("Colour Palette");
        customDialog.setNegativeButton("Cancel", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return customDialog.createDialog();
    }

    private View buildPaletteView() {
        final int[] colours = getResources().getIntArray(R.array.arr_tag_colours);
        final String[] names = getResources().getStringArray(R.array.arr_tag_colours_names);
        int offset = 0;

        // sub layout params
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        // circle item params
        LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemParams.weight = 1f;

        // master layout to be returned
        LinearLayout container = new LinearLayout(getActivity());
        container.setLayoutParams(layoutParams);
        container.setOrientation(LinearLayout.VERTICAL);

        // iterate per row -> 4
        for (int i = 0; i < ROWS; i++) {
            int padding = Scale.dpFromPx(getActivity(), 10);

            // sub/row layout
            LinearLayout subLayout = new LinearLayout(getActivity());
            subLayout.setLayoutParams(layoutParams);
            subLayout.setOrientation(LinearLayout.HORIZONTAL);
            subLayout.setPadding(padding, padding, padding, padding);

            // iterate per column -> 4
            for (int j = 0; j < COLUMNS; j++) {
                final int finalOffset = j + offset;

                // create colour image
                final CircleImageView item = new CircleImageView(getActivity());
                item.setLayoutParams(itemParams);
                item.setImageDrawable(Res.getDrawable(R.drawable.ic_circle_filled));
                item.setColorFilter(colours[finalOffset], PorterDuff.Mode.SRC_ATOP);
                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onItemClick(names[finalOffset]);
                    }
                });

                // add to sub view
                subLayout.addView(item);
            }
            // add sub layout to container
            container.addView(subLayout);

            // increment offset for next round
            offset += 4;
        }
        return container;
    }

    private void onItemClick(String name) {
        if (mColourSelectionCallBack != null) {
            mColourSelectionCallBack.onColorSelected(name);
        } else {
            System.out.println("listener is null!!!");
        }
        dismiss();
    }
}
