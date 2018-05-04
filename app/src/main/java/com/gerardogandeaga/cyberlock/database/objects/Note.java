package com.gerardogandeaga.cyberlock.database.objects;

import com.gerardogandeaga.cyberlock.database.objects.notes.CardNote;
import com.gerardogandeaga.cyberlock.database.objects.notes.GenericNote;
import com.gerardogandeaga.cyberlock.database.objects.notes.LoginNote;

import java.util.Date;

/**
 * @author gerardogandeaga
 */
public class Note extends SavableObject {
    public static final String GENERIC = "TYPE_NOTE";
    public static final String CARD  = "TYPE_CARD";
    public static final String LOGIN = "TYPE_LOGIN";

    // instance vars
    private boolean mIsTrashed;
    private String mFolder;
    private String mType;
    private String mColourTag;
    private String mLabel;
    private String mContent;

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
        setTimeModified(modded);
        setTimeCreated(created);
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
        this.mColourTag = "default";
        this.mIsNew = true;
    }

    /**
     * this is mre of copy constructor which allows types of notes to be cast back into
     * their general note form
     * @param note note which created a type note
     */
    public Note(Note note) {
        if (!note.isEmpty()) {
            setTimeModified(note.getTimeModified());
        }
        setTimeCreated(note.getTimeCreated());
        this.mIsTrashed = note.isTrashed();
        this.mFolder = note.getFolder();
        this.mType = note.getType();
        this.mColourTag = note.getColourTag();
        this.mLabel = note.getLabel();
        this.mContent = note.getContent();
        this.mIsNew = note.isNew();
    }

    // getters

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

    public Note withIsTrashed(boolean isTrashed) {
        this.mIsTrashed = isTrashed;
        return this;
    }
    public Note withFolder(String folder) {
        this.mFolder = folder;
        return this;
    }
    public Note withType(String type) {
        this.mType = type;
        return this;
    }
    public Note withColourTag(String colourTag) {
        this.mColourTag = colourTag;
        return this;
    }
    public Note withLabel(String label) {
        this.mLabel = label;
        return this;
    }
    public Note withContent(String content) {
        this.mContent = content;
        return this;
    }

    @Override
    public boolean isEmpty() {
        return (mLabel == null && mContent == null);
    }

    public GenericNote getGenericNote() {
        return new GenericNote(this);
    }

    public CardNote getCardNote() {
        return new CardNote(this);
    }

    public LoginNote getLoginNote() {
        return new LoginNote(this);
    }
}
