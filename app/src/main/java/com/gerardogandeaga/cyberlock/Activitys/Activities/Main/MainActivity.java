package com.gerardogandeaga.cyberlock.Activitys.Activities.Main;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gerardogandeaga.cyberlock.Activitys.Activities.Menus.BugReportActivity;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Edits.LoginInfoEditActivity;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Edits.MemoEditActivity;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Edits.PaymentInfoEditActivity;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LoginActivity;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Menus.Contribute;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Menus.Settings;
import com.gerardogandeaga.cyberlock.Encryption.CryptContent;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.ContentDatabase.Data;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.ContentDatabase.MasterDatabaseAccess;
import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.Supports.Globals;
import com.gerardogandeaga.cyberlock.Supports.LogoutProtocol;

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

    // DATA VARIABLES
    private MasterDatabaseAccess mMasterDatabaseAccess;
    private List<Data> mDatas;

    // WIDGETS
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mListView;
    private NavigationView mNavigationView;

    // INITIAL ON CREATE METHODS
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Globals.COLORSCHEME(this);
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        setupLayout();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public void onResume() {
        super.onResume();

        this.mMasterDatabaseAccess.open();
        this.mDatas = mMasterDatabaseAccess.getAllData();
        this.mMasterDatabaseAccess.close();
        DataAdapter adapter = new DataAdapter(this, mDatas);
        this.mListView.setAdapter(adapter);
    }
    private void setupLayout() {
        setContentView(R.layout.activity_main);
        ACTIVITY_INTENT = null;
        // GET WIDGETS
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.Content);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        this.mNavigationView = (NavigationView) findViewById(R.id.NavigationContent);
        this.mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        this.mListView = (ListView) findViewById(R.id.listView);

        // GET DATA
        this.mMasterDatabaseAccess = MasterDatabaseAccess.getInstance(this);
        // SET WIDGETS
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cyber Lock");

        this.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });

        calculateDrawerSize();
        drawerLayout.addDrawerListener(mDrawerToggle);
        this.mDrawerToggle.syncState();

        this.mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                onNavigationItemClicked(item);

                return false;
            }
        });
    } // LAYOUT SET UP
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

            final LinearLayout Content = (LinearLayout) convertView.findViewById(R.id.Content);

            final RelativeLayout MemoListLayout = (RelativeLayout) convertView.findViewById(R.id.MemoListLayout);
            final RelativeLayout PaymentInfoListLayout = (RelativeLayout) convertView.findViewById(R.id.PaymentInfoListLayout);
            final RelativeLayout LoginInfoListLayout = (RelativeLayout) convertView.findViewById(R.id.LoginInfoListLayout);

//            final ImageView imgDelete = (ImageView) convertView.findViewById(R.id.imgDelete);

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


            Content.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    onEditClicked(TYPE, data);
                    System.out.println(TYPE);
                }
            });

            return convertView;
        }
    }
    // -------------------------

    // FUNCTIONS FOR THE DATA ADAPTER
    private void memoFunctions(final Data data, CryptContent CRYPTCONTENT, View convertView, LinearLayout.LayoutParams params, LinearLayout layoutContent) {
        data.setFullDisplayed(false);

        final String date = data.getDate();
        final String label = CRYPTCONTENT.DECRYPT_CONTENT(data.getLabel(), MASTER_KEY);
        final String content = CRYPTCONTENT.DECRYPT_CONTENT(data.getContent(), MASTER_KEY);

        String memo = "";

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
    private void loginInfoFunctions(final Data data, CryptContent CRYPTCONTENT, View convertView, LinearLayout layoutContent) {
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
                ACTIVITY_INTENT = new Intent(this, BugReportActivity.class);
                this.finish();
                this.startActivity(ACTIVITY_INTENT);
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
    public void onDeleteClicked(Data data) {
        this.mMasterDatabaseAccess.open();
        this.mMasterDatabaseAccess.delete(data);
        this.mMasterDatabaseAccess.close();

        ArrayAdapter<Data> adapter = (ArrayAdapter<Data>) mListView.getAdapter();
        adapter.remove(data);
        adapter.notifyDataSetChanged();
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
}