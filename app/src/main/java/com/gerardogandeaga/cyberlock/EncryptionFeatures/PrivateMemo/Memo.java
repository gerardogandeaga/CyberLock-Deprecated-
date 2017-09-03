package com.gerardogandeaga.cyberlock.EncryptionFeatures.PrivateMemo;

import android.content.Context;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Memo implements Serializable
{
    private Date date;
    private String text;
    private String label;
    private boolean fullDisplayed;

    private static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy");

    public Memo()
    {
        this.date = new Date();
    }
    public Memo(long time, String label, String text)
    {
        this.date = new Date(time);
        this.label = label;
        this.text = text;
    }

    public String getDate()
    {
        return dateFormat.format(date);
    }

    public String getLabel()
    {
        return this.label;
    }
    public void setLabel(String label)
    {
        this.label = label;
    }

    public long getTime()
    {
        return date.getTime();
    }
    public void setTime(long time)
    {
        this.date = new Date(time);
    }

    public String getText()
    {
        return this.text;
    }
    public void setText(String text)
    {
        this.text = text;
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
        this.fullDisplayed = fullDisplayed;
    }
    public boolean isFullDisplayed()
    {
        return this.fullDisplayed;
    }

    @Override
    public String toString()
    {
        return this.text;
    }
}
