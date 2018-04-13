package com.gerardogandeaga.cyberlock.database.loaders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.gerardogandeaga.cyberlock.database.DBFolderAccessor;
import com.gerardogandeaga.cyberlock.database.DBNoteAccessor;
import com.gerardogandeaga.cyberlock.database.objects.Folder;
import com.gerardogandeaga.cyberlock.database.objects.Note;
import com.gerardogandeaga.cyberlock.interfaces.AdapterLoaderCallback;
import com.gerardogandeaga.cyberlock.items.NoteItem;
import com.gerardogandeaga.cyberlock.items.NoteItemContentHandler;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import java.util.List;

/**
 * @author gerardogandeaga
 *
 * loads notes from database, constructs a list of note items and adds them
 * to the adapter
 */
public class AdapterLoader extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "AdapterLoader";
    private AdapterLoaderCallback mAdapterLoaderCallback;

    @SuppressLint("StaticFieldLeak")
    private Context mContext;

    private DBFolderAccessor mFolderAccessor;
    private Folder mFolder;
    private DBNoteAccessor mNoteAccessor;
    private List<Note> mNotes;

    private FastItemAdapter<NoteItem> mItemAdapter;
    private List<NoteItem> mNoteItems;

    @Override
    protected void onPreExecute() {
        if (mFolderAccessor != null) {
            // open folders
            mFolderAccessor.open();
        }
        // open notes
        mNoteAccessor.open();

        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        // if we are "pulling" notes from a folder
        if (mFolderAccessor != null) {
            if (mFolderAccessor.isOpen()) {
                // get top folder
                this.mFolder = mFolderAccessor.getTopFolder();
                // get notes with this folder
                this.mNotes = mNoteAccessor.getAllNotes(mFolder.getName());
            }
        } else {
            // all notes
            this.mNotes = mNoteAccessor.getAllNotes();
        }

        // construct note items from notes list
        if (mNotes != null) {
            this.mNoteItems = new NoteItemContentHandler(mContext).getItems(mNotes);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (mFolderAccessor != null) {
            // close folders
            mFolderAccessor.close();
        }
        // close notes
        mNoteAccessor.close();

        // load notes into the adapter
        if (mNotes != null) {
            mItemAdapter.add(mNoteItems);
        }

        // callback
        mAdapterLoaderCallback.onLoaded(mFolder);

        super.onPostExecute(aVoid);
    }

    public AdapterLoader(Context context, FastItemAdapter<NoteItem> itemAdapter, boolean withLastFolder) {
        this.mContext = context;

        if (withLastFolder) {
            this.mFolderAccessor = DBFolderAccessor.getInstance(context);
        }
        this.mNoteAccessor = DBNoteAccessor.getInstance(context);
        this.mItemAdapter = itemAdapter;

        // init the callback interface instance
        try {
            this.mAdapterLoaderCallback = (AdapterLoaderCallback) context;
        } catch (ClassCastException e) {
            Log.i(TAG, "AdapterLoader: could not cast class to AdapterLoaderCallback");
        }
    }
}
