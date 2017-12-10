package com.gerardogandeaga.cyberlock.support.handlers;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.gerardogandeaga.cyberlock.crypto.CryptoContent;
import com.gerardogandeaga.cyberlock.sqlite.data.RawData;
import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.support.adapter.DataItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DataItemHandler {
    private Context mContext;

    public List<DataItem> getDataItems(Context context, List<RawData> rawDataList) {
        this.mContext = context;
        
        // DataItem list as an array
        List<DataItem> dataItemArrayList = new ArrayList<>();

        CryptoContent cc = new CryptoContent(mContext);
        // Iterate through SQLite data list
        for(int i = 0; i < rawDataList.size(); i++) {
            RawData rawData = rawDataList.get(i);

            dataItemArrayList.add(
                    // Get rawData from index
                    new DataItem()
                            .withIdentifier((long) (i + 1))
                            .withData(rawData)
                            .withLabel(rawData.getLabel(cc))
                            .withContent(getUnbindedContent(cc, rawData, rawData.getContent(cc)))
                            .withDate(rawData.getDate())
                            .withTag(getColour(cc, rawData))
                            // If type is paymentinfo
                            .withCardIcon(getCardImage(rawData.getContent(cc)))
            );
        }

        return dataItemArrayList;
    }

    // Deconstruct content strings
    private String getUnbindedContent(CryptoContent cc, RawData rawData, String content) {
        // Return content based on rawData-type
        switch (rawData.getType(cc)) {
            case "TYPE_NOTE":        return rawData.getShortNoteText(mContext, parseNoteContent(content));
            case "TYPE_PAYMENTINFO": return parsePaymentInfoContent(content);
            case "TYPE_LOGININFO":   return parseLoginInfoContent(content);
            default: return null;
        }
    }
    //
    private String parseNoteContent(String content) {
        StringBuilder note;

        Scanner scanner = new Scanner(content);
        note = new StringBuilder(scanner.nextLine());
        while (scanner.hasNextLine()) {
            note.append("\n");
            note.append(scanner.nextLine());
        } 
        scanner.close();

        return note.toString();
    }
    private String parsePaymentInfoContent(String content) {
        String name;
        String number;
        
        Scanner scanner = new Scanner(content);
        name = scanner.nextLine();
        number = scanner.nextLine();
        scanner.close();
        //
        String format = "";
        // Check if strings are empty
        if (!name.isEmpty()) format = name;
        if (!number.isEmpty()) if (format.isEmpty()) format = number; else format += "\n" + number;
        if (format.isEmpty()) format = "No Preview RawData!"; // if there is no preview content

        return format;
    }
    private String parseLoginInfoContent(String content) {
        String url;
        String email;
        String username;
        
        Scanner scanner = new Scanner(content);
        url = scanner.nextLine();
        email = scanner.nextLine();
        username = scanner.nextLine();
        scanner.close();
        //
        String format = "";
        // Check if strings are empty
        if (!url.isEmpty()) format = url;
        if (!email.isEmpty()) { if (format.isEmpty()) format = email; else format += "\n" + email; }
        if (!username.isEmpty()) { if (format.isEmpty()) format = username; else format += "\n" + username; }
        if (format.isEmpty()) format = "No Preview RawData!"; // if there is no preview content

        return format;
    }
    // ---------------------------

    private int getColour(CryptoContent cc, RawData rawData) {
        switch (rawData.getColourTag(cc)) {
            case "COL_BLUE":   return mContext.getResources().getColor(R.color.coltag_blue);
            case "COL_RED":    return mContext.getResources().getColor(R.color.coltag_red);
            case "COL_GREEN":  return mContext.getResources().getColor(R.color.coltag_green);
            case "COL_YELLOW": return mContext.getResources().getColor(R.color.coltag_yellow);
            case "COL_PURPLE": return mContext.getResources().getColor(R.color.coltag_purple);
            case "COL_ORANGE": return mContext.getResources().getColor(R.color.coltag_orange);
            default:           return mContext.getResources().getColor(R.color.coltag_default);
        }
    }
    //
    private Drawable getCardImage(String content) {
        String cardType;
        Drawable factoryIcon = null;

        Scanner scanner = new Scanner(content);
        while (scanner.hasNextLine()) {
            cardType = scanner.nextLine();
            switch (cardType) {
                case ("Visa"):             factoryIcon = mContext.getResources().getDrawable(R.drawable.card_visa); break;
                case ("Master Card"):      factoryIcon = mContext.getResources().getDrawable(R.drawable.card_mastercard); break;
                case ("American Express"): factoryIcon = mContext.getResources().getDrawable(R.drawable.card_americanexpress); break;
                case ("Discover"):         factoryIcon = mContext.getResources().getDrawable(R.drawable.card_discover); break;
                case ("Other"):            factoryIcon = mContext.getResources().getDrawable(R.drawable.card_default); break;
            }
        }

        // Icon scaling
        if (factoryIcon != null) {
            float x = factoryIcon.getMinimumWidth();
            float y = factoryIcon.getMinimumHeight();
            float scaleFactor = 0.65f;
            int xx = (int) ((int) x - (x * scaleFactor));
            int yy = (int) ((int) y - (y * scaleFactor));

            // Apply scaling
            factoryIcon.setBounds(0, 0, xx, yy);
        }

        return factoryIcon;
    }
}
