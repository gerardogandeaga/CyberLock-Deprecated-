package com.gerardogandeaga.cyberlock.EncryptBook.PrivateMemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LoginActivity;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Main.MainActivity;
import com.gerardogandeaga.cyberlock.Encryption.AESContent;
import com.gerardogandeaga.cyberlock.Encryption.AESKeyHandler;
import com.gerardogandeaga.cyberlock.R;

import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.APP_LOGGED_IN;

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

        this.mEtMemo = (EditText) findViewById(R.id.etText);
        this.mEtTag = (EditText) findViewById(R.id.etMemoTitle);
        Button btnSave = (Button) findViewById(R.id.btnSave);
        Button btnCancel = (Button) findViewById(R.id.btnCancel);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
        {
            mMemo = (Memo) bundle.get("MEMO");
            if (mMemo != null)
            {
                try
                {
                    String ENCDEC_KEY = (AESKeyHandler.DECRYPTKEY(this.getSharedPreferences("com.gerardogandeaga.cyberlock", Context.MODE_PRIVATE).getString("KEY", null),
                                                                  this.getSharedPreferences("com.gerardogandeaga.cyberlock", Context.MODE_PRIVATE).getString("TEMP_PIN", null)));

                    if (!mMemo.getLabel().matches("")) { this.mEtTag.setText(mMemo.getLabel()); } // SET LABEL
                    if (!mMemo.getText().matches("")) { this.mEtMemo.setText(AESContent.decryptContent(mMemo.getText(), ENCDEC_KEY)); } // PULLED DECRYPTED VALUES (STRING)

                    ENCDEC_KEY = null;
                    System.gc();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        btnSave.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onSave();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onCancelClicked();
            }
        });
    }

    public void onSave()
    {
        MemoDatabaseAccess memoDatabaseAccess = MemoDatabaseAccess.getInstance(this);
        memoDatabaseAccess.open();

        String ENCDEC_KEY = (AESKeyHandler.DECRYPTKEY(this.getSharedPreferences("com.gerardogandeaga.cyberlock", Context.MODE_PRIVATE).getString("KEY", null),
                                                      this.getSharedPreferences("com.gerardogandeaga.cyberlock", Context.MODE_PRIVATE).getString("TEMP_PIN", null)));

        if ((!mEtTag.getText().toString().matches("")) || (!mEtMemo.getText().toString().matches("")))
        {
            if (mMemo == null) // WHEN SAVING A NEW UNKNOWN MEMO
            {
                // ADD NEW MEMO
                Memo temp = new Memo();

                temp.setText(AESContent.encryptContent(mEtMemo.getText().toString(), ENCDEC_KEY)); // SET TEXT
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
                mMemo.setText(AESContent.encryptContent(mEtMemo.getText().toString(), ENCDEC_KEY)); // SET TEXT
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
            onBackPressed();
        } else
        {
            Toast.makeText(this, "Nothing to save", Toast.LENGTH_SHORT).show();

            ENCDEC_KEY = null;
            System.gc(); // GARBAGE COLLECT TO TERMINATE -KEY- VARIABLE

            memoDatabaseAccess.close();
            onBackPressed();
        }
    }

    public void onCancelClicked()
    {
        onBackPressed();
    }

    // THIS IS THE START OF THE SCRIPT FOR *** THE "TO LOGIN FUNCTION" THIS DETECTS THE ON PRESSED, START, TABS AND HOME BUTTONS IN ORDER TO INITIALIZE SECURITY "FAIL-SAFE"
    @Override
    protected void onStart()
    {
        super.onStart();

        if (!APP_LOGGED_IN)
        {
//            onSave();

            ACTIVITY_INTENT = new Intent(this, LoginActivity.class);
            ACTIVITY_INTENT.putExtra("lastActivity", "MEMO_EDIT");
            ACTIVITY_INTENT.putExtra("lastDatabase", mMemo);
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
            if (ACTIVITY_INTENT == null) // NO PENDING ACTIVITIES ???(MAIN)--->(EDIT)???
            {
                new LogoutProtocol().logoutExecute(this);
            }
        }
    }

    @Override
    public void finish() // BACK BUTTON CACHES ACTIVITY ACTUAL START ---> MAIN ACTIVITY
    {
        super.finish();

        Intent i = new Intent(this, MainActivity.class);
        this.startActivity(i);
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
}
