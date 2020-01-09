package com.ketchup.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.ketchup.AdapterType;
import com.ketchup.model.task.ItemType;

@Entity(tableName="category")
public class Category implements AdapterType {

    @NonNull
    @PrimaryKey
    private String categoryId;

    private String name;
    // This is supposed to be at CategoryWithTasks.
    private boolean folded = false;

    @TypeConverters(ItemType.class)
    private ItemType itemType = ItemType.HEADER;

    public Category(String categoryId, String name) {
        this.categoryId = categoryId;
        this.name = name;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFolded() {
        return folded;
    }

    public void setFolded(boolean folded) {
        this.folded = folded;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    @Override
    public ItemType getItemType() {
        return itemType;
    }
    /*
    * 카테고리에 필요한 메소드
    * 일단 생성자(카테고리 아이디, 이름)
    * */
}
