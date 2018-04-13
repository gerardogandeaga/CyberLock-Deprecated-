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
    private String mSize;

    @SuppressLint("SimpleDateFormat")
    private static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy 'at' hh:mm aaa");

    public Folder(long time,
                  String colour_tag,
                  String name,
                  String size) {
        this.mDate = new Date(time);
        this.mColourTag = colour_tag;
        this.mName = name;
        this.mSize = size;
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
    public String getSize() {
        return mSize;
    }

    // setters

    public Folder setTime(long time) {
        this.mDate = new Date(time);
        return this;
    }
    public Folder setColourTag(String colourTag) {
        this.mColourTag = colourTag;
        return this;
    }
    public Folder setName(String name) {
        this.mName = name;
        return this;
    }
    public Folder setSize(String size) {
        this.mSize = size;
        return this;
    }

    @Override
    public boolean isEmpty() {
        return (mColourTag == null && mName == null && mSize == null);
    }
}
