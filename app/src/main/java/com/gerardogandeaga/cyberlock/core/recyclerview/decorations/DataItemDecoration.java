package com.gerardogandeaga.cyberlock.core.recyclerview.decorations;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gerardogandeaga.cyberlock.utils.math.Scaling;

public class DataItemDecoration extends RecyclerView.ItemDecoration {
    private boolean mIsLinear = false;
    private int mSpace;

    public DataItemDecoration(Context context, boolean isLinear) {
        this.mSpace = Scaling.dpFromPx(context, 4);
        this.mIsLinear = isLinear;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (!mIsLinear) {
            outRect.top = mSpace;
            outRect.left = mSpace;
            outRect.right = mSpace;
            outRect.bottom = mSpace;
        }
    }
}
