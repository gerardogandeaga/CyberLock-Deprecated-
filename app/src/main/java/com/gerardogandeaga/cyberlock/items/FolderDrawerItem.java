package com.gerardogandeaga.cyberlock.items;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.database.objects.Folder;
import com.gerardogandeaga.cyberlock.utils.Graphics;
import com.gerardogandeaga.cyberlock.utils.Res;
import com.gerardogandeaga.cyberlock.utils.Views;
import com.mikepenz.materialdrawer.model.BaseDescribeableDrawerItem;
import com.mikepenz.materialdrawer.model.BaseViewHolder;

import java.util.List;

/**
 * @author gerardogandeaga
 */
public class FolderDrawerItem extends BaseDescribeableDrawerItem<FolderDrawerItem, FolderDrawerItem.ViewHolder> {
    private Folder mFolder;
    // menu
    private boolean mHasMenu;
    private int mMenu;
    // set outside of class
    private PopupMenu.OnMenuItemClickListener mMenuItemClickListener;
    private PopupMenu.OnDismissListener mOnDismissListener;

    public FolderDrawerItem(@NonNull final Folder folder, boolean hasMenu) {
        super();
        this.mFolder = folder;
        this.mHasMenu = hasMenu;

        // configure drawer item with default settings
        withSelectable(false);

        withIcon(R.drawable.ic_folder);
        withName(mFolder.getName());

        // extras
        if (hasMenu) {
            withMenu();
        }
    }

    public Folder getFolder() {
        return mFolder;
    }

    // menu

    public boolean hasMenu() {
        return mHasMenu;
    }

    private void withMenu() {
        this.mMenu = R.menu.popup_folder_drawer_item;
    }

    // menu listeners

    public void setMenuItemClickListener(PopupMenu.OnMenuItemClickListener onMenuItemClickListener) {
        this.mMenuItemClickListener = onMenuItemClickListener;
    }

    // view binding and layout manager

    @Override
    public int getType() {
        return R.id.material_drawer_item_overflow_menu;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.material_drawer_item_overflow_menu_primary;
    }

    @Override
    public void bindView(final ViewHolder viewHolder, List<Object> payloads) {
        super.bindView(viewHolder, payloads);

        final Context context = viewHolder.itemView.getContext();

        // bind the basic view parts
        bindViewHelper(viewHolder);

        if (mHasMenu) {
            // handle menu clicks
            viewHolder.menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                    MenuInflater menuInflater = popupMenu.getMenuInflater();
                    menuInflater.inflate(mMenu, popupMenu.getMenu());
                    // menu item click
                    popupMenu.setOnMenuItemClickListener(mMenuItemClickListener);
                    popupMenu.setOnDismissListener(mOnDismissListener);
                    popupMenu.show();
                }
            });

            // handle folder colour
            viewHolder.icon.setColorFilter(Graphics.ColourTags.colourTag(context, mFolder.getColourTag()));
            // handle menu image
            Views.setVisibility(viewHolder.menu, true);
            viewHolder.menu.setImageDrawable(Res.getDrawable(R.drawable.ic_options_vert));
        } else {
            Views.setVisibility(viewHolder.menu, false);
        }
        // call the onPostBindView to trigger post bind view actions (like the listener to modify the item if required)
        onPostBindView(this, viewHolder.itemView);
    }

    @Override
    public ViewHolder getViewHolder(View view) {
        return new ViewHolder(view);
    }

    static class ViewHolder extends BaseViewHolder {
        private ImageButton menu;
        private ImageView icon;

        ViewHolder(View view) {
            super(view);
            this.menu = view.findViewById(R.id.material_drawer_menu_overflow);
            this.icon = view.findViewById(R.id.material_drawer_icon);
        }
    }
}
