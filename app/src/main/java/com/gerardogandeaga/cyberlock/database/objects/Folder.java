package com.gerardogandeaga.cyberlock.database.objects;

// todo create Folder Object like Note Object

import android.annotation.SuppressLint;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author gerardogandeaga
 */
public class Folder extends SavableObject implements Serializable {
    private Date mDate;
    private String mColourTag;
    private String mName;
    private int mSize;

    @SuppressLint("SimpleDateFormat")
    private static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy 'at' hh:mm aaa");

    public Folder(long time,
                  String colour_tag,
                  String name) {
        this.mDate = new Date(time);
        this.mColourTag = colour_tag;
        this.mName = name;
    }

    public Folder() {
        this.mDate = new Date();
    }

    public String getDate() {
        return dateFormat.format(mDate);
    }

    // getters

    public long getTime() {
        return mDate.getTime();
    }
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

    public Folder withTime(long time) {
        this.mDate = new Date(time);
        return this;
    }
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


    public static class Constants {
        public static final String TRASH = "Trash";
        public static final String ARCHIVE = "Archive";
        public static final String ALL_NOTES = "All Notes";

        public static final Folder TRASH_FOLDER = new Folder()
                .withColourTag("DEFUALT")
                .withName(TRASH);

        public static final Folder ARCHIVE_FOLDER = new Folder()
                .withColourTag("DEFAULT")
                .withName(ARCHIVE);

        public static final Folder ALL_NOTES_FOLDER = new Folder()
                .withColourTag("DEFAULT")
                .withName(ALL_NOTES);

    }
}
