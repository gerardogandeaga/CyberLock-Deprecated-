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
import com.gerardogandeaga.cyberlock.database.objects.notes.LoginNote;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author gerardogandeaga
 */
public class LoginEditFragment extends EditFragment {
    private static final String TAG = "LoginEditFragment";

    private Note mNote;
    private LoginNote mLoginNote;

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

        // get note object
        Bundle bundle = this.getArguments();
        this.mNote = (Note) bundle.get("data");

        assert mNote != null;
        this.mLoginNote = mNote.getLoginNote();
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
        if (!mNote.isNew()) {
            mTvDate.setText(mLoginNote.getDate());
            mEtLabel.setText(mLoginNote.getLabel());
            mEtUrl.setText(mLoginNote.getUrl());
            mEtEmail.setText(mLoginNote.getEmail());
            mEtUsername.setText(mLoginNote.getUsername());
            mEtPassword.setText(mLoginNote.getPassword());
            mEtNotes.setText(mLoginNote.getNotes());
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
    public void toggleViewMode() {
        setReadOnly(!isReadOnly());

    }

    @Override
    protected void compile() {
        Log.i(TAG, "compile: compiling note object...");

        mLoginNote.withUrl(mEtUrl.getText().toString())
                .withEmail(mEtEmail.getText().toString())
                .withUsername(mEtUsername.getText().toString())
                .withPassword(mEtPassword.getText().toString())
                .withNotes(mEtNotes.getText().toString());
        mLoginNote.withLabel(mEtLabel.getText().toString());
        // create note
        this.mNote = mLoginNote.compile();

        Log.i(TAG, "compile: done compiling");
    }

    @Override
    public void update() {
        Log.i(TAG, "update: updated object requested");
        compile();
        mRequestResponder.onUpdateObjectResponse(mNote);
        Log.i(TAG, "update: updated object sent");
    }

    public void save() {
        Log.i(TAG, "onSaveRequest: save requested");
        compile();
        mRequestResponder.onSaveResponse(mNote);
    }
}
