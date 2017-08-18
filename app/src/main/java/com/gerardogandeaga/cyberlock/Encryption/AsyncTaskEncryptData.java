package com.gerardogandeaga.cyberlock.Encryption;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

public class AsyncTaskEncryptData extends AsyncTask<String, Void, Void>
{
    private Context context;

    private static SharedPreferences preferences;
    private static final String KEY = "KEY"; // ENCRYPTION KEY STORE
    private static final String TEMP_PIN = "TEMP_PIN"; // TEMP PIN STORE
    private static String pulledKey;

    public AsyncTaskEncryptData(Context context)
    {
        this.context = context;
        preferences = context.getSharedPreferences("com.gerardogandeaga.cyberlock", Context.MODE_PRIVATE);
        pulledKey = preferences.getString(KEY, null);
    }

    @Override
    protected Void doInBackground(String... params)
    {
        if (pulledKey != null)
        {
            System.out.println("ASYNC RUNNING");
            try
            {
                String ENC_DEC_KEY_StringVal = AESKeyHandler.ENCRYPTKEY(pulledKey, preferences.getString(TEMP_PIN, null)); // ENCRYPT KEY TO STRING USE A PULLED KEY OUT OF A VARIABLE TO LEAVE NOTHING IN MEMORY

                preferences.edit().putString(KEY, ENC_DEC_KEY_StringVal).apply(); // STORE THE RE-ENCRYPTED STRING
                System.out.println(ENC_DEC_KEY_StringVal);

                preferences.edit().remove(TEMP_PIN).apply(); // TERMINATE THE TEMP PIN !!!
            } catch (Exception e)
            {
                e.printStackTrace();
                Toast.makeText(context, "LOG-OUT ENCRYPTION FAILED!", Toast.LENGTH_SHORT).show();
            }
        }
        System.out.println("ASYNC DONE");

        return null;
    }
}
