package com.gerardogandeaga.cyberlock.core.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.items.LibItem;
import com.gerardogandeaga.cyberlock.items.ExternalLibItemContentHandler;
import com.gerardogandeaga.cyberlock.custom.CustomRecyclerView;
import com.gerardogandeaga.cyberlock.custom.decorations.ExternalLibItemDecoration;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import java.util.List;

import butterknife.ButterKnife;

/**
 * @author gerardogandeaga on 2018-03-30.
 */
public class LibActivity extends CoreActivity {
    FastItemAdapter<LibItem> mItemAdapter;

    private CustomRecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        View view = View.inflate(this, R.layout.activity_container_static_toolbar, null);
        setContentView(view);
        bindView();

        this.mItemAdapter = new FastItemAdapter<>();

        mItemAdapter.setHasStableIds(true);
        mItemAdapter.withSelectable(true);
        mItemAdapter.withMultiSelect(true);
        mItemAdapter.withSelectOnLongClick(true);

        // linear layout
        mRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        // decoration
        mRecyclerView.addItemDecoration(new ExternalLibItemDecoration(this));
        // adapter
        mRecyclerView.setAdapter(mItemAdapter);

        List<LibItem> libItems = new ExternalLibItemContentHandler(this).getItems();

        mItemAdapter.add(libItems);

        setupActionBar("Libraries And Licences", null, R.drawable.ic_back);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void bindView() {
        this.mRecyclerView = new CustomRecyclerView(this);
        ((FrameLayout) findViewById(R.id.fragment_container)).addView(mRecyclerView);
        ButterKnife.bind(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        newIntent(OptionsActivity.class);
        super.onBackPressed();
    }
}
