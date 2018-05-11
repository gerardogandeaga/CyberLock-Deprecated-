package com.gerardogandeaga.cyberlock.handlers;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.database.DBFolderAccessor;
import com.gerardogandeaga.cyberlock.database.loaders.NoteAdapterLoader;
import com.gerardogandeaga.cyberlock.database.objects.Folder;
import com.gerardogandeaga.cyberlock.items.FolderDrawerItem;
import com.gerardogandeaga.cyberlock.items.NoteItem;
import com.gerardogandeaga.cyberlock.utils.Res;
import com.gerardogandeaga.cyberlock.utils.math.Scaling;
import com.gerardogandeaga.cyberlock.views.CustomDialog;
import com.gerardogandeaga.cyberlock.views.CustomToast;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

/**
 * @author gerardogandeaga
 *
 * todo handle drawer click listeners. ie... folder creation, folder selection
 */
public class FolderDrawerHandler {
    private static final String TAG = "FolderDrawerHandler";

    private Context mContext;

    private Drawer mDrawer;
    private static Dialog mDialog;

    public FolderDrawerHandler(Context context, final Drawer drawer, final FastItemAdapter<NoteItem> itemAdapter) {
        this.mContext = context;
        this.mDrawer = drawer;

        mDrawer.setOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem item) {
                // if folder item
                if (item instanceof FolderDrawerItem) {
                    mDrawer.closeDrawer();
                    new NoteAdapterLoader(mContext, itemAdapter, ((FolderDrawerItem) item).getFolder()).execute();
                }

                // if create folder item
                if (item instanceof SecondaryDrawerItem) {
                    createFolder();
                }

                return false;
            }
        });
    }

    /**
     * creates a "create folder" dialog which asks for generic user input to create a basic folder
     */
    public void createFolder() {
        // name input field
        final RelativeLayout wrapper = new RelativeLayout(mContext);
        final LinearLayout content = new LinearLayout(mContext);
        final EditText folderName = new EditText(mContext);

        LinearLayout.LayoutParams dialogParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        content.setLayoutParams(params);
        content.setPadding(
                Scaling.dpFromPx(mContext, 15),
                Scaling.dpFromPx(mContext, 10),
                Scaling.dpFromPx(mContext, 15),
                Scaling.dpFromPx(mContext, 10)
        );
        wrapper.setLayoutParams(dialogParams);
        content.setOrientation(LinearLayout.VERTICAL);
        folderName.setLayoutParams(params);

        content.addView(folderName);

        wrapper.addView(content);

        // name folder dialog prompt
        final CustomDialog dialog = new CustomDialog(mContext);
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
                        mDrawer.addItem(new FolderDrawerItem(folder, true));
                        CustomToast.buildAndShowToast(mContext, "\"" + folderName.getText().toString() + "\" Created", CustomToast.SUCCESS, CustomToast.LENGTH_SHORT);
                    } else {
                        CustomToast.buildAndShowToast(mContext, "Folder Already Exists", CustomToast.WARNING, CustomToast.LENGTH_SHORT);
                    }
                } else {
                    CustomToast.buildAndShowToast(mContext, "No Inputted Name, Folder Was Not Created", CustomToast.WARNING, CustomToast.LENGTH_SHORT);
                }
                mDialog.dismiss();
            }
        });

        mDialog = dialog.createDialog();
        mDialog.show();
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
            return true;
        }
    }
}
