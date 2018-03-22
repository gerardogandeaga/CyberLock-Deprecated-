package com.gerardogandeaga.cyberlock.database.loader;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.gerardogandeaga.cyberlock.database.DBAccess;
import com.gerardogandeaga.cyberlock.database.DataPackage;

public class DataLoader extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "DataLoader";

    // listener for when a single package is loaded
    public interface OnDataPackageLoaded {
        // send the data package to the adapter
        void sendPackage(DataPackage dataPackage);
    }
    private OnDataPackageLoaded mOnDataPackageLoaded;

    private DBAccess mDBAccess;
    private Cursor mCursor;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDBAccess.open();
        this.mCursor = mDBAccess.getQuery();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        // start on the first item in db
        mCursor.moveToFirst();

        while (!mCursor.isClosed()) {
            DataPackage dataPackage = getDataPackage();
            sendProcessedDataPackage(dataPackage);
            Log.i(TAG, "doInBackground: Data package : " + dataPackage);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mDBAccess.close();
    }

    public DataLoader(Context context) {
        this.mDBAccess = DBAccess.getInstance(context);

        try {
            this.mOnDataPackageLoaded = (OnDataPackageLoaded) context;
        } catch (ClassCastException e) {
            Log.e(TAG, "DataLoader: Error casting context to OnDataPackageLoaded interface");
        }
    }

    public int size() {
        int size = -1;
        if (mDBAccess != null && !mDBAccess.isOpen()) {
            mDBAccess.open();
            size = mDBAccess.size();
            mDBAccess.close();
        }
        Log.i(TAG, "size: Database size : " + size);
        return size;
    }

    private DataPackage getDataPackage() {
        DataPackage dataPackage = null;
        if (mDBAccess != null) {

            dataPackage = mDBAccess.getDataPackage(mCursor);
            mCursor.moveToNext();

            // if the object is null then we assume we are out bounds and will close the cursor
            if (dataPackage == null) {
                mCursor.close();
            }
        }

        return dataPackage;
    }

    private void sendProcessedDataPackage(DataPackage dataPackage) {
        if (mOnDataPackageLoaded != null) {
            mOnDataPackageLoaded.sendPackage(dataPackage);
        }
    }
}