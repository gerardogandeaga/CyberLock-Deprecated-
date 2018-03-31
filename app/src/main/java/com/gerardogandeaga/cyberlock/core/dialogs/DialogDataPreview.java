package com.gerardogandeaga.cyberlock.core.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.android.CustomDialog;
import com.gerardogandeaga.cyberlock.core.TempEdit;
import com.gerardogandeaga.cyberlock.database.objects.NoteObject;
import com.gerardogandeaga.cyberlock.lists.handlers.extractors.NoteContentHandler;
import com.gerardogandeaga.cyberlock.utils.Resources;
import com.gerardogandeaga.cyberlock.utils.graphics.Graphics;
import com.gerardogandeaga.cyberlock.utils.views.ViewSetter;

import static com.gerardogandeaga.cyberlock.utils.security.LogoutProtocol.ACTIVITY_INTENT;

/**
 * @author gerardogandeaga
 */
public class DialogDataPreview {
    private Context mContext;

    // Data variables
    private NoteObject mNoteObject;

    // Views
    private Dialog mDialog;

    public DialogDataPreview(Context context, NoteObject noteObject) {
        this.mContext = context;
        this.mNoteObject = noteObject;
    }

    // Select type
    public void initializeDialog() {
        switch (mNoteObject.getType()) {
            case NoteObject.NOTE:         constructPreviewNote(); break;
            case NoteObject.CARD: constructPreviewPaymentInfo(); break;
            case NoteObject.LOGIN:   constructPreviewLoginInfo(); break;
        }
    }

    // Create dialog
    private void constructPreviewNote() {
        View view = View.inflate(mContext, R.layout.preview_note, null);
        NoteContentHandler noteContentHandler = new NoteContentHandler(mContext, mNoteObject);
        //
        TextView note = view.findViewById(R.id.tvNote);

        note.setText(noteContentHandler.mNotes);

        buildDialog(view, noteContentHandler);
    }
    private void constructPreviewPaymentInfo() {
        View view = View.inflate(mContext, R.layout.preview_paymentinfo, null);
        NoteContentHandler noteContentHandler = new NoteContentHandler(mContext, mNoteObject);
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

        if (ViewSetter.setLinearLayoutVisibility(Holder, noteContentHandler.mHolder))     holder.setText(noteContentHandler.mHolder);
        if (ViewSetter.setLinearLayoutVisibility(Number, noteContentHandler.mNumber))     number.setText(noteContentHandler.mNumber);
        if (ViewSetter.setLinearLayoutVisibility(Expiry, noteContentHandler.mExpiry))     expiry.setText(noteContentHandler.mExpiry);
        if (ViewSetter.setLinearLayoutVisibility(CVV, noteContentHandler.mCVV))           cvv.setText(noteContentHandler.mCVV);
        if (ViewSetter.setLinearLayoutVisibility(CardType, noteContentHandler.mCardType)) cardType.setText(noteContentHandler.mCardType);
        if (ViewSetter.setLinearLayoutVisibility(Notes, noteContentHandler.mNotes))        notes.setText(noteContentHandler.mNotes);

        buildDialog(view, noteContentHandler);
    }
    private void constructPreviewLoginInfo() {
        View view = View.inflate(mContext, R.layout.preview_logininfo, null);
        NoteContentHandler noteContentHandler = new NoteContentHandler(mContext, mNoteObject);
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

        if (ViewSetter.setLinearLayoutVisibility(Url, noteContentHandler.mUrl))           url.setText(noteContentHandler.mUrl);
        if (ViewSetter.setLinearLayoutVisibility(Email, noteContentHandler.mEmail))       email.setText(noteContentHandler.mEmail);
        if (ViewSetter.setLinearLayoutVisibility(Username, noteContentHandler.mUsername)) username.setText(noteContentHandler.mUsername);
        if (ViewSetter.setLinearLayoutVisibility(Password, noteContentHandler.mPassword)) password.setText(noteContentHandler.mPassword);
        if (ViewSetter.setLinearLayoutVisibility(Notes, noteContentHandler.mNotes))        notes.setText(noteContentHandler.mNotes);

        buildDialog(view, noteContentHandler);
    }

    private void buildDialog(View view, NoteContentHandler noteContentHandler) {
        CustomDialog customDialog = new CustomDialog(mContext);
        customDialog.setContentView(view);
        if (noteContentHandler.mCardImage != null) { customDialog.setIcon(noteContentHandler.mCardImage); }
        customDialog.setMenuIcon(mContext.getResources().getDrawable(R.drawable.ic_options), R.color.white);
        customDialog.setTitle(noteContentHandler.mLabel);
        customDialog.setSubTitle(noteContentHandler.mDate);
        customDialog.setTitleBackgroundColour(Graphics.ColourTags.colourTagHeader(mContext, noteContentHandler.mTag));
        customDialog.setTitleColour(Resources.getColour(mContext, R.color.white));
        customDialog.setPositiveButton("Edit", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                onEdit();
            }
        });
        customDialog.setNegativeButton("Done", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });
        this.mDialog = customDialog.createDialog();
        mDialog.show();
    }

    // Edit data package
    private void onEdit() {
        ACTIVITY_INTENT = new Intent(mContext, TempEdit.class);
        ACTIVITY_INTENT.putExtra("data", mNoteObject);
        mContext.startActivity(ACTIVITY_INTENT);
    }
}
