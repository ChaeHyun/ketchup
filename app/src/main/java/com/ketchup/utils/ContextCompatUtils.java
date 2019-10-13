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

    public int convertButtonBackgroundColorToColorInteger(int colorLabelId) {
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

    public Drawable getDrawable(int drawableId) {
        return ContextCompat.getDrawable(context, drawableId);
    }
}
