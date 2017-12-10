package com.gerardogandeaga.cyberlock.activities.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.activities.core.ActivityEdit;
import com.gerardogandeaga.cyberlock.crypto.CryptoContent;
import com.gerardogandeaga.cyberlock.sqlite.data.RawData;
import com.gerardogandeaga.cyberlock.support.handlers.RawDataHandler;

import static com.gerardogandeaga.cyberlock.support.LogoutProtocol.ACTIVITY_INTENT;

public class DialogDataPreview {
    private Context mContext;
    private CryptoContent mCryptoContent;

    // Data variables
    private RawData mRawData;

    // Views
    private AlertDialog.Builder mBuilder;

    public DialogDataPreview(Context context, RawData rawData) {
        this.mContext = context;
        this.mCryptoContent = new CryptoContent(mContext);
        this.mRawData = rawData;
    }

    // Select type
    public void initializeDialog() {
        switch (mRawData.getType(mCryptoContent)) {
            case "TYPE_NOTE":        constructPreviewNote(); break;
            case "TYPE_PAYMENTINFO": constructPreviewPaymentInfo(); break;
            case "TYPE_LOGININFO":   constructPreviewLoginInfo(); break;
        }
    }

    // Create dialog
    private void constructPreviewNote() {
        View view = View.inflate(mContext, R.layout.preview_note, null);
        RawDataHandler dataHandler = new RawDataHandler(mContext, mRawData);
        //
        TextView note = (TextView) view.findViewById(R.id.tvNote);
        TextView date = (TextView) view.findViewById(R.id.tvDate);

        note.setText(dataHandler.mNote);
        date.setText(dataHandler.mDate);

        mBuilder = new AlertDialog.Builder(mContext);
        mBuilder.setTitle(dataHandler.mLabel);
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
        View view = View.inflate(mContext, R.layout.preview_paymentinfo, null);
        RawDataHandler dataHandler = new RawDataHandler(mContext, mRawData);
        //
        TextView name = (TextView) view.findViewById(R.id.tvName);
        TextView number = (TextView) view.findViewById(R.id.tvNumber);
        TextView expiry = (TextView) view.findViewById(R.id.tvExpiry);
        TextView cvv = (TextView) view.findViewById(R.id.tvCVV);
        TextView cardType = (TextView) view.findViewById(R.id.tvCardType);
        TextView note = (TextView) view.findViewById(R.id.tvNote);
        TextView date = (TextView) view.findViewById(R.id.tvDate);

        name.setText(dataHandler.mName);
        number.setText(dataHandler.mNumber);
        expiry.setText(dataHandler.mExpiry);
        cvv.setText(dataHandler.mCVV);
        cardType.setText(dataHandler.mCardtype);
        note.setText(dataHandler.mNote);
        date.setText(dataHandler.mDate);
        cardType.setCompoundDrawables(null, null, dataHandler.mCardImage, null);

        mBuilder = new AlertDialog.Builder(mContext);
        mBuilder.setTitle(dataHandler.mLabel);
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
        View view = View.inflate(mContext, R.layout.preview_logininfo, null);
        RawDataHandler dataHandler = new RawDataHandler(mContext, mRawData);
        //
        TextView url = (TextView) view.findViewById(R.id.tvUrl);
        TextView email = (TextView) view.findViewById(R.id.tvEmail);
        TextView username = (TextView) view.findViewById(R.id.tvUsername);
        TextView password = (TextView) view.findViewById(R.id.tvPassword);
        TextView note = (TextView) view.findViewById(R.id.tvNote);
        TextView date = (TextView) view.findViewById(R.id.tvDate);

        url.setText(dataHandler.mUrl);
        email.setText(dataHandler.mEmail);
        username.setText(dataHandler.mUsername);
        password.setText(dataHandler.mPassword);
        note.setText(dataHandler.mNote);
        date.setText(dataHandler.mDate);

        mBuilder = new AlertDialog.Builder(mContext);
        mBuilder.setTitle(dataHandler.mLabel);
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
        ACTIVITY_INTENT.putExtra("data", mRawData);
        mContext.startActivity(ACTIVITY_INTENT);
    }
}
