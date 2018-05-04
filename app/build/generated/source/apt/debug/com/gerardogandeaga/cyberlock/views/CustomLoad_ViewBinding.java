// Generated code from Butter Knife. Do not modify!
package com.gerardogandeaga.cyberlock.views;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.gerardogandeaga.cyberlock.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class CustomLoad_ViewBinding implements Unbinder {
  private CustomLoad target;

  @UiThread
  public CustomLoad_ViewBinding(CustomLoad target, View source) {
    this.target = target;

    target.mProgressBar = Utils.findRequiredViewAsType(source, R.id.progressbar, "field 'mProgressBar'", ProgressBar.class);
    target.mTitle = Utils.findRequiredViewAsType(source, R.id.tvTitle, "field 'mTitle'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    CustomLoad target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mProgressBar = null;
    target.mTitle = null;
  }
}
