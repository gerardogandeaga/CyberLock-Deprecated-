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
     * date when the note was initially is created
     */
    String DATE_CREATED = "date_created";
    int POS_DATE_CREATED = 0;

    /**
     * date and time when the note was created. this is also the PRIMARY KEY
     */
    String DATE_MODIFIED = "date_modified";
    int POS_DATE_MODIFIED = 1;

    /**
     * colour that the note is tagged under
     */
    String COLOUR_TAG = "colour_tag";
    int POS_COLOUR_TAG = 2;

    /**
     * name of the folder
     */
    String NAME = "name";
    int POS_NAME = 3;

}
