package com.ketchup.model;

import android.graphics.Color;

public enum ColorLabel {

    DEFAULT(215, 204, 200),
    RED(211, 47, 47),
    BLUE(25, 118, 210),
    GREEN(46, 125, 50),
    YELLOW(251, 192, 45),
    PURPLE(123, 31, 162);

    private final Integer red, green, blue;

    ColorLabel(final Integer red, final Integer green, final Integer blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public Integer getRedValue() {
        return red;
    }

    public Integer getGreenValue() {
        return green;
    }

    public Integer getBlueValue() {
        return blue;
    }

    public int getColor() {
        return Color.rgb(red, green, blue);
    }
}
