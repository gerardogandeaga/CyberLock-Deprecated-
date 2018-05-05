package com.gerardogandeaga.cyberlock.core.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gerardogandeaga.cyberlock.core.dialogs.NotePreviewDialog;
import com.gerardogandeaga.cyberlock.database.objects.Folder;
import com.gerardogandeaga.cyberlock.database.objects.Note;
import com.gerardogandeaga.cyberlock.interfaces.AdapterLoaderCallback;

/**
 * @author gerardogandeaga
 */
public class NoteListFragment extends Fragment implements AdapterLoaderCallback, NotePreviewDialog.EditSelectedPreview {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onLoaded(Folder folder) {

    }

    @Override
    public void onEdit(Note note) {

    }
}
