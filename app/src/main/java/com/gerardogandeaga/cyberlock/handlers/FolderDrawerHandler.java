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
    private Dialog mDialog;

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

    public static void createFolder(Context context) {
        // name input field
        RelativeLayout wrapper = new RelativeLayout(context);
        LinearLayout content = new LinearLayout(context);
        EditText folderName = new EditText(context);

        LinearLayout.LayoutParams dialogParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        content.setLayoutParams(params);
        content.setPadding(
                Scaling.dpFromPx(context, 20),
                Scaling.dpFromPx(context, 20),
                Scaling.dpFromPx(context, 20),
                Scaling.dpFromPx(context, 20)
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

            }
        });
        dialog.setPositiveButton("Create", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Dialog d = dialog.createDialog();
        d.show();
    }
}
