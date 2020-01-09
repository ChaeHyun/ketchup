package com.ketchup.model;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.ketchup.AdapterType;
import com.ketchup.model.task.ItemType;
import com.ketchup.model.task.Task;

import java.util.ArrayList;
import java.util.List;

public class CategoryWithTasks implements AdapterType {
    @Embedded
    public Category category;
    @Relation(
            parentColumn="categoryId",
            entity = Task.class,
            entityColumn = "uuid",
            associateBy = @Junction(
                value = CategoryTaskCrossRef.class,
                parentColumn = "categoryId",
                entityColumn = "taskId")
    )
    public List<Task> tasks;

    public CategoryWithTasks(Category category, List<Task> tasks) {
        this.category = category;
        this.tasks = tasks;
    }

    public CategoryWithTasks(Category category, List<Task> tasks, boolean folded) {
        this.category = category;
        this.tasks = tasks;
        if (tasks == null)
            this.tasks = new ArrayList<>();

        this.category.setFolded(folded);
    }

    public int getCount() {
        return tasks.size();
    }
    @Override
    public ItemType getItemType() {
        return category.getItemType();
    }
}
