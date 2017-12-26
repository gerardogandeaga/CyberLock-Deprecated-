package com.gerardogandeaga.cyberlock.activities.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.activities.core.ActivityEdit;
import com.gerardogandeaga.cyberlock.crypto.CryptoContent;
import com.gerardogandeaga.cyberlock.sqlite.data.RawDataPackage;
import com.gerardogandeaga.cyberlock.support.handlers.extractors.ContentHandler;

import static com.gerardogandeaga.cyberlock.support.LogoutProtocol.ACTIVITY_INTENT;

public class DialogDataPreview {
    private Context mContext;
    private CryptoContent mCryptoContent;

    // Data variables
    private RawDataPackage mRawDataPackage;

    // Views
    private AlertDialog.Builder mBuilder;

    public DialogDataPreview(Context context, RawDataPackage rawDataPackage) {
        this.mContext = context;
        this.mCryptoContent = new CryptoContent(mContext);
        this.mRawDataPackage = rawDataPackage;
    }

    // Select type
    public void initializeDialog() {
        switch (mRawDataPackage.getType(mCryptoContent)) {
            case "TYPE_NOTE":        constructPreviewNote(); break;
            case "TYPE_PAYMENTINFO": constructPreviewPaymentInfo(); break;
            case "TYPE_LOGININFO":   constructPreviewLoginInfo(); break;
        }
    }

    // Create dialog
    private void constructPreviewNote() {
        View titleView = View.inflate(mContext, R.layout.dialog_title, null);
        View view = View.inflate(mContext, R.layout.preview_note, null);
        ContentHandler dataHandler = new ContentHandler(mContext, mRawDataPackage);
        //
        TextView title = titleView.findViewById(R.id.tvDialogTitle);
        TextView date = titleView.findViewById(R.id.tvDate);
        TextView note = view.findViewById(R.id.tvNote);

        title.setText(dataHandler.mLabel);
        date.setText("Updated: " + dataHandler.mDate);
        note.setText(dataHandler.mNote);

        mBuilder = new AlertDialog.Builder(mContext);
        mBuilder.setCustomTitle(titleView);
        mBuilder.setView(view);
        mBuilder.setNegativeButton(R.string.btnDone, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mBuilder.setPositiveButton(R.string.btnEdit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                onEdit();
            }
        });
        mBuilder.create();
        mBuilder.show();
    }
    private void constructPreviewPaymentInfo() {
        View titleView = View.inflate(mContext, R.layout.dialog_title, null);
        View view = View.inflate(mContext, R.layout.preview_paymentinfo, null);
        ContentHandler dataHandler = new ContentHandler(mContext, mRawDataPackage);
        //
        TextView title = titleView.findViewById(R.id.tvDialogTitle);
        TextView date = titleView.findViewById(R.id.tvDate);
        TextView name = view.findViewById(R.id.tvName);
        TextView number = view.findViewById(R.id.tvNumber);
        TextView expiry = view.findViewById(R.id.tvExpiry);
        TextView cvv = view.findViewById(R.id.tvCVV);
        TextView cardType = view.findViewById(R.id.tvCardType);
        TextView note = view.findViewById(R.id.tvNote);
        ImageView icon = view.findViewById(R.id.imgIcon);

        title.setText(dataHandler.mLabel);
        date.setText("Updated: " + dataHandler.mDate);
        name.setText(dataHandler.mName);
        number.setText(dataHandler.mNumber);
        expiry.setText(dataHandler.mExpiry);
        cvv.setText(dataHandler.mCVV);
        cardType.setText(dataHandler.mCardType);
        note.setText(dataHandler.mNote);
        icon.setImageDrawable(dataHandler.mCardImage);

        mBuilder = new AlertDialog.Builder(mContext);
        mBuilder.setCustomTitle(titleView);
        mBuilder.setView(view);
        mBuilder.setNegativeButton(R.string.btnDone, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mBuilder.setPositiveButton(R.string.btnEdit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                onEdit();
            }
        });
        mBuilder.create();
        mBuilder.show();
    }
    private void constructPreviewLoginInfo() {
        View titleView = View.inflate(mContext, R.layout.dialog_title, null);
        View view = View.inflate(mContext, R.layout.preview_logininfo, null);
        ContentHandler dataHandler = new ContentHandler(mContext, mRawDataPackage);
        //
        TextView title = titleView.findViewById(R.id.tvDialogTitle);
        TextView date = titleView.findViewById(R.id.tvDate);
        TextView url = view.findViewById(R.id.tvUrl);
        TextView email = view.findViewById(R.id.tvEmail);
        TextView username = view.findViewById(R.id.tvUsername);
        TextView password = view.findViewById(R.id.tvPassword);
        TextView note = view.findViewById(R.id.tvNote);

        title.setText(dataHandler.mLabel);
        date.setText("Updated: " + dataHandler.mDate);
        url.setText(dataHandler.mUrl);
        email.setText(dataHandler.mEmail);
        username.setText(dataHandler.mUsername);
        password.setText(dataHandler.mPassword);
        note.setText(dataHandler.mNote);

        mBuilder = new AlertDialog.Builder(mContext);
        mBuilder.setCustomTitle(titleView);
        mBuilder.setView(view);
        mBuilder.setNegativeButton(R.string.btnDone, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mBuilder.setPositiveButton(R.string.btnEdit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                onEdit();
            }
        });
        mBuilder.create();
        mBuilder.show();
    }

    // Edit data package
    private void onEdit() {
        ACTIVITY_INTENT = new Intent(mContext, ActivityEdit.class);
        ACTIVITY_INTENT.putExtra("data", mRawDataPackage);
        mContext.startActivity(ACTIVITY_INTENT);
    }
}
