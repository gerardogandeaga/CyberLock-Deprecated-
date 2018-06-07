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

    private DBNoteAccessor mNoteAccessor;

    private FastItemAdapter<NoteItem> mItemAdapter;
    private List<NoteItem> mNoteItems;

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
        // lets load all notes
        List<Note> notes = mNoteAccessor.getAllNotes();
        // create items for recycler view
        if (notes != null && notes.size() > 0) {
            this.mNoteItems = new NoteItemBuilder(mContext).buildItems(notes);
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

        // callback
        // send the folder to the note list activity
        mAdapterLoaderCallback.onNoteItemsLoaded(Folder.Constants.ALL_NOTES, mItemAdapter.getItemCount());

        super.onPostExecute(aVoid);
    }
}
