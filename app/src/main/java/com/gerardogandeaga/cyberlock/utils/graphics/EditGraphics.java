package com.gerardogandeaga.cyberlock.utils.graphics;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.database.DataPackage;

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

    @Contract(pure = true)
    public String getColourId(Boolean isNew, DataPackage dataPackage) {
        if (isNew) {
            if (this.mColor != null) {
                return this.mColor;
            } else {
                return "DEFAULT";
            }
        } else {
            if (this.mColor == null) {
                return dataPackage.getTag();
            } else {
                return this.mColor;
            }
        }
    }
}
