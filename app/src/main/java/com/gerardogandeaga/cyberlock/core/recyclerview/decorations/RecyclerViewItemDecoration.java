package com.gerardogandeaga.cyberlock.core.recyclerview.decorations;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class RecyclerViewItemDecoration extends RecyclerView.ItemDecoration {
    private boolean mIsLinear = false;
    private int mSpace;

    public RecyclerViewItemDecoration(int space, boolean isLinear) {
        this.mSpace = space;
        this.mIsLinear = isLinear;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (mIsLinear) {
            outRect.bottom = 0;
        } else {
            outRect.top = mSpace;
            outRect.left = mSpace;
            outRect.right = mSpace;
            outRect.bottom = mSpace;

            // for the last 2 children add margin to the bottom
            // todo parent.getChildCount() returns the current size, because the async task loads it one by one the count does not work. so to fix this we have to load the count of items before they load
            if (parent.getChildAdapterPosition(view) == parent.getChildCount() - 2
                    || parent.getChildAdapterPosition(view) == parent.getChildCount() - 1) {
//                System.out.println(parent.getChildLayoutPosition(view));
            }
        }
    }
}
