package com.gerardogandeaga.cyberlock.Activitys.Activities.Main;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PorterDuff;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LoginActivity;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Menus.Contribute;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Menus.Settings;
import com.gerardogandeaga.cyberlock.Crypto.CryptoContent;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.ContentDatabase.Data;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.ContentDatabase.MasterDatabaseAccess;
import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.Supports.Globals;
import com.gerardogandeaga.cyberlock.Supports.LogoutProtocol;
import com.gerardogandeaga.cyberlock.Supports.Settings_ScrambleKey;

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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
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
    private void memoFunctions(final Data data, View convertView, LinearLayout.LayoutParams params, final RelativeLayout layoutContent) {
        data.setFullDisplayed(false);

        final String date = data.getDate();
        final String label = data.getLabel(mCryptoContent);
        final String content = data.getContent(mCryptoContent);

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

        layoutContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("HI!");
                if (isEditable(data, layoutContent)) {
                    View dialogView = View.inflate(mContext, R.layout.child_view_memo, null);
                    ImageButton childDone = (ImageButton) dialogView.findViewById(R.id.btnChildDone);
                    ImageButton childEdit = (ImageButton) dialogView.findViewById(R.id.btnChildEdit);
                    TextView childLabel = (TextView) dialogView.findViewById(R.id.tvChildLabel);
                    TextView childMemo = (TextView) dialogView.findViewById(R.id.tvChildMemo);
                    TextView childDate = (TextView) dialogView.findViewById(R.id.tvChildDate);

                    childLabel.setText(label);
                    childMemo.setText(finalMemo);
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
    private void paymentInfoFunctions(Data data, View convertView, LinearLayout.LayoutParams params) {
        data.setFullDisplayed(false);

        final String date = data.getDate();
        final String label = data.getLabel(mCryptoContent);
        final String content = data.getContent(mCryptoContent);

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
    private void loginInfoFunctions(final Data data, View convertView, RelativeLayout layoutContent) {
        data.setFullDisplayed(false);

        final String date = data.getDate();
        final String label = data.getLabel(mCryptoContent);
        final String content = data.getContent(mCryptoContent);

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

//        layoutContent.setOnLongClickListener(new View.OnLongClickListener()
//        {
//            @Override
//            public boolean onLongClick(View v) {
//                if (data.isFullDisplayed()) {
//                    tvUsername.setLayoutParams(hideParams);
//                    tvEmail.setLayoutParams(hideParams);
//                    tvPassword.setLayoutParams(hideParams);
//                    data.setFullDisplayed(false);
//                } else {
//                    tvUsername.setLayoutParams(displayParams);
//                    tvEmail.setLayoutParams(displayParams);
//                    tvPassword.setLayoutParams(displayParams);
//                    data.setFullDisplayed(true);
//                }
//
//                return false;
//            }
//        });
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
                onDeleteClicked();
                return true;
            case R.id.action_done:
                onMultiSelectClicked();
                return true;


            // FUNTIONS
            case R.id.action_memo:
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
            case R.id.action_memo:
                onAddClicked(1);
                break;
            case R.id.action_paymentinfo:
                onAddClicked(2);
                break;
            case R.id.action_logininfo:
                onAddClicked(3);
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
    // MULTI SELECT
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
    private class DataAdapter extends ArrayAdapter<Data> // TODO OPTOMIZE LIST VIEW TO HANDLE MORE INPUTS AND VIEWS!!!
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

            final RelativeLayout Content = (RelativeLayout) convertView.findViewById(R.id.Content);
            final RelativeLayout MemoListLayout = (RelativeLayout) convertView.findViewById(R.id.MemoListLayout);
            final RelativeLayout PaymentInfoListLayout = (RelativeLayout) convertView.findViewById(R.id.PaymentInfoListLayout);
            final RelativeLayout LoginInfoListLayout = (RelativeLayout) convertView.findViewById(R.id.LoginInfoListLayout);


            final ImageView ColourTag = (ImageView) convertView.findViewById(R.id.ColourTag);

            final String TYPE = data.getType(mCryptoContent);
            switch (TYPE) {
                case "TYPE_MEMO":
                    PaymentInfoListLayout.setLayoutParams(params);
                    LoginInfoListLayout.setLayoutParams(params);
                    memoFunctions(data, convertView, params, Content);
                    break;
                case "TYPE_PAYMENTINFO":
                    MemoListLayout.setLayoutParams(params);
                    LoginInfoListLayout.setLayoutParams(params);
                    paymentInfoFunctions(data, convertView, params);
                    break;
                case "TYPE_LOGININFO":
                    MemoListLayout.setLayoutParams(params);
                    PaymentInfoListLayout.setLayoutParams(params);
                    loginInfoFunctions(data, convertView, Content);
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

//            Content.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (mListView.getChoiceMode() != ListView.CHOICE_MODE_MULTIPLE_MODAL) {
//                        if (!data.isSelected()) {
//                            Content.setBackground(mResources.getDrawable(R.drawable.clickable_listitem_selected));
//                            data.setSelected(true);
//                            mSelectedDatas.add(data);
//                            mCount++;
//
////                            System.out.println(mSelectedDatas);
//                        } else {
//                            Content.setBackground(mResources.getDrawable(R.drawable.clickable_listitem));
//                            data.setSelected(false);
//                            mSelectedDatas.remove(data);
//                            mCount--;
//
////                            System.out.println(mSelectedDatas);
//                        }
//                        getSupportActionBar().setTitle(mCount + " Selected");
//                    }
//                }
//            });

            return convertView;
        }
    }
}