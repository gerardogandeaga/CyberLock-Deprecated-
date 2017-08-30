package com.gerardogandeaga.cyberlock.EncryptionFeatures.LoginInfo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LoginActivity;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol;
import com.gerardogandeaga.cyberlock.Encryption.CryptContent;
import com.gerardogandeaga.cyberlock.Encryption.CryptKeyHandler;
import com.gerardogandeaga.cyberlock.R;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.APP_LOGGED_IN;
import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.mCountDownIsFinished;
import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.mCountDownTimer;
import static com.gerardogandeaga.cyberlock.Supports.Globals.AUTOSAVE;
import static com.gerardogandeaga.cyberlock.Supports.Globals.CRYPT_KEY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.DIRECTORY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.TEMP_PIN;

public class LoginInfoEditActivity extends AppCompatActivity
{
    // DATA
    private LoginInfo mLoginInfo;
    // WIDGETS
    private EditText mEtTag, mEtUrl, mEtUsername, mEtEmail, mEtPassword, mEtNotes, mEtQuestion1, mEtQuestion2, mEtAnswer1, mEtAnswer2;
    private TextView mTvDate;
    private ImageView mImgImage;

    private static final int PICK_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_logininfo);
        ACTIVITY_INTENT = null;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Login Edit");

        this.mEtTag = (EditText) findViewById(R.id.etTag);
        this.mEtUrl = (EditText) findViewById(R.id.etUrl);
        this.mEtUsername = (EditText) findViewById(R.id.etUsername);
        this.mEtEmail = (EditText) findViewById(R.id.etEmail);
        this.mEtPassword = (EditText) findViewById(R.id.etPassword);
        this.mEtQuestion1 = (EditText) findViewById(R.id.etQuestion1);
        this.mEtQuestion2 = (EditText) findViewById(R.id.etQuestion2);
        this.mEtAnswer1 = (EditText) findViewById(R.id.etAnswer1);
        this.mEtAnswer2 = (EditText) findViewById(R.id.etAnswer2);
        this.mEtNotes = (EditText) findViewById(R.id.etNotes);
        this.mTvDate = (TextView) findViewById(R.id.tvLastUpdated);
        this.mImgImage = (ImageView) findViewById(R.id.imgImage);

        Button btnUploadImage = (Button) findViewById(R.id.btnUploadImage);

        Bundle bundle = getIntent().getExtras();
        onInstantCreate(bundle);

        btnUploadImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openGallery();
            }
        });
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
            mLoginInfo = (LoginInfo) bundle.get("LOGININFO");
            if (mLoginInfo != null)
            {
                try
                {
                    String ENCDEC_KEY = (new CryptKeyHandler(this).DECRYPTKEY(this.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getString(CRYPT_KEY, null),
                            this.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getString(TEMP_PIN, null)));

                    CryptContent content = new CryptContent(this);
                    // DECRYPT CONTENT
                    if (!mLoginInfo.getUrl().matches("")) { this.mEtUrl.setText(content.decryptContent(mLoginInfo.getUrl(), ENCDEC_KEY)); }
                    if (!mLoginInfo.getUsername().matches("")) { this.mEtUsername.setText(content.decryptContent(mLoginInfo.getUsername(), ENCDEC_KEY)); }
                    if (!mLoginInfo.getEmail().matches("")) { this.mEtEmail.setText(content.decryptContent(mLoginInfo.getEmail(), ENCDEC_KEY));}
                    if (!mLoginInfo.getPassword().matches("")) { this.mEtPassword.setText(content.decryptContent(mLoginInfo.getPassword(), ENCDEC_KEY)); }
                    if (!mLoginInfo.getQuestion1().matches("")) { this.mEtQuestion1.setText(content.decryptContent(mLoginInfo.getQuestion1(), ENCDEC_KEY));}
                    if (!mLoginInfo.getQuestion2().matches("")) { this.mEtQuestion2.setText(content.decryptContent(mLoginInfo.getQuestion2(), ENCDEC_KEY));}
                    if (!mLoginInfo.getAnswer1().matches("")) { this.mEtAnswer1.setText(content.decryptContent(mLoginInfo.getAnswer1(), ENCDEC_KEY));}
                    if (!mLoginInfo.getAnswer2().matches("")) { this.mEtAnswer2.setText(content.decryptContent(mLoginInfo.getAnswer2(), ENCDEC_KEY));}
                    if (!mLoginInfo.getNotes().matches("")) { this.mEtNotes.setText(content.decryptContent(mLoginInfo.getNotes(), ENCDEC_KEY)); }

                    if (!mLoginInfo.getLabel().matches("")) { this.mEtTag.setText(mLoginInfo.getLabel()); }
                    if(mLoginInfo.getImage() != null) { this.mImgImage.setImageDrawable(mLoginInfo.setImageButton(mLoginInfo)); }
                    if (!mLoginInfo.getDate().matches("")) {
                        this.mTvDate.setText("Last Updated: " + mLoginInfo.getDate());
                    } else {
                        this.mTvDate.setText("Last Updated: ---");
                    }

                    ENCDEC_KEY = null;
                    System.gc();
                } catch (Exception e)
                {
                    e.printStackTrace();
                    Toast.makeText(this, "Error: could not set one or more text fields", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // IMAGE PROCESSING TO VISUALS IN THE MAIN ACTIVITY LOGIN //
    private byte[] saveImageToDataBase()
    {
        Bitmap bitmap = ((BitmapDrawable) mImgImage.getDrawable()).getBitmap();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.WEBP, 50, outputStream);

        return outputStream.toByteArray();
    }

    private void openGallery()
    {
        ACTIVITY_INTENT = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(ACTIVITY_INTENT, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE)
        {
            Uri imageUri = data.getData();

            try
            {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                mImgImage.setImageDrawable(scaleImage(inputStream));

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "File not found", Toast.LENGTH_LONG).show();
            } catch (OutOfMemoryError e)
            {
                e.printStackTrace();
                Toast.makeText(this, "Image way too big!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private Drawable scaleImage(InputStream inputStream)
    {
        Bitmap bitmap = new BitmapDrawable(inputStream).getBitmap();
        int num = (int) (bitmap.getHeight() * (1024.0 / bitmap.getWidth()));
        Bitmap bitmapScaled = Bitmap.createScaledBitmap(bitmap, 1024, num, true);

        return new BitmapDrawable(bitmapScaled);
    }
    // ------------------------------------------------------ //

    private void onSave()
    {
        LoginInfoDatabaseAccess loginInfoDatabaseAccess = LoginInfoDatabaseAccess.getInstance(this);
        loginInfoDatabaseAccess.open();

        String ENCDEC_KEY = (new CryptKeyHandler(this).DECRYPTKEY(this.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getString(CRYPT_KEY, null),
                this.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getString(TEMP_PIN, null)));

        if ((!mEtTag.getText().toString().matches("")) || (!mEtUrl.getText().toString().matches("")) || (!mEtUsername.getText().toString().matches("")) || (!mEtEmail.getText().toString().matches("")) || (!mEtPassword.getText().toString().matches("")) || (!mEtNotes.getText().toString().matches("")) || (!mEtQuestion1.getText().toString().matches("")) || (!mEtQuestion2.getText().toString().matches("")) || (!mEtAnswer1.getText().toString().matches("")) || (!mEtAnswer2.getText().toString().matches("")))
        {
            CryptContent content = new CryptContent(this);

            if (mLoginInfo == null) // WHEN SAVING A NEW UNKNOWN LOGIN INFO
            {
                // ADD NEW LOGIN INFO
                LoginInfo temp = new LoginInfo();

                // SET INFO
                temp.setUrl(content.encryptContent(mEtUrl.getText().toString(), ENCDEC_KEY)); // SET URL
                temp.setUsername(content.encryptContent(mEtUsername.getText().toString(), ENCDEC_KEY)); // SET USERNAME
                temp.setEmail(content.encryptContent(mEtEmail.getText().toString(), ENCDEC_KEY)); // SET EMAIL
                temp.setPassword(content.encryptContent(mEtPassword.getText().toString(), ENCDEC_KEY)); // SET PASSWORD
                temp.setQuestion1(content.encryptContent(mEtQuestion1.getText().toString(), ENCDEC_KEY)); // SET QUESTION
                temp.setQuestion2(content.encryptContent(mEtQuestion2.getText().toString(), ENCDEC_KEY)); // SET QUESTION
                temp.setAnswer1(content.encryptContent(mEtAnswer1.getText().toString(), ENCDEC_KEY)); // SET ANSWER
                temp.setAnswer2(content.encryptContent(mEtAnswer2.getText().toString(), ENCDEC_KEY)); // SET ANSWER
                temp.setNotes(content.encryptContent(mEtNotes.getText().toString(), ENCDEC_KEY)); // SET NOTES
                if (!mEtTag.getText().toString().matches(""))
                {
                    temp.setLabel(mEtTag.getText().toString());
                } else
                {
                    temp.setLabel("");
                } // SET TAG
                temp.setImage(saveImageToDataBase()); // SET IMAGE

                // SAVE NEW DATA TABLE
                loginInfoDatabaseAccess.save(temp);
            } else
            {
                // UPDATE THE LOGIN INFO
                // SET INFO
                mLoginInfo.setUrl(content.encryptContent(mEtUrl.getText().toString(), ENCDEC_KEY)); // SET URL
                mLoginInfo.setUsername(content.encryptContent(mEtUsername.getText().toString(), ENCDEC_KEY)); // SET USERNAME
                mLoginInfo.setEmail(content.encryptContent(mEtEmail.getText().toString(), ENCDEC_KEY)); // SET EMAIL
                mLoginInfo.setPassword(content.encryptContent(mEtPassword.getText().toString(), ENCDEC_KEY)); // SET PASSWORD
                mLoginInfo.setQuestion1(content.encryptContent(mEtQuestion1.getText().toString(), ENCDEC_KEY)); // SET QUESTION
                mLoginInfo.setQuestion2(content.encryptContent(mEtQuestion2.getText().toString(), ENCDEC_KEY)); // SET QUESTION
                mLoginInfo.setAnswer1(content.encryptContent(mEtAnswer1.getText().toString(), ENCDEC_KEY)); // SET ANSWER
                mLoginInfo.setAnswer2(content.encryptContent(mEtAnswer2.getText().toString(), ENCDEC_KEY)); // SET ANSWER
                mLoginInfo.setNotes(content.encryptContent(mEtNotes.getText().toString(), ENCDEC_KEY)); // SET NOTES
                if (!mEtTag.getText().toString().matches(""))
                {
                    mLoginInfo.setLabel(mEtTag.getText().toString());
                } else
                {
                    mLoginInfo.setLabel("");
                } // SET TAG
                mLoginInfo.setImage(saveImageToDataBase()); // SET IMAGE

                // UPDATE DATA TABLE
                loginInfoDatabaseAccess.update(mLoginInfo);
            }

            ENCDEC_KEY = null;
            System.gc(); // GARBAGE COLLECT TO TERMINATE -KEY- VARIABLE

            loginInfoDatabaseAccess.close();
        } else
        {
            Toast.makeText(this, "Nothing to save", Toast.LENGTH_SHORT).show();

            ENCDEC_KEY = null;
            System.gc(); // GARBAGE COLLECT TO TERMINATE -KEY- VARIABLE

            loginInfoDatabaseAccess.close();
        }
    }

    public void onCancel()
    {
        onBackPressed();
    }

    // THIS IS THE START OF THE SCRIPT FOR *** THE "TO LOGIN FUNCTION" THIS DETECTS THE ON PRESSED, START, TABS AND HOME BUTTONS IN ORDER TO INITIALIZE SECURITY "FAIL-SAFE" //
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
                    ACTIVITY_INTENT.putExtra("lastActivity", "LOGININFO_EDIT");
                    ACTIVITY_INTENT.putExtra("lastDatabase", mLoginInfo);
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

            ACTIVITY_INTENT = new Intent(this, MainLoginInfoActivity.class);
            finish();
            this.startActivity(ACTIVITY_INTENT);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();

        if (!this.isFinishing())
        { // HOME AND TABS AND SCREEN OFF
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
    // --------------------------------------------------------------------------------------------------------------------------------------------------------------------- //
}