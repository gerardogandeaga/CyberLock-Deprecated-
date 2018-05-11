package com.gerardogandeaga.cyberlock.database.loaders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.gerardogandeaga.cyberlock.database.DBNoteAccessor;
import com.gerardogandeaga.cyberlock.database.objects.Folder;
import com.gerardogandeaga.cyberlock.database.objects.Note;
import com.gerardogandeaga.cyberlock.interfaces.AdapterLoaderCallback;
import com.gerardogandeaga.cyberlock.items.NoteItem;
import com.gerardogandeaga.cyberlock.items.NoteItemBuilder;
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

    private Folder mFolder;
    private DBNoteAccessor mNoteAccessor;
    private List<Note> mNotes;

    private FastItemAdapter<NoteItem> mItemAdapter;
    private List<NoteItem> mNoteItems;

    public NoteAdapterLoader(Context context, FastItemAdapter<NoteItem> itemAdapter, Folder folder) {
        this(context, itemAdapter);
        this.mFolder = folder;
    }

    public NoteAdapterLoader(Context context, FastItemAdapter<NoteItem> itemAdapter) {
        this.mContext = context;
        this.mNoteAccessor = DBNoteAccessor.getInstance();
        this.mItemAdapter = itemAdapter;

        // recycle the callback interface instance
        try {
            this.mAdapterLoaderCallback = (AdapterLoaderCallback) context;
        } catch (ClassCastException e) {
            Log.i(TAG, "NoteAdapterLoader: could not cast class to AdapterLoaderCallback");
        }
    }

    @Override
    protected void onPreExecute() {
        // remove for refreshing
        if (mItemAdapter.getItemCount() != 0) {
            mItemAdapter.removeItemRange(0, mItemAdapter.getItemCount());
        }

        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        // if folder is not null immediately then it must be trash, archive or all
        if (mFolder == null || mFolder.equals(Folder.Constants.ALL_NOTES_FOLDER) || mFolder.equals(Folder.Constants.ARCHIVE_FOLDER) || mFolder.equals(Folder.Constants.TRASH_FOLDER)) {
            // if no folder is selected we default it to all notes
            if (mFolder == null) {
                this.mFolder = Folder.Constants.ALL_NOTES_FOLDER;
            }

            switch (mFolder.getName()) {
                case Folder.Constants.ALL_NOTES:
                    this.mNotes = mNoteAccessor.getAllNotes();
                    break;
                case Folder.Constants.ARCHIVE:
                    this.mNotes = mNoteAccessor.getAllNotes(mFolder);
                    break;
                case Folder.Constants.TRASH:
                    this.mNotes = mNoteAccessor.getTrashedNotes();
                    break;
            }

            // construct note items from notes list
            if (mNotes != null) {
                this.mNoteItems = new NoteItemBuilder(mContext).getItems(mNotes);
            }
            return null;
        }

        // if user loads adapter with a specific folder
        if (mFolder != null) {
            this.mNotes = mNoteAccessor.getAllNotes(mFolder);

            if (mNotes != null) {
                this.mNoteItems = new NoteItemBuilder(mContext).getItems(mNotes);
            }

            return null;
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        // load notes into the adapter
        if (mNoteItems != null) {
            mItemAdapter.add(mNoteItems);
            mItemAdapter.notifyAdapterDataSetChanged();
        }

        // folder size
        // if notes is null then size = 0 or else size = notes size
        mFolder.withSize((mNotes == null ? 0 : mNotes.size()));

        // callback
        // send the folder to the note list activity
        mAdapterLoaderCallback.onLoaded(mFolder);

        super.onPostExecute(aVoid);
    }
}
