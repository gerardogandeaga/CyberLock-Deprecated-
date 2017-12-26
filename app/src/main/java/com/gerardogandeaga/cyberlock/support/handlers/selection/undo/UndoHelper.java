package com.gerardogandeaga.cyberlock.support.handlers.selection.undo;

import android.content.Context;
import android.os.CountDownTimer;
import android.widget.Toast;

import com.gerardogandeaga.cyberlock.sqlite.data.MasterDatabaseAccess;
import com.gerardogandeaga.cyberlock.sqlite.data.RawDataPackage;
import com.gerardogandeaga.cyberlock.support.adapter.DataItemView;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import java.util.ArrayList;

public class UndoHelper {
    private Context mContext;
    private FastItemAdapter<DataItemView> mFastItemAdapter;
    private ArrayList<RawDataPackage> mTempDataPackageArray;
    private ArrayList<DataItemView> mItemViewArray;

    private CountDownTimer mCountDownTimer;

    public UndoHelper(Context context) {
        this.mContext = context;
    }

    public void populateTempArray(FastItemAdapter<DataItemView> fastItemAdapter, ArrayList<RawDataPackage> dataPackages, ArrayList<DataItemView> items) {
        this.mFastItemAdapter = fastItemAdapter;
        this.mTempDataPackageArray = dataPackages;
        this.mItemViewArray = items;
        deleteTimer(); // Start the count down
    }

    public void recoverData() {
        mCountDownTimer.cancel();

        if (!mTempDataPackageArray.isEmpty()) {
            MasterDatabaseAccess masterDatabaseAccess = MasterDatabaseAccess.getInstance(mContext);
            masterDatabaseAccess.open();
            for (int i = 0; i < mTempDataPackageArray.size(); i++) {
                // Re-save data packages
                masterDatabaseAccess.save(mTempDataPackageArray.get(i));

                // Re-input into adapter
                DataItemView item = mItemViewArray.get(i);
                item.withSetSelected(false);
                int index = (int) (item.getIdentifier() - 1L);
                mFastItemAdapter.add(index, item);

                mFastItemAdapter.notifyAdapterDataSetChanged();
            }
            masterDatabaseAccess.close();
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
                Toast.makeText(mContext, "Done!", Toast.LENGTH_SHORT).show();
                deleteData();
            }
        }.start();
    }

    public void finish() {
        this.mFastItemAdapter = null;
        this.mTempDataPackageArray = null;
        this.mItemViewArray = null;
    }
}
