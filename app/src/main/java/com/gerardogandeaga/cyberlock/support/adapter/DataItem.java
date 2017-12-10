package com.gerardogandeaga.cyberlock.support.adapter;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.sqlite.data.RawData;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DataItem extends AbstractItem<DataItem, DataItem.ViewHolder> {
    public RawData mRawData;

    // List information
    private String mLabel;
    private String mContent;
    private String mDate;
    private int mTag;
    private Drawable mCardType;

    // Binding and unbinding
    @Override public void bindView(@NonNull ViewHolder viewHolder,@NonNull List<Object> payloads) {
        super.bindView(viewHolder, payloads);

        // Bind our data to the view
        viewHolder.Label.setText(mLabel);
        viewHolder.Content.setText(mContent);
        viewHolder.Date.setText(mDate);
        // Image filter for tag
        viewHolder.Tag.setVisibility(View.VISIBLE);
        viewHolder.Tag.setBackgroundColor(mTag);
        // If there is a card type
        if (mCardType != null) {
            viewHolder.Content.setCompoundDrawables(null, null, mCardType, null);
        }
        viewHolder.CheckBox.setVisibility(View.GONE); // "Erase" check box for now
    }
    @Override public void unbindView(@NonNull ViewHolder viewHolder) {
        super.unbindView(viewHolder);

        // Clear view text
        viewHolder.Label.setText(null);
        viewHolder.Content.setText(null);
        viewHolder.Date.setText(null);
        // Clear graphics
        viewHolder.Tag.setVisibility(View.INVISIBLE);
        viewHolder.Content.setCompoundDrawables(null, null, null, null);
    }

    // View holder class
    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvLabel)      TextView  Label;
        @BindView(R.id.tvContent)    TextView  Content;
        @BindView(R.id.tvDate)       TextView  Date;
        @BindView(R.id.imgColourTag) ImageView Tag;
        @BindView(R.id.cbItem)       CheckBox  CheckBox;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    // Setting content information
    public DataItem withData(RawData rawData) {
        this.mRawData = rawData;
        return this;
    }
    public DataItem withLabel(String label) {
        this.mLabel = label;
        return this;
    }
    public DataItem withContent(String content) {
        this.mContent = content;
        return this;
    }
    public DataItem withDate(String date) {
        this.mDate = date;
        return this;
    }
    public DataItem withTag(int tag) {
        this.mTag = tag;
        return this;
    }
    //
    public DataItem withCardIcon(Drawable cardType) {
        this.mCardType = cardType;
        return this;
    }

    // The unique ID for this type of item
    @Override public int getType() {
        return R.id.fastadapter_item;
    }
    // The layout that will be used for item
    @Override public int getLayoutRes() {
        return R.layout.recycler_list_item;
    }
    @Override public ViewHolder getViewHolder(@NonNull View view) {
        return new ViewHolder(view);
    }
    // -----------------------------------
}
