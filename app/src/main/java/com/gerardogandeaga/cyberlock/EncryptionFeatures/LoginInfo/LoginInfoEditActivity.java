package com.gerardogandeaga.cyberlock.EncryptionFeatures.LoginInfo;

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
import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol;
import com.gerardogandeaga.cyberlock.Encryption.CryptContent;
import com.gerardogandeaga.cyberlock.R;

import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.APP_LOGGED_IN;
import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.mCountDownIsFinished;
import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.mCountDownTimer;
import static com.gerardogandeaga.cyberlock.Supports.Globals.AUTOSAVE;
import static com.gerardogandeaga.cyberlock.Supports.Globals.DIRECTORY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.MASTER_KEY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.TEMP_PIN;

public class LoginInfoEditActivity extends AppCompatActivity
{
    // DATA
    private CryptContent mContent;
    private LoginInfo mLoginInfo;
    // WIDGETS
    private EditText mEtLabel, mEtUrl, mEtUsername, mEtEmail, mEtPassword, mEtNotes;
    private TextView mTvDate;

//    private static final int PICK_IMAGE = 100;

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

        this.mEtLabel = (EditText) findViewById(R.id.etTag);
        this.mEtUrl = (EditText) findViewById(R.id.etUrl);
        this.mEtUsername = (EditText) findViewById(R.id.etUsername);
        this.mEtEmail = (EditText) findViewById(R.id.etEmail);
        this.mEtPassword = (EditText) findViewById(R.id.etPassword);
        this.mEtNotes = (EditText) findViewById(R.id.etNotes);
        this.mTvDate = (TextView) findViewById(R.id.tvLastUpdated);

        Bundle bundle = getIntent().getExtras();
        setupActivity(bundle);

//        btnUploadImage.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                openGallery();
//            }
//        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        switch (id)
        {
            case (R.id.action_save):
                Toast.makeText(this, "Encrypting...", Toast.LENGTH_SHORT).show();
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

    // TODO IMAGE FEATURE
    // IMAGE PROCESSING TO VISUALS IN THE MAIN ACTIVITY LOGIN //
//    private byte[] saveImageToDataBase()
//    {
//        Bitmap bitmap = ((BitmapDrawable) mImgImage.getDrawable()).getBitmap();
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.WEBP, 50, outputStream);
//
//        return outputStream.toByteArray();
//    }
//
//    private void openGallery()
//    {
//        ACTIVITY_INTENT = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
//        startActivityForResult(ACTIVITY_INTENT, PICK_IMAGE);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data)
//    {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE)
//        {
//            Uri imageUri = data.getData();
//
//            try
//            {
//                InputStream inputStream = getContentResolver().openInputStream(imageUri);
//                mImgImage.setImageDrawable(scaleImage(inputStream));
//
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//                Toast.makeText(this, "File not found", Toast.LENGTH_LONG).show();
//            } catch (OutOfMemoryError e)
//            {
//                e.printStackTrace();
//                Toast.makeText(this, "Image way too big!", Toast.LENGTH_LONG).show();
//            }
//        }
//    }
//
//    private Drawable scaleImage(InputStream inputStream)
//    {
//        Bitmap bitmap = new BitmapDrawable(inputStream).getBitmap();
//        int num = (int) (bitmap.getHeight() * (1024.0 / bitmap.getWidth()));
//        Bitmap bitmapScaled = Bitmap.createScaledBitmap(bitmap, 1024, num, true);
//
//        return new BitmapDrawable(bitmapScaled);
//    }
    // ------------------------------------------------------ // /

    private void onSave()
    {
        LoginInfoDatabaseAccess loginInfoDatabaseAccess = LoginInfoDatabaseAccess.getInstance(this);
        loginInfoDatabaseAccess.open();

        if ((!mEtLabel.getText().toString().matches("")) || (!mEtUrl.getText().toString().matches("")) || (!mEtUsername.getText().toString().matches("")) || (!mEtEmail.getText().toString().matches("")) || (!mEtPassword.getText().toString().matches("")) || (!mEtNotes.getText().toString().matches("")))
        {
            if (mLoginInfo == null) // WHEN SAVING A NEW UNKNOWN LOGIN INFO
            {
                // ADD NEW LOGIN INFO
                LoginInfo temp = new LoginInfo();

                // SET INFO
                temp.setUrl(mContent.encryptContent(mEtUrl.getText().toString(), MASTER_KEY)); // SET URL
                temp.setUsername(mContent.encryptContent(mEtUsername.getText().toString(), MASTER_KEY)); // SET USERNAME
                temp.setEmail(mContent.encryptContent(mEtEmail.getText().toString(), MASTER_KEY)); // SET EMAIL
                temp.setPassword(mContent.encryptContent(mEtPassword.getText().toString(), MASTER_KEY)); // SET PASSWORD
                temp.setNotes(mContent.encryptContent(mEtNotes.getText().toString(), MASTER_KEY)); // SET NOTES
                if (!mEtLabel.getText().toString().matches(""))
                {
                    temp.setLabel(mEtLabel.getText().toString());
                } else
                {
                    temp.setLabel("");
                } // SET TAG
//                temp.setImage(saveImageToDataBase()); // SET IMAGE

                // SAVE NEW DATA TABLE
                loginInfoDatabaseAccess.save(temp);
            } else
            {
                // UPDATE THE LOGIN INFO
                // SET INFO
                mLoginInfo.setUrl(mContent.encryptContent(mEtUrl.getText().toString(), MASTER_KEY)); // SET URL
                mLoginInfo.setUsername(mContent.encryptContent(mEtUsername.getText().toString(), MASTER_KEY)); // SET USERNAME
                mLoginInfo.setEmail(mContent.encryptContent(mEtEmail.getText().toString(), MASTER_KEY)); // SET EMAIL
                mLoginInfo.setPassword(mContent.encryptContent(mEtPassword.getText().toString(), MASTER_KEY)); // SET PASSWORD
                mLoginInfo.setNotes(mContent.encryptContent(mEtNotes.getText().toString(), MASTER_KEY)); // SET NOTES
                if (!mEtLabel.getText().toString().matches(""))
                {
                    mLoginInfo.setLabel(mEtLabel.getText().toString());
                } else
                {
                    mLoginInfo.setLabel("");
                } // SET TAG
//                mLoginInfo.setImage(saveImageToDataBase()); // SET IMAGE

                // UPDATE DATA TABLE
                loginInfoDatabaseAccess.update(mLoginInfo);
            }

            loginInfoDatabaseAccess.close();
        } else
        {
            Toast.makeText(this, "Nothing to save", Toast.LENGTH_SHORT).show();

            loginInfoDatabaseAccess.close();
        }
    }

    private void onCancel()
    {
        ACTIVITY_INTENT = new Intent(this, MainLoginInfoActivity.class);
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
        mContent = new CryptContent(this);
        if (bundle != null)
        {
            mLoginInfo = (LoginInfo) bundle.get("LOGININFO");
            if (mLoginInfo != null)
            {
                try
                {
                    String label = mContent.decryptContent(mLoginInfo.getLabel(), MASTER_KEY);
                    String url = mContent.decryptContent(mLoginInfo.getUrl(), MASTER_KEY);
                    String email = mContent.decryptContent(mLoginInfo.getEmail(), MASTER_KEY);
                    String password = mContent.decryptContent(mLoginInfo.getPassword(), MASTER_KEY);
                    String notes = mContent.decryptContent(mLoginInfo.getNotes(), MASTER_KEY);

                    // DECRYPT CONTENT
                    if (label != null) mEtLabel.setText(label);
                    if (url != null) mEtUrl.setText(url);
                    if (email != null) mEtEmail.setText(email);
                    if (password != null) mEtPassword.setText(password);
                    if (notes != null) mEtLabel.setText(notes);

                    if (!mLoginInfo.getDate().matches("")) {
                        this.mTvDate.setText("Last Updated: " + mLoginInfo.getDate());
                    } else {
                        this.mTvDate.setText("Last Updated: ---");
                    }

                    System.gc();
                } catch (Exception e)
                {
                    e.printStackTrace();
                    Toast.makeText(this, "Error: could not set one or more text fields", Toast.LENGTH_SHORT).show();
                }
            }
        }
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