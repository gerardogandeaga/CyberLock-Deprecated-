package com.gerardogandeaga.cyberlock.core.fragments.preview;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.database.objects.Note;
import com.gerardogandeaga.cyberlock.database.objects.notes.GenericNote;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.richeditor.RichEditor;

/**
 * @author gerardogandeaga
 */
public class NotePreviewFragment extends Fragment {
    private GenericNote mGenericNote;

    @BindView(R.id.tvDate) TextView mDate;
    @BindView(R.id.etLabel) EditText mLabel;
    @BindView(R.id.etNotes) RichEditor mNotes;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        Note note = (Note) bundle.get("data");

        assert note != null;
        this.mGenericNote = note.getGenericNote();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_note, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mDate.setText(mGenericNote.getDate());
        mLabel.setText(mGenericNote.getLabel());
        mNotes.setHtml(mGenericNote.getNotes());

        mLabel.setEnabled(false);
        mNotes.setInputEnabled(false);
    }
}
