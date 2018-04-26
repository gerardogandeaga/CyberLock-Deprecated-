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
import com.gerardogandeaga.cyberlock.interfaces.RequestResponder;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author gerardogandeaga
 */
public class LoginEditFragment extends EditFragment {
    private static final String TAG = "LoginEditFragment";

    // response interface
    private RequestResponder mRequestResponder;

    private Note mNote;
    private NoteContentHandler mNoteContentHandler;

    // view
    @BindView(R.id.tvDate)     TextView mTvDate;
    @BindView(R.id.etLabel)    EditText mEtLabel;
    @BindView(R.id.etUrl)      EditText mEtUrl;
    @BindView(R.id.etEmail)    EditText mEtEmail;
    @BindView(R.id.etUsername) EditText mEtUsername;
    @BindView(R.id.etPassword) EditText mEtPassword;
    @BindView(R.id.etNotes)    EditText mEtNotes;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // instantiate interface
        try {
            this.mRequestResponder = (RequestResponder) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG, "onCreate: could not cast " + TAG + " to RequestResponder class");
        }

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
        View view = inflater.inflate(R.layout.fragment_edit_login, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // if data is not null then we set our stored data onto
        if (mNote != null) {
            mTvDate.setText(mNoteContentHandler.mDate);
            mEtLabel.setText(mNoteContentHandler.mLabel);
            mEtUrl.setText(mNoteContentHandler.mUrl);
            mEtEmail.setText(mNoteContentHandler.mEmail);
            mEtUsername.setText(mNoteContentHandler.mUsername);
            mEtPassword.setText(mNoteContentHandler.mPassword);
            mEtNotes.setText(mNoteContentHandler.mNotes);
        } else {
            mTvDate.setText(null);
            mEtLabel.setText(null);
            mEtUrl.setText(null);
            mEtEmail.setText(null);
            mEtUsername.setText(null);
            mEtPassword.setText(null);
            mEtNotes.setText(null);
        }
    }

    @Override
    protected void compileObject() {
        Log.i(TAG, "compileObject: compiling note object...");
        final String label = mEtLabel.getText().toString();
        final String url = mEtUrl.getText().toString();
        final String email = mEtEmail.getText().toString();
        final String username = mEtUsername.getText().toString();
        final String password = mEtPassword.getText().toString();
        final String notes = mEtNotes.getText().toString();

        // format content
        final String format = "%s\n%s\n%s\n%s\n%s";
        final String content = String.format(format, url, email, username, password, notes);

        if (mNote == null) {
            this.mNote = new Note();
            mNote.setType(Note.LOGIN);
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

    public void save() {
        Log.i(TAG, "onSaveRequest: save requested");
        compileObject();
        mRequestResponder.onSaveResponse(mNote);
    }
}
