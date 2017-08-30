package com.gerardogandeaga.cyberlock.EncryptionFeatures.PrivateMemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LoginActivity;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol;
import com.gerardogandeaga.cyberlock.Encryption.CryptContent;
import com.gerardogandeaga.cyberlock.Encryption.CryptKeyHandler;
import com.gerardogandeaga.cyberlock.R;

import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.APP_LOGGED_IN;
import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.mCountDownIsFinished;
import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.mCountDownTimer;
import static com.gerardogandeaga.cyberlock.Supports.Globals.AUTOSAVE;
import static com.gerardogandeaga.cyberlock.Supports.Globals.CRYPT_KEY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.DIRECTORY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.TEMP_PIN;

public class MemoEditActivity extends AppCompatActivity
{
    // DATA
    private Memo mMemo;
    // WIDGETS
    private EditText mEtMemo, mEtTag;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_memo);
        ACTIVITY_INTENT = null;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Memo Edit");

        this.mEtMemo = (EditText) findViewById(R.id.etText);
        this.mEtTag = (EditText) findViewById(R.id.etMemoTitle);

        Bundle bundle = getIntent().getExtras();
        onInstantCreate(bundle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        switch (id)
        {
            case (R.id.action_save): onSave(); onBackPressed(); return true;
            case (R.id.action_cancel): onCancel(); return true;
            case android.R.id.home: onBackPressed(); return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    private void onInstantCreate(Bundle bundle)
    {
        if (bundle != null)
        {
            mMemo = (Memo) bundle.get("MEMO");
            if (mMemo != null)
            {
                try
                {
                    String ENCDEC_KEY = (new CryptKeyHandler(this).DECRYPTKEY(this.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getString(CRYPT_KEY, null),
                            this.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getString(TEMP_PIN, null)));

                    CryptContent content = new CryptContent(this);

                    if (!mMemo.getLabel().matches("")) { this.mEtTag.setText(mMemo.getLabel()); } // SET LABEL
                    if (!mMemo.getText().matches("")) { this.mEtMemo.setText(content.decryptContent(mMemo.getText(), ENCDEC_KEY)); } // PULLED DECRYPTED VALUES (STRING)

                    ENCDEC_KEY = null;
                    System.gc();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onSave()
    {
        MemoDatabaseAccess memoDatabaseAccess = MemoDatabaseAccess.getInstance(this);
        memoDatabaseAccess.open();

        String ENCDEC_KEY = (new CryptKeyHandler(this).DECRYPTKEY(this.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getString(CRYPT_KEY, null),
                                                      this.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getString(TEMP_PIN, null)));

        if ((!mEtTag.getText().toString().matches("")) || (!mEtMemo.getText().toString().matches("")))
        {
            CryptContent content = new CryptContent(this);

            if (mMemo == null) // WHEN SAVING A NEW UNKNOWN MEMO
            {
                // ADD NEW MEMO
                Memo temp = new Memo();

                temp.setText(content.encryptContent(mEtMemo.getText().toString(), ENCDEC_KEY)); // SET TEXT
                if (!mEtTag.getText().toString().matches(""))
                {
                    temp.setLabel(mEtTag.getText().toString());
                } else
                {
                    temp.setLabel("");
                } // SET TAG

                // SAVE NEW DATA TABLE
                memoDatabaseAccess.save(temp);
            } else
            {
                // UPDATE THE MEMO
                // SET TEXT
                mMemo.setText(content.encryptContent(mEtMemo.getText().toString(), ENCDEC_KEY)); // SET TEXT
                if (!mEtTag.getText().toString().matches(""))
                {
                    mMemo.setLabel(mEtTag.getText().toString());
                } else
                {
                    mMemo.setLabel("");
                } // SET TAG

                // UPDATE DATA TABLE
                memoDatabaseAccess.update(mMemo);
            }

            ENCDEC_KEY = null;
            System.gc(); // GARBAGE COLLECT TO TERMINATE -KEY- VARIABLE

            memoDatabaseAccess.close();
        } else
        {
            Toast.makeText(this, "Nothing to save", Toast.LENGTH_SHORT).show();

            ENCDEC_KEY = null;
            System.gc(); // GARBAGE COLLECT TO TERMINATE -KEY- VARIABLE

            memoDatabaseAccess.close();
        }
    }

    public void onCancel()
    {
        ACTIVITY_INTENT = new Intent(this, MainMemoActivity.class);
        this.finish();
        this.startActivity(ACTIVITY_INTENT);
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
                if (this.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getBoolean(AUTOSAVE, false))
                {
                    onSave();

                    this.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).edit().remove(TEMP_PIN).apply();
                } else
                {
                    ACTIVITY_INTENT = new Intent(this, LoginActivity.class);
                    ACTIVITY_INTENT.putExtra("lastActivity", "MEMO_EDIT");
                    ACTIVITY_INTENT.putExtra("lastDatabase", mMemo);
                }

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
            if (this.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getBoolean(AUTOSAVE, false)) { onSave(); }

            ACTIVITY_INTENT = new Intent(this, MainMemoActivity.class);
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
