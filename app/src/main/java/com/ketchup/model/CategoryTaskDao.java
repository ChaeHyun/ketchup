package com.ketchup.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CategoryTaskDao {

    // Create
    // Setting Relation b/w [Category] and [Task].
    @Insert
    void insertCategoryTaskRelation(CategoryTaskCrossRef categoryTaskCrossRef);

    // Read
    @Query("SELECT * FROM categorytaskcrossref")
    List<CategoryTaskCrossRef> readAllCategoryTaskRelation();

    // Update

    // Delete
    @Delete
    void deleteCategoryTaskRelation(CategoryTaskCrossRef categoryTaskCrossRef);

    @Query("DELETE FROM categorytaskcrossref WHERE categoryId = :categoryId AND taskId = :taskId")
    void deleteCateogryTaskRelation(String categoryId, String taskId);


}
