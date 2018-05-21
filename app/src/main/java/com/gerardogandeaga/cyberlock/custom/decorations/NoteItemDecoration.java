package com.gerardogandeaga.cyberlock.custom.decorations;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gerardogandeaga.cyberlock.utils.Scale;

/**
 * @author gerardogandeaga
 */
public class NoteItemDecoration extends RecyclerView.ItemDecoration {
    private static final int SPACE_DP = 12;
    private boolean mIsLinear = false;
    private int mSpace;

    public NoteItemDecoration(Context context, boolean isLinear) {
        this.mSpace = Scale.dpFromPx(context, SPACE_DP);
        this.mIsLinear = isLinear;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = mSpace;
        }
        outRect.left = mSpace;
        outRect.right = mSpace;
        outRect.bottom = mSpace;
    }
}
