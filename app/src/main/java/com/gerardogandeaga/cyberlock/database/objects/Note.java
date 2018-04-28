package com.gerardogandeaga.cyberlock.database.objects;

import android.annotation.SuppressLint;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author gerardogandeaga
 */
public class Note extends SavableObject implements Serializable {
    public static final String NOTE  = "TYPE_NOTE";
    public static final String CARD  = "TYPE_CARD";
    public static final String LOGIN = "TYPE_LOGIN";

    // instance vars
    private Date mDateCreated;
    private Date mDateModified;
    private boolean mIsTrashed;
    private String mFolder;
    private String mType;
    private String mColourTag;
    private String mLabel;
    private String mContent;

    private boolean mIsNew;

    @SuppressLint("SimpleDateFormat")
    private static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy 'at' hh:mm aaa");

    /*
    constructor that builds the data from the database-accessor class when data is first loaded into memory.
    constructor gets called when by the "getAllNotes" function. */
    public Note(long modded,
                long created,
                boolean isTrashed,
                String folder,
                String type,
                String colour_tag,
                String label,
                String content) {
        this.mDateModified = new Date(modded);
        this.mDateCreated = new Date(created);
        this.mIsTrashed = isTrashed;
        this.mFolder = folder;
        this.mType = type;
        this.mColourTag = colour_tag;
        this.mLabel = label;
        this.mContent = content;

        this.mIsNew = false;
    }
    /*
    constructor that initializes a brand new note initialized by the EditActivity class
    when saving data for the first time. */
    public Note() {
        this.mDateCreated = new Date();
        this.mIsNew = true;
        this.mColourTag = "default";
    }

    public String getDate() {
        return (mDateModified == null ? dateFormat.format(mDateCreated) : dateFormat.format(mDateModified));
    }

    // getters

    public long getTimeModified() {
        return mDateModified.getTime();
    }
    public long getTimeCreated() {
        return mDateCreated.getTime();
    }
    public boolean isTrashed() {
        return mIsTrashed;
    }
    public String getFolder() {
        return mFolder;
    }
    public String getType() {
        return mType;
    }
    public String getColourTag() {
        return mColourTag;
    }
    public String getLabel() {
        return mLabel;
    }
    public String getContent() {
        return mContent;
    }

    // setters

    public Note setTimeModified(long time) {
        this.mDateModified = new Date(time);
        return this;
    }
    public Note setTimeCreated(long time) {
        this.mDateCreated = new Date(time);
        return this;
    }
    public Note setIsTrashed(boolean isTrashed) {
        this.mIsTrashed = isTrashed;
        return this;
    }
    public Note setFolder(String folder) {
        this.mFolder = folder;
        return this;
    }
    public Note setType(String type) {
        this.mType = type;
        return this;
    }
    public Note setColourTag(String colourTag) {
        this.mColourTag = colourTag;
        return this;
    }
    public Note setLabel(String label) {
        this.mLabel = label;
        return this;
    }
    public Note setContent(String content) {
        this.mContent = content;
        return this;
    }

    @Override
    public boolean isNew() {
        return mIsNew;
    }

    @Override
    public boolean isEmpty() {
        return (mFolder == null && mType == null && mColourTag == null && mLabel == null && mContent == null);
    }
}
