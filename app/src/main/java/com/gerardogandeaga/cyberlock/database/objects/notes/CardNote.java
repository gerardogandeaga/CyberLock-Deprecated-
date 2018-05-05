package com.gerardogandeaga.cyberlock.database.objects.notes;

import android.graphics.drawable.Drawable;

import com.gerardogandeaga.cyberlock.App;
import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.database.objects.Note;
import com.gerardogandeaga.cyberlock.utils.Graphics;

import java.util.Scanner;

/**
 * @author gerardogandeaga
 */
public class CardNote extends Note {
    public static final String OTHER =      App.getContext().getString(R.string.str_cardtype_other);
    public static final String VISA =       App.getContext().getString(R.string.str_cardtype_visa);
    public static final String MASTERCARD = App.getContext().getString(R.string.str_cardtype_mastercard);
    public static final String AMEX =       App.getContext().getString(R.string.str_cardtype_amex);
    public static final String DISCOVER =   App.getContext().getString(R.string.str_cardtype_discover);

    private String mHolder;
    private String mNumber;
    private String mCardType;
    private String mExpiry;
    private String mCVV;
    private String mNotes;
    private Drawable mCardImage;

    public CardNote(Note note) {
        super(note);

        if (getContent() != null) {
            String name = "";
            String number = "";
            String cardType = "";
            String expiry = "";
            String cvv = "";
            StringBuilder notes = new StringBuilder();
            //
            Scanner scanner = new Scanner(getContent());
            try {
                name = scanner.nextLine();
                number = scanner.nextLine();
                cardType = scanner.nextLine();
                expiry = scanner.nextLine();
                cvv = scanner.nextLine();
                notes.append(scanner.nextLine());
                while (scanner.hasNextLine()) {
                    notes.append("\n");
                    notes.append(scanner.nextLine());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            scanner.close();

            this.mHolder = name;
            this.mNumber = number;
            this.mCardType = cardType;
            this.mExpiry = expiry;
            this.mCVV = cvv;
            this.mNotes = notes.toString();
            this.mCardImage = getIcon(getContent());
        }
    }

    // getters

    public String getHolder() {
        return mHolder;
    }
    public String getNumber() {
        return mNumber;
    }
    public String getCardType() {
        return (mCardType == null || mCardType.isEmpty() ? OTHER : mCardType);
    }
    public String getExpiry() {
        return mExpiry;
    }
    public String getCVV() {
        return mCVV;
    }
    public String getNotes() {
        return mNotes;
    }
    public Drawable getIcon() {
        return mCardImage;
    }

    // setters

    public CardNote withHolder(String holder) {
        this.mHolder = holder;
        return this;
    }
    public CardNote withNumber(String number) {
        this.mNumber = number;
        return this;
    }
    public CardNote withCardType(String cardType) {
        this.mCardType = cardType;
        return this;
    }
    public CardNote withExpiry(String expiry) {
        this.mExpiry = expiry;
        return this;
    }
    public CardNote withCVV(String cvv) {
        this.mCVV = cvv;
        return this;
    }
    public CardNote withNotes(String notes) {
        this.mNotes = notes;
        return this;
    }
    public CardNote withCardImage(Drawable cardImage) {
        this.mCardImage = cardImage;
        return this;
    }

    // Set card image
    private Drawable getIcon(String content) {
        String cardType;

        Scanner scanner = new Scanner(content);
        while (scanner.hasNextLine()) {
            cardType = scanner.nextLine();

            if (Graphics.CardImages.isCardType(cardType)) {
                return Graphics.CardImages.getCardImage(cardType);
            }
        }

        return null;
    }

    public Note compile() {
        // format content
        final String format = "%s\n%s\n%s\n%s\n%s\n%s";
        withContent(String.format(format, mHolder, mNumber, mCardType, mExpiry, mCVV, mNotes));

        return this;
    }
}
