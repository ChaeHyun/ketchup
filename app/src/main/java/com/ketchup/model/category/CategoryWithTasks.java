package com.ketchup.model.category;

import androidx.room.Embedded;
import androidx.room.Ignore;
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
    @Ignore
    private boolean folded;

    public CategoryWithTasks(Category category, List<Task> tasks) {
        this.category = category;
        this.tasks = tasks;
        this.folded = (getCount() == 0);
    }

    @Ignore
    public CategoryWithTasks(Category category, List<Task> tasks, boolean folded) {
        this.category = category;
        this.tasks = tasks;
        if (tasks == null)
            this.tasks = new ArrayList<>();

        this.folded = folded;
    }

    public boolean isFolded() {
        return folded;
    }

    public void setFolded(boolean folded) {
        this.folded = folded;
    }

    public int getCount() {
        return (tasks == null) ? 0 : tasks.size();
    }
    @Override
    public ItemType getItemType() {
        return category.getItemType();
    }
}
