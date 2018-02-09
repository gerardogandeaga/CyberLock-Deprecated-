package com.gerardogandeaga.cyberlock.activities.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.activities.core.ActivityEdit;
import com.gerardogandeaga.cyberlock.core.handlers.extractors.ContentHandler;
import com.gerardogandeaga.cyberlock.database.DataPackage;
import com.gerardogandeaga.cyberlock.utils.graphics.ColourTag;
import com.gerardogandeaga.cyberlock.utils.views.ViewHandler;

import static com.gerardogandeaga.cyberlock.utils.LogoutProtocol.ACTIVITY_INTENT;

public class DialogDataPreview {
    private Context mContext;

    // Data variables
    private DataPackage mDataPackage;

    // Views
    private AlertDialog.Builder mBuilder;

    public DialogDataPreview(Context context, DataPackage dataPackage) {
        this.mContext = context;
        this.mDataPackage = dataPackage;
    }

    // Select type
    public void initializeDialog() {
        switch (mDataPackage.getType()) {
            case "TYPE_NOTE":        constructPreviewNote(); break;
            case "TYPE_PAYMENTINFO": constructPreviewPaymentInfo(); break;
            case "TYPE_LOGININFO":   constructPreviewLoginInfo(); break;
        }
    }

    // Create dialog
    @SuppressLint("SetTextI18n") // TODO change the additional notes into an expandable layout like options change password
    private void constructPreviewNote() {
        View view = View.inflate(mContext, R.layout.preview_note, null);
        ContentHandler contentHandler = new ContentHandler(mContext, mDataPackage);
        //
        TextView title = view.findViewById(R.id.tvDialogTitle);
        TextView date = view.findViewById(R.id.tvDate);

        TextView note = view.findViewById(R.id.tvNote);

        title.setText(contentHandler.mLabel);
        date.setText("Updated: " + contentHandler.mDate);
        mutateTitle(view, title, date, contentHandler);

        note.setText(contentHandler.mNote);

        this.mBuilder = new AlertDialog.Builder(mContext);
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
    @SuppressLint("SetTextI18n")
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

        TextView title = view.findViewById(R.id.tvDialogTitle);
        TextView date = view.findViewById(R.id.tvDate);

        TextView holder = view.findViewById(R.id.tvName);
        TextView number = view.findViewById(R.id.tvNumber);
        TextView expiry = view.findViewById(R.id.tvExpiry);
        TextView cvv = view.findViewById(R.id.tvCVV);
        TextView cardType = view.findViewById(R.id.tvCardType);
        TextView notes = view.findViewById(R.id.tvNote);
        ImageView icon = view.findViewById(R.id.imgIcon);

        title.setText(contentHandler.mLabel);
        date.setText("Updated: " + contentHandler.mDate);
        mutateTitle(view, title, date, contentHandler);

        if (ViewHandler.setLinearLayoutVisibility(Holder, contentHandler.mHolder))     holder.setText(contentHandler.mHolder);
        if (ViewHandler.setLinearLayoutVisibility(Number, contentHandler.mNumber))     number.setText(contentHandler.mNumber);
        if (ViewHandler.setLinearLayoutVisibility(Expiry, contentHandler.mExpiry))     expiry.setText(contentHandler.mExpiry);
        if (ViewHandler.setLinearLayoutVisibility(CVV, contentHandler.mCVV))           cvv.setText(contentHandler.mCVV);
        if (ViewHandler.setLinearLayoutVisibility(CardType, contentHandler.mCardType)) cardType.setText(contentHandler.mCardType);
        if (ViewHandler.setLinearLayoutVisibility(Notes, contentHandler.mNote))        notes.setText(contentHandler.mNote);
        icon.setImageDrawable(contentHandler.mCardImage);

        this.mBuilder = new AlertDialog.Builder(mContext);
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
    @SuppressLint("SetTextI18n")
    private void constructPreviewLoginInfo() {
        View view = View.inflate(mContext, R.layout.preview_logininfo, null);
        ContentHandler contentHandler = new ContentHandler(mContext, mDataPackage);
        //
        LinearLayout Url = view.findViewById(R.id.Url);
        LinearLayout Email = view.findViewById(R.id.Email);
        LinearLayout Username = view.findViewById(R.id.Username);
        LinearLayout Password = view.findViewById(R.id.Password);
        LinearLayout Notes = view.findViewById(R.id.Notes);

        TextView title = view.findViewById(R.id.tvDialogTitle);
        TextView date = view.findViewById(R.id.tvDate);

        TextView url = view.findViewById(R.id.tvUrl);
        TextView email = view.findViewById(R.id.tvEmail);
        TextView username = view.findViewById(R.id.tvUsername);
        TextView password = view.findViewById(R.id.tvPassword);
        TextView notes = view.findViewById(R.id.tvNote);

        title.setText(contentHandler.mLabel);
        date.setText("Updated: " + contentHandler.mDate);
        mutateTitle(view, title, date, contentHandler);

        if (ViewHandler.setLinearLayoutVisibility(Url, contentHandler.mUrl))           url.setText(contentHandler.mUrl);
        if (ViewHandler.setLinearLayoutVisibility(Email, contentHandler.mEmail))       email.setText(contentHandler.mEmail);
        if (ViewHandler.setLinearLayoutVisibility(Username, contentHandler.mUsername)) username.setText(contentHandler.mUsername);
        if (ViewHandler.setLinearLayoutVisibility(Password, contentHandler.mPassword)) password.setText(contentHandler.mPassword);
        if (ViewHandler.setLinearLayoutVisibility(Notes, contentHandler.mNote))        notes.setText(contentHandler.mNote);

        this.mBuilder = new AlertDialog.Builder(mContext);
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

    private void mutateTitle(View view, TextView title, TextView date, ContentHandler contentHandler) {
        LinearLayout linearLayout = view.findViewById(R.id.Title);

        linearLayout.setBackgroundColor(ColourTag.colourTag(mContext, contentHandler.mTag));
        title.setTextColor(mContext.getResources().getColor(R.color.white));
        date.setTextColor(mContext.getResources().getColor(R.color.white));
    }

    // Edit data package
    private void onEdit() {
        ACTIVITY_INTENT = new Intent(mContext, ActivityEdit.class);
        ACTIVITY_INTENT.putExtra("data", mDataPackage);
        mContext.startActivity(ACTIVITY_INTENT);
    }
}
