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
    public OnDataPackageLoaded mOnDataPackageLoaded;

    private DBAccess mDBAccess;
    private Cursor mCursor;

    @Override
    protected Void doInBackground(Void... voids) {
        // start on the first item in db
        mCursor.moveToFirst();

        while (!mCursor.isClosed()) {
            DataPackage dataPackage = getDataPackage();
            sendProcessedDataPackage(dataPackage);
        }

        return null;
    }

    public DataLoader(Context context) {
        this.mDBAccess = DBAccess.getInstance(context);
        this.mCursor = mDBAccess.getQuery();

        try {
            this.mOnDataPackageLoaded = (OnDataPackageLoaded) context;
        } catch (ClassCastException e) {
            Log.e(TAG, "DataLoader: Error casting context to OnDataPackageLoaded interface");
        }
    }

    private DataPackage getDataPackage() {
        DataPackage dataPackage = null;
        if (mDBAccess != null) {

            mDBAccess.open();

            dataPackage = mDBAccess.getDataPackage(mCursor);
            mCursor.moveToNext();

            mDBAccess.close();

            // if the object is null then we assume we are out bounds and will close the cursor
            if (dataPackage == null) {
                mCursor.close();
            }
        }

        return dataPackage;
    }

    private void sendProcessedDataPackage(DataPackage dataPackage) {
        if (dataPackage != null) {
            if (mOnDataPackageLoaded != null) {
                mOnDataPackageLoaded.sendPackage(dataPackage);
            }
        } else {
            Log.d(TAG, "sendProcessedDataPackage: data package is equal to null, not sending null object");
        }
    }
}