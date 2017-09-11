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

import java.util.NoSuchElementException;
import java.util.Scanner;

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
    private CryptContent mCryptContent;
    private Data mData;
    // WIDGETS
    private TextView mTvDate;
    private EditText
            mEtLabel,
            mEtUrl,
            mEtUsername,
            mEtEmail,
            mEtPassword,
            mEtNotes;
//    private static final int PICK_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_logininfo);
        ACTIVITY_INTENT = null;

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }

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
        MasterDatabaseAccess masterDatabaseAccess = MasterDatabaseAccess.getInstance(this);
        masterDatabaseAccess.open();

        if ((!mEtLabel.getText().toString().matches("")) || (!mEtUrl.getText().toString().matches("")) || (!mEtUsername.getText().toString().matches("")) || (!mEtEmail.getText().toString().matches("")) || (!mEtPassword.getText().toString().matches("")) || (!mEtNotes.getText().toString().matches("")))
        {
            final String url = mEtUrl.getText().toString();
            final String username = mEtUsername.getText().toString();
            final String email = mEtEmail.getText().toString();
            final String password = mEtPassword.getText().toString();
            final String notes = mEtNotes.getText().toString();

            final String format = "%s\n%s\n%s\n%s\n%s";
            final String tmpString = String.format(format,
                    url, username, email, password, notes);

            if (mData == null)
            {

                Data tmp = new Data();

                tmp.setType("TYPE_LOGININFO");
                tmp.setLabel(mCryptContent.encryptContent(mEtLabel.getText().toString(), MASTER_KEY));
                tmp.setContent(mCryptContent.encryptContent(tmpString, MASTER_KEY));

                masterDatabaseAccess.save(tmp);
            } else {

                mData.setLabel(mCryptContent.encryptContent(mEtLabel.getText().toString(), MASTER_KEY));
                mData.setContent(mCryptContent.encryptContent(tmpString, MASTER_KEY));

                masterDatabaseAccess.update(mData);
            }

            masterDatabaseAccess.close();
        } else {

            masterDatabaseAccess.close();
            Toast.makeText(this, "Nothing to save", Toast.LENGTH_SHORT).show();
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

                    String url;
                    String username;
                    String email;
                    String password;
                    String notes;

                    final String content = mCryptContent.decryptContent(mData.getContent(), MASTER_KEY);
                    if (content != null)
                    {
                        Scanner scanner = new Scanner(content);

                        url = scanner.nextLine();
                        username = scanner.nextLine();
                        email = scanner.nextLine();
                        password = scanner.nextLine();
                        try
                        {
                            notes = scanner.nextLine();
                            while (scanner.hasNextLine())
                            {
                                notes += "\n";
                                notes += scanner.hasNextLine();
                            }
                            mEtNotes.setText(notes);
                        } catch (NoSuchElementException e) {
                            e.printStackTrace();
                        }
                        scanner.close();

                        mEtUrl.setText(url);
                        mEtUsername.setText(username);
                        mEtEmail.setText(email);
                        mEtPassword.setText(password);
                    }

                    if (!mData.getDate().matches("")) {
                        this.mTvDate.setText("Last Updated: " + mData.getDate());
                    } else {
                        this.mTvDate.setText("Last Updated: ---");
                    }

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
                if (this.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getBoolean(AUTOSAVE, false)) { onSave();MASTER_KEY = null; TEMP_PIN = null; }

                ACTIVITY_INTENT = new Intent(this, LoginActivity.class);
                ACTIVITY_INTENT.putExtra("lastActivity", "LOGININFO_EDIT");
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