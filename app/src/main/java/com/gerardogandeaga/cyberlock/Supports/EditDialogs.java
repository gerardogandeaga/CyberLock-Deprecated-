package com.gerardogandeaga.cyberlock.Supports;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gerardogandeaga.cyberlock.R;

public class EditDialogs extends AppCompatActivity implements View.OnClickListener
{
    private Context mContext;
    private AlertDialog.Builder mAlertDialog;
    private Dialog mDialog;

    private String mTmpLabel;
    private String mTmpColour;

    public EditDialogs(Context context) {
        mContext = context;
    }

    // SET LABEL
    public void createLabelDialog() {
        mAlertDialog = new AlertDialog.Builder(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 10);

        // BUILD VIEW
        final EditText etLabel = new EditText(mContext);

        etLabel.setLayoutParams(params);

        // BUILD DIALOG
        mAlertDialog.setCancelable(false);
        mAlertDialog.setTitle("Input A New Label");
        mAlertDialog.setView(etLabel);

        mAlertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mAlertDialog.setPositiveButton("SET", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setTmpLabel(etLabel.getText().toString());
                dialog.dismiss();
            }
        });
        mAlertDialog.show();
    }
    public String getTmpLabel() {
        if (mTmpLabel != null) {
            return mTmpLabel;
        } else {
            return "New Document";
        }
    }
    private void setTmpLabel(String tmpLabel) {
        mTmpLabel = tmpLabel;
    }

    // COLOUR TAG CUSTOMIZATION
    public void createColourPickDialog() {
        mDialog = new Dialog(mContext);

        // BUILD DIALOG
        mDialog.setContentView(R.layout.dialog_colourtag);
        mDialog.setCanceledOnTouchOutside(true);
        // BUTTONS
        ImageView blue = (ImageView) mDialog.findViewById(R.id.imgBlue);
        ImageView red = (ImageView) mDialog.findViewById(R.id.imgRed);
        ImageView green = (ImageView) mDialog.findViewById(R.id.imgGreen);
        ImageView yellow = (ImageView) mDialog.findViewById(R.id.imgYellow);
        ImageView purple = (ImageView) mDialog.findViewById(R.id.imgPurple);
        ImageView orange = (ImageView) mDialog.findViewById(R.id.imgOrange);

        blue.setOnClickListener(this);
        red.setOnClickListener(this);
        green.setOnClickListener(this);
        yellow.setOnClickListener(this);
        purple.setOnClickListener(this);
        orange.setOnClickListener(this);

        mDialog.show();
    }
    public String getTmpColour() {
        return mTmpColour;
    }
    private void setTmpColour(String tmpColour) {
        mTmpColour = tmpColour;
        mDialog.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBlue:
                setTmpColour("COL_BLUE");
                System.out.println("HI!");
                break;
            case R.id.imgRed:
                setTmpColour("COL_RED");
                break;
            case R.id.imgGreen:
                setTmpColour("COL_GREEN");
                break;
            case R.id.imgYellow:
                setTmpColour("COL_YELLOW");
                break;
            case R.id.imgPurple:
                setTmpColour("COL_PURPLE");
                break;
            case R.id.imgOrange:
                setTmpColour("COL_ORANGE");
                break;
            default:
                setTmpColour("DEFAULT");
                break;
        }
    }
}