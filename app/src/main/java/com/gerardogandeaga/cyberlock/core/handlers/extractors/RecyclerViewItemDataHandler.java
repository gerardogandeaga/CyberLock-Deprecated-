package com.gerardogandeaga.cyberlock.core.handlers.extractors;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.core.recyclerview.items.RecyclerViewItem;
import com.gerardogandeaga.cyberlock.database.DataPackage;
import com.gerardogandeaga.cyberlock.utils.graphics.ColourTag;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RecyclerViewItemDataHandler {
    private Context mContext;

    public List<RecyclerViewItem> getDataItems(Context context, List<DataPackage> dataPackageList) {
        this.mContext = context;
        
        // RecyclerViewItem list as an array
        List<RecyclerViewItem> recyclerViewItemArrayList = new ArrayList<>();

        // Iterate through SQLite data list
        for(int i = 0; i < dataPackageList.size(); i++) {
            DataPackage dataPackage = dataPackageList.get(i);

            recyclerViewItemArrayList.add(
                    // Get rawDataPackage from index
                    new RecyclerViewItem()
                            .withIdentifier((long) (i + 1))
                            .withRawDataPackage(dataPackage)
                            .withType(dataPackage.getType())

                            .withLabel(dataPackage.getLabel())
                            .withContent(getUnbindedContent(dataPackage, dataPackage.getContent()))
                            .withDate(dataPackage.getDate())
                            .withTag(getColour(dataPackage))
                            // If type is paymentinfo
                            .withCardIcon(getCardImage(dataPackage.getContent()))
            );
        }

        return recyclerViewItemArrayList;
    }

    // Deconstruct content strings
    @Nullable
    private String getUnbindedContent(DataPackage dataPackage, String content) {
        // Return content based on rawDataPackage-type
        switch (dataPackage.getType()) {
            case "TYPE_NOTE":        return dataPackage.getShortNoteText(mContext, parseNoteContent(content));
            case "TYPE_PAYMENTINFO": return parsePaymentInfoContent(content);
            case "TYPE_LOGININFO":   return parseLoginInfoContent(content);
            default: return null;
        }
    }
    //
    @NonNull
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

    private int getColour(DataPackage dataPackage) {
        return ColourTag.colourTagListView(mContext, dataPackage.getTag());
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