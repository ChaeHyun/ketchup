package com.ketchup.model;

import androidx.annotation.NonNull;

import com.ketchup.AppExecutors;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import timber.log.Timber;

public class CategoryDataSource implements CategoryRepository {

    private AppExecutors appExecutors;
    private ExecutorService diskIO;
    private CategoryDao dao;

    private CategoryTaskDao relationDao;

    public CategoryDataSource(CategoryDao categoryDao, CategoryTaskDao relationDao, AppExecutors appExecutors) {
        Timber.v("Constructor[CategoryDataSource] : CategoryDao is provided to CategoryDataSource");
        this.dao = categoryDao;
        this.relationDao = relationDao;
        this.appExecutors = appExecutors;
        this.diskIO = appExecutors.diskIO();
    }

    @Override
    public void insertCategory(@NonNull final Category category) {
        diskIO.execute(() -> dao.insertCategory(category));
    }

    @Override
    public void insertCategories(@NonNull final List<Category> categories) {
        diskIO.execute(() -> dao.insertAllCategory(categories));
    }

    @Override
    public List<Category> getAllCategories() {
        return dao.getAllCategories();
    }

    @Override
    public List<Category> getCategories(@NonNull final String name) {
        return dao.getCategoriesByName(name);
    }

    @Override
    public Category getCategory(@NonNull final UUID uuid) {

        Future<Category> result = diskIO.submit(() -> dao.getCategoryById(uuid.toString()));
        try {
            return result.get();
        } catch(Exception ee) {
            ee.printStackTrace();
            return null;
        }
        //return dao.getCategoryById(uuid.toString());
    }

    @Override
    public void updateCategory(@NonNull final Category category) {
        diskIO.execute(() -> dao.updateCategory(category));
    }

    @Override
    public void deleteCategory(@NonNull final UUID uuid) {
        diskIO.execute(() -> dao.deleteCategory(uuid.toString()));
    }

    @Override
    public void deleteAllCategories() {
        diskIO.execute(() -> dao.deleteAllCategories());
    }

    @Override
    public String getCategoryId(String name) {
        return dao.getCategoryId(name);
    }

    /* Below here, Relation */

    @Override
    public void createRelationWithTask(String categoryName, String taskId) {
        String categoryId = getCategoryId(categoryName);

        relationDao.insertCategoryTaskRelation(new CategoryTaskCrossRef(categoryId, taskId));
    }

    @Override
    public void updateRelationWithTask(String oldCategoryName, String newCategoryName, String taskId) {
        String categoryId = getCategoryId(oldCategoryName);
        relationDao.deleteCateogryTaskRelation(categoryId, taskId);

        categoryId = getCategoryId(newCategoryName);
        relationDao.insertCategoryTaskRelation(new CategoryTaskCrossRef(categoryId, taskId));
    }

    @Override
    public List<CategoryTaskCrossRef> getAllRelation() {
        return relationDao.readAllCategoryTaskRelation();
    }
}
