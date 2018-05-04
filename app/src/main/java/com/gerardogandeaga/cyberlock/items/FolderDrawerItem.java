package com.gerardogandeaga.cyberlock.items;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.database.objects.Folder;
import com.gerardogandeaga.cyberlock.utils.Res;
import com.gerardogandeaga.cyberlock.utils.math.Scaling;
import com.mikepenz.materialdrawer.model.BaseDescribeableDrawerItem;
import com.mikepenz.materialdrawer.model.BaseViewHolder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author gerardogandeaga
 */
public class FolderDrawerItem extends BaseDescribeableDrawerItem<FolderDrawerItem, FolderDrawerItem.ViewHolder> {
    private static final String TAG = "FolderDrawerItem";
    private PopupMenu.OnDismissListener mOnDismissListener;

    private boolean mHasMenu;
    private int mMenu;

    public FolderDrawerItem(@NonNull final Folder folder, boolean withMenu) {
        super();

        // configure drawer item with default settings
        withSelectable(false);

        withIcon(R.drawable.ic_folder);
        withName(folder.getName());

        // menu
        this.mHasMenu = withMenu;
        if (withMenu) {
            withMenu(R.menu.menu_folder_drawer);
        }
    }

    public FolderDrawerItem withMenu(int menu) {
        this.mMenu = menu;
        withOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_folder_rename:
                        break;
                    case R.id.menu_folder_colour:
                        break;
                    case R.id.menu_folder_delete:
                        break;
                }
                return false;
            }
        });
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
    public ViewHolder getViewHolder(View view) {
        return new ViewHolder(view);
    }

    static class ViewHolder extends BaseViewHolder {
        @BindView(R.id.material_drawer_menu_overflow) ImageButton Menu;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public void bindView(ViewHolder viewHolder, List payloads) {
        super.bindView(viewHolder, payloads);

        Context context = viewHolder.itemView.getContext();

        //bind the basic view parts
        bindViewHelper(viewHolder);

        if (mHasMenu) {
            //handle menu click
            viewHolder.Menu.setOnClickListener(new View.OnClickListener() {
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
            viewHolder.Menu.setLayoutParams(new LinearLayout.LayoutParams(Scaling.dpFromPx(context, 40), Scaling.dpFromPx(context, 40)));
            viewHolder.Menu.setRotation(90f);
            viewHolder.Menu.setImageDrawable(Res.getDrawable(R.drawable.ic_options_hor));
        } else {
            viewHolder.Menu.setVisibility(View.GONE);
        }

        //call the onPostBindView method to trigger post bind view actions (like the listener to modify the item if required)
        onPostBindView(this, viewHolder.itemView);
    }
}
