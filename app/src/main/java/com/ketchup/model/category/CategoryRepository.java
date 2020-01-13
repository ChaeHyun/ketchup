package com.ketchup.model.category;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface CategoryRepository {
    // Methods to open to the ViewModels.

    // Create
    void insertCategory(Category category);
    void insertCategories(List<Category> categories);

    // Read
    List<Category> getAllCategories();
    List<Category> getCategories(String name);
    Category getCategory(UUID uuid);

    // Update
    void updateCategory(Category category);

    // Delete
    void deleteCategory(UUID uuid);
    void deleteAllCategories();

    String getCategoryId(String name);

    void createRelationWithTask(String categoryName, String taskId, Date dueDate);

    void updateRelationWithTask(String oldCategoryName, String newCategoryName, String taskId, Date dueDate);

    List<CategoryTaskCrossRef> getAllRelation();

    List<CategoryWithTasks> getAllCategoryWithTasksData();

    List<CategoryWithTasks> testCategoryWithTasksWithDate();

    List<CategoryWithTasks> test(String name);
}
