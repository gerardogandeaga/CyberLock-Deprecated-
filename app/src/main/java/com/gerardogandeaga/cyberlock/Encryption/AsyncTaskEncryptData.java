package com.gerardogandeaga.cyberlock.Encryption;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

public class AsyncTaskEncryptData extends AsyncTask<String, Void, Void>
{
    private Context mContext;

    private SharedPreferences mSharedPreferences;
    private static final String KEY = "KEY"; // ENCRYPTION KEY STORE
    private static final String TEMP_PIN = "TEMP_PIN"; // TEMP PIN STORE
    private String mPulledKey;

    public AsyncTaskEncryptData(Context context)
    {
        this.mContext = context;
        mSharedPreferences = context.getSharedPreferences("com.gerardogandeaga.cyberlock", Context.MODE_PRIVATE);
        mPulledKey = mSharedPreferences.getString(KEY, null);
    }

    @Override
    protected Void doInBackground(String... params)
    {
        if (mPulledKey != null)
        {
            System.out.println("ASYNC RUNNING");
            try
            {
                String ENC_DEC_KEY_StringVal = AESKeyHandler.ENCRYPTKEY(mPulledKey, mSharedPreferences.getString(TEMP_PIN, null)); // ENCRYPT KEY TO STRING USE A PULLED KEY OUT OF A VARIABLE TO LEAVE NOTHING IN MEMORY

                mSharedPreferences.edit().putString(KEY, ENC_DEC_KEY_StringVal).apply(); // STORE THE RE-ENCRYPTED STRING
                System.out.println(ENC_DEC_KEY_StringVal);

                mSharedPreferences.edit().remove(TEMP_PIN).apply(); // TERMINATE THE TEMP PIN !!!
            } catch (Exception e)
            {
                e.printStackTrace();
                Toast.makeText(mContext, "LOG-OUT ENCRYPTION FAILED!", Toast.LENGTH_SHORT).show();
            }
        }
        System.out.println("ASYNC DONE");

        return null;
    }
}
