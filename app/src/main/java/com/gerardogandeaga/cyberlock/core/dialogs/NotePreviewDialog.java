package com.gerardogandeaga.cyberlock.core.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.utils.Views;
import com.gerardogandeaga.cyberlock.database.objects.Note;
import com.gerardogandeaga.cyberlock.database.objects.notes.CardNote;
import com.gerardogandeaga.cyberlock.database.objects.notes.GenericNote;
import com.gerardogandeaga.cyberlock.database.objects.notes.LoginNote;
import com.gerardogandeaga.cyberlock.utils.Graphics;
import com.gerardogandeaga.cyberlock.utils.Res;
import com.gerardogandeaga.cyberlock.custom.CustomDialog;

/**
 * @author gerardogandeaga
 */
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
            case Note.GENERIC:
                constructPreviewNote();
                break;
            case Note.CARD:
                constructPreviewPaymentInfo();
                break;
            case Note.LOGIN:
                constructPreviewLoginInfo();
                break;
        }
    }

    // Create dialog
    private void constructPreviewNote() {
        View view = View.inflate(mContext, R.layout.preview_note, null);
        GenericNote genericNote = mNote.getGenericNote();
        //
        TextView note = view.findViewById(R.id.tvNotes);

        note.setText(genericNote.getNotes());

        buildDialog(view, null);
    }
    private void constructPreviewPaymentInfo() {
        View v = View.inflate(mContext, R.layout.preview_card, null);
        CardNote cardNote = mNote.getCardNote();
        //
        Views.TextViews.setOrHideTextOnNestedView(v.findViewById(R.id.Holder), (TextView) v.findViewById(R.id.tvHolder), cardNote.getHolder());
        Views.TextViews.setOrHideTextOnNestedView(v.findViewById(R.id.Number), (TextView) v.findViewById(R.id.tvNumber), cardNote.getNumber());
        Views.TextViews.setOrHideTextOnNestedView(v.findViewById(R.id.Expiry), (TextView) v.findViewById(R.id.tvExpiry), cardNote.getExpiry());
        Views.TextViews.setOrHideTextOnNestedView(v.findViewById(R.id.CVV), (TextView) v.findViewById(R.id.tvCVV), cardNote.getCVV());
        Views.TextViews.setOrHideTextOnNestedView(v.findViewById(R.id.CardType), (TextView) v.findViewById(R.id.tvCardType), cardNote.getCardType());
        Views.TextViews.setOrHideTextOnNestedView(v.findViewById(R.id.Notes), (TextView) v.findViewById(R.id.tvNotes), cardNote.getNotes());

        buildDialog(v, cardNote.getIcon());
    }
    private void constructPreviewLoginInfo() {
        View v = View.inflate(mContext, R.layout.preview_login, null);
        LoginNote loginNote = mNote.getLoginNote();
        //
        Views.TextViews.setOrHideTextOnNestedView(v.findViewById(R.id.Url), (TextView) v.findViewById(R.id.tvUrl), loginNote.getUrl());
        Views.TextViews.setOrHideTextOnNestedView(v.findViewById(R.id.Email), (TextView) v.findViewById(R.id.tvEmail), loginNote.getEmail());
        Views.TextViews.setOrHideTextOnNestedView(v.findViewById(R.id.Username), (TextView) v.findViewById(R.id.tvUsername), loginNote.getUsername());
        Views.TextViews.setOrHideTextOnNestedView(v.findViewById(R.id.Password), (TextView) v.findViewById(R.id.tvPassword), loginNote.getPassword());
        Views.TextViews.setOrHideTextOnNestedView(v.findViewById(R.id.Notes), (TextView) v.findViewById(R.id.tvNotes), loginNote.getNotes());

        buildDialog(v, null);
    }

    private void buildDialog(View view, Drawable icon) {
        CustomDialog customDialog = new CustomDialog(mContext);
        customDialog.setContentView(view);
        if (icon != null) { customDialog.setMenuIcon(icon); }
        customDialog.setTitle(mNote.getLabel());
        customDialog.setSubTitle(mNote.getDate());
        customDialog.setTitleBackgroundColour(Graphics.ColourTags.colourTagHeader(mContext, mNote.getColourTag()));
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
