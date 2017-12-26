package com.gerardogandeaga.cyberlock.support.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.sqlite.data.RawDataPackage;
import com.gerardogandeaga.cyberlock.support.handlers.selection.graphic.AdapterItemGraphics;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.materialize.holder.StringHolder;
import com.mikepenz.materialize.util.UIUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DataItemView extends AbstractItem<DataItemView, DataItemView.ViewHolder> {
    public RawDataPackage mRawDataPackage;

    // List information
    private StringHolder mLabel;
    private StringHolder mContent;
    private StringHolder mDate;
    private int mTag;
    private Drawable mCardType;

    // View holder class
    protected class ViewHolder extends FastItemAdapter.ViewHolder<DataItemView> {
        @NonNull protected View View;

        @BindView(R.id.cardView)     CardView  CardView;
        @BindView(R.id.tvLabel)      TextView  Label;
        @BindView(R.id.tvContent)    TextView  Content;
        @BindView(R.id.tvDate)       TextView  Date;
        @BindView(R.id.imgColourTag) ImageView Tag;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.View = view;
        }

        // Binding and unbinding
        @Override
        public void bindView(DataItemView item, List<Object> payloads) {
            //get the context
            Context ctx = itemView.getContext();

            UIUtils.setBackground(View, AdapterItemGraphics.getItemDrawableStates(ctx, R.color.white, R.color.c_yellow_20, true));

            // Bind our data to the view
            StringHolder.applyTo(item.mLabel, Label);
            StringHolder.applyTo(item.mContent, Content);
            StringHolder.applyTo(item.mDate, Date);


            // Image filter for tag
            Tag.setVisibility(android.view.View.VISIBLE);
            Tag.setBackgroundColor(item.mTag);
            // If there is a card type
            if (item.mCardType != null) {
                Content.setCompoundDrawables(null, null, item.mCardType, null);
            }
        }

        @Override
        public void unbindView(DataItemView item) {
            // Clear view text
            Label.setText(null);
            Content.setText(null);
            Date.setText(null);

            item.getViewHolder(View);

            // Clear graphics
            Tag.setVisibility(android.view.View.INVISIBLE);
            Content.setCompoundDrawables(null, null, null, null);
        }
    }

    // Setting content information
    public DataItemView withRawDataPackage(RawDataPackage rawDataPackage) {
        this.mRawDataPackage = rawDataPackage;
        return this;
    }
    public DataItemView withLabel(String label) {
        this.mLabel = new StringHolder(label);
        return this;
    }
    public DataItemView withContent(String content) {
        this.mContent = new StringHolder(content);
        return this;
    }
    public DataItemView withDate(String date) {
        this.mDate = new StringHolder(date);
        return this;
    }
    public DataItemView withTag(int tag) {
        this.mTag = tag;
        return this;
    }
    //
    public DataItemView withCardIcon(Drawable cardType) {
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
