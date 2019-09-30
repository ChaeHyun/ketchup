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

    private TaskViewModel taskViewModel;

    public static String TASK_FILTER = "task_filter";


    public static TaskListFragment createFor(Bundle bundle, int filter) {
        TaskListFragment fragment = new TaskListFragment();
        if (bundle == null)
            bundle = new Bundle();
        bundle.putInt(TASK_FILTER, filter);
        fragment.setArguments(bundle);

        return fragment;
    }
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
    public void onPause() {
        super.onPause();
        Timber.d("[ onPause()] ");
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

        taskViewModel = ViewModelProviders.of(this, viewModelFactory).get(TaskViewModel.class);

        taskViewModel.getTasks().observe(this, list -> {
            Timber.d("[ Observer in TaskListFragment for List<Task>]");
            for(Task t : list) {
                Timber.d("Task 값 : " + t.getTitle() + "\n");
            }
            taskAdapter.setTasks(list);
        });

        taskViewModel.getTask().observe(this, task -> {
            Timber.d("[ Observer in TaskListFragment for SingleTask ]");
            Timber.d("Task 값: %s", task.getTitle());
            List<Task> temp = new ArrayList<>();
            temp.add(task);
            taskAdapter.appendTasks(temp);
        });

        taskViewModel.getTaskFilter().observe(this, filter -> {
           Timber.d("[ Type Value Check ] : %d ", filter);
        });

        taskViewModel.loadTasks();

        if (getArguments() != null) {
            Timber.d("TASK_FILTER : %d ", getArguments().getInt(TASK_FILTER));
        }

        // View Binding.
        if (getActivity() != null) {
            // Collapsing Toolbar 인스턴스 얻어오기 from MainActivity's View
            ctl = getActivity().findViewById(R.id.activity_main_collapsing_toolbar);

            // EmptyRecyclerView Init
            recyclerView = getActivity().findViewById(R.id.fragment_task_list_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setEmptyView(getActivity().findViewById(R.id.fragment_task_list_empty_item));
            recyclerView.setNestedScrollingEnabled(false);

            // FAB
            FloatingActionButton fab = getActivity().findViewById(R.id.fab);
            fab.setOnClickListener(v -> {
                Timber.d("[ FAB.onClick in TaskListFragment]");
                //taskViewModel.loadTasksByTitle("세탁물 맡기기");
                Task newTask = new Task(UUID.randomUUID().toString(), "새로운 할일", false);
                taskViewModel.insertTask(newTask);
            });
        }

        ctl.setTitle("TaskList");       // 나중에 Activity --nav--> Fragment 에서 넘겨받도록 수정할것.

        // Adapter Init
        taskAdapter = new TaskAdapter();

        // Adapter - RV binding
        recyclerView.setAdapter(taskAdapter);


    }
}
