// Generated code from Butter Knife. Do not modify!
package com.gerardogandeaga.cyberlock.core.activities;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.gerardogandeaga.cyberlock.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class LoginActivity_ViewBinding implements Unbinder {
  private LoginActivity target;

  @UiThread
  public LoginActivity_ViewBinding(LoginActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public LoginActivity_ViewBinding(LoginActivity target, View source) {
    this.target = target;

    target.mTitle = Utils.findRequiredViewAsType(source, R.id.tvTitle, "field 'mTitle'", TextView.class);
    target.mEtPassword = Utils.findRequiredViewAsType(source, R.id.etPassword, "field 'mEtPassword'", EditText.class);
    target.mEtRegister = Utils.findRequiredViewAsType(source, R.id.etRegister, "field 'mEtRegister'", EditText.class);
    target.mRegister = Utils.findRequiredViewAsType(source, R.id.Register, "field 'mRegister'", TextInputLayout.class);
    target.mBtnEnter = Utils.findRequiredViewAsType(source, R.id.btnEnter, "field 'mBtnEnter'", Button.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    LoginActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mTitle = null;
    target.mEtPassword = null;
    target.mEtRegister = null;
    target.mRegister = null;
    target.mBtnEnter = null;
  }
}
