package com.gerardogandeaga.cyberlock.items;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.database.objects.Folder;
import com.gerardogandeaga.cyberlock.utils.Resources;
import com.gerardogandeaga.cyberlock.views.CustomToast;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.BaseDescribeableDrawerItem;
import com.mikepenz.materialdrawer.model.BaseViewHolder;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.List;

/**
 * @author gerardogandeaga
 *
 * custom drawer folder item for easy folder item integration in the
 * navigation drawer
 *
 * todo create a advanced drawer menu item parent class
 */
public class FolderDrawerItem extends BaseDescribeableDrawerItem<FolderDrawerItem, FolderDrawerItem.ViewHolder> {
    private Context mContext;
    private int mMenu;

    public FolderDrawerItem(@NonNull final Folder folder) {
        super();

        // configure drawer item with default settings
        withSelectable(false);

        withIcon(R.drawable.ic_folder);
        withName(folder.getName());
        withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                CustomToast.buildAndShowToast(view.getContext(), folder.getName(), CustomToast.INFORMATION, CustomToast.LENGTH_SHORT);
                return false;
            }
        });

        // menu
        withMenu(R.menu.menu_drawer_item);
        withOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
//                    case R.id.menu_folder_rename:
//                        CustomToast.buildAndShowToast(mContext, "Rename Folder", CustomToast.INFORMATION, CustomToast.LENGTH_LONG);
//                        break;
//                    case R.id.menu_folder_colour:
//                        CustomToast.buildAndShowToast(mContext, "Change Colour Tag", CustomToast.INFORMATION, CustomToast.LENGTH_LONG);
//                        break;
//                    case R.id.menu_folder_delete:
//                        CustomToast.buildAndShowToast(mContext, "Delete Folder", CustomToast.INFORMATION, CustomToast.LENGTH_LONG);
//                        break;
                }
                return false;
            }
        });
    }








    public FolderDrawerItem withMenu(int menu) {
        this.mMenu = menu;
        return this;
    }

    public int getMenu() {
        return mMenu;
    }

    private PopupMenu.OnMenuItemClickListener mOnMenuItemClickListener;

    public FolderDrawerItem withOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener onMenuItemClickListener) {
        this.mOnMenuItemClickListener = onMenuItemClickListener;
        return this;
    }

    public PopupMenu.OnMenuItemClickListener getOnMenuItemClickListener() {
        return mOnMenuItemClickListener;
    }

    private PopupMenu.OnDismissListener mOnDismissListener;


    public FolderDrawerItem withOnDismissListener(PopupMenu.OnDismissListener onDismissListener) {
        this.mOnDismissListener = onDismissListener;
        return this;
    }

    public PopupMenu.OnDismissListener getOnDismissListener() {
        return mOnDismissListener;
    }

    @Override
    public int getType() {
        return R.id.material_drawer_item_overflow_menu;
    }

    @Override
    @LayoutRes
    public int getLayoutRes() {
        return R.layout.material_drawer_item_overflow_menu_primary;
    }

    @Override
    public void bindView(ViewHolder viewHolder, List payloads) {
        super.bindView(viewHolder, payloads);

        Context context = viewHolder.itemView.getContext();

        //bind the basic view parts
        bindViewHelper(viewHolder);

        //handle menu click
        viewHolder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(view.getContext(), view);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(mMenu, popup.getMenu());

                popup.setOnMenuItemClickListener(mOnMenuItemClickListener);
                popup.setOnDismissListener(mOnDismissListener);

                popup.show();
            }
        });

        //handle image
        viewHolder.menu.setImageDrawable(Resources.getDrawable(context, R.drawable.ic_small_options));

        //call the onPostBindView method to trigger post bind view actions (like the listener to modify the item if required)
        onPostBindView(this, viewHolder.itemView);
    }

    @Override
    public ViewHolder getViewHolder(View view) {
        this.mContext = view.getContext();
        return new ViewHolder(view);
    }

    public static class ViewHolder extends BaseViewHolder {
        //protected ImageButton ibOverflow;
        private ImageButton menu;

        public ViewHolder(View view) {
            super(view);
            this.menu = (ImageButton) view.findViewById(R.id.material_drawer_menu_overflow);
        }
    }
}
