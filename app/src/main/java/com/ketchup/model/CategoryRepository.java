package com.ketchup.model;

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

    void createRelationWithTask(String categoryName, String taskId);

    void updateRelationWithTask(String oldCategoryName, String newCategoryName, String taskId);

    List<CategoryTaskCrossRef> getAllRelation();
}
