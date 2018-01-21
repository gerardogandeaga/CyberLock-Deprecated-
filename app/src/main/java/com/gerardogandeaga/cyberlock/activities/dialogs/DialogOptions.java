package com.gerardogandeaga.cyberlock.activities.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.support.Stored;

public class DialogOptions implements View.OnClickListener {
    private Context mContext;
    private SharedPreferences mSharedPreferences;

    // spinners
    private ArrayAdapter<CharSequence> mAdapterAutoLogoutDelay;
    private String mAutoLogoutDelay, mOldEncryptionAlgorithm;

    // widgets
    private android.support.v7.widget.SwitchCompat mSwAutoSave;
    private Spinner mSpAutoLogoutDelay;

    public DialogOptions(Context context) {
        this.mContext = context;
        buildDialog(this.mContext);
    }

    private void buildDialog(Context context) {
        AlertDialog.Builder builder;
        View titleView = View.inflate(context, R.layout.dialog_title, null);
        View view = View.inflate(context, R.layout.dialog_options, null);

        TextView title = titleView.findViewById(R.id.tvDialogTitle);
        titleView.findViewById(R.id.tvDate).setVisibility(View.GONE);

        title.setText("Application Options");

        widgets(view);

        builder = new AlertDialog.Builder(context, R.style.MyDialogStyle);
        builder.setCustomTitle(titleView);
        builder.setView(view);
        builder.setNegativeButton(R.string.btnDone, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create();
        builder.show();
    }

    private void widgets(View view) {
        LinearLayout linAutoSave = view.findViewById(R.id.AutoSave);
        LinearLayout linAutoLogoutDelay = view.findViewById(R.id.AutoLogoutDelay);
        LinearLayout linChangePassword = view.findViewById(R.id.ChangePassword);
        LinearLayout linGitHub = view.findViewById(R.id.GitHub);
        LinearLayout linAbout = view.findViewById(R.id.About);


        linAutoSave.setOnClickListener(this);
        linAutoLogoutDelay.setOnClickListener(this);
        linChangePassword.setOnClickListener(this);
        linGitHub.setOnClickListener(this);
        linAbout.setOnClickListener(this);

        savedStates(view);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.AutoSave: onAutoSave(); break;
        }
    }

    // saved states
    private void savedStates(View view) {
        iniAutoSave(view);
        iniAutoLogoutDelay(view);
    }
    private void iniAutoSave(View view) {
        this.mSwAutoSave = view.findViewById(R.id.swAutoSave);
        this.mSwAutoSave.setChecked(Stored.getAutoSave(this.mContext));
    }
    private void iniAutoLogoutDelay(View view) {
        this.mSpAutoLogoutDelay = view.findViewById(R.id.spAutoLogoutDelay);
        this.mAdapterAutoLogoutDelay =
                ArrayAdapter.createFromResource(
                this.mContext,
                R.array.AutoLogoutDelay_array,
                R.layout.spinner_setting_text);
        this.mAdapterAutoLogoutDelay.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        this.mSpAutoLogoutDelay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object object = parent.getItemAtPosition(position);
                if (object != null) {
                    mAutoLogoutDelay = object.toString();
                    long time = 0;
                    switch (mAutoLogoutDelay) {
                        case "Immediate": time = 0; break;
                        case "15 Seconds": time = 15000; break;
                        case "30 Seconds": time = 30000; break;
                        case "1 Minute": time = 60000; break;
                        case "5 Minutes": time = 300000; break;
                        case "10 Minutes": time = 600000; break;
                        case "30 Minutes": time = 1800000; break;
                        case "1 Hour": time = 3600000; break;
                        case "2 Hours": time = 7200000; break;
                        case "Never": break;
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

    // on clicks
    private void onAutoSave() {
        Stored.setAutoSave(this.mContext, !Stored.getAutoSave(this.mContext));
        this.mSwAutoSave.setChecked(Stored.getAutoSave(this.mContext));
    }
    private void onAutoLogoutDelay() {

    }
}
