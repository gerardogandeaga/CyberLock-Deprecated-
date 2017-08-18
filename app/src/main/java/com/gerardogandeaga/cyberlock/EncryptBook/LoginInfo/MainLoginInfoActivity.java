package com.gerardogandeaga.cyberlock.EncryptBook.LoginInfo;

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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LoginActivity;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Main.MainActivity;
import com.gerardogandeaga.cyberlock.R;

import java.util.List;

import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.APP_LOGGED_IN;
import static com.gerardogandeaga.cyberlock.R.id.tvTitle;

public class MainLoginInfoActivity extends AppCompatActivity
{
    // DATA
    private LoginInfoDatabaseAccess mLoginInfoDatabaseAccess;
    private List<LoginInfo> mLoginInfos;
    // WIDGETS
    private ListView mListView;
    private FloatingActionButton mFabAdd;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_logininfo);
        ACTIVITY_INTENT = null; // START ACTIVITY WITH EMPTY INTENT

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
        startActivity(ACTIVITY_INTENT);
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

    private void onEditClicked(LoginInfo loginInfo) // EDIT PAYMENT INFO
    {
        ACTIVITY_INTENT = new Intent(this, LoginInfoEditActivity.class);
        ACTIVITY_INTENT.putExtra("LOGININFO", loginInfo);
        startActivity(ACTIVITY_INTENT);
    }

    private class loginInfoAdapter extends ArrayAdapter<LoginInfo>
    {

        private loginInfoAdapter(Context context, List<LoginInfo> objects) { super(context, 0, objects); }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if (convertView == null)
            {
                convertView = getLayoutInflater().inflate(R.layout.layout_list_item_logininfo, parent, false);
            }

            TextView tvLabel = (TextView) convertView.findViewById(tvTitle);
            TextView tvDate = (TextView) convertView.findViewById(R.id.tvDate);
            ImageView imgDelete = (ImageView) convertView.findViewById(R.id.imgDelete);
            ImageView imgSite = (ImageView) convertView.findViewById(R.id.imgSite);
            RelativeLayout reContent = (RelativeLayout) convertView.findViewById(R.id.Content);

            final LoginInfo loginInfo = mLoginInfos.get(position);
            loginInfo.setFullDisplayed(false);

            tvDate.setText("Updated:" + loginInfo.getDate());
            tvLabel.setText(loginInfo.getLabel());
            imgSite.setImageDrawable(loginInfo.setImageButton(loginInfo));

            reContent.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    onEditClicked(loginInfo);
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

    // THIS IS THE START OF THE SCRIPT FOR *** THE "TO LOGIN FUNCTION" THIS DETECTS THE ON PRESSED, START, TABS AND HOME BUTTONS IN ORDER TO INITIALIZE SECURITY "FAIL-SAFE"
    @Override
    public void onStart()
    {
        super.onStart();

        if (!APP_LOGGED_IN)
        {
            ACTIVITY_INTENT = new Intent(this, LoginActivity.class);
            this.finish(); // CLEAN UP AND END
            this.startActivity(ACTIVITY_INTENT); // GO TO LOGIN ACTIVITY
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
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();

        if (!this.isFinishing()) { // HOME AND TABS AND SCREEN OFF
            if (ACTIVITY_INTENT == null) // NO PENDING ACTIVITIES ???(MAIN)--->(EDIT)???
            {
                new LogoutProtocol().logoutExecute(this);
            }
        }
        // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
    }
    // -----------------------------------------------------------------------------------------------------------------------------------------------------------------------
}