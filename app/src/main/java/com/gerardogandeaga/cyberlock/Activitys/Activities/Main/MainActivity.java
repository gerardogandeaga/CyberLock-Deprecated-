package com.gerardogandeaga.cyberlock.Activitys.Activities.Main;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gerardogandeaga.cyberlock.Activitys.Activities.Edits.LoginInfoEditActivity;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Edits.MemoEditActivity;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Edits.PaymentInfoEditActivity;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LoginActivity;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Menus.Contribute;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Menus.Settings;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Menus.Settings_ScrambleKey;
import com.gerardogandeaga.cyberlock.Crypto.CryptContent;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.ContentDatabase.Data;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.ContentDatabase.MasterDatabaseAccess;
import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.Supports.Globals;
import com.gerardogandeaga.cyberlock.Supports.LogoutProtocol;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.gerardogandeaga.cyberlock.Supports.Globals.MASTER_KEY;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.APP_LOGGED_IN;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.mCountDownIsFinished;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.mCountDownTimer;

public class MainActivity extends AppCompatActivity
{
    private Context mContext = this;
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
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
    private void buildDialog() {
        // LINEAR LAYOUT BUILDING
        LinearLayout Content = new LinearLayout(mContext);
        Content.setOrientation(LinearLayout.VERTICAL);

        TextView tvTitle = new TextView(mContext);
        tvTitle.setText("Label:");

        TextView tvReport = new TextView(mContext);
        tvReport.setText("Content:");

        EditText etTitle = new EditText(mContext);
        etTitle.setHint("Title");

        EditText etReport = new EditText(mContext);
        etReport.setHint("Details");

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(50, 20, 50, 0);
//        Content.addView(tvTitle, layoutParams);
        Content.addView(etTitle, layoutParams);
//        Content.addView(tvReport, layoutParams);
        Content.addView(etReport, layoutParams);

        Content.setLayoutParams(layoutParams);

        // DIALOG BUILDER
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setView(Content);
        dialog.setTitle("Bug Report");
        dialog.setCancelable(false);

        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.setPositiveButton("SEND", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                new Settings_ScrambleKey(mContext).execute();
            }
        });
        dialog.show();
    }
    // -------------------------

    // FUNCTIONS FOR THE DATA ADAPTER
    private void memoFunctions(final Data data, CryptContent CRYPTCONTENT, View convertView, LinearLayout.LayoutParams params, RelativeLayout layoutContent) {
        data.setFullDisplayed(false);

        final String date = data.getDate();
        final String label = CRYPTCONTENT.DECRYPT_CONTENT(data.getLabel(), MASTER_KEY);
        final String content = CRYPTCONTENT.DECRYPT_CONTENT(data.getContent(), MASTER_KEY);

        String memo = "";

        if (content != null) {
            Scanner scanner = new Scanner(content);

            try {
                memo = scanner.nextLine();
                while (scanner.hasNextLine()) {
                    memo += "\n";
                    memo += scanner.nextLine();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            scanner.close();
        }

        final TextView tvDate = (TextView) convertView.findViewById(R.id.tvMemoDate);
        final TextView tvLabel = (TextView) convertView.findViewById(R.id.tvMemoLabel);
        final TextView tvMemo = (TextView) convertView.findViewById(R.id.tvMemo);

        if (label != null) {
            tvLabel.setText(label);
        } else {
            tvLabel.setLayoutParams(params);
        }
        if (memo != null) {
            tvMemo.setText(data.getShortText(mContext, memo));
        } else {
            tvMemo.setLayoutParams(params);
        }
        tvDate.setText(date);

        final String finalMemo = memo;
        layoutContent.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v) {
                if (data.isFullDisplayed()) {
                    if (finalMemo != null)
                        tvMemo.setText(data.getShortText(mContext, finalMemo));
                    data.setFullDisplayed(false);
                } else {
                    if (finalMemo != null) tvMemo.setText(finalMemo);
                    data.setFullDisplayed(true);
                }

                return false;
            }
        });

    }
    private void paymentInfoFunctions(Data data, CryptContent CRYPTCONTENT, View convertView, LinearLayout.LayoutParams params) {
        data.setFullDisplayed(false);

        final String date = data.getDate();
        final String label = CRYPTCONTENT.DECRYPT_CONTENT(data.getLabel(), MASTER_KEY);
        final String content = CRYPTCONTENT.DECRYPT_CONTENT(data.getContent(), MASTER_KEY);

        String cardName = "";
        String cardNumber = "";
        String cardType = "";

        Scanner scanner = new Scanner(content);

        try {
            cardName = scanner.nextLine();
            cardNumber = scanner.nextLine();
            cardType = scanner.nextLine();
        } catch (Exception e) {
            e.printStackTrace();
        }

        scanner.close();

        String tempNumber = "";

        final TextView tvDate = (TextView) convertView.findViewById(R.id.tvPaymentInfoDate);
        final TextView tvLabel = (TextView) convertView.findViewById(R.id.tvPaymentInfoLabel);
        final TextView tvCardName = (TextView) convertView.findViewById(R.id.tvCardName);
        final TextView tvCardNumber = (TextView) convertView.findViewById(R.id.tvCardNumber);
        final ImageView imgCard = (ImageView) convertView.findViewById(R.id.imgCard);

        if (label != null) {
            tvLabel.setText(label);
        } else {
            tvLabel.setLayoutParams(params);
        }
        if (!cardName.matches("")) {
            tvCardName.setText(cardName);
        } else {
            tvCardName.setLayoutParams(params);
        }
        if (!cardNumber.matches("")) { // ***** ASTRIX ALGORITHM
            if (cardNumber.length() < 5) {
                tempNumber = cardNumber.substring(0, cardNumber.length());
            } else {
                int i = 0;
                while (i < cardNumber.length() - 5) {
                    tempNumber = tempNumber + "*";
                    i++;
                }
                tempNumber = tempNumber + cardNumber.substring(cardNumber.length() - 4, cardNumber.length());
            }
            tvCardNumber.setText(tempNumber);
        } else {
            tvCardNumber.setLayoutParams(params);
        }
        tvDate.setText(date);

        switch (cardType) {
            case ("None"):
                imgCard.setImageResource(R.drawable.card_default);
                break;
            case ("Visa"):
                imgCard.setImageResource(R.drawable.card_visa);
                break;
            case ("Master Card"):
                imgCard.setImageResource(R.drawable.card_mastercard);
                break;
            case ("American Express"):
                imgCard.setImageResource(R.drawable.card_americanexpress);
                break;
            case ("Discover"):
                imgCard.setImageResource(R.drawable.card_discover);
                break;
            case ("Other"):
                imgCard.setImageResource(R.drawable.card_default);
                break;
            default:
                imgCard.setImageResource(R.drawable.card_americanexpress);
                break;
        }
    }
    private void loginInfoFunctions(final Data data, CryptContent CRYPTCONTENT, View convertView, RelativeLayout layoutContent) {
        data.setFullDisplayed(false);

        final String date = data.getDate();
        final String label = CRYPTCONTENT.DECRYPT_CONTENT(data.getLabel(), MASTER_KEY);
        final String content = CRYPTCONTENT.DECRYPT_CONTENT(data.getContent(), MASTER_KEY);

        String url = "";
        String username = "";
        String email = "";
        String password = "";

        Scanner scanner = new Scanner(content);

        try {
            url = scanner.nextLine();
            username = scanner.nextLine();
            email = scanner.nextLine();
            password = scanner.nextLine();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final TextView tvDate = (TextView) convertView.findViewById(R.id.tvLoginInfoDate);
        final TextView tvLabel = (TextView) convertView.findViewById(R.id.tvLoginInfoLabel);
        final TextView tvUrl = (TextView) convertView.findViewById(R.id.tvURL);
        final TextView tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
        final TextView tvEmail = (TextView) convertView.findViewById(R.id.tvEmail);
        final TextView tvPassword = (TextView) convertView.findViewById(R.id.tvPassword);

        final LinearLayout.LayoutParams hideParams = new LinearLayout.LayoutParams(0, 0);
        final LinearLayout.LayoutParams displayParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (!label.matches("")) {
            tvLabel.setText(label);
        } else {
            tvLabel.setLayoutParams(hideParams);
        }
        if (!url.matches("")) {
            tvUrl.setText("Url: " + url);
        } else {
            tvUrl.setLayoutParams(hideParams);
        }
        if (!username.matches("")) {
            tvUsername.setText("Username: " + username);
            tvUsername.setLayoutParams(hideParams);
        } else {
            tvUsername.setLayoutParams(hideParams);
        }
        if (!email.matches("")) {
            tvEmail.setText("Email: " + email);
            tvEmail.setLayoutParams(hideParams);
        } else {
            tvEmail.setLayoutParams(hideParams);
        }
        if (!password.matches("")) {
            tvPassword.setText("Password: " + password);
            tvPassword.setLayoutParams(hideParams);
        } else {
            tvPassword.setLayoutParams(hideParams);
        }
        tvDate.setText(date);

        layoutContent.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v) {
                if (data.isFullDisplayed()) {
                    tvUsername.setLayoutParams(hideParams);
                    tvEmail.setLayoutParams(hideParams);
                    tvPassword.setLayoutParams(hideParams);
                    data.setFullDisplayed(false);
                } else {
                    tvUsername.setLayoutParams(displayParams);
                    tvEmail.setLayoutParams(displayParams);
                    tvPassword.setLayoutParams(displayParams);
                    data.setFullDisplayed(true);
                }

                return false;
            }
        });
    }
    // ------------------------------

    // ON ACTION CLICKS
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Dialog dialog = new Dialog(this);

        if (mDrawerToggle.onOptionsItemSelected(item)) return true;

        switch (item.getItemId()) {
            // OPTIONS
            case R.id.action_deletesweep:
                onMultiSelectClicked();
            case R.id.action_delete:
                System.out.println("Delete Called");
                onDeleteClicked();
                return true;
            case R.id.action_done:
                onMultiSelectClicked();
                return true;


            // FUNTIONS
            case R.id.action_memo:
                onAddClicked("TYPE_MEMO");
                return true;
            case R.id.action_paymentinfo:
                onAddClicked("TYPE_PAYMENTINFO");
                return true;
            case R.id.action_logininfo:
                onAddClicked("TYPE_LOGININFO");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void onNavigationItemClicked(MenuItem menuItem) {

        int id = menuItem.getItemId();
        Dialog dialog = new Dialog(this);

        switch (id)
        {
            case R.id.action_memo:
                onAddClicked("TYPE_MEMO");
                break;
            case R.id.action_paymentinfo:
                onAddClicked("TYPE_PAYMENTINFO");
                break;
            case R.id.action_logininfo:
                onAddClicked("TYPE_LOGININFO");
                break;



            case R.id.action_playground:
                ACTIVITY_INTENT = new Intent(this, MainPlaygroundActivity.class);
                this.finish();
                this.startActivity(ACTIVITY_INTENT);
                break;
            case R.id.action_settings:
                ACTIVITY_INTENT = new Intent(this, Settings.class);
                this.finish();
                this.startActivity(ACTIVITY_INTENT);
                break;
            case R.id.action_about:
                dialog.setContentView(R.layout.activity_about);
                dialog.setTitle("About Cyber Lock");
                dialog.show();
                break;
            case R.id.action_bugReport:
                buildDialog();
                break;
            case R.id.action_contribute:
                ACTIVITY_INTENT = new Intent(this, Contribute.class);
                this.finish();
                this.startActivity(ACTIVITY_INTENT);
                break;

            case R.id.action_webpage:
                Uri uri = Uri.parse(getString(R.string.SiteURL));
                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                this.finish();
                this.startActivity(i);
                break;
        }
    }
    public void onAddClicked(String TYPE) {
        switch (TYPE) {
            case "TYPE_MEMO":
                ACTIVITY_INTENT = new Intent(this, MemoEditActivity.class);
                break;
            case "TYPE_PAYMENTINFO":
                ACTIVITY_INTENT = new Intent(this, PaymentInfoEditActivity.class);
                break;
            case "TYPE_LOGININFO":
                ACTIVITY_INTENT = new Intent(this, LoginInfoEditActivity.class);
                break;
        }
        this.finish();
        this.startActivity(ACTIVITY_INTENT);
    }
    public void onEditClicked(final String TYPE, final Data data) {
        switch (TYPE) {
            case "TYPE_MEMO":
                ACTIVITY_INTENT = new Intent(mContext, MemoEditActivity.class);
                ACTIVITY_INTENT.putExtra("DATA", data);
                this.finish();
                this.startActivity(ACTIVITY_INTENT);
                break;
            case "TYPE_PAYMENTINFO":
                ACTIVITY_INTENT = new Intent(mContext, PaymentInfoEditActivity.class);
                ACTIVITY_INTENT.putExtra("DATA", data);
                this.finish();
                this.startActivity(ACTIVITY_INTENT);
                break;
            case "TYPE_LOGININFO":
                ACTIVITY_INTENT = new Intent(mContext, LoginInfoEditActivity.class);
                ACTIVITY_INTENT.putExtra("DATA", data);
                this.finish();
                this.startActivity(ACTIVITY_INTENT);
                break;
        }
    }
    public void onMultiSelectClicked() {
        MenuInflater menuInflater = new MenuInflater(mContext);

        if (mListView.getChoiceMode() != ListView.CHOICE_MODE_MULTIPLE_MODAL) {
            mSelectedDatas = new ArrayList<Data>();

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
        if (mSelectedDatas.size() != 0) {
            this.mMasterDatabaseAccess.open();
            for (Data id : mSelectedDatas) {
                this.mMasterDatabaseAccess.delete(id);
            }
            this.mMasterDatabaseAccess.close();
            onMultiSelectClicked();
        } else {
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
    private class DataAdapter extends ArrayAdapter<Data>
    {
        private DataAdapter(Context context, List<Data> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_main_list, parent, false);
            }

            final Data data = mDatas.get(position);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 0);

            CryptContent CRYPTCONTENT = new CryptContent(mContext);

            final RelativeLayout Content = (RelativeLayout) convertView.findViewById(R.id.Content);
            final RelativeLayout MemoListLayout = (RelativeLayout) convertView.findViewById(R.id.MemoListLayout);
            final RelativeLayout PaymentInfoListLayout = (RelativeLayout) convertView.findViewById(R.id.PaymentInfoListLayout);
            final RelativeLayout LoginInfoListLayout = (RelativeLayout) convertView.findViewById(R.id.LoginInfoListLayout);


            final LinearLayout ColourTag = (LinearLayout) convertView.findViewById(R.id.ColourTag);

            final String TYPE = data.getType();
            switch (TYPE) {
                case "TYPE_MEMO":
                    PaymentInfoListLayout.setLayoutParams(params);
                    LoginInfoListLayout.setLayoutParams(params);
                    memoFunctions(data, CRYPTCONTENT, convertView, params, Content);
                    break;
                case "TYPE_PAYMENTINFO":
                    MemoListLayout.setLayoutParams(params);
                    LoginInfoListLayout.setLayoutParams(params);
                    paymentInfoFunctions(data, CRYPTCONTENT, convertView, params);
                    break;
                case "TYPE_LOGININFO":
                    MemoListLayout.setLayoutParams(params);
                    PaymentInfoListLayout.setLayoutParams(params);
                    loginInfoFunctions(data, CRYPTCONTENT, convertView, Content);
                    break;
            }

            final String COL_TAG = data.getColourTag();
            switch (COL_TAG) {
                case "COL_BLUE":
                    ColourTag.setBackgroundColor(getColor(R.color.coltag_blue));
                    break;
                case "COL_RED":
                    ColourTag.setBackgroundColor(getColor(R.color.coltag_red));
                    break;
                case "COL_GREEN":
                    ColourTag.setBackgroundColor(getColor(R.color.coltag_green));
                    break;
                case "COL_YELLOW":
                    ColourTag.setBackgroundColor(getColor(R.color.coltag_yellow));
                    break;
                case "COL_PURPLE":
                    ColourTag.setBackgroundColor(getColor(R.color.coltag_purple));
                    break;
                case "COL_ORANGE":
                    ColourTag.setBackgroundColor(getColor(R.color.coltag_orange));
                    break;
                default:
                    ColourTag.setBackgroundColor(getColor(R.color.coltag_default));
                    break;
            }

            Content.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    if (mListView.getChoiceMode() != ListView.CHOICE_MODE_MULTIPLE_MODAL) {
                        onEditClicked(TYPE, data);
                    } else {
                        if (!data.isSelected()) {
                            Content.setBackground(mResources.getDrawable(R.drawable.clickable_listitem_selected));
                            data.setSelected(true);
                            mSelectedDatas.add(data);
                            mCount++;

                            System.out.println(mSelectedDatas);
                        } else {
                            Content.setBackground(mResources.getDrawable(R.drawable.clickable_listitem));
                            data.setSelected(false);
                            mSelectedDatas.remove(data);
                            mCount--;

                            System.out.println(mSelectedDatas);
                        }

                        getSupportActionBar().setTitle(mCount + " Selected");
                    }
                }
            });

            return convertView;
        }
    }
}