// Generated code from Butter Knife. Do not modify!
package com.gerardogandeaga.cyberlock.core.fragments;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.gerardogandeaga.cyberlock.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class CardEditFragment_ViewBinding implements Unbinder {
  private CardEditFragment target;

  @UiThread
  public CardEditFragment_ViewBinding(CardEditFragment target, View source) {
    this.target = target;

    target.mTvDate = Utils.findRequiredViewAsType(source, R.id.tvDate, "field 'mTvDate'", TextView.class);
    target.mEtLabel = Utils.findRequiredViewAsType(source, R.id.etLabel, "field 'mEtLabel'", EditText.class);
    target.mEtCardHolder = Utils.findRequiredViewAsType(source, R.id.etCardHolder, "field 'mEtCardHolder'", EditText.class);
    target.mEtCardNumber = Utils.findRequiredViewAsType(source, R.id.etCardNumber, "field 'mEtCardNumber'", EditText.class);
    target.mEtCardExpire = Utils.findRequiredViewAsType(source, R.id.etCardExpire, "field 'mEtCardExpire'", EditText.class);
    target.mEtCardCVV = Utils.findRequiredViewAsType(source, R.id.etCardCVV, "field 'mEtCardCVV'", EditText.class);
    target.mEtNotes = Utils.findRequiredViewAsType(source, R.id.etNotes, "field 'mEtNotes'", EditText.class);
    target.mSpCardSelect = Utils.findRequiredViewAsType(source, R.id.spCardSelect, "field 'mSpCardSelect'", Spinner.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    CardEditFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mTvDate = null;
    target.mEtLabel = null;
    target.mEtCardHolder = null;
    target.mEtCardNumber = null;
    target.mEtCardExpire = null;
    target.mEtCardCVV = null;
    target.mEtNotes = null;
    target.mSpCardSelect = null;
  }
}
