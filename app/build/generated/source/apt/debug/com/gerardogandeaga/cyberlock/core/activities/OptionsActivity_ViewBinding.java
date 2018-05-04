// Generated code from Butter Knife. Do not modify!
package com.gerardogandeaga.cyberlock.core.activities;

import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import butterknife.internal.Utils;
import com.gerardogandeaga.cyberlock.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class OptionsActivity_ViewBinding extends CoreActivity_ViewBinding {
  private OptionsActivity target;

  @UiThread
  public OptionsActivity_ViewBinding(OptionsActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public OptionsActivity_ViewBinding(OptionsActivity target, View source) {
    super(target, source);

    this.target = target;

    target.mLinAutoSave = Utils.findRequiredViewAsType(source, R.id.AutoSave, "field 'mLinAutoSave'", LinearLayout.class);
    target.mLinListFormat = Utils.findRequiredViewAsType(source, R.id.ListFormat, "field 'mLinListFormat'", LinearLayout.class);
    target.mLinTaggedHeaders = Utils.findRequiredViewAsType(source, R.id.TaggedHeaders, "field 'mLinTaggedHeaders'", LinearLayout.class);
    target.mLinAutoLogoutDelay = Utils.findRequiredViewAsType(source, R.id.AutoLogoutDelay, "field 'mLinAutoLogoutDelay'", LinearLayout.class);
    target.mLinChangePassword = Utils.findRequiredViewAsType(source, R.id.ChangePassword, "field 'mLinChangePassword'", LinearLayout.class);
    target.mLinGitHub = Utils.findRequiredViewAsType(source, R.id.GitHub, "field 'mLinGitHub'", LinearLayout.class);
    target.mLinAbout = Utils.findRequiredViewAsType(source, R.id.About, "field 'mLinAbout'", LinearLayout.class);
    target.mSwAutoSave = Utils.findRequiredViewAsType(source, R.id.swAutoSave, "field 'mSwAutoSave'", Switch.class);
    target.mImgDirection = Utils.findRequiredViewAsType(source, R.id.imgDirection, "field 'mImgDirection'", ImageView.class);
    target.mImgListFormat = Utils.findRequiredViewAsType(source, R.id.imgListFormat, "field 'mImgListFormat'", ImageView.class);
    target.mSwTaggedHeaders = Utils.findRequiredViewAsType(source, R.id.swTaggedHeaders, "field 'mSwTaggedHeaders'", Switch.class);
    target.mSpLogoutDelay = Utils.findRequiredViewAsType(source, R.id.spAutoLogoutDelay, "field 'mSpLogoutDelay'", Spinner.class);
    target.mInputChangePassword = Utils.findRequiredViewAsType(source, R.id.inputChangePassword, "field 'mInputChangePassword'", LinearLayout.class);
    target.mBtnRegister = Utils.findRequiredViewAsType(source, R.id.btnRegister, "field 'mBtnRegister'", Button.class);
    target.mEtCurrentPass = Utils.findRequiredViewAsType(source, R.id.etCurrent, "field 'mEtCurrentPass'", EditText.class);
    target.mEtInitialPass = Utils.findRequiredViewAsType(source, R.id.etInitial, "field 'mEtInitialPass'", EditText.class);
    target.mEtFinalPass = Utils.findRequiredViewAsType(source, R.id.etFinal, "field 'mEtFinalPass'", EditText.class);
  }

  @Override
  public void unbind() {
    OptionsActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mLinAutoSave = null;
    target.mLinListFormat = null;
    target.mLinTaggedHeaders = null;
    target.mLinAutoLogoutDelay = null;
    target.mLinChangePassword = null;
    target.mLinGitHub = null;
    target.mLinAbout = null;
    target.mSwAutoSave = null;
    target.mImgDirection = null;
    target.mImgListFormat = null;
    target.mSwTaggedHeaders = null;
    target.mSpLogoutDelay = null;
    target.mInputChangePassword = null;
    target.mBtnRegister = null;
    target.mEtCurrentPass = null;
    target.mEtInitialPass = null;
    target.mEtFinalPass = null;

    super.unbind();
  }
}
