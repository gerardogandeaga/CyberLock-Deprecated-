package com.gerardogandeaga.cyberlock.interfaces;

import com.gerardogandeaga.cyberlock.database.objects.NoteObject;

public interface SaveResponder {

    /**
     * sends a note object to the fragment parent to sve in the database
     * @param noteObject filled note
     */
    void onSaveResponse(NoteObject noteObject);

}
