// Generated code from Butter Knife. Do not modify!
package com.gerardogandeaga.cyberlock.items;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.gerardogandeaga.cyberlock.R;
import de.hdodenhof.circleimageview.CircleImageView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class NoteItem$ViewHolder_ViewBinding implements Unbinder {
  private NoteItem.ViewHolder target;

  @UiThread
  public NoteItem$ViewHolder_ViewBinding(NoteItem.ViewHolder target, View source) {
    this.target = target;

    target.CardView = Utils.findRequiredViewAsType(source, R.id.container, "field 'CardView'", CardView.class);
    target.Note = Utils.findRequiredViewAsType(source, R.id.note, "field 'Note'", LinearLayout.class);
    target.PaymentInfo = Utils.findRequiredViewAsType(source, R.id.paymentInfo, "field 'PaymentInfo'", LinearLayout.class);
    target.LoginInfo = Utils.findRequiredViewAsType(source, R.id.loginInfo, "field 'LoginInfo'", LinearLayout.class);
    target.Label = Utils.findRequiredViewAsType(source, R.id.tvLabel, "field 'Label'", TextView.class);
    target.Date = Utils.findRequiredViewAsType(source, R.id.tvSubTitle, "field 'Date'", TextView.class);
    target.ColourTag = Utils.findRequiredViewAsType(source, R.id.imgColourTag, "field 'ColourTag'", CircleImageView.class);
    target.Notes = Utils.findRequiredViewAsType(source, R.id.tvNotes, "field 'Notes'", TextView.class);
    target.Holder = Utils.findRequiredViewAsType(source, R.id.tvHolder, "field 'Holder'", TextView.class);
    target.Number = Utils.findRequiredViewAsType(source, R.id.tvNumber, "field 'Number'", TextView.class);
    target.CardIcon = Utils.findRequiredViewAsType(source, R.id.imgCardIcon, "field 'CardIcon'", ImageView.class);
    target.Url = Utils.findRequiredViewAsType(source, R.id.tvUrl, "field 'Url'", TextView.class);
    target.Email = Utils.findRequiredViewAsType(source, R.id.tvEmail, "field 'Email'", TextView.class);
    target.Username = Utils.findRequiredViewAsType(source, R.id.tvUsername, "field 'Username'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    NoteItem.ViewHolder target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.CardView = null;
    target.Note = null;
    target.PaymentInfo = null;
    target.LoginInfo = null;
    target.Label = null;
    target.Date = null;
    target.ColourTag = null;
    target.Notes = null;
    target.Holder = null;
    target.Number = null;
    target.CardIcon = null;
    target.Url = null;
    target.Email = null;
    target.Username = null;
  }
}
