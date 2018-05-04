package com.gerardogandeaga.cyberlock.handlers;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.core.drawers.FolderDrawer;
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
    private Context mContext;

    private Drawer mDrawer;
    private FastItemAdapter<NoteItem> mItemAdapter;
    private static Dialog mDialog;

    public FolderDrawerHandler(Context context, final Drawer drawer, FastItemAdapter<NoteItem> itemAdapter) {
        this.mContext = context;
        this.mDrawer = drawer;
        this.mItemAdapter = itemAdapter;

        mDrawer.setOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem item) {
                // if folder item
                if (item instanceof FolderDrawer) {
                    mDrawer.closeDrawer();
                }

                // if create folder item
                if (item instanceof SecondaryDrawerItem) {
                    createFolder();
                }

                return false;
            }
        });
    }

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
                    CustomToast.buildAndShowToast(mContext, "\"" + folderName.getText().toString() + "\" Created", CustomToast.SUCCESS, CustomToast.LENGTH_SHORT);
                    Folder folder = new Folder()
                            .withColourTag("default")
                            .withName(folderName.getText().toString());
                    mDrawer.addItem(new FolderDrawerItem(folder, true));
                } else {
                    CustomToast.buildAndShowToast(mContext, "No Inputted Name, Folder Was Not Created", CustomToast.WARNING, CustomToast.LENGTH_SHORT);
                }
                mDialog.dismiss();
            }
        });

        mDialog = dialog.createDialog();
        mDialog.show();
    }
}
