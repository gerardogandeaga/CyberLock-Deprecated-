package com.gerardogandeaga.cyberlock.views;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gerardogandeaga.cyberlock.R;

import org.jetbrains.annotations.Contract;

import butterknife.BindView;
import butterknife.ButterKnife;

// todo make the load view more customizable
/**
 * @author gerardogandeaga
 */
public class CustomLoad {
    private Context mContext;

    private boolean mIsVisible;

    private View mViewToOverlay;
    private View mView;

    @BindView(R.id.progressbar) ProgressBar mProgressBar;
    @BindView(R.id.tvTitle)     TextView mTitle;

    public CustomLoad(Context context, View viewToOverLay) {
        this.mContext = context;
        this.mViewToOverlay = viewToOverLay;
        this.mView = View.inflate(mContext, R.layout.fragment_load, null);

        ButterKnife.bind(this, mView);

        defaultViewVisibility();
    }

    public void show(View container) {
        // add overlay view to main view
        ((FrameLayout) container).addView(mView);
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
