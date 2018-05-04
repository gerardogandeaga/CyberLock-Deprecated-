package com.gerardogandeaga.cyberlock.interfaces;

/**
 * @author gerardogandeaga
 *
 * interface of constant that hold the database keys and positions.
 * String values are database keys, the integer values are their position
 */
public interface DBNoteConstants {

    /**
     * table name for database file
     */
    String TABLE_NOTES = "notes";

    /**
     * date and time when the note was created. this is also the PRIMARY KEY
     */
    String DATE_MODIFIED = "date_modified";
    int POS_DATE_MODIFIED = 0;

    /**
     * date when the note was initially is created
     */
    String DATE_CREATED = "date_created";
    int POS_DATE_CREATED = 1;


    /**
     * numeric boolean for the item being trashed or not
     */
    String TRASHED = "trashed";
    int POS_TRASHED = 2;

    /**
     * folder it is tagged under. the folder system for the time being it more of a filter
     * rather than an actual folder structure
     */
    String FOLDER = "folder";
    int POS_FOLDER = 3;

    /**
     * type of note that is stores ie. TYPE_NOTE, TYPE_PAYMENTINFO, TYPE_LOGININFO
     */
    String TYPE = "type";
    int POS_TYPE = 4;

    /**
     * colour that the note is tagged under
     */
    String COLOUR_TAG = "colour_tag";
    int POS_COLOUR_TAG = 5;

    /**
     * title of the note determined by manual input
     */
    String LABEL = "label";
    int POS_LABEL = 6;

    /**
     * content holds all the text values as a result of manual input
     */
    String CONTENT = "content";
    int POS_CONTENT = 7;
}
