package com.gerardogandeaga.cyberlock.core.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.widget.ProgressBar;

import com.gerardogandeaga.cyberlock.custom.CustomDialog;

/**
 * @author gerardogandeaga
 *
 * quick dirty class to show load widgets anywhere
 */
public class LoadDialog {
    private Context mContext;

    private Dialog mDialog;

    public LoadDialog(Context context) {
        this.mContext = context;
    }

    /**
     * horizontal load bar
     */
    public void indeterminateProgress(String title) {
        // create indeterminate load widget
        final ProgressBar progressBar = new ProgressBar(mContext);
        progressBar.setIndeterminate(true);

        CustomDialog dialogBuilder = new CustomDialog(mContext);
        dialogBuilder.setTitle(title);
        dialogBuilder.setContentView(progressBar);

        this.mDialog = dialogBuilder.createDialog();
        mDialog.show();
    }

    public void dismiss() {

        if (mDialog != null) {
            mDialog.dismiss();
        }
    }
}
