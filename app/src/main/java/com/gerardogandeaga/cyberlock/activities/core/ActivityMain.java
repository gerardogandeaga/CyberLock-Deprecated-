package com.gerardogandeaga.cyberlock.activities.core;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.activities.clearances.LoginActivity;
import com.gerardogandeaga.cyberlock.activities.dialogs.DialogDataPreview;
import com.gerardogandeaga.cyberlock.sqlite.data.MasterDatabaseAccess;
import com.gerardogandeaga.cyberlock.sqlite.data.RawData;
import com.gerardogandeaga.cyberlock.support.Globals;
import com.gerardogandeaga.cyberlock.support.LogoutProtocol;
import com.gerardogandeaga.cyberlock.support.adapter.DataItem;
import com.gerardogandeaga.cyberlock.support.handlers.DataItemHandler;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import java.util.ArrayList;
import java.util.List;

import static com.gerardogandeaga.cyberlock.support.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.support.LogoutProtocol.APP_LOGGED_IN;
import static com.gerardogandeaga.cyberlock.support.LogoutProtocol.mCountDownIsFinished;
import static com.gerardogandeaga.cyberlock.support.LogoutProtocol.mCountDownTimer;

public class ActivityMain extends AppCompatActivity {
    private Context mContext = this;

    // RawData Variables
    private boolean mIsMultiChoice = false;
    private int mCount;
    private MasterDatabaseAccess mMasterDatabaseAccess;
    private ArrayList<RawData> mSelectedRawData;
    // Views
    private Menu mMenu;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;

    // Initial on create methods
    @Override public void onCreate(Bundle savedInstanceState) {
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        Globals.COLORSCHEME(this);
        super.onCreate(savedInstanceState);
        setupLayout();
    }
    @Override public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.mMenu = menu;
        //
        PorterDuff.Mode mode = PorterDuff.Mode.SRC_ATOP;
        //
        menu.findItem(R.id.miSelect).getIcon().mutate().setColorFilter(setMenuItemsColour(), mode);
        menu.findItem(R.id.mnuNew).getIcon().mutate().setColorFilter(setMenuItemsColour(), mode);
        menu.findItem(R.id.mnuOptions).getIcon().mutate().setColorFilter(setMenuItemsColour(), mode);

        return true;
    }
    //
    private void setupLayout() {
        ACTIVITY_INTENT = null;
        setContentView(R.layout.activity_main);
        // Appbar
        setupSupportActionBar();

        // Pull data list from database
        this.mMasterDatabaseAccess = MasterDatabaseAccess.getInstance(this);
        this.mMasterDatabaseAccess.open();
        List<RawData> rawDataList = this.mMasterDatabaseAccess.getAllData();
        this.mMasterDatabaseAccess.close();

        // Create and configure FastAdapter
        FastItemAdapter<DataItem> fastItemAdapter = new FastItemAdapter<>();

        fastItemAdapter.withOnClickListener(new OnClickListener<DataItem>() {
            @Override public boolean onClick(View view, IAdapter<DataItem> adapter, DataItem item, int position) {
                new DialogDataPreview(mContext, item.mRawData).initializeDialog();
                return false;
            }
        });

        // Setup and configure RecyclerView
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(fastItemAdapter);

        // Derive adapter DataItems from rawDataList
        List<DataItem> dataItemList = new DataItemHandler().getDataItems(this, rawDataList);
        fastItemAdapter.add(dataItemList);
    }
    private void setupSupportActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle(null);

        // Drawer Layout
        this.mDrawerLayout = (DrawerLayout) findViewById(R.id.Data);
        this.mNavigationView = (NavigationView) findViewById(R.id.NavigationContent);
        this.mDrawerToggle = new ActionBarDrawerToggle(this, this.mDrawerLayout, R.string.DRAWER_OPEN, R.string.DRAWER_CLOSE);
        this.mDrawerToggle.setDrawerIndicatorEnabled(false);
        //
        final Drawable drawerIcon = getResources().getDrawable(R.drawable.ic_drawer);
        drawerIcon.mutate().setColorFilter(setMenuItemsColour(), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(drawerIcon);

        computeNavViewSize();
        this.mDrawerToggle.syncState();
        this.mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                onNavigationItemClicked(item);
                return false;
            }
        });
    }
    private void computeNavViewSize() {
        Resources resources = getResources();
        DisplayMetrics metrics = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;

        float screenWidth = width / resources.getDisplayMetrics().density;
        float navWidth = screenWidth - 56;

        navWidth = Math.min(navWidth, 320);

        int newWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, navWidth, resources.getDisplayMetrics());

        DrawerLayout.LayoutParams params = (android.support.v4.widget.DrawerLayout.LayoutParams) this.mNavigationView.getLayoutParams();
        params.width = (newWidth);
        this.mNavigationView.setLayoutParams(params);
    }
    private int setMenuItemsColour() {
        return getResources().getColor(R.color.matLightWhiteYellow);
    }

    // Global clicks
    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (this.mDrawerToggle.onOptionsItemSelected(item)) return true;
        switch (item.getItemId()) {
            // Misc
            case android.R.id.home: if (this.mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
                this.mDrawerLayout.closeDrawer(GravityCompat.START);
            } else {
                this.mDrawerLayout.openDrawer(GravityCompat.START);
            }; return true;
            case R.id.miSelect: onMultiSelectClicked(); return true;

            // On multi select mode
            case R.id.action_delete: onDeleteClicked(); return true;
            case R.id.action_done: onMultiSelectClicked(); return true;

            // New sub menu
            case R.id.acNote: onAddClicked(1); return true;
            case R.id.acPaymentInfo: onAddClicked(2); return true;
            case R.id.acLoginInfo: onAddClicked(3); return true;

            // Options sub menu
            case R.id.acSettings: onSettings(); return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void onNavigationItemClicked(MenuItem menuItem) {
    }

    // Menu options functions
    // Notes, PaymentInfo, LoginInfo ################################
    public void onAddClicked(int TYPE) {
        ACTIVITY_INTENT = new Intent(this, ActivityEdit.class);
        ACTIVITY_INTENT.putExtra("type", TYPE);
        this.finish();
        this.startActivity(ACTIVITY_INTENT);
    }
    // ----------------
    public void onMultiSelectClicked() {
        PorterDuff.Mode mode = PorterDuff.Mode.SRC_ATOP;
        //
        if (!this.mIsMultiChoice) {
            this.mSelectedRawData = new ArrayList<>();

            this.mMenu.clear();
            getMenuInflater().inflate(R.menu.menu_multiselect, this.mMenu);

            MenuItem delete = this.mMenu.findItem(R.id.action_delete);
            MenuItem done = this.mMenu.findItem(R.id.action_done);
            delete.getIcon().mutate().setColorFilter(setMenuItemsColour(), mode);
            done.getIcon().mutate().setColorFilter(setMenuItemsColour(), mode);

            delete.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            done.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            getSupportActionBar().setTitle(this.mCount + " Selected");

            this.mIsMultiChoice = true;
        } else {
            this.mSelectedRawData = null;
            this.mCount = 0;

            this.mMenu.clear();
            getMenuInflater().inflate(R.menu.menu_main, this.mMenu);

            MenuItem multiSelect = this.mMenu.findItem(R.id.miSelect);
            MenuItem add = this.mMenu.findItem(R.id.mnuNew);
            MenuItem options = this.mMenu.findItem(R.id.mnuOptions);
            multiSelect.getIcon().mutate().setColorFilter(setMenuItemsColour(), mode);
            add.getIcon().mutate().setColorFilter(setMenuItemsColour(), mode);
            options.getIcon().mutate().setColorFilter(setMenuItemsColour(), mode);

            multiSelect.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            add.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            options.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            getSupportActionBar().setTitle(null);

            this.mIsMultiChoice = false;
        }
    } // TODO Better streamline the multi select and reset vies of the list items
    public void onDeleteClicked() {
        if (!this.mSelectedRawData.isEmpty()) {
            this.mMasterDatabaseAccess.open();
            for (RawData id : this.mSelectedRawData) {
                this.mMasterDatabaseAccess.delete(id);
            }
            this.mMasterDatabaseAccess.close();
            onMultiSelectClicked();
        }
    }
    // ActivitySettings, About ##############################################
    private void onSettings() {
        ACTIVITY_INTENT = new Intent(this, ActivitySettings.class);
        this.finish();
        this.startActivity(ACTIVITY_INTENT);
        overridePendingTransition(R.anim.anim_slide_inright, R.anim.anim_slide_outleft);
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

        if (mCountDownIsFinished) {
            if (!APP_LOGGED_IN) {
                ACTIVITY_INTENT = new Intent(this, LoginActivity.class);
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
        super.onBackPressed();
        if (!this.mIsMultiChoice)
            if (ACTIVITY_INTENT == null) // NO PENDING ACTIVITIES ???(MAIN)--->(EDIT)???
            {
                new LogoutProtocol().logoutImmediate(this);
            }
    }
    @Override public void onPause() {
        super.onPause();

        if (!this.isFinishing()) // HOME AND TABS AND SCREEN OFF
        {
            if (ACTIVITY_INTENT == null) // NO PENDING ACTIVITIES ???(MAIN)--->(EDIT)???
            {
                new LogoutProtocol().logoutExecuteAutosaveOff(mContext);
            }
        }
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------

    // TODO Pull up panel android
}