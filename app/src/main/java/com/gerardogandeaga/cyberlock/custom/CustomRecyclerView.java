package com.gerardogandeaga.cyberlock.custom;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.gerardogandeaga.cyberlock.R;

/**
 * @author gerardogandeaga
 */
public class CustomRecyclerView extends RecyclerView {
    private boolean mIsScrollable;

    public CustomRecyclerView(Context context) {
        this(context, null);
    }

    public CustomRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs, R.style.MyMainScrollbarStyle);
        this.mIsScrollable = false;

        // properties
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(params);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        return !mIsScrollable || super.dispatchTouchEvent(motionEvent);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        // animate recycler items
        for (int i = 0; i < getChildCount(); i++) {
            animate(getChildAt(i), i);

            if (i == getChildCount() - 1) {
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIsScrollable = true;
                    }
                }, i * 100);
            }
        }
    }

    /**
     * simple animation logic
     */
    private void animate(View view, final int pos) {
        view.animate().cancel();
        view.setTranslationY(100);
        view.setAlpha(0);
        view.animate().alpha(1.0f).translationY(0).setDuration(300).setStartDelay(pos * 100);
    }
}
