package com.gerardogandeaga.cyberlock.overlay;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.utils.math.Scaling;

import org.jetbrains.annotations.Contract;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoadOverlay {
    private Context mContext;

    private boolean mIsVisible;

    private View mViewToOverlay;
    private View mView;

    @BindView(R.id.progressbar) ProgressBar mProgressBar;
    @BindView(R.id.tvTitle)     TextView mTitle;

    public LoadOverlay(Context context, View viewToOverLay) {
        this.mContext = context;
        this.mViewToOverlay = viewToOverLay;
        this.mView = View.inflate(mContext, R.layout.item_progress, null);

        ButterKnife.bind(this, mView);

        defaultViewVisibility();
    }

    public void show(int containerId) {
        // todo find a better way to always keep overlay in the middle, currently i'm just adding a margin... try using gravity
        int height = ((Scaling.pxFromDp(mContext, mContext.getResources().getDisplayMetrics().heightPixels)) / 2) - Scaling.dpFromPx(mContext, 56);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        params.topMargin = Scaling.dpFromPx(mContext, height);
        mView.setLayoutParams(params);

        // add overlay view to main view
        ((LinearLayout) mViewToOverlay.findViewById(containerId)).addView(mView);

        this.mIsVisible = true;
    }

    public void dismiss() {
        if (mView != null) {
            mView.setVisibility(View.GONE);

            this.mIsVisible = false;
        }
    }

    public void setTitle(String text) {
        textviewSetProperties(text);
    }

    private void defaultViewVisibility() {
        mTitle.setVisibility(View.GONE);
    }

    private void textviewSetProperties(String text) {
        if (!isNull(text)) {
            mTitle.setVisibility(View.VISIBLE);
            mTitle.setText(text);
        }
    }

    @Contract(pure = true)
    public boolean isVisible() {
        return mIsVisible;
    }

    private boolean isNull(String string) {
        return string == null || string.isEmpty();
    }
}
