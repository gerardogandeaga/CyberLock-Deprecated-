package com.gerardogandeaga.cyberlock.core.drawers;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.Toolbar;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.database.DBFolderAccessor;
import com.gerardogandeaga.cyberlock.database.objects.Folder;
import com.gerardogandeaga.cyberlock.items.FolderDrawerItem;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gerardogandeaga
 */
public class FolderDrawer {
    private Context mContext;
    private Activity mActivity;
    private Toolbar mToolbar;

    private Drawer mDrawer;
    private List<Folder> mFolders;

    /**
     * access list of folders
     * @param activity calling activity
     */
    public FolderDrawer(Activity activity, Toolbar toolbar) {
        this.mActivity = activity;
        this.mContext = activity;
        this.mToolbar = toolbar;

        // get folder list
        DBFolderAccessor folderAccessor = DBFolderAccessor.getInstance();
        this.mFolders = folderAccessor.getAllFolders();
    }

    /**
     * build the custom folder drawer
     */
    public Drawer createDrawer() {
        // sending array list items to the array
        ArrayList<IDrawerItem> mDrawerItemArrayList = drawerItems();
        IDrawerItem[] drawerItems = new IDrawerItem[mDrawerItemArrayList.size()];
        for (int i = 0; i < drawerItems.length; i++) {
            drawerItems[i] = mDrawerItemArrayList.get(i);
        }

        // drawer
        this.mDrawer = new DrawerBuilder()
                .withActivity(mActivity)
                .withToolbar(mToolbar)
                .withDisplayBelowStatusBar(false)
                .withTranslucentStatusBar(false)
                .withDrawerLayout(R.layout.drawer_container)
                // add standard constant folders
                .addDrawerItems(
                        new FolderDrawerItem(Folder.Constants.ALL_NOTES_FOLDER, false),
                        new FolderDrawerItem(Folder.Constants.TRASH_FOLDER, false).withIcon(R.drawable.ic_trash),
                        new FolderDrawerItem(Folder.Constants.ARCHIVE_FOLDER, false).withIcon(R.drawable.ic_archive),
                        new DividerDrawerItem()
                )
                // add custom folders
                .addDrawerItems(drawerItems)
                .addStickyDrawerItems(
                        new SecondaryDrawerItem()
                                .withSelectable(false)
                                .withName("Create New Folder")
                                .withIcon(R.drawable.ic_create_folder)
                )
                .withCloseOnClick(false)
                .withSelectedItem(-1)
                .build();

        mDrawer.getDrawerLayout().setFitsSystemWindows(true);
        mDrawer.getSlider().setFitsSystemWindows(true);

        return mDrawer;
    }

    /**
     * convert all folders into folder drawer items
     * @return folder drawer item list
     */
    private ArrayList<IDrawerItem> drawerItems() {
        ArrayList<IDrawerItem> drawerItems = new ArrayList<>();

        // create drawer items
        for (int i = 0; i < mFolders.size(); i++) {
            // folder
            final Folder folder = mFolders.get(i);
            // add to list
            drawerItems.add(new FolderDrawerItem(folder, true));
        }

        return drawerItems;
    }
}
