package com.gerardogandeaga.cyberlock.support.handlers.selection;


import android.annotation.SuppressLint;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.sqlite.data.MasterDatabaseAccess;
import com.gerardogandeaga.cyberlock.sqlite.data.RawDataPackage;
import com.gerardogandeaga.cyberlock.support.recyclerview.items.RecyclerViewItem;
import com.gerardogandeaga.cyberlock.support.handlers.selection.undo.UndoHelper;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;

public class AdapterItemHandler {
    private static boolean mIsActive = false;
    private static int mCount = 0;
    private static ArrayList<RawDataPackage> mRawDataPackageList;
    // Undo handler
    @SuppressLint("StaticFieldLeak")
    private static UndoHelper mUndoHelper;

    public static void onLongClick(FastItemAdapter<RecyclerViewItem> fastItemAdapter, RecyclerViewItem item, int position) {
        mIsActive = true;
        mRawDataPackageList = new ArrayList<>();
        onClick(fastItemAdapter, item, position);
    }

    public static boolean onClick(FastItemAdapter<RecyclerViewItem> fastItemAdapter, RecyclerViewItem item, int position) {
        if (mIsActive) {
            RawDataPackage rawDataPackage = item.mRawDataPackage;

            // Check is the list already contains the data package
            if (!mRawDataPackageList.contains(rawDataPackage)) { // If item does not exist
                mRawDataPackageList.add(rawDataPackage);
                fastItemAdapter.select(position);
            } else {
                mRawDataPackageList.remove(rawDataPackage);      // If Item does exist
                fastItemAdapter.deselect(position);
            }
            mCount = mRawDataPackageList.size();

            return true;
        }

        return false;
    }

    public static void onDelete(Context context, FastItemAdapter<RecyclerViewItem> fastItemAdapter, View view) {
        if (mIsActive && (!mRawDataPackageList.isEmpty() || mRawDataPackageList == null)) {
            ArrayList<RecyclerViewItem> selectedItems = new ArrayList<>(fastItemAdapter.getSelectedItems());

            mUndoHelper = new UndoHelper(context);
            mUndoHelper.populateTempArray(fastItemAdapter, mRawDataPackageList, selectedItems);

            MasterDatabaseAccess masterDatabaseAccess = MasterDatabaseAccess.getInstance(context);
            masterDatabaseAccess.open();
            for (RawDataPackage id: mRawDataPackageList) {
                masterDatabaseAccess.delete(id);
            }
            masterDatabaseAccess.close();

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
        mRawDataPackageList = new ArrayList<>();
        mCount = 0;
        mIsActive = false;
    }

    @Contract(pure = true)
    public static boolean isActive() {
        return mIsActive;
    }
    public static boolean isValid() {
        if (mRawDataPackageList != null) {
            boolean bool = !mRawDataPackageList.isEmpty();
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
        snackbar.setActionTextColor(view.getContext().getResources().getColor(R.color.c_red_80));
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
