package com.ketchup;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.ketchup.model.task.Task;
import com.ketchup.tasklist.TaskListViewModel;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

public class TaskListViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private TaskListViewModel viewModel;
    private FakeTaskRepository repository;
    private Task newTask = new Task(UUID.randomUUID().toString(), "Newly added Task");
    private UUID saved = UUID.randomUUID();

    @Before
    public void setup() {
        Timber.d("[setup()] : 3 Tasks are created and inserted in FakeRepository.");
        Task task1 = new Task(saved.toString(), "title1");
        Task task2 = new Task(UUID.randomUUID().toString(), "title2", true);
        Task task3 = new Task(UUID.randomUUID().toString(), "title3", true);

        HashMap<String, Task> data = new LinkedHashMap<>();
        data.put(task1.getUuid(), task1);
        data.put(task2.getUuid(), task2);
        data.put(task3.getUuid(), task3);
        repository = new FakeTaskRepository(data);

        // FakeRepository 를 이용해서 ViewModel 인스턴스를 생성한다.
        viewModel = new TaskListViewModel(repository);
    }

    @After
    public void finish() {
        repository.data.clear();
    }

    @Test
    public void loadTasks_ShouldReturnTasks() {
        // [Given] : setUp(), DB에 3개의 Task 데이터가 입력 되어있다.

        // [When] : ViewModel runs loadTasks() method.
        viewModel.loadTasks();
        try {
            // [Then] : LiveData<Boolean> loading should be false to notify the loading is done.
            boolean loading = LiveDataTestUtil.getValue(viewModel.getLoading());
            Timber.d("loading is " + loading);
            Assert.assertFalse(loading);

            // [Then] : loadTasks() 메소드는 Task Table에 있는 모든 Task를 리스트로 반환한다.
            // Now there are 3 data in DB. Check the size of the retrieved data.
            List<Task> result = LiveDataTestUtil.getValue(viewModel.getTasks());
            Assert.assertEquals(3,  result.size());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadTaskByUuid_ThenShouldReturnTask() {
        // [Given] : setUp(), DB에 3개의 Task 데이터가 입력 되어있다.
        // Title == "title1"인 Task 의 UUID 는 생성할때 saved 변수에 보관되어 있다.
        String savedUUID = saved.toString();

        // [When] : 저장해놓은 UUID를 조건으로 Task를 불러온다.
        viewModel.loadTaskByUuid(savedUUID);
        try {
            // [Then] : UUID는 PrimaryKey이므로 하나의 Task만 반환된다.
            // viewModel에서 불러온 Task의 uuid가 savedUUID 와 같아야한다.
            Task result = LiveDataTestUtil.getValue(viewModel.getTask());
            Assert.assertEquals(savedUUID, result.getUuid());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadTaskByTitle_ThenShouldReturnTask() {
        // [Given] : setUp(), DB에 3개의 Task 데이터가 입력 되어있다.
        // 우리가 검색하고자 하는 Task 의 title 이름은 targetTitle 이다.
        // 같은 이름의 "title2"를 가진 Task를 하나 더 추가한다.
        String targetTitle = "title2";
        viewModel.insertTask(new Task(UUID.randomUUID().toString(), targetTitle, true));

        // [When] : targetTitle을 가진 Task 데이터를 불러온다
        viewModel.loadTasksByTitle(targetTitle);
        try {
            // [Then] : 검색결과인 Result 의 title은 targetTitle과 동일해야한다.
            // "title2"인 데이터의 수는 2여야 한다.
            List<Task> result = LiveDataTestUtil.getValue(viewModel.getTasks());
            Assert.assertEquals(2, result.size());
            for (Task t : result) {
                Assert.assertEquals(targetTitle, t.getTitle());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadTasksCompleted_ThenShouldReturnTasksOnlyCompleted() {
        // [Given] : setUp(), DB에 3개의 Task 데이터가 입력 되어있다.
        // 3개 중에 2개의 Task가 Completed이다.
        try {
            // [Given] : 기존 저장된 Task를 불러와서 completed == true로 변경 후 갱신한다.
            viewModel.loadTaskByUuid(saved.toString());
            Task updatingTarget = LiveDataTestUtil.getValue(viewModel.getTask());
            updatingTarget.setCompleted(true);

            viewModel.updateTask(updatingTarget);

            // [When] : Completed == true 인 Task만 불러온다.
            viewModel.loadTasksCompleted(true);

            // [Then] : 불러온 결과의 크기가 3여야 한다.
            List<Task> result = LiveDataTestUtil.getValue(viewModel.getTasks());
            Assert.assertEquals(3, result.size());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void insertTask_ThenShouldAddNewTask() {
        // [Given] : setUp(), DB에 3개의 Task 데이터가 입력 되어있다.
        try {
            // [When] : 새로운 데이터인 newTask 를 DB에 삽입한다.
            viewModel.insertTask(newTask);

            // [Then] :  After insert a new task, the total data size should be 4.
            viewModel.loadTasks();
            List<Task> tasks = LiveDataTestUtil.getValue(viewModel.getTasks());
            Assert.assertEquals(4, tasks.size());


            // [Then] : 새롭게 추가한 newTask가 올바르게 삽입되었는지 확인하기 위해서.
            // newTask.uuid로 검색해서 반환된 Task 는 newTask 와 동일한 값을 가져야한다.
            viewModel.loadTaskByUuid(newTask.getUuid());
            Task result = LiveDataTestUtil.getValue(viewModel.getTask());
            Assert.assertEquals(newTask, result);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void insertTasks_ThenShouldAddMultipleTasks() {
        // [Given] : setUp(), DB에 3개의 Task 데이터가 입력 되어있다.
        // 2개의 Task를 새롭게 만든다.
        List<Task> taskList = new ArrayList<>();
        taskList.add(new Task(UUID.randomUUID().toString(), "New 1", false));
        taskList.add(new Task(UUID.randomUUID().toString(), "New 2", false));

        // [When] : taskList 를 한번에 insert 한다.
        viewModel.insertTasks(taskList);

        // [Then] : 전체 Task 데이터를 불러왔을 때 크기가 5여야 한다.
        viewModel.loadTasks();
        try {
            List<Task> result = LiveDataTestUtil.getValue(viewModel.getTasks());
            Assert.assertEquals(5, result.size());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void updateTask_ThenShouldRenewExistingTask() {
        // [Given] : setUp(), DB에 3개의 Task 데이터가 입력 되어있다.
        String savedUuid = saved.toString();
        String changeTitle = "Changed";

        Task update = new Task(savedUuid, changeTitle);

        // [When] : saved UUID를 갖고 있는 Task의 title을 업데이트한다.
        viewModel.updateTask(update);

        // [Then] : 기존 Task를 업데이트하므로 기존 Task의 size는 그대로 3이여야하고
        // savedUUID를 가진 Task의 title을 변경되어야 한다.
        viewModel.loadTasks();
        try {
            List<Task> result = LiveDataTestUtil.getValue(viewModel.getTasks());
            Assert.assertEquals(3,  result.size());

            viewModel.loadTaskByUuid(savedUuid);
            Task resultTask = LiveDataTestUtil.getValue(viewModel.getTask());
            //Assert.assertNull(resultTask);
            Assert.assertEquals(changeTitle, resultTask.getTitle());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deleteTask_ThenSizeOfResultShouldBeTwo() {
        // [Given] : setUp(), DB에 3개의 Task 데이터가 입력 되어있다.
        String savedUuid = saved.toString();
        try {

            // [When] : uuid == savedUuid 인 Task 를 삭제한다.
            viewModel.deleteTask(savedUuid);

            // [Then] : uuid로 검색했을 때 결과가 존재하지 않아야한다.
            viewModel.loadTaskByUuid(savedUuid);
            Task task = LiveDataTestUtil.getValue(viewModel.getTask());
            Assert.assertNull(task);

            // [Then] : Task 데이터의 수가 2로 감소해야한다.
            viewModel.loadTasks();
            List<Task> result = LiveDataTestUtil.getValue(viewModel.getTasks());
            Assert.assertEquals(2, result.size());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void setLoading_ThenShouldChangeLoadingValue() {
        // [Given] : setUp(), DB에 3개의 Task 데이터가 입력 되어있다.
        try {
            // [When] : loading 값을 true 로 변경했을 때
            viewModel.testSetLoading(true);

            // [Then] : LiveData<Boolean> loading 이 올바르게 true 로 변경되는 것을 검증한다.
            boolean result = LiveDataTestUtil.getValue(viewModel.getLoading());
            Assert.assertTrue(result);

            // [When] : loading 값을 false 로 변경했을 때
            viewModel.testSetLoading(false);

            // [Then] : LiveData<Boolean> loading 이 올바르게 false 로 변경되는 것을 검증한다.
            result = LiveDataTestUtil.getValue(viewModel.getLoading());
            Assert.assertFalse(result);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
