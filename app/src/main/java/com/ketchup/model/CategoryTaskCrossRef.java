package com.ketchup.model;

import androidx.room.Entity;

// Junction Table, Associative Table, Join Table
@Entity(primaryKeys = {"categoryId", "taskId"})
public class CategoryTaskCrossRef {
    public String categoryId;
    public String taskId;
}
