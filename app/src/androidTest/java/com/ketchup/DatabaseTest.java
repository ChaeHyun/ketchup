package com.ketchup;

import android.content.Context;
import android.util.Log;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import com.ketchup.model.AppDatabase;
import com.ketchup.model.task.Task;
import com.ketchup.model.task.TaskDao;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;


public class DatabaseTest {
    private static final String TAG = DatabaseTest.class.getSimpleName();

    private AppDatabase database;
    private TaskDao taskDao;

    private String uuid1 = UUID.randomUUID().toString();
    private String uuid2 = UUID.randomUUID().toString();
    private String title1 = "첫번째 할일";
    private String title2 = "두번째 할일";


    @Before
    public void setup() {
        Log.i(TAG, "[ BeforeTest ] : Preparing database instance and dao.");
        Context context = ApplicationProvider.getApplicationContext();
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        taskDao = database.getTaskDao();
    }

    /**
     * Table 이 비어있을 경우 [READ]를 해도 결과 리스트는 Empty 이어야한다.
     * */
    @Test
    public void selectAllTasks_shouldGetEmptyList() {
        List<Task> task = taskDao.getAllTasks();
        Assert.assertTrue(task.isEmpty());
    }


    /**
     *  [READ] 테스트
     *  2개의 Task 인스턴스를 DB에 삽입하고 2개의 Task 를 읽는 것을 확인한다.
     * */
    @Test
    public void insertData_ThenCheckTotalSize_AndCheckEachValue() {
        Task task1 = new Task(uuid1, title1);
        Task task2 = new Task(uuid2, title2);

        Log.i(TAG, "Insert a couple of Task instances to Task Table.");
        taskDao.insertTask(task1);
        taskDao.insertTask(task2);

        List<Task> taskList = taskDao.getAllTasks();
        assertEquals(2, taskList.size());

        List<Task> check = taskDao.getTasksByTitle(title1);
        assertEquals(task1.getUuid(), check.get(0).getUuid());
        assertEquals(task1.getTitle(), check.get(0).getTitle());

        Task result = taskDao.getTask(uuid2);
        assertEquals(result.getUuid(), task2.getUuid());
        assertEquals(result.getTitle(), task2.getTitle());
    }

    /**
     * [UPDATE] 테스트
     * Task 인스턴스를 삽입하고 해당 Task 의 정보를 갱신하여 확인한다.
     * */
    @Test
    public void updateTitle_ThenShouldTitleUpdated() {
        Task task = new Task(uuid1, title1);
        taskDao.insertTask(task);

        Task check = taskDao.getTask(uuid1);
        assertEquals(check.getUuid(), uuid1);
        assertEquals(check.getTitle(), title1);

        String updatedTitle = "새롭게 변경된 할일";
        task.setTitle(updatedTitle);

        Log.i(TAG, "Update an existing task data with a new Title value.");
        taskDao.updateTask(task);

        check = taskDao.getTask(uuid1);
        assertEquals(check.getUuid(), uuid1);
        assertNotEquals(check.getTitle(), title1);
        assertEquals(check.getTitle(), updatedTitle);
    }

    /**
     * [DELETE] 테스트
     * Task 인스턴스를 추가하고 그 Task 데이터를 삭제한 뒤 확인한다.
     */
    @Test
    public void deleteTask_ThenShouldTaskDeleted() {
        Task task = new Task(uuid1, title1);
        taskDao.insertTask(task);

        Task check = taskDao.getTask(uuid1);
        assertNotNull(check);
        Log.i(TAG, "Data is inserted successfully.");

        taskDao.deleteTask(uuid1);
        Log.i(TAG, "Data is deleted.");

        check = taskDao.getTask(uuid1);
        assertNull(check);

        Log.i(TAG, "TaskTable is empty now.");
        List<Task> tasks = taskDao.getAllTasks();
        assertTrue(tasks.isEmpty());
    }


    @After
    public void finish() throws Exception {
        Log.v(TAG, "[ AfterTest ] : clear db resource");
        database.close();

    }
}
