package com.gerardogandeaga.cyberlock.EncryptionFeatures.LoginInfo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LoginActivity;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Main.MainActivity;
import com.gerardogandeaga.cyberlock.Encryption.CryptContent;
import com.gerardogandeaga.cyberlock.R;

import java.util.List;

import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.APP_LOGGED_IN;
import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.mCountDownIsFinished;
import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.mCountDownTimer;
import static com.gerardogandeaga.cyberlock.Supports.Globals.MASTER_KEY;

public class MainLoginInfoActivity extends AppCompatActivity
{
    // DATA
    private LoginInfoDatabaseAccess mLoginInfoDatabaseAccess;
    private List<LoginInfo> mLoginInfos;
    // WIDGETS
    private ListView mListView;
    private FloatingActionButton mFabAdd;

    private Context mContext = this;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_logininfo);
        ACTIVITY_INTENT = null; // START ACTIVITY WITH EMPTY INTENT

        setupLayout();
    }

    @Override
    public void onResume() // FIRE UP THE DATABASE
    {
        super.onResume();

        this.mLoginInfoDatabaseAccess.open();
        this.mLoginInfos = mLoginInfoDatabaseAccess.getAllLoginInfos();
        this.mLoginInfoDatabaseAccess.close();
        loginInfoAdapter adapter = new loginInfoAdapter(this, mLoginInfos);
        this.mListView.setAdapter(adapter);
    }

    private void onAddClicked() // START NEW PAYMENT INFO
    {
        ACTIVITY_INTENT = new Intent(this, LoginInfoEditActivity.class);
        finish();
        this.startActivity(ACTIVITY_INTENT);
    }

    private void onDeleteClicked(LoginInfo loginInfo) // DELETE DATABASE "COMPONENT"
    {
        this.mLoginInfoDatabaseAccess.open();
        this.mLoginInfoDatabaseAccess.delete(loginInfo);
        this.mLoginInfoDatabaseAccess.close();

        ArrayAdapter<LoginInfo> adapter = (ArrayAdapter<LoginInfo>) mListView.getAdapter();
        adapter.remove(loginInfo);
        adapter.notifyDataSetChanged();
    }

    private void onEditClicked(final LoginInfo loginInfo) // EDIT PAYMENT INFO -> ASYNC TASK
    {
        ACTIVITY_INTENT = new Intent(mContext, LoginInfoEditActivity.class);
        ACTIVITY_INTENT.putExtra("LOGININFO", loginInfo);
        finish();
        this.startActivity(ACTIVITY_INTENT);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) // ACTION BAR BACK BUTTON RESPONSE
    {
        switch (item.getItemId())
        {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class loginInfoAdapter extends ArrayAdapter<LoginInfo>
    {

        private loginInfoAdapter(Context context, List<LoginInfo> objects) { super(context, 0, objects); }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if (convertView == null) { convertView = getLayoutInflater().inflate(R.layout.layout_list_item_logininfo, parent, false); }

            final LoginInfo loginInfo = mLoginInfos.get(position);
            loginInfo.setFullDisplayed(false);
            CryptContent content = new CryptContent(mContext);

            final String label = content.decryptContent(loginInfo.getLabel(), MASTER_KEY);
            final String url = content.decryptContent(loginInfo.getUrl(), MASTER_KEY);
            final String username = content.decryptContent(loginInfo.getUsername(), MASTER_KEY);
            final String email = content.decryptContent(loginInfo.getEmail(), MASTER_KEY);
            final String password = content.decryptContent(loginInfo.getPassword(), MASTER_KEY);
            final String notes = content.decryptContent(loginInfo.getNotes(), MASTER_KEY);
            final String date = loginInfo.getDate();

            final RelativeLayout Content = (RelativeLayout) convertView.findViewById(R.id.Content);
            final TextView tvLabel = (TextView) convertView.findViewById(R.id.tvLabel);
            final TextView tvUrl = (TextView) convertView.findViewById(R.id.tvURL);
            final TextView tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
            final TextView tvEmail = (TextView) convertView.findViewById(R.id.tvEmail);
            final TextView tvPassword = (TextView) convertView.findViewById(R.id.tvPassword);
            final TextView tvNotes= (TextView) convertView.findViewById(R.id.tvNotes);
            final TextView tvDate = (TextView) convertView.findViewById(R.id.tvDate);
            final ImageView imgDelete = (ImageView) convertView.findViewById(R.id.imgDelete);

            final LinearLayout.LayoutParams hideParams = new LinearLayout.LayoutParams(0, 0);
            final LinearLayout.LayoutParams displayParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            if (label != null) { tvLabel.setText(label); }                                                                 else { tvLabel.setLayoutParams(hideParams); }
            if (url != null) { tvUrl.setText("Url: " + url); }                                                             else { tvUrl.setLayoutParams(hideParams); }
            if (username != null) { tvUsername.setText("Username: " + username); tvUsername.setLayoutParams(hideParams); } else { tvUsername.setLayoutParams(hideParams); }
            if (email != null) { tvEmail.setText("Email: " + email); tvEmail.setLayoutParams(hideParams); }                else { tvEmail.setLayoutParams(hideParams); }
            if (password != null) { tvPassword.setText("Password: " + password); tvPassword.setLayoutParams(hideParams); } else { tvPassword.setLayoutParams(hideParams); }
            if (notes != null) { tvNotes.setText("Notes: " + notes); tvNotes.setLayoutParams(hideParams); }                else { tvNotes.setLayoutParams(hideParams); }
            tvDate.setText(date);

            Content.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    onEditClicked(loginInfo);
                }
            });
            Content.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    if (loginInfo.isFullDisplayed())
                    {
                        if (username != null) { tvUsername.setLayoutParams(hideParams); } else { tvUsername.setLayoutParams(hideParams); }
                        if (email != null) { tvEmail.setLayoutParams(hideParams); }       else { tvEmail.setLayoutParams(hideParams); }
                        if (password != null) { tvPassword.setLayoutParams(hideParams); } else { tvPassword.setLayoutParams(hideParams); }
                        if (notes != null) { tvNotes.setLayoutParams(hideParams); }       else { tvNotes.setLayoutParams(hideParams); }
                        loginInfo.setFullDisplayed(false);
                    } else {
                        if (username != null) { tvUsername.setLayoutParams(displayParams); } else { tvUsername.setLayoutParams(hideParams); }
                        if (email != null) { tvEmail.setLayoutParams(displayParams); }       else { tvEmail.setLayoutParams(hideParams); }
                        if (password != null) { tvPassword.setLayoutParams(displayParams); } else { tvPassword.setLayoutParams(hideParams); }
                        if (notes != null) { tvNotes.setLayoutParams(displayParams); }       else { tvNotes.setLayoutParams(hideParams); }
                        loginInfo.setFullDisplayed(true);
                    }

                    return false;
                }
            });
            imgDelete.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    onDeleteClicked(loginInfo);
                }
            });

            return convertView;
        }
    }

    private void setupLayout()
    {
        this.mLoginInfoDatabaseAccess = LoginInfoDatabaseAccess.getInstance(this);

        // ACTION BAR TITLE AND BACK BUTTON
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Login Lock");

        this.mListView = (ListView) findViewById(R.id.listView);
        this.mFabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);

        this.mFabAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onAddClicked();
            }
        });
        this.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
            }
        });
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

                System.gc();
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
            ACTIVITY_INTENT = new Intent(this, MainActivity.class);
            finish();
            this.startActivity(ACTIVITY_INTENT);
            overridePendingTransition(R.anim.anim_slide_inleft, R.anim.anim_slide_outright);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();

        if (!this.isFinishing())
        {
            if (ACTIVITY_INTENT == null) // NO PENDING ACTIVITIES ???(MAIN)--->(EDIT)???
            {
                new LogoutProtocol().logoutExecuteAutosaveOff(this);
            }
        }
    }
    // -----------------------------------------------------------------------------------------------------------------------------------------------------------------------
}