package com.gerardogandeaga.cyberlock.Supports;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import com.gerardogandeaga.cyberlock.Crypto.CryptoContent;
import com.gerardogandeaga.cyberlock.Crypto.CryptKeyHandler;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.ContentDatabase.Data;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.ContentDatabase.MasterDatabaseAccess;

import java.util.List;

import static com.gerardogandeaga.cyberlock.Supports.Globals.CIPHER_ALGO;
import static com.gerardogandeaga.cyberlock.Supports.Globals.CRYPT_KEY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.DIRECTORY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.ENCRYPTION_ALGO;
import static com.gerardogandeaga.cyberlock.Supports.Globals.MASTER_KEY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.PASSCODE;
import static com.gerardogandeaga.cyberlock.Supports.Globals.TEMP_PIN;

public class Settings_EncryptionMethodChange extends AsyncTask<Void, Void, Void>
{
    private Context mContext;
    private CryptoContent mCryptoContent;
    private SharedPreferences mSharedPreferences;

    // DATA VARIABLES
    private MasterDatabaseAccess mMasterDatabaseAccess;
    private List<Data> mDatas;

    private String ALGO;
    private String  CIPHER;

    // WIDGETS
    private ProgressDialog mProgressDialog;

    public Settings_EncryptionMethodChange(Context context, String algorithm)
    {
        mContext = context;
        mCryptoContent = new CryptoContent(mContext);
        mSharedPreferences = mContext.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE);
        mMasterDatabaseAccess = MasterDatabaseAccess.getInstance(mContext);

        ALGO = algorithm;
        CIPHER = algorithm + "/CBC/PKCS5Padding";
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
            final CryptoContent CRYPTCONTENT = new CryptoContent(mContext);
            String decryptedPulledPin = new CryptKeyHandler(mContext).DECRYPT_KEY(mSharedPreferences.getString(PASSCODE, null), TEMP_PIN);
            System.out.println(decryptedPulledPin);

            mSharedPreferences.edit().remove(CRYPT_KEY).apply();
            mSharedPreferences.edit().remove(ENCRYPTION_ALGO).apply();
            mSharedPreferences.edit().remove(CIPHER_ALGO).apply();

            mSharedPreferences.edit().putString(ENCRYPTION_ALGO, ALGO).apply();
            mSharedPreferences.edit().putString(CIPHER_ALGO, CIPHER).apply();

            final CryptKeyHandler cryptKeyHandler = new CryptKeyHandler(mContext);
            cryptKeyHandler.GENERATE_NEW_KEY(TEMP_PIN); // GENERATE A NEW BYTE ARRAY AS A SYMMETRIC KEY

            String newKeyStringVal = cryptKeyHandler.DECRYPT_KEY(mSharedPreferences.getString(CRYPT_KEY, null), TEMP_PIN);

            final CryptoContent newCRYPTCONTENT = new CryptoContent(mContext);

            // GO THROUGH ALL DATABASES
            this.mMasterDatabaseAccess.open();
            this.mDatas = mMasterDatabaseAccess.getAllData();
            for (int i = 0; i < mDatas.size(); i++)
            {
                final Data data = mDatas.get(i);
                String type = data.getType(mCryptoContent);
                String colourTag = data.getColourTag(mCryptoContent);
                String label = data.getLabel(mCryptoContent);
                String content = data.getContent(mCryptoContent);

                data.setType(newCRYPTCONTENT, type, newKeyStringVal);
                data.setColourTag(newCRYPTCONTENT, colourTag, newKeyStringVal);
                data.setLabel(newCRYPTCONTENT, label, newKeyStringVal);
                data.setContent(newCRYPTCONTENT, content, newKeyStringVal);

                mMasterDatabaseAccess.update(data);
                type = null;
                colourTag = null;
                label = null;
                content = null;
                System.out.println("done memo");
            }
            this.mMasterDatabaseAccess.close();
            MASTER_KEY = newKeyStringVal;
            mSharedPreferences.edit().putString(PASSCODE, cryptKeyHandler.ENCRYPT_KEY(decryptedPulledPin, TEMP_PIN)).apply();

            new Handler(mContext.getMainLooper()).post(new Runnable()
            {
                @Override
                public void run()
                {
                    Toast.makeText(mContext, "Encryption Algorithm Changed Successfully", Toast.LENGTH_SHORT).show();
                }
            });

            System.out.println("NEW ENCRYPTION ALGO: " + mSharedPreferences.getString(ENCRYPTION_ALGO, "AES") + "!!!!!!");

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

        System.out.println("Changing Encryption: doInBackground");
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
        mProgressDialog.setTitle("Encryption Method...");
        mProgressDialog.setMessage("Changing Encryption Method...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }
}