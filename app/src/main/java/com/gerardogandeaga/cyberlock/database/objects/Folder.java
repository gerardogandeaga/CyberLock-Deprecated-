package com.gerardogandeaga.cyberlock.database.objects;

// todo create Folder Object like Note Object

import java.io.Serializable;
import java.util.Date;

/**
 * @author gerardogandeaga
 */
public class Folder extends SavableObject implements Serializable {
    private String mColourTag;
    private String mName;
    private int mSize;

    public Folder(long modded,
                  long created,
                  String colour_tag,
                  String name) {
        setTimeModified(modded);
        setTimeCreated(created);
        this.mColourTag = colour_tag;
        this.mName = name;
    }

    public Folder() {
        this.mDateCreated = new Date();
    }

    // getters

    public String getColourTag() {
        return mColourTag;
    }
    public String getName() {
        return mName;
    }
    public int getSize() {
        return mSize;
    }

    // setters

    public Folder withColourTag(String colourTag) {
        this.mColourTag = colourTag;
        return this;
    }
    public Folder withName(String name) {
        this.mName = name;
        return this;
    }
    public Folder withSize(int size) {
        this.mSize = size;
        return this;
    }

    @Override
    public boolean isEmpty() {
        return (mColourTag == null && mName == null);
    }

    // default drawer folders
    public static class Constants {
        public static final String TRASH = "Trash";
        public static final String ARCHIVE = "Archive";
        public static final String ALL_NOTES = "All Notes";

        public static final Folder TRASH_FOLDER = new Folder()
                .withColourTag("DEFAULT")
                .withName(TRASH);

        public static final Folder ARCHIVE_FOLDER = new Folder()
                .withColourTag("DEFAULT")
                .withName(ARCHIVE);

        public static final Folder ALL_NOTES_FOLDER = new Folder()
                .withColourTag("DEFAULT")
                .withName(ALL_NOTES);

    }
}
