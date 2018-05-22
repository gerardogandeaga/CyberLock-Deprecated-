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
import com.gerardogandeaga.cyberlock.database.objects.notes.GenericNote;
import com.gerardogandeaga.cyberlock.utils.Views;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author gerardogandeaga
 */
public class NoteEditFragment extends EditFragment {
    private static final String TAG = "NoteEditFragment";

    private Note mNote;
    private GenericNote mGenericNote;

    // view
    @BindView(R.id.tvDate)  TextView mTvDate;
    @BindView(R.id.etLabel) EditText mEtLabel;
    @BindView(R.id.etNotes) EditText mEtNotes;

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
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // if is not null then we set our stored data onto
        if (!mNote.isNew()) {
            Views.TextViews.setOrHideText(mTvDate, mGenericNote.getDate());
            mEtLabel.setText(mGenericNote.getLabel());
            mEtNotes.setText(mGenericNote.getNotes());
        } else {
            Views.TextViews.setOrHideText(mTvDate, null);
            mEtLabel.setText(null);
            mEtNotes.setText(null);
        }
    }

    @Override
    protected void compile() {
        Log.i(TAG, "compile: compiling note object...");

        mGenericNote.withNotes(mEtNotes.getText().toString());
        mGenericNote.withLabel(mEtLabel.getText().toString());
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
