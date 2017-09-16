package com.gerardogandeaga.cyberlock.Activitys.Activities.Main;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gerardogandeaga.cyberlock.Activitys.Activities.Edits.LoginInfoEditActivity;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Edits.MemoEditActivity;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Edits.PaymentInfoEditActivity;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LoginActivity;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Menus.Contribute;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Menus.Settings;
import com.gerardogandeaga.cyberlock.Encryption.CryptContent;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.Database.Data;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.Database.MasterDatabaseAccess;
import com.gerardogandeaga.cyberlock.R;

import java.util.List;
import java.util.Scanner;

import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.APP_LOGGED_IN;
import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.mCountDownIsFinished;
import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.mCountDownTimer;
import static com.gerardogandeaga.cyberlock.Supports.Globals.MASTER_KEY;

public class MainActivity extends AppCompatActivity
{
    // DATA
    private MasterDatabaseAccess mMasterDatabaseAccess;
    private List<Data> mDatas;
    // WIDGETS
    private ListView mListView;
    private ImageButton
            mBtnNewMemo,
            mBtnNewPaymentInfo,
            mBtnNewLoginInfo;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mDrawerToggle;

    private Context mContext = this;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);
        ACTIVITY_INTENT = null;

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }

        setupLayout();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        Dialog dialog = new Dialog(this);

        if (mDrawerToggle.onOptionsItemSelected(item)) return true;

        switch (item.getItemId())
        {
            case (R.id.action_settings):
                ACTIVITY_INTENT = new Intent(this, Settings.class);
                this.finish();
                this.startActivity(ACTIVITY_INTENT);
                overridePendingTransition(R.anim.anim_push_downin, R.anim.anim_push_downout);
                return true;

            case (R.id.action_about):
                dialog.setContentView(R.layout.activity_about);
                dialog.setTitle("About Cyber Lock");
                dialog.show();
                return true;


            case (R.id.action_contribute):
                ACTIVITY_INTENT = new Intent(this, Contribute.class);
                this.finish();
                this.startActivity(ACTIVITY_INTENT);
                overridePendingTransition(R.anim.anim_push_downin, R.anim.anim_push_downout);
                return true;

            case (R.id.action_playground):
                ACTIVITY_INTENT = new Intent(this, MainPlaygroundActivity.class);
                this.finish();
                this.startActivity(ACTIVITY_INTENT);
                overridePendingTransition(R.anim.anim_slide_inright, R.anim.anim_slide_outleft);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        this.mMasterDatabaseAccess.open();
        this.mDatas = mMasterDatabaseAccess.getAllData();
        this.mMasterDatabaseAccess.close();
        DataAdapter adapter = new DataAdapter(this, mDatas);
        this.mListView.setAdapter(adapter);
    }

    public void onAddClicked(String TYPE)
    {
        switch (TYPE)
        {
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

    public void onDeleteClicked(Data data)
    {
        this.mMasterDatabaseAccess.open();
        this.mMasterDatabaseAccess.delete(data);
        this.mMasterDatabaseAccess.close();

        ArrayAdapter<Data> adapter = (ArrayAdapter<Data>) mListView.getAdapter();
        adapter.remove(data);
        adapter.notifyDataSetChanged();
    }

    public void onEditClicked(final String TYPE, final Data data)
    {
        switch (TYPE)
        {
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

    private class DataAdapter extends ArrayAdapter<Data>
    {
        private DataAdapter(Context context, List<Data> objects)
        {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if (convertView == null) { convertView = getLayoutInflater().inflate(R.layout.list_item, parent, false); }

            final Data data = mDatas.get(position);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 0);

            CryptContent cryptContent = new CryptContent(mContext);

            final LinearLayout Content = (LinearLayout) convertView.findViewById(R.id.Content);

            final RelativeLayout MemoListLayout = (RelativeLayout) convertView.findViewById(R.id.MemoListLayout);
            final RelativeLayout PaymentInfoListLayout = (RelativeLayout) convertView.findViewById(R.id.PaymentInfoListLayout);
            final RelativeLayout LoginInfoListLayout = (RelativeLayout) convertView.findViewById(R.id.LoginInfoListLayout);

//            final ImageView imgDelete = (ImageView) convertView.findViewById(R.id.imgDelete);

            final String TYPE = data.getType();

            switch (TYPE)
            {
                case "TYPE_MEMO":
                    PaymentInfoListLayout.setLayoutParams(params);
                    LoginInfoListLayout.setLayoutParams(params);

                    memoFunctions(data, cryptContent, convertView, params, Content);
                    break;

                case "TYPE_PAYMENTINFO":
                    MemoListLayout.setLayoutParams(params);
                    LoginInfoListLayout.setLayoutParams(params);

                    paymentInfoFunctions(data, cryptContent, convertView, params);
                    break;

                case "TYPE_LOGININFO":
                    MemoListLayout.setLayoutParams(params);
                    PaymentInfoListLayout.setLayoutParams(params);

                    loginInfoFunctions(data, cryptContent, convertView, Content);
                    break;
            }


            Content.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    onEditClicked(TYPE, data);
                    System.out.println(TYPE);
                }
            });

            return convertView;
        }
    }

    private void memoFunctions(final Data data, CryptContent cryptContent, View convertView, LinearLayout.LayoutParams params, LinearLayout layoutContent)
    {
        data.setFullDisplayed(false);

        final String date = data.getDate();
        final String label = cryptContent.decryptContent(data.getLabel(), MASTER_KEY);
        final String content = cryptContent.decryptContent(data.getContent(), MASTER_KEY);

        String memo;

        Scanner scanner = new Scanner(content);

        memo = scanner.nextLine();
        while (scanner.hasNextLine())
        {
            memo += "\n";
            memo += scanner.nextLine();
        }
        scanner.close();

        final TextView tvDate = (TextView) convertView.findViewById(R.id.tvMemoDate);
        final TextView tvLabel = (TextView) convertView.findViewById(R.id.tvMemoLabel);
        final TextView tvMemo = (TextView) convertView.findViewById(R.id.tvMemo);

        if (label != null) { tvLabel.setText(label); }                           else { tvLabel.setLayoutParams(params); }
        if (memo != null) { tvMemo.setText(data.getShortText(mContext, memo)); } else { tvMemo.setLayoutParams(params); }
        tvDate.setText(date);

        final String finalMemo = memo;
        layoutContent.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                if (data.isFullDisplayed())
                {
                    if (finalMemo != null) tvMemo.setText(data.getShortText(mContext, finalMemo));
                    data.setFullDisplayed(false);
                } else {
                    if (finalMemo != null) tvMemo.setText(finalMemo);
                    data.setFullDisplayed(true);
                }

                return false;
            }
        });
    }

    private void paymentInfoFunctions(Data data, CryptContent cryptContent, View convertView, LinearLayout.LayoutParams params)
    {
        data.setFullDisplayed(false);

        final String date = data.getDate();
        final String label = cryptContent.decryptContent(data.getLabel(), MASTER_KEY);
        final String content = cryptContent.decryptContent(data.getContent(), MASTER_KEY);

        String cardName;
        String cardNumber;
        String cardType;

        Scanner scanner = new Scanner(content);

        cardName = scanner.nextLine();
        cardNumber = scanner.nextLine();
        cardType = scanner.nextLine();

        scanner.close();

        String tempNumber = "";

        final TextView tvDate = (TextView) convertView.findViewById(R.id.tvPaymentInfoDate);
        final TextView tvLabel = (TextView) convertView.findViewById(R.id.tvPaymentInfoLabel);
        final TextView tvCardName = (TextView) convertView.findViewById(R.id.tvCardName);
        final TextView tvCardNumber = (TextView) convertView.findViewById(R.id.tvCardNumber);
        final ImageView imgCard = (ImageView) convertView.findViewById(R.id.imgCard);

        if (label != null) { tvLabel.setText(label); }               else { tvLabel.setLayoutParams(params); }
        if (!cardName.matches("")) { tvCardName.setText(cardName); } else { tvCardName.setLayoutParams(params); }
        if (!cardNumber.matches(""))
        { // ***** ASTRIX ALGORITHM
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
        }                                                        else { tvCardNumber.setLayoutParams(params); }
        tvDate.setText(date);

        switch (cardType)
        {
            case ("None"): imgCard.setImageResource(R.drawable.card_default); break;
            case ("Visa"): imgCard.setImageResource(R.drawable.card_visa); break;
            case ("Master Card"): imgCard.setImageResource(R.drawable.card_mastercard); break;
            case ("American Express"): imgCard.setImageResource(R.drawable.card_americanexpress); break;
            case ("Discover"): imgCard.setImageResource(R.drawable.card_discover); break;
            case ("Other"): imgCard.setImageResource(R.drawable.card_default); break;
            default: imgCard.setImageResource(R.drawable.card_default); break;
        }
    }

    private void loginInfoFunctions(final Data data, CryptContent cryptContent, View convertView, LinearLayout layoutContent)
    {
        data.setFullDisplayed(false);

        final String date = data.getDate();
        final String label = cryptContent.decryptContent(data.getLabel(), MASTER_KEY);
        final String content = cryptContent.decryptContent(data.getContent(), MASTER_KEY);

        String url;
        final String username;
        final String email;
        final String password;

        Scanner scanner = new Scanner(content);

        url = scanner.nextLine();
        username = scanner.nextLine();
        email = scanner.nextLine();
        password = scanner.nextLine();

        final TextView tvDate = (TextView) convertView.findViewById(R.id.tvLoginInfoDate);
        final TextView tvLabel = (TextView) convertView.findViewById(R.id.tvLoginInfoLabel);
        final TextView tvUrl = (TextView) convertView.findViewById(R.id.tvURL);
        final TextView tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
        final TextView tvEmail = (TextView) convertView.findViewById(R.id.tvEmail);
        final TextView tvPassword = (TextView) convertView.findViewById(R.id.tvPassword);

        final LinearLayout.LayoutParams hideParams = new LinearLayout.LayoutParams(0, 0);
        final LinearLayout.LayoutParams displayParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (!label.matches("")) { tvLabel.setText(label); }                                                                 else { tvLabel.setLayoutParams(hideParams); }
        if (!url.matches("")) { tvUrl.setText("Url: " + url); }                                                             else { tvUrl.setLayoutParams(hideParams); }
        if (!username.matches("")) { tvUsername.setText("Username: " + username); tvUsername.setLayoutParams(hideParams); } else { tvUsername.setLayoutParams(hideParams); }
        if (!email.matches("")) { tvEmail.setText("Email: " + email); tvEmail.setLayoutParams(hideParams); }                else { tvEmail.setLayoutParams(hideParams); }
        if (!password.matches("")) { tvPassword.setText("Password: " + password); tvPassword.setLayoutParams(hideParams); } else { tvPassword.setLayoutParams(hideParams); }
        tvDate.setText(date);

        layoutContent.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                if (data.isFullDisplayed())
                {
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

    private void setupLayout()
    {
        this.mMasterDatabaseAccess = MasterDatabaseAccess.getInstance(this);

        // ACTION BAR TITLE AND BACK BUTTON
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("{ Cyber Lock }");

        mDrawerLayout = (DrawerLayout) findViewById(R.id.Content);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.Open, R.string.Close);
        mNavigationView = (NavigationView) findViewById(R.id.NavigationContent);

        this.mListView = (ListView) findViewById(R.id.listView);
        this.mBtnNewMemo = (ImageButton) findViewById(R.id.btnNewMemo);
        this.mBtnNewPaymentInfo = (ImageButton) findViewById(R.id.btnNewPaymentInfo);
        this.mBtnNewLoginInfo = (ImageButton) findViewById(R.id.btnNewLoginInfo);

        calculateDrawerSize();
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        this.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {  }
        });

        this.mBtnNewMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                onAddClicked("TYPE_MEMO");
            }
        });
        this.mBtnNewPaymentInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                onAddClicked("TYPE_PAYMENTINFO");
            }
        });
        this.mBtnNewLoginInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                onAddClicked("TYPE_LOGININFO");
            }
        });

        this.mBtnNewMemo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v)
            {
                Toast.makeText(mContext, "New Memo", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        this.mBtnNewPaymentInfo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v)
            {
                Toast.makeText(mContext, "New Payment Information", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        this.mBtnNewLoginInfo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v)
            {
                Toast.makeText(mContext, "New Login Credentials", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

    }

    private void calculateDrawerSize()
    {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // THIS IS THE START OF THE SCRIPT FOR *** THE "TO LOGIN FUNCTION" THIS DETECTS THE ON PRESSED, START, TABS AND HOME BUTTONS IN ORDER TO INITIALIZE SECURITY "FAIL-SAFE"
    @Override
    public void onStart()
    {
        super.onStart();

        if (mCountDownIsFinished)
        {
            if (!APP_LOGGED_IN)
            {
                ACTIVITY_INTENT = new Intent(this, LoginActivity.class);
                this.finish(); // CLEAN UP AND END
                this.startActivity(ACTIVITY_INTENT); // GO TO LOGIN ACTIVITY
            }
        } else
        {
            if (mCountDownTimer != null)
            {
                System.out.println("Cancel Called!");
                mCountDownTimer.cancel();

            }
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        if (ACTIVITY_INTENT == null) // NO PENDING ACTIVITIES ???(MAIN)--->(EDIT)???
        {
            new LogoutProtocol().logoutImmediate(this);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();

        if (!this.isFinishing()) { // HOME AND TABS AND SCREEN OFF
            if (ACTIVITY_INTENT == null) // NO PENDING ACTIVITIES ???(MAIN)--->(EDIT)???
            {
                new LogoutProtocol().logoutExecuteAutosaveOff(this);
            }
        }
        // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
    }
}