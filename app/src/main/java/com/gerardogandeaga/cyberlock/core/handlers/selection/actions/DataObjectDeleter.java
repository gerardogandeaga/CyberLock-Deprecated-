package com.gerardogandeaga.cyberlock.core.handlers.selection.actions;

import android.content.Context;
import android.os.CountDownTimer;

import com.gerardogandeaga.cyberlock.android.CustomToast;
import com.gerardogandeaga.cyberlock.core.recyclerview.items.NoteItem;
import com.gerardogandeaga.cyberlock.database.DBNoteAccessor;
import com.gerardogandeaga.cyberlock.database.objects.NoteObject;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import java.util.ArrayList;

// todo this class should only work when you are in the trash folder and wanting to permanently delete an item
// todo remove the undo helper
public class DataObjectDeleter {
    private Context mContext;
    private UndoHelper mUndoHelper;

    private int mCount;
    private FastItemAdapter<NoteItem> mFastItemAdapter;
    private ArrayList<NoteItem> mNoteItemList;
    private ArrayList<NoteObject> mNoteObjectList;

    public DataObjectDeleter(Context context, FastItemAdapter<NoteItem> fastItemAdapter, ArrayList<NoteItem> noteItemList) {
        System.out.println("data item list : " + noteItemList);

        this.mContext = context;
        this.mCount = 0;
        this.mFastItemAdapter = fastItemAdapter;
        this.mNoteItemList = noteItemList;
        this.mNoteObjectList = new ArrayList<>();

        // get all data items from recycler view item
        for (int i = 0; i < mNoteItemList.size(); i++) {
            NoteItem item = noteItemList.get(i);
            // get data object
            mNoteObjectList.add(item.getNoteObject());
        }
    }

    public void deleteItems() {
        DBNoteAccessor accessor = DBNoteAccessor.getInstance(mContext);
        accessor.open();

        for (NoteObject noteObject : mNoteObjectList) {
            // delete object from the database
            accessor.delete(noteObject);

            mCount++;
        }

        accessor.close();

        // start undo timer
        this.mUndoHelper = new UndoHelper();
        mUndoHelper.startUndoTimer();
    }

    public void undoDeletion() {
        // cancel timer
        mUndoHelper.getTimer().cancel();

        // recover data
        if (!mNoteObjectList.isEmpty()) {
            DBNoteAccessor accessor = DBNoteAccessor.getInstance(mContext);
            accessor.open();
            // insert into the database
            for (int i = 0; i < mNoteObjectList.size(); i++) {
                accessor.save(mNoteObjectList.get(i));

                // re-input item into the adapter
                NoteItem item = mNoteItemList.get(i).withSetSelected(false);
                // add to the back
                mFastItemAdapter.add(item);

                if (item.getPosition() < mFastItemAdapter.getItemCount()) {
                    // move the item from the back to original position
                    mFastItemAdapter.move(mFastItemAdapter.getItemCount() - 1, item.getPosition());
                }

                // update the adapter
                mFastItemAdapter.notifyAdapterDataSetChanged();
            }
            accessor.close();
            finish();
        }
    }

    public void finish() {
        this.mFastItemAdapter = null;
        this.mNoteItemList = null;
        this.mNoteObjectList = null;
    }

    public int getDeletedCount() {
        return mCount;
    }

    class UndoHelper {
        private CountDownTimer mTimer;

        // total time
        private static final int TIME_LENGTH = 3500;
        // 1000 = 1 second
        private static final int TIME_INTERVAL = 1000;

        UndoHelper() {
            this.mTimer = new CountDownTimer(TIME_LENGTH, TIME_INTERVAL) {
                @Override
                public void onTick(long l) {}

                @Override
                public void onFinish() {
                    finish();
                    CustomToast.buildAndShowToast(
                            mContext,
                            getDeletedCount() + " Items Permanently Deleted",
                            CustomToast.INFORMATION,
                            CustomToast.LENGTH_LONG);
                }
            };
        }

        public void startUndoTimer() {
            mTimer.start();
        }

        public CountDownTimer getTimer() {
            return mTimer;
        }
    }
}
