package com.gerardogandeaga.cyberlock.core.handlers.selection;


import android.annotation.SuppressLint;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.database.DBAccess;
import com.gerardogandeaga.cyberlock.database.DataPackage;
import com.gerardogandeaga.cyberlock.core.recyclerview.items.RecyclerViewItem;
import com.gerardogandeaga.cyberlock.core.handlers.selection.undo.UndoHelper;
import com.gerardogandeaga.cyberlock.utils.Res;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;

public class AdapterItemHandler {
    private static boolean mIsActive = false;
    private static int mCount = 0;
    private static ArrayList<DataPackage> mDataPackageList;
    // Undo handler
    @SuppressLint("StaticFieldLeak")
    private static UndoHelper mUndoHelper;

    public static void onLongClick(FastItemAdapter<RecyclerViewItem> fastItemAdapter, RecyclerViewItem item, int position) {
        mIsActive = true;
        mDataPackageList = new ArrayList<>();
        onClick(fastItemAdapter, item, position);
    }

    public static boolean onClick(FastItemAdapter<RecyclerViewItem> fastItemAdapter, RecyclerViewItem item, int position) {
        if (mIsActive) {
            DataPackage dataPackage = item.mDataPackage;

            // Check is the list already contains the data package
            if (!mDataPackageList.contains(dataPackage)) { // If item does not exist
                mDataPackageList.add(dataPackage);
                fastItemAdapter.select(position);
            } else {
                mDataPackageList.remove(dataPackage);      // If Item does exist
                fastItemAdapter.deselect(position);
            }
            mCount = mDataPackageList.size();

            return true;
        }

        return false;
    }

    public static void onDelete(Context context, FastItemAdapter<RecyclerViewItem> fastItemAdapter, View view) {
        if (mIsActive && (!mDataPackageList.isEmpty() || mDataPackageList == null)) {
            ArrayList<RecyclerViewItem> selectedItems = new ArrayList<>(fastItemAdapter.getSelectedItems());

            mUndoHelper = new UndoHelper(context);
            mUndoHelper.populateTempArray(fastItemAdapter, mDataPackageList, selectedItems);

            DBAccess dbAccess = DBAccess.getInstance(context);
            dbAccess.open();
            for (DataPackage id: mDataPackageList) {
                dbAccess.delete(id);
            }
            dbAccess.close();

            // Remove items from view
            fastItemAdapter.deleteAllSelectedItems();
            fastItemAdapter.notifyAdapterDataSetChanged();

            snackbarDelete(view);

            finish();
        }
    }

    public static void cancel(FastItemAdapter<RecyclerViewItem> fastItemAdapter) {
        for (int i = 0; i < fastItemAdapter.getItemCount(); i++) {
            fastItemAdapter.deselect(i);
        }
        finish();
    }

    private static void finish() {
        mDataPackageList = new ArrayList<>();
        mCount = 0;
        mIsActive = false;
    }

    @Contract(pure = true)
    public static boolean isActive() {
        return mIsActive;
    }
    public static boolean isValid() {
        if (mDataPackageList != null) {
            boolean bool = !mDataPackageList.isEmpty();
            if (!bool) mIsActive = false;
            return bool;
        }
        // Return false if the array is null
        return false;
    }
    @Contract(pure = true)
    public static int getCount () {
        return mCount;
    }

    // Snack bar builder
    private static void snackbarDelete(View view) {
        String s;
        if (mCount == 1) {
            s = " Item Deleted";
        } else {
            s = " Items Deleted";
        }

        // Snack bar
        Snackbar snackbar = Snackbar.make(view, mCount + s, Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Res.getColour(view.getContext(), R.color.white));
        snackbar.setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUndoHelper.recoverData();
                finish();
            }
        });

        snackbar.show();
    }
}
