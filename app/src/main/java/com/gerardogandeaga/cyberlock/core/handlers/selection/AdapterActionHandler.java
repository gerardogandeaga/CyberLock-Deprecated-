package com.gerardogandeaga.cyberlock.core.handlers.selection;

import android.content.Context;
import android.util.Log;

import com.gerardogandeaga.cyberlock.core.recyclerview.items.DataItem;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.select.SelectExtension;

public class AdapterActionHandler<Item extends IItem> {
    private static final String TAG = "AdapterActionHandler";
    private Context mContext;

    private boolean mActive = false;
    private FastAdapter<Item> mFastAdapter;
    private SelectExtension<Item> mItemSelectExtension;

    public AdapterActionHandler(Context context, FastAdapter<Item> fastAdapter) {
        this.mContext = context;

        this.mFastAdapter = fastAdapter;
        this.mItemSelectExtension = new SelectExtension<>();

        // configure select extension
        mItemSelectExtension.init(mFastAdapter);
        mItemSelectExtension.withSelectable(false);
        mItemSelectExtension.withMultiSelect(false);
        mItemSelectExtension.withAllowDeselection(false);
        mItemSelectExtension.withSelectOnLongClick(false);
    }

    public void activate() {
        this.mActive = true;
    }

    public void deactivate() {
        this.mActive = false;
    }

    public boolean isActive() {
        return mActive;
    }

    public void toggle(int position) {
        if (mActive) {
            System.out.println("toggled");
            System.out.println(mFastAdapter.getItem(position).isSelected());
            if (mFastAdapter.getItem(position).isSelected()) {
                deselect(position);
            } else {
                select(position);
            }
        }
    }

    public void select(int position) {
        if (mActive) {
            mItemSelectExtension.select(position);
            Log.i(TAG, "select: Item selected at position " + position);
        }
    }

    public void deselect(int position) {
        if (mActive) {
            mItemSelectExtension.deselect(position);
            Log.i(TAG, "deselect: Item deselected at position " + position);
        }
    }

    public void deselectAll() {
        if (mItemSelectExtension.getSelectedItems() != null) {
            for (Item item : mItemSelectExtension.getSelectedItems()) {
                deselect(mFastAdapter.getPosition(item));
            }
        }
    }

    public void deleteSelected(Item item) {
        // list of permissible objects
        if (item instanceof DataItem) {
            mItemSelectExtension.deleteAllSelectedItems();
        }
    }
}
