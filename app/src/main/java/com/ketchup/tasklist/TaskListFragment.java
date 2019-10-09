package com.ketchup.tasklist;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ketchup.AddEditTaskFragment;
import com.ketchup.DaggerViewModelFactory;
import com.ketchup.R;
import com.ketchup.model.task.Task;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import timber.log.Timber;


public class TaskListFragment extends DaggerFragment {

    /** Cached 로써 TaskListFragment도 데이터를 홀드하고 있어야 한다.
     * LiveData 값을 그대로 쓰는게 아니라 DeepCopy 를 해야한다. */
    private List<Task> cachedTaskList;
    private int cachedFilter = 1;
    private TaskAdapter taskAdapter;

    private CollapsingToolbarLayout ctl;
    private EmptyRecyclerView recyclerView;

    @Inject
    DaggerViewModelFactory viewModelFactory;

    private TaskListViewModel taskListViewModel;
    private NavController navController;

    private FloatingActionButton fab;

    public static String TASK_FILTER = "task_filter";



    public TaskListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("[ onCreate() ]");
        // 옵저버를 한번만 등록하도록 onCreate()에서 작업한다.
        taskListViewModel = ViewModelProviders.of(this, viewModelFactory).get(TaskListViewModel.class);

        if (taskListViewModel.getTasks().getValue() != null) {
            Timber.d("기존 ViewModel에 보관된 TaskList 데이터가 존재한다.");
            cachedTaskList = taskListViewModel.getTasks().getValue();
        }

        observeFilter();
        observeLoading();
        observeTasks();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Timber.d("[ onAttach ]");
    }

    @Override
    public void onResume() {
        super.onResume();
        Timber.d("[ onResume() ]");
    }

    @Override
    public void onPause() {
        super.onPause();
        Timber.d("[ onPause() ]");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.d("[ onDestroy() ]");
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
        Timber.d("[ onViewCreated() ] , cachedFilter : %d", cachedFilter);

        // View Binding.
        setupCollapsingToolbar();
        setupEmptyRecyclerView();
        setupFab();

        if (getArguments() != null) {
            Timber.d("TASK_FILTER : %d ", getArguments().getInt(TASK_FILTER));
        }

        navController = NavHostFragment.findNavController(this);
        navController.addOnDestinationChangedListener(destinationChangedListener);


        //taskListViewModel.loadTasks();
        // AddEditTaskFragment 화면에서 되돌아올때 기존에 보여주던 filtered Data 리스트를 그대로 불러오기 위해서.
        // 기존에는 fragment resume시 onViewCreated가 새로 불리면서 항상 all data list로 실행되었다.
        if (cachedTaskList != null && !cachedTaskList.isEmpty()) {
            Timber.d("Restored from cachedTaskList");
            taskAdapter.setTasks(cachedTaskList);
        }
        else {
            Timber.d("NEWLY LOADED");
            loadTasksByFilter(cachedFilter);
        }
    }

    private NavController.OnDestinationChangedListener destinationChangedListener = new NavController.OnDestinationChangedListener() {
        @Override
        public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
            if (destination.getLabel().toString().equals("taskList")) {
                Timber.d(" TASK_LIST_FRAGMENT ");

                // Disable the anchored position of Floating Action Button
                CoordinatorLayout.LayoutParams pl = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
                pl.gravity = Gravity.BOTTOM| GravityCompat.END;
                pl.setAnchorId(-1);
                pl.anchorGravity = 0;
                fab.setLayoutParams(pl);

                if (getActivity() != null)
                    fab.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_add_black_24dp));
                fab.hide();
                fab.show();
            }
        }
    };

    private void setupCollapsingToolbar() {
        if (getActivity() != null) {
            // Collapsing Toolbar 인스턴스 얻어오기 from MainActivity's View
            ctl = getActivity().findViewById(R.id.activity_main_collapsing_toolbar);
            ctl.setTitle("TaskListScreen");
        }
    }

    private void setupEmptyRecyclerView() {
        if (getActivity() != null) {
            taskAdapter = new TaskAdapter();

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

                navigateToAddEditTaskFragment("ADD_MODE", null);
            });
        }
    }

    private void navigateToAddEditTaskFragment(String mode, String taskId) {
        Timber.d("[ navigateTo AddEditTaskFragment ] : %s, %s", mode, taskId);
        Bundle bundle = new Bundle();
        bundle.putString(AddEditTaskFragment.MODE, mode);
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
                taskListViewModel.loadTasks();
                break;
            case 2:
                // 미완료 Task만 가져오기
                taskListViewModel.loadTasksCompleted(false);
                break;
            case 3:
                // 완료된 Task만 가져오기
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
            for(Task t : list) {
                //Timber.d("Task 값 : " + t.getTitle() + "\n");
            }

            cachedTaskList = list;
            taskAdapter.setTasks(list);
        });
    }
}
