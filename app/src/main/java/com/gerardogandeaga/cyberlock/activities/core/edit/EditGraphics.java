package com.gerardogandeaga.cyberlock.activities.core.edit;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.crypto.CryptoContent;
import com.gerardogandeaga.cyberlock.sqlite.data.RawDataPackage;

import org.jetbrains.annotations.Contract;

public class EditGraphics {
    private Context mContext;

    // Colour tag
    private String mColor;

    // Paymentinfo

    public EditGraphics(Context context) {
        this.mContext = context;
    }

    // Paymentinfo
    public Drawable getCardImage(String args) {
        Drawable factoryIcon = null;
        switch (args) {
            case ("Visa"):             factoryIcon = mContext.getResources().getDrawable(R.drawable.card_visa); break;
            case ("Master Card"):      factoryIcon = mContext.getResources().getDrawable(R.drawable.card_mastercard); break;
            case ("American Express"): factoryIcon = mContext.getResources().getDrawable(R.drawable.card_americanexpress); break;
            case ("Discover"):         factoryIcon = mContext.getResources().getDrawable(R.drawable.card_discover); break;
            case ("Other"):            factoryIcon = mContext.getResources().getDrawable(R.drawable.card_default); break;
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








    // Colour tag
    public void alterTagColour(ImageView imageView, String args) {
        imageView.setColorFilter(getColour(mContext, args), PorterDuff.Mode.SRC_ATOP);
    }

    private int getColour(Context context, String args) {
        Resources resources = context.getResources();
        this.mColor = args;
        switch (args) {
            case "COL_BLUE":   return resources.getColor(R.color.ct_blue);
            case "COL_RED":    return resources.getColor(R.color.ct_red);
            case "COL_GREEN":  return resources.getColor(R.color.ct_green);
            case "COL_YELLOW": return resources.getColor(R.color.ct_yellow);
            case "COL_PURPLE": return resources.getColor(R.color.ct_purple);
            case "COL_ORANGE": return resources.getColor(R.color.ct_orange);
            default:           return resources.getColor(R.color.black);
        }
    }

    @Contract(pure = true)
    public String getColourId(Boolean isNew, RawDataPackage rawDataPackage, CryptoContent cc) {
        if (isNew) {
            if (this.mColor != null) {
                return this.mColor;
            } else {
                return "DEFAULT";
            }
        } else {
            if (this.mColor == null) {
                return rawDataPackage.getColourTag(cc);
            } else {
                return this.mColor;
            }
        }
    }
    // ----------
}
