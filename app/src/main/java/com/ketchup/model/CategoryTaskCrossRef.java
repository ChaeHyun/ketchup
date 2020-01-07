package com.ketchup.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;

// Junction Table, Associative Table, Join Table
@Entity(tableName = "categorytaskcrossref", primaryKeys = {"categoryId", "taskId"})
public class CategoryTaskCrossRef {
    @NonNull
    public String categoryId;
    @NonNull
    public String taskId;

    public CategoryTaskCrossRef(String categoryId, String taskId) {
        this.categoryId = categoryId;
        this.taskId = taskId;
    }
}
