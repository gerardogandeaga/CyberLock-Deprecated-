package com.gerardogandeaga.cyberlock.activities.core;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
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
import com.gerardogandeaga.cyberlock.activities.clearances.ActivityLogin;
import com.gerardogandeaga.cyberlock.activities.dialogs.DialogDataPreview;
import com.gerardogandeaga.cyberlock.sqlite.data.MasterDatabaseAccess;
import com.gerardogandeaga.cyberlock.sqlite.data.RawDataPackage;
import com.gerardogandeaga.cyberlock.support.LogoutProtocol;
import com.gerardogandeaga.cyberlock.support.adapter.DataItemView;
import com.gerardogandeaga.cyberlock.support.graphics.DrawableColours;
import com.gerardogandeaga.cyberlock.support.handlers.extractors.RawDataListItemHandler;
import com.gerardogandeaga.cyberlock.support.handlers.selection.AdapterItemHandler;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.mikepenz.fastadapter.listeners.OnLongClickListener;

import java.util.List;

import static com.gerardogandeaga.cyberlock.support.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.support.LogoutProtocol.APP_LOGGED_IN;
import static com.gerardogandeaga.cyberlock.support.LogoutProtocol.mIsCountDownTimerFinished;
import static com.gerardogandeaga.cyberlock.support.LogoutProtocol.mCountDownTimer;

public class ActivityMain extends AppCompatActivity implements View.OnClickListener {
    private Context mContext = this;

    // Adapter
    private FastItemAdapter<DataItemView> mFastItemAdapter;
    private View mView;

    // Views
    private Menu mMenu;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;

    // Initial on create methods
    @Override
    public void onCreate(Bundle savedInstanceState) {
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        ACTIVITY_INTENT = null;
        super.onCreate(savedInstanceState);

        View view = View.inflate(this, R.layout.activity_main, null);
        this.mView = view;

        setContentView(view);
        setupSupportActionBar();

        // Pull data list from database
        MasterDatabaseAccess masterDatabaseAccess = MasterDatabaseAccess.getInstance(this);
        masterDatabaseAccess.open();
        List<RawDataPackage> rawDataPackageList = masterDatabaseAccess.getAllData();
        masterDatabaseAccess.close();

        // Create the FastAdapter
        this.mFastItemAdapter = new FastItemAdapter<>();

        // Configure the FastAdapter
        mFastItemAdapter.setHasStableIds(true);
        mFastItemAdapter.withSelectable(true);
        mFastItemAdapter.withMultiSelect(true);
        mFastItemAdapter.withSelectOnLongClick(true);

        // Item Listeners
        this.mFastItemAdapter.withOnClickListener(new OnClickListener<DataItemView>() {
            @Override
            public boolean onClick(@NonNull View view, @NonNull IAdapter<DataItemView> adapter, @NonNull DataItemView item, int position) {
                // Perform normal on click if it is active and if it return false then continue to next step
                if (!AdapterItemHandler.onClick(mFastItemAdapter, item, position)) {
                    new DialogDataPreview(mContext, item.mRawDataPackage).initializeDialog();
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
        this.mFastItemAdapter.withOnLongClickListener(new OnLongClickListener<DataItemView>() {
            @Override
            public boolean onLongClick(@NonNull View view, @NonNull IAdapter<DataItemView> adapter, @NonNull DataItemView item, int position) {
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
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mFastItemAdapter);

        // Derive adapter DataItems from rawDataPackageList
        List<DataItemView> dataItemViewList = new RawDataListItemHandler().getDataItems(this, rawDataPackageList);
        mFastItemAdapter.add(dataItemViewList);


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
            getMenuInflater().inflate(R.menu.menu_multiselect, mMenu);  // Inflate multi select menu
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle(null);

        // Drawer Layout
        this.mDrawerLayout = findViewById(R.id.Data);
        this.mNavigationView = findViewById(R.id.NavigationContent);
        this.mDrawerToggle = new ActionBarDrawerToggle(this, this.mDrawerLayout, R.string.DRAWER_OPEN, R.string.DRAWER_CLOSE);
        this.mDrawerToggle.setDrawerIndicatorEnabled(false);
        //
        getSupportActionBar().setHomeAsUpIndicator(DrawableColours.mutateHomeAsUpIndicatorDrawable(
                this, this.getResources().getDrawable(R.drawable.ic_drawer)));

        computeNavViewSize();
        this.mDrawerToggle.syncState();
        this.mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return false;
            }
        });
    }
    private void resetSupportActionBar() {
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setSubtitle(null);
        getSupportActionBar().setHomeAsUpIndicator(DrawableColours.mutateHomeAsUpIndicatorDrawable(
                this, this.getResources().getDrawable(R.drawable.ic_drawer)));
    }
    //
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

    // Global clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (this.mDrawerToggle.onOptionsItemSelected(item)) return true;
        switch (item.getItemId()) {
            // Misc
            case android.R.id.home:
                if (!AdapterItemHandler.isActive()) {
                    if (this.mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
                        this.mDrawerLayout.closeDrawer(GravityCompat.START);
                    } else {
                        this.mDrawerLayout.openDrawer(GravityCompat.START);
                    }
                } else {
                    onBackPressed();
                }
                return true;

            // On multi select mode
            case R.id.action_delete:
                AdapterItemHandler.onDelete(this, mFastItemAdapter, mView);
                onCreateOptionsMenu(mMenu);
                resetSupportActionBar();
                return true;

            // New sub menu
//            case R.id.acNote:        onAddClicked("TYPE_NOTE"); return true;
//            case R.id.acPaymentInfo: onAddClicked("TYPE_PAYMENTINFO"); return true;
//            case R.id.acLoginInfo:   onAddClicked("TYPE_LOGININFO"); return true;

            // Options sub menu
            case R.id.acSettings: onSettings(); return true;
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
            if (ACTIVITY_INTENT == null) // NO PENDING ACTIVITIES ???(MAIN)--->(EDIT)???
            {
                new LogoutProtocol().logoutExecuteAutosaveOff(mContext);
            }
        }
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------

    // TODO Pull up panel android
}