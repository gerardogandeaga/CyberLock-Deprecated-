package com.gerardogandeaga.cyberlock.support.settings;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import com.gerardogandeaga.cyberlock.crypto.CryptKeyHandler;
import com.gerardogandeaga.cyberlock.crypto.CryptoContent;
import com.gerardogandeaga.cyberlock.sqlite.data.RawDataPackage;
import com.gerardogandeaga.cyberlock.sqlite.data.MasterDatabaseAccess;

import java.util.List;

import static com.gerardogandeaga.cyberlock.support.Globals.CRYPT_KEY;
import static com.gerardogandeaga.cyberlock.support.Globals.DIRECTORY;
import static com.gerardogandeaga.cyberlock.support.Globals.MASTER_KEY;
import static com.gerardogandeaga.cyberlock.support.Globals.TEMP_PIN;

public class SettingsScrambleCryptKey extends AsyncTask<Void, Void, Void> {
    private Context mContext;
    private CryptoContent mCryptoContent;
    private SharedPreferences mSharedPreferences;
    // DATA VARIABLES
    private MasterDatabaseAccess mMasterDatabaseAccess;
    private List<RawDataPackage> mRawDatumPackages;
    // WIDGETS
    private ProgressDialog mProgressDialog;

    public SettingsScrambleCryptKey(Context context) {
        mContext = context;
        mCryptoContent = new CryptoContent(mContext);
        mSharedPreferences = mContext.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE);
        this.mMasterDatabaseAccess = MasterDatabaseAccess.getInstance(mContext);
    }

    // ASYNC TASKS
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar();
    }
    @Override
    protected Void doInBackground(Void... params) {
        try {
            CryptKeyHandler keyHandler = new CryptKeyHandler(mContext);
            CryptoContent CRYPTCONTENT = new CryptoContent(mContext);

            keyHandler.GENERATE_NEW_KEY(TEMP_PIN); // GENERATE A NEW BYTE ARRAY AS A SYMMETRIC KEY

            String newKeyStringVal = keyHandler.DECRYPT_KEY(mSharedPreferences.getString(CRYPT_KEY, null), TEMP_PIN);

            // GO THROUGH ALL DATABASES
            this.mMasterDatabaseAccess.open();
            this.mRawDatumPackages = mMasterDatabaseAccess.getAllData();

            for (int i = 0; i < mRawDatumPackages.size(); i++) { // TODO try counting down
                final RawDataPackage rawDataPackage = mRawDatumPackages.get(i);
                String type = rawDataPackage.getType(mCryptoContent);
                String colourTag = rawDataPackage.getColourTag(mCryptoContent);
                String label = rawDataPackage.getLabel(mCryptoContent);
                String content = rawDataPackage.getContent(mCryptoContent);

                rawDataPackage.setType(mCryptoContent, type, newKeyStringVal);
                rawDataPackage.setColourTag(mCryptoContent, colourTag, newKeyStringVal);
                rawDataPackage.setLabel(mCryptoContent, label, newKeyStringVal);
                rawDataPackage.setContent(mCryptoContent, content, newKeyStringVal);

                mMasterDatabaseAccess.update(rawDataPackage);
            }

            this.mMasterDatabaseAccess.close();
            MASTER_KEY = newKeyStringVal;

            new Handler(mContext.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "Key Scrambled Successfully", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            new Handler(mContext.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "Something Went Wrong...", Toast.LENGTH_SHORT).show();
                }
            });
        }
        return null;
    }
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mProgressDialog.dismiss();
    }

    // -----------
    private void progressBar() {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setTitle("Scrambling Key...");
        mProgressDialog.setMessage("Loading RawDataPackage...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }
}