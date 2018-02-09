package com.gerardogandeaga.cyberlock.activities.dialogs;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;

public class DialogCustomLoad {
    private Context mContext;

    private MaterialDialog mDialog;

    public DialogCustomLoad(Context context) {
        this.mContext = context;
    }

    public void indeterminateProgress(String str) {

        final MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext);
        builder.content(str);
        builder.progress(true, 0);
        builder.progressIndeterminateStyle(true);
        builder.cancelable(false);

        this.mDialog = builder.build();
        mDialog.show();
    }

    public void dismiss() {

        if (mDialog != null) {
            mDialog.dismiss();

        }
    }
}
