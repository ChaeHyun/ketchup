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

}
