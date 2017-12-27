package com.gerardogandeaga.cyberlock.support.handlers.extractors;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.gerardogandeaga.cyberlock.crypto.CryptoContent;
import com.gerardogandeaga.cyberlock.sqlite.data.RawDataPackage;
import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.support.adapter.DataItemView;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RawDataListItemHandler {
    private Context mContext;

    public List<DataItemView> getDataItems(Context context, List<RawDataPackage> rawDataPackageList) {
        this.mContext = context;
        
        // DataItemView list as an array
        List<DataItemView> dataItemViewArrayList = new ArrayList<>();

        CryptoContent cc = new CryptoContent(mContext);
        // Iterate through SQLite data list
        for(int i = 0; i < rawDataPackageList.size(); i++) {
            RawDataPackage rawDataPackage = rawDataPackageList.get(i);

            dataItemViewArrayList.add(
                    // Get rawDataPackage from index
                    new DataItemView()
                            .withIdentifier((long) (i + 1))
                            .withRawDataPackage(rawDataPackage)
                            .withType(rawDataPackage.getType(cc))

                            .withLabel(rawDataPackage.getLabel(cc))
                            .withContent(getUnbindedContent(cc, rawDataPackage, rawDataPackage.getContent(cc)))
                            .withDate(rawDataPackage.getDate())
                            .withTag(getColour(cc, rawDataPackage))
                            // If type is paymentinfo
                            .withCardIcon(getCardImage(rawDataPackage.getContent(cc)))
            );
        }

        return dataItemViewArrayList;
    }

    // Deconstruct content strings
    private String getUnbindedContent(CryptoContent cc, RawDataPackage rawDataPackage, String content) {
        // Return content based on rawDataPackage-type
        switch (rawDataPackage.getType(cc)) {
            case "TYPE_NOTE":        return rawDataPackage.getShortNoteText(mContext, parseNoteContent(content));
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
        String holder;
        String number;
        
        Scanner scanner = new Scanner(content);
        holder = scanner.nextLine();
        number = scanner.nextLine();
        scanner.close();

        final String format = "%s\n%s";
        return String.format(format, holder, censorNumber(number));
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

        final String format = "%s\n%s\n%s";
        return String.format(format, url, email, username);
    }
    // ---------------------------

    private int getColour(CryptoContent cc, RawDataPackage rawDataPackage) {
        switch (rawDataPackage.getColourTag(cc)) {
            case "COL_BLUE":   return mContext.getResources().getColor(R.color.ct_blue);
            case "COL_RED":    return mContext.getResources().getColor(R.color.ct_red);
            case "COL_GREEN":  return mContext.getResources().getColor(R.color.ct_green);
            case "COL_YELLOW": return mContext.getResources().getColor(R.color.ct_yellow);
            case "COL_PURPLE": return mContext.getResources().getColor(R.color.ct_purple);
            case "COL_ORANGE": return mContext.getResources().getColor(R.color.ct_orange);
            default:           return mContext.getResources().getColor(R.color.ct_default);
        }
    }

    private String censorNumber(String number) {
        if (number.length() > 4) {
            StringBuilder tmp = new StringBuilder();
            for (int i = 0; i < number.length(); i++) {
                if (i == (number.length() - 4)) {
                    return tmp.append(number.substring(i, number.length())).toString();
                }
                tmp.append("*");
            }
        }
        return number;
    }

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
