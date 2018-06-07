package com.gerardogandeaga.cyberlock.interfaces;

/**
 * @author gerardogandeaga
 */
public interface AdapterLoaderCallback {

    /**
     * gets called when the adapter loader is finished loading the data and adding
     * items to the adapter
     *
     * @param folderName name of folder that has been loaded by the loader
     * @param folderSize how many item will be displayed in the adapter
     */
    void onNoteItemsLoaded(String folderName, int folderSize);
}
