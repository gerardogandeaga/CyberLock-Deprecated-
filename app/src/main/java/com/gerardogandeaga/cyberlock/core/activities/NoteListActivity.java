package com.gerardogandeaga.cyberlock.core.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.core.dialogs.NotePreviewDialog;
import com.gerardogandeaga.cyberlock.core.drawers.FolderDrawer;
import com.gerardogandeaga.cyberlock.database.loaders.NoteAdapterLoader;
import com.gerardogandeaga.cyberlock.database.objects.Folder;
import com.gerardogandeaga.cyberlock.database.objects.Note;
import com.gerardogandeaga.cyberlock.handlers.FolderDrawerHandler;
import com.gerardogandeaga.cyberlock.helpers.AdapterActionManager;
import com.gerardogandeaga.cyberlock.helpers.DataObjectDeleter;
import com.gerardogandeaga.cyberlock.interfaces.AdapterLoaderCallback;
import com.gerardogandeaga.cyberlock.items.NoteItem;
import com.gerardogandeaga.cyberlock.utils.Graphics;
import com.gerardogandeaga.cyberlock.utils.ListFormat;
import com.gerardogandeaga.cyberlock.utils.PreferencesAccessor;
import com.gerardogandeaga.cyberlock.views.CustomLoad;
import com.gerardogandeaga.cyberlock.views.CustomRecyclerView;
import com.gerardogandeaga.cyberlock.views.CustomSnackBar;
import com.gerardogandeaga.cyberlock.views.decorations.NoteItemDecoration;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.mikepenz.fastadapter.listeners.OnLongClickListener;
import com.mikepenz.materialdrawer.Drawer;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author gerardogandeaga on 2018-03-30.
 */
public class NoteListActivity extends CoreActivity implements AdapterLoaderCallback, NotePreviewDialog.EditSelectedPreview {
    @Override
    public void onLoaded(Folder folder) {
        mRecyclerView.setVisibility(View.VISIBLE);

        this.mCurrentFolder = folder;

        actionbarFolderTitle();
    }

    @Override
    public void onEdit(Note note) {
        newIntent(NoteEditActivity.class);
        getNewIntent().putExtra("data", note);
        newIntentGoTo();
    }

    private Context mContext = this;

    // adapter
    private FastItemAdapter<NoteItem> mItemAdapter;
    private AdapterActionManager<NoteItem> mAdapterActionManager;
    private FolderDrawerHandler mFolderDrawerHandler;
    private Folder mCurrentFolder;

    // views
    private View mView;
    private CustomRecyclerView mRecyclerView;
    private Menu mMenu;
    private Drawer mDrawer;

    @BindView(R.id.fragment_container) FrameLayout mContainer;

    // initial on create methods
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // set view
        this.mView = View.inflate(this, R.layout.activity_container_static_toolbar, null);
        setContentView(mView);
        bindView();

        setupRecyclerView();

        // create the FastAdapter
        this.mItemAdapter = new FastItemAdapter<>();
        this.mAdapterActionManager = new AdapterActionManager<>(this, mItemAdapter);

        // configure the FastAdapter
        mItemAdapter.setHasStableIds(true);
        mItemAdapter.withSelectable(false);
        mItemAdapter.withMultiSelect(false);
        mItemAdapter.withAllowDeselection(false);
        mItemAdapter.withSelectOnLongClick(false);

        // item Listeners
        mItemAdapter.withOnClickListener(new OnClickListener<NoteItem>() {
            @Override
            public boolean onClick(View view, @NonNull IAdapter<NoteItem> adapter, @NonNull NoteItem item, int position) {
                if (mAdapterActionManager.isActive()) {
                    // selection mode
                    mAdapterActionManager.toggle(position);
                    setActionBarTitleCount(mAdapterActionManager.selectedCount());

                    if (mAdapterActionManager.noneAreSelected()) {
                        mAdapterActionManager.deactivate();
                        // resets titles to current folder name and size
                        actionbarFolderTitle();
                    }

                    updateMenu();

                } else {
                    // preview data
                    new NotePreviewDialog(mContext, item.getNote()).initializeDialog();
                }

                return true;
            }
        });
        mItemAdapter.withOnLongClickListener(new OnLongClickListener<NoteItem>() {
            @Override
            public boolean onLongClick(@NonNull View view, @NonNull IAdapter<NoteItem> adapter, @NonNull NoteItem item, int position) {
                if (!mAdapterActionManager.isActive()) {
                    mAdapterActionManager.activate();
                    mAdapterActionManager.toggle(position);
                    setActionBarTitleCount(mAdapterActionManager.selectedCount());

                } else if (mAdapterActionManager.isActive()) {
                    mAdapterActionManager.toggle(position);
                    setActionBarTitleCount(mAdapterActionManager.selectedCount());
                }

                updateMenu();

                return false;
            }
        });

        // set adapter
        mRecyclerView.setAdapter(mItemAdapter);

        // start the adapter loader
        new NoteAdapterLoader(this, mItemAdapter, true).execute();

        // create the folder drawer
        this.mDrawer = new FolderDrawer(this, mToolbar).createDrawer();
        this.mFolderDrawerHandler = new FolderDrawerHandler(this, mDrawer, mItemAdapter);

        setupActionBar(null, null, R.drawable.ic_menu);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void bindView() {
        ButterKnife.bind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.mMenu = menu;

        updateMenu();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if (this.mDrawerToggle.onOptionsItemSelected(item)) return true;
        switch (item.getItemId()) {
            // add
            case R.id.menu_add_note:
                onAddClicked(Note.GENERIC);
                break;
            case R.id.menu_add_card:
                onAddClicked(Note.CARD);
                break;
            case R.id.menu_add_login:
                onAddClicked(Note.LOGIN);
                break;

            // options
            case R.id.menu_option_options:
                newIntentGoTo(OptionsActivity.class);
                return true;

            // open the drawer
            case android.R.id.home:
                if (mDrawer != null) {
                    mDrawer.openDrawer();
                }
                return true;

            // on multi select mode
            case R.id.menu_delete:
                // deleter
                final DataObjectDeleter deleter = new DataObjectDeleter(this, mItemAdapter, mAdapterActionManager.removeItemsFromAdapter());
                deleter.deleteItems();
                // snackbar
                CustomSnackBar.buildAndShowSnackBar(
                        mView,
                        deleter.getDeletedCount() + (deleter.getDeletedCount() > 1 ? " Items Deleted" : " Item Deleted"),
                        CustomSnackBar.LENGTH_LONG,
                        "Undo",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // undo deleter
                                deleter.undoDeletion();
                            }
                        },
                        R.color.white);
                // finish
                mAdapterActionManager.finish();
                updateMenu();
                actionBarTitles(null, null, NO_ICON);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateMenu() {
        mMenu.clear();
        if (mAdapterActionManager.isActive()) {
            getMenuInflater().inflate(R.menu.menu_delete, mMenu);
        } else {
            getMenuInflater().inflate(R.menu.menu_main, mMenu);
        }

        // tint menu colour
        if (mMenu.hasVisibleItems()) {
            Graphics.BasicFilter.mutateMenuItems(mMenu);
        }
    }
    //
    private void setupRecyclerView() {
        this.mRecyclerView = new CustomRecyclerView(this);
        mContainer.addView(mRecyclerView);

        // Setup and configure RecyclerView
        mRecyclerView.setVisibility(View.GONE);
        final LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        final StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        // Set layout format
        switch (PreferencesAccessor.getListFormat(mContext)) {
            case ListFormat.GRID:
                staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
                mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
                mRecyclerView.addItemDecoration(new NoteItemDecoration(this, false));
                break;
            default:
                mRecyclerView.setLayoutManager(linearLayoutManager);
                mRecyclerView.addItemDecoration(new NoteItemDecoration(this, true));
                break;
        }
    }
    private void displayLoad() {
        CustomLoad customLoad = new CustomLoad(this, mView);
        customLoad.show(R.id.container);
    }
    //
    private void actionbarFolderTitle() {
        actionBarTitle((mCurrentFolder.getName().equals("MAIN") ? "All Notes" : mCurrentFolder.getName()));
        actionBarSubTitle(Integer.toString(mCurrentFolder.getSize()) + (mCurrentFolder.getSize() == 1 ? " Item" : " Items"));
    }
    private void setActionBarTitleCount(int selectedCount) {
        actionBarTitle(Integer.toString(selectedCount));
        actionBarSubTitle(selectedCount > 0 ? "Items Selected" : "Item Selected");
    }

    public void onAddClicked(String noteType) {
        newIntent(NoteEditActivity.class);
        getNewIntent().putExtra("type", noteType);
        getNewIntent().putExtra("folder", mCurrentFolder.getName());
        newIntentGoTo();
    }

    @Override
    public void onBackPressed() {
        // end the multi adapter action manager
        if (mAdapterActionManager.isActive()) {
            mAdapterActionManager.deactivate();
            updateMenu();
            actionBarTitles(null, null, NO_ICON);
        } else {
            super.onBackPressed();
        }
    }
}