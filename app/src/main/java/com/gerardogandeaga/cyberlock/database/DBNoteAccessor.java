package com.gerardogandeaga.cyberlock.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.gerardogandeaga.cyberlock.App;
import com.gerardogandeaga.cyberlock.database.objects.Note;
import com.gerardogandeaga.cyberlock.interfaces.DBNoteConstants;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author gerardogandeaga
 *
 * accessor to the Note database
 */
public class DBNoteAccessor implements DBNoteConstants {
    private static final String TAG = "DBNoteAccessor";

    private DatabaseOpenHelper mOpenHelper;
    private static volatile DBNoteAccessor INSTANCE;

    private DBNoteAccessor() {
        this.mOpenHelper = App.getDatabase();
    }

    // class INSTANCE manager
    public static synchronized DBNoteAccessor getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DBNoteAccessor();
        }
        return INSTANCE;
    }

    // database interactions / mods
    public void save(Note note) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DATE_MODIFIED, note.getTimeCreated());
        values.put(DATE_CREATED,  note.getTimeCreated());
        values.put(TRASHED,       note.isTrashed());
        values.put(FOLDER,        note.getFolder());
        values.put(TYPE,          note.getType());
        values.put(COLOUR_TAG,    note.getColourTag());
        values.put(LABEL,         note.getLabel());
        values.put(CONTENT,       note.getContent());

        db.beginTransaction();
        db.insert(TABLE_NOTES, null, values);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void update(Note note) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DATE_MODIFIED, new Date().getTime());
        values.put(TRASHED,       note.isTrashed());
        values.put(FOLDER,        note.getFolder());
        values.put(TYPE,          note.getType());
        values.put(COLOUR_TAG,    note.getColourTag());
        values.put(LABEL,         note.getLabel());
        values.put(CONTENT,       note.getContent());

        String date = Long.toString(note.getTimeModified());
        db.beginTransaction();
        db.update(TABLE_NOTES, values, DATE_MODIFIED + " = ?", new String[]{date});
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void delete(Note note) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        String date = Long.toString(note.getTimeModified());

        db.beginTransaction();
        db.delete(TABLE_NOTES, DATE_MODIFIED + " = ?", new String[]{date});
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    /**
     * get list of all notes
     */
    public List<Note> getAllNotes() {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        List<Note> notes = new ArrayList<>();
        Cursor cursor = getQuery(db);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            notes.add(getNote(cursor));
            cursor.moveToNext();
        }
        cursor.close();

        return notes;
    }

    /**
     * get list of notes in a particular folder
     * @param folder filter
     */
    public List<Note> getAllNotes(String folder) {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        List<Note> notes = new ArrayList<>();
        Cursor cursor = getQuery(db);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            notes.add(getNote(cursor, folder));
            cursor.moveToNext();
        }
        cursor.close();

        return notes;
    }

    /**
     * @param cursor position in db
     * @return regular unfiltered note
     */
    private Note getNote(Cursor cursor) {
        if (!cursor.isAfterLast()) {
            return constructNote(cursor);
        }

        // return null for out of bounds
        return null;
    }

    /**
     * @param cursor position in db
     * @param folder folder filter
     * @return note that pertains to a particular folder
     */
    private Note getNote(Cursor cursor, String folder) {
        if (!cursor.isAfterLast()) {
            Note note = constructNote(cursor);
            if (folder.equals(note.getFolder())) {
                return note;
            } else {
                return new Note();
            }
        }

        return null;
    }

    public List<Note> getTrashedNotes() {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        List<Note> trashedNotes = new ArrayList<>();
        Cursor cursor = getQuery(db);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Note note = constructNote(cursor);
            if (note.isTrashed()) {
                trashedNotes.add(note);
            }
        }

        return trashedNotes;
    }

    public int size() {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        int size = 0;
        Cursor cursor = getQuery(db);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            size++;
            cursor.moveToNext();
        }

        return size;
    }

    // returns a new data package from the cursor position
    private Note constructNote(Cursor cursor) {
        long modded =       cursor.getLong(POS_DATE_MODIFIED);
        long created =      cursor.getLong(POS_DATE_CREATED);
        boolean isTrashed = cursor.getShort(POS_TRASHED) == 1;
        String folder =     cursor.getString(POS_FOLDER);
        String type =       cursor.getString(POS_TYPE);
        String colourTag =  cursor.getString(POS_COLOUR_TAG);
        String label =      cursor.getString(POS_LABEL);
        String content =    cursor.getString(POS_CONTENT);
        // create new note object
        return new Note(modded, created, isTrashed, folder, type, colourTag, label, content);
    }

    /**
     * this function checks if a specific piece of data exists in the
     * database returning a boolean
     */
    public boolean containsNote(Note note) {
        List<Note> notes = getAllNotes();
        for (int i = 0; i < notes.size(); i++) {
            if (note.toString().equals(notes.get(i).toString())) {
                return false;
            }
        }

        return true;
    }

    private Cursor getQuery(SQLiteDatabase db) {
        return db.rawQuery(
                "SELECT * FROM " + TABLE_NOTES + " ORDER BY " + DATE_MODIFIED + " DESC",
                null
        );
    }
}
