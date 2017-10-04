package com.gerardogandeaga.cyberlock.EncryptionFeatures.ContentDatabase;

import android.content.Context;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Data implements Serializable
{
    private Date mDate;
    private String mtype;
    private String mLabel;
    private String mContent;
    private boolean mFullDisplayed;

    private static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy");

    public Data()
    {
        this.mDate = new Date();
    }
    public Data(long time, String type, String label, String mContent)
    {
        this.mDate = new Date(time);
        this.mtype = type;
        this.mLabel = label;
        this.mContent = mContent;
    }

    public String getDate()
    {
        return dateFormat.format(mDate);
    }
    public long getTime()
    {
        return mDate.getTime();
    }
    public void setTime(long time)
    {
        this.mDate = new Date(time);
    }

    public String getType()
    {
        return mtype;
    }
    public void setType(String mtype)
    {
        this.mtype = mtype;
    }

    public String getLabel()
    {
        return this.mLabel;
    }
    public void setLabel(String label)
    {
        this.mLabel = label;
    }

    public String getContent()
    {
        return this.mContent;
    }
    public void setContent(String content)
    {
        this.mContent = content;
    }

    public String getShortText(Context context, String text)
    {
        float widthSp = (context.getResources().getDisplayMetrics().widthPixels / (12 * context.getResources().getDisplayMetrics().scaledDensity));
        int finalWidth = (int) widthSp;

        String temp = text.replaceAll("\n", " ");
        if (temp.length() > finalWidth) {
            return temp.substring(0, finalWidth) + "...";
        } else {
            return temp;
        }
    }

    public void setFullDisplayed(boolean fullDisplayed)
    {
        this.mFullDisplayed = fullDisplayed;
    }
    public boolean isFullDisplayed()
    {
        return this.mFullDisplayed;
    }

    @Override
    public String toString()
    {
        return this.mContent;
    }
}
