package com.gerardogandeaga.cyberlock.Supports;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import com.gerardogandeaga.cyberlock.Crypto.CryptContent;
import com.gerardogandeaga.cyberlock.Crypto.CryptKeyHandler;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.ContentDatabase.Data;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.ContentDatabase.MasterDatabaseAccess;

import java.util.List;

import static com.gerardogandeaga.cyberlock.Supports.Globals.CRYPT_KEY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.DIRECTORY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.MASTER_KEY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.TEMP_PIN;

public class Settings_ScrambleKey extends AsyncTask<Void, Void, Void>
{
    // DATA
    private SharedPreferences mSharedPreferences;

    private MasterDatabaseAccess mMasterDatabaseAccess;
    private List<Data> mDatas;
    
    private Context mContext;

    // WIDGETS
    private ProgressDialog mProgressDialog;

    public Settings_ScrambleKey(Context context)
    {
        mContext = context;

        mSharedPreferences = mContext.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE);

        this.mMasterDatabaseAccess = MasterDatabaseAccess.getInstance(mContext);
    }

    private void progressBar()
    {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setTitle("Scrambling Key...");
        mProgressDialog.setMessage("Loading Data...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    // ASYNC TASKS
    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

        progressBar();

        System.out.println("Scramble Key: onPreExecute");
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        try
        {
            CryptKeyHandler keyHandler = new CryptKeyHandler(mContext);
            CryptContent CRYPTCONTENT = new CryptContent(mContext);

            keyHandler.GENERATE_NEW_KEY(TEMP_PIN); // GENERATE A NEW BYTE ARRAY AS A SYMMETRIC KEY

            String newKeyStringVal = keyHandler.DECRYPT_KEY(mSharedPreferences.getString(CRYPT_KEY, null), TEMP_PIN);

            // GO THROUGH ALL DATABASES
            this.mMasterDatabaseAccess.open();
            this.mDatas = mMasterDatabaseAccess.getAllData();
            for (int i = 0; i < mDatas.size(); i++)
            {
                final Data data = mDatas.get(i);
                String label = null;
                String content = null;

                label = CRYPTCONTENT.DECRYPT_CONTENT(data.getLabel(), MASTER_KEY);
                content = CRYPTCONTENT.DECRYPT_CONTENT(data.getContent(), MASTER_KEY);

                if (label != null) data.setLabel(CRYPTCONTENT.ENCRYPT_KEY(label, newKeyStringVal));
                if (content != null) data.setContent(CRYPTCONTENT.ENCRYPT_KEY(content, newKeyStringVal));

                mMasterDatabaseAccess.update(data);
                label = null;
                content = null;
                System.out.println("done memo");
            }
            this.mMasterDatabaseAccess.close();
            MASTER_KEY = newKeyStringVal;

            new Handler(mContext.getMainLooper()).post(new Runnable()
            {
                @Override
                public void run()
                {
                    Toast.makeText(mContext, "Key Scrambled Successfully", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e)
        {
            e.printStackTrace();

            System.out.println("Something Went Wrong...");

            new Handler(mContext.getMainLooper()).post(new Runnable()
            {
                @Override
                public void run()
                {
                    Toast.makeText(mContext, "Something Went Wrong...", Toast.LENGTH_SHORT).show();
                }
            });
        }

        System.out.println("Scramble Key: doInBackground");
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid)
    {
        super.onPostExecute(aVoid);

        mProgressDialog.dismiss();

        System.out.println("Scramble Key: onPostExecute");
    }
    // -----------
}