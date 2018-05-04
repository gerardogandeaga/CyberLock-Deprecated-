// Generated code from Butter Knife. Do not modify!
package com.gerardogandeaga.cyberlock.views;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.gerardogandeaga.cyberlock.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class CustomDialog_ViewBinding implements Unbinder {
  private CustomDialog target;

  @UiThread
  public CustomDialog_ViewBinding(CustomDialog target, View source) {
    this.target = target;

    target.mTitleBackground = Utils.findRequiredViewAsType(source, R.id.lnTitleBackground, "field 'mTitleBackground'", LinearLayout.class);
    target.mIcon = Utils.findRequiredViewAsType(source, R.id.imgIcon, "field 'mIcon'", ImageView.class);
    target.mMenuIcon = Utils.findRequiredViewAsType(source, R.id.imgMenuIcon, "field 'mMenuIcon'", ImageView.class);
    target.mTitle = Utils.findRequiredViewAsType(source, R.id.tvTitle, "field 'mTitle'", TextView.class);
    target.mSubTitle = Utils.findRequiredViewAsType(source, R.id.tvSubTitle, "field 'mSubTitle'", TextView.class);
    target.mPositive = Utils.findRequiredViewAsType(source, R.id.btnPositive, "field 'mPositive'", Button.class);
    target.mNegative = Utils.findRequiredViewAsType(source, R.id.btnNegative, "field 'mNegative'", Button.class);
    target.mNeutral = Utils.findRequiredViewAsType(source, R.id.btnNeutral, "field 'mNeutral'", Button.class);
    target.mTitleContainer = Utils.findRequiredViewAsType(source, R.id.titleContainer, "field 'mTitleContainer'", LinearLayout.class);
    target.mButtonContainer = Utils.findRequiredViewAsType(source, R.id.buttonContainer, "field 'mButtonContainer'", LinearLayout.class);
    target.mContainer = Utils.findRequiredViewAsType(source, R.id.container, "field 'mContainer'", LinearLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    CustomDialog target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mTitleBackground = null;
    target.mIcon = null;
    target.mMenuIcon = null;
    target.mTitle = null;
    target.mSubTitle = null;
    target.mPositive = null;
    target.mNegative = null;
    target.mNeutral = null;
    target.mTitleContainer = null;
    target.mButtonContainer = null;
    target.mContainer = null;
  }
}
