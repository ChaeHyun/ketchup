package com.ketchup.tasklist;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ketchup.AdapterType;
import com.ketchup.addedit.AddEditTaskFragment;
import com.ketchup.model.Category;
import com.ketchup.model.CategoryWithTasks;
import com.ketchup.model.task.DateGroup;
import com.ketchup.utils.AnchoringFab;
import com.ketchup.utils.ContextCompatUtils;
import com.ketchup.di.DaggerViewModelFactory;
import com.ketchup.R;
import com.ketchup.utils.ToolbarController;
import com.ketchup.model.task.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import timber.log.Timber;


public class TaskListFragment extends DaggerFragment {

    private List<Task> cachedTaskList;
    private DateGroup cachedDateGroupFilter = DateGroup.TODAY;
    private EmptyRecyclerView recyclerView;

    private TaskAdapterRenewal taskAdapterRenewal;

    @Inject
    DaggerViewModelFactory viewModelFactory;
    @Inject
    ToolbarController toolbarController;
    @Inject
    ContextCompatUtils contextCompatUtils;

    private TaskListViewModel taskListViewModel;
    private NavController navController;

    private FloatingActionButton fab;

    public static String TASK_FILTER = "task_filter";
    public static String NEW_TASK_ID = "new_task_id";
    public static String ADD_MODE = "ADD_MODE";


    public TaskListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("[ onCreate() ]");
        taskListViewModel = ViewModelProviders.of(this, viewModelFactory).get(TaskListViewModel.class);

        if (taskListViewModel.getTasks().getValue() != null)
            cachedTaskList = taskListViewModel.getTasks().getValue();

        observeFilter();
        observeLoading();
        observeTasks();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Timber.i("[ onAttach ]");
    }

    @Override
    public void onResume() {
        super.onResume();
        Timber.i("[ onResume() ]");
    }

    @Override
    public void onPause() {
        super.onPause();
        Timber.i("[ onPause() ]");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.i("[ onDestroy() ]");
        if (navController != null)
            navController.removeOnDestinationChangedListener(destinationChangedListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Timber.i("[ onViewCreated() ] , cachedDateGroupFilter : %d", cachedDateGroupFilter.ordinal());

        // View Binding.
        setupToolbar();
        setupEmptyRecyclerView();
        setupFab();

        if (getArguments() != null) {
            Timber.i("TASK_FILTER : %d ", getArguments().getInt(TASK_FILTER));
            Timber.i("전송받은 ADD_MODE 값 : %s", getArguments().getBoolean(ADD_MODE));
            Timber.i("전송받은 NEW_TASK_ID 값 : %s", getArguments().getString(NEW_TASK_ID));
        }

        navController = NavHostFragment.findNavController(this);
        navController.addOnDestinationChangedListener(destinationChangedListener);

        loadTasksByFilter(cachedDateGroupFilter);
    }


    private NavController.OnDestinationChangedListener destinationChangedListener = new NavController.OnDestinationChangedListener() {
        @Override
        public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
            if (destination.getLabel().toString().equals("taskList")) {
                //Timber.d(" TASK_LIST_FRAGMENT ");

                AnchoringFab anchoringFab = new AnchoringFab(fab.getLayoutParams(), fab);
                anchoringFab.removeAnchor(contextCompatUtils.getDrawable(R.drawable.ic_add_black_24dp));
            }
        }
    };

    private void setupToolbar() {
        if (getActivity() != null) {
            toolbarController.setTitle("TaskListScreen");
        }
    }

    private void setupEmptyRecyclerView() {
        if (getActivity() != null) {
            taskAdapterRenewal = new TaskAdapterRenewal(NavHostFragment.findNavController(this));

            // EmptyRecyclerView Init
            recyclerView = getActivity().findViewById(R.id.fragment_task_list_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setEmptyView(getActivity().findViewById(R.id.fragment_task_list_empty_item));
            recyclerView.setNestedScrollingEnabled(false);

            recyclerView.setAdapter(taskAdapterRenewal);
        }
    }

    private void setupFab() {
        if (getActivity() != null) {
            // FAB
            fab = getActivity().findViewById(R.id.fab);

            fab.setOnClickListener(v -> {
                Timber.d("[ FAB.onClick in TaskListFragment]");
                navigateToAddEditTaskFragment(true, null);
            });
        }
    }

    private void navigateToAddEditTaskFragment(boolean addMode, String taskId) {
        Timber.d("[ navigateToAddEditTaskFragment AddEditTaskFragment ] : %s, %s", addMode, taskId);
        Bundle bundle = new Bundle();
        bundle.putString(AddEditTaskFragment.TASK_ID, taskId);
        navController.navigate(R.id.action_task_list_to_addEditTaskFragment, bundle);
    }

    private void observeFilter() {
        taskListViewModel.getTaskFilter().observe(this, new Observer<DateGroup>() {
            @Override
            public void onChanged(DateGroup dateGroup) {
                Timber.d("[ Filter Value Check ] : %d ", dateGroup.ordinal());
                cachedDateGroupFilter = dateGroup;
                loadTasksByFilter(dateGroup);
            }
        });
    }

    private void loadTasksByFilter(DateGroup dateGroup) {
        switch (dateGroup) {
            case TODAY:
                // Task for Today
                toolbarController.setTitle("오늘 할일");
                break;
            case UPCOMING:
                // Task for Upcoming
                toolbarController.setTitle("다가올 일");
                break;
            case OVERDUE:
                // Task for Overdue
                toolbarController.setTitle("지난 일");
                break;
            case NOTE:
                // Date Query : Before
                toolbarController.setTitle("메모");
                break;
            case TOMORROW:
                // Date == NULL
                toolbarController.setTitle("내일");
                break;
            case ALL:
                toolbarController.setTitle("모두 보기");
                taskListViewModel.loadTasks();
                return;
        }
        taskListViewModel.loadTasksInCertainPeriod(dateGroup);
    }

    private void observeLoading() {
        // Observe
        taskListViewModel.getLoading().observe(this, loading -> {
            if (loading) {
                Timber.v("[ LiveData loading is TRUE ]");
                Toast.makeText(getActivity(), "Loading : TRUE", Toast.LENGTH_SHORT).show();
            } else {
                Timber.v("[ LiveData loading is FALSE ]");
                Toast.makeText(getActivity(), "Loading : FALSE", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 현재 DB에서 데이터를 꺼내오는 형태는 List<Task> 이다.
    // 업데이트하고 싶은 데이터 형태는 List<CategoryWithTasks>이다.
    // DB에서 List<CategoryWithTasks로 데이터를 꺼내기 위해서는 사전 작업이 필요하다.
    // 우선 현재 받아온 List<Task> 수작업으로 List<CategoryWithTasks>로 바꾸는 작업을 해보자.

    private void observeTasks() {
        taskListViewModel.getTasks().observe(this, list -> {
            Timber.d("[ Observer in TaskListFragment for List<Task>]");
            //listTestPrinting("observerTasks()", list);

            cachedTaskList = list;

            Category categoryUncom = new Category(UUID.randomUUID().toString(), "미완료");
            Category categoryCom = new Category(UUID.randomUUID().toString(), "완료");

            List<Task> uncompletedTaskList = filterCompleted(list, false);
            List<Task> completedTaskList = filterCompleted(list, true);

            CategoryWithTasks categoryWithTasksUncom = new CategoryWithTasks(categoryUncom, uncompletedTaskList, false);
            CategoryWithTasks categoryWithTasksCom = new CategoryWithTasks(categoryCom, completedTaskList, true);

            List<AdapterType> data = new ArrayList<>();
            data.add(categoryWithTasksUncom);
            data.add(categoryWithTasksCom);
            // 확인
            print(categoryWithTasksUncom);
            print(categoryWithTasksCom);
            taskAdapterRenewal.setData(data);
        });
    }

    private void print(CategoryWithTasks input) {
        String title = input.category.getName();
        Timber.d("[ %s, %s , %s]", title, input.getItemType(), input.category.isFolded());
        for (Task t : input.tasks) {
            Timber.d("  (%s, %s, %s)", t.getTitle(), t.isCompleted(), t.getItemType());
        }
    }

    private List<Task> filterCompleted(List<Task> input, boolean complete) {
        List<Task> result = new ArrayList<>();
        for (Task task : input) {
            if (task.isCompleted() == complete)
                result.add(task);
        }

        return result;
    }

    private void listTestPrinting(String title, List<Task> list) {
        // cachedTask 출력
        Timber.d("[ cachedTask 출력 ] : %s", title);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                Timber.d("%d : %s", i, list.get(i).getTitle());
            }
        }
    }
}
