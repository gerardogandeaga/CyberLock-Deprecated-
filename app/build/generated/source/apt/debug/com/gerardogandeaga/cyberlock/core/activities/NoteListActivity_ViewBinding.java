// Generated code from Butter Knife. Do not modify!
package com.gerardogandeaga.cyberlock.core.activities;

import android.support.annotation.UiThread;
import android.view.View;
import android.widget.FrameLayout;
import butterknife.internal.Utils;
import com.gerardogandeaga.cyberlock.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class NoteListActivity_ViewBinding extends CoreActivity_ViewBinding {
  private NoteListActivity target;

  @UiThread
  public NoteListActivity_ViewBinding(NoteListActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public NoteListActivity_ViewBinding(NoteListActivity target, View source) {
    super(target, source);

    this.target = target;

    target.mContainer = Utils.findRequiredViewAsType(source, R.id.fragment_container, "field 'mContainer'", FrameLayout.class);
  }

  @Override
  public void unbind() {
    NoteListActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mContainer = null;

    super.unbind();
  }
}
