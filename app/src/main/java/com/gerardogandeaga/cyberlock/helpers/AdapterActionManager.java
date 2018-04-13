package com.gerardogandeaga.cyberlock.helpers;

import android.content.Context;

import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.select.SelectExtension;

import java.util.ArrayList;

/**
 * @author gerardogandeaga on 2018-03-30.
 */
public class AdapterActionManager<Item extends IItem> {
    private static final String TAG = "AdapterActionManager";
    private Context mContext;

    private int mSelectedCount;
    private boolean mActive;
    private FastItemAdapter<Item> mItemAdapter;
    private SelectExtension<Item> mItemSelectExtension;
    private ArrayList<Item> mItemList;

    /**
     * @param context calling class context
     * @param itemAdapter arbitrary item adapter
     */
    public AdapterActionManager(Context context, FastItemAdapter<Item> itemAdapter) {
        this.mContext = context;

        this.mItemAdapter = itemAdapter;
        this.mItemSelectExtension = new SelectExtension<>();

        // configure select extension
        mItemSelectExtension.init(mItemAdapter);
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
        if (mItemAdapter.getItem(position).isSelected()) {
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
        mItemList.add(mItemAdapter.getItem(position));

        mSelectedCount++;
    }

    public void deselect(int position) {
        // deselect
        mItemSelectExtension.deselect(position);
        mItemList.remove(mItemAdapter.getItem(position));

        mSelectedCount--;

        // if nothing is selected then deactivate
        if (noneAreSelected()) {
            deactivate();
        }
    }

    public void deselectAll() {
        if (mItemSelectExtension.getSelectedItems() != null) {
            for (Item item : mItemSelectExtension.getSelectedItems()) {
                deselect(mItemAdapter.getPosition(item));
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
        mItemAdapter.notifyDataSetChanged();
        return mItemList;
    }
}
