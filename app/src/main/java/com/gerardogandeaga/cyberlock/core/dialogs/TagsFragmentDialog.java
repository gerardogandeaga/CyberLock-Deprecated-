package com.gerardogandeaga.cyberlock.core.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.views.CustomDialog;
import com.gerardogandeaga.cyberlock.utils.Resources;
import com.gerardogandeaga.cyberlock.utils.math.Scaling;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author gerardogandeaga
 */
public class TagsFragmentDialog extends DialogFragment {
    // fragment properties
    private static final String TAG = "TagsFragmentDialog";

    public interface OnColourSelected {
        void sendInput(String colour);
    }
    public OnColourSelected mOnColourSelected;

    // instance vars
    private Dialog mDialog;

    private static final int ROWS = 4;
    private static final int COLUMNS = 4;

    @Override
    public void onStart() {
        super.onStart();
        // callback listener
        try {
            this.mOnColourSelected = (OnColourSelected) getActivity();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    public static void show(AppCompatActivity context) {
        TagsFragmentDialog tagsFragmentDialog = new TagsFragmentDialog();
        tagsFragmentDialog.show(context.getFragmentManager(), TAG);
    }

    // fragment methods
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return buildDialog();
    }

    private Dialog buildDialog() {
        final CustomDialog customDialog = new CustomDialog(getActivity());
        customDialog.setContentView(buildViews());
        customDialog.setTitle("Colour Palette");
        customDialog.setNegativeButton("Cancel", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });

        this.mDialog = customDialog.createDialog();
        return mDialog;
    }

    private View buildViews() {
        final int[] colours = getResources().getIntArray(R.array.arr_tag_colours);
        final String[] names = getResources().getStringArray(R.array.arr_tag_colours_names);
        int offset = 0;

        // sub layout params
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        // circle item params
        LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT);
        itemParams.weight = 1f;

        // master layout to be returned
        LinearLayout container = new LinearLayout(getActivity());
        container.setLayoutParams(layoutParams);
        container.setOrientation(LinearLayout.VERTICAL);

        // iterate per row -> 4
        for (int i = 0; i < ROWS; i++) {
            int padding = Scaling.dpFromPx(getActivity(), 10);

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
                item.setImageDrawable(Resources.getDrawable(getActivity(), R.drawable.graphic_circle_filled));
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
        if (mOnColourSelected != null) {
            mOnColourSelected.sendInput(name);
        } else {
            System.out.println("listener is null!!!");
        }
        mDialog.dismiss();
    }
}
