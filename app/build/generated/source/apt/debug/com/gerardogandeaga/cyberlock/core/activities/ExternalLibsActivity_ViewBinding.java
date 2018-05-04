// Generated code from Butter Knife. Do not modify!
package com.gerardogandeaga.cyberlock.core.activities;

import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.internal.Utils;
import com.gerardogandeaga.cyberlock.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ExternalLibsActivity_ViewBinding extends CoreActivity_ViewBinding {
  private ExternalLibsActivity target;

  @UiThread
  public ExternalLibsActivity_ViewBinding(ExternalLibsActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public ExternalLibsActivity_ViewBinding(ExternalLibsActivity target, View source) {
    super(target, source);

    this.target = target;

    target.mRecyclerView = Utils.findRequiredViewAsType(source, R.id.recyclerView, "field 'mRecyclerView'", RecyclerView.class);
  }

  @Override
  public void unbind() {
    ExternalLibsActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mRecyclerView = null;

    super.unbind();
  }
}
