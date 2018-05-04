// Generated code from Butter Knife. Do not modify!
package com.gerardogandeaga.cyberlock.items;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.gerardogandeaga.cyberlock.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ExternalLibItem$ViewHolder_ViewBinding implements Unbinder {
  private ExternalLibItem.ViewHolder target;

  @UiThread
  public ExternalLibItem$ViewHolder_ViewBinding(ExternalLibItem.ViewHolder target, View source) {
    this.target = target;

    target.CardView = Utils.findRequiredViewAsType(source, R.id.container, "field 'CardView'", CardView.class);
    target.Title = Utils.findRequiredViewAsType(source, R.id.tvTitle, "field 'Title'", TextView.class);
    target.Author = Utils.findRequiredViewAsType(source, R.id.tvSubTitle, "field 'Author'", TextView.class);
    target.Description = Utils.findRequiredViewAsType(source, R.id.tvDescription, "field 'Description'", TextView.class);
    target.Url = Utils.findRequiredViewAsType(source, R.id.tvUrl, "field 'Url'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ExternalLibItem.ViewHolder target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.CardView = null;
    target.Title = null;
    target.Author = null;
    target.Description = null;
    target.Url = null;
  }
}
