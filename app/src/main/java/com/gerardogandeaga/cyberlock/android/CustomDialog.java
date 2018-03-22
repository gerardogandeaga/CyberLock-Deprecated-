package com.gerardogandeaga.cyberlock.android;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.utils.Res;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomDialog {
    private Context mContext;
    private View mView;

    // views
    @BindView(R.id.lnTitleBackground) LinearLayout mTitleBackground;
    // icons
    @BindView(R.id.imgIcon)           ImageView mIcon;
    @BindView(R.id.imgMenuIcon)       ImageView mMenuIcon;
    // text views
    @BindView(R.id.tvTitle)           TextView mTitle;
    @BindView(R.id.tvSubTitle)        TextView mSubTitle;
    // action buttons
    @BindView(R.id.btnPositive)       Button mPositive;
    @BindView(R.id.btnNegative)       Button mNegative;
    @BindView(R.id.btnNeutral)        Button mNeutral;
    // containers
    @BindView(R.id.titleContainer)    LinearLayout mTitleContainer;
    @BindView(R.id.buttonContainer)   LinearLayout mButtonContainer;
    @BindView(R.id.container)         LinearLayout mContainer;

    public CustomDialog(Context context) {
        this.mContext = context;
        this.mView = View.inflate(context, R.layout.custom_dialog_view, null);
        ButterKnife.bind(this, mView);

        // on create
        defaultViewVisibility();
    }

    public Dialog createDialog() {
        checkAndSetContainerVisibility();

        // wrapper keeps the dimensions consistent in the dialog view
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        RelativeLayout wrapper = new RelativeLayout(mContext);
        wrapper.setLayoutParams(params);
        wrapper.addView(mView);

        return new AlertDialog.Builder(mContext)
                .setView(wrapper)
                .create();
    }

    // views
    public void setTitleBackgroundColour(int colour) {
        mTitleBackground.setBackgroundColor(colour);
    }

    public void setTitleColour(int colour) {
        mTitle.setTextColor(colour);
        mSubTitle.setTextColor(colour);
    }

    // icon
    public void setIcon(Drawable drawable) {
        imageviewSetProperties(mIcon, drawable);
    }

    // menu icon
    public void setMenuIcon(Drawable drawable, int colourId) {
        mMenuIcon.setColorFilter(Res.getColour(mContext, colourId), PorterDuff.Mode.SRC_ATOP);
        setMenuIcon(drawable);
    }
    public void setMenuIcon(Drawable drawable)  {
        imageviewSetProperties(mMenuIcon, drawable);
    }

    // text views
    public void setTitle(String text) {
        textviewSetProperties(mTitle, text);
    }

    public void setSubTitle(String text) {
        textviewSetProperties(mSubTitle, text);
    }

    // buttons
    public void setPositiveButton(String text, View.OnClickListener listener) {
        buttonSetProperties(mPositive, text, listener);
    }

    public void setNegativeButton(String text, View.OnClickListener listener) {
        buttonSetProperties(mNegative, text, listener);
    }

    public void setNeutralButton(String text, View.OnClickListener listener) {
        buttonSetProperties(mNeutral, text, listener);
    }

    // visibility managers
    private void checkAndSetContainerVisibility() {
        // title container
        if (mIcon.getVisibility() == View.GONE && mTitle.getVisibility() == View.GONE && mSubTitle.getVisibility() == View.GONE) {
            mTitleContainer.setVisibility(View.GONE);
        }

        // button container
        if (mPositive.getVisibility() == View.GONE && mNegative.getVisibility() == View.GONE && mNeutral.getVisibility() == View.GONE) {
            mButtonContainer.setVisibility(View.GONE);
        }
    }

    // container
    public void setContentView(View view) {
        mContainer.addView(view);
    }

    private void defaultViewVisibility() {
        // icons
        mIcon.setVisibility(View.GONE);
        mMenuIcon.setVisibility(View.GONE);
        // text views
        mTitle.setVisibility(View.GONE);
        mSubTitle.setVisibility(View.GONE);
        // button
        mPositive.setVisibility(View.GONE);
        mNegative.setVisibility(View.GONE);
        mNeutral.setVisibility(View.GONE);
    }

    private void imageviewSetProperties(ImageView imageView, Drawable drawable) {
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageDrawable(drawable);
    }

    private void textviewSetProperties(TextView textView, String text) {
        textView.setVisibility(View.VISIBLE);
        textView.setText(text);
    }

    private void buttonSetProperties(Button button, String text, View.OnClickListener listener) {
        button.setVisibility(View.VISIBLE);
        button.setText(text);
        button.setOnClickListener(listener);
    }
}
