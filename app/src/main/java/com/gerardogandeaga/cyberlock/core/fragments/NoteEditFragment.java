package com.gerardogandeaga.cyberlock.core.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.core.SaveResponder;
import com.gerardogandeaga.cyberlock.database.objects.NoteObject;
import com.gerardogandeaga.cyberlock.lists.handlers.extractors.NoteContentHandler;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author gerardogandeaga
 */
public class NoteEditFragment extends Fragment {
    private static final String TAG = "NoteEditFragment";

    // response interface
    private SaveResponder mSaveResponder;

    private NoteObject mNoteObject;
    private NoteContentHandler mNoteContentHandler;

    // view
    @BindView(R.id.tvDate)  TextView mTvDate;
    @BindView(R.id.etLabel) EditText mEtLabel;
    @BindView(R.id.etNotes) EditText mEtNotes;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // instantiate interface
        try {
            this.mSaveResponder = (SaveResponder) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG, "onCreate: could not cast " + TAG + " to SaveResponder class");
        }

        // get note object
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            this.mNoteObject = (NoteObject) bundle.get("data");
            this.mNoteContentHandler = new NoteContentHandler(getActivity(), mNoteObject);
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
        if (mNoteObject != null) {
            mTvDate.setText(mNoteContentHandler.mDate);
            mEtLabel.setText(mNoteContentHandler.mLabel);
            mEtNotes.setText(mNoteContentHandler.mNotes);
        } else {
            mTvDate.setText(null);
            mEtLabel.setText(null);
            mEtNotes.setText(null);
        }
    }

    public void save() {
        Log.i(TAG, "onSaveRequest: save requested");
        // todo compile note object here
        final String label = mEtLabel.getText().toString();
        final String note = mEtNotes.getText().toString();

        // format content
        final String format = "%s";
        final String content = String.format(format, note);

        if (mNoteObject == null) {
            this.mNoteObject = new NoteObject();

            mNoteObject.setFolder("MAIN");
            mNoteObject.setType(NoteObject.NOTE);
        }

        mNoteObject.setLabel(label);
        mNoteObject.setContent(content);

        mSaveResponder.onSaveResponse(mNoteObject);
    }
}
