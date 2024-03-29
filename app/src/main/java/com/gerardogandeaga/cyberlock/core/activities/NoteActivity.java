package com.gerardogandeaga.cyberlock.core.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.core.drawers.FolderDrawer;
import com.gerardogandeaga.cyberlock.custom.CustomLoad;
import com.gerardogandeaga.cyberlock.custom.decorations.NoteItemDecoration;
import com.gerardogandeaga.cyberlock.database.loaders.NoteAdapterLoader;
import com.gerardogandeaga.cyberlock.database.objects.Folder;
import com.gerardogandeaga.cyberlock.database.objects.Note;
import com.gerardogandeaga.cyberlock.helpers.ActionModeManager;
import com.gerardogandeaga.cyberlock.interfaces.AdapterLoaderCallback;
import com.gerardogandeaga.cyberlock.items.NoteItem;
import com.gerardogandeaga.cyberlock.utils.Graphics;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.ItemFilterListener;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.mikepenz.fastadapter.listeners.OnLongClickListener;
import com.mikepenz.materialdrawer.Drawer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author gerardogandeaga on 2018-03-30.
 */
public class NoteActivity extends CoreActivity implements AdapterLoaderCallback, ActionModeManager.ActionManagerCallBack {
    @Override
    public void onNoteItemsLoaded(String folderName, int folderSize) {
        mLoad.dismiss();
        mRecyclerView.setVisibility(View.VISIBLE);

        setCurrentWorkingFolder(folderName, folderSize);
    }

    // todo fix trashing mechanics
    @Override
    public void onRemoveSelections(ArrayList<Object> items) {

    }

    private ActionModeManager<NoteItem> mActionModeManager;

    // views
    private View mView;
    private CustomLoad mLoad;
    private RecyclerView mRecyclerView;
    private Drawer mDrawer;

    private String mCurrentFolderName;

    @BindView(R.id.fragment_container) FrameLayout mContainer;

    // initial on create methods
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // set view
        this.mView = View.inflate(this, R.layout.activity_container_static_toolbar, null);
        setContentView(mView);
        bindView();

        setupRecyclerView();
        displayLoad();

        // create the FastAdapter
        FastItemAdapter<NoteItem> itemAdapter = new FastItemAdapter<>();

        // configure the FastAdapter
        itemAdapter.setHasStableIds(true);
        itemAdapter.withSelectable(true);
        itemAdapter.withMultiSelect(true);
        itemAdapter.withSelectOnLongClick(true);

        // set adapter
        mRecyclerView.setAdapter(itemAdapter);

        // create the folder drawer
        this.mDrawer = new FolderDrawer(this, mToolbar, itemAdapter).createDrawer();

        // initialize action mode manager
        this.mActionModeManager = new ActionModeManager<>(this, itemAdapter, mDrawer);

        // listeners

        itemAdapter.withOnPreClickListener(new OnClickListener<NoteItem>() {
            @Override
            public boolean onClick(@Nullable View view, @NonNull IAdapter<NoteItem> adapter, @NonNull NoteItem item, int position) {
                // try to select the item, it will be false if nothing was selected
                if (!mActionModeManager.isActive()) {
                    newIntent(NoteEditActivity.class);
                    getNewIntent().putExtra("data", item.getNote());
                    newIntentGoTo();
                }
                return mActionModeManager.onClick(item);
            }
        });

        // starts the action mode manager in selection mode
        itemAdapter.withOnPreLongClickListener(new OnLongClickListener<NoteItem>() {
            @Override
            public boolean onLongClick(@NonNull View view, @NonNull IAdapter<NoteItem> adapter, @NonNull NoteItem item, int position) {
                return mActionModeManager.onLongClick(position);
            }
        });

        // filter listener
        itemAdapter.getItemFilter().withFilterPredicate(new IItemAdapter.Predicate<NoteItem>() {
            @Override
            public boolean filter(@NonNull NoteItem item, @Nullable CharSequence constraint) {
                assert constraint != null;

                String filter = constraint.toString();
                switch (filter) {
                    case Folder.Constants.ALL_NOTES:
                        return true;

                    case Folder.Constants.TRASH:
                        return item.getNote().isTrashed();

                    default:
                        return (item.getNote().getFolder().equals(filter) && !item.getNote().isTrashed());
                }
            }
        });

        itemAdapter.getItemFilter().withItemFilterListener(new ItemFilterListener<NoteItem>() {
            @Override
            public void itemsFiltered(@Nullable CharSequence constraint, @Nullable List<NoteItem> results) {
                // action bar titles
                assert constraint != null && results != null;
                setCurrentWorkingFolder(constraint.toString(), results.size());
            }

            @Override
            public void onReset() {

            }
        });

        // start fetching the notes
        new NoteAdapterLoader(this, itemAdapter).execute();

        actionBarIcon(R.drawable.ic_drawer);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void bindView() {
        ButterKnife.bind(this);
        setupActionBar(null, null, R.drawable.ic_drawer);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // tint menu colour
        Graphics.BasicFilter.mutateMenuItems(menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // add notes
            case R.id.menu_add_note:
                onAddClicked(Note.GENERIC);
                break;

            case R.id.menu_add_card:
                onAddClicked(Note.CARD);
                break;

            case R.id.menu_add_login:
                onAddClicked(Note.LOGIN);
                break;

            // open the drawer
            case android.R.id.home:
                if (mDrawer != null) {
                    mDrawer.openDrawer();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView() {
        this.mRecyclerView = new RecyclerView(this);
        mContainer.addView(mRecyclerView);
        mRecyclerView.setVisibility(View.GONE);

        // recyclerView decorations
        mRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        );
        mRecyclerView.addItemDecoration(new NoteItemDecoration(this, true));
    }

    private void displayLoad() {
        this.mLoad = new CustomLoad(this, mView);
        mLoad.show(mContainer);
    }

    private void setCurrentWorkingFolder(@NonNull String folderName, int folderSize) {
        actionBarTitle(folderName);
        actionBarSubTitle(
                folderSize == 0 ? "No items " :
                        folderSize + " Item" + (folderSize == 1 ? "" : "s")
        );

        this.mCurrentFolderName = folderName;
    }

    public void onAddClicked(String noteType) {
        newIntent(NoteEditActivity.class);
        getNewIntent().putExtra("type", noteType);
        getNewIntent().putExtra("folder", mCurrentFolderName);
        newIntentGoTo();
    }

    @Override
    public void onBackPressed() {
        // close drawer
        if (mDrawer.isDrawerOpen()) {
            mDrawer.closeDrawer();
            return;
        }

        newIntent(LoginActivity.class);
        super.onBackPressed();
    }
}
