package com.gerardogandeaga.cyberlock.activities.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.support.Stored;

public class DialogOptions extends DialogFragment implements View.OnClickListener {
    // fragment properties
    private static final String TAG = "DialogOptions";

    public interface OnInputListener {
        void sendInput(boolean bool);
    }
    public OnInputListener mOnInputListener;

    // instance vars
    private Context mContext;

    // spinners
    private String mAutoLogoutDelay;

    // widgets
    private android.support.v7.widget.SwitchCompat mSwAutoSave;
    private Spinner mSpLogoutDelay;
    private LinearLayout mInputChangePassword;
    private ImageView mImgDirection;
    private ImageView mImgListFormat;

    // fragment methods
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return buildDialog(getActivity());
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.mContext = context;

        try {
            mOnInputListener = (OnInputListener) getActivity();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

    }

    private MaterialDialog buildDialog(Context context) {
        // builder, title view and layout view
        final MaterialDialog.Builder builder;
        View view = View.inflate(context, R.layout.dialog_options, null);

        // title
        TextView title = view.findViewById(R.id.tvDialogTitle);
        title.setText("Application Options");

        // initialize widgets
        widgets(view);

        builder = new MaterialDialog.Builder(mContext);
        builder.customView(view, false);
        builder.negativeText("close");
        builder.negativeColor(mContext.getResources().getColor(R.color.black));
        builder.onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        });

        return builder.show();
    }
    private void widgets(View view) {
        LinearLayout linAutoSave = view.findViewById(R.id.AutoSave);
        LinearLayout linAutoLogoutDelay = view.findViewById(R.id.AutoLogoutDelay);
        LinearLayout linChangePassword = view.findViewById(R.id.ChangePassword);
        LinearLayout linListFormat = view.findViewById(R.id.ListFormat);
        LinearLayout linGitHub = view.findViewById(R.id.GitHub);
        LinearLayout linAbout = view.findViewById(R.id.About);


        linAutoSave.setOnClickListener(this);
        linAutoLogoutDelay.setOnClickListener(this);
        linChangePassword.setOnClickListener(this);
        linListFormat.setOnClickListener(this);
        linGitHub.setOnClickListener(this);
        linAbout.setOnClickListener(this);

        savedStates(view);
    }

    // saved states
    private void savedStates(View view) {
        iniAutoSave(view);
        iniAutoLogoutDelay(view);
        iniChangePassword(view);
        iniListFormat(view);
    }
    private void iniAutoSave(View view) {
        this.mSwAutoSave = view.findViewById(R.id.swAutoSave);

        this.mSwAutoSave.setChecked(Stored.getAutoSave(this.mContext));
    }
    private void iniAutoLogoutDelay(View view) {
        mSpLogoutDelay = view.findViewById(R.id.spAutoLogoutDelay);
        ArrayAdapter<CharSequence> adapterLogoutDelay = ArrayAdapter.createFromResource(
                        mContext,
                        R.array.AutoLogoutDelay_array,
                        R.layout.spinner_setting_text);
        adapterLogoutDelay.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        mSpLogoutDelay.setAdapter(adapterLogoutDelay);
        mSpLogoutDelay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object object = parent.getItemAtPosition(position);
                if (object != null) {
                    mAutoLogoutDelay = object.toString();
                    long time = 0;
                    switch (mAutoLogoutDelay) {
                        case "Immediate": time = 0; break;
                        case "1 Minute": time = 60000; break;
                        case "2 Minutes": time = 120000; break;
                        case "5 Minutes": time = 300000; break;
                        case "10 Minutes": time = 600000; break;
                        case "15 Minutes": time = 900000; break;
                        case "30 Minutes": time = 1800000; break;
                    }

                    System.out.println("Time = " + time);
                    Stored.setAutoLogoutDelay(mContext, time);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    private void iniChangePassword(View view) {
        this.mInputChangePassword = view.findViewById(R.id.inputChangePassword);
        this.mImgDirection = view.findViewById(R.id.imgDirection);

        mInputChangePassword.setVisibility(View.GONE);
        mImgDirection.setRotation(-90);
    }
    private void iniListFormat(View view) {
        this.mImgListFormat = view.findViewById(R.id.imgListFormat);

        mImgListFormat.setImageDrawable(mContext.getResources().getDrawable(R.drawable.graphic_list_linear));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // settings
            case R.id.AutoSave: onAutoSave(); break;
            case R.id.AutoLogoutDelay: onAutoLogoutDelay(); break;
            case R.id.ChangePassword: onChangePassword(); break;
            case R.id.ListFormat: onListFormat(); break;
        }
    }

    // on clicks
    private void onAutoSave() {
        Stored.setAutoSave(this.mContext, !Stored.getAutoSave(this.mContext));
        this.mSwAutoSave.setChecked(Stored.getAutoSave(this.mContext));
    }
    private void onAutoLogoutDelay() {
        mSpLogoutDelay.performClick();
    }
    private void onChangePassword() {
        if (mInputChangePassword.getVisibility() != View.GONE) {
            mInputChangePassword.setVisibility(View.GONE);
            mImgDirection.setRotation(-90);
        } else {
            mInputChangePassword.setVisibility(View.VISIBLE);
            mImgDirection.setRotation(90);
        }
    }
    private void onListFormat() {
        if (Stored.getListFormat(mContext).matches("RV_STAGGEREDGRID")) {
            Stored.setListFormat(mContext, "RV_LINEAR");
            mImgListFormat.setImageDrawable(mContext.getResources().getDrawable(R.drawable.graphic_list_linear));
            Toast.makeText(mContext, "Linear list format", Toast.LENGTH_SHORT).show();
            mOnInputListener.sendInput(true);
        } else {
            Stored.setListFormat(mContext, "RV_STAGGEREDGRID");
            mImgListFormat.setImageDrawable(mContext.getResources().getDrawable(R.drawable.graphic_list_grid));
            Toast.makeText(mContext, "Grid list format", Toast.LENGTH_SHORT).show();
            mOnInputListener.sendInput(false);
        }
    }
}
