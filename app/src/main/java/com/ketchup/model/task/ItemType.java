package com.ketchup.model.task;

import androidx.room.TypeConverter;

public enum ItemType {
    HEADER(0), CHILD(1);

    private int code;

    ItemType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    @TypeConverter
    public static ItemType getItemType(int value) {
        for (ItemType itemType : values()) {
            if (itemType.getCode() == value) {
                return itemType;
            }
        }
        return null;
    }

    @TypeConverter
    public static Integer getItemTypeInteger(ItemType itemType) {
        if (itemType != null)
            return itemType.getCode();

        return null;
    }
}
