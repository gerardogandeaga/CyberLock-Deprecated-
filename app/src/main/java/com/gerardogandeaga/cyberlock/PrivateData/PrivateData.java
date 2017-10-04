package com.gerardogandeaga.cyberlock.PrivateData;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PrivateData implements Serializable
{
    private Date mDate;
    private String mPasscode;
    private String mCryptKey;

    private static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy");

    public PrivateData() {
        this.mDate = new Date();
    }

    public PrivateData(long lastlogin, String passcode, String cryptKey) {
        this.mDate = new Date(lastlogin);
        this.mPasscode = passcode;
        this.mCryptKey = cryptKey;
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

    public String getType() {
        return mPasscode;
    }
    public void setType(String mtype) {
        this.mPasscode = mtype;
    }

    public String getCryptKey() {
        return this.mCryptKey;
    }
    public void setCryptKey(String cryptKey) {
        this.mCryptKey = cryptKey;
    }
}
