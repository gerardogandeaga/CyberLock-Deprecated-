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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LoginActivity;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Main.MainActivity;
import com.gerardogandeaga.cyberlock.Encryption.AESContent;
import com.gerardogandeaga.cyberlock.Encryption.AESKeyHandler;
import com.gerardogandeaga.cyberlock.R;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.APP_LOGGED_IN;

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

        Button btnSave = (Button) findViewById(R.id.btnSave);
        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        Button btnUploadImage = (Button) findViewById(R.id.btnUploadImage);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
        {
            mLoginInfo = (LoginInfo) bundle.get("LOGININFO");
            if (mLoginInfo != null)
            {
                try
                {
                    String ENCDEC_KEY = (AESKeyHandler.DECRYPTKEY(this.getSharedPreferences("com.gerardogandeaga.cyberlock", Context.MODE_PRIVATE).getString("KEY", null),
                            this.getSharedPreferences("com.gerardogandeaga.cyberlock", Context.MODE_PRIVATE).getString("TEMP_PIN", null)));

                    // DECRYPT CONTENT
                    if (!mLoginInfo.getUrl().matches("")) { this.mEtUrl.setText(AESContent.decryptContent(mLoginInfo.getUrl(), ENCDEC_KEY)); }
                    if (!mLoginInfo.getUsername().matches("")) { this.mEtUsername.setText(AESContent.decryptContent(mLoginInfo.getUsername(), ENCDEC_KEY)); }
                    if (!mLoginInfo.getEmail().matches("")) { this.mEtEmail.setText(AESContent.decryptContent(mLoginInfo.getEmail(), ENCDEC_KEY));}
                    if (!mLoginInfo.getPassword().matches("")) { this.mEtPassword.setText(AESContent.decryptContent(mLoginInfo.getPassword(), ENCDEC_KEY)); }
                    if (!mLoginInfo.getQuestion1().matches("")) { this.mEtQuestion1.setText(AESContent.decryptContent(mLoginInfo.getQuestion1(), ENCDEC_KEY));}
                    if (!mLoginInfo.getQuestion2().matches("")) { this.mEtQuestion2.setText(AESContent.decryptContent(mLoginInfo.getQuestion2(), ENCDEC_KEY));}
                    if (!mLoginInfo.getAnswer1().matches("")) { this.mEtAnswer1.setText(AESContent.decryptContent(mLoginInfo.getAnswer1(), ENCDEC_KEY));}
                    if (!mLoginInfo.getAnswer2().matches("")) { this.mEtAnswer2.setText(AESContent.decryptContent(mLoginInfo.getAnswer2(), ENCDEC_KEY));}
                    if (!mLoginInfo.getNotes().matches("")) { this.mEtNotes.setText(AESContent.decryptContent(mLoginInfo.getNotes(), ENCDEC_KEY)); }

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

        btnUploadImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openGallery();
            }
        });

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

    // IMAGE PROCESSING TO VISUALS IN THE MAIN ACTIVITY LOGIN //
    public byte[] saveImageToDataBase()
    {
        Bitmap bitmap = ((BitmapDrawable) mImgImage.getDrawable()).getBitmap();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.WEBP, 50, outputStream);

        return outputStream.toByteArray();
    }

    public void openGallery()
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

    public Drawable scaleImage(InputStream inputStream)
    {
        Bitmap bitmap = new BitmapDrawable(inputStream).getBitmap();
        int num = (int) (bitmap.getHeight() * (1024.0 / bitmap.getWidth()));
        Bitmap bitmapScaled = Bitmap.createScaledBitmap(bitmap, 1024, num, true);

        return new BitmapDrawable(bitmapScaled);
    }
    // ------------------------------------------------------ //

    public void onSave()
    {
        LoginInfoDatabaseAccess loginInfoDatabaseAccess = LoginInfoDatabaseAccess.getInstance(this);
        loginInfoDatabaseAccess.open();

        String ENCDEC_KEY = (AESKeyHandler.DECRYPTKEY(this.getSharedPreferences("com.gerardogandeaga.cyberlock", Context.MODE_PRIVATE).getString("KEY", null),
                this.getSharedPreferences("com.gerardogandeaga.cyberlock", Context.MODE_PRIVATE).getString("TEMP_PIN", null)));

        if ((!mEtTag.getText().toString().matches("")) || (!mEtUrl.getText().toString().matches("")) || (!mEtUsername.getText().toString().matches("")) || (!mEtEmail.getText().toString().matches("")) || (!mEtPassword.getText().toString().matches("")) || (!mEtNotes.getText().toString().matches("")) || (!mEtQuestion1.getText().toString().matches("")) || (!mEtQuestion2.getText().toString().matches("")) || (!mEtAnswer1.getText().toString().matches("")) || (!mEtAnswer2.getText().toString().matches("")))
        {
            if (mLoginInfo == null) // WHEN SAVING A NEW UNKNOWN LOGIN INFO
            {
                // ADD NEW LOGIN INFO
                LoginInfo temp = new LoginInfo();

                // SET INFO
                temp.setUrl(AESContent.encryptContent(mEtUrl.getText().toString(), ENCDEC_KEY)); // SET URL
                temp.setUsername(AESContent.encryptContent(mEtUsername.getText().toString(), ENCDEC_KEY)); // SET USERNAME
                temp.setEmail(AESContent.encryptContent(mEtEmail.getText().toString(), ENCDEC_KEY)); // SET EMAIL
                temp.setPassword(AESContent.encryptContent(mEtPassword.getText().toString(), ENCDEC_KEY)); // SET PASSWORD
                temp.setQuestion1(AESContent.encryptContent(mEtQuestion1.getText().toString(), ENCDEC_KEY)); // SET QUESTION
                temp.setQuestion2(AESContent.encryptContent(mEtQuestion2.getText().toString(), ENCDEC_KEY)); // SET QUESTION
                temp.setAnswer1(AESContent.encryptContent(mEtAnswer1.getText().toString(), ENCDEC_KEY)); // SET ANSWER
                temp.setAnswer2(AESContent.encryptContent(mEtAnswer2.getText().toString(), ENCDEC_KEY)); // SET ANSWER
                temp.setNotes(AESContent.encryptContent(mEtNotes.getText().toString(), ENCDEC_KEY)); // SET NOTES
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
                mLoginInfo.setUrl(AESContent.encryptContent(mEtUrl.getText().toString(), ENCDEC_KEY)); // SET URL
                mLoginInfo.setUsername(AESContent.encryptContent(mEtUsername.getText().toString(), ENCDEC_KEY)); // SET USERNAME
                mLoginInfo.setEmail(AESContent.encryptContent(mEtEmail.getText().toString(), ENCDEC_KEY)); // SET EMAIL
                mLoginInfo.setPassword(AESContent.encryptContent(mEtPassword.getText().toString(), ENCDEC_KEY)); // SET PASSWORD
                mLoginInfo.setQuestion1(AESContent.encryptContent(mEtQuestion1.getText().toString(), ENCDEC_KEY)); // SET QUESTION
                mLoginInfo.setQuestion2(AESContent.encryptContent(mEtQuestion2.getText().toString(), ENCDEC_KEY)); // SET QUESTION
                mLoginInfo.setAnswer1(AESContent.encryptContent(mEtAnswer1.getText().toString(), ENCDEC_KEY)); // SET ANSWER
                mLoginInfo.setAnswer2(AESContent.encryptContent(mEtAnswer2.getText().toString(), ENCDEC_KEY)); // SET ANSWER
                mLoginInfo.setNotes(AESContent.encryptContent(mEtNotes.getText().toString(), ENCDEC_KEY)); // SET NOTES
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
            onBackPressed();
        } else
        {
            Toast.makeText(this, "Nothing to save", Toast.LENGTH_SHORT).show();

            ENCDEC_KEY = null;
            System.gc(); // GARBAGE COLLECT TO TERMINATE -KEY- VARIABLE

            loginInfoDatabaseAccess.close();
            onBackPressed();
        }
    }

    public void onCancelClicked()
    {
        onBackPressed();
    }

    // THIS IS THE START OF THE SCRIPT FOR *** THE "TO LOGIN FUNCTION" THIS DETECTS THE ON PRESSED, START, TABS AND HOME BUTTONS IN ORDER TO INITIALIZE SECURITY "FAIL-SAFE" //
    @Override
    protected void onStart()
    {
        super.onStart();

        if (!APP_LOGGED_IN)
        {
            ACTIVITY_INTENT = new Intent(this, LoginActivity.class);
            ACTIVITY_INTENT.putExtra("lastActivity", "LOGININFO_EDIT");
            ACTIVITY_INTENT.putExtra("lastDatabase", mLoginInfo);
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
    // --------------------------------------------------------------------------------------------------------------------------------------------------------------------- //
}