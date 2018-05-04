// Generated code from Butter Knife. Do not modify!
package com.gerardogandeaga.cyberlock.core.fragments;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.gerardogandeaga.cyberlock.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class NoteEditFragment_ViewBinding implements Unbinder {
  private NoteEditFragment target;

  @UiThread
  public NoteEditFragment_ViewBinding(NoteEditFragment target, View source) {
    this.target = target;

    target.mTvDate = Utils.findRequiredViewAsType(source, R.id.tvDate, "field 'mTvDate'", TextView.class);
    target.mEtLabel = Utils.findRequiredViewAsType(source, R.id.etLabel, "field 'mEtLabel'", EditText.class);
    target.mEtNotes = Utils.findRequiredViewAsType(source, R.id.etNotes, "field 'mEtNotes'", EditText.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    NoteEditFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mTvDate = null;
    target.mEtLabel = null;
    target.mEtNotes = null;
  }
}
