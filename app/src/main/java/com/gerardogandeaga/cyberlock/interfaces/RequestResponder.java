package com.gerardogandeaga.cyberlock.interfaces;

public interface RequestResponder {

    /**
     * sends a note object to the fragment parent to sve in the database
     * @param object filled note
     */
    void onSaveResponse(Object object);

    void onUpdateObjectResponse(Object object);
}
