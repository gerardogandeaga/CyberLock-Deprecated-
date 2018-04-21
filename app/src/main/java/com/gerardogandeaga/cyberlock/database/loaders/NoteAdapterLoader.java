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
public class NoteAdapterLoader extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "NoteAdapterLoader";
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
        // if folder is not null immediately then it must be trash, archive or all
        if (mFolderAccessor == null) {
            // all notes
            this.mFolder = Folder.Constants.ALL_NOTES_FOLDER;

            switch (mFolder.getName()) {
                case Folder.Constants.ALL_NOTES:
                    this.mNotes = mNoteAccessor.getAllNotes();
                    break;
                case Folder.Constants.ARCHIVE:
                    this.mNotes = mNoteAccessor.getAllNotes(mFolder.getName());
                    break;
                case Folder.Constants.TRASH:
                    this.mNotes = mNoteAccessor.getTrashedNotes();
                    break;
            }

            // construct note items from notes list
            if (mNotes != null) {
                this.mNoteItems = new NoteItemContentHandler(mContext).getItems(mNotes);
            }
            return null;
        }

        // if we are "pulling" notes from a custom folder + archived
        if (mFolderAccessor.isOpen()) {
            // get top folder
            this.mFolder = mFolderAccessor.getTopFolder();
            // get notes with this folder
            this.mNotes = mNoteAccessor.getAllNotes(mFolder.getName());

            // construct note items from notes list
            if (mNotes != null) {
                this.mNoteItems = new NoteItemContentHandler(mContext).getItems(mNotes);
            }
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

        // folder size
        // if notes is null then size = 0 or else size = notes size
        mFolder.withSize((mNotes == null ? 0 : mNotes.size()));

        // callback
        // send the folder to the note list activity
        mAdapterLoaderCallback.onLoaded(mFolder);

        super.onPostExecute(aVoid);
    }

    public NoteAdapterLoader(Context context, FastItemAdapter<NoteItem> itemAdapter, boolean withLastFolder) {
        this.mContext = context;

        if (withLastFolder) {
//            this.mFolderAccessor = DBFolderAccessor.getInstance(context);
        }
        this.mNoteAccessor = DBNoteAccessor.getInstance(context);
        this.mItemAdapter = itemAdapter;

        // init the callback interface instance
        try {
            this.mAdapterLoaderCallback = (AdapterLoaderCallback) context;
        } catch (ClassCastException e) {
            Log.i(TAG, "NoteAdapterLoader: could not cast class to AdapterLoaderCallback");
        }
    }

    public NoteAdapterLoader(Context context, FastItemAdapter<NoteItem> itemAdapter, Folder constantFolder) {
        this.mContext = context;
        this.mFolder = constantFolder;
        this.mNoteAccessor = DBNoteAccessor.getInstance(context);
        this.mItemAdapter = itemAdapter;

        try {
            this.mAdapterLoaderCallback = (AdapterLoaderCallback) context;
        } catch (ClassCastException e) {
            Log.i(TAG, "NoteAdapterLoader: could not cast class to AdapterLoaderCallback");
        }
    }
}
