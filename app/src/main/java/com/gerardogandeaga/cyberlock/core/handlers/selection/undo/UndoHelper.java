package com.gerardogandeaga.cyberlock.core.handlers.selection.undo;

import android.content.Context;
import android.os.CountDownTimer;

import com.gerardogandeaga.cyberlock.database.DBAccess;
import com.gerardogandeaga.cyberlock.database.DataPackage;
import com.gerardogandeaga.cyberlock.core.recyclerview.items.RecyclerViewItem;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import java.util.ArrayList;

public class UndoHelper {
    private Context mContext;
    private FastItemAdapter<RecyclerViewItem> mFastItemAdapter;
    private ArrayList<DataPackage> mTempDataPackageArray;
    private ArrayList<RecyclerViewItem> mItemViewArray;

    private CountDownTimer mCountDownTimer;

    public UndoHelper(Context context) {
        this.mContext = context;
    }

    public void populateTempArray(FastItemAdapter<RecyclerViewItem> fastItemAdapter, ArrayList<DataPackage> dataPackages, ArrayList<RecyclerViewItem> items) {
        this.mFastItemAdapter = fastItemAdapter;
        this.mTempDataPackageArray = dataPackages;
        this.mItemViewArray = items;
        deleteTimer(); // Start the count down
    }

    public void recoverData() {
        mCountDownTimer.cancel();

        if (!mTempDataPackageArray.isEmpty()) {
            DBAccess dbAccess = DBAccess.getInstance(mContext);
            dbAccess.open();
            for (int i = 0; i < mTempDataPackageArray.size(); i++) {
                // Re-save data packages
                dbAccess.save(mTempDataPackageArray.get(i));

                // Re-input into adapter
                RecyclerViewItem item = mItemViewArray.get(i);
                item.withSetSelected(false);
                int index = (int) (item.getIdentifier() - 1L);
                mFastItemAdapter.add(index, item);

                mFastItemAdapter.notifyAdapterDataSetChanged();
            }
            dbAccess.close();
            finish();
        }
    }

    private void deleteData() {
        finish();
    }

    private void deleteTimer() {
        mCountDownTimer = new CountDownTimer(3500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Do nothing
            }

            @Override
            public void onFinish() {
//                Toast.makeText(mContext, "Done!", Toast.LENGTH_SHORT).show();
                deleteData();
            }
        }.start();
    }

    private void finish() {
        this.mFastItemAdapter = null;
        this.mTempDataPackageArray = null;
        this.mItemViewArray = null;
    }
}
