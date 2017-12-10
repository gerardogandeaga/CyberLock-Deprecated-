package com.gerardogandeaga.cyberlock.sqlite.data;

import android.content.Context;

import com.gerardogandeaga.cyberlock.crypto.CryptoContent;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.gerardogandeaga.cyberlock.support.Globals.MASTER_KEY;

public class RawData implements Serializable {
    private Date mDate;
    private String mType;
    private String mColour;
    private String mLabel;
    private String mContent;

    private boolean mFullDisplayed;
    private boolean mSelected;

    public RawData(long time, String type, String colour, String label, String mContent) {
        this.mDate = new Date(time);
        this.mType = type;
        this.mColour = colour;
        this.mLabel = label;
        this.mContent = mContent;
    }

    private static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy 'at' hh:mm aaa");
    public RawData() {
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
        return cryptoContent.DECRYPT_CONTENT(this.mType, MASTER_KEY);
    }
    public String getType() {
        return this.mType;
    }
    public void setType(CryptoContent cryptoContent, String type) {
        this.mType = cryptoContent.ENCRYPT_CONTENT(type, MASTER_KEY);
    }
    public void setType(CryptoContent cryptoContent, String type, String masterKey) {
        this.mType = cryptoContent.ENCRYPT_CONTENT(type, masterKey);
    }

    public String getColourTag(CryptoContent cryptoContent) {
        return cryptoContent.DECRYPT_CONTENT(this.mColour, MASTER_KEY);
    }
    public String getColourTag() {
        return this.mColour;
    }
    public void setColourTag(CryptoContent cryptoContent, String colour) {
        if (colour != null) {
            mColour = cryptoContent.ENCRYPT_CONTENT(colour, MASTER_KEY);
        } else {
            mColour = cryptoContent.ENCRYPT_CONTENT("DEFAULT", MASTER_KEY);
        }
    }
    public void setColourTag(CryptoContent cryptoContent, String colour, String masterKey) {
        if (colour != null) {
            mColour = cryptoContent.ENCRYPT_CONTENT(colour, masterKey);
        } else {
            mColour = cryptoContent.ENCRYPT_CONTENT("DEFAULT", masterKey);
        }
    }

    public String getLabel(CryptoContent cryptoContent) {
        return cryptoContent.DECRYPT_CONTENT(this.mLabel, MASTER_KEY);
    }
    public String getLabel() {
        return this.mLabel;
    }
    public void setLabel(CryptoContent cryptoContent, String label) {
        this.mLabel = cryptoContent.ENCRYPT_CONTENT(label, MASTER_KEY);
    }
    public void setLabel(CryptoContent cryptoContent, String label, String masterKey) {
        this.mLabel = cryptoContent.ENCRYPT_CONTENT(label, masterKey);
    }

    public String getContent(CryptoContent cryptoContent) {
        return cryptoContent.DECRYPT_CONTENT(this.mContent, MASTER_KEY);
    }
    public String getContent() {
        return this.mContent;
    }
    public void setContent(CryptoContent cryptoContent, String content) {
        this.mContent = cryptoContent.ENCRYPT_CONTENT(content, MASTER_KEY);
    }
    public void setContent(CryptoContent cryptoContent, String content, String masterKey) {
        this.mContent = cryptoContent.ENCRYPT_CONTENT(content, masterKey);
    }
    public String getShortNoteText(Context context, String text) {
        float widthSp = (context.getResources().getDisplayMetrics().widthPixels / (4 * context.getResources().getDisplayMetrics().scaledDensity));
        int finalWidth = (int) widthSp;

        String temp = text.replaceAll("\n", " ");
        if (temp.length() > finalWidth) {
            return temp.substring(0, finalWidth) + "...";
        } else {
            return temp;
        }
    }

    public String getShortText(Context context, String text) {
        float widthSp = (context.getResources().getDisplayMetrics().widthPixels / (20 * context.getResources().getDisplayMetrics().scaledDensity));
        int finalWidth = (int) widthSp;

        String temp = text.replaceAll("\n", " ");
        if (temp.length() > finalWidth) {
            return temp.substring(0, finalWidth) + "...";
        } else {
            return temp;
        }
    }

    public void setSelected(boolean selected) {
        this.mSelected = selected;
    }
    public boolean isSelected() {
        return this.mSelected;
    }

    @Override
    public String toString() {
        return this.mContent;
    }
}
