package com.ketchup.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.ketchup.R;

import javax.inject.Inject;

import timber.log.Timber;

public class ContextCompatUtils {

    @Inject
    Context context;

    @Inject
    public ContextCompatUtils(Context context) {
        Timber.d("ContextCompatUtil is created;");
        this.context = context;
    }

    public int getColor(int resId) {
        return ContextCompat.getColor(context, resId);
    }

    public int getDarkColor(int color) {
        int darkColor = ContextCompat.getColor(context, R.color.colorPrimaryDark);

        if (color == ContextCompat.getColor(context, R.color.defaultGray))
            darkColor = ContextCompat.getColor(context, R.color.defaultGrayDark);
        if (color == ContextCompat.getColor(context, R.color.labelRed))
            darkColor = ContextCompat.getColor(context, R.color.labelRedDark);
        if (color == ContextCompat.getColor(context, R.color.labelBlue))
            darkColor = ContextCompat.getColor(context, R.color.labelBlueDark);
        if (color == ContextCompat.getColor(context, R.color.labelGreen))
            darkColor = ContextCompat.getColor(context, R.color.labelGreenDark);
        if (color == ContextCompat.getColor(context, R.color.labelYellow))
            darkColor = ContextCompat.getColor(context, R.color.labelYellowDark);
        if (color == ContextCompat.getColor(context, R.color.labelPurple))
            darkColor = ContextCompat.getColor(context, R.color.labelPurpleDark);

        return darkColor;
    }

    public int convertButtonBackgroundColorToColorId(int colorLabelId) {
        // Default Color
        int color = ContextCompat.getColor(context, R.color.addItemToolbar);

        switch (colorLabelId) {
            case R.id.label_red :
                color = ContextCompat.getColor(context, R.color.labelRed);
                break;
            case R.id.label_blue:
                color = ContextCompat.getColor(context, R.color.labelBlue);
                break;
            case R.id.label_green:
                color = ContextCompat.getColor(context, R.color.labelGreen);
                break;
            case R.id.label_yellow:
                color = ContextCompat.getColor(context, R.color.labelYellow);
                break;
            case R.id.label_purple:
                color = ContextCompat.getColor(context, R.color.labelPurple);
                break;
        }

        return color;
    }

    public int convertButtonColorToButtonId(int color) {
        if (color == ContextCompat.getColor(context, R.color.labelRed))
            return R.id.label_red;
        if (color == ContextCompat.getColor(context, R.color.labelBlue))
            return R.id.label_blue;
        if (color == ContextCompat.getColor(context, R.color.labelGreen))
            return R.id.label_green;
        if (color == ContextCompat.getColor(context, R.color.labelYellow))
            return R.id.label_yellow;
        if (color == ContextCompat.getColor(context, R.color.labelPurple))
            return R.id.label_purple;
        return -1;
    }

    public Drawable getDrawable(int drawableId) {
        return ContextCompat.getDrawable(context, drawableId);
    }

    public String getStringResource(int resourceId) {
        return context.getString(resourceId);
    }
}
