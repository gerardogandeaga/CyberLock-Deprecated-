package com.gerardogandeaga.cyberlock.EncryptionFeatures.PaymentInfo;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PaymentInfo implements Serializable
{
    private Date date;
    private static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy");

    private String label, cardName, cardNumber, cardExpire, cardSecCode, notes, cardType, question1, question2, answer1, answer2;
    private boolean fullDisplayed;


    public PaymentInfo() { this.date = new Date(); }

    public PaymentInfo(long time, String label, String cardName, String cardNumber, String cardExpire, String cardSecCode, String notes, String cardType, String question1, String question2, String answer1, String answer2)
    {
        this.date = new Date(time);
        this.label = label;
        this.cardName = cardName;
        this.cardNumber = cardNumber;
        this.cardExpire = cardExpire;
        this.cardSecCode = cardSecCode;
        this.notes = notes;
        this.cardType = cardType;
        this.question1 = question1;
        this.question2 = question2;
        this.answer1 = answer1;
        this.answer2 = answer2;
    }

    public String getDate() { return dateFormat.format(date); }
    public long getTime() { return date.getTime(); }

    public String getLabel() {
        return this.label;
    }
    public void setLabel(String label) { this.label = label; }

    public String getCardName() { return this.cardName; }
    public void setCardName(String cardName) { this.cardName = cardName; }

    public String getCardNumber() { return this.cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getCardExpire() { return this.cardExpire; }
    public void setCardExpire(String cardExpire) { this.cardExpire = cardExpire; }

    public String getCardSecCode() { return this.cardSecCode; }
    public void setCardSecCode(String cardSecCode) { this.cardSecCode = cardSecCode; }

    public String getNotes() { return this.notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getCardType() { return this.cardType; }
    public void setCardType(String cardType) { this.cardType = cardType; }

    public String getQuestion1() { return question1; }
    public void setQuestion1(String question1) { this.question1 = question1; }
    public String getQuestion2() { return question2; }
    public void setQuestion2(String question2) { this.question2 = question2; }

    public String getAnswer1() { return answer1; }
    public void setAnswer1(String answer1) { this.answer1 = answer1; }
    public String getAnswer2() { return answer2; }
    public void setAnswer2(String answer2) { this.answer2 = answer2; }

    public String getShortLabel()
    {
        String temp = label.replaceAll("\n", " ");
        if (temp.length() > 100)
        {
            return temp.substring(0, 100) + "...";
        } else {
            return temp;
        }
    }

    public void setFullDisplayed(boolean fullDisplayed) { this.fullDisplayed = fullDisplayed; }
    public boolean isFullDisplayed() { return this.fullDisplayed; }
}
