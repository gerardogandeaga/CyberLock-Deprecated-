package com.gerardogandeaga.cyberlock.activities.core;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.activities.clearances.ActivityLogin;
import com.gerardogandeaga.cyberlock.core.handlers.extractors.ExternalLibItemHandler;
import com.gerardogandeaga.cyberlock.core.recyclerview.decorations.ExternalLibItemDecoration;
import com.gerardogandeaga.cyberlock.core.recyclerview.items.ExternalLibItem;
import com.gerardogandeaga.cyberlock.utils.Res;
import com.gerardogandeaga.cyberlock.utils.graphics.Graphics;
import com.gerardogandeaga.cyberlock.utils.security.LogoutProtocol;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.gerardogandeaga.cyberlock.utils.security.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.utils.security.LogoutProtocol.APP_LOGGED_IN;
import static com.gerardogandeaga.cyberlock.utils.security.LogoutProtocol.mCountDownTimer;
import static com.gerardogandeaga.cyberlock.utils.security.LogoutProtocol.mIsCountDownTimerFinished;


public class ActivityExternalLibs extends AppCompatActivity {
    FastItemAdapter<ExternalLibItem> mFastItemAdapter;

    @BindView(R.id.toolbar)      Toolbar mToolbar;
    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ACTIVITY_INTENT = null;
        super.onCreate(savedInstanceState);

        View view = View.inflate(this, R.layout.activity_external_libraries, null);
        setContentView(view);
        ButterKnife.bind(this);

        setupSupportActionBar();

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

        List<ExternalLibItem> externalLibItemList = new ExternalLibItemHandler(this).getItems();

        mFastItemAdapter.add(externalLibItemList);

        System.out.println(externalLibItemList.toString());
        System.out.println(mFastItemAdapter.getItem(0));
        System.out.println(mFastItemAdapter.getItem(1));
        System.out.println(mFastItemAdapter.getItem(2));
    }

    private void setupSupportActionBar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Open Source Libraries");
        getSupportActionBar().setHomeAsUpIndicator(Graphics.BasicFilter.mutateHomeAsUpIndicatorDrawable(
                this, Res.getDrawable(this, R.drawable.ic_back)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    // THIS IS THE START OF THE SCRIPT FOR *** THE "TO LOGIN FUNCTION" THIS DETECTS THE ON PRESSED, START, TABS AND HOME BUTTONS IN ORDER TO INITIALIZE SECURITY "FAIL-SAFE"
    @Override protected void onStart() {
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
        super.onBackPressed();
        if (ACTIVITY_INTENT == null) { // NO PENDING ACTIVITIES ???(MAIN)--->(EDIT)???
            ACTIVITY_INTENT = new Intent(this, ActivityOptions.class);
            this.finish();
            this.startActivity(ACTIVITY_INTENT);
        }
    }
    @Override public void onPause() {
        super.onPause();

        if (!this.isFinishing()) { // HOME AND TABS AND SCREEN OFF
            if (ACTIVITY_INTENT == null) { // NO PENDING ACTIVITIES ???(MAIN)--->(EDIT)???
                new LogoutProtocol().logoutExecuteAutosaveOff(this);
            }
        }
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
}
