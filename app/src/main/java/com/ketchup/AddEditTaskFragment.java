package com.ketchup;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ketchup.tasklist.TaskListFragment;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dagger.android.support.DaggerFragment;
import timber.log.Timber;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddEditTaskFragment extends DaggerFragment {

    public static final String MODE = "mode";
    public static final String TASK_ID = "taskId";

    private FloatingActionButton fab;

    public AddEditTaskFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_edit_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CollapsingToolbarLayout ctl = getActivity().findViewById(R.id.activity_main_collapsing_toolbar);
        ctl.setTitle("Add Task");

        Timber.d("[ onViewCreated ]");


        if (getArguments() != null) {
            String mode = getArguments().getString(MODE);
            String taskId = getArguments().getString(TASK_ID);

            Timber.d("[ Check Args. Values ]\nMode -> %s\ntaskId -> %s", mode, taskId);
        }

        setupFab(R.id.fab);

        NavHostFragment.findNavController(this).addOnDestinationChangedListener(onDestinationChangedListener);

    }

    private NavController.OnDestinationChangedListener onDestinationChangedListener = new NavController.OnDestinationChangedListener() {
        @Override
        public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
            Timber.d("\n   Destination : %s" , destination.getLabel() + ",   arguments : " + arguments);
            if (destination.getLabel().equals("fragment_add_edit_task")) {
                Timber.d("ADD_EDIT_TASK_FRAGMENT in ADD_EDIT_FRAGMENT");

                // make an anchor to "appbar"
                CoordinatorLayout.LayoutParams pl = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
                pl.setAnchorId(R.id.appbar);
                pl.anchorGravity = Gravity.BOTTOM | GravityCompat.END;
                pl.gravity = 0;
                fab.setLayoutParams(pl);

                // show() bug
                /**
                 * It's a bug in the FloatingActionButton class: When calling show(), imageMatrixScale is set to 0.
                 * */
                fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_menu_send));
                fab.hide();
                fab.show();
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("[ onCreate() ]");
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_add_black_24dp));
        Timber.d("[ onDestroy() - DestinationChangedListener Remove ]");
        NavHostFragment.findNavController(this).removeOnDestinationChangedListener(onDestinationChangedListener);
    }

    private void setupFab(@NonNull int viewId) {
        fab = getActivity().findViewById(viewId);

        fab.setOnClickListener(v -> {
            Timber.d("[ Fab.onClick in AddEditTaskFragment ]");

            // navigate back to TaskListFragment
            // popUpTo 동작 체크. 백스택에 TaskListFragment가 추가되는 것이 아니라. TaskListFragment까지 Popup 되어야한다.
            // taskList 가 모두 삭제되고 하나만 남아야한다.
            Bundle bundle = new Bundle();
            bundle.putInt(TaskListFragment.TASK_FILTER, 100);
            NavHostFragment.findNavController(this).navigate(R.id.action_addEditTaskFragment_to_task_list, bundle);
        });
    }
}
