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
import com.gerardogandeaga.cyberlock.database.objects.Note;
import com.gerardogandeaga.cyberlock.utils.PreferencesAccessor;
import com.gerardogandeaga.cyberlock.views.handlers.TextViews;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.materialize.util.UIUtils;

import java.util.List;
import java.util.Scanner;

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
    private int mTag;
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
    public NoteItem withTag(int tag) {
        this.mTag = tag;
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
        if (mContext != null) {
            if (PreferencesAccessor.Checkers.isLinearFormat(PreferencesAccessor.getListFormat(mContext))) {
                return R.layout.note_item_linear;
            } else {
                return R.layout.note_item_grid;
            }
        } else {
            return R.layout.note_item_linear;
        }
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
        @BindView(R.id.paymentInfo)  LinearLayout PaymentInfo;
        @BindView(R.id.loginInfo)    LinearLayout LoginInfo;

        @BindView(R.id.tvLabel)      TextView  Label;
        @BindView(R.id.tvSubTitle)   TextView  Date;
        @BindView(R.id.imgColourTag) CircleImageView ColourTag;

        // note
        @BindView(R.id.tvNote)       TextView Notes;
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

        // binding and unbinding
        @Override
        public void bindView(@NonNull NoteItem item, @NonNull List<Object> payloads) {
            UIUtils.setBackground(View, R.drawable.data_item_drawable_states);

            TextViews.setLinearLayoutVisibility(Note, PaymentInfo, LoginInfo, item.mType);

            // bind our data to the view
            TextViews.setOrHideTextView(item.mLabel, Label);
            TextViews.setOrHideTextView("Updated on " + item.mDate, Date);
            filterContent(item);

            // images
            ColourTag.setVisibility(android.view.View.VISIBLE);
            ColourTag.setColorFilter(item.mTag, PorterDuff.Mode.SRC_ATOP);
        }

        @Override
        public void unbindView(@NonNull NoteItem item) {
            // nullify views
            Label.setText(null);
            Date.setText(null);

            Notes.setText(null);

            Holder.setText(null);
            Number.setText(null);
            CardIcon.setImageDrawable(null);

            Url.setText(null);
            Email.setText(null);
            Username.setText(null);

            // reset visibility
            Note.setVisibility(android.view.View.GONE);
            PaymentInfo.setVisibility(android.view.View.GONE);
            LoginInfo.setVisibility(android.view.View.GONE);

            // text views
            Label.setVisibility(android.view.View.GONE);
            Date.setVisibility(android.view.View.GONE);

            Notes.setVisibility(android.view.View.GONE);

            Holder.setVisibility(android.view.View.GONE);
            Number.setVisibility(android.view.View.GONE);

            Url.setVisibility(android.view.View.GONE);
            Email.setVisibility(android.view.View.GONE);
            Username.setVisibility(android.view.View.GONE);

            // image views
            CardIcon.setVisibility(android.view.View.GONE);

            item.getViewHolder(View);

            // clear graphics
            ColourTag.setVisibility(android.view.View.INVISIBLE);
        }

        // filter content
        private void filterContent(NoteItem item) {
            Scanner scanner = new Scanner(item.mContent);
            switch(item.mType) {
                case com.gerardogandeaga.cyberlock.database.objects.Note.NOTE:
                    TextViews.setOrHideTextView(item.mContent, Notes);
                    break;
                case com.gerardogandeaga.cyberlock.database.objects.Note.CARD:
                    String holder = "", number = "";
                    if (scanner.hasNextLine()) holder = scanner.nextLine();
                    if (scanner.hasNextLine()) number = scanner.nextLine();

                    TextViews.setOrHideTextView(holder, Holder);
                    TextViews.setOrHideTextView(number, Number);
                    TextViews.setOrHideImageView(item.mCardType, CardIcon);
                    break;
                case com.gerardogandeaga.cyberlock.database.objects.Note.LOGIN:
                    String url = "", email = "", username = "";
                    if (scanner.hasNextLine()) url = scanner.nextLine();
                    if (scanner.hasNextLine()) email = scanner.nextLine();
                    if (scanner.hasNextLine()) username = scanner.nextLine();

                    TextViews.setOrHideTextView(url, Url);
                    TextViews.setOrHideTextView(email, Email);
                    TextViews.setOrHideTextView(username, Username);
                    break;
            }
            scanner.close();
        }
    }

    @Override
    public String toString() {
        return "position : " + mPosition + " " + mNote;
    }
}
