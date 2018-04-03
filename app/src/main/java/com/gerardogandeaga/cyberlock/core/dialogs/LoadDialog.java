package com.gerardogandeaga.cyberlock.core.dialogs;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;

// todo remove this DialogLoad class and replace with a dialog wrapper that has a custom load inside of it
/**
 * @author gerardogandeaga
 */
public class LoadDialog {
    private Context mContext;

    private MaterialDialog mDialog;

    public LoadDialog(Context context) {
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
