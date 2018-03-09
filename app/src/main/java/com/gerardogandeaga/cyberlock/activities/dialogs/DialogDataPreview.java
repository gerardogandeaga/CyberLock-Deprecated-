package com.gerardogandeaga.cyberlock.activities.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.activities.core.ActivityEdit;
import com.gerardogandeaga.cyberlock.core.handlers.extractors.ContentHandler;
import com.gerardogandeaga.cyberlock.database.DataPackage;
import com.gerardogandeaga.cyberlock.dialog.BaseDialog;
import com.gerardogandeaga.cyberlock.utils.Res;
import com.gerardogandeaga.cyberlock.utils.graphics.Graphics;
import com.gerardogandeaga.cyberlock.utils.views.ViewHandler;

import static com.gerardogandeaga.cyberlock.utils.security.LogoutProtocol.ACTIVITY_INTENT;

public class DialogDataPreview {
    private Context mContext;

    // Data variables
    private DataPackage mDataPackage;

    // Views
    private Dialog mDialog;

    public DialogDataPreview(Context context, DataPackage dataPackage) {
        this.mContext = context;
        this.mDataPackage = dataPackage;
    }

    // Select type
    public void initializeDialog() {
        switch (mDataPackage.getType()) {
            case DataPackage.NOTE:         constructPreviewNote(); break;
            case DataPackage.PAYMENT_INFO: constructPreviewPaymentInfo(); break;
            case DataPackage.LOGIN_INFO:   constructPreviewLoginInfo(); break;
        }
    }

    // Create dialog
    private void constructPreviewNote() {
        View view = View.inflate(mContext, R.layout.preview_note, null);
        ContentHandler contentHandler = new ContentHandler(mContext, mDataPackage);
        //
        TextView note = view.findViewById(R.id.tvNote);

        note.setText(contentHandler.mNote);

        buildDialog(view, contentHandler);
    }
    private void constructPreviewPaymentInfo() {
        View view = View.inflate(mContext, R.layout.preview_paymentinfo, null);
        ContentHandler contentHandler = new ContentHandler(mContext, mDataPackage);
        //
        LinearLayout Holder = view.findViewById(R.id.Holder);
        LinearLayout Number = view.findViewById(R.id.Number);
        LinearLayout Expiry = view.findViewById(R.id.Expiry);
        LinearLayout CVV = view.findViewById(R.id.CVV);
        LinearLayout CardType = view.findViewById(R.id.CardType);
        LinearLayout Notes = view.findViewById(R.id.Notes);

        TextView holder = view.findViewById(R.id.tvName);
        TextView number = view.findViewById(R.id.tvNumber);
        TextView expiry = view.findViewById(R.id.tvExpiry);
        TextView cvv = view.findViewById(R.id.tvCVV);
        TextView cardType = view.findViewById(R.id.tvCardType);
        TextView notes = view.findViewById(R.id.tvNote);

        if (ViewHandler.setLinearLayoutVisibility(Holder, contentHandler.mHolder))     holder.setText(contentHandler.mHolder);
        if (ViewHandler.setLinearLayoutVisibility(Number, contentHandler.mNumber))     number.setText(contentHandler.mNumber);
        if (ViewHandler.setLinearLayoutVisibility(Expiry, contentHandler.mExpiry))     expiry.setText(contentHandler.mExpiry);
        if (ViewHandler.setLinearLayoutVisibility(CVV, contentHandler.mCVV))           cvv.setText(contentHandler.mCVV);
        if (ViewHandler.setLinearLayoutVisibility(CardType, contentHandler.mCardType)) cardType.setText(contentHandler.mCardType);
        if (ViewHandler.setLinearLayoutVisibility(Notes, contentHandler.mNote))        notes.setText(contentHandler.mNote);

        buildDialog(view, contentHandler);
    }
    private void constructPreviewLoginInfo() {
        View view = View.inflate(mContext, R.layout.preview_logininfo, null);
        ContentHandler contentHandler = new ContentHandler(mContext, mDataPackage);
        //
        LinearLayout Url = view.findViewById(R.id.Url);
        LinearLayout Email = view.findViewById(R.id.Email);
        LinearLayout Username = view.findViewById(R.id.Username);
        LinearLayout Password = view.findViewById(R.id.Password);
        LinearLayout Notes = view.findViewById(R.id.Notes);

        TextView url = view.findViewById(R.id.tvUrl);
        TextView email = view.findViewById(R.id.tvEmail);
        TextView username = view.findViewById(R.id.tvUsername);
        TextView password = view.findViewById(R.id.tvPassword);
        TextView notes = view.findViewById(R.id.tvNote);

        if (ViewHandler.setLinearLayoutVisibility(Url, contentHandler.mUrl))           url.setText(contentHandler.mUrl);
        if (ViewHandler.setLinearLayoutVisibility(Email, contentHandler.mEmail))       email.setText(contentHandler.mEmail);
        if (ViewHandler.setLinearLayoutVisibility(Username, contentHandler.mUsername)) username.setText(contentHandler.mUsername);
        if (ViewHandler.setLinearLayoutVisibility(Password, contentHandler.mPassword)) password.setText(contentHandler.mPassword);
        if (ViewHandler.setLinearLayoutVisibility(Notes, contentHandler.mNote))        notes.setText(contentHandler.mNote);

        buildDialog(view, contentHandler);
    }

    private void buildDialog(View view, ContentHandler contentHandler) {
        BaseDialog baseDialog = new BaseDialog(mContext);
        baseDialog.setContentView(view);
        if (contentHandler.mCardImage != null) { baseDialog.setIcon(contentHandler.mCardImage); }
        baseDialog.setMenuIcon(mContext.getResources().getDrawable(R.drawable.ic_options), R.color.white);
        baseDialog.setTitle(contentHandler.mLabel);
        baseDialog.setSubTitle(contentHandler.mDate);
        baseDialog.setTitleBackgroundColour(Graphics.ColourTags.colourTagHeader(mContext, contentHandler.mTag));
        baseDialog.setTitleColour(Res.getColour(mContext, R.color.white));
        baseDialog.setPositiveButton("Edit", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                onEdit();
            }
        });
        baseDialog.setNegativeButton("Done", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });
        this.mDialog = baseDialog.createDialog();
        mDialog.show();
    }

    // Edit data package
    private void onEdit() {
        ACTIVITY_INTENT = new Intent(mContext, ActivityEdit.class);
        ACTIVITY_INTENT.putExtra("data", mDataPackage);
        mContext.startActivity(ACTIVITY_INTENT);
    }
}
