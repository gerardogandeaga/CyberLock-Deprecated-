package com.gerardogandeaga.cyberlock.support.recyclerview.items;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.sqlite.data.RawDataPackage;
import com.gerardogandeaga.cyberlock.support.ViewHandler;
import com.gerardogandeaga.cyberlock.support.graphics.Fonts;
import com.gerardogandeaga.cyberlock.support.graphics.Themes;
import com.gerardogandeaga.cyberlock.support.handlers.selection.graphic.AdapterItemGraphics;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.materialize.holder.StringHolder;
import com.mikepenz.materialize.util.UIUtils;

import java.util.List;
import java.util.Scanner;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecyclerViewItem extends AbstractItem<RecyclerViewItem, RecyclerViewItem.ViewHolder> {
    public RawDataPackage mRawDataPackage;
    private String mType;

    // List information
    private StringHolder mLabel;
    private StringHolder mDate;
    private int mTag;

    private StringHolder mContent;
    private Drawable mCardType;

    // View holder class
    protected class ViewHolder extends FastItemAdapter.ViewHolder<RecyclerViewItem> {
        @NonNull protected View View;

        @BindView(R.id.cardView)     CardView CardView;
        @BindView(R.id.Note)         LinearLayout Note;
        @BindView(R.id.PaymentInfo)  LinearLayout PaymentInfo;
        @BindView(R.id.LoginInfo)    LinearLayout LoginInfo;

        @BindView(R.id.tvLabel)      TextView  Label;
        @BindView(R.id.tvDate)       TextView  Date;
        @BindView(R.id.imgColourTag) ImageView Tag;

        // Note
        @BindView(R.id.tvNote)    TextView Notes;
        // PaymentInfo
        @BindView(R.id.tvHolder)     TextView  Holder;
        @BindView(R.id.tvNumber)     TextView  Number;
        @BindView(R.id.imgCardIcon)  ImageView CardIcon;
        // LoginInfo
        @BindView(R.id.tvUrl)        TextView  Url;
        @BindView(R.id.tvEmail)      TextView  Email;
        @BindView(R.id.tvUsername)   TextView  Username;

        ViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.View = view;
        }

        // Binding and unbinding
        @Override
        public void bindView(@NonNull RecyclerViewItem item, @NonNull List<Object> payloads) {
            //get the context
            Context context = itemView.getContext();
            UIUtils.setBackground(View, AdapterItemGraphics.getItemDrawableStates(
                    context, Themes.recyclerListItemRegular(), Themes.recyclerListItemSelected(), false));
            ViewHandler.setLinearLayoutVisibility(Note, PaymentInfo, LoginInfo, item.mType);

            // Bind our data to the view
            ViewHandler.setOrHideTextView(item.mLabel, Label);
            ViewHandler.setOrHideTextView(item.mDate, Date);
            filterContent(item);

            // Images
            Tag.setVisibility(android.view.View.VISIBLE);
            Tag.setBackgroundColor(item.mTag);
        }

        @Override
        public void unbindView(@NonNull RecyclerViewItem item) {
            // Nullify Views
            Label.setText(null);
            Date.setText(null);

            Notes.setText(null);

            Holder.setText(null);
            Number.setText(null);
            CardIcon.setImageDrawable(null);

            Url.setText(null);
            Email.setText(null);
            Username.setText(null);

            // Reset visibility
            Note.setVisibility(android.view.View.GONE);
            PaymentInfo.setVisibility(android.view.View.GONE);
            LoginInfo.setVisibility(android.view.View.GONE);

            // TextViews
            Label.setVisibility(android.view.View.GONE);
            Date.setVisibility(android.view.View.GONE);

            Notes.setVisibility(android.view.View.GONE);

            Holder.setVisibility(android.view.View.GONE);
            Number.setVisibility(android.view.View.GONE);

            Url.setVisibility(android.view.View.GONE);
            Email.setVisibility(android.view.View.GONE);
            Username.setVisibility(android.view.View.GONE);

            // ImageViews
            CardIcon.setVisibility(android.view.View.GONE);

            item.getViewHolder(View);

            // Clear graphics
            Tag.setVisibility(android.view.View.INVISIBLE);
        }

        // Filter content
        private void filterContent(RecyclerViewItem item) {
            Scanner scanner = new Scanner(item.mContent.toString());
            switch(item.mType) {
                case "TYPE_NOTE":
                    ViewHandler.setOrHideTextView(item.mContent, Notes);
                    break;
                case "TYPE_PAYMENTINFO":
                    String holder = "", number = "";
                    if (scanner.hasNextLine()) holder = scanner.nextLine();
                    if (scanner.hasNextLine()) number = scanner.nextLine();

                    ViewHandler.setOrHideTextView(holder, Holder);
                    ViewHandler.setOrHideTextView(number, Number);
                    ViewHandler.setOrHideImageView(item.mCardType, CardIcon);
                    break;
                case "TYPE_LOGININFO":
                    String url = "", email = "", username = "";
                    if (scanner.hasNextLine()) url = scanner.nextLine();
                    if (scanner.hasNextLine()) email = scanner.nextLine();
                    if (scanner.hasNextLine()) username = scanner.nextLine();

                    ViewHandler.setOrHideTextView(url, Url);
                    ViewHandler.setOrHideTextView(email, Email);
                    ViewHandler.setOrHideTextView(username, Username);
                    break;
            }
            scanner.close();
        }

        // Text View
        private void setFonts(Context context) {
            Label.setTypeface(Fonts.robotoMonoBold(context));
            Date.setTypeface(Fonts.robotoMonoThin(context));

            Notes.setTypeface(Fonts.robotoMonoRegular(context));
            Holder.setTypeface(Fonts.robotoMonoRegular(context));
            Number.setTypeface(Fonts.robotoMonoRegular(context));
            Url.setTypeface(Fonts.robotoMonoRegular(context));
            Email.setTypeface(Fonts.robotoMonoRegular(context));
            Username.setTypeface(Fonts.robotoMonoRegular(context));
        }
    }

    // Setting content information
    public RecyclerViewItem withRawDataPackage(RawDataPackage rawDataPackage) {
        this.mRawDataPackage = rawDataPackage;
        return this;
    }

    public RecyclerViewItem withType(String type) {
        this.mType = type;
        return this;
    }
    public RecyclerViewItem withLabel(String label) {
        this.mLabel = new StringHolder(label);
        return this;
    }
    public RecyclerViewItem withDate(String date) {
        this.mDate = new StringHolder(date);
        return this;
    }
    public RecyclerViewItem withTag(int tag) {
        this.mTag = tag;
        return this;
    }

    public RecyclerViewItem withContent(String note) {
        this.mContent = new StringHolder(note);
        return this;
    }

    public RecyclerViewItem withCardIcon(Drawable cardType) {
        this.mCardType = cardType;
        return this;
    }

    // The unique ID for this type of item
    @Override
    public int getType() {
        return R.id.fastadapter_item;
    }
    // The layout that will be used for item
    @Override
    public int getLayoutRes() {
        return R.layout.recycler_list_item;
    }
    @Override
    public ViewHolder getViewHolder(@NonNull View view) {
        return new ViewHolder(view);
    }
    // -----------------------------------
}
