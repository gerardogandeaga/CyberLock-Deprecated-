package com.gerardogandeaga.cyberlock.Supports;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import com.gerardogandeaga.cyberlock.Crypto.CryptKeyHandler;
import com.gerardogandeaga.cyberlock.Crypto.CryptoContent;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.ContentDatabase.Data;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.ContentDatabase.MasterDatabaseAccess;

import java.util.List;

import static com.gerardogandeaga.cyberlock.Supports.Globals.CRYPT_KEY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.DIRECTORY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.MASTER_KEY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.TEMP_PIN;

public class Settings_ScrambleKey extends AsyncTask<Void, Void, Void>
{
    private Context mContext;
    private CryptoContent mCryptoContent;
    private SharedPreferences mSharedPreferences;

    // DATA VARIABLES
    private MasterDatabaseAccess mMasterDatabaseAccess;
    private List<Data> mDatas;


    // WIDGETS
    private ProgressDialog mProgressDialog;

    public Settings_ScrambleKey(Context context)
    {
        mContext = context;
        mCryptoContent = new CryptoContent(mContext);
        mSharedPreferences = mContext.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE);
        this.mMasterDatabaseAccess = MasterDatabaseAccess.getInstance(mContext);
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
            CryptoContent CRYPTCONTENT = new CryptoContent(mContext);

            keyHandler.GENERATE_NEW_KEY(TEMP_PIN); // GENERATE A NEW BYTE ARRAY AS A SYMMETRIC KEY

            String newKeyStringVal = keyHandler.DECRYPT_KEY(mSharedPreferences.getString(CRYPT_KEY, null), TEMP_PIN);

            // GO THROUGH ALL DATABASES
            this.mMasterDatabaseAccess.open();
            this.mDatas = mMasterDatabaseAccess.getAllData();
            for (int i = 0; i < mDatas.size(); i++)
            {
                final Data data = mDatas.get(i);
                String type = data.getType(mCryptoContent);
                String colourTag = data.getColourTag();
                String label = data.getLabel(mCryptoContent);
                String content = data.getContent(mCryptoContent);


                data.setType(mCryptoContent, type, newKeyStringVal);
                data.setColourTag(mCryptoContent, colourTag, newKeyStringVal);
                data.setLabel(mCryptoContent, label, newKeyStringVal);
                data.setContent(mCryptoContent, content, newKeyStringVal);

                mMasterDatabaseAccess.update(data);
                type = null;
                colourTag = null;
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
    private void progressBar()
    {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setTitle("Scrambling Key...");
        mProgressDialog.setMessage("Loading Data...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }
}