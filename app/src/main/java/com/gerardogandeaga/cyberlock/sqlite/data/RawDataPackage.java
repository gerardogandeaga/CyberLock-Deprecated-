package com.gerardogandeaga.cyberlock.sqlite.data;

import android.annotation.SuppressLint;
import android.content.Context;

import com.gerardogandeaga.cyberlock.crypto.CryptoContent;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.gerardogandeaga.cyberlock.support.Globals.MASTER_KEY;

public class RawDataPackage implements Serializable {
    private Date mDate;
    private String mType;
    private String mTag;
    private String mLabel;
    private String mContent;

    public RawDataPackage(long time, String type, String tag, String label, String mContent) {
        this.mDate = new Date(time);
        this.mType = type;
        this.mTag = tag;
        this.mLabel = label;
        this.mContent = mContent;
    }

    @SuppressLint("SimpleDateFormat")
    private static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy 'at' hh:mm aaa");
    public RawDataPackage() {
        this.mDate = new Date();
    }

    public String getDate() {
        return dateFormat.format(mDate);
    }
    public long getTime() {
        return mDate.getTime();
    }
    public void setTime(long time) {
        this.mDate = new Date(time);
    }

    public String getType(CryptoContent cryptoContent) {
        return cryptoContent.decrypt(this.mType, MASTER_KEY);
    }
    public String getType() {
        return this.mType;
    }
    public void setType(CryptoContent cryptoContent, String type) {
        this.mType = cryptoContent.encrypt(type, MASTER_KEY);
    }
    public void setType(CryptoContent cryptoContent, String type, String masterKey) {
        this.mType = cryptoContent.decrypt(type, masterKey);
    }

    public String getColourTag(CryptoContent cryptoContent) {
        return cryptoContent.decrypt(this.mTag, MASTER_KEY);
    }
    public String getColourTag() {
        return this.mTag;
    }
    public void setColourTag(CryptoContent cryptoContent, String colour) {
        if (colour != null) {
            mTag = cryptoContent.encrypt(colour, MASTER_KEY);
        } else {
            mTag = cryptoContent.encrypt("DEFAULT", MASTER_KEY);
        }
    }
    public void setColourTag(CryptoContent cryptoContent, String colour, String masterKey) {
        if (colour != null) {
            mTag = cryptoContent.encrypt(colour, masterKey);
        } else {
            mTag = cryptoContent.encrypt("DEFAULT", masterKey);
        }
    }

    public String getLabel(CryptoContent cryptoContent) {
        return cryptoContent.decrypt(this.mLabel, MASTER_KEY);
    }
    public String getLabel() {
        return this.mLabel;
    }
    public void setLabel(CryptoContent cryptoContent, String label) {
        this.mLabel = cryptoContent.encrypt(label, MASTER_KEY);
    }
    public void setLabel(CryptoContent cryptoContent, String label, String masterKey) {
        this.mLabel = cryptoContent.encrypt(label, masterKey);
    }

    public String getContent(CryptoContent cryptoContent) {
        return cryptoContent.decrypt(this.mContent, MASTER_KEY);
    }
    public String getContent() {
        return this.mContent;
    }
    public void setContent(CryptoContent cryptoContent, String content) {
        this.mContent = cryptoContent.encrypt(content, MASTER_KEY);
    }
    public void setContent(CryptoContent cryptoContent, String content, String masterKey) {
        this.mContent = cryptoContent.encrypt(content, masterKey);
    }
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
    public String toString() {
        return this.mContent;
    }
}
