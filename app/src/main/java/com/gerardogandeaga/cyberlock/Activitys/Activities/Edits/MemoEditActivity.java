package com.gerardogandeaga.cyberlock.Activitys.Activities.Edits;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LoginActivity;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Main.MainActivity;
import com.gerardogandeaga.cyberlock.Encryption.CryptContent;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.ContentDatabase.Data;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.ContentDatabase.MasterDatabaseAccess;
import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.Supports.Globals;
import com.gerardogandeaga.cyberlock.Supports.LogoutProtocol;

import java.util.Scanner;

import static com.gerardogandeaga.cyberlock.Supports.Globals.AUTOSAVE;
import static com.gerardogandeaga.cyberlock.Supports.Globals.DIRECTORY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.ENCRYPTION_ALGO;
import static com.gerardogandeaga.cyberlock.Supports.Globals.MASTER_KEY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.TEMP_PIN;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.APP_LOGGED_IN;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.mCountDownIsFinished;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.mCountDownTimer;

public class MemoEditActivity extends AppCompatActivity
{
    // DATA VARIABLES
    private CryptContent mCRYPTCONTENT;
    private Data mData;

    // WIDGETS
    private EditText
            mEtMemo,
            mEtLabel;
    private TextView mTvDate;

    // INITIAL ON CREATE METHODS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Globals.COLORSCHEME(this);
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        setupLayout();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }
    private void setupLayout() {
        setContentView(R.layout.activity_edit_memo);
        ACTIVITY_INTENT = null;
        //
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Memo Edit");
        getSupportActionBar().setSubtitle("Algorithm: " +
                getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getString(ENCRYPTION_ALGO, "---"));

        this.mEtMemo = (EditText) findViewById(R.id.etText);
        this.mEtLabel = (EditText) findViewById(R.id.etMemoTitle);
        this.mTvDate = (TextView) findViewById(R.id.tvLastUpdated);

        Bundle bundle = getIntent().getExtras();
        setupActivity(bundle);
    }
    private void setupActivity(Bundle bundle) {
        mCRYPTCONTENT = new CryptContent(this);
        if (bundle != null) {
            mData = (Data) bundle.get("DATA");
            if (mData != null) {
                try {
                    String label = mCRYPTCONTENT.DECRYPT_CONTENT(mData.getLabel(), MASTER_KEY);
                    mEtLabel.setText(label);

                    String memo;

                    // TODO PASS INTO ASYNC TASK TO CUT PROCESSES
                    final String content = mCRYPTCONTENT.DECRYPT_CONTENT(mData.getContent(), MASTER_KEY);
                    if (content != null) {
                        Scanner scanner = new Scanner(content);

                        memo = scanner.nextLine();
                        while (scanner.hasNextLine()) {
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

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    // -------------------------

    // ON CLICK
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
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
    // --------

    // ACTIVITY ACTIONS
    private void onSave() {
        MasterDatabaseAccess masterDatabaseAccess = MasterDatabaseAccess.getInstance(this);
        masterDatabaseAccess.open();

        if ((!mEtLabel.getText().toString().matches("")) || (!mEtMemo.getText().toString().matches(""))) {
            final String memo = mEtMemo.getText().toString();

            final String format = "%s";
            final String tmpString = String.format(format,
                    memo);
            if (mData == null) {
                Data temp = new Data();

                temp.setType("TYPE_MEMO"); // TODO ENCRYPT TYPE!
                temp.setLabel(mCRYPTCONTENT.ENCRYPT_KEY(mEtLabel.getText().toString(), MASTER_KEY));
                temp.setContent(mCRYPTCONTENT.ENCRYPT_KEY(tmpString, MASTER_KEY));

                masterDatabaseAccess.save(temp);
            } else {
                mData.setLabel(mCRYPTCONTENT.ENCRYPT_KEY(mEtLabel.getText().toString(), MASTER_KEY));
                mData.setContent(mCRYPTCONTENT.ENCRYPT_KEY(tmpString, MASTER_KEY));

                masterDatabaseAccess.update(mData);
            }

            masterDatabaseAccess.close();
        } else {
            Toast.makeText(this, "Nothing to save", Toast.LENGTH_SHORT).show();

            masterDatabaseAccess.close();
        }
    }
    private void onCancel() {
        ACTIVITY_INTENT = new Intent(this, MainActivity.class);
        this.finish();
        this.startActivity(ACTIVITY_INTENT);
    }
    // ----------------

    // THIS IS THE START OF THE SCRIPT FOR *** THE "TO LOGIN FUNCTION" THIS DETECTS THE ON PRESSED, START, TABS AND HOME BUTTONS IN ORDER TO INITIALIZE SECURITY "FAIL-SAFE"
    @Override
    protected void onStart() {
        super.onStart();

        if (mCountDownIsFinished) {
            if (!APP_LOGGED_IN) {
                if (this.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getBoolean(AUTOSAVE, false)) {
                    onSave();
                    MASTER_KEY = null;
                    TEMP_PIN = null;
                }

                ACTIVITY_INTENT = new Intent(this, LoginActivity.class);
                ACTIVITY_INTENT.putExtra("lastActivity", "MEMO_EDIT");
                ACTIVITY_INTENT.putExtra("lastDatabase", mData);

                this.finish(); // CLEAN UP AND END
                this.startActivity(ACTIVITY_INTENT); // GO TO LOGIN ACTIVITY

                System.gc();
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
            if (this.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getBoolean(AUTOSAVE, false)) {
                onSave();
            }

            ACTIVITY_INTENT = new Intent(this, MainActivity.class);
            this.finish();
            this.startActivity(ACTIVITY_INTENT);
        }
    }
    @Override
    public void onPause() {
        super.onPause();

        if (!this.isFinishing()) // HOME AND TABS AND SCREEN OFF
        {
            if (ACTIVITY_INTENT == null) // NO PENDING ACTIVITIES ???(MAIN)--->(EDIT)???
            {
                if (!this.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getBoolean(AUTOSAVE, false)) {
                    new LogoutProtocol().logoutExecuteAutosaveOff(this);
                } else {
                    new LogoutProtocol().logoutExecuteAutosaveOn(this);
                }
            }

        }
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
}
