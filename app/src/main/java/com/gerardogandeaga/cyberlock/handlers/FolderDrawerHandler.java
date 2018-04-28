package com.gerardogandeaga.cyberlock.handlers;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.items.FolderDrawerItem;
import com.gerardogandeaga.cyberlock.items.NoteItem;
import com.gerardogandeaga.cyberlock.utils.Res;
import com.gerardogandeaga.cyberlock.utils.math.Scaling;
import com.gerardogandeaga.cyberlock.views.CustomDialog;
import com.gerardogandeaga.cyberlock.views.CustomToast;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.materialdrawer.Drawer;
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
        this.mDrawer = drawer;
        this.mItemAdapter = itemAdapter;

        mDrawer.setOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                // handle folder drawer items
                if (drawerItem instanceof FolderDrawerItem) {
                    mDrawer.closeDrawer();
                }

                return false;
            }
        });
    }

    public static void createFolder(final Context context) {
        // name input field
        final RelativeLayout wrapper = new RelativeLayout(context);
        final LinearLayout content = new LinearLayout(context);
        final EditText folderName = new EditText(context);

        LinearLayout.LayoutParams dialogParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        content.setLayoutParams(params);
        content.setPadding(
                Scaling.dpFromPx(context, 15),
                Scaling.dpFromPx(context, 10),
                Scaling.dpFromPx(context, 15),
                Scaling.dpFromPx(context, 10)
        );
        wrapper.setLayoutParams(dialogParams);
        content.setOrientation(LinearLayout.VERTICAL);
        folderName.setLayoutParams(params);

        content.addView(folderName);

        wrapper.addView(content);

        // name folder dialog prompt
        final CustomDialog dialog = new CustomDialog(context);
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
                    CustomToast.buildAndShowToast(context, "' " + folderName.getText().toString() + " ' Folder Created", CustomToast.SUCCESS, CustomToast.LENGTH_SHORT);
                } else {
                    CustomToast.buildAndShowToast(context, "Empty Name, Folder Was Not Created", CustomToast.WARNING, CustomToast.LENGTH_SHORT);
                }
                mDialog.dismiss();
            }
        });

        mDialog = dialog.createDialog();
        mDialog.show();
    }
}
