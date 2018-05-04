// Generated code from Butter Knife. Do not modify!
package com.gerardogandeaga.cyberlock.items;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ImageButton;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.gerardogandeaga.cyberlock.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class FolderDrawerItem$ViewHolder_ViewBinding implements Unbinder {
  private FolderDrawerItem.ViewHolder target;

  @UiThread
  public FolderDrawerItem$ViewHolder_ViewBinding(FolderDrawerItem.ViewHolder target, View source) {
    this.target = target;

    target.Menu = Utils.findRequiredViewAsType(source, R.id.material_drawer_menu_overflow, "field 'Menu'", ImageButton.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    FolderDrawerItem.ViewHolder target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.Menu = null;
  }
}
