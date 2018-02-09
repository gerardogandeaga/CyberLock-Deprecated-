package com.gerardogandeaga.cyberlock.activities.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gerardogandeaga.cyberlock.R;

public class DialogFragmentTags extends DialogFragment implements View.OnClickListener {
    // fragment properties
    private static final String TAG = "DialogFragmentTags";

    public interface OnInputListener {
        void sendInput(String colour);
    }
    public OnInputListener mOnInputListener;

    // instance vars
    private Context mContext;
    private Dialog mDialog;

    public static void show(AppCompatActivity context) {
        DialogFragmentTags dialogFragmentTags = new DialogFragmentTags();
        dialogFragmentTags.show(context.getFragmentManager(), TAG);
    }

    // fragment methods
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return buildDialog();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.mContext = context;

        try {
            this.mOnInputListener = (OnInputListener) getActivity();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private Dialog buildDialog() {
        View view = View.inflate(mContext, R.layout.dialog_colourtag, null);
        // title
        TextView title = view.findViewById(R.id.tvDialogTitle);
//        ImageView icon = view.findViewById(R.id.imgDialogIcon);
        // content
        ImageView blue = view.findViewById(R.id.imgBlue);
        ImageView red = view.findViewById(R.id.imgRed);
        ImageView green = view.findViewById(R.id.imgGreen);
        ImageView yellow = view.findViewById(R.id.imgYellow);
        ImageView purple = view.findViewById(R.id.imgPurple);
        ImageView orange = view.findViewById(R.id.imgOrange);

        blue.setOnClickListener(this);
        red.setOnClickListener(this);
        green.setOnClickListener(this);
        yellow.setOnClickListener(this);
        purple.setOnClickListener(this);
        orange.setOnClickListener(this);

        title.setText("Colour Picker");
//        icon.setVisibility(View.VISIBLE);
//        icon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_tag));
        this.mDialog = new AlertDialog.Builder(mContext)
                .setView(view)
                .setPositiveButton(R.string.btnDone, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.btnCancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNeutralButton(R.string.btnCustom, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return mDialog;
    }

    @Override
    public void onClick(View view) {
        String colorHex;
        switch (view.getId()) {
            case R.id.imgBlue:   colorHex = "ct_blue"; break;
            case R.id.imgRed:    colorHex = "ct_red"; break;
            case R.id.imgGreen:  colorHex = "ct_green"; break;
            case R.id.imgYellow: colorHex = "ct_yellow"; break;
            case R.id.imgPurple: colorHex = "ct_purple"; break;
            case R.id.imgOrange: colorHex = "ct_orange"; break;
            default:             colorHex = "ct_default"; break;
        }
        mOnInputListener.sendInput(colorHex);
        mDialog.dismiss();
    }
}
