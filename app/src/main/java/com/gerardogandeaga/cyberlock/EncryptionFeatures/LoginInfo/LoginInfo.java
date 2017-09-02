package com.gerardogandeaga.cyberlock.EncryptionFeatures.LoginInfo;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoginInfo implements Serializable
{
    private Date date;
    private String label, url, username, email, password, notes;
    private boolean fullDisplayed;

    private static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy");

    public LoginInfo()
    {
        this.date = new Date();
    }

    public LoginInfo(long time, String label, String url, String username, String email, String password, String notes)
    {
        this.date = new Date(time);
        this.label = label;
        this.url = url;
        this.username = username;
        this.email = email;
        this.password = password;
        this.notes = notes;
    }

    public String getDate()
    {
        return dateFormat.format(date);
    }
    public long getTime()
    {
        return date.getTime();
    }

    public String getLabel()
    {
        return this.label;
    }
    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getUrl()
    {
        return this.url;
    }
    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getUsername()
    {
        return this.username;
    }
    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getEmail()
    {
        return this.email;
    }
    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPassword()
    {
        return this.password;
    }
    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getNotes()
    {
        return this.notes;
    }
    public void setNotes(String notes)
    {
        this.notes = notes;
    }

//    public Drawable setImageButton(LoginInfo loginInfo)
//    {
//        byte[] imageByteVal = loginInfo.getImage();
//        Bitmap imageBitMap = BitmapFactory.decodeByteArray(imageByteVal, 0, imageByteVal.length);
//
//        return new BitmapDrawable(imageBitMap);
//    }

    public void setFullDisplayed(boolean fullDisplayed)
    {
        this.fullDisplayed = fullDisplayed;
    }
    public boolean isFullDisplayed()
    {
        return this.fullDisplayed;
    }
}
