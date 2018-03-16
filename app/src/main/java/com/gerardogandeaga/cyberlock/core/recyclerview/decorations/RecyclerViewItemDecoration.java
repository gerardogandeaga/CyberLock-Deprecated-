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
        }
    }
}
