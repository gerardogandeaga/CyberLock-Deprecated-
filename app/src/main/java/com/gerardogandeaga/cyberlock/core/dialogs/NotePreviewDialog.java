package com.gerardogandeaga.cyberlock.core.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.database.objects.Note;
import com.gerardogandeaga.cyberlock.handlers.NoteContentHandler;
import com.gerardogandeaga.cyberlock.utils.Graphics;
import com.gerardogandeaga.cyberlock.utils.Res;
import com.gerardogandeaga.cyberlock.views.CustomDialog;
import com.gerardogandeaga.cyberlock.views.handlers.TextViews;

/**
 * @author gerardogandeaga
 */
// todo possible dialog fragment
public class NotePreviewDialog {
    public interface EditSelectedPreview {
        void onEdit(Note note);
    }
    private EditSelectedPreview mEditSelectedPreview;

    private Context mContext;
    private Note mNote;
    private Dialog mDialog;

    public NotePreviewDialog(Context context, Note note) {
        this.mContext = context;
        this.mNote = note;

        try {
            this.mEditSelectedPreview = (EditSelectedPreview) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    // Select type
    public void initializeDialog() {
        switch (mNote.getType()) {
            case Note.NOTE:         constructPreviewNote(); break;
            case Note.CARD: constructPreviewPaymentInfo(); break;
            case Note.LOGIN:   constructPreviewLoginInfo(); break;
        }
    }

    // Create dialog
    private void constructPreviewNote() {
        View view = View.inflate(mContext, R.layout.preview_note, null);
        NoteContentHandler noteContentHandler = new NoteContentHandler(mContext, mNote);
        //
        TextView note = view.findViewById(R.id.tvNote);

        note.setText(noteContentHandler.mNotes);

        buildDialog(view, noteContentHandler);
    }
    private void constructPreviewPaymentInfo() {
        View view = View.inflate(mContext, R.layout.preview_paymentinfo, null);
        NoteContentHandler noteContentHandler = new NoteContentHandler(mContext, mNote);
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

        if (TextViews.setLinearLayoutVisibility(Holder, noteContentHandler.mHolder))     holder.setText(noteContentHandler.mHolder);
        if (TextViews.setLinearLayoutVisibility(Number, noteContentHandler.mNumber))     number.setText(noteContentHandler.mNumber);
        if (TextViews.setLinearLayoutVisibility(Expiry, noteContentHandler.mExpiry))     expiry.setText(noteContentHandler.mExpiry);
        if (TextViews.setLinearLayoutVisibility(CVV, noteContentHandler.mCVV))           cvv.setText(noteContentHandler.mCVV);
        if (TextViews.setLinearLayoutVisibility(CardType, noteContentHandler.mCardType)) cardType.setText(noteContentHandler.mCardType);
        if (TextViews.setLinearLayoutVisibility(Notes, noteContentHandler.mNotes))       notes.setText(noteContentHandler.mNotes);

        buildDialog(view, noteContentHandler);
    }
    private void constructPreviewLoginInfo() {
        View view = View.inflate(mContext, R.layout.preview_logininfo, null);
        NoteContentHandler noteContentHandler = new NoteContentHandler(mContext, mNote);
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

        if (TextViews.setLinearLayoutVisibility(Url, noteContentHandler.mUrl))           url.setText(noteContentHandler.mUrl);
        if (TextViews.setLinearLayoutVisibility(Email, noteContentHandler.mEmail))       email.setText(noteContentHandler.mEmail);
        if (TextViews.setLinearLayoutVisibility(Username, noteContentHandler.mUsername)) username.setText(noteContentHandler.mUsername);
        if (TextViews.setLinearLayoutVisibility(Password, noteContentHandler.mPassword)) password.setText(noteContentHandler.mPassword);
        if (TextViews.setLinearLayoutVisibility(Notes, noteContentHandler.mNotes))        notes.setText(noteContentHandler.mNotes);

        buildDialog(view, noteContentHandler);
    }

    private void buildDialog(View view, NoteContentHandler noteContentHandler) {
        CustomDialog customDialog = new CustomDialog(mContext);
        customDialog.setContentView(view);
        if (noteContentHandler.mCardImage != null) { customDialog.setIcon(noteContentHandler.mCardImage); }
//        customDialog.setMenuIcon(mContext.getResources().getDrawable(R.drawable.ic_options), R.color.white);
        customDialog.setTitle(noteContentHandler.mLabel);
        customDialog.setSubTitle(noteContentHandler.mDate);
        customDialog.setTitleBackgroundColour(Graphics.ColourTags.colourTagHeader(mContext, noteContentHandler.mTag));
        customDialog.setTitleColour(Res.getColour(R.color.white));
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

    /**
     * sends a call to the main activity sending the note object to be
     * put into and intent and then started by the activity
     */
    private void onEdit() {
        if (mEditSelectedPreview != null) {
            mEditSelectedPreview.onEdit(mNote);
        }
    }
}
