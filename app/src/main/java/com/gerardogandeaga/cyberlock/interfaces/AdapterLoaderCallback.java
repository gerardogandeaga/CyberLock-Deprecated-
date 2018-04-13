package com.gerardogandeaga.cyberlock.interfaces;

import com.gerardogandeaga.cyberlock.database.objects.Folder;

/**
 * @author gerardogandeaga
 */
public interface AdapterLoaderCallback {

    /**
     * gets called when the adapter loader is finished loading the data and adding
     * items to the adapter
     */
    void onLoaded(Folder folder);
}
