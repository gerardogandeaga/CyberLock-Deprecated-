package com.gerardogandeaga.cyberlock.database.loaders;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.gerardogandeaga.cyberlock.database.DBNoteAccessor;
import com.gerardogandeaga.cyberlock.database.objects.NoteObject;

/**
 * @author gerardogandeaga
 */
// todo convert to runnable or simpler class than async
public class NoteLoader extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "NoteLoader";

    // listener for when a single package is loaded
    public interface OnDataPackageLoaded {
        // send the data package to the adapter
        void sendPackage(NoteObject noteObject);
    }
    private OnDataPackageLoaded mOnDataPackageLoaded;

    private DBNoteAccessor mAccessor;
    private Cursor mCursor;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mAccessor.open();
        this.mCursor = mAccessor.getQuery();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        // start on the first item in db
        mCursor.moveToFirst();

        while (!mCursor.isClosed()) {
            NoteObject noteObject = getDataPackage();
            sendProcessedDataPackage(noteObject);
            Log.i(TAG, "doInBackground: Data package : " + noteObject);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mAccessor.close();
    }

    public NoteLoader(Context context) {
        this.mAccessor = DBNoteAccessor.getInstance(context);

        try {
            this.mOnDataPackageLoaded = (OnDataPackageLoaded) context;
        } catch (ClassCastException e) {
            Log.e(TAG, "NoteLoader: Error casting context to OnDataPackageLoaded interface");
        }
    }

    public int size() {
        int size = -1;
        if (mAccessor != null && !mAccessor.isOpen()) {
            mAccessor.open();
            size = mAccessor.size();
            mAccessor.close();
        }
        Log.i(TAG, "size: Database size : " + size);
        return size;
    }

    private NoteObject getDataPackage() {
        NoteObject noteObject = null;
        if (mAccessor != null) {

            noteObject = mAccessor.getDataPackage(mCursor);
            mCursor.moveToNext();

            // if the object is null then we assume we are out bounds and will close the cursor
            if (noteObject == null) {
                mCursor.close();
            }
        }

        return noteObject;
    }

    private void sendProcessedDataPackage(NoteObject noteObject) {
        if (mOnDataPackageLoaded != null) {
            mOnDataPackageLoaded.sendPackage(noteObject);
        }
    }
}