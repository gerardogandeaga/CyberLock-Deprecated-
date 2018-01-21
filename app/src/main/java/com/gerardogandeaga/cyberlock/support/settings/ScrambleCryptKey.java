package com.gerardogandeaga.cyberlock.support.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.gerardogandeaga.cyberlock.crypto.CryptKey;
import com.gerardogandeaga.cyberlock.sqlite.data.DBAccess;
import com.gerardogandeaga.cyberlock.support.graphics.CustomLoadDialog;

import static com.gerardogandeaga.cyberlock.support.Stored.CRYPT_KEY;
import static com.gerardogandeaga.cyberlock.support.Stored.DIRECTORY;
import static com.gerardogandeaga.cyberlock.support.Stored.TMP_PWD;

public class ScrambleCryptKey extends AsyncTask<Void, Void, Void> {
    @SuppressLint("StaticFieldLeak")
    private Context mContext;

    // widgets
    private CustomLoadDialog mCustomLoadDialog;

    public ScrambleCryptKey(Context context) {
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mCustomLoadDialog = new CustomLoadDialog(mContext);
        mCustomLoadDialog.indeterminateLoad("Scrambling...");
    }
    @Override
    protected Void doInBackground(Void... params) {
        final String newKey = CryptKey.generateNewMasterEncryptionKey(this.mContext, TMP_PWD);

        DBAccess DBAccess = com.gerardogandeaga.cyberlock.sqlite.data.DBAccess.getInstance(this.mContext);
        DBAccess.open();
        DBAccess.close();

        saveNewKey(newKey);

        mCustomLoadDialog.dismiss();

        return null;
    }

    private void saveNewKey(String newKey) {
        SharedPreferences sharedPreferences = this.mContext.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putString(CRYPT_KEY, newKey)
                .apply();
    }
}