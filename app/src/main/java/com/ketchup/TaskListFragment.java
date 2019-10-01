package com.ketchup;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ketchup.model.task.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import timber.log.Timber;


public class TaskListFragment extends DaggerFragment {

    /** Cached 로써 TaskListFragment도 데이터를 홀드하고 있어야 한다.
     * LiveData 값을 그대로 쓰는게 아니라 DeepCopy 를 해야한다. */
    private List<Task> cachedTaskList;
    private TaskAdapter taskAdapter;

    private CollapsingToolbarLayout ctl;
    private EmptyRecyclerView recyclerView;

    @Inject
    DaggerViewModelFactory viewModelFactory;

    private TaskListViewModel taskListViewModel;

    public static String TASK_FILTER = "task_filter";


    public TaskListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        Timber.d("[ onResume() ]");
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

        // setup ViewModel
        taskListViewModel = ViewModelProviders.of(this, viewModelFactory).get(TaskListViewModel.class);

        // View Binding.
        setupCollapsingToolbar();
        setupEmptyRecyclerView();
        setupFab();

        observeFilter();
        observeLoading();
        observeTasks();

        if (getArguments() != null) {
            Timber.d("TASK_FILTER : %d ", getArguments().getInt(TASK_FILTER));
        }

        taskListViewModel.loadTasks();
    }

    private void setupCollapsingToolbar() {
        if (getActivity() != null) {
            // Collapsing Toolbar 인스턴스 얻어오기 from MainActivity's View
            ctl = getActivity().findViewById(R.id.activity_main_collapsing_toolbar);
            ctl.setTitle("TaskList");
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
            FloatingActionButton fab = getActivity().findViewById(R.id.fab);
            fab.setOnClickListener(v -> {
                Timber.d("[ FAB.onClick in TaskListFragment]");

                // Navigate To AddEditFragment, with Args : taskId, mode
                //Task newTask = new Task(UUID.randomUUID().toString(), "새로운 할일", false);
                //taskListViewModel.insertTask(newTask);
            });
        }
    }

    private void observeFilter() {
        taskListViewModel.getTaskFilter().observe(this, filter -> {
            Timber.d("[ Type Value Check ] : %d ", filter);
        });
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
                Timber.d("Task 값 : " + t.getTitle() + "\n");
            }
            taskAdapter.setTasks(list);
        });
    }
}
