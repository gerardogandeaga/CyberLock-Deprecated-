package com.gerardogandeaga.cyberlock.core.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.database.DBFolderAccessor;
import com.gerardogandeaga.cyberlock.database.objects.Folder;
import com.gerardogandeaga.cyberlock.utils.Graphics;
import com.gerardogandeaga.cyberlock.utils.Res;
import com.gerardogandeaga.cyberlock.views.CustomDialog;
import com.gerardogandeaga.cyberlock.views.CustomRecyclerView;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author gerardogandeaga
 */
public class FolderSelectDialogFragment extends DialogFragment {
    private static final String TAG = "FolderSelectDialogFragment";

    public interface FolderSelectionCallback {
        void onFolderSelected(Folder folder);
    }
    private FolderSelectionCallback mFolderSelectionCallback;

    private static String CurrentFolder;
    private CustomRecyclerView mRecyclerView;

    @Override
    public void onStart() {
        super.onStart();

        // initialize folder selection callback
        try {
            this.mFolderSelectionCallback = (FolderSelectionCallback) getActivity();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // constructor-like variable initializing
        FastItemAdapter<Item> itemAdapter = new FastItemAdapter<>();

        // adapter clicks
        itemAdapter.withOnClickListener(new OnClickListener<Item>() {
            @Override
            public boolean onClick(@Nullable View view, @NonNull IAdapter<Item> adapter, @NonNull Item item, int position) {
                mFolderSelectionCallback.onFolderSelected(item.getFolder());
                dismiss();
                return true;
            }
        });

        this.mRecyclerView = new CustomRecyclerView(getActivity());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(itemAdapter);

        // retrieve folders
        DBFolderAccessor accessor = DBFolderAccessor.getInstance();
        List<Folder> folders = accessor.getAllFolders();
        List<Item> items = new ArrayList<>();
        for (Folder folder : folders) {
            // create and add new list item
            items.add(new Item(folder, CurrentFolder.equals(folder.getName())));
        }
        // add items to adapter
        itemAdapter.add(items);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // build the dialog
        final CustomDialog customDialog = new CustomDialog(getActivity());
        customDialog.setTitle("Select Folder");
        customDialog.setContentView(mRecyclerView);
        customDialog.setNegativeButton("Cancel", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return customDialog.createDialog();
    }

    public static void show(AppCompatActivity activity, String folder) {
        CurrentFolder = folder;
        new FolderSelectDialogFragment().show(activity.getFragmentManager(), TAG);
    }

    /**
     * quick item adapter class
     */
    class Item extends AbstractItem<Item, Item.ViewHolder> {
        private Folder mFolder;
        private boolean mIsCurrentFolder;
        private int mColourTag;
        private String mName;

        Item(Folder folder, boolean isCurrentFolder) {
            this.mFolder = folder;
            this.mColourTag = Graphics.ColourTags.colourTag(getActivity(), folder.getColourTag());
            this.mName = folder.getName();
            this.mIsCurrentFolder = isCurrentFolder;
        }

        public Folder getFolder() {
            return mFolder;
        }

        @Override
        public int getType() {
            return R.id.fastadapter_item;
        }

        @Override
        public int getLayoutRes() {
            return R.layout.folder_simple_list_item;
        }

        @NonNull
        @Override
        public ViewHolder getViewHolder(@NonNull View view) {
            return new ViewHolder(view);
        }

        protected class ViewHolder extends FastItemAdapter.ViewHolder<Item> {
            @NonNull protected View View;

            @BindView(R.id.imgSelected) ImageView SelectedIcon;
            @BindView(R.id.imgIcon)     ImageView Icon;
            @BindView(R.id.tvTitle)     TextView Name;

            ViewHolder(@NonNull View view) {
                super(view);
                ButterKnife.bind(this, view);
                this.View = view;
            }

            @Override
            public void bindView(@NonNull Item item, @NonNull List<Object> payloads) {
                if (item.mIsCurrentFolder) {
                    SelectedIcon.setVisibility(android.view.View.VISIBLE);
                    SelectedIcon.setImageDrawable(Res.getDrawable(R.drawable.ic_check));
                } else {
                    SelectedIcon.setVisibility(android.view.View.GONE);
                    SelectedIcon.setImageDrawable(null);
                }
                Icon.setColorFilter(item.mColourTag, PorterDuff.Mode.SRC_ATOP);
                Name.setText(item.mName);
            }

            @Override
            public void unbindView(@NonNull Item item) {
                SelectedIcon.setVisibility(android.view.View.GONE);
                SelectedIcon.setImageDrawable(null);
                Icon.setColorFilter(Res.getColour(R.color.black), PorterDuff.Mode.SRC_ATOP);
                Name.setText(null);
            }
        }
    }
}
