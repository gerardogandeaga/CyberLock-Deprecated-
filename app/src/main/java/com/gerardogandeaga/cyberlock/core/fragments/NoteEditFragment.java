package com.gerardogandeaga.cyberlock.core.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.custom.EditorBar;
import com.gerardogandeaga.cyberlock.database.objects.Note;
import com.gerardogandeaga.cyberlock.database.objects.notes.GenericNote;
import com.gerardogandeaga.cyberlock.utils.Views;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.richeditor.RichEditor;

/**
 * @author gerardogandeaga
 */
public class NoteEditFragment extends EditFragment {
    private static final String TAG = "NoteEditFragment";

    private Note mNote;
    private GenericNote mGenericNote;

    // view
    @BindView(R.id.tvDate)  TextView mDate;
    @BindView(R.id.etLabel) EditText mLabel;
    @BindView(R.id.etNotes) RichEditor mEditor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get note object
        Bundle bundle = this.getArguments();
        this.mNote = (Note) bundle.get("data");

        // note should never equal null at this point coming form the main container activity
        assert mNote != null;
        this.mGenericNote = mNote.getGenericNote();
    }

    /**
     * set view content
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_note, container, false);
        ButterKnife.bind(this, view);

        // configure the editor
        mEditor.setEditorFontSize(18);
        mEditor.setPlaceholder("Input text here...");

        // start the editor bar
        new EditorBar(getActivity())
                .withRootView((LinearLayout) getActivity().findViewById(R.id.root_view))
                .withEditField(mEditor);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // if is not null then we set our stored data onto
        if (!mNote.isNew()) {
            Views.TextViews.setOrHideText(mDate, mGenericNote.getDate());
            mLabel.setText(mGenericNote.getLabel());
            mEditor.setHtml(mGenericNote.getNotes());
        } else {
            Views.TextViews.setOrHideText(mDate, null);
            mLabel.setText(null);
            mEditor.setHtml(null);
        }
    }

    @Override
    protected void compile() {
        Log.i(TAG, "compile: compiling note object...");

        mGenericNote.withNotes(mEditor.getHtml());
        mGenericNote.withLabel(mLabel.getText().toString());
        // create note
        this.mNote = mGenericNote.compile();

        Log.i(TAG, "compile: done compiling");
    }

    @Override
    public void update() {
        Log.i(TAG, "update: updated object requested");
        compile();
        mRequestResponder.onUpdateObjectResponse(mNote);
        Log.i(TAG, "update: updated object sent");
    }

    @Override
    public void save() {
        Log.i(TAG, "onSaveRequest: save requested");
        compile();
        mRequestResponder.onSaveResponse(mNote);
    }
}
