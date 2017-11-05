package com.gerardogandeaga.cyberlock.Activitys.Activities.Main;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LoginActivity;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Menus.Settings;
import com.gerardogandeaga.cyberlock.Crypto.CryptoContent;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.ContentDatabase.Data;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.ContentDatabase.MasterDatabaseAccess;
import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.Supports.Globals;
import com.gerardogandeaga.cyberlock.Supports.LogoutProtocol;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.APP_LOGGED_IN;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.mCountDownIsFinished;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.mCountDownTimer;

public class MainActivity extends AppCompatActivity {
    private Context mContext = this;
    private Menu mMenu;
    private Resources mResources;

    // DATA VARIABLES
    private boolean mIsMultiChoice = false;
    private int mCount;
    private MasterDatabaseAccess mMasterDatabaseAccess;
    private ArrayList<Data> mSelectedDatas;

    // WIDGETS
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;

    // INITIAL ON CREATE METHODS
    @Override
    public void onCreate(Bundle savedInstanceState) {
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        Globals.COLORSCHEME(this);
        super.onCreate(savedInstanceState);
        setupLayout();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMenu = menu;

        return true;
    }
    @Override
    public void onResume() {
        super.onResume();
        this.mMasterDatabaseAccess.open();
        List<Data> datas = mMasterDatabaseAccess.getAllData();
        this.mMasterDatabaseAccess.close();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RVDataAdapter adapter = new RVDataAdapter(mContext, datas);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    }
    private void setupLayout() {
        setContentView(R.layout.activity_main);
        ACTIVITY_INTENT = null;
        mResources = getResources();
        // GET WIDGETS
        DrawerLayout drawerLayout = findViewById(R.id.Data);

        this.mNavigationView = findViewById(R.id.NavigationContent);
        this.mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        this.mMasterDatabaseAccess = MasterDatabaseAccess.getInstance(this);

        // SET WIDGETS
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        calculateDrawerSize();
        drawerLayout.addDrawerListener(mDrawerToggle);
        this.mDrawerToggle.syncState();

        this.mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                onNavigationItemClicked(item);

                return false;
            }
        });
    }
    private void calculateDrawerSize() {
        Resources resources = getResources();
        DisplayMetrics metrics = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;

        float screenWidth = width / resources.getDisplayMetrics().density;
        float navWidth = screenWidth - 56;

        navWidth = Math.min(navWidth, 320);

        int newWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, navWidth, resources.getDisplayMetrics());

        DrawerLayout.LayoutParams params = (android.support.v4.widget.DrawerLayout.LayoutParams) mNavigationView.getLayoutParams();
        params.width = (newWidth);
        mNavigationView.setLayoutParams(params);
    }

    // ON ACTION CLICKS
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) return true;
        switch (item.getItemId()) {
            // OPTIONS
            case R.id.action_deletesweep:
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
    private void onNavigationItemClicked(MenuItem menuItem) {
        Dialog dialog = new Dialog(this);

        switch (menuItem.getItemId())
        {
            case R.id.action_note:
                onAddClicked(1);
                overridePendingTransition(R.anim.anim_slide_inright, R.anim.anim_slide_outleft);
                break;
            case R.id.action_paymentinfo:
                onAddClicked(2);
                overridePendingTransition(R.anim.anim_slide_inright, R.anim.anim_slide_outleft);
                break;
            case R.id.action_logininfo:
                onAddClicked(3);
                overridePendingTransition(R.anim.anim_slide_inright, R.anim.anim_slide_outleft);
                break;

            case R.id.action_playground:
                ACTIVITY_INTENT = new Intent(this, MainPlaygroundActivity.class);
                this.finish();
                this.startActivity(ACTIVITY_INTENT);
                overridePendingTransition(R.anim.anim_slide_inright, R.anim.anim_slide_outleft);
                break;
            case R.id.action_settings:
                ACTIVITY_INTENT = new Intent(this, Settings.class);
                this.finish();
                this.startActivity(ACTIVITY_INTENT);
                overridePendingTransition(R.anim.anim_slide_inright, R.anim.anim_slide_outleft);
                break;
            case R.id.action_about:
                dialog.setContentView(R.layout.activity_about);
                dialog.setTitle("About Cyber Lock");
                dialog.show();
                break;

            case R.id.action_webpage:
                Uri uri = Uri.parse(getString(R.string.SiteURL));
                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                this.finish();
                this.startActivity(i);
                break;
        }
    }
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
    // MULTI SELECT
    public void onMultiSelectClicked() {
        MenuInflater menuInflater = new MenuInflater(mContext);

        if (!mIsMultiChoice) {
            mSelectedDatas = new ArrayList<>();
            onResume();

            mMenu.removeItem(R.id.action_search);
            mMenu.removeItem(R.id.action_deletesweep);
            mMenu.removeItem(R.id.subMenuAdd);

            menuInflater.inflate(R.menu.menu_delete, mMenu);

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

            mMenu.removeItem(R.id.action_delete);
            mMenu.removeItem(R.id.action_done);

            menuInflater.inflate(R.menu.menu_main, mMenu);

            MenuItem search = mMenu.findItem(R.id.action_search);
            MenuItem multiSelect = mMenu.findItem(R.id.action_deletesweep);
            MenuItem add = mMenu.findItem(R.id.subMenuAdd);

            search.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            multiSelect.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            add.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            getSupportActionBar().setTitle(null);

            mIsMultiChoice = false;
        }
    }
    public void onDeleteClicked() {
        if (!mSelectedDatas.isEmpty()) {
            this.mMasterDatabaseAccess.open();
            for (Data id : mSelectedDatas) {
                this.mMasterDatabaseAccess.delete(id);
            }
            this.mMasterDatabaseAccess.close();
            onMultiSelectClicked();
        }
    }
    // ----------------

    // THIS IS THE START OF THE SCRIPT FOR *** THE "TO LOGIN FUNCTION" THIS DETECTS THE ON PRESSED, START, TABS AND HOME BUTTONS IN ORDER TO INITIALIZE SECURITY "FAIL-SAFE"
    @Override
    public void onStart() {
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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!mIsMultiChoice)
        if (ACTIVITY_INTENT == null) // NO PENDING ACTIVITIES ???(MAIN)--->(EDIT)???
        {
            new LogoutProtocol().logoutImmediate(this);
        }
    }
    @Override
    public void onPause() {
        super.onPause();

        if (!this.isFinishing()) // HOME AND TABS AND SCREEN OFF
        {
            if (ACTIVITY_INTENT == null) // NO PENDING ACTIVITIES ???(MAIN)--->(EDIT)???
            {
                new LogoutProtocol().logoutExecuteAutosaveOff(mContext);
            }
        }
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        onResume();
//    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------

    // INNER ADAPTER CLASS
    private class RVDataAdapter extends android.support.v7.widget.RecyclerView.Adapter<RVDataAdapter.ViewHolder> {
        private Context mContext;
        private List<Data> mDatas = Collections.emptyList();

        RVDataAdapter(Context context, List<Data> datas) {
            mContext = context;
            mDatas = datas;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_list_item, parent, false);
            return new ViewHolder(v);
        }
        @Override
        public void onBindViewHolder(ViewHolder vh, int position) {
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
        @Override
        public int getItemCount() {
            return mDatas.size();
        }
        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        // FUNCTIONS FOR THE DATA ADAPTER
        private void noteFunctions(final ViewHolder vh, final Data d, CryptoContent cc, final String TYPE) {
            setColourTag(vh, d.getColourTag(cc));
            final String date = d.getDate();
            final String label = d.getLabel(cc);
            final String content = d.getContent(cc);
            // ----
            StringBuilder note;

            Scanner scanner = new Scanner(content);
            note = new StringBuilder(scanner.nextLine());
            while (scanner.hasNextLine()) {
                note.append("\n");
                note.append(scanner.nextLine());
            }
            scanner.close();

            final String prevMemo = note.toString();
            // SET VIEWS
            vh.label.setText(label);
            vh.date.setText(date);
            setContentNote(vh, d.getShortNoteText(mContext, prevMemo));
            // --------
            vh.cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isEditable(d, vh)) {
                        View dialogView = View.inflate(mContext, R.layout.preview_note, null);
                        ImageView childType = dialogView.findViewById(R.id.imgChildIconType);
                        ImageButton childDone = dialogView.findViewById(R.id.btnChildDone);
                        ImageButton childEdit = dialogView.findViewById(R.id.btnChildEdit);
                        TextView childLabel = dialogView.findViewById(R.id.tvChildLabel);
                        TextView childMemo = dialogView.findViewById(R.id.tvChildMemo);
                        TextView childDate = dialogView.findViewById(R.id.tvChildDate);

                        setDataIcon(childType, TYPE);
                        childLabel.setText(label);
                        childMemo.setText(prevMemo);
                        childDate.setText(date);

                        // DIALOG BUILDER
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setView(dialogView);
                        // DIALOG SHOW
                        final AlertDialog dialog = builder.show();

                        childEdit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                onEditClicked(d);
                            }
                        });
                        childDone.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }
                }
            }); // PREVIEW DIALOG
        }
        private void paymentInfoFunctions(final ViewHolder vh, final Data d, CryptoContent cc, final String TYPE) {
            setColourTag(vh, d.getColourTag(cc));
            final String date = d.getDate();
            final String label = d.getLabel(cc);
            final String content = d.getContent(cc);
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
                        View dialogView = View.inflate(mContext, R.layout.preview_paymentinfo, null);
                        ImageView childType = dialogView.findViewById(R.id.imgChildIconType);
                        ImageButton childDone = dialogView.findViewById(R.id.btnChildDone);
                        ImageButton childEdit = dialogView.findViewById(R.id.btnChildEdit);
                        TextView childLabel = dialogView.findViewById(R.id.tvChildLabel);
                        TextView childCardName = dialogView.findViewById(R.id.tvChildCardName);
                        TextView childCardNumber = dialogView.findViewById(R.id.tvChildCardNumber);
                        TextView childCardExpiry = dialogView.findViewById(R.id.tvChildCardExpiry);
                        TextView childCardSecCode = dialogView.findViewById(R.id.tvChildCardSecCode);
                        TextView childCardType = dialogView.findViewById(R.id.tvChildCardType);
                        TextView childNotes = dialogView.findViewById(R.id.tvChildNotes);
                        TextView childDate = dialogView.findViewById(R.id.tvChildDate);
                        ImageView childCardIcon = dialogView.findViewById(R.id.imgChildCardType);

                        setDataIcon(childType, TYPE);
                        childLabel.setText(label);
                        childCardName.setText("Holder Name: " + prevName);
                        childCardNumber.setText("Number: " + prevNumber);
                        childCardExpiry.setText("Expiry Date: " + prevExpiry);
                        childCardSecCode.setText("CVV: " + prevCVV);
                        childCardType.setText("Card Type: " + prevCardType);
                        childNotes.setText(prevNotes);
                        childDate.setText(date);
                        childCardIcon.setImageDrawable(setCardImage(prevCardType));

                        // DIALOG BUILDER
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setView(dialogView);
                        // DIALOG SHOW
                        final AlertDialog dialog = builder.show();

                        childEdit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                onEditClicked(d);
                            }
                        });
                        childDone.setOnClickListener(new View.OnClickListener() {
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
            setColourTag(vh, d.getColourTag(cc));
            final String date = d.getDate();
            final String label = d.getLabel(cc);
            final String content = d.getContent(cc);
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

            final String finalUrl = url;
            final String finalEmail = email;
            final String finalUsername = username;
            final String finalPassword = password;
            final String finalNotes = notes.toString();
            // SET VIEWS
            vh.label.setText(label);
            vh.date.setText(date);
            setContentLogininfo(vh, finalUrl, finalEmail, finalUsername);
            // ----
            vh.cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isEditable(d, vh)) {
                        View dialogView = View.inflate(mContext, R.layout.preview_logininfo, null);
                        ImageView childType = dialogView.findViewById(R.id.imgChildIconType);
                        ImageButton childDone = dialogView.findViewById(R.id.btnChildDone);
                        ImageButton childEdit = dialogView.findViewById(R.id.btnChildEdit);
                        TextView childLabel = dialogView.findViewById(R.id.tvChildLabel);
                        TextView childUrl = dialogView.findViewById(R.id.tvchildUrl);
                        TextView childUsername = dialogView.findViewById(R.id.tvChildUsername);
                        TextView childEmail = dialogView.findViewById(R.id.tvChildEmail);
                        TextView childPassword = dialogView.findViewById(R.id.tvChildPassword);
                        TextView childNotes = dialogView.findViewById(R.id.tvChildNotes);
                        TextView childDate = dialogView.findViewById(R.id.tvChildDate);

                        setDataIcon(childType, TYPE);
                        childLabel.setText(label);
                        childUrl.setText(finalUrl);
                        childUsername.setText(finalUsername);
                        childEmail.setText(finalEmail);
                        childPassword.setText(finalPassword);
                        childNotes.setText(finalNotes);
                        childDate.setText(date);

                        // DIALOG BUILDER
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setView(dialogView);
                        // DIALOG SHOW
                        final AlertDialog dialog = builder.show();

                        childEdit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                onEditClicked(d);
                            }
                        });
                        childDone.setOnClickListener(new View.OnClickListener() {
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
            String empty = "*** card information does not \n contain a holder name or number ***";
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
                vh.content.setTextColor(mResources.getColor(R.color.red_1D));
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
        // SETTERS
        private void resetViews(ViewHolder vh) {
            vh.content.setCompoundDrawables(null, null, null, null);
            vh.content.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            vh.content.setTextColor(mResources.getColor(R.color.black));
            vh.content.setText("");
        }
        private void setColourTag(ViewHolder vh, String col) {
            final PorterDuff.Mode m = PorterDuff.Mode.MULTIPLY;
            switch (col) {
                case "COL_BLUE": vh.colourTag.getBackground().setColorFilter(getResources().getColor(R.color.coltag_blue), m); break;
                case "COL_RED": vh.colourTag.getBackground().setColorFilter(getResources().getColor(R.color.coltag_red), m); break;
                case "COL_GREEN": vh.colourTag.getBackground().setColorFilter(getResources().getColor(R.color.coltag_green), m); break;
                case "COL_YELLOW": vh.colourTag.getBackground().setColorFilter(getResources().getColor(R.color.coltag_yellow), m); break;
                case "COL_PURPLE": vh.colourTag.getBackground().setColorFilter(getResources().getColor(R.color.coltag_purple), m); break;
                case "COL_ORANGE": vh.colourTag.getBackground().setColorFilter(getResources().getColor(R.color.coltag_orange), m); break;
                default: vh.colourTag.getBackground().setColorFilter(getResources().getColor(R.color.coltag_default), m); break;
            }
        }
        private Drawable setCardImage(String cardType) {
            switch (cardType) {
                case ("Visa"): return getResources().getDrawable(R.drawable.card_visa);
                case ("Master Card"): return getResources().getDrawable(R.drawable.card_mastercard);
                case ("American Express"): return getResources().getDrawable(R.drawable.card_americanexpress);
                case ("Discover"): return getResources().getDrawable(R.drawable.card_discover);
                default: return getResources().getDrawable(R.drawable.card_default);
            }
        }
        // DIALOG EXTRAS
        private void setDataIcon(ImageView img, String s) {
            Drawable d;
            switch (s) {
                case "TYPE_NOTE": d = mResources.getDrawable(R.drawable.ic_note); break;
                case "TYPE_PAYMENTINFO": d = mResources.getDrawable(R.drawable.ic_card); break;
                case "TYPE_LOGININFO": d = mResources.getDrawable(R.drawable.ic_login); break;
                default: d = mResources.getDrawable(R.drawable.ic_graphic_none); break;
            }
            img.setImageDrawable(d);
            img.setColorFilter(mResources.getColor(R.color.white));
        }
        // PRIMITIVES
        @Contract("null -> false")
        private boolean isNotNull(String s) {
            return s != null && !s.isEmpty();
        }
        @SuppressLint("ResourceType")
        private boolean isEditable(Data d, ViewHolder vh) {
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

        // VIEW HOLDER CLASS
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
                cv = itemView.findViewById(R.id.cardView);
                checkbox = itemView.findViewById(R.id.cbDataSelect);
                colourTag = itemView.findViewById(R.id.imgColourTag);
                date = itemView.findViewById(R.id.tvDate);
                label = itemView.findViewById(R.id.tvLabel);
                content = itemView.findViewById(R.id.tvContent);
            }
        }
    }
}