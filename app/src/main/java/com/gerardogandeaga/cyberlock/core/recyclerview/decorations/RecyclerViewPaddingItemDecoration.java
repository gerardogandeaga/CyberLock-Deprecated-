package com.gerardogandeaga.cyberlock.core.recyclerview.decorations;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class RecyclerViewPaddingItemDecoration extends RecyclerView.ItemDecoration {
    private boolean mIsLinear = false;
    private int mSpace;

    public RecyclerViewPaddingItemDecoration( int space, boolean isLinear) {
        this.mSpace = space;
        this.mIsLinear = isLinear;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (mIsLinear) {
            outRect.bottom = 0;
        } else {
            outRect.left = mSpace;
            outRect.right = mSpace;
            outRect.bottom = mSpace - 2;

            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.top = mSpace;
            } else {
                outRect.top = mSpace;
            }
        }
    }
}
