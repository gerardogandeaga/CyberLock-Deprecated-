package com.gerardogandeaga.cyberlock.Activitys.Activities.Main;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LoginActivity;
import com.gerardogandeaga.cyberlock.Crypto.CryptoContent;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.ContentDatabase.Data;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.ContentDatabase.MasterDatabaseAccess;
import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.Supports.Globals;
import com.gerardogandeaga.cyberlock.Supports.LogoutProtocol;
import com.gerardogandeaga.cyberlock.Supports.Settings_EncryptionMethodChange;
import com.gerardogandeaga.cyberlock.Supports.Settings_ScrambleKey;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import iammert.com.expandablelib.ExpandCollapseListener;
import iammert.com.expandablelib.ExpandableLayout;
import iammert.com.expandablelib.Section;

import static com.gerardogandeaga.cyberlock.Supports.Globals.AUTOSAVE;
import static com.gerardogandeaga.cyberlock.Supports.Globals.DELAY_TIME;
import static com.gerardogandeaga.cyberlock.Supports.Globals.DIRECTORY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.ENCRYPTION_ALGO;
import static com.gerardogandeaga.cyberlock.Supports.Globals.LOGOUT_DELAY;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.APP_LOGGED_IN;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.mCountDownIsFinished;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.mCountDownTimer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private int STATE = 0;
    private Context mContext = this;
    private SharedPreferences mSharedPreferences;

    // Data Variables
    private boolean mIsMultiChoice = false;
    private int mCount;
    private MasterDatabaseAccess mMasterDatabaseAccess;
    private ArrayList<Data> mSelectedDatas;
    private String mAutoLogoutDelay, mOldEncryptionMethod;
    // State
    private boolean mIsAutoSave;
    private ArrayAdapter<CharSequence> mAdapterAutoLogoutDelay,mAdapterEncryptionMethod;

    // Widgets
    private ImageButton mBtnNotes, mBtnPlayground, mBtnSettings;
    private CheckBox mCbAutoSave;
    private Spinner mSpAutoLogoutDelay, mSpEncryptionMethod;

    // Views
    private Menu mMenu;
    private LinearLayout mAnchorView;
    private NavigationView mNavigationView;
    private View mView;

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
        mMenu = menu;

        return true;
    }
    private void setupLayout() {
        ACTIVITY_INTENT = null;
        setContentView(R.layout.activity_main);
        // Appbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("");
        // Options
        mBtnNotes = (ImageButton) findViewById(R.id.btnNotes);
        mBtnSettings = (ImageButton) findViewById(R.id.btnSettings);
        //
        mBtnNotes.setOnClickListener(this);
        mBtnSettings.setOnClickListener(this);
        // Drawer Layout
        mNavigationView = (NavigationView) findViewById(R.id.NavigationContent);
        computeNavViewSize();
        navigationViewItems();
        // Main view
        mAnchorView = (LinearLayout) findViewById(R.id.AnchorView);
        createNotes();
    }
    private void computeNavViewSize() {
        Resources resources = getResources();
        DisplayMetrics metrics = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;

        float screenWidth = width / resources.getDisplayMetrics().density;
        float navWidth = screenWidth - 56;

        navWidth = Math.min(navWidth, 320); // b = 320

        int newWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, navWidth, resources.getDisplayMetrics());

        DrawerLayout.LayoutParams params = (android.support.v4.widget.DrawerLayout.LayoutParams) mNavigationView.getLayoutParams();
        params.width = (newWidth);
        mNavigationView.setLayoutParams(params);
    }
    private void navigationViewItems() {
        View view = mNavigationView.inflateHeaderView(R.layout.drawer_header); // Set the navigation header view and parse to 'View'
        ExpandableLayout el = (ExpandableLayout) view.findViewById(R.id.expandableLayout);

        el.setRenderer(new ExpandableLayout.Renderer<InfoCategory, Info>() {
            @Override public void renderParent(View v, InfoCategory ic, boolean b, int i) {
                ((TextView) v.findViewById(R.id.parentTitle)).setText(ic.title);
            }
            @Override public void renderChild(View v, Info io, int ip, int ic) {
                ((TextView) v.findViewById(R.id.childTitle)).setText(io.text);
            }
        });
        el.setExpandListener(new ExpandCollapseListener.ExpandListener<InfoCategory>() {
            @Override public void onExpanded(int i, InfoCategory ic, View v) {
                v.findViewById(R.id.expandableArrow).setRotation(90);
            }
        });
        el.setCollapseListener(new ExpandCollapseListener.CollapseListener<InfoCategory>() {
            @Override public void onCollapsed(int i, InfoCategory ic, View v) {
                v.findViewById(R.id.expandableArrow).setRotation(-90);
            }
        });

        el.addSection(sectionUpdates());
        el.addSection(sectionTechnology());
    } // Set expandable views
    // Expandable items
    private Section<InfoCategory, Info> sectionUpdates() {
        Section<InfoCategory, Info> section = new Section<>();
        InfoCategory ic = new InfoCategory(getString(R.string.UpdateTitle));

        List<Info> ioList = new ArrayList<>();

        ioList.add(new Info(getResources().getText(R.string.V1_0_0)));

        section.parent = ic;
        section.children.addAll(ioList);

        return section;
    } // Update board
    private Section<InfoCategory, Info> sectionTechnology() {
        Section<InfoCategory, Info> section = new Section<>();
        InfoCategory ic = new InfoCategory(getString(R.string.CyberLockTechTitle));

        List<Info> ioList = new ArrayList<>();

        ioList.add(new Info(getResources().getText(R.string.CyberLockTech)));

        section.parent = ic;
        section.children.addAll(ioList);

        return section;
    } // Cyber Lock's technology

    // Main click-listener
    @Override public void onClick(View v) {
        switch (v.getId()) {
            // Notes
            case R.id.btnNotes:
                STATE = 0;
                switchStates();
                break;
            case R.id.btnSettings:
                STATE = 1;
                switchStates();
                break;

            // Settings
            case R.id.AutoSave:
                onAutoSave();
                break;
            case R.id.ChangePasscode:
                onChangePasscode();
                break;
            case R.id.ScrambleKey:
                onScrambleKey();
                break;
        }
    }
    // Global clicks
    @Override public boolean onOptionsItemSelected(MenuItem item) {
//        if (mDrawerToggle.onOptionsItemSelected(item)) return true;
        switch (item.getItemId()) {
            // OPTIONS
            case R.id.action_select:
                onMultiSelectClicked();
            case R.id.action_delete:
                onDeleteClicked();
                return true;
            case R.id.action_done:
                onMultiSelectClicked();
                return true;

            // FUNTIONS
            case R.id.action_note:
                onAddClicked(1);
                return true;
            case R.id.action_paymentinfo:
                onAddClicked(2);
                return true;
            case R.id.action_logininfo:
                onAddClicked(3);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    // -------------------

    // Create views
    private void createNotes() {
        getSupportActionBar().setTitle("Notes");
        if (mMenu != null) getMenuInflater().inflate(R.menu.menu_main, mMenu);
        mBtnNotes.setBackgroundColor(getResources().getColor(R.color.gray_2L));
        //
        mMasterDatabaseAccess = MasterDatabaseAccess.getInstance(this);
        mMasterDatabaseAccess.open();
        List<Data> dlist = mMasterDatabaseAccess.getAllData();
        mMasterDatabaseAccess.close();
        //
        mView = View.inflate(this, R.layout.view_notes, null);
        mAnchorView.addView(mView);
        RecyclerView rv = (RecyclerView) findViewById(R.id.recyclerView);
        RVDataAdapter adapter = new RVDataAdapter(this, dlist);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
    }
    private void createSettings() {
        getSupportActionBar().setTitle("Settings");
        mBtnSettings.setBackgroundColor(getResources().getColor(R.color.gray_2L));
        //
        mSharedPreferences = getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE);
        mIsAutoSave = mSharedPreferences.getBoolean(AUTOSAVE, false);
        //
        mView = View.inflate(this, R.layout.view_settings, null);
        mAnchorView.addView(mView);
        //
        mSpAutoLogoutDelay = (Spinner) findViewById(R.id.spAutoLogoutDelay);
        mAdapterAutoLogoutDelay = ArrayAdapter.createFromResource(this, R.array.AutoLogoutDelay_array, R.layout.spinner_setting_text);
        mAdapterAutoLogoutDelay.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        mSpAutoLogoutDelay.setAdapter(mAdapterAutoLogoutDelay);

        mSpEncryptionMethod = (Spinner) findViewById(R.id.spEncryptionMethod);
        mAdapterEncryptionMethod = ArrayAdapter.createFromResource(this, R.array.CryptALGO_array, R.layout.spinner_setting_text);
        mAdapterEncryptionMethod.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        mSpEncryptionMethod.setAdapter(mAdapterEncryptionMethod);

        mCbAutoSave = (CheckBox) findViewById(R.id.cbAutoSave);
        RelativeLayout autoSave = (RelativeLayout) findViewById(R.id.AutoSave);
        RelativeLayout changePasscode = (RelativeLayout) findViewById(R.id.ChangePasscode);
        RelativeLayout scrambleKey = (RelativeLayout) findViewById(R.id.ScrambleKey);

        autoSave.setOnClickListener(this);
        changePasscode.setOnClickListener(this);
        scrambleKey.setOnClickListener(this);

        mCbAutoSave.setClickable(false);
        mCbAutoSave.setChecked(false);

        savedStates();

        this.mSpAutoLogoutDelay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object object = parent.getItemAtPosition(position);
                if (object != null) {
                    mAutoLogoutDelay = object.toString();
                    long time = 0;
                    switch (mAutoLogoutDelay) {
                        case "Immediate": time = 0; break;
                        case "15 Seconds": time = 15000; break;
                        case "30 Seconds": time = 30000; break;
                        case "1 Minute": time = 60000; break;
                        case "5 Minutes": time = 300000; break;
                        case "10 Minutes": time = 600000; break;
                        case "30 Minutes": time = 1800000; break;
                        case "1 Hour": time = 3600000; break;
                        case "2 Hours": time = 7200000; break;
                        case "Never": break;
                    }

                    System.out.println("Time = " + time);
                    mSharedPreferences.edit().putString(LOGOUT_DELAY, mAutoLogoutDelay).putLong(DELAY_TIME, time).apply();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        this.mSpEncryptionMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object object = parent.getItemAtPosition(position);
                if (object != null) {
                    String algorithm = object.toString();
                    if (algorithm.matches("AES - 256")) {
                        String newAlgorithm = "AES";
                        if (!mSharedPreferences.getString(ENCRYPTION_ALGO, "AES").matches(newAlgorithm)) {
                            onEncryptionMethodChange(newAlgorithm);
                        }
                    } else if (algorithm.matches("Blowfish - 448")) {
                        String newAlgorithm = "Blowfish";
                        if (!mSharedPreferences.getString(ENCRYPTION_ALGO, "AES").matches(newAlgorithm)) {
                            onEncryptionMethodChange(newAlgorithm);
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    // View managers
    private void switchStates() {
        cleanup();
        switch (STATE) {
            case 0:
                createNotes();
                break;
            case 1:
                createSettings();
                break;
        }
    }
    private void cleanup() {
        mMenu.clear();
        mBtnNotes.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        mBtnSettings.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        // Clean up notes
        mMasterDatabaseAccess  = null;
        mSelectedDatas = null;
        mIsMultiChoice = false;
        mCount = 0;
        // Clean up settings
        mSharedPreferences = null;
        mSpAutoLogoutDelay = null;
        mSpEncryptionMethod = null;
        mAdapterAutoLogoutDelay = null;
        mAdapterEncryptionMethod = null;
        mCbAutoSave = null;
        // View
        mAnchorView.removeAllViews();
    }

    // Notes ########################################################
    public void onAddClicked(int TYPE) {
        ACTIVITY_INTENT = new Intent(this, MainEditActivity.class);
        ACTIVITY_INTENT.putExtra("type", TYPE);
        this.finish();
        this.startActivity(ACTIVITY_INTENT);
    }
    public void onEditClicked(final Data data) {
        ACTIVITY_INTENT = new Intent(mContext, MainEditActivity.class);
        ACTIVITY_INTENT.putExtra("data", data);
        this.finish();
        this.startActivity(ACTIVITY_INTENT);
    }
    // ----------------
    public void onMultiSelectClicked() {
        if (!mIsMultiChoice) {
            mSelectedDatas = new ArrayList<>();
            onResume();

            mMenu.clear();
            getMenuInflater().inflate(R.menu.menu_delete, mMenu);

            MenuItem delete = mMenu.findItem(R.id.action_delete);
            MenuItem done = mMenu.findItem(R.id.action_done);

            delete.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            done.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            getSupportActionBar().setTitle(mCount + " Selected");

            mIsMultiChoice = true;
        } else {
            mSelectedDatas = null;
            mCount = 0;
            onResume();

            mMenu.clear();
            getMenuInflater().inflate(R.menu.menu_main, mMenu);

            MenuItem search = mMenu.findItem(R.id.action_search);
            MenuItem multiSelect = mMenu.findItem(R.id.action_select);
            MenuItem add = mMenu.findItem(R.id.subMenuAdd);

            search.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            multiSelect.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            add.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            getSupportActionBar().setTitle("Notes");

            mIsMultiChoice = false;
        }
    }
    public void onDeleteClicked() {
        if (!mSelectedDatas.isEmpty()) {
            mMasterDatabaseAccess.open();
            for (Data id : mSelectedDatas) {
                mMasterDatabaseAccess.delete(id);
            }
            mMasterDatabaseAccess.close();
            onMultiSelectClicked();
        }
    }
    // Settings #####################################################
    private void savedStates() {
        // CHECK BOXES
        if (!mIsAutoSave) {
            mCbAutoSave.setChecked(false);
        } else {
            mCbAutoSave.setChecked(true);
        }

        int delaySpinnerPosition = mAdapterAutoLogoutDelay.getPosition(mSharedPreferences.getString(LOGOUT_DELAY, "Immediate"));
        mSpAutoLogoutDelay.setSelection(delaySpinnerPosition);

        // ENCRYPTION METHOD
        int algoSpinnerPosition;
        switch (mSharedPreferences.getString(ENCRYPTION_ALGO, "AES")) {
            case "AES":
                algoSpinnerPosition = mAdapterEncryptionMethod.getPosition("AES - 256");
                mSpEncryptionMethod.setSelection(algoSpinnerPosition);
                mOldEncryptionMethod = mSpEncryptionMethod.getItemAtPosition(algoSpinnerPosition).toString();
                break;
            case "Blowfish":
                algoSpinnerPosition = mAdapterEncryptionMethod.getPosition("Blowfish - 448");
                mSpEncryptionMethod.setSelection(algoSpinnerPosition);
                mOldEncryptionMethod = mSpEncryptionMethod.getItemAtPosition(algoSpinnerPosition).toString();
                break;
        }
    }
    // ----------------
    private void onAutoSave() {
        final boolean autoSave = mSharedPreferences.getBoolean(AUTOSAVE, false);

        if (!autoSave) {
            mSharedPreferences.edit().putBoolean(AUTOSAVE, true).apply();
            mCbAutoSave.setChecked(true);
        } else {
            mSharedPreferences.edit().putBoolean(AUTOSAVE, false).apply();
            mCbAutoSave.setChecked(false);
        }
    }
    private void onChangePasscode() {
        View v = View.inflate(mContext, R.layout.dialog_view_passcode_change, null);
        // Dialog primitives
        ImageView icon = (ImageView) v.findViewById(R.id.imgDialogAction);
        TextView title = (TextView) v.findViewById(R.id.tvDialogTitle);
        Button negative = (Button) v.findViewById(R.id.btnDialogNegative);
        Button positive = (Button) v.findViewById(R.id.btnDialogPositive);
        icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_passcode));
        title.setText("Change Passcode");
        negative.setText("Cancel");
        positive.setText("Change");
        // ----------------- TODO password change
        EditText current = (EditText) v.findViewById(R.id.etCurrent);
        EditText initial = (EditText) v.findViewById(R.id.etInitial);
        EditText Final = (EditText) v.findViewById(R.id.etFinal);

        // DIALOG BUILDER
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
        builder.setView(v);
        final android.support.v7.app.AlertDialog dialog = builder.show();
        // -----------------------------------------------------------
        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        // -----------------------------------------------------------
    }
    private void onScrambleKey() {
        View v = View.inflate(mContext, R.layout.dialog_view_alert_info, null);
        // Dialog primitives
        ImageView icon = (ImageView) v.findViewById(R.id.imgDialogAction);
        TextView title = (TextView) v.findViewById(R.id.tvDialogTitle);
        Button negative = (Button) v.findViewById(R.id.btnDialogNegative);
        Button positive = (Button) v.findViewById(R.id.btnDialogPositive);
        icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_key));
        title.setText("Scramble Encryption Key");
        negative.setText("Cancel");
        positive.setText("Scramble");
        // -----------------
        TextView alertText = (TextView) v.findViewById(R.id.tvDialogAlertText);
        alertText.setText(R.string.AlertDialog_ScrambleKey);
        // DIALOG BUILDER
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
        builder.setView(v);
        final android.support.v7.app.AlertDialog dialog = builder.show();
        // -----------------------------------------------------------
        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                new Settings_ScrambleKey(mContext).execute();
            }
        });
        // -----------------------------------------------------------
    }
    private void onEncryptionMethodChange(final String algorithm) {
        View v = View.inflate(mContext, R.layout.dialog_view_alert_info, null);
        // Dialog primitives
        ImageView icon = (ImageView) v.findViewById(R.id.imgDialogAction);
        TextView title = (TextView) v.findViewById(R.id.tvDialogTitle);
        Button negative = (Button) v.findViewById(R.id.btnDialogNegative);
        Button positive = (Button) v.findViewById(R.id.btnDialogPositive);
        icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_shield));
        title.setText("Change Encryption Method");
        negative.setText("Cancel");
        positive.setText("Change");
        // -----------------
        TextView alertText = (TextView) v.findViewById(R.id.tvDialogAlertText);
        alertText.setText(R.string.AlertDialog_EncryptionMethodChange);
        // DIALOG BUILDER
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
        builder.setView(v);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mSpEncryptionMethod.setSelection(mAdapterEncryptionMethod.getPosition(mOldEncryptionMethod));
            }
        });
        final android.support.v7.app.AlertDialog dialog = builder.show();
        // -----------------------------------------------------------
        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mSpEncryptionMethod.setSelection(mAdapterEncryptionMethod.getPosition(mOldEncryptionMethod));
            }
        });
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                new Settings_EncryptionMethodChange(mContext, algorithm).execute();
            }
        });
        // -----------------------------------------------------------
    }
    // ##############################################################

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
        if (!mIsMultiChoice)
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

    // Inner recycler view adapter class
    private class RVDataAdapter extends android.support.v7.widget.RecyclerView.Adapter<RVDataAdapter.ViewHolder> {
        private Context mContext;
        private List<Data> mDatas = Collections.emptyList();

        RVDataAdapter(Context context, List<Data> datas) {
            mContext = context;
            mDatas = datas;
        }

        @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_list_item, parent, false);
            return new ViewHolder(v);
        }
        @Override public void onBindViewHolder(ViewHolder vh, int position) {
            CryptoContent cc = new CryptoContent(mContext);
            final Data data = mDatas.get(position);
            String TYPE = data.getType(cc);
            switch (TYPE) {
                case "TYPE_NOTE":
                    noteFunctions(vh, data, cc, TYPE);
                    break;
                case "TYPE_PAYMENTINFO":
                    paymentInfoFunctions(vh, data, cc, TYPE);
                    break;
                case "TYPE_LOGININFO":
                    loginInfoFunctions(vh, data, cc, TYPE);
                    break;
                default:
                    Toast.makeText(mContext, "Error with data type", Toast.LENGTH_SHORT).show();
                    break;
            }
            if (!mIsMultiChoice) {
                vh.checkbox.setClickable(false);
                vh.checkbox.setVisibility(View.GONE);
            } else {
                vh.checkbox.setClickable(true);
                vh.checkbox.setVisibility(View.VISIBLE);
            }
        }
        @Override public int getItemCount() {
            return mDatas.size();
        }
        @Override public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        // Functions for the data adapter
        private void noteFunctions(final ViewHolder vh, final Data d, CryptoContent cc, final String TYPE) {
            final String date = d.getDate();
            final String colourTag = d.getColourTag(cc);
            final String label = d.getLabel(cc);
            final String content = d.getContent(cc);
            vh.colourTag.setColorFilter(setColourFiler(colourTag));
            // ----
            StringBuilder note;

            Scanner scanner = new Scanner(content);
            note = new StringBuilder(scanner.nextLine());
            while (scanner.hasNextLine()) {
                note.append("\n");
                note.append(scanner.nextLine());
            }
            scanner.close();

            final String prevNote = note.toString();
            // SET VIEWS
            vh.label.setText(label);
            vh.date.setText(date);
            setContentNote(vh, d.getShortNoteText(mContext, prevNote));
            // --------
            vh.cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isEditable(d, vh)) {
                        View dv = View.inflate(mContext, R.layout.preview_note, null);
                        ImageView type = (ImageView) dv.findViewById(R.id.imgChildIconType);
                        ImageButton cancel = (ImageButton) dv.findViewById(R.id.btnChildDone);
                        ImageButton edit = (ImageButton) dv.findViewById(R.id.btnChildEdit);
                        ImageView ColourTag = (ImageView) dv.findViewById(R.id.imgColourTag);
                        TextView Label = (TextView) dv.findViewById(R.id.tvChildLabel);
                        TextView Note = (TextView) dv.findViewById(R.id.tvChildMemo);
                        TextView Date = (TextView) dv.findViewById(R.id.tvChildDate);

                        type.setImageDrawable(setDataIcon(TYPE));
                        if (colourTag.matches("COL_DEFAULT")) ColourTag.setColorFilter(setColourFiler(colourTag));
                        Label.setText(label);
                        Note.setText(prevNote);
                        Date.setText(date);

                        // DIALOG BUILDER
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setView(dv);
                        // DIALOG SHOW
                        final AlertDialog dialog = builder.show();

                        edit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                onEditClicked(d);
                            }
                        });
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }
                }
            }); // Preview dialog
        }
        private void paymentInfoFunctions(final ViewHolder vh, final Data d, CryptoContent cc, final String TYPE) {
            final String date = d.getDate();
            final String colourTag = d.getColourTag(cc);
            final String label = d.getLabel(cc);
            final String content = d.getContent(cc);
            vh.colourTag.setColorFilter(setColourFiler(colourTag));
            // ----
            String name = "";
            String number = "";
            String cardType = "";
            String expiry = "";
            String cvv = "";
            StringBuilder notes = new StringBuilder("");

            Scanner scanner = new Scanner(content);
            try {
                name = scanner.nextLine();
                number = scanner.nextLine();
                cardType = scanner.nextLine();
                expiry = scanner.nextLine();
                cvv = scanner.nextLine();
                notes = new StringBuilder(scanner.nextLine());
                while (scanner.hasNextLine()) {
                    notes.append("\n");
                    notes.append(scanner.nextLine());
                }
            } catch (Exception e) {
//                e.printStackTrace();
            }
            scanner.close();

            StringBuilder tmp = new StringBuilder();
            if (!number.matches("")) { // ***** ASTRIX ALGORITHM
                if (number.length() < 5) {
                    tmp = new StringBuilder(number.substring(0, number.length()));
                } else {
                    int i = 0;
                    while (i < number.length() - 5) {
                        tmp.append("*");
                        i++;
                    }
                    tmp.append(number.substring(number.length() - 4, number.length()));
                }
                setContentPaymentinfo(vh, cardType, d.getShortText(mContext, name), tmp.toString());
            } else {
                setContentPaymentinfo(vh, cardType, d.getShortText(mContext, name), "");
            }

            final String prevName = name;
            final String prevNumber = number;
            final String prevExpiry = expiry;
            final String prevCVV = cvv;
            final String prevCardType = cardType;
            final String prevNotes = notes.toString();
            // SET VIEWS
            vh.label.setText(label);
            vh.date.setText(date);
            // ----
            vh.cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isEditable(d, vh)) {
                        View dv = View.inflate(mContext, R.layout.preview_paymentinfo, null);
                        ImageView type = (ImageView) dv.findViewById(R.id.imgChildIconType);
                        ImageButton cancel = (ImageButton) dv.findViewById(R.id.btnChildDone);
                        ImageButton edit = (ImageButton) dv.findViewById(R.id.btnChildEdit);
                        ImageView ColourTag = (ImageView) dv.findViewById(R.id.imgColourTag);
                        TextView Label = (TextView) dv.findViewById(R.id.tvChildLabel);
                        TextView CardName = (TextView) dv.findViewById(R.id.tvChildCardName);
                        TextView CardNumber = (TextView) dv.findViewById(R.id.tvChildCardNumber);
                        TextView CardExpiry = (TextView) dv.findViewById(R.id.tvChildCardExpiry);
                        TextView CardSecCode = (TextView) dv.findViewById(R.id.tvChildCardSecCode);
                        TextView CardType = (TextView) dv.findViewById(R.id.tvChildCardType);
                        TextView Notes = (TextView) dv.findViewById(R.id.tvChildNotes);
                        TextView Date = (TextView) dv.findViewById(R.id.tvChildDate);
                        ImageView CardIcon = (ImageView) dv.findViewById(R.id.imgChildCardType);

                        type.setImageDrawable(setDataIcon(TYPE));
                        if (colourTag.matches("COL_DEFAULT")) ColourTag.setColorFilter(setColourFiler(colourTag));
                        Label.setText(label);
                        CardName.setText(getResources().getText(R.string.cName) + prevName);
                        CardNumber.setText(getResources().getText(R.string.cNumber) + prevNumber);
                        CardExpiry.setText(getResources().getText(R.string.cExpire) + prevExpiry);
                        CardSecCode.setText(getResources().getText(R.string.cCVV) + prevCVV);
                        CardType.setText(getResources().getText(R.string.cType) + prevCardType);
                        Notes.setText(prevNotes);
                        Date.setText(date);
                        CardIcon.setImageDrawable(setCardImage(prevCardType));

                        // DIALOG BUILDER
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setView(dv);
                        // DIALOG SHOW
                        final AlertDialog dialog = builder.show();

                        edit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                onEditClicked(d);
                            }
                        });
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }
                }
            });
        }
        private void loginInfoFunctions(final ViewHolder vh, final Data d, CryptoContent cc, final String TYPE) {
            final String date = d.getDate();
            final String colourTag = d.getColourTag(cc);
            final String label = d.getLabel(cc);
            final String content = d.getContent(cc);
            vh.colourTag.setColorFilter(setColourFiler(colourTag));
            // ----
            String url = "";
            String email = "";
            String username = "";
            String password = "";
            StringBuilder notes = new StringBuilder("");

            Scanner scanner = new Scanner(content);
            try {
                url = scanner.nextLine();
                email = scanner.nextLine();
                username = scanner.nextLine();
                password = scanner.nextLine();
                notes = new StringBuilder(scanner.nextLine());
                while (scanner.hasNextLine()) {
                    notes.append("\n");
                    notes.append(scanner.nextLine());
                }
            } catch (Exception e) {
//                e.printStackTrace();
            }
            scanner.close();

            final String prevUrl = url;
            final String prevEmail = email;
            final String prevUsername = username;
            final String prevPassword = password;
            final String prevNotes = notes.toString();
            // SET VIEWS
            vh.label.setText(label);
            vh.date.setText(date);
            setContentLogininfo(vh, prevUrl, prevEmail, prevUsername);
            // ----
            vh.cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isEditable(d, vh)) {
                        View dv = View.inflate(mContext, R.layout.preview_logininfo, null);
                        ImageView type = (ImageView) dv.findViewById(R.id.imgChildIconType);
                        ImageButton cancel = (ImageButton) dv.findViewById(R.id.btnChildDone);
                        ImageButton edit = (ImageButton) dv.findViewById(R.id.btnChildEdit);
                        ImageView ColourTag = (ImageView) dv.findViewById(R.id.imgColourTag);
                        TextView Label = (TextView) dv.findViewById(R.id.tvChildLabel);
                        TextView URL = (TextView) dv.findViewById(R.id.tvchildUrl);
                        TextView Username = (TextView) dv.findViewById(R.id.tvChildUsername);
                        TextView Email = (TextView) dv.findViewById(R.id.tvChildEmail);
                        TextView Password = (TextView) dv.findViewById(R.id.tvChildPassword);
                        TextView Notes = (TextView) dv.findViewById(R.id.tvChildNotes);
                        TextView Date = (TextView) dv.findViewById(R.id.tvChildDate);

                        type.setImageDrawable(setDataIcon(TYPE));
                        if (colourTag.matches("COL_DEFAULT")) ColourTag.setColorFilter(setColourFiler(colourTag));
                        Label.setText(label);
                        URL.setText(prevUrl);
                        Username.setText(prevUsername);
                        Email.setText(prevEmail);
                        Password.setText(prevPassword);
                        Notes.setText(prevNotes);
                        Date.setText(date);

                        // DIALOG BUILDER
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setView(dv);
                        // DIALOG SHOW
                        final AlertDialog dialog = builder.show();

                        edit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                onEditClicked(d);
                            }
                        });
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }
                }
            });
        }
        // ------------------------------
        private void setContentNote(ViewHolder vh, String note) {
            resetViews(vh);
            vh.content.setText(note);
        }
        private void setContentPaymentinfo(ViewHolder vh, String type, String name, String number) {
            resetViews(vh);
            String dName = "CARD NAME: ";
            String dNumber = "CARD NUMBER: ";
            String empty = "*** card information does not \n contain a holder text or number ***";
            String s;
            if (isNotNull(name) && isNotNull(number)) {
                s = dName + name + "\n" + dNumber + number;
                vh.content.setText(s);
            } else if (isNotNull(name) && !isNotNull(number)) {
                s = dName + name + "\n" + "\n";
                vh.content.setText(s);
            } else if (!isNotNull(name) && isNotNull(number)) {
                s = dNumber + number + "\n" + "\n";
                vh.content.setText(s);
            } else if (!isNotNull(name) && !isNotNull(number)) {
                s = empty;
                vh.content.setText(s);
                vh.content.setTextColor(getResources().getColor(R.color.red_1D));
                vh.content.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            }
            Drawable img = setCardImage(type);
            System.out.println(type);
            float x = img.getMinimumWidth();
            float y = img.getMinimumHeight();
            float scale = 0.65f;
            int xx = (int) ((int) x - (x * scale));
            int yy = (int) ((int) y - (y * scale));
            img.setBounds(0, 0, xx, yy);
            vh.content.setCompoundDrawables(null, null, img, null);
            s = null;
        }
        private void setContentLogininfo(ViewHolder vh, String url, String email, String username) {
            resetViews(vh);
            String dUrl = "URL: ";
            String dEmail = "EMAIL: ";
            String dUsername = "USERNAME: ";
            String empty = "Login credentials are missing url, email and/or username";
            String s;
            if (isNotNull(url)) {
                if (isNotNull(email) && isNotNull(username)) {
                    s = dUrl + url + "\n" + dEmail + email;
                    vh.content.setText(s);
                } else if (isNotNull(email) && !isNotNull(username)) {
                    s = dUrl + url + "\n" + dEmail + email;
                    vh.content.setText(s);
                } else if (!isNotNull(email) && isNotNull(username)) {
                    s = dUrl + url + "\n" + dUsername + username;
                    vh.content.setText(s);
                }
            } else if (!isNotNull(url)) {
                if (isNotNull(email) && isNotNull(username)) {
                    s = dEmail + email + "\n" + dUsername + username;
                    vh.content.setText(s);
                } else if (isNotNull(email) && !isNotNull(username)) {
                    s = dEmail + email + "\n" + "\n";
                    vh.content.setText(s);
                } else if (!isNotNull(email) && isNotNull(username)) {
                    s = dUsername + username + "\n" + "\n";
                    vh.content.setText(s);
                } else if (!isNotNull(email) && !isNotNull(username)) {
                    s = empty;
                    vh.content.setText(s);
                }
            }
            s = null;
        }
        // ------------------------------
        // Setters
        private void resetViews(ViewHolder vh) {
            vh.content.setCompoundDrawables(null, null, null, null);
            vh.content.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            vh.content.setTextColor(getResources().getColor(R.color.black));
            vh.content.setText("");
        }
        private int setColourFiler(String col) {
            switch (col) {
                case "COL_BLUE": return (getResources().getColor(R.color.coltag_blue));
                case "COL_RED": return (getResources().getColor(R.color.coltag_red));
                case "COL_GREEN": return (getResources().getColor(R.color.coltag_green));
                case "COL_YELLOW": return (getResources().getColor(R.color.coltag_yellow));
                case "COL_PURPLE": return (getResources().getColor(R.color.coltag_purple));
                case "COL_ORANGE": return (getResources().getColor(R.color.coltag_orange));
                default: return (getResources().getColor(R.color.coltag_default));
            }
        }
        private Drawable setCardImage(String cardType) {
            switch (cardType) {
                case ("Visa"): return (getResources().getDrawable(R.drawable.card_visa));
                case ("Master Card"): return (getResources().getDrawable(R.drawable.card_mastercard));
                case ("American Express"): return (getResources().getDrawable(R.drawable.card_americanexpress));
                case ("Discover"): return (getResources().getDrawable(R.drawable.card_discover));
                default: return (getResources().getDrawable(R.drawable.card_default));
            }
        }
        // Dialog Extras
        private Drawable setDataIcon(String s) {
            System.out.println(s);
            switch (s) {
                case "TYPE_NOTE": return (getResources().getDrawable(R.drawable.ic_file));
                case "TYPE_PAYMENTINFO": return (getResources().getDrawable(R.drawable.ic_card));
                case "TYPE_LOGININFO": return (getResources().getDrawable(R.drawable.ic_login));
                default: return (getResources().getDrawable(R.drawable.ic_graphic_none));
            }
        }

        // Primitives
        @Contract("null -> false") private boolean isNotNull(String s) {
            return s != null && !s.isEmpty();
        }
        @SuppressLint("ResourceType") private boolean isEditable(Data d, ViewHolder vh) {
            if (!mIsMultiChoice) {
                return true;
            } else {
                if (!d.isSelected()) {
                    vh.checkbox.setChecked(true);
                    d.setSelected(true);
                    mSelectedDatas.add(d);
                    mCount++;
                } else {
                    vh.checkbox.setChecked(false);
                    d.setSelected(false);
                    mSelectedDatas.remove(d);
                    mCount--;
                }
                getSupportActionBar().setTitle(mCount + " Selected");

                return false;
            }
        }

        // View holder class
        class ViewHolder extends RecyclerView.ViewHolder {
            CardView cv;
            CheckBox checkbox;
            ImageView colourTag;
            TextView date;
            TextView label;
            TextView content;

            ViewHolder(View itemView) {
                super(itemView);
                // GLOBAL
                cv = (CardView) itemView.findViewById(R.id.cardView);
                checkbox = (CheckBox) itemView.findViewById(R.id.cbDataSelect);
                colourTag = (ImageView) itemView.findViewById(R.id.imgColourTag);
                date = (TextView) itemView.findViewById(R.id.tvDate);
                label = (TextView) itemView.findViewById(R.id.tvLabel);
                content = (TextView) itemView.findViewById(R.id.tvContent);
            }
        }
    }
    //
    private class InfoCategory {
        private String title;

        InfoCategory(String title) {
            this.title = title;
        }
    }
    private class Info {
        private CharSequence text;

        Info(CharSequence text) {
            this.text = text;
        }
    }
}