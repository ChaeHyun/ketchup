package com.ketchup.model.category;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
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

    // CategoryWithTasks
    @Transaction
    @Query("SELECT * FROM category")
    List<CategoryWithTasks> getAllCategoryWithTasks();


    // WHERE 이 속한 카테고리가 전부 반환된다.
    @Transaction
    @Query("SELECT * FROM category " +
            "INNER JOIN categorytaskcrossref " +
            "ON category.categoryId = categorytaskcrossref.categoryId " +
            "WHERE categorytaskcrossref.dueDate IS NOT NULL"
    )
    List<CategoryWithTasks> getCategoryWithTasksWithSpecificDate();

    @Transaction
    @Query("SELECT * FROM category WHERE category.name = :categoryName OR category.name = 'uncompleted'")
    List<CategoryWithTasks> test(String categoryName);





}
