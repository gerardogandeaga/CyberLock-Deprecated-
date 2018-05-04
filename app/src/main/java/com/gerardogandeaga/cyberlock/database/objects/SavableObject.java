package com.gerardogandeaga.cyberlock.database.objects;

import android.annotation.SuppressLint;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author gerardogandeaga
 */
public abstract class SavableObject implements Serializable {
    Date mDateModified;
    Date mDateCreated;

    boolean mIsNew;

    @SuppressLint("SimpleDateFormat")
    private static final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy 'at' hh:mm aaa");

    public String getDate() {
        return (mDateModified == null ? dateFormat.format(mDateCreated) : dateFormat.format(mDateModified));
    }

    public boolean isNew() {
        return mIsNew;
    }

    // getters
    public long getTimeModified() {
        return mDateModified.getTime();
    }

    public long getTimeCreated() {
        return mDateCreated.getTime();
    }

    // setters
    void setTimeModified(long time) {
        this.mDateModified = new Date(time);
    }

    void setTimeCreated(long time) {
        this.mDateCreated = new Date(time);
    }

    // abstracts

    public abstract boolean isEmpty();
}
