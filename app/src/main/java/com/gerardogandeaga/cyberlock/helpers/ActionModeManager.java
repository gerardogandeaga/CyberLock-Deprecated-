package com.gerardogandeaga.cyberlock.helpers;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.gerardogandeaga.cyberlock.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter_extensions.ActionModeHelper;
import com.mikepenz.fastadapter_extensions.UndoHelper;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialize.util.UIUtils;

import java.util.ArrayList;
import java.util.Set;

/**
 * @author gerardogandeaga
 * <p>
 * simple generic action mode for ItemAdapters
 */
public class ActionModeManager<Item extends IItem> {
    private static final String TAG = "ActionModeManager";

    private Activity mActivity;

    private boolean mIsActive;
    private Drawer mDrawer;
    private FastItemAdapter<Item> mItemAdapter;
    private UndoHelper<Item> mUndoHelper;
    private ActionModeHelper mActionModeHelper;
    // callback
    private ActionManagerCallBack mActionManagerCallBack;
    // conditions
    public static final int NORMAL = 0;
    public static final int ARCHIVE = 1;
    public static final int TRASH = 2;
    private int mCondition;

    public ActionModeManager(Activity activity, FastItemAdapter itemAdapter, Drawer drawer) {
        // content
//        activity.findViewById(android.R.id.content).setSystemUiVisibility(activity.findViewById(android.R.id.content).getVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        // main action mode
        this.mActivity = activity;

        this.mIsActive = false;
        this.mDrawer = drawer;
        this.mItemAdapter = itemAdapter;

        this.mUndoHelper = new UndoHelper<>(mItemAdapter, new UndoHelper.UndoListener<Item>() {
            @Override
            public void commitRemove(Set<Integer> positions, ArrayList<FastAdapter.RelativeInfo<Item>> removed) {

            }
        });

        this.mActionModeHelper = new ActionModeHelper(mItemAdapter, R.menu.menu_action_mode, new ActionBarCallBack());

        // initialize the callback
        try {
            this.mActionManagerCallBack = (ActionManagerCallBack) activity;
        } catch (ClassCastException e) {
            Log.i(TAG, "ActionModeManager: could not cast activity to callback");
        }

        // set condition/state
        this.mCondition = NORMAL;
    }

    /**
     * we can configure how the manager will behave depending on the condition. we will always assume
     * that the manager will be used for the same general purposes.
     *
     * @param condition NORMAL, ARCHIVE, TRASH
     */
    public void setCondition(int condition) {
        this.mCondition = condition;
    }

    /**
     * get manager condition/state
     */
    public int getCondition() {
        return mCondition;
    }

    /**
     * handles click events resulting either ignoring the event or processing the request
     * if action mode is active
     *
     * @param item adapter list item
     * @return if action was processed
     */
    public boolean onClick(IItem item) {
        final Boolean res = mActionModeHelper.onClick(item);
        return res != null ? res : false;
    }

    /**
     * begins the action mode or just selects the item if action mode is already active
     *
     * @param position position of long clicked list item
     * @return if action mode was successfully started or not
     */
    public boolean onLongClick(int position) {
        ActionMode actionMode = mActionModeHelper.onLongClick((AppCompatActivity) mActivity, position);
        if (actionMode != null) {
            mActivity.findViewById(R.id.action_mode_bar).setBackgroundColor(UIUtils.getThemeColorFromAttrOrRes(mActivity, R.attr.colorPrimary, R.color.material_drawer_primary));
        }

        return actionMode != null;
    }

    public boolean isActive() {
        return mIsActive;
    }

    /**
     * simple callback class for action mode results
     */
    private class ActionBarCallBack implements ActionMode.Callback {

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_delete:
                    // get items
                    ArrayList<Object> items = new ArrayList<>();
                    Set<Item> selectedItems = mItemAdapter.getSelectedItems();
                    items.addAll(selectedItems);
                    // remove from adapter
                    mUndoHelper.remove(mActivity.findViewById(android.R.id.content), "Item removed", "Undo", Snackbar.LENGTH_LONG, mItemAdapter.getSelections());
                    mode.finish();
                    // send removed items to the main activity
                    mActionManagerCallBack.onRemoveSelections(items);
                    return true;

                case R.id.menu_archive:
                    return true;

                case R.id.menu_undo:
                    return true;
            }
            return false;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mIsActive = true;

            // if drawer is not null lock the drawer
            if (mDrawer != null) {
                mDrawer.closeDrawer();
                mDrawer.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }

            return mIsActive;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mIsActive = false;

            // unlock drawer
            if (mDrawer != null) {
                mDrawer.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }
    }

    public interface ActionManagerCallBack {

        /**
         * sends list of removed items, since this is a static function, we need to use and object array
         * and convert it back for a weak reference to the data
         *
         * @param items Item list
         */
        void onRemoveSelections(ArrayList<Object> items);
    }
}
