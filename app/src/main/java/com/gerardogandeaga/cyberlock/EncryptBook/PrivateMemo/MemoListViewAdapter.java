package com.gerardogandeaga.cyberlock.EncryptBook.PrivateMemo;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gerardogandeaga.cyberlock.R;

import java.util.List;

public class MemoListViewAdapter extends ArrayAdapter<Memo>
{
        // Declare Variables
        Context context;
        LayoutInflater inflater;
        List<Memo> memos;
        private SparseBooleanArray mSelectedItemsIds;

        public MemoListViewAdapter(Context context, int resourceId,
                                   List<Memo> memos) {
            super(context, resourceId, memos);
            mSelectedItemsIds = new SparseBooleanArray();
            this.context = context;
            this.memos = memos;
            inflater = LayoutInflater.from(context);
        }

        private class ViewHolder {
            TextView tvTitle;
            TextView tvDate;
        }

        public View getView(int position, View view, ViewGroup parent) {
            final ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.layout_list_item_memo, null);
                // Locate the TextViews in listview_item.xml
                holder.tvTitle = (TextView) view.findViewById(R.id.tvTitle);
                holder.tvDate = (TextView) view.findViewById(R.id.tvDate);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            // Capture position and set to the TextViews
            holder.tvTitle.setText(memos.get(position).getLabel());
            holder.tvDate.setText(memos.get(position).getDate());
            return view;
        }

        @Override
        public void remove(Memo object) {
            memos.remove(object);
            notifyDataSetChanged();
        }

        public List<Memo> getWorldPopulation() {
            return memos;
        }

        public void toggleSelection(int position) {
            selectView(position, !mSelectedItemsIds.get(position));
        }

        public void removeSelection() {
            mSelectedItemsIds = new SparseBooleanArray();
            notifyDataSetChanged();
        }

        public void selectView(int position, boolean value) {
            if (value)
                mSelectedItemsIds.put(position, value);
            else
                mSelectedItemsIds.delete(position);
            notifyDataSetChanged();
        }

        public int getSelectedCount() {
            return mSelectedItemsIds.size();
        }

        public SparseBooleanArray getSelectedIds() {
            return mSelectedItemsIds;
        }
}
