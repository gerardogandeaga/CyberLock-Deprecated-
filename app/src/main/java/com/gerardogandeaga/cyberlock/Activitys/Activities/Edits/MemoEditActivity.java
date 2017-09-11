package com.gerardogandeaga.cyberlock.Activitys.Activities.Edits;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LoginActivity;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Main.MainActivity;
import com.gerardogandeaga.cyberlock.Encryption.CryptContent;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.Database.Data;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.Database.MasterDatabaseAccess;
import com.gerardogandeaga.cyberlock.R;

import java.util.Scanner;

import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.APP_LOGGED_IN;
import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.mCountDownIsFinished;
import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.mCountDownTimer;
import static com.gerardogandeaga.cyberlock.Supports.Globals.AUTOSAVE;
import static com.gerardogandeaga.cyberlock.Supports.Globals.DIRECTORY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.MASTER_KEY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.TEMP_PIN;

public class MemoEditActivity extends AppCompatActivity
{
    // DATA
    private CryptContent mCryptContent;
    private Data mData;
    // WIDGETS
    private TextView mTvDate;
    private EditText
            mEtMemo,
            mEtLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_memo);
        ACTIVITY_INTENT = null;

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Memo Edit");

        this.mEtMemo = (EditText) findViewById(R.id.etText);
        this.mEtLabel = (EditText) findViewById(R.id.etMemoTitle);
        this.mTvDate = (TextView) findViewById(R.id.tvLastUpdated);

        Bundle bundle = getIntent().getExtras();
        setupActivity(bundle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        switch (id)
        {
            case (R.id.action_save):
                onSave();
                onBackPressed();
                return true;
            case (R.id.action_cancel):
                onCancel();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onSave()
    {
        MasterDatabaseAccess masterDatabaseAccess = MasterDatabaseAccess.getInstance(this);
        masterDatabaseAccess.open();

        if ((!mEtLabel.getText().toString().matches("")) || (!mEtMemo.getText().toString().matches("")))
        {
            final String memo = mEtMemo.getText().toString();

            final String format = "%s";
            final String tmpString = String.format(format,
                    memo);
            if (mData == null)
            {
                Data temp = new Data();

                temp.setType("TYPE_MEMO");
                temp.setLabel(mCryptContent.encryptContent(mEtLabel.getText().toString(), MASTER_KEY));
                temp.setContent(mCryptContent.encryptContent(tmpString, MASTER_KEY));

                masterDatabaseAccess.save(temp);
            } else
            {
                mData.setLabel(mCryptContent.encryptContent(mEtLabel.getText().toString(), MASTER_KEY));
                mData.setContent(mCryptContent.encryptContent(tmpString, MASTER_KEY));

                masterDatabaseAccess.update(mData);
            }

            masterDatabaseAccess.close();
        } else
        {
            Toast.makeText(this, "Nothing to save", Toast.LENGTH_SHORT).show();

            masterDatabaseAccess.close();
        }
    }

    private void onCancel()
    {
        ACTIVITY_INTENT = new Intent(this, MainActivity.class);
        this.finish();
        this.startActivity(ACTIVITY_INTENT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    private void setupActivity(Bundle bundle)
    {
        mCryptContent = new CryptContent(this);
        if (bundle != null)
        {
            mData = (Data) bundle.get("DATA");
            if (mData != null)
            {
                try
                {
                    String label = mCryptContent.decryptContent(mData.getLabel(), MASTER_KEY);
                    mEtLabel.setText(label);

                    String memo;

                    final String content = mCryptContent.decryptContent(mData.getContent(), MASTER_KEY);
                    if (content != null)
                    {
                        Scanner scanner = new Scanner(content);

                        memo = scanner.nextLine();
                        while (scanner.hasNextLine())
                        {
                            memo += "\n";
                            memo += scanner.hasNextLine();
                        }
                        scanner.close();

                        mEtMemo.setText(content);
                    }

                    if (!mData.getDate().matches("")) {
                        mTvDate.setText("Last Updated: " + mData.getDate());
                    } else {
                        mTvDate.setText("Last Updated: ---");
                    }

                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    // THIS IS THE START OF THE SCRIPT FOR *** THE "TO LOGIN FUNCTION" THIS DETECTS THE ON PRESSED, START, TABS AND HOME BUTTONS IN ORDER TO INITIALIZE SECURITY "FAIL-SAFE"
    @Override
    protected void onStart()
    {
        super.onStart();

        if (mCountDownIsFinished)
        {
            if (!APP_LOGGED_IN)
            {
                if (this.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getBoolean(AUTOSAVE, false)) { onSave(); MASTER_KEY = null; TEMP_PIN = null; }

                ACTIVITY_INTENT = new Intent(this, LoginActivity.class);
                ACTIVITY_INTENT.putExtra("lastActivity", "MEMO_EDIT");
                ACTIVITY_INTENT.putExtra("lastDatabase", mData);

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
            if (this.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getBoolean(AUTOSAVE, false)) { onSave(); }

            ACTIVITY_INTENT = new Intent(this, MainActivity.class);
            this.finish();
            this.startActivity(ACTIVITY_INTENT);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();

        if (!this.isFinishing()) // HOME AND TABS AND SCREEN OFF
        {
            System.out.println("PAUSE CALLED!");
            if (ACTIVITY_INTENT == null) // NO PENDING ACTIVITIES ???(MAIN)--->(EDIT)???
            {
                if (!this.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getBoolean(AUTOSAVE, false))
                {
                    new LogoutProtocol().logoutExecuteAutosaveOff(this);
                }
                else
                {
                    new LogoutProtocol().logoutExecuteAutosaveOn(this);
                }
            }

        }
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
}
