package com.gerardogandeaga.cyberlock.custom;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.utils.Res;
import com.gerardogandeaga.cyberlock.utils.Views;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author gerardogandeaga
 *
 * a dialog class with a custom title view, the any of these functions you want to use must be executed
 * first before casting back to a AlertDialog.Builder.
 *
 * for ex:
 *
 * new CustomDialog(mActivity)
 * .setIcon(drawable)
 * .setTitle("title")
 * .setView(view)
 *
 * *** the top functions must be declared first beacuse the bottom statement
 * will cast to super class which cannot be undone with out a re-cast; ***
 *
 * .setNegativeButton("negative", new DialogInterface.OnClickListener() {})
 * .setPositiveButton("positive", new DialogInterface.OnClickListener() {})
 * .show();
 */
public class CustomDialog extends AlertDialog.Builder {
    @BindView(R.id.imgIcon)     ImageView mIcon;
    @BindView(R.id.imgMenuIcon) ImageView mMenuIcon;
    @BindView(R.id.tvTitle)     TextView mTitle;
    @BindView(R.id.tvSubTitle)  TextView mSubTitle;

    public CustomDialog(@NonNull Context context) {
        this(context, 0);
    }

    public CustomDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);

        // custom title view
        View titleView = View.inflate(context, R.layout.custom_dialog_title, null);
        // attach the dialog
        setCustomTitle(titleView);

        ButterKnife.bind(this, titleView);

        // assume title views are null
        setIcon(null);
        setMenuIcon(null);
        setTitle(null);
        setSubTitle(null);
    }

    // extended properties
    // icon

    public CustomDialog setIcon(@DrawableRes int drawableResId) {
        return setIcon(Res.getDrawable(drawableResId));
    }

    public CustomDialog setIcon(Drawable icon) {
        Views.ImageViews.setOrHideImage(mIcon, icon);
        return this;
    }

    // menu icon

    public CustomDialog setMenuIcon(@DrawableRes int drawableResId) {
        return setMenuIcon(Res.getDrawable(drawableResId));
    }

    public CustomDialog setMenuIcon(Drawable icon) {
        Views.ImageViews.setOrHideImage(mMenuIcon, icon);
        return this;
    }

    // title

    public CustomDialog setTitle(@StringRes int stringResId) {
        return setTitle(Res.getString(stringResId));
    }

    public CustomDialog setTitle(String title) {
        Views.TextViews.setOrHideText(mTitle, title);
        return this;
    }

    // sub title

    public CustomDialog setSubTitle(@StringRes int stringResId) {
        return setSubTitle(Res.getString(stringResId));
    }

    public CustomDialog setSubTitle(String subTitle) {
        Views.TextViews.setOrHideText(mSubTitle, subTitle);
        return this;
    }
}
