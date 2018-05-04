// Generated code from Butter Knife. Do not modify!
package com.gerardogandeaga.cyberlock.core.activities;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.Toolbar;
import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.gerardogandeaga.cyberlock.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class CoreActivity_ViewBinding implements Unbinder {
  private CoreActivity target;

  @UiThread
  public CoreActivity_ViewBinding(CoreActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public CoreActivity_ViewBinding(CoreActivity target, View source) {
    this.target = target;

    target.mToolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'mToolbar'", Toolbar.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    CoreActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mToolbar = null;
  }
}
