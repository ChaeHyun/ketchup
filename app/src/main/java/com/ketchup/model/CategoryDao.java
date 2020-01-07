package com.ketchup.model;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;


@Dao
public interface CategoryDao {

    // Create
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCategory(@NonNull final Category category);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllCategory(@NonNull final List<Category> categories);

    // Read
    @Query("SELECT * FROM category")
    List<Category> getAllCategories();

    @Query("SELECT * FROM category WHERE name = :name")
    List<Category> getCategoriesByName(@NonNull final String name);

    @Query("SELECT * FROM category WHERE categoryId = :categoryId")
    Category getCategoryById(@NonNull final String categoryId);


    // Update
    @Update
    void updateCategory(@NonNull final  Category category);

    // Delete
    @Query("DELETE FROM category")
    void deleteAllCategories();

    @Query("DELETE FROM category WHERE categoryId = :categoryId")
    void deleteCategory(@NonNull final String categoryId);

    // get Id by using Name
    @Query("SELECT categoryId FROM category WHERE name = :name")
    String getCategoryId(String name);

}
