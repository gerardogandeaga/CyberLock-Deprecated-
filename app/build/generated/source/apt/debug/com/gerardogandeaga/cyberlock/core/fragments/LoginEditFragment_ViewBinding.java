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

public class LoginEditFragment_ViewBinding implements Unbinder {
  private LoginEditFragment target;

  @UiThread
  public LoginEditFragment_ViewBinding(LoginEditFragment target, View source) {
    this.target = target;

    target.mTvDate = Utils.findRequiredViewAsType(source, R.id.tvDate, "field 'mTvDate'", TextView.class);
    target.mEtLabel = Utils.findRequiredViewAsType(source, R.id.etLabel, "field 'mEtLabel'", EditText.class);
    target.mEtUrl = Utils.findRequiredViewAsType(source, R.id.etUrl, "field 'mEtUrl'", EditText.class);
    target.mEtEmail = Utils.findRequiredViewAsType(source, R.id.etEmail, "field 'mEtEmail'", EditText.class);
    target.mEtUsername = Utils.findRequiredViewAsType(source, R.id.etUsername, "field 'mEtUsername'", EditText.class);
    target.mEtPassword = Utils.findRequiredViewAsType(source, R.id.etPassword, "field 'mEtPassword'", EditText.class);
    target.mEtNotes = Utils.findRequiredViewAsType(source, R.id.etNotes, "field 'mEtNotes'", EditText.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    LoginEditFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mTvDate = null;
    target.mEtLabel = null;
    target.mEtUrl = null;
    target.mEtEmail = null;
    target.mEtUsername = null;
    target.mEtPassword = null;
    target.mEtNotes = null;
  }
}
