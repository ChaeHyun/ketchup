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
import com.ketchup.addedit.AddEditTaskFragment;
import com.ketchup.utils.AnchoringFab;
import com.ketchup.utils.ContextCompatUtils;
import com.ketchup.DaggerViewModelFactory;
import com.ketchup.R;
import com.ketchup.utils.ToolbarController;
import com.ketchup.model.task.Task;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import timber.log.Timber;


public class TaskListFragment extends DaggerFragment {

    private List<Task> cachedTaskList;
    private int cachedFilter = 1;
    private TaskAdapter taskAdapter;
    private EmptyRecyclerView recyclerView;

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
        Timber.i("[ onViewCreated() ] , cachedFilter : %d", cachedFilter);

        // View Binding.
        setupToolbar();
        setupEmptyRecyclerView();
        setupFab();

        if (getArguments() != null) {
            Timber.i("TASK_FILTER : %d ", getArguments().getInt(TASK_FILTER));
            Timber.i("전송받은 ADD_MODE 값 : %s", getArguments().getBoolean("ADD_MODE"));
            Timber.i("전송받은 NEW_TASK_ID 값 : %s", getArguments().getString(NEW_TASK_ID));
        }

        navController = NavHostFragment.findNavController(this);
        navController.addOnDestinationChangedListener(destinationChangedListener);

        loadTasksByFilter(cachedFilter);
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
            taskAdapter = new TaskAdapter(NavHostFragment.findNavController(this));

            // EmptyRecyclerView Init
            recyclerView = getActivity().findViewById(R.id.fragment_task_list_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setEmptyView(getActivity().findViewById(R.id.fragment_task_list_empty_item));
            recyclerView.setNestedScrollingEnabled(false);

            recyclerView.setAdapter(taskAdapter);
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
        Timber.d("[ navigateTo AddEditTaskFragment ] : %s, %s", addMode, taskId);
        Bundle bundle = new Bundle();
        bundle.putString(AddEditTaskFragment.TASK_ID, taskId);
        navController.navigate(R.id.action_task_list_to_addEditTaskFragment, bundle);
    }

    private void observeFilter() {
        taskListViewModel.getTaskFilter().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer filter) {
                Timber.d("[ Filter Value Check ] : %d ", filter);
                cachedFilter = filter;
                loadTasksByFilter(filter);
            }
        });
    }

    private void loadTasksByFilter(int filter) {
        switch (filter) {
            case 1:
                // 전체 Task 가져오기
                toolbarController.setTitle("All");
                taskListViewModel.loadTasks();
                break;
            case 2:
                // 미완료 Task만 가져오기
                toolbarController.setTitle("Uncompleted");
                taskListViewModel.loadTasksCompleted(false);
                break;
            case 3:
                // 완료된 Task만 가져오기
                toolbarController.setTitle("Completed");
                taskListViewModel.loadTasksCompleted(true);
                break;
        }
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

    private void observeTasks() {
        taskListViewModel.getTasks().observe(this, list -> {
            Timber.d("[ Observer in TaskListFragment for List<Task>]");
            //listTestPrinting("observerTasks()", list);

            cachedTaskList = list;
            taskAdapter.setTasks(list);
        });
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
