package com.gerardogandeaga.cyberlock.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gerardogandeaga.cyberlock.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BaseDialog {
    private Context mContext;
    private View mView;

    // views
    @BindView(R.id.lnTitleBackground) LinearLayout mTitleBackground;
    // icon
    @BindView(R.id.imgIcon)           ImageView mIcon;
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

    public BaseDialog(Context context) {
        this.mContext = context;
        this.mView = View.inflate(context, R.layout.base_dialog_view, null);
        ButterKnife.bind(this, mView);

        // on create
        defaultViewVisibility();
    }

    public Dialog createDialog() {
        checkAndSetContainerVisibility();
        return new AlertDialog.Builder(mContext)
                .setView(mView)
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
        // icon
        mIcon.setVisibility(View.GONE);
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