package com.gerardogandeaga.cyberlock.core.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.items.ExternalLibItem;
import com.gerardogandeaga.cyberlock.items.ExternalLibItemContentHandler;
import com.gerardogandeaga.cyberlock.views.decorations.ExternalLibItemDecoration;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author gerardogandeaga on 2018-03-30.
 */
public class ExternalLibsActivity extends CoreActivity {
    FastItemAdapter<ExternalLibItem> mFastItemAdapter;

    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        View view = View.inflate(this, R.layout.activity_external_libraries, null);
        setContentView(view);
        bindView();

        this.mFastItemAdapter = new FastItemAdapter<>();

        mFastItemAdapter.setHasStableIds(true);
        mFastItemAdapter.withSelectable(true);
        mFastItemAdapter.withMultiSelect(true);
        mFastItemAdapter.withSelectOnLongClick(true);

        // linear layout
        mRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        // decoration
        mRecyclerView.addItemDecoration(new ExternalLibItemDecoration(this));
        // adapter
        mRecyclerView.setAdapter(mFastItemAdapter);

        List<ExternalLibItem> externalLibItemList = new ExternalLibItemContentHandler(this).getItems();

        mFastItemAdapter.add(externalLibItemList);

        setupActionBar("Open Source Libraries", null, R.drawable.ic_back);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void bindView() {
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
