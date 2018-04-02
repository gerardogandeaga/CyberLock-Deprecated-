package com.gerardogandeaga.cyberlock.core.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.core.TempEdit;
import com.gerardogandeaga.cyberlock.core.dialogs.NotePreviewDialog;
import com.gerardogandeaga.cyberlock.database.loaders.NoteLoader;
import com.gerardogandeaga.cyberlock.database.objects.NoteObject;
import com.gerardogandeaga.cyberlock.helpers.AdapterActionManager;
import com.gerardogandeaga.cyberlock.helpers.DataObjectDeleter;
import com.gerardogandeaga.cyberlock.items.NoteItem;
import com.gerardogandeaga.cyberlock.items.NoteItemContentHandler;
import com.gerardogandeaga.cyberlock.utils.Graphics;
import com.gerardogandeaga.cyberlock.utils.ListFormat;
import com.gerardogandeaga.cyberlock.utils.SharedPreferences;
import com.gerardogandeaga.cyberlock.views.CustomLoad;
import com.gerardogandeaga.cyberlock.views.CustomRecyclerView;
import com.gerardogandeaga.cyberlock.views.CustomSnackBar;
import com.gerardogandeaga.cyberlock.views.decorations.NoteItemDecoration;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.mikepenz.fastadapter.listeners.OnLongClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author gerardogandeaga on 2018-03-30.
 */
public class NoteListActivity extends CoreActivity implements NoteLoader.OnDataPackageLoaded {
    // adapter package loading
    @Override
    public void sendPackage(final NoteObject noteObject) {
        // arrange data as a recycler view item
        final NoteItem item = new NoteItemContentHandler(this).getItem(noteObject);

        // add to adapter
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (item != null) {
                    mFastItemAdapter.add(item);
                }

                // dismiss load overlay and show recycler view
                if (mCustomLoad.isVisible()) {
                    mCustomLoad.dismiss();
                    mRecyclerView.setVisibility(View.VISIBLE);
                }

                if (mFastItemAdapter.getItemCount() == mSize) {
                    mRecyclerView.endAnimations();
                }
            }
        });
    }

    private Context mContext = this;
    // adapter
    private int mSize;
    private FastItemAdapter<NoteItem> mFastItemAdapter;
    private AdapterActionManager<NoteItem> mAdapterActionManager;
    // views
    private View mView;
    private Menu mMenu;
    // load view
    private CustomLoad mCustomLoad;

    @BindView(R.id.recyclerView) CustomRecyclerView mRecyclerView;

    // initial on create methods
    @Override
    public void onCreate(Bundle savedInstanceState) {
//        Themes.setTheme(this);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        // set view
        this.mView = View.inflate(this, R.layout.activity_main, null);
        setContentView(mView);
        bindView();

        setupRecyclerView();
        displayLoad();

        // create the FastAdapter
        this.mFastItemAdapter = new FastItemAdapter<>();
        this.mAdapterActionManager = new AdapterActionManager<>(this, mFastItemAdapter);

        // configure the FastAdapter
        mFastItemAdapter.setHasStableIds(true);
        mFastItemAdapter.withSelectable(false);
        mFastItemAdapter.withMultiSelect(false);
        mFastItemAdapter.withAllowDeselection(false);
        mFastItemAdapter.withSelectOnLongClick(false);

        // item Listeners
        mFastItemAdapter.withOnClickListener(new OnClickListener<NoteItem>() {
            @Override
            public boolean onClick(View view, @NonNull IAdapter<NoteItem> adapter, @NonNull NoteItem item, int position) {
                if (mAdapterActionManager.isActive()) {
                    // selection mode
                    mAdapterActionManager.toggle(position);
                    setActionBarTitleCount(mAdapterActionManager.selectedCount());

                    if (mAdapterActionManager.noneAreSelected()) {
                        mAdapterActionManager.deactivate();
                        resetActionBar(null, null, NO_ICON);
                    }

                    updateMenu();

                } else {
                    // preview data
                    new NotePreviewDialog(mContext, item.getNoteObject()).initializeDialog();
                }

                return true;
            }
        });
        mFastItemAdapter.withOnLongClickListener(new OnLongClickListener<NoteItem>() {
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
        mRecyclerView.setAdapter(mFastItemAdapter);

        // initialize and execute data loader task
        NoteLoader noteLoader = new NoteLoader(this);
        this.mSize = noteLoader.size();
        // now start the new task
        noteLoader.execute();

        setupActionBar(null, null, NO_ICON);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void bindView() {
        ButterKnife.bind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        this.mMenu = menu;

        updateMenu();

        return true;
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
            Graphics.BasicFilter.mutateMenuItems(this, mMenu);
        }
    }
    //
    private void setupRecyclerView() {
        // Setup and configure RecyclerView
        mRecyclerView.setVisibility(View.GONE);
        final LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        final StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        // Set layout format
        switch (SharedPreferences.getListFormat(mContext)) {
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
        this.mCustomLoad = new CustomLoad(this, mView);
        mCustomLoad.show(R.id.container);
    }
    //
    private void setActionBarTitleCount(int selectedCount) {
        actionBarTitle(Integer.toString(selectedCount));
        actionBarSubTitle(selectedCount > 0 ? "Items Selected" : "Item Selected");
    }

    // Global clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if (this.mDrawerToggle.onOptionsItemSelected(item)) return true;
        switch (item.getItemId()) {
            // add
            case R.id.menu_add_note:
                onAddClicked(NoteObject.NOTE);
                break;
            case R.id.menu_add_card:
                onAddClicked(NoteObject.CARD);
                break;
            case R.id.menu_add_login:
                onAddClicked(NoteObject.LOGIN);
                break;

            // options
            case R.id.menu_option_options:
                intentGoTo(OptionsActivity.class);
                return true;

            // misc
            case android.R.id.home:
                onBackPressed();
                return true;

            // on multi select mode
            case R.id.menu_delete:
                // deleter
                final DataObjectDeleter deleter = new DataObjectDeleter(this, mFastItemAdapter, mAdapterActionManager.removeItemsFromAdapter());
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
                resetActionBar(null, null, NO_ICON);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Menu options functions
    // Notes, PaymentInfo, LoginInfo ################################
    public void onAddClicked(String noteType) {
        newIntent(TempEdit.class);
        getNewIntent().putExtra("type", noteType);
        intentGoTo();
    }

    @Override
    public void onStart() {
        newIntent(LoginActivity.class);
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        // end the multi adapter action manager
        if (mAdapterActionManager.isActive()) {
            mAdapterActionManager.deactivate();
            updateMenu();
            resetActionBar(null, null, NO_ICON);
        } else {
            super.onBackPressed();
        }
    }
}