package com.ketchup.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import java.util.Date;

// Junction Table, Associative Table, Join Table
@Entity(tableName = "categorytaskcrossref", primaryKeys = {"categoryId", "taskId"})
public class CategoryTaskCrossRef {
    @NonNull
    public String categoryId;
    @NonNull
    public String taskId;
    public Date dueDate;

    public CategoryTaskCrossRef(String categoryId, String taskId, Date dueDate) {
        this.categoryId = categoryId;
        this.taskId = taskId;
        this.dueDate = dueDate;
    }
}
