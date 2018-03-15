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
import com.gerardogandeaga.cyberlock.core.handlers.extractors.RecyclerViewItemDataHandler;
import com.gerardogandeaga.cyberlock.core.handlers.selection.AdapterItemHandler;
import com.gerardogandeaga.cyberlock.core.recyclerview.CustomRecyclerView;
import com.gerardogandeaga.cyberlock.core.recyclerview.decorations.RecyclerViewItemDecoration;
import com.gerardogandeaga.cyberlock.core.recyclerview.items.RecyclerViewItem;
import com.gerardogandeaga.cyberlock.database.DataPackage;
import com.gerardogandeaga.cyberlock.database.loader.DataLoader;
import com.gerardogandeaga.cyberlock.overlay.LoadOverlay;
import com.gerardogandeaga.cyberlock.utils.Res;
import com.gerardogandeaga.cyberlock.utils.Settings;
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

public class ActivityMain extends AppCompatActivity implements DataLoader.OnDataPackageLoaded {
    // adapter package loading
    @Override
    public void sendPackage(final DataPackage dataPackage) {
        // arrange data as a recycler view item
        final RecyclerViewItem item = new RecyclerViewItemDataHandler(this).getDataItem(dataPackage);

        // add to adapter
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (item!= null) {
                    mFastItemAdapter.add(item);
                }

                // dismiss load overlay and show recycler view
                if (mLoadOverlay.isVisible()) {
                    mLoadOverlay.dismiss();
                    mRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private Context mContext = this;
    // adapter
    private FastItemAdapter<RecyclerViewItem> mFastItemAdapter;
    private View mView;
    // views
    private Menu mMenu;
    // load view
    private LoadOverlay mLoadOverlay;

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

        // configure the FastAdapter
        mFastItemAdapter.setHasStableIds(true);
        mFastItemAdapter.withSelectable(true);
        mFastItemAdapter.withMultiSelect(true);
        mFastItemAdapter.withSelectOnLongClick(true);

        // item Listeners
        mFastItemAdapter.withOnClickListener(new OnClickListener<RecyclerViewItem>() {
            @Override
            public boolean onClick(View view, @NonNull IAdapter<RecyclerViewItem> adapter, @NonNull RecyclerViewItem item, int position) {
                // Perform normal on click if it is active and if it return false then continue to next step
                if (!AdapterItemHandler.onClick(mFastItemAdapter, item, position)) {
                    new DialogDataPreview(mContext, item.mDataPackage).initializeDialog();
                } else {
                    getSupportActionBar().setTitle(Integer.toString(AdapterItemHandler.getCount()));
                }

                // Check if there is still data in array, if not then reset the action bar
                if (!AdapterItemHandler.isValid()) {
                    onCreateOptionsMenu(mMenu);
                    resetSupportActionBar();
                }

                return true;
            }
        });
        mFastItemAdapter.withOnLongClickListener(new OnLongClickListener<RecyclerViewItem>() {
            @Override
            public boolean onLongClick(@NonNull View view, @NonNull IAdapter<RecyclerViewItem> adapter, @NonNull RecyclerViewItem item, int position) {
                if (!AdapterItemHandler.isActive()) {
                AdapterItemHandler.onLongClick(mFastItemAdapter, item, position);
                onCreateOptionsMenu(mMenu);

                getSupportActionBar().setTitle(Integer.toString(AdapterItemHandler.getCount()));
                getSupportActionBar().setSubtitle("Items Selected");
                getSupportActionBar().setHomeAsUpIndicator(Graphics.BasicFilter.mutateHomeAsUpIndicatorDrawable(
                        mContext, Res.getDrawable(mContext, R.drawable.ic_back)));

                return true;
            }
                return false;
            }
        });

        // set adapter
        mRecyclerView.setAdapter(mFastItemAdapter);

        // initialize and execute data loader task
        DataLoader dataLoader = new DataLoader(this);
        dataLoader.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.mMenu = menu;

        // Clear menus
        if (mMenu != null && mMenu.hasVisibleItems()) {
            mMenu.clear();
        }

        // Check is multi select mode is active
        if (!AdapterItemHandler.isActive()) { // If not in multi select mode
            getMenuInflater().inflate(R.menu.menu_main, mMenu);         // Inflate main menu
        } else { // If multi select mode is active
            getMenuInflater().inflate(R.menu.menu_delete, mMenu);  // Inflate multi select menu
        }

        if (mMenu.hasVisibleItems()) {
            Graphics.BasicFilter.mutateMenuItems(this, menu);
        }

        return true;
    }
    //
    private void setupRecyclerView() {
        // Setup and configure RecyclerView
        mRecyclerView.setVisibility(View.GONE);
        final LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        final StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        // Set layout format
        switch (Settings.getListFormat(mContext)) {
            case "RV_STAGGEREDGRID":
                staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
                mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
                mRecyclerView.addItemDecoration(new RecyclerViewItemDecoration(10, false)); break;
            default:
                mRecyclerView.setLayoutManager(linearLayoutManager);
                mRecyclerView.addItemDecoration(new RecyclerViewItemDecoration(10, true)); break;
        }
    }
    private void displayLoad() {
        this.mLoadOverlay = new LoadOverlay(this, mView);
        mLoadOverlay.setTitle("Loading Data");
        mLoadOverlay.show(R.id.container);
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
                this, Res.getDrawable(this, R.drawable.ic_drawer)));
    }


    // Global clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if (this.mDrawerToggle.onOptionsItemSelected(item)) return true;
        switch (item.getItemId()) {
            // add
            case R.id.add_note:        onAddClicked(DataPackage.NOTE); break;
            case R.id.add_paymentinfo: onAddClicked(DataPackage.PAYMENT_INFO); break;
            case R.id.add_logininfo:   onAddClicked(DataPackage.LOGIN_INFO); break;

            // options
            case R.id.option_options: onOptions(); return true;

            // misc
            case android.R.id.home: onBackPressed(); return true;

            // on multi select mode
            case R.id.option_delete:
                AdapterItemHandler.onDelete(this, mFastItemAdapter, mView);
                onCreateOptionsMenu(mMenu);
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
        if (!AdapterItemHandler.isActive()) {
            if (ACTIVITY_INTENT == null) { // NO PENDING ACTIVITIES ???(MAIN)--->(EDIT)???
                new LogoutProtocol().logoutImmediate(this);
            }
        } else {
            AdapterItemHandler.cancel(mFastItemAdapter);
            onCreateOptionsMenu(mMenu);
            resetSupportActionBar();

            return;
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

    // TODO Pull up panel android
}