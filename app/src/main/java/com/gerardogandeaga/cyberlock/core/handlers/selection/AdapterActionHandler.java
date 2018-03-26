package com.gerardogandeaga.cyberlock.core.handlers.selection;

import android.content.Context;

import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.select.SelectExtension;

import java.util.ArrayList;

public class AdapterActionHandler<Item extends IItem> {
    private static final String TAG = "AdapterActionHandler";
    private Context mContext;

    private int mSelectedCount;
    private boolean mActive;
    private FastItemAdapter<Item> mFastItemAdapter;
    private SelectExtension<Item> mItemSelectExtension;
    private ArrayList<Item> mItemList;

    /**
     * @param context calling class context
     * @param fastItemAdapter arbitrary item adapter
     */
    public AdapterActionHandler(Context context, FastItemAdapter<Item> fastItemAdapter) {
        this.mContext = context;

        this.mFastItemAdapter = fastItemAdapter;
        this.mItemSelectExtension = new SelectExtension<>();

        // configure select extension
        mItemSelectExtension.init(mFastItemAdapter);
        mItemSelectExtension.withSelectable(false);
        mItemSelectExtension.withMultiSelect(false);
        mItemSelectExtension.withAllowDeselection(false);
        mItemSelectExtension.withSelectOnLongClick(false);

        this.mActive = false;
        this.mSelectedCount = 0;
    }

    public void activate() {
        this.mItemList = new ArrayList<>();
        this.mActive = true;
    }

    /** only call it when you are not deleting anything and deselecting everything
     */
    public void deactivate() {
        deselectAll();
        this.mItemList = null;
        this.mActive = false;
    }

    /** called after the deleter is called to delete selected items
     */
    public void finish() {
        this.mSelectedCount = 0;
        this.mItemList = null;
        this.mActive = false;
    }

    public boolean isActive() {
        return mActive;
    }

    public int selectedCount() {
        return mSelectedCount;
    }

    public boolean noneAreSelected() {
        return mSelectedCount == 0;
    }

    public void toggle(int position) {
        if (mFastItemAdapter.getItem(position).isSelected()) {
            deselect(position);
        } else {
            select(position);
        }
    }

    public void select(int position) {
        // activate
        if (!mActive) {
            activate();
        }

        // select
        mItemSelectExtension.select(position);
        mItemList.add(mFastItemAdapter.getItem(position));

        mSelectedCount++;
    }

    public void deselect(int position) {
        // deselect
        mItemSelectExtension.deselect(position);
        mItemList.remove(mFastItemAdapter.getItem(position));

        mSelectedCount--;

        // if nothing is selected then deactivate
        if (noneAreSelected()) {
            deactivate();
        }
    }

    public void deselectAll() {
        if (mItemSelectExtension.getSelectedItems() != null) {
            for (Item item : mItemSelectExtension.getSelectedItems()) {
                deselect(mFastItemAdapter.getPosition(item));
            }
        }
    }

    /**
     * removes items from the adapter
     * @return items that have been removed from the adapter
     */
    public ArrayList<Item> removeItemsFromAdapter() {
        // delete items from the adapter not deleting the actual data
        mItemSelectExtension.deleteAllSelectedItems();
        // trim and return item list
        mItemList.trimToSize();
        mFastItemAdapter.notifyDataSetChanged();
        return mItemList;
    }
}
