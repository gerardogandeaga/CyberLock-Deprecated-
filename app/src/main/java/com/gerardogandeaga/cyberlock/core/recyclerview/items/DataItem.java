package com.gerardogandeaga.cyberlock.core.recyclerview.items;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.database.DataPackage;
import com.gerardogandeaga.cyberlock.utils.Res;
import com.gerardogandeaga.cyberlock.utils.Settings;
import com.gerardogandeaga.cyberlock.utils.views.ViewSetter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.materialize.holder.StringHolder;

import java.util.List;
import java.util.Scanner;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DataItem extends AbstractItem<DataItem, DataItem.ViewHolder> {
    public Context mContext;

    public DataPackage mDataPackage;
    private String mType;

    // list information
    private StringHolder mLabel;
    private StringHolder mDate;
    private int mTag;

    private StringHolder mContent;
    private Drawable mCardType;

    public DataItem withContext(Context context) {
        this.mContext = context;
        return this;
    }
    // setting content information
    public DataItem withRawDataPackage(DataPackage dataPackage) {
        this.mDataPackage = dataPackage;
        return this;
    }
    public DataItem withType(String type) {
        this.mType = type;
        return this;
    }
    public DataItem withLabel(String label) {
        this.mLabel = new StringHolder(label);
        return this;
    }
    public DataItem withDate(String date) {
        this.mDate = new StringHolder(date);
        return this;
    }
    public DataItem withTag(int tag) {
        this.mTag = tag;
        return this;
    }
    public DataItem withContent(String note) {
        this.mContent = new StringHolder(note);
        return this;
    }
    public DataItem withCardIcon(Drawable cardType) {
        this.mCardType = cardType;
        return this;
    }

    // the unique ID for this type of item
    @Override
    public int getType() {
        return R.id.fastadapter_item;
    }
    // the layout that will be used for item
    @Override
    public int getLayoutRes() {
        if (mContext != null) {
            if (Settings.Checkers.isLinearFormat(Settings.getListFormat(mContext))) {
                return R.layout.data_item_linear;
            } else {
                return R.layout.data_item_grid;
            }
        } else {
            return R.layout.data_item_linear;
        }
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(@NonNull View view) {
        return new ViewHolder(view);
    }

    // view holder class
    protected class ViewHolder extends FastItemAdapter.ViewHolder<DataItem> {
        @NonNull protected View View;

        @BindView(R.id.container)    CardView CardView;
        @BindView(R.id.note)         LinearLayout Note;
        @BindView(R.id.paymentInfo)  LinearLayout PaymentInfo;
        @BindView(R.id.loginInfo)    LinearLayout LoginInfo;

        @BindView(R.id.tvLabel)      TextView  Label;
        @BindView(R.id.tvSubTitle)   TextView  Date;
        @BindView(R.id.imgColourTag) ImageView Tag;

        // note
        @BindView(R.id.tvNote)       TextView Notes;
        // paymentinfo
        @BindView(R.id.tvHolder)     TextView  Holder;
        @BindView(R.id.tvNumber)     TextView  Number;
        @BindView(R.id.imgCardIcon) ImageView CardIcon;
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
        public void bindView(@NonNull DataItem item, @NonNull List<Object> payloads) {
            //get the context
            Context context = itemView.getContext();
            View.setBackground(Res.getDrawable(context, R.drawable.data_item_drawable_states));
//            UIUtils.setBackground(View, AdapterItemBackground.getItemDrawableStates(
//                    context, R.color.white, R.color.c_yellow_20, false));
            ViewSetter.setLinearLayoutVisibility(Note, PaymentInfo, LoginInfo, item.mType);

            // bind our data to the view
            ViewSetter.setOrHideTextView(item.mLabel, Label);
            ViewSetter.setOrHideTextView(item.mDate, Date);
            filterContent(item);

            // images
            Tag.setVisibility(android.view.View.VISIBLE);
            Tag.setBackgroundColor(item.mTag);
        }

        @Override
        public void unbindView(@NonNull DataItem item) {
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
            Tag.setVisibility(android.view.View.INVISIBLE);
        }

        // filter content
        private void filterContent(DataItem item) {
            Scanner scanner = new Scanner(item.mContent.toString());
            switch(item.mType) {
                case DataPackage.NOTE:
                    ViewSetter.setOrHideTextView(item.mContent, Notes);
                    break;
                case DataPackage.PAYMENT_INFO:
                    String holder = "", number = "";
                    if (scanner.hasNextLine()) holder = scanner.nextLine();
                    if (scanner.hasNextLine()) number = scanner.nextLine();

                    ViewSetter.setOrHideTextView(holder, Holder);
                    ViewSetter.setOrHideTextView(number, Number);
                    ViewSetter.setOrHideImageView(item.mCardType, CardIcon);
                    break;
                case DataPackage.LOGIN_INFO:
                    String url = "", email = "", username = "";
                    if (scanner.hasNextLine()) url = scanner.nextLine();
                    if (scanner.hasNextLine()) email = scanner.nextLine();
                    if (scanner.hasNextLine()) username = scanner.nextLine();

                    ViewSetter.setOrHideTextView(url, Url);
                    ViewSetter.setOrHideTextView(email, Email);
                    ViewSetter.setOrHideTextView(username, Username);
                    break;
            }
            scanner.close();
        }
    }
}
