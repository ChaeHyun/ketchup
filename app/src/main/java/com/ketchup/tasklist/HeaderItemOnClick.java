package com.ketchup.tasklist;

import com.ketchup.model.category.CategoryWithTasks;

public interface HeaderItemOnClick {
    void insertChildrenOfHeader(CategoryWithTasks header);
    void removeChildrenOfHeader(CategoryWithTasks header);
}
