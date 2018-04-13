package com.gerardogandeaga.cyberlock.database.objects;

import android.annotation.SuppressLint;
import android.content.Context;

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
    private Date mDate;
    private String mFolder;
    private String mType;
    private String mColourTag;
    private String mLabel;
    private String mContent;

    @SuppressLint("SimpleDateFormat")
    private static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy 'at' hh:mm aaa");

    /*
    constructor that builds the data from the database-accessor class when data is first loaded into memory.
    constructor gets called when by the "getAllNotes" function. */
    public Note(long time,
                String folder,
                String type,
                String colour_tag,
                String label,
                String content) {
        this.mDate = new Date(time);
        this.mFolder = folder;
        this.mType = type;
        this.mColourTag = colour_tag;
        this.mLabel = label;
        this.mContent = content;
    }
    /*
    constructor that initializes a brand new note initialized by the EditActivity class
    when saving data for the first time. */
    public Note() {
        this.mDate = new Date();
    }

    public String getDate() {
        return dateFormat.format(mDate);
    }

    // getters

    public long getTime() {
        return mDate.getTime();
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

    public void setTime(long time) {
        this.mDate = new Date(time);
    }
    public void setFolder(String folder) {
        System.out.println(folder);
        this.mFolder = folder;
    }
    public void setType(String type) {
        System.out.println(type);
        this.mType = type;
    }
    public void setColourTag(String colourTag) {
        System.out.println(colourTag);
        this.mColourTag = colourTag;
    }
    public void setLabel(String label) {
        System.out.println(label);
        this.mLabel = label;
    }
    public void setContent(String content) {
        System.out.println(content);
        this.mContent = content;
    }

    // short text view in the recycler list to load less memory onto a individual textview
    public String getShortNoteText(Context context, String text) {
        float widthSp = (context.getResources().getDisplayMetrics().widthPixels / (((int) 3.5) * context.getResources().getDisplayMetrics().scaledDensity));
        int finalWidth = (int) widthSp;

        String temp = text.replaceAll("\n", " ");
        if (temp.length() > finalWidth) {
            return temp.substring(0, finalWidth) + "...";
        } else {
            return temp;
        }
    }

    @Override
    public boolean isEmpty() {
        return (mFolder == null && mType == null && mColourTag == null && mLabel == null && mContent == null);
    }
}
