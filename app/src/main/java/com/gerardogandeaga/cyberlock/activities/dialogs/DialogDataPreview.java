package com.gerardogandeaga.cyberlock.activities.dialogs;

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
import com.gerardogandeaga.cyberlock.crypto.CryptoContent;
import com.gerardogandeaga.cyberlock.sqlite.data.RawDataPackage;
import com.gerardogandeaga.cyberlock.support.ViewHandler;
import com.gerardogandeaga.cyberlock.support.handlers.extractors.ContentHandler;

import static com.gerardogandeaga.cyberlock.support.LogoutProtocol.ACTIVITY_INTENT;

public class DialogDataPreview {
    private Context mContext;
    private CryptoContent cc;

    // Data variables
    private RawDataPackage mRawDataPackage;

    // Views
    private AlertDialog.Builder mBuilder;

    public DialogDataPreview(Context context, RawDataPackage rawDataPackage) {
        this.mContext = context;
        this.cc = new CryptoContent(mContext);
        this.mRawDataPackage = rawDataPackage;
    }

    // Select type
    public void initializeDialog() {
        switch (mRawDataPackage.getType(cc)) {
            case "TYPE_NOTE":        constructPreviewNote(); break;
            case "TYPE_PAYMENTINFO": constructPreviewPaymentInfo(); break;
            case "TYPE_LOGININFO":   constructPreviewLoginInfo(); break;
        }
    }

    // Create dialog
    private void constructPreviewNote() {
        View titleView = View.inflate(mContext, R.layout.dialog_title, null);
        View view = View.inflate(mContext, R.layout.preview_note, null);
        ContentHandler contentHandler = new ContentHandler(mContext, mRawDataPackage);
        //
        TextView title = titleView.findViewById(R.id.tvDialogTitle);
        TextView date = titleView.findViewById(R.id.tvDate);
        TextView note = view.findViewById(R.id.tvNote);

        title.setText(contentHandler.mLabel);
        date.setText("Updated: " + contentHandler.mDate);
        mutateTitle(titleView, title, date, contentHandler);

        note.setText(contentHandler.mNote);

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
        ContentHandler contentHandler = new ContentHandler(mContext, mRawDataPackage);
        //
        LinearLayout Holder = view.findViewById(R.id.Holder);
        LinearLayout Number = view.findViewById(R.id.Number);
        LinearLayout Expiry = view.findViewById(R.id.Expiry);
        LinearLayout CVV = view.findViewById(R.id.CVV);
        LinearLayout CardType = view.findViewById(R.id.CardType);
        LinearLayout Notes = view.findViewById(R.id.Notes);
        TextView title = titleView.findViewById(R.id.tvDialogTitle);
        TextView date = titleView.findViewById(R.id.tvDate);
        TextView holder = view.findViewById(R.id.tvName);
        TextView number = view.findViewById(R.id.tvNumber);
        TextView expiry = view.findViewById(R.id.tvExpiry);
        TextView cvv = view.findViewById(R.id.tvCVV);
        TextView cardType = view.findViewById(R.id.tvCardType);
        TextView notes = view.findViewById(R.id.tvNote);
        ImageView icon = view.findViewById(R.id.imgIcon);

        title.setText(contentHandler.mLabel);
        date.setText("Updated: " + contentHandler.mDate);
        mutateTitle(titleView, title, date, contentHandler);

        if (ViewHandler.setLinearLayoutVisibility(Holder, contentHandler.mHolder))     holder.setText(contentHandler.mHolder);
        if (ViewHandler.setLinearLayoutVisibility(Number, contentHandler.mNumber))     number.setText(contentHandler.mNumber);
        if (ViewHandler.setLinearLayoutVisibility(Expiry, contentHandler.mExpiry))     expiry.setText(contentHandler.mExpiry);
        if (ViewHandler.setLinearLayoutVisibility(CVV, contentHandler.mCVV))           cvv.setText(contentHandler.mCVV);
        if (ViewHandler.setLinearLayoutVisibility(CardType, contentHandler.mCardType)) cardType.setText(contentHandler.mCardType);
        if (ViewHandler.setLinearLayoutVisibility(Notes, contentHandler.mNote))        notes.setText(contentHandler.mNote);
        icon.setImageDrawable(contentHandler.mCardImage);

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
        ContentHandler contentHandler = new ContentHandler(mContext, mRawDataPackage);
        //
        LinearLayout Url = view.findViewById(R.id.Url);
        LinearLayout Email = view.findViewById(R.id.Email);
        LinearLayout Username = view.findViewById(R.id.Username);
        LinearLayout Password = view.findViewById(R.id.Password);
        LinearLayout Notes = view.findViewById(R.id.Notes);
        TextView title = titleView.findViewById(R.id.tvDialogTitle);
        TextView date = titleView.findViewById(R.id.tvDate);
        TextView url = view.findViewById(R.id.tvUrl);
        TextView email = view.findViewById(R.id.tvEmail);
        TextView username = view.findViewById(R.id.tvUsername);
        TextView password = view.findViewById(R.id.tvPassword);
        TextView notes = view.findViewById(R.id.tvNote);

        title.setText(contentHandler.mLabel);
        date.setText("Updated: " + contentHandler.mDate);
        mutateTitle(titleView, title, date, contentHandler);

        if (ViewHandler.setLinearLayoutVisibility(Url, contentHandler.mUrl))           url.setText(contentHandler.mUrl);
        if (ViewHandler.setLinearLayoutVisibility(Email, contentHandler.mEmail))       email.setText(contentHandler.mEmail);
        if (ViewHandler.setLinearLayoutVisibility(Username, contentHandler.mUsername)) username.setText(contentHandler.mUsername);
        if (ViewHandler.setLinearLayoutVisibility(Password, contentHandler.mPassword)) password.setText(contentHandler.mPassword);
        if (ViewHandler.setLinearLayoutVisibility(Notes, contentHandler.mNote))        notes.setText(contentHandler.mNote);

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

    private void mutateTitle(View view, TextView title, TextView date, ContentHandler contentHandler) {
        LinearLayout linearLayout = view.findViewById(R.id.Title);
        int color;
        boolean bool = true;
        switch (contentHandler.mTag) {
            case "COL_BLUE":   color = mContext.getResources().getColor(R.color.ct_blue); break;
            case "COL_RED":    color = mContext.getResources().getColor(R.color.ct_red); break;
            case "COL_GREEN":  color = mContext.getResources().getColor(R.color.ct_green); break;
            case "COL_YELLOW": color = mContext.getResources().getColor(R.color.ct_yellow); break;
            case "COL_PURPLE": color = mContext.getResources().getColor(R.color.ct_purple); break;
            case "COL_ORANGE": color = mContext.getResources().getColor(R.color.ct_orange); break;
            default:           color = mContext.getResources().getColor(R.color.white); bool = false; break;
        }
        if (bool) {
            linearLayout.setBackgroundColor(color);
            title.setTextColor(mContext.getResources().getColor(R.color.white));
            date.setTextColor(mContext.getResources().getColor(R.color.white));
        }
    }

    // Edit data package
    private void onEdit() {
        ACTIVITY_INTENT = new Intent(mContext, ActivityEdit.class);
        ACTIVITY_INTENT.putExtra("data", mRawDataPackage);
        mContext.startActivity(ACTIVITY_INTENT);
    }
}
