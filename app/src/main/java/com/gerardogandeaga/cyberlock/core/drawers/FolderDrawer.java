package com.gerardogandeaga.cyberlock.core.drawers;

import android.app.Activity;
import android.app.Dialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.core.activities.CoreActivity;
import com.gerardogandeaga.cyberlock.core.activities.OptionsActivity;
import com.gerardogandeaga.cyberlock.core.dialogs.ColourPaletteDialogFragment;
import com.gerardogandeaga.cyberlock.custom.CustomDialog;
import com.gerardogandeaga.cyberlock.custom.CustomToast;
import com.gerardogandeaga.cyberlock.database.DBFolderAccessor;
import com.gerardogandeaga.cyberlock.database.loaders.NoteAdapterLoader;
import com.gerardogandeaga.cyberlock.database.objects.Folder;
import com.gerardogandeaga.cyberlock.items.FolderDrawerItem;
import com.gerardogandeaga.cyberlock.items.NoteItem;
import com.gerardogandeaga.cyberlock.utils.Res;
import com.gerardogandeaga.cyberlock.utils.Scale;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.List;

/**
 * @author gerardogandeaga
 */
public class FolderDrawer {
    private Activity mActivity;
    private Toolbar mToolbar;

    private Drawer mDrawer;
    private Handler mHandler;
    private FastItemAdapter<NoteItem> mItemAdapter;
    private List<Folder> mFolders;

    /**
     * access list of folders
     * @param activity calling activity
     */
    public FolderDrawer(Activity activity, Toolbar toolbar, FastItemAdapter<NoteItem> itemAdapter) {
        this.mActivity = activity;
        this.mToolbar = toolbar;
        this.mItemAdapter = itemAdapter;

        // get folder list
        DBFolderAccessor folderAccessor = DBFolderAccessor.getInstance();
        this.mFolders = folderAccessor.getAllFolders();
    }

    public Drawer getDrawer() {
        return mDrawer;
    }

    /**
     * build the custom folder drawer
     */
    public Drawer createDrawer() {
        // drawer
        this.mDrawer = new DrawerBuilder()
                .withActivity(mActivity)
                .withToolbar(mToolbar)
                .withDisplayBelowStatusBar(false)
                .withTranslucentStatusBar(false)
                .withDrawerLayout(R.layout.drawer_container)
                // add standard constant folders
                .addDrawerItems(mainDrawerItems())
                // add custom folders
                .addDrawerItems(drawerItems())
                .addStickyDrawerItems(
                        new SecondaryDrawerItem()
                                .withSelectable(false)
                                .withName("Create New Folder")
                                .withIcon(R.drawable.ic_create_folder),
                        new SecondaryDrawerItem()
                                .withSelectable(false)
                                .withName("Application Options")
                                .withIcon(R.drawable.ic_cog)
                )
                .withCloseOnClick(false)
                .withSelectedItem(-1)
                .build();

        mDrawer.getDrawerLayout().setFitsSystemWindows(true);
        mDrawer.getSlider().setFitsSystemWindows(true);

        // drawer handler
        this.mHandler = new Handler(mActivity, mItemAdapter);

        return mDrawer;
    }

    private void updateDrawer() {
        // remove items
        mDrawer.removeAllItems();
        // get folders
        DBFolderAccessor accessor = DBFolderAccessor.getInstance();
        this.mFolders = accessor.getAllFolders();
        // re-add drawer items
        this.mDrawer.addItems(mainDrawerItems());
        this.mDrawer.addItems(drawerItems());

        // update handler
        mHandler.setMenuClicks();
    }

    private IDrawerItem[] mainDrawerItems() {
        return new IDrawerItem[] {
                // all
                new FolderDrawerItem(Folder.Constants.ALL_NOTES_FOLDER, false),
                // trash
                new FolderDrawerItem(Folder.Constants.TRASH_FOLDER, false)
                        .withIcon(R.drawable.ic_trash),
                // archive
                new FolderDrawerItem(Folder.Constants.ARCHIVE_FOLDER, false)
                        .withIcon(R.drawable.ic_archive),
                // divider
                new SectionDrawerItem()
                        .withDivider(true).
                        withName(mFolders.size() > 0 ? "Folders" : null)
        };
    }

    /**
     * convert all folders into folder drawer items
     * @return folder drawer item list
     */
    private IDrawerItem[] drawerItems() {
        IDrawerItem[] drawerItems = new IDrawerItem[mFolders.size()];

        // create drawer items
        for (int i = 0; i < mFolders.size(); i++) {
            // folder
            final Folder folder = mFolders.get(i);
            // add to list
            drawerItems[i] = (new FolderDrawerItem(folder, true));
        }

        return drawerItems;
    }

    /**
     * drawer handler :
     * handlers interactions
     */
    private class Handler implements ColourPaletteDialogFragment.ColourSelectionCallBack {
        private static final String TAG = "Handler";

        private Activity mActivity;

        private FolderDrawerItem mCurrentItem;
        private FastItemAdapter<NoteItem> mItemAdapter;
        private Dialog mDialog;

        Handler(Activity activity, final FastItemAdapter<NoteItem> itemAdapter) {
            this.mActivity = activity;
            this.mItemAdapter = itemAdapter;

            mDrawer.setOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                @Override
                public boolean onItemClick(View view, int position, IDrawerItem item) {
                    // if folder item
                    if (item instanceof FolderDrawerItem) {
                        mDrawer.closeDrawer();
                        switchFolder((FolderDrawerItem) item);
                    }

                    // if create folder item
                    if (item instanceof SecondaryDrawerItem) {
                        switch (((SecondaryDrawerItem) item).getName().toString()) {
                            // todo look through project to replace for string resources
                            case "Create New Folder":
                                createFolder();
                                break;
                            case "Application Options":
                                // go to options from the core activity reference
                                ((CoreActivity) mActivity).newIntentGoTo(OptionsActivity.class);
                                break;
                        }
                    }

                    return false;
                }
            });

            // menu clicks
            setMenuClicks();
        }

        /**
         * creates a "create folder" dialog which asks for generic user input to create a basic folder
         */
        private void createFolder() {
            // name input field
            final RelativeLayout wrapper = new RelativeLayout(mActivity);
            final LinearLayout content = new LinearLayout(mActivity);
            final EditText folderName = new EditText(mActivity);

            LinearLayout.LayoutParams dialogParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            content.setLayoutParams(params);
            content.setPadding(
                    Scale.dpFromPx(mActivity, 15),
                    Scale.dpFromPx(mActivity, 10),
                    Scale.dpFromPx(mActivity, 15),
                    Scale.dpFromPx(mActivity, 10)
            );
            wrapper.setLayoutParams(dialogParams);
            content.setOrientation(LinearLayout.VERTICAL);
            folderName.setLayoutParams(params);

            content.addView(folderName);

            wrapper.addView(content);

            // name folder dialog prompt
            final CustomDialog dialog = new CustomDialog(mActivity);
            dialog.setIcon(Res.getDrawable(R.drawable.ic_folder));
            dialog.setTitle("Folder Name");
            dialog.setContentView(wrapper);
            dialog.setNegativeButton("Cancel", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                }
            });
            dialog.setPositiveButton("Create", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!folderName.getText().toString().isEmpty()) {
                        Folder folder = new Folder()
                                .withColourTag("default")
                                .withName(folderName.getText().toString());

                        // now we try to save the folder and if it's successful the drawer item will be created
                        if (saveFolder(folder)) {
                            // saved!
                            CustomToast.buildAndShowToast(mActivity, "\"" + folderName.getText().toString() + "\" Created", CustomToast.SUCCESS, CustomToast.LENGTH_SHORT);
                        } else {
                            // folder exists!
                            CustomToast.buildAndShowToast(mActivity, "Folder Already Exists", CustomToast.WARNING, CustomToast.LENGTH_SHORT);
                        }
                    } else {
                        // input field empty!
                        CustomToast.buildAndShowToast(mActivity, "No Inputted Name, Folder Was Not Created", CustomToast.WARNING, CustomToast.LENGTH_SHORT);
                    }
                    mDialog.dismiss();
                }
            });

            this.mDialog = dialog.createDialog();
            mDialog.show();
        }

        /**
         * switch to new folder
         */
        private void switchFolder(FolderDrawerItem item) {
            try {
                new NoteAdapterLoader(mActivity, mItemAdapter, item.getFolder()).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * set menu clicks
         */
        void setMenuClicks() {
            List<IDrawerItem> items = mDrawer.getDrawerItems();

            // loop through and set listener to proper menu items
            for (IDrawerItem item : items) {
                if (item instanceof FolderDrawerItem) {
                    final FolderDrawerItem folderItem = (FolderDrawerItem) item;
                    // check if the item has a menu
                    if (folderItem.hasMenu()) {
                        // now we set the menus
                        folderItem.setMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.menu_folder_rename:
                                        return true;

                                    case R.id.menu_folder_colour:
                                        mCurrentItem = folderItem;
                                        ColourPaletteDialogFragment.show(mActivity);
                                        return true;

                                    case R.id.menu_folder_delete:
                                        Folder folder = folderItem.getFolder();
                                        if (deleteFolder(folder)) {
                                            CustomToast.buildAndShowToast(mActivity, folder.getName() + " Deleted", CustomToast.SUCCESS, CustomToast.LENGTH_SHORT);
                                        }
                                        return true;
                                }
                                return false;
                            }
                        });
                    }
                }
            }
        }

        // menu click colour palette callback
        @Override
        public void onColorSelected(String colour) {
            Folder folder = mCurrentItem.getFolder();
            folder.withColourTag(colour);

            // save
            saveFolder(folder);
            updateDrawer();
        }

        /**
         * saves folder to the db and sends back a response code to whether save was successful or not
         * @return if save was successfully completed
         */
        private boolean saveFolder(Folder folder) {
            DBFolderAccessor accessor = DBFolderAccessor.getInstance();
            // first we check if the db already contains the folder
            if (accessor.containsFolder(folder)) {
                Log.i(TAG, "saveFolder: folder already exists");
                return false;
            } else {
                accessor.save(folder);
                updateDrawer();
                return true;
            }
        }

        /**
         * update folder in the db
         * @return if it was successful or not
         */
        private boolean updateFolder(Folder folder) {
            DBFolderAccessor accessor = DBFolderAccessor.getInstance();
            // only update if folder exists
            if (accessor.containsFolder(folder)) {
                accessor.update(folder);
                return true;
            } else {
                return false;
            }
        }

        /**
         * delete folder from the db
         * @return if the folder deletions was successful or not
         */
        private boolean deleteFolder(Folder folder) {
            DBFolderAccessor accessor = DBFolderAccessor.getInstance();
            // delete folder from db
            if (accessor.containsFolder(folder)) {
                accessor.delete(folder);
                updateDrawer();
                return true;
            } else {
                Log.i(TAG, "deleteFolder: folder could not be deleted");
                return false;
            }
        }
    }
}
