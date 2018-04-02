package com.gerardogandeaga.cyberlock.items;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.views.handlers.TextViews;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.materialize.holder.StringHolder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author gerardogandeaga
 */
public class ExternalLibItem extends AbstractItem<ExternalLibItem, ExternalLibItem.ViewHolder> {
    private StringHolder mTitle;
    private StringHolder mAuthor;
    private StringHolder mDescription;
    private StringHolder mUrl;

    public ExternalLibItem withTitle(String title) {
        this.mTitle = new StringHolder(title);
        return this;
    }
    public ExternalLibItem withAuthor(String subTitle) {
        this.mAuthor = new StringHolder(subTitle);
        return this;
    }
    public ExternalLibItem withDescription(String description) {
        this.mDescription = new StringHolder(description);
        return this;
    }
    public ExternalLibItem withUrl(String url) {
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
        return R.layout.external_library_item;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(@NonNull View view) {
        return new ViewHolder(view);
    }

    protected class ViewHolder extends FastItemAdapter.ViewHolder<ExternalLibItem> {
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
        public void bindView(@NonNull ExternalLibItem item, @NonNull List<Object> payloads) {
            TextViews.setOrHideTextView(item.mTitle, Title);
            TextViews.setOrHideTextView(item.mAuthor, Author);
            TextViews.setOrHideTextView(item.mDescription, Description);
            TextViews.setOrHideTextView(item.mUrl, Url);
        }

        @Override
        public void unbindView(@NonNull ExternalLibItem item) {
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
