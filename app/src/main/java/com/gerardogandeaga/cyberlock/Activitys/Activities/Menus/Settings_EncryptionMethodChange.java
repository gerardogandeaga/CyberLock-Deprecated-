package com.gerardogandeaga.cyberlock.Activitys.Activities.Menus;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Base64;
import android.widget.Toast;

import com.gerardogandeaga.cyberlock.Encryption.CryptContent;
import com.gerardogandeaga.cyberlock.Encryption.CryptKeyHandler;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.Database.Data;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.Database.MasterDatabaseAccess;

import java.util.List;

import static com.gerardogandeaga.cyberlock.Supports.Globals.CIPHER_ALGO;
import static com.gerardogandeaga.cyberlock.Supports.Globals.CRYPT_KEY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.DIRECTORY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.ENCRYPTION_ALGO;
import static com.gerardogandeaga.cyberlock.Supports.Globals.FLAGS;
import static com.gerardogandeaga.cyberlock.Supports.Globals.MASTER_KEY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.PIN;
import static com.gerardogandeaga.cyberlock.Supports.Globals.TEMP_PIN;

public class Settings_EncryptionMethodChange extends AsyncTask<Void, Void, Void>
{
    // DATA
    private SharedPreferences mSharedPreferences;

    private MasterDatabaseAccess mMasterDatabaseAccess;
    private List<Data> mDatas;

    private String ALGO;
    private String  CIPHER;
    private Context mContext;

    // WIDGETS
    private ProgressDialog mProgressDialog;

    public Settings_EncryptionMethodChange(Context context, String algorithm)
    {
        mContext = context;

        mSharedPreferences = mContext.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE);

        this.mMasterDatabaseAccess = MasterDatabaseAccess.getInstance(mContext);

        ALGO = algorithm;
        CIPHER = algorithm + "/CBC/PKCS5Padding";
    }

    private void progressBar()
    {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setTitle("Encryption Method...");
        mProgressDialog.setMessage("Changing Encryption Method...");
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
            final CryptContent cryptContent = new CryptContent(mContext);
            String decryptedPulledPin = new CryptKeyHandler(mContext).DECRYPTKEY(mSharedPreferences.getString(PIN, null), TEMP_PIN);
            System.out.println(decryptedPulledPin);

            mSharedPreferences.edit().remove(CRYPT_KEY).apply();
            mSharedPreferences.edit().remove(ENCRYPTION_ALGO).apply();
            mSharedPreferences.edit().remove(CIPHER_ALGO).apply();

            mSharedPreferences.edit().putString(ENCRYPTION_ALGO, ALGO).apply();
            mSharedPreferences.edit().putString(CIPHER_ALGO, CIPHER).apply();

            final CryptKeyHandler cryptKeyHandler = new CryptKeyHandler(mContext);
            byte[] KEY_Byte = cryptKeyHandler.BYTE_KEY_GENERATE(); // GENERATE A NEW BYTE ARRAY AS A SYMMETRIC KEY
            byte[] ENC_DEC_KEY_ByteVal = cryptKeyHandler.KEY_GENERATE(KEY_Byte);

            String new_ENC_DEC_KEY = Base64.encodeToString(ENC_DEC_KEY_ByteVal, FLAGS);

            final CryptContent newCryptContent = new CryptContent(mContext);

            // GO THROUGH ALL DATABASES
            this.mMasterDatabaseAccess.open();
            this.mDatas = mMasterDatabaseAccess.getAllData();
            for (int i = 0; i < mDatas.size(); i++)
            {
                final Data data = mDatas.get(i);
                String label = null;
                String content = null;

                label = cryptContent.decryptContent(data.getLabel(), MASTER_KEY);
                content = cryptContent.decryptContent(data.getContent(), MASTER_KEY);

                if (label != null) data.setLabel(newCryptContent.encryptContent(label, new_ENC_DEC_KEY));
                if (content != null) data.setContent(newCryptContent.encryptContent(content, new_ENC_DEC_KEY));

                mMasterDatabaseAccess.update(data);
                label = null;
                content = null;
                System.out.println("done memo");
            }
            this.mMasterDatabaseAccess.close();
            MASTER_KEY = new_ENC_DEC_KEY;
            mSharedPreferences.edit().putString(PIN, cryptKeyHandler.ENCRYPTKEY(decryptedPulledPin, TEMP_PIN)).apply();
            System.out.println(mSharedPreferences.getString(PIN, null));
            mSharedPreferences.edit().putString(CRYPT_KEY, cryptKeyHandler.ENCRYPTKEY(MASTER_KEY, TEMP_PIN)).apply();

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
}
