package com.gerardogandeaga.cyberlock.sqlite.data;

import android.annotation.SuppressLint;
import android.content.Context;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataPackage implements Serializable {
    private Date mDate;
    private String mType;
    private String mTag;
    private String mLabel;
    private String mContent;

    /* constructor that builds the data from the database-accessor class when data is first loaded into memory.
       constructor gets called when by the "getAllData" function.
     */
    public DataPackage(long time, String type, String tag, String label, String mContent) {
        this.mDate = new Date(time);
        this.mType = type;
        this.mTag = tag;
        this.mLabel = label;
        this.mContent = mContent;
    }
    /* constructor that initializes a brand new data object initialized by the AcitivtyEdit class
       when saving data for the first time.
     */

    public DataPackage() {
        this.mDate = new Date();
    }

    @SuppressLint("SimpleDateFormat")
    private static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy 'at' hh:mm aaa");

    public String getDate() {
        return dateFormat.format(mDate);
    }

    // getters
    public long getTime() {
        return mDate.getTime();
    }
    public String getType() {
        return this.mType;
    }
    public String getTag() {
        return this.mTag;
    }
    public String getLabel() {
        return this.mLabel;
    }
    public String getContent() {
        return this.mContent;
    }
    // setters
    public void setTime(long time) {
        this.mDate = new Date(time);
    }
    public void setType(String type) {
        System.out.println(type);
        this.mType = type;
    }
    public void setTag(String tag) {
        System.out.println(tag);
        this.mTag = tag;
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
}
