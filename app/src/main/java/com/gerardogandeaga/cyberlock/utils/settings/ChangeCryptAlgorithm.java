package com.gerardogandeaga.cyberlock.utils.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import com.gerardogandeaga.cyberlock.crypto.key.CryptKey;
import com.gerardogandeaga.cyberlock.crypto.database.DBCrypt;
import com.gerardogandeaga.cyberlock.database.DBAccess;
import com.gerardogandeaga.cyberlock.database.DataPackage;
import com.gerardogandeaga.cyberlock.activities.dialogs.DialogCustomLoad;

import java.util.List;

import static com.gerardogandeaga.cyberlock.utils.Settings.CIPHER_ALGORITHM;
import static com.gerardogandeaga.cyberlock.utils.Settings.CRYPT_KEY;
import static com.gerardogandeaga.cyberlock.utils.Settings.DIRECTORY;
import static com.gerardogandeaga.cyberlock.utils.Settings.ENCRYPTION_ALGORITHM;
import static com.gerardogandeaga.cyberlock.utils.Settings.TMP_PWD;

public class ChangeCryptAlgorithm extends AsyncTask<Void, Void, Void> {
    @SuppressLint("StaticFieldLeak")
    private Context mContext;
    private DBCrypt mDBCrypt;
    private SharedPreferences mSharedPreferences;

    private List<DataPackage> mRawDatumPackages;

    private String ALGO;
    private String CIPHER;

    // widgets
    private DialogCustomLoad mDialogCustomLoad;

    public ChangeCryptAlgorithm(Context context, String algorithm) {
        this.mContext = context;
        this.mSharedPreferences = mContext.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE);

        this.ALGO = algorithm;
        this.CIPHER = algorithm + "/CBC/PKCS5Padding";
    }

    // ASYNC TASKS
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.mDialogCustomLoad = new DialogCustomLoad(mContext);
    }

    @Override
    protected Void doInBackground(Void... params) {
        saveNewAlgorithm();
        String newKey = CryptKey.generateNewMasterEncryptionKey(mContext, TMP_PWD); // GENERATE A NEW BYTE ARRAY AS A SYMMETRIC KEY

        // GO THROUGH ALL DATABASES
        DBAccess DBAccess = com.gerardogandeaga.cyberlock.database.DBAccess.getInstance(mContext);
        DBAccess.open();
        DBAccess.close();

        new Handler(mContext.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "Encryption Algorithm Changed Successfully", Toast.LENGTH_SHORT).show();
                }
            });

        return null;
    }

    private void saveNewKey(String newKey) {
        SharedPreferences sharedPreferences = this.mContext.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putString(CRYPT_KEY, newKey)
                .apply();
    }

    private void saveNewAlgorithm() {
        this.mSharedPreferences.edit().putString(ENCRYPTION_ALGORITHM, this.ALGO).apply();
        this.mSharedPreferences.edit().putString(CIPHER_ALGORITHM, this.CIPHER).apply();
    }
}