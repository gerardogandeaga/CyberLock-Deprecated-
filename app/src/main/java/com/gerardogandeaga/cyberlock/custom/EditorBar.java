package com.gerardogandeaga.cyberlock.custom;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.gerardogandeaga.cyberlock.R;

import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.richeditor.RichEditor;

/**
 * @author gerardogandeaga
 *
 * edit text bar that modifies text on at run time
 * -bold
 * -italic
 * -underlined
 * -strike
 *
 * todo finish bar!
 */
public class EditorBar {
    private Context mContext;
    private View mView;

    private RichEditor mEditField;

    // bar buttons

    @OnClick(R.id.btnBold) void setBold() {
        mEditField.setBold();
    }

    @OnClick(R.id.btnItalic) void setItalic() {
        mEditField.setItalic();
    }

    @OnClick(R.id.btnUnderline) void setUnderline() {
        mEditField.setUnderline();
    }

    @OnClick(R.id.btnStrike) void setStrike() {
        mEditField.setStrikeThrough();
    }

    public EditorBar(Context context) {
        this.mContext = context;
        this.mView = View.inflate(context, R.layout.custom_editor_bar, null);
        ButterKnife.bind(this, mView);
    }

    public EditorBar withRootView(LinearLayout view) {
        view.addView(mView);
        return this;
    }

    public EditorBar withEditField(RichEditor editField) {
        this.mEditField = editField;
        return this;
    }
}
