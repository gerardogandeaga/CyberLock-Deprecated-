package com.gerardogandeaga.cyberlock.activities.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.activities.core.ActivityEdit;
import com.gerardogandeaga.cyberlock.activities.core.edit.EditGraphics;

public class DialogColourTag implements View.OnClickListener {
    private Context mContext;
    private static final String[] ARGS = new String[]{
            "COL_BLUE", "COL_RED", "COL_GREEN", "COL_YELLOW", "COL_PURPLE", "COL_ORANGE", "DEFAULT"};

    private static String mColour;

    private EditGraphics mEditGraphics;

    private AlertDialog mAlertDialog;
    private ImageView mImageView;

    public DialogColourTag(Context context, EditGraphics editGraphics, ImageView imageView) {
        this.mContext = context;
        this.mEditGraphics = editGraphics;
        this.mImageView = imageView;
    }

    public void initializeDialog() {
        View dv = View.inflate(mContext, R.layout.dialog_colourtag, null);
        ImageView blue = dv.findViewById(R.id.imgBlue);
        ImageView red = dv.findViewById(R.id.imgRed);
        ImageView green = dv.findViewById(R.id.imgGreen);
        ImageView yellow = dv.findViewById(R.id.imgYellow);
        ImageView purple = dv.findViewById(R.id.imgPurple);
        ImageView orange = dv.findViewById(R.id.imgOrange);

        blue.setOnClickListener(this);
        red.setOnClickListener(this);
        green.setOnClickListener(this);
        yellow.setOnClickListener(this);
        purple.setOnClickListener(this);
        orange.setOnClickListener(this);

        // Dialog builder
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(dv);
        // Dialog show
        this.mAlertDialog = builder.show();
        this.mAlertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBlue:   mColour = ARGS[0]; setEditMainColour(); break;
            case R.id.imgRed:    mColour = ARGS[1]; setEditMainColour(); break;
            case R.id.imgGreen:  mColour = ARGS[2]; setEditMainColour(); break;
            case R.id.imgYellow: mColour = ARGS[3]; setEditMainColour(); break;
            case R.id.imgPurple: mColour = ARGS[4]; setEditMainColour(); break;
            case R.id.imgOrange: mColour = ARGS[5]; setEditMainColour(); break;
            default:             mColour = ARGS[6]; setEditMainColour(); break;
        }
        mEditGraphics.alterTagColour(mImageView, mColour);
        mAlertDialog.dismiss();
    }

    private void setEditMainColour() {
        ActivityEdit.mColour = mColour;
    }
}
