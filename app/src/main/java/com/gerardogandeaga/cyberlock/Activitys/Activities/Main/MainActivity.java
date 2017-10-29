package com.gerardogandeaga.cyberlock.Activitys.Activities.Main;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import java.util.List;
import java.util.Scanner;

import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.APP_LOGGED_IN;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.mCountDownIsFinished;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.mCountDownTimer;

public class MainActivity extends AppCompatActivity {
    private Context mContext = this;
    private CryptoContent mCryptoContent;
    private Menu mMenu;
    private Resources mResources;

    // DATA VARIABLES
    private int mCount;
    private MasterDatabaseAccess mMasterDatabaseAccess;
    private ArrayList<Data> mSelectedDatas;
    private List<Data> mDatas;

    // WIDGETS
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mListView;
    private NavigationView mNavigationView;

    // INITIAL ON CREATE METHODS
    @Override
    public void onCreate(Bundle savedInstanceState) {
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        Globals.COLORSCHEME(this);
        mCryptoContent = new CryptoContent(mContext);
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
        this.mDatas = mMasterDatabaseAccess.getAllData();
        this.mMasterDatabaseAccess.close();
        DataAdapter adapter = new DataAdapter(this, mDatas);
        this.mListView.setAdapter(adapter); // TODO CREATE EMPTY LIST MESSAGE
    }
    private void setupLayout() {
        setContentView(R.layout.activity_main);
        ACTIVITY_INTENT = null;
        mResources = getResources();
        // GET WIDGETS
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.Data);

        this.mNavigationView = (NavigationView) findViewById(R.id.NavigationContent);
        this.mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        this.mListView = (ListView) findViewById(R.id.listView);

        // GET DATA
        this.mMasterDatabaseAccess = MasterDatabaseAccess.getInstance(this);

        // SET WIDGETS
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        this.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });

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

    // PAYMENTINFO CARD GRAPHIC
    private Drawable setCardImage(String cardType) {
        switch (cardType) {
            case ("None"):
                return getResources().getDrawable(R.drawable.card_default);
            case ("Visa"):
                return getResources().getDrawable(R.drawable.card_visa);
            case ("Master Card"):
                return getResources().getDrawable(R.drawable.card_mastercard);
            case ("American Express"):
                return getResources().getDrawable(R.drawable.card_americanexpress);
            case ("Discover"):
                return getResources().getDrawable(R.drawable.card_discover);
            case ("Other"):
                return getResources().getDrawable(R.drawable.card_default);
            default:
                return getResources().getDrawable(R.drawable.card_default);
        }
    }
    // -------------------------

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

        int id = menuItem.getItemId();
        Dialog dialog = new Dialog(this);

        switch (id)
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

        if (mListView.getChoiceMode() != ListView.CHOICE_MODE_MULTIPLE_MODAL) {
            mSelectedDatas = new ArrayList<>();

            mMenu.removeItem(R.id.action_search);
            mMenu.removeItem(R.id.action_deletesweep);
            mMenu.removeItem(R.id.subMenuAdd);

            menuInflater.inflate(R.menu.menu_delete, mMenu);

            MenuItem delete = mMenu.findItem(R.id.action_delete);
            MenuItem done = mMenu.findItem(R.id.action_done);

            delete.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            done.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            getSupportActionBar().setTitle(mCount + " Selected");

            mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
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
            getSupportActionBar().setTitle("");

            mListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
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
        if (mListView.getChoiceMode() != ListView.CHOICE_MODE_MULTIPLE_MODAL)
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
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------

    // INNER ADAPTER CLASS
    private class DataAdapter extends ArrayAdapter<Data> // TODO OPTIMIZE LIST VIEW TO HANDLE MORE INPUTS AND VIEWS!!!
    {
        private DataAdapter(Context context, List<Data> objects) {
            super(context, 0, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_main_list, parent, false);
            }
            final Data data = mDatas.get(position);

            final RelativeLayout Content = (RelativeLayout) convertView.findViewById(R.id.Content);
            final RelativeLayout MemoListLayout = (RelativeLayout) convertView.findViewById(R.id.MemoListLayout);
            final RelativeLayout PaymentInfoListLayout = (RelativeLayout) convertView.findViewById(R.id.PaymentInfoListLayout);
            final RelativeLayout LoginInfoListLayout = (RelativeLayout) convertView.findViewById(R.id.LoginInfoListLayout);
            final ImageView ColourTag = (ImageView) convertView.findViewById(R.id.ColourTag);

            final String TYPE = data.getType(mCryptoContent);
            switch (TYPE) {
                case "TYPE_NOTE":
                    removeView(PaymentInfoListLayout);
                    removeView(LoginInfoListLayout);
                    noteFunctions(convertView, data, Content);
                    break;
                case "TYPE_PAYMENTINFO":
                    removeView(MemoListLayout);
                    removeView(LoginInfoListLayout);
                    paymentInfoFunctions(convertView, data, Content);
                    break;
                case "TYPE_LOGININFO":
                    removeView(MemoListLayout);
                    removeView(PaymentInfoListLayout);
                    loginInfoFunctions(convertView, data, Content);
                    break;
            }

            final String COL_TAG = data.getColourTag(mCryptoContent);
            switch (COL_TAG) {
                case "COL_BLUE": ColourTag.getBackground().setColorFilter(getResources().getColor(R.color.coltag_blue), PorterDuff.Mode.MULTIPLY); break;
                case "COL_RED": ColourTag.getBackground().setColorFilter(getResources().getColor(R.color.coltag_red), PorterDuff.Mode.MULTIPLY); break;
                case "COL_GREEN": ColourTag.getBackground().setColorFilter(getResources().getColor(R.color.coltag_green), PorterDuff.Mode.MULTIPLY); break;
                case "COL_YELLOW": ColourTag.getBackground().setColorFilter(getResources().getColor(R.color.coltag_yellow), PorterDuff.Mode.MULTIPLY); break;
                case "COL_PURPLE": ColourTag.getBackground().setColorFilter(getResources().getColor(R.color.coltag_purple), PorterDuff.Mode.MULTIPLY); break;
                case "COL_ORANGE": ColourTag.getBackground().setColorFilter(getResources().getColor(R.color.coltag_orange), PorterDuff.Mode.MULTIPLY); break;
                default: ColourTag.getBackground().setColorFilter(getResources().getColor(R.color.coltag_default), PorterDuff.Mode.MULTIPLY); break;
            }

            return convertView;
        }

        // FUNCTIONS FOR THE DATA ADAPTER
        private void noteFunctions(View convertView, final Data data, final RelativeLayout view) {
            final TextView tvDate = (TextView) convertView.findViewById(R.id.tvMemoDate);
            final TextView tvLabel = (TextView) convertView.findViewById(R.id.tvMemoLabel);
            final TextView tvMemo = (TextView) convertView.findViewById(R.id.tvMemo);
            // ----
            final String date = data.getDate();
            final String label = data.getLabel(mCryptoContent);
            final String content = data.getContent(mCryptoContent);
            final String TYPE = data.getType(mCryptoContent);
            // ----
            StringBuilder note;

            Scanner scanner = new Scanner(content);
            note = new StringBuilder(scanner.nextLine());
            while (scanner.hasNextLine()) {
                note.append("\n");
                note.append(scanner.nextLine());
            }
            scanner.close();

            // SET VIEWS
            if (label != null) { tvLabel.setText(label); } else { removeView(tvLabel); }
            tvMemo.setText(data.getShortText(mContext, note.toString()));
            tvDate.setText(date);
            // --------
            final String prevMemo = note.toString();
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isEditable(data, view)) { // MULTI-SELECT MODE IS NOT ACTIVE
                        View dialogView = View.inflate(mContext, R.layout.preview_note, null);
                        ImageView childType = (ImageView) dialogView.findViewById(R.id.imgChildIconType);
                        ImageButton childDone = (ImageButton) dialogView.findViewById(R.id.btnChildDone);
                        ImageButton childEdit = (ImageButton) dialogView.findViewById(R.id.btnChildEdit);
                        TextView childLabel = (TextView) dialogView.findViewById(R.id.tvChildLabel);
                        TextView childMemo = (TextView) dialogView.findViewById(R.id.tvChildMemo);
                        TextView childDate = (TextView) dialogView.findViewById(R.id.tvChildDate);

                        childType.setImageDrawable(getIconType(TYPE));
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
                                onEditClicked(data);
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
        private void paymentInfoFunctions(View convertView, final Data data, final RelativeLayout view) {
            final TextView tvDate = (TextView) convertView.findViewById(R.id.tvPaymentInfoDate);
            final TextView tvLabel = (TextView) convertView.findViewById(R.id.tvPaymentInfoLabel);
            final TextView tvCardName = (TextView) convertView.findViewById(R.id.tvCardName);
            final TextView tvCardNumber = (TextView) convertView.findViewById(R.id.tvCardNumber);
            final ImageView imgCard = (ImageView) convertView.findViewById(R.id.imgCard);
            // ----
            final String date = data.getDate();
            final String label = data.getLabel(mCryptoContent);
            final String content = data.getContent(mCryptoContent);
            final String TYPE = data.getType(mCryptoContent);
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
                e.printStackTrace();
            }
            scanner.close();

            // SET VIEWS
            if (isNull(label)) { tvLabel.setText(label); } else { removeView(tvLabel); }
            if (isNull(name)) { tvCardName.setText(name); } else { removeView(tvCardName); }
            tvDate.setText(date);
            imgCard.setImageDrawable(setCardImage(cardType)); // SET CARD TYPE ICON
            // ----
            StringBuilder tempNumber = new StringBuilder();
            if (!number.matches("")) { // ***** ASTRIX ALGORITHM
                if (number.length() < 5) {
                    tempNumber = new StringBuilder(number.substring(0, number.length()));
                } else {
                    int i = 0;
                    while (i < number.length() - 5) {
                        tempNumber.append("*");
                        i++;
                    }
                    tempNumber.append(number.substring(number.length() - 4, number.length()));
                }
                tvCardNumber.setText(tempNumber.toString());
            } else {
                removeView(tvCardNumber);
            }
            // ----
            final String prevName = name;
            final String prevNumber = number;
            final String prevExpiry = expiry;
            final String prevCVV = cvv;
            final String prevCardType = cardType;
            final String prevNotes = notes.toString();
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isEditable(data, view)) {
                        View dialogView = View.inflate(mContext, R.layout.preview_paymentinfo, null);
                        ImageView childType = (ImageView) dialogView.findViewById(R.id.imgChildIconType);
                        ImageButton childDone = (ImageButton) dialogView.findViewById(R.id.btnChildDone);
                        ImageButton childEdit = (ImageButton) dialogView.findViewById(R.id.btnChildEdit);
                        TextView childLabel = (TextView) dialogView.findViewById(R.id.tvChildLabel);
                        TextView childCardName = (TextView) dialogView.findViewById(R.id.tvChildCardName);
                        TextView childCardNumber= (TextView) dialogView.findViewById(R.id.tvChildCardNumber);
                        TextView childCardExpiry = (TextView) dialogView.findViewById(R.id.tvChildCardExpiry);
                        TextView childCardSecCode = (TextView) dialogView.findViewById(R.id.tvChildCardSecCode);
                        TextView childCardType = (TextView) dialogView.findViewById(R.id.tvChildCardType);
                        TextView childNotes = (TextView) dialogView.findViewById(R.id.tvChildNotes);
                        TextView childDate = (TextView) dialogView.findViewById(R.id.tvChildDate);
                        ImageView childCardIcon = (ImageView) dialogView.findViewById(R.id.imgChildCardType);

                        childType.setImageDrawable(getIconType(TYPE));
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
                                onEditClicked(data);
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
        private void loginInfoFunctions(View convertView, final Data data, final RelativeLayout view) {
            final TextView tvDate = (TextView) convertView.findViewById(R.id.tvLoginInfoDate);
            final TextView tvLabel = (TextView) convertView.findViewById(R.id.tvLoginInfoLabel);
            final TextView tvUrl = (TextView) convertView.findViewById(R.id.tvURL);
            final TextView tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
            final TextView tvEmail = (TextView) convertView.findViewById(R.id.tvEmail);
            final TextView tvPassword = (TextView) convertView.findViewById(R.id.tvPassword);
            // ----
            final String date = data.getDate();
            final String label = data.getLabel(mCryptoContent);
            final String content = data.getContent(mCryptoContent);
            final String TYPE = data.getType(mCryptoContent);
            // ----
            String url = "";
            String username = "";
            String email = "";
            String password = "";
            StringBuilder notes = new StringBuilder("");

            Scanner scanner = new Scanner(content);
            try {
                url = scanner.nextLine();
                username = scanner.nextLine();
                email = scanner.nextLine();
                password = scanner.nextLine();
                notes = new StringBuilder(scanner.nextLine());
                while (scanner.hasNextLine()) {
                    notes.append("\n");
                    notes.append(scanner.nextLine());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            scanner.close();

            // SET VIEWS
            if (isNull(label)) { tvLabel.setText(label); } else { removeView(tvLabel); }
            if (isNull(url)) { tvUrl.setText("URL: " + url); } else { removeView(tvUrl); }
            if (isNull(username)) { tvUsername.setText("Username: " + username); } else { removeView(tvUsername); }
            if (isNull(email)) { tvEmail.setText("Email: " + email); } else { removeView(tvEmail); }
            if (isNull(password)) { tvPassword.setText("Password: " + password); } else { removeView(tvPassword); }
            tvDate.setText(date);
            // ----
            final String finalUrl = url;
            final String finalUsername = username;
            final String finalEmail = email;
            final String finalPassword = password;
            final String finalNotes = notes.toString();
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isEditable(data, view)) {
                        View dialogView = View.inflate(mContext, R.layout.preview_logininfo, null);
                        ImageView childType = (ImageView) dialogView.findViewById(R.id.imgChildIconType);
                        ImageButton childDone = (ImageButton) dialogView.findViewById(R.id.btnChildDone);
                        ImageButton childEdit = (ImageButton) dialogView.findViewById(R.id.btnChildEdit);
                        TextView childLabel = (TextView) dialogView.findViewById(R.id.tvChildLabel);
                        TextView childUrl = (TextView) dialogView.findViewById(R.id.tvchildUrl);
                        TextView childUsername = (TextView) dialogView.findViewById(R.id.tvChildUsername);
                        TextView childEmail = (TextView) dialogView.findViewById(R.id.tvChildEmail);
                        TextView childPassword = (TextView) dialogView.findViewById(R.id.tvChildPassword);
                        TextView childNotes = (TextView) dialogView.findViewById(R.id.tvChildNotes);
                        TextView childDate = (TextView) dialogView.findViewById(R.id.tvChildDate);

                        childType.setImageDrawable(getIconType(TYPE));
                        childLabel.setText(label);
                        childUrl.setText("URL: " + finalUrl);
                        childUsername.setText("Username: " + finalUsername);
                        childEmail.setText("Email: " + finalEmail);
                        childPassword.setText("Password: " + finalPassword);
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
                                onEditClicked(data);
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
        // SIMPLE OVERLOADED "REMOVE" FUNCTIONS
        private void removeView(TextView view) {
            view.setVisibility(View.GONE);
        }
        private void removeView(RelativeLayout view) {
            view.setVisibility(View.GONE);
        }
        // ----------------
        // DIALOG EXTRAS
        private Drawable getIconType(String TYPE) {
            switch (TYPE) {
                case "TYPE_NOTE":
                    return mResources.getDrawable(R.drawable.ic_graphic_note);
                case "TYPE_PAYMENTINFO":
                    return mResources.getDrawable(R.drawable.ic_graphic_paymentinfo);
                case "TYPE_LOGININFO":
                    return mResources.getDrawable(R.drawable.ic_graphic_logininfo);
                default:
                    Toast.makeText(mContext, "Could find 'type' of DATA", Toast.LENGTH_SHORT).show(); break;
            }
            return mResources.getDrawable(R.drawable.ic_graphic_none);
        }
        // PRIMITIVES
        @Contract("null -> false")
        private boolean isNull(String string) {
            if (string != null) { if (string.isEmpty()) { return false; } }
            return string != null;
        }
        private boolean isEditable(Data data, RelativeLayout Content) {
            if (mListView.getChoiceMode() != ListView.CHOICE_MODE_MULTIPLE_MODAL) {
                return true;
            } else {
                if (!data.isSelected()) {
                    Content.setBackground(mResources.getDrawable(R.drawable.clickable_listitem_selected));
                    data.setSelected(true);
                    mSelectedDatas.add(data);
                    mCount++;
                } else {
                    Content.setBackground(mResources.getDrawable(R.drawable.clickable_listitem));
                    data.setSelected(false);
                    mSelectedDatas.remove(data);
                    mCount--;
                }
                getSupportActionBar().setTitle(mCount + " Selected");

                return false;
            }
        }
    }
}