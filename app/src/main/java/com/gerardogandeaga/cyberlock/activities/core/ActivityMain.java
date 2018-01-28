package com.gerardogandeaga.cyberlock.activities.core;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.activities.clearances.ActivityLogin;
import com.gerardogandeaga.cyberlock.activities.dialogs.DialogDataPreview;
import com.gerardogandeaga.cyberlock.activities.dialogs.DialogOptions;
import com.gerardogandeaga.cyberlock.sqlite.data.DBAccess;
import com.gerardogandeaga.cyberlock.sqlite.data.DataPackage;
import com.gerardogandeaga.cyberlock.support.LogoutProtocol;
import com.gerardogandeaga.cyberlock.support.Stored;
import com.gerardogandeaga.cyberlock.support.graphics.DrawableColours;
import com.gerardogandeaga.cyberlock.support.graphics.Themes;
import com.gerardogandeaga.cyberlock.support.handlers.extractors.RecyclerViewItemDataHandler;
import com.gerardogandeaga.cyberlock.support.handlers.selection.AdapterItemHandler;
import com.gerardogandeaga.cyberlock.support.recyclerview.decorations.RecyclerViewPaddingItemDecoration;
import com.gerardogandeaga.cyberlock.support.recyclerview.items.RecyclerViewItem;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.mikepenz.fastadapter.listeners.OnLongClickListener;

import java.util.List;

import static com.gerardogandeaga.cyberlock.support.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.support.LogoutProtocol.APP_LOGGED_IN;
import static com.gerardogandeaga.cyberlock.support.LogoutProtocol.mCountDownTimer;
import static com.gerardogandeaga.cyberlock.support.LogoutProtocol.mIsCountDownTimerFinished;

public class ActivityMain extends AppCompatActivity implements View.OnClickListener, DialogOptions.OnInputListener {
    private static final String TAG = "ActivityMain";

    @Override
    public void sendInput(boolean bool) {
        if (bool) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            removeRecyclerViewDecorations();
            mRecyclerView.addItemDecoration(new RecyclerViewPaddingItemDecoration(8, true));
        } else {
            mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager( 2, GridLayoutManager.VERTICAL));
            removeRecyclerViewDecorations();
            mRecyclerView.addItemDecoration(new RecyclerViewPaddingItemDecoration(8, false));
        }
    }

    private Context mContext = this;

    // Adapter
    private FastItemAdapter<RecyclerViewItem> mFastItemAdapter;
    private View mView;

    // Views
    private Menu mMenu;
    public RecyclerView mRecyclerView;

    // Initial on create methods
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Themes.setTheme(this);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        ACTIVITY_INTENT = null;
        super.onCreate(savedInstanceState);

        // set view
        this.mView = View.inflate(this, R.layout.activity_main, null);
        setContentView(mView);
        setupSupportActionBar();

        // Pull data list from database
        DBAccess dbAccess = DBAccess.getInstance(this);
        dbAccess.open();
        List<DataPackage> dataPackageList = dbAccess.getAllData();
        dbAccess.close();

        // Create the FastAdapter
        this.mFastItemAdapter = new FastItemAdapter<>();

        // Configure the FastAdapter
        mFastItemAdapter.setHasStableIds(true);
        mFastItemAdapter.withSelectable(true);
        mFastItemAdapter.withMultiSelect(true);
        mFastItemAdapter.withSelectOnLongClick(true);

        // Item Listeners
        this.mFastItemAdapter.withOnClickListener(new OnClickListener<RecyclerViewItem>() {
            @Override
            public boolean onClick(@NonNull View view, @NonNull IAdapter<RecyclerViewItem> adapter, @NonNull RecyclerViewItem item, int position) {
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
        this.mFastItemAdapter.withOnLongClickListener(new OnLongClickListener<RecyclerViewItem>() {
            @Override
            public boolean onLongClick(@NonNull View view, @NonNull IAdapter<RecyclerViewItem> adapter, @NonNull RecyclerViewItem item, int position) {
                if (!AdapterItemHandler.isActive()) {
                    AdapterItemHandler.onLongClick(mFastItemAdapter, item, position);
                    onCreateOptionsMenu(mMenu);

                    getSupportActionBar().setTitle(Integer.toString(AdapterItemHandler.getCount()));
                    getSupportActionBar().setSubtitle("Items Selected");
                    getSupportActionBar().setHomeAsUpIndicator(DrawableColours.mutateHomeAsUpIndicatorDrawable(
                            mContext, mContext.getResources().getDrawable(R.drawable.ic_back)));

                    return true;
                }
                return false;
            }
        });

        // Setup and configure RecyclerView
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        mRecyclerView = findViewById(R.id.recyclerView);
        // Set layout format
        switch (Stored.getListFormat(this)) {
            case "RV_STAGGEREDGRID":
                mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
                mRecyclerView.addItemDecoration(new RecyclerViewPaddingItemDecoration(8, false)); break;
            default:
                mRecyclerView.setLayoutManager(linearLayoutManager);
                mRecyclerView.addItemDecoration(new RecyclerViewPaddingItemDecoration(8, true));break;
        }
        mRecyclerView.setAdapter(mFastItemAdapter); // Set adapter

        // Derive adapter DataItems from rawDataPackageList
        List<RecyclerViewItem> recyclerViewItemList = new RecyclerViewItemDataHandler().getDataItems(this, dataPackageList);
        mFastItemAdapter.add(recyclerViewItemList);


        FloatingActionMenu actionMenu = findViewById(R.id.fabAdd);
        actionMenu.setClosedOnTouchOutside(true);

        FloatingActionButton actionNote = findViewById(R.id.fabNote);
        FloatingActionButton actionPaymentInfo = findViewById(R.id.fabPaymentInfo);
        FloatingActionButton actionLoginInfo = findViewById(R.id.fabLoginInfo);
        actionNote.setOnClickListener(this);
        actionPaymentInfo.setOnClickListener(this);
        actionLoginInfo.setOnClickListener(this);
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
            DrawableColours.mutateMenuItems(this, menu);
        }

        return true;
    }
    //
    private void setupSupportActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false); // true
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setSubtitle(null);
    }
    private void resetSupportActionBar() {
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setSubtitle(null);
        getSupportActionBar().setHomeAsUpIndicator(DrawableColours.mutateHomeAsUpIndicatorDrawable(
                this, this.getResources().getDrawable(R.drawable.ic_drawer)));
    }

    public void removeRecyclerViewDecorations() {
        if (mRecyclerView != null) {
            if (mRecyclerView.getItemDecorationCount() != 0) {
                for (int i = 0; i < mRecyclerView.getItemDecorationCount(); i++) {
                    mRecyclerView.removeItemDecorationAt(i);
                }
            }
        }
    }

    // Global clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if (this.mDrawerToggle.onOptionsItemSelected(item)) return true;
        switch (item.getItemId()) {
            // Options
            case R.id.option_options: onSettings(); return true;

            // Misc
            case android.R.id.home: onBackPressed(); return true;

            // On multi select mode
            case R.id.option_delete:
                AdapterItemHandler.onDelete(this, mFastItemAdapter, mView);
                onCreateOptionsMenu(mMenu);
                resetSupportActionBar();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabNote:        onAddClicked("TYPE_NOTE"); break;
            case R.id.fabPaymentInfo: onAddClicked("TYPE_PAYMENTINFO"); break;
            case R.id.fabLoginInfo:   onAddClicked( "TYPE_LOGININFO"); break;
        }
    }

    // Menu options functions
    // Notes, PaymentInfo, LoginInfo ################################
    public void onAddClicked(String TYPE) {
        ACTIVITY_INTENT = new Intent(this, ActivityEdit.class);
        ACTIVITY_INTENT.putExtra("type", TYPE);
        this.finish();
        this.startActivity(ACTIVITY_INTENT);
    }

    // ActivitySettings, About ##############################################
    private void onSettings() {
//        new DialogOptions(this);

        DialogOptions dialogOptions = new DialogOptions();
        dialogOptions.show(getFragmentManager(), "DialogOptions");
    }
    private void onPlayground() {
        ACTIVITY_INTENT = new Intent(this, ActivityPlayground.class);
        this.finish();
        this.startActivity(ACTIVITY_INTENT);
        overridePendingTransition(R.anim.anim_slide_inright, R.anim.anim_slide_outleft);
    }
    // ----------------

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