package com.gerardogandeaga.cyberlock.core.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.database.objects.Note;
import com.gerardogandeaga.cyberlock.handlers.NoteContentHandler;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author gerardogandeaga
 */
public class NoteEditFragment extends EditFragment {
    private static final String TAG = "NoteEditFragment";

    private Note mNote;
    private NoteContentHandler mNoteContentHandler;

    // view
    @BindView(R.id.tvDate)  TextView mTvDate;
    @BindView(R.id.etLabel) EditText mEtLabel;
    @BindView(R.id.etNotes) EditText mEtNotes;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get note object
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            this.mNote = (Note) bundle.get("data");
            this.mNoteContentHandler = new NoteContentHandler(getActivity(), mNote);
        }
    }

    /**
     * set view content
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_note, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // if data is not null then we set our stored data onto
        if (mNote != null) {
            mTvDate.setText(mNoteContentHandler.mDate);
            mEtLabel.setText(mNoteContentHandler.mLabel);
            mEtNotes.setText(mNoteContentHandler.mNotes);
        } else {
            mTvDate.setText(null);
            mEtLabel.setText(null);
            mEtNotes.setText(null);
        }
    }

    @Override
    protected void compileObject() {
        Log.i(TAG, "compileObject: compiling note object...");
        final String label = mEtLabel.getText().toString();
        final String note = mEtNotes.getText().toString();

        // format content
        final String format = "%s";
        final String content = String.format(format, note);

        if (mNote == null) {
            this.mNote = new Note();

            mNote.setType(Note.NOTE);
        }

        mNote.setLabel(label);
        mNote.setContent(content);
        Log.i(TAG, "compileObject: done compiling");
    }

    @Override
    public void updateObject() {
        Log.i(TAG, "updateObject: updated object requested");
        compileObject();
        mRequestResponder.onUpdateObjectResponse(mNote);
        Log.i(TAG, "updateObject: updated object sent");
    }

    @Override
    public void save() {
        Log.i(TAG, "onSaveRequest: save requested");
        compileObject();
        mRequestResponder.onSaveResponse(mNote);
    }
}
