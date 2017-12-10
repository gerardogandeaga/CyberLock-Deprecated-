package com.gerardogandeaga.cyberlock.support.settings;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import com.gerardogandeaga.cyberlock.crypto.CryptKeyHandler;
import com.gerardogandeaga.cyberlock.crypto.CryptoContent;
import com.gerardogandeaga.cyberlock.sqlite.data.RawData;
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
    private List<RawData> mRawData;
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
            this.mRawData = mMasterDatabaseAccess.getAllData();

            for (int i = 0; i < mRawData.size(); i++) {
                final RawData rawData = mRawData.get(i);
                String type = rawData.getType(mCryptoContent);
                String colourTag = rawData.getColourTag(mCryptoContent);
                String label = rawData.getLabel(mCryptoContent);
                String content = rawData.getContent(mCryptoContent);

                rawData.setType(mCryptoContent, type, newKeyStringVal);
                rawData.setColourTag(mCryptoContent, colourTag, newKeyStringVal);
                rawData.setLabel(mCryptoContent, label, newKeyStringVal);
                rawData.setContent(mCryptoContent, content, newKeyStringVal);

                mMasterDatabaseAccess.update(rawData);
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
        mProgressDialog.setMessage("Loading RawData...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }
}