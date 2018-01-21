package com.gerardogandeaga.cyberlock.support.graphics;

import android.app.ProgressDialog;
import android.content.Context;

public class CustomLoadDialog {
    private Context mContext;
    private ProgressDialog mProgressDialog;

    public CustomLoadDialog(Context context) {
        this.mContext = context;
    }

    public void indeterminateLoad(String message) {
        // if prograss dialog is not already null we nullify it
        if (this.mProgressDialog != null) {
            this.mProgressDialog = null;
        }

        // build dialog and its properties
        this.mProgressDialog = new ProgressDialog(mContext);
        this.mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        this.mProgressDialog.setCancelable(false);
        this.mProgressDialog.setMessage(message);
        this.mProgressDialog.show();
    }

    public void dismiss() {
        if (this.mProgressDialog != null) {
            this.mProgressDialog.dismiss();
            this.mProgressDialog = null;
        }
    }
}
