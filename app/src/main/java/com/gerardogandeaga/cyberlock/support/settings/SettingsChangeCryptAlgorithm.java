package com.gerardogandeaga.cyberlock.support.settings;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import com.gerardogandeaga.cyberlock.crypto.CryptoContent;
import com.gerardogandeaga.cyberlock.crypto.CryptKeyHandler;
import com.gerardogandeaga.cyberlock.sqlite.data.RawDataPackage;
import com.gerardogandeaga.cyberlock.sqlite.data.MasterDatabaseAccess;

import java.util.List;

import static com.gerardogandeaga.cyberlock.support.Globals.CIPHER_ALGO;
import static com.gerardogandeaga.cyberlock.support.Globals.CRYPT_KEY;
import static com.gerardogandeaga.cyberlock.support.Globals.DIRECTORY;
import static com.gerardogandeaga.cyberlock.support.Globals.CRYPT_ALGO;
import static com.gerardogandeaga.cyberlock.support.Globals.MASTER_KEY;
import static com.gerardogandeaga.cyberlock.support.Globals.PASSCODE;
import static com.gerardogandeaga.cyberlock.support.Globals.TEMP_PIN;

public class SettingsChangeCryptAlgorithm extends AsyncTask<Void, Void, Void>
{
    private Context mContext;
    private CryptoContent mCryptoContent;
    private SharedPreferences mSharedPreferences;

    // DATA VARIABLES
    private MasterDatabaseAccess mMasterDatabaseAccess;
    private List<RawDataPackage> mRawDatumPackages;

    private String ALGO;
    private String  CIPHER;

    // WIDGETS
    private ProgressDialog mProgressDialog;

    public SettingsChangeCryptAlgorithm(Context context, String algorithm)
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
            final CryptoContent old_cc = new CryptoContent(mContext);
            String decryptedPulledPin = new CryptKeyHandler(mContext).DECRYPT_KEY(mSharedPreferences.getString(PASSCODE, null), TEMP_PIN);
            System.out.println(decryptedPulledPin);

            mSharedPreferences.edit().remove(CRYPT_KEY).apply();
            mSharedPreferences.edit().remove(CRYPT_ALGO).apply();
            mSharedPreferences.edit().remove(CIPHER_ALGO).apply();

            mSharedPreferences.edit().putString(CRYPT_ALGO, ALGO).apply();
            mSharedPreferences.edit().putString(CIPHER_ALGO, CIPHER).apply();

            final CryptKeyHandler cryptKeyHandler = new CryptKeyHandler(mContext);
            cryptKeyHandler.GENERATE_NEW_KEY(TEMP_PIN); // GENERATE A NEW BYTE ARRAY AS A SYMMETRIC KEY

            String newKeyStringVal = cryptKeyHandler.DECRYPT_KEY(mSharedPreferences.getString(CRYPT_KEY, null), TEMP_PIN);

            final CryptoContent new_cc = new CryptoContent(mContext);

            // GO THROUGH ALL DATABASES
            this.mMasterDatabaseAccess.open();
            this.mRawDatumPackages = mMasterDatabaseAccess.getAllData();

            for (int i = 0; i < mRawDatumPackages.size(); i++) {
                final RawDataPackage rawDataPackage = mRawDatumPackages.get(i);
                String type = rawDataPackage.getType(mCryptoContent);
                String colourTag = rawDataPackage.getColourTag(mCryptoContent);
                String label = rawDataPackage.getLabel(mCryptoContent);
                String content = rawDataPackage.getContent(mCryptoContent);

                rawDataPackage.setType(new_cc, type, newKeyStringVal);
                rawDataPackage.setColourTag(new_cc, colourTag, newKeyStringVal);
                rawDataPackage.setLabel(new_cc, label, newKeyStringVal);
                rawDataPackage.setContent(new_cc, content, newKeyStringVal);

                mMasterDatabaseAccess.update(rawDataPackage);
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

            System.out.println("NEW ENCRYPTION ALGO: " + mSharedPreferences.getString(CRYPT_ALGO, "AES") + "!!!!!!");

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