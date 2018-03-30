package com.gerardogandeaga.cyberlock.database;

/**
 * @author gerardogandeaga on 2018-03-30.
 *
 * interface of constant that hold the database keys and positions.
 * String values are database keys, the integer values are their position
 */
public interface DBNoteConstants {

    /**
     * date is the main key for the note database, it organizes the db by most recently updates;
     * date is updated when the note is initially created and whenever it's modified
     */
    String DATE = "date";

    int POS_DATE = 0;

    /**
     * folder it is tagged under. the folder system for the time being it more of a filter
     * rather than an actual folder structure
     */
    String FOLDER = "folder";

    int POS_FOLDER = 1;

    /**
     * type of note that is stores ie. TYPE_NOTE, TYPE_PAYMENTINFO, TYPE_LOGININFO
     */
    String TYPE = "type";

    int POS_TYPE = 2;

    /**
     * colour that the note is tagged under
     */
    String COLOUR_TAG = "colour_tag";

    int POS_COLOUR_TAG = 3;

    /**
     * title of the note determined by manual input
     */
    String LABEL = "label";

    int POS_LABEL = 4;

    /**
     * content holds all the text values as a result of manual input
     */
    String CONTENT = "content";

    int POS_CONTENT = 5;
}
