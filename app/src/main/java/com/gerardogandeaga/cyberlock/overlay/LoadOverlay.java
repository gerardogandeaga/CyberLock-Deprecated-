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

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoadOverlay {
    private Context mContext;
    private View mViewToOverlay;
    private View mView;

    @BindView(R.id.progressbar) ProgressBar mProgressBar;
    @BindView(R.id.tvTitle)     TextView mTitle;

    public LoadOverlay(Context context, View viewToOverLay) {
        this.mContext = context;
        this.mViewToOverlay = viewToOverLay;
        this.mView = View.inflate(mContext, R.layout.item_progress, null);

        ButterKnife.bind(this, mView);
    }

    public void show(int resId) {
        int height = ((Scaling.pxToDp(mContext, mContext.getResources().getDisplayMetrics().heightPixels)) / 2) - Scaling.dpToPx(mContext, 56);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        params.topMargin = Scaling.dpToPx(mContext, height);
        mView.setLayoutParams(params);

        // add overlay view to main view
        ((LinearLayout) mViewToOverlay.findViewById(resId)).addView(mView);
    }

    public void dismiss() {
        if (mView != null) {
            mView.setVisibility(View.GONE);
        }
    }

    public void setTitle(String text) {
        textviewSetProperties(mTitle, text);
    }

    private void defaultViewVisibility() {
        mTitle.setVisibility(View.GONE);
    }

    private void textviewSetProperties(TextView textView, String text) {
        textView.setVisibility(View.VISIBLE);
        textView.setText(text);
    }
}
