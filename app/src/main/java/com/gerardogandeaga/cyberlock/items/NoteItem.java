package com.gerardogandeaga.cyberlock.items;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.Views;
import com.gerardogandeaga.cyberlock.database.objects.Note;
import com.gerardogandeaga.cyberlock.database.objects.notes.CardNote;
import com.gerardogandeaga.cyberlock.database.objects.notes.GenericNote;
import com.gerardogandeaga.cyberlock.database.objects.notes.LoginNote;
import com.gerardogandeaga.cyberlock.utils.Res;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.materialize.util.UIUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author gerardogandeaga
 */
public class NoteItem extends AbstractItem<NoteItem, NoteItem.ViewHolder> {
    public Context mContext;

    // recycler item info
    private int mPosition;
    private Note mNote;
    private String mType;
    // data
    private int mColourTag;
    private String mDate;
    private String mLabel;
    private String mContent;
    private Drawable mCardType;

    /**
     * return recycler item info
     */
    public int getPosition() {
        return mPosition;
    }
    public Note getNote() {
        return mNote;
    }

    public NoteItem withContext(Context context) {
        this.mContext = context;
        return this;
    }
    // setting content information
    public NoteItem withDataObject(Note note) {
        this.mNote = note;
        return this;
    }
    public NoteItem withType(String type) {
        this.mType = type;
        return this;
    }
    //
    public NoteItem withColourTag(int colourTag) {
        this.mColourTag = colourTag;
        return this;
    }
    public NoteItem withDate(String date) {
        this.mDate = date;
        return this;
    }
    public NoteItem withLabel(String label) {
        this.mLabel = label;
        return this;
    }
    public NoteItem withContent(String note) {
        this.mContent = note;
        return this;
    }
    public NoteItem withCardIcon(Drawable cardType) {
        this.mCardType = cardType;
        return this;
    }

    @Override
    public NoteItem withIdentifier(long identifier) {
        this.mPosition = (int) identifier;
        return super.withIdentifier(identifier);
    }

    /**
     * the unique ID for this type of item
     */
    @Override
    public int getType() {
        return R.id.fastadapter_item;
    }

    /**
     * the layout that will be used for item
     */
    @Override
    public int getLayoutRes() {
        return R.layout.item_note;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(@NonNull View view) {
        return new ViewHolder(view);
    }



    /**
     * view holder class
     */
    protected class ViewHolder extends FastItemAdapter.ViewHolder<NoteItem> {
        @NonNull protected View View;

        @BindView(R.id.container)    CardView CardView;
        @BindView(R.id.note)         LinearLayout Note;
        @BindView(R.id.paymentInfo)  LinearLayout Card;
        @BindView(R.id.loginInfo)    LinearLayout Login;

        @BindView(R.id.tvLabel)      TextView  Label;
        @BindView(R.id.tvSubTitle)   TextView  Date;
        @BindView(R.id.imgColourTag) CircleImageView ColourTag;

        // note
        @BindView(R.id.tvNotes)      TextView Notes;
        // paymentinfo
        @BindView(R.id.tvHolder)     TextView  Holder;
        @BindView(R.id.tvNumber)     TextView  Number;
        @BindView(R.id.imgCardIcon)  ImageView CardIcon;
        // logininfo
        @BindView(R.id.tvUrl)        TextView  Url;
        @BindView(R.id.tvEmail)      TextView  Email;
        @BindView(R.id.tvUsername)   TextView  Username;

        ViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.View = view;
        }

        @Override
        public void bindView(@NonNull NoteItem item, @NonNull List<Object> payloads) {
            // container background
            UIUtils.setBackground(View, R.drawable.note_item_background);

            // bind our data to the view
            Views.TextViews.setOrHideText(Label, item.mLabel);
            Views.TextViews.setOrHideText(Date, "Updated : " + item.mDate);
            // set the content according to the note
            setContent(item);

            // images
            Views.ImageViews.setOrHideImage(ColourTag, Res.getDrawable(R.drawable.ic_dot));
            ColourTag.setColorFilter(item.mColourTag, PorterDuff.Mode.SRC_ATOP);
        }

        @Override
        public void unbindView(@NonNull NoteItem item) {
            // first we need to just quickly hide all the sections
            Views.setVisibility(false, Note, Card, Login);

            // note
            Views.TextViews.setOrHideText(Notes, null);
            // card
            Views.TextViews.setOrHideText(Holder, null);
            Views.TextViews.setOrHideText(Number, null);
            Views.ImageViews.setOrHideImage(CardIcon, null);
            // login
            Views.TextViews.setOrHideText(Url, null);
            Views.TextViews.setOrHideText(Email, null);
            Views.TextViews.setOrHideText(Username, null);

            item.getViewHolder(View);
        }

        /**
         * filters the type of note the note is and sets the views accordingly
         * @param item list item which contains a Note object
         */
        private void setContent(NoteItem item) {
            final Note note = item.getNote();

            switch(note.getType()) {
                case com.gerardogandeaga.cyberlock.database.objects.Note.GENERIC:
                    GenericNote genericNote = new GenericNote(note);
                    Views.TextViews.setOrHideText(Notes, genericNote.getNotes());
                    // expand the parent view
                    Views.setVisibility(false, Card, Login);
                    Views.setVisibility(Note, true);
                    break;

                case com.gerardogandeaga.cyberlock.database.objects.Note.CARD:
                    CardNote cardNote = new CardNote(note);
                    Views.TextViews.setOrHideText(Holder, cardNote.getHolder());
                    Views.TextViews.setOrHideText(Number, formatCardNumber(cardNote.getNumber()));
                    Views.ImageViews.setOrHideImage(CardIcon, cardNote.getIcon());
                    // expand the parent view
                    Views.setVisibility(false, Note, Login);
                    Views.setVisibility(Card, true);
                    break;

                case com.gerardogandeaga.cyberlock.database.objects.Note.LOGIN:
                    LoginNote loginNote = new LoginNote(note);
                    Views.TextViews.setOrHideText(Url, loginNote.getUrl());
                    Views.TextViews.setOrHideText(Email, loginNote.getEmail());
                    Views.TextViews.setOrHideText(Username, loginNote.getUsername());
                    // expand the parent view
                    Views.setVisibility(false, Note, Card);
                    Views.setVisibility(Login, true);
                    break;
            }
        }
    }

    private String formatCardNumber(String number) {
        number = number.trim();

        if (number.length() <= 4) {
            return number;
        }

        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < (number.length() - 4); i++) {
            char c = number.charAt(i);

            if (c == ' ') {
                formatted.append(" ");
            } else {
                formatted.append("*");
            }
        }

        number = number.substring(formatted.length() - 1, number.length());

        return formatted + number;
    }

    @Override
    public String toString() {
        return "position : " + mPosition + " " + mNote;
    }
}
