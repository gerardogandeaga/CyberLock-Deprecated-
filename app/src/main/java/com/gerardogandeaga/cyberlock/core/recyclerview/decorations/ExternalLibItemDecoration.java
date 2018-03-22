package com.gerardogandeaga.cyberlock.core.recyclerview.decorations;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gerardogandeaga.cyberlock.utils.math.Scaling;

public class ExternalLibItemDecoration extends RecyclerView.ItemDecoration {
    private int mSpace;

    public ExternalLibItemDecoration(Context context) {
        this.mSpace = Scaling.dpFromPx(context, 15);
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
