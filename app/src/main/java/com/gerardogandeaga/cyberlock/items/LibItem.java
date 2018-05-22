package com.gerardogandeaga.cyberlock.items;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.utils.Views;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.materialize.holder.StringHolder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author gerardogandeaga
 */
public class LibItem extends AbstractItem<LibItem, LibItem.ViewHolder> {
    private StringHolder mTitle;
    private StringHolder mAuthor;
    private StringHolder mDescription;
    private StringHolder mUrl;

    public LibItem withTitle(String title) {
        this.mTitle = new StringHolder(title);
        return this;
    }
    public LibItem withAuthor(String subTitle) {
        this.mAuthor = new StringHolder(subTitle);
        return this;
    }
    public LibItem withDescription(String description) {
        this.mDescription = new StringHolder(description);
        return this;
    }
    public LibItem withUrl(String url) {
        this.mUrl = new StringHolder(url);
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
        return R.layout.item_lib;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(@NonNull View view) {
        return new ViewHolder(view);
    }

    protected class ViewHolder extends FastItemAdapter.ViewHolder<LibItem> {
        @NonNull protected View View;

        @BindView(R.id.container)     CardView CardView;
        @BindView(R.id.tvTitle)       TextView Title;
        @BindView(R.id.tvSubTitle)    TextView Author;
        @BindView(R.id.tvDescription) TextView Description;
        @BindView(R.id.tvUrl)         TextView Url;

        ViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.View = view;
        }

        @Override
        public void bindView(@NonNull LibItem item, @NonNull List<Object> payloads) {
            Views.TextViews.setOrHideText(Title, item.mTitle.toString());
            Views.TextViews.setOrHideText(Author, item.mAuthor.toString());
            Views.TextViews.setOrHideText(Description, item.mDescription.toString());
            Views.TextViews.setOrHideText(Url, item.mUrl.toString());
        }

        @Override
        public void unbindView(@NonNull LibItem item) {
            Title.setText(null);
            Author.setText(null);
            Description.setText(null);
            Url.setText(null);

            Title.setVisibility(android.view.View.GONE);
            Author.setVisibility(android.view.View.GONE);
            Description.setVisibility(android.view.View.GONE);
            Url.setVisibility(android.view.View.GONE);
        }
    }
}
