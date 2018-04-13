package com.gerardogandeaga.cyberlock.interfaces;

/**
 * @author gerardogandeaga
 *
 * interface of constant that hold the database keys and positions.
 * String values are database keys, the integer values are their position
 */
public interface DBFolderConstants {

    /**
     * table name for the database file
     */
    String TABLE = "folders";

    /**
     * date is the main key for the note database, it organizes the db by most recently updates;
     * date is updated when the note is initially created and whenever it's modified
     */
    String DATE = "date";
    int POS_DATE = 0;

    /**
     * colour that the note is tagged under
     */
    String COLOUR_TAG = "colour_tag";
    int POS_COLOUR_TAG = 1;

    /**
     * name of the folder
     */
    String NAME = "name";
    int POS_NAME = 2;

    /**
     * space in MB that the folder takes
     */
    String SIZE = "size";
    int POS_SIZE = 3;
}
