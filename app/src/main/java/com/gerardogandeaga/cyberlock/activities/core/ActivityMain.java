package com.gerardogandeaga.cyberlock.activities.core;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.activities.clearances.ActivityLogin;
import com.gerardogandeaga.cyberlock.activities.dialogs.DialogDataPreview;
import com.gerardogandeaga.cyberlock.android.CustomRecyclerView;
import com.gerardogandeaga.cyberlock.android.CustomSnackBar;
import com.gerardogandeaga.cyberlock.android.CustomLoad;
import com.gerardogandeaga.cyberlock.core.handlers.extractors.NoteItemContentHandler;
import com.gerardogandeaga.cyberlock.core.handlers.selection.AdapterActionHandler;
import com.gerardogandeaga.cyberlock.core.handlers.selection.actions.DataObjectDeleter;
import com.gerardogandeaga.cyberlock.core.recyclerview.decorations.NoteItemDecoration;
import com.gerardogandeaga.cyberlock.core.recyclerview.items.NoteItem;
import com.gerardogandeaga.cyberlock.database.loaders.NoteLoader;
import com.gerardogandeaga.cyberlock.database.objects.NoteObject;
import com.gerardogandeaga.cyberlock.utils.ListFormat;
import com.gerardogandeaga.cyberlock.utils.SharedPreferences;
import com.gerardogandeaga.cyberlock.utils.Resources;
import com.gerardogandeaga.cyberlock.utils.graphics.Graphics;
import com.gerardogandeaga.cyberlock.utils.security.LogoutProtocol;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.mikepenz.fastadapter.listeners.OnLongClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.gerardogandeaga.cyberlock.utils.security.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.utils.security.LogoutProtocol.APP_LOGGED_IN;
import static com.gerardogandeaga.cyberlock.utils.security.LogoutProtocol.mCountDownTimer;
import static com.gerardogandeaga.cyberlock.utils.security.LogoutProtocol.mIsCountDownTimerFinished;

public class ActivityMain extends AppCompatActivity implements NoteLoader.OnDataPackageLoaded {
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
    private AdapterActionHandler<NoteItem> mAdapterActionHandler;
    // views
    private View mView;
    private Menu mMenu;
    // load view
    private CustomLoad mCustomLoad;

    @BindView(R.id.toolbar)      Toolbar mToolbar;
    @BindView(R.id.recyclerView) CustomRecyclerView mRecyclerView;

    // initial on create methods
    @Override
    public void onCreate(Bundle savedInstanceState) {
//        Themes.setTheme(this);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        ACTIVITY_INTENT = null;
        super.onCreate(savedInstanceState);

        // set view
        this.mView = View.inflate(this, R.layout.activity_main, null);
        setContentView(mView);
        ButterKnife.bind(this);

        setupSupportActionBar();
        setupRecyclerView();
        displayLoad();

        // create the FastAdapter
        this.mFastItemAdapter = new FastItemAdapter<>();
        this.mAdapterActionHandler = new AdapterActionHandler<>(this, mFastItemAdapter);

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
                if (mAdapterActionHandler.isActive()) {
                    // selection mode
                    mAdapterActionHandler.toggle(position);
                    setTitleCount(mAdapterActionHandler.selectedCount());

                    if (mAdapterActionHandler.noneAreSelected()) {
                        mAdapterActionHandler.deactivate();
                        getSupportActionBar().setTitle(null);
                        getSupportActionBar().setSubtitle(null);
                    }

                    updateMenu();

                } else {
                    // preview data
                    new DialogDataPreview(mContext, item.getNoteObject()).initializeDialog();
                }

                return true;
            }
        });
        mFastItemAdapter.withOnLongClickListener(new OnLongClickListener<NoteItem>() {
            @Override
            public boolean onLongClick(@NonNull View view, @NonNull IAdapter<NoteItem> adapter, @NonNull NoteItem item, int position) {
                if (!mAdapterActionHandler.isActive()) {
                    mAdapterActionHandler.activate();
                    mAdapterActionHandler.toggle(position);
                    setTitleCount(mAdapterActionHandler.selectedCount());

                } else if (mAdapterActionHandler.isActive()) {
                    mAdapterActionHandler.toggle(position);
                    setTitleCount(mAdapterActionHandler.selectedCount());
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
        if (mAdapterActionHandler.isActive()) {
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
    private void setupSupportActionBar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false); // true
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setSubtitle(null);
    }
    private void resetSupportActionBar() {
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setSubtitle(null);
        getSupportActionBar().setHomeAsUpIndicator(Graphics.BasicFilter.mutateHomeAsUpIndicatorDrawable(
                this, Resources.getDrawable(this, R.drawable.ic_drawer)));
    }
    private void setTitleCount(int i) {
        getSupportActionBar().setTitle(Integer.toString(i));
        if (i == 1) {
            getSupportActionBar().setSubtitle("Item Selected");
        } else {
            getSupportActionBar().setSubtitle("Items Selected");
        }
    }

    // Global clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if (this.mDrawerToggle.onOptionsItemSelected(item)) return true;
        switch (item.getItemId()) {
            // add
            case R.id.add_note:
                onAddClicked(NoteObject.NOTE);
                break;
            case R.id.add_paymentinfo:
                onAddClicked(NoteObject.PAYMENT_INFO);
                break;
            case R.id.add_logininfo:
                onAddClicked(NoteObject.LOGIN_INFO);
                break;

            // options
            case R.id.option_options:
                onOptions();
                return true;

            // misc
            case android.R.id.home:
                onBackPressed();
                return true;

            // on multi select mode
            case R.id.option_delete:
                // deleter
                final DataObjectDeleter deleter = new DataObjectDeleter(this, mFastItemAdapter, mAdapterActionHandler.removeItemsFromAdapter());
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
                mAdapterActionHandler.finish();
                updateMenu();
                resetSupportActionBar();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Menu options functions
    // Notes, PaymentInfo, LoginInfo ################################
    public void onAddClicked(String TYPE) {
        ACTIVITY_INTENT = new Intent(this, ActivityEdit.class);
        ACTIVITY_INTENT.putExtra("type", TYPE);
        this.finish();
        this.startActivity(ACTIVITY_INTENT);
    }

    // ActivityOptions, About ##############################################
    private void onOptions() {
        ACTIVITY_INTENT = new Intent(this, ActivityOptions.class);
        this.finish();
        this.startActivity(ACTIVITY_INTENT);
    }

    // THIS IS THE START OF THE SCRIPT FOR *** THE "TO LOGIN FUNCTION" THIS DETECTS THE ON PRESSED, START, TABS AND HOME BUTTONS IN ORDER TO INITIALIZE SECURITY "FAIL-SAFE"
    @Override public void onStart() {
        super.onStart();

        if (mIsCountDownTimerFinished) {
            if (!APP_LOGGED_IN) {
                ACTIVITY_INTENT = new Intent(this, ActivityLogin.class);
                this.finish(); // CLEAN UP AND END
                this.startActivity(ACTIVITY_INTENT); // GO TO LOGIN ACTIVITY
            }
        } else {
            if (mCountDownTimer != null) {
                mCountDownTimer.cancel();
            }
        }
    }
    @Override public void onBackPressed() {
        if (mAdapterActionHandler.isActive()) {
            mAdapterActionHandler.deactivate();
            updateMenu();
            resetSupportActionBar();

            return;
        } else {
            if (ACTIVITY_INTENT == null) { // NO PENDING ACTIVITIES ???(MAIN)--->(EDIT)???
                new LogoutProtocol().logoutImmediate(this);
            }
        }

        super.onBackPressed();
    }
    @Override public void onPause() {
        super.onPause();

        if (!this.isFinishing()) { // HOME AND TABS AND SCREEN OFF
            if (ACTIVITY_INTENT == null) { // NO PENDING ACTIVITIES ???(MAIN)--->(EDIT)???
                new LogoutProtocol().logoutExecuteAutosaveOff(mContext);
            }
        }
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
}