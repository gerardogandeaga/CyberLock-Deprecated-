package com.gerardogandeaga.cyberlock.utils.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.gerardogandeaga.cyberlock.crypto.key.CryptKey;
import com.gerardogandeaga.cyberlock.database.DBAccess;
import com.gerardogandeaga.cyberlock.activities.dialogs.DialogCustomLoad;

import static com.gerardogandeaga.cyberlock.utils.Settings.CRYPT_KEY;
import static com.gerardogandeaga.cyberlock.utils.Settings.DIRECTORY;
import static com.gerardogandeaga.cyberlock.utils.Settings.TMP_PWD;

public class ScrambleCryptKey extends AsyncTask<Void, Void, Void> {
    @SuppressLint("StaticFieldLeak")
    private Context mContext;

    // widgets
    private DialogCustomLoad mDialogCustomLoad;

    public ScrambleCryptKey(Context context) {
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialogCustomLoad = new DialogCustomLoad(mContext);
        mDialogCustomLoad.indeterminateProgress("Scrambling...");
    }
    @Override
    protected Void doInBackground(Void... params) {
        final String newKey = CryptKey.generateNewMasterEncryptionKey(this.mContext, TMP_PWD);

        DBAccess DBAccess = com.gerardogandeaga.cyberlock.database.DBAccess.getInstance(this.mContext);
        DBAccess.open();
        DBAccess.close();

        saveNewKey(newKey);

        mDialogCustomLoad.dismiss();

        return null;
    }

    private void saveNewKey(String newKey) {
        SharedPreferences sharedPreferences = this.mContext.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putString(CRYPT_KEY, newKey)
                .apply();
    }
}