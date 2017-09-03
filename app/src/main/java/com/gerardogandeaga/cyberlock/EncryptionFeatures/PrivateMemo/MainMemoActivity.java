package com.gerardogandeaga.cyberlock.EncryptionFeatures.PrivateMemo;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
import com.gerardogandeaga.cyberlock.Encryption.CryptContent;
import com.gerardogandeaga.cyberlock.R;

import java.util.List;

import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.APP_LOGGED_IN;
import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.mCountDownIsFinished;
import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.mCountDownTimer;
import static com.gerardogandeaga.cyberlock.Supports.Globals.MASTER_KEY;

public class MainMemoActivity extends AppCompatActivity
{
    // DATA
    private MemoDatabaseAccess mMemoDatabaseAccess;
    private List<Memo> mMemos;
    // WIDGETS
    private ListView mListView;
    private FloatingActionButton mFabAdd;

    private Context mContext = this;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_memo);
        ACTIVITY_INTENT = null; // START ACTIVITY WITH EMPTY INTENT

        setupLayout();
    }

    @Override
    public void onResume() // FIRE UP THE DATABASE
    {
        super.onResume();

        this.mMemoDatabaseAccess.open();
        this.mMemos = mMemoDatabaseAccess.getAllMemos();
        this.mMemoDatabaseAccess.close();
        MemoAdapter adapter = new MemoAdapter(this, mMemos);
        this.mListView.setAdapter(adapter);
    }

    public void onAddClicked() // START NEW MEMO
    {
        ACTIVITY_INTENT = new Intent(this, MemoEditActivity.class);
        startActivity(ACTIVITY_INTENT);
    }

    public void onDeleteClicked(Memo memo) // DELETE DATABASE "COMPONENT"
    {
        this.mMemoDatabaseAccess.open();
        this.mMemoDatabaseAccess.delete(memo);
        this.mMemoDatabaseAccess.close();

        ArrayAdapter<Memo> adapter = (ArrayAdapter<Memo>) mListView.getAdapter();
        adapter.remove(memo);
        adapter.notifyDataSetChanged();
    }

    public void onEditClicked(final Memo memo) // EDIT MEMO -> ASYNC TASK
    {
        new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params)
            {
                ACTIVITY_INTENT = new Intent(mContext, MemoEditActivity.class);
                ACTIVITY_INTENT.putExtra("MEMO", memo);
                startActivity(ACTIVITY_INTENT);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid)
            {
                super.onPostExecute(aVoid);
            }
        }.execute();
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

    private class MemoAdapter extends ArrayAdapter<Memo>
    {
        private MemoAdapter(Context context, List<Memo> objects)
        {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if (convertView == null)
            {
                convertView = getLayoutInflater().inflate(R.layout.layout_list_item_memo, parent, false);
            }

            final RelativeLayout Content = (RelativeLayout) convertView.findViewById(R.id.Content);
            final TextView tvLabel = (TextView) convertView.findViewById(R.id.tvLabel);
            final TextView tvMemo = (TextView) convertView.findViewById(R.id.tvMemo);
            final TextView tvDate = (TextView) convertView.findViewById(R.id.tvDate);
            final ImageView imgDelete = (ImageView) convertView.findViewById(R.id.imgDelete);

            final Memo memo = mMemos.get(position);
            memo.setFullDisplayed(false);

            CryptContent content = new CryptContent(mContext);

            String label = content.decryptContent(memo.getLabel(), MASTER_KEY);
            final String text = content.decryptContent(memo.getText(), MASTER_KEY);

            String date = memo.getDate();

            tvLabel.setText(label);
            tvMemo.setText(memo.getShortText(mContext, text));
            tvDate.setText(date);

            Content.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    onEditClicked(memo);
                }
            });

            Content.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    if (memo.isFullDisplayed()) {
                        tvMemo.setText(memo.getShortText(mContext, text));
                        memo.setFullDisplayed(false);
                    } else {
                        tvMemo.setText(text);
                        memo.setFullDisplayed(true);
                    }

                    return false;
                }
            });

            imgDelete.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    onDeleteClicked(memo);
                }
            });

            return convertView;
        }
    }

    private void setupLayout()
    {
        this.mMemoDatabaseAccess = MemoDatabaseAccess.getInstance(this);

        // ACTION BAR TITLE AND BACK BUTTON
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Memo Lock");

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
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {  }
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
    // ----------------------------------------------------------------------------------------------------------------------------------------------------------------------
}