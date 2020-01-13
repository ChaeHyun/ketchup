package com.ketchup.model.category;

import androidx.annotation.NonNull;

import com.ketchup.AppExecutors;

import java.util.Date;
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
    public void createRelationWithTask(String categoryName, String taskId, Date dueDate) {
        String categoryId = getCategoryId(categoryName);

        relationDao.insertCategoryTaskRelation(new CategoryTaskCrossRef(categoryId, taskId, dueDate));
    }

    @Override
    public void updateRelationWithTask(String oldCategoryName, String newCategoryName, String taskId, Date dueDate) {
        String categoryId = getCategoryId(oldCategoryName);
        relationDao.deleteCategoryTaskRelation(categoryId, taskId);

        categoryId = getCategoryId(newCategoryName);
        relationDao.insertCategoryTaskRelation(new CategoryTaskCrossRef(categoryId, taskId, dueDate));
    }

    @Override
    public List<CategoryTaskCrossRef> getAllRelation() {
        return relationDao.readAllCategoryTaskRelation();
    }


    /* CategoryWithTasks Types */
    @Override
    public List<CategoryWithTasks> getAllCategoryWithTasksData() {
        return dao.getAllCategoryWithTasks();
    }

    @Override
    public List<CategoryWithTasks> testCategoryWithTasksWithDate() {
        return dao.getCategoryWithTasksWithSpecificDate();
    }

    @Override
    public List<CategoryWithTasks> test(String name) {
        return dao.test(name);
    }
}
