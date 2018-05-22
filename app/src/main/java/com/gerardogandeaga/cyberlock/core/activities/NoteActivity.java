package com.gerardogandeaga.cyberlock.core.activities;

import android.content.Context;
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
import com.gerardogandeaga.cyberlock.core.dialogs.NotePreviewDialog;
import com.gerardogandeaga.cyberlock.core.drawers.FolderDrawer;
import com.gerardogandeaga.cyberlock.custom.CustomLoad;
import com.gerardogandeaga.cyberlock.custom.decorations.NoteItemDecoration;
import com.gerardogandeaga.cyberlock.database.DBNoteAccessor;
import com.gerardogandeaga.cyberlock.database.loaders.NoteAdapterLoader;
import com.gerardogandeaga.cyberlock.database.objects.Folder;
import com.gerardogandeaga.cyberlock.database.objects.Note;
import com.gerardogandeaga.cyberlock.helpers.ActionModeManager;
import com.gerardogandeaga.cyberlock.interfaces.AdapterLoaderCallback;
import com.gerardogandeaga.cyberlock.items.NoteItem;
import com.gerardogandeaga.cyberlock.utils.Graphics;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.mikepenz.fastadapter.listeners.OnLongClickListener;
import com.mikepenz.materialdrawer.Drawer;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author gerardogandeaga on 2018-03-30.
 */
public class NoteActivity extends CoreActivity implements AdapterLoaderCallback, ActionModeManager.ActionManagerCallBack, NotePreviewDialog.EditSelectedPreview {
    @Override
    public void onLoaded(Folder folder) {
        mLoad.dismiss();
        mRecyclerView.setVisibility(View.VISIBLE);

        this.mCurrentFolder = folder;

        // only change action bar when adapter is finished
        actionbarFolderTitle();
    }

    @Override
    public void onRemoveSelections(ArrayList<Object> items) {
        // cast to note items
        ArrayList<NoteItem> noteItems = new ArrayList<>();
        for (Object item : items) {
            noteItems.add((NoteItem) item);
        }

        // handle trash
        ArrayList<Note> notes = new ArrayList<>();
        for (NoteItem noteItem : noteItems) {
            Note note = noteItem.getNote();
            // flag as trash
            note.withTrashed(true);
            // add to list
            notes.add(note);
        }

        // handle db interactions based on the manager condition
        switch (mActionModeManager.getCondition()) {
            case ActionModeManager.NORMAL:
                // update db
                DBNoteAccessor accessor = DBNoteAccessor.getInstance();
                accessor.update(notes);

                // titles
                mCurrentFolder.withSize(mCurrentFolder.getSize() - items.size());
                reloadFolderTitle();
                break;

            case ActionModeManager.ARCHIVE:
                break;

            case ActionModeManager.TRASH:
                break;
        }
    }

    @Override
    public void onEdit(Note note) {
        newIntent(NoteEditActivity.class);
        getNewIntent().putExtra("data", note);
        newIntentGoTo();
    }

    private Context mContext = this;

    private ActionModeManager<NoteItem> mActionModeManager;
    private Folder mCurrentFolder;
    // views
    private View mView;
    private CustomLoad mLoad;
    private RecyclerView mRecyclerView;
    private Drawer mDrawer;

    @BindView(R.id.fragment_container)
    FrameLayout mContainer;

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

        // click listeners

        itemAdapter.withOnPreClickListener(new OnClickListener<NoteItem>() {
            @Override
            public boolean onClick(@Nullable View view, @NonNull IAdapter<NoteItem> adapter, @NonNull NoteItem item, int position) {
                // try to select the item, it will be false if nothing was selected
                if (!mActionModeManager.isActive()) {
                    new NotePreviewDialog(mContext, item.getNote()).initializeDialog();
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

        // start fetching the notes
        new NoteAdapterLoader(this, itemAdapter).execute();

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

    private void actionbarFolderTitle() {
        actionBarIcon(R.drawable.ic_drawer);
        actionBarTitle((mCurrentFolder.getName().equals("MAIN") ? "All Notes" : mCurrentFolder.getName()));
        actionBarSubTitle(Integer.toString(mCurrentFolder.getSize()) + (mCurrentFolder.getSize() == 1 ? " Item" : " Items"));
    }

    private void reloadFolderTitle() {
        actionBarSubTitle(Integer.toString(mCurrentFolder.getSize()) + (mCurrentFolder.getSize() == 1 ? " Item" : " Items"));
    }

    public void onAddClicked(String noteType) {
        newIntent(NoteEditActivity.class);
        getNewIntent().putExtra("type", noteType);
        getNewIntent().putExtra("folder", mCurrentFolder.getName());
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
