package com.gerardogandeaga.cyberlock.EncryptBook.PrivateMemo;

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
