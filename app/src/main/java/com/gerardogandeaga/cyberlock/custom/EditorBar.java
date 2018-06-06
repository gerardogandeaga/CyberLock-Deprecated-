package com.gerardogandeaga.cyberlock.custom;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.utils.Views;

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
public class EditorBar implements View.OnLongClickListener {
    private View mView;
    private RichEditor mEditField;

    public EditorBar(Context context) {
        this.mView = View.inflate(context, R.layout.custom_editor_bar, null);
        ButterKnife.bind(this, mView);

        (mView.findViewById(R.id.btnUndo)).setOnLongClickListener(this);
        (mView.findViewById(R.id.btnRedo)).setOnLongClickListener(this);
        (mView.findViewById(R.id.btnBold)).setOnLongClickListener(this);
        (mView.findViewById(R.id.btnItalic)).setOnLongClickListener(this);
        (mView.findViewById(R.id.btnUnderline)).setOnLongClickListener(this);
        (mView.findViewById(R.id.btnStrike)).setOnLongClickListener(this);
        (mView.findViewById(R.id.btnIndent)).setOnLongClickListener(this);
        (mView.findViewById(R.id.btnOutdent)).setOnLongClickListener(this);
        (mView.findViewById(R.id.btnUnOrderedList)).setOnLongClickListener(this);
        (mView.findViewById(R.id.btnOrderedList)).setOnLongClickListener(this);
        (mView.findViewById(R.id.btnLeft)).setOnLongClickListener(this);
        (mView.findViewById(R.id.btnCenter)).setOnLongClickListener(this);
        (mView.findViewById(R.id.btnRight)).setOnLongClickListener(this);
    }

    public EditorBar withRootView(Activity activity) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        mView.setLayoutParams(params);
        // seek the root view through the activity
        ((LinearLayout) activity.findViewById(R.id.root_view)).addView(mView);
        return this;
    }

    public EditorBar withEditField(RichEditor editField) {
        this.mEditField = editField;

        // we listen if the edit field is out of focus, if so then we drop the bar
        mEditField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Views.setVisibility(mView, hasFocus);
            }
        });

        // grab focus
        mEditField.requestFocus();

        return this;
    }

    // bar buttons

    /**
     * display tag name that gives button function
     */
    @Override
    public boolean onLongClick(View view) {
        if (view.getTag() != null) {
            CustomToast.buildAndShowToast(view.getContext(), view.getTag().toString());
            return true;
        }

        return false;
    }

    @OnClick(R.id.btnUndo) void setUndo() {
        mEditField.undo();
    }

    @OnClick(R.id.btnRedo) void setRedo() {
        mEditField.redo();
    }

    @OnClick(R.id.btnBold) void setBold() {
        mEditField.setBold();
    }

    @OnClick(R.id.btnItalic) void setItalic() {
        mEditField.setItalic();
    }

    @OnClick(R.id.btnUnderline) void setUnderline() {
        mEditField.setUnderline();
    }

    @OnClick(R.id.btnIndent) void setIndent() {
        mEditField.setIndent();
    }

    @OnClick(R.id.btnStrike) void setStrike() {
        mEditField.setStrikeThrough();
    }

    @OnClick(R.id.btnOutdent) void setOutdent() {
        mEditField.setOutdent();
    }

    @OnClick(R.id.btnUnOrderedList) void setUnorderedList() {
        mEditField.setBullets();
    }

    @OnClick(R.id.btnOrderedList) void setOrderedList() {
        mEditField.setNumbers();
    }

    @OnClick(R.id.btnLeft) void setLeft() {
        mEditField.setAlignLeft();
    }

    @OnClick(R.id.btnCenter) void setCenter() {
        mEditField.setAlignCenter();
    }

    @OnClick(R.id.btnRight) void setRight() {
        mEditField.setAlignRight();
    }
}
