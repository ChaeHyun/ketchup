package com.ketchup.addedit;


import android.animation.LayoutTransition;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ketchup.utils.AnchoringFab;
import com.ketchup.utils.ContextCompatUtils;
import com.ketchup.DaggerViewModelFactory;
import com.ketchup.R;
import com.ketchup.utils.ToolbarController;
import com.ketchup.model.task.Task;
import com.ketchup.tasklist.TaskListFragment;

import java.util.Date;
import java.util.UUID;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import timber.log.Timber;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddEditTaskFragment extends DaggerFragment
        implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static int TOOLBAR_DEFAULT_COLOR;

    public static final String NEWLY_ADD = "add_mode";
    public static final String TASK_ID = "taskId";

    private static final String ERR_TITLE_EMPTY = "title is empty";

    // MODE
    private boolean addMode = true;
    private String taskId = null;

    private Task cachedTask;

    @Inject
    DaggerViewModelFactory viewModelFactory;
    private AddEditTaskViewModel viewModel;

    // Toolbar
    @Inject
    ToolbarController toolbarController;
    @Inject
    ContextCompatUtils contextCompatUtils;

    private FloatingActionButton fab;

    // Task Info
    private EditText titleEditText;
    private EditText descriptionEditText;

    // select Color label things
    private RadioGroup radioGroup;
    private SwitchCompat switchOfColorLabelButton;

    // Reminder things
    private LinearLayout reminderLayout;
    private SwitchCompat switchOfReminderButton;

    // Complete things
    private SwitchCompat switchOfCompleteButton;

    // 3 Switch Button On/Off Layout
    private LinearLayout[] switchLayout = new LinearLayout[3];

    private ActionBarDrawerToggle toggle;       // DrawerLayout Listener
    private NavController navController;

    public AddEditTaskFragment() {
        // Required empty public constructor
    }

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
        // Title이 MainActivity에 속하기 때문에 따로 관리해줘야한다.
        //resetTitleLayout(titleLayout, titleEditText);
        toolbarController.setupTitleLayout(View.GONE, null, null);
        NavHostFragment.findNavController(this).removeOnDestinationChangedListener(onDestinationChangedListener);

        //drawer.removeDrawerListener(toggle);
        toolbarController.removeDrawerListener(toggle);

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
        Timber.d("[ onViewCreated ]");

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(AddEditTaskViewModel.class);
        observeTask();

        setupDrawerAndToolbar();
        setupSwitchLayout();
        setupFab(R.id.fab);
        // **This must be called after initializing fab.
        navController = NavHostFragment.findNavController(this);
        navController.addOnDestinationChangedListener(onDestinationChangedListener);
        setupColorLabelArea();
        setupReminderArea();
        setupCompleteArea();

        /** 1. Bundle에 첨부된 값을 찾아보고 MODE 와 TASK_ID 값을 확인한다. */
        // Check Bundle
        if (getArguments() != null) {
            addMode = getArguments().getBoolean(NEWLY_ADD);
            taskId = getArguments().getString(TASK_ID);

            Timber.d("[ Check Args. Values ]\nMode -> %s\ntaskId -> %s", addMode, taskId);
        }

        if (addMode) {
            // Task 새로 추가
        } else {
            // 기존 Task 정보 불러오기
            viewModel.loadTaskByUuid(taskId);
        }

        // Task 정보 관련 뷰 초기화
        if (getActivity() != null) {
            titleEditText = getActivity().findViewById(R.id.add_item_edit_text_title);
            descriptionEditText = getActivity().findViewById(R.id.add_item_edit_text_description);
        }

    }

    /** 2. Title.isEmpty() 이면 저장하지 않아야한다. 판단하는 메소드 만들기 */
    private boolean isTitleEmpty(EditText titleEditText) {
        if (titleEditText.length() <= 0)
            return true;
        return false;
    }

    /** 3. View에 입력된 값을 Task로 포장해주는 로직을 메소드화한다.
     * AddMode라면 new Task를 받을 것이고,
     * EditMode라면 기존의 Task를 받을 것이다. */
    private Task wrapupUserInputToObject(Task inputTask) {
        // title, desc, completed , id
        inputTask.setTitle(titleEditText.getText().toString());
        inputTask.setDescription(descriptionEditText.getText().toString());
        inputTask.setCompleted(switchOfCompleteButton.isChecked());
        int colorLabelCheckedId = radioGroup.getCheckedRadioButtonId();
        int color = colorLabelCheckedId == -1 ? TOOLBAR_DEFAULT_COLOR : contextCompatUtils.convertButtonBackgroundColorToColorInteger(colorLabelCheckedId);


        // DueDate part later

        inputTask.setWrittenDate(new Date());

        return inputTask;
    }

    private Task makeTaskObject(boolean addMode) {
        if (addMode)
            return wrapupUserInputToObject(new Task(UUID.randomUUID().toString()));

        return wrapupUserInputToObject(cachedTask);
    }

    private String saveTaskInDatabase(boolean addMode) {
        if (isTitleEmpty(titleEditText)) {
            titleEditText.setError(getResources().getString(R.string.title_error));
            return ERR_TITLE_EMPTY;
        }

        Task resultTask = makeTaskObject(addMode);
        Timber.d("[만들어진 Task Obj값 확인]\nid : %s\ntitle : %s\ndesc : %s" +
                "\ncomp : %s\nwrittenDate : %s",
                resultTask.getUuid(), resultTask.getTitle(), resultTask.getDescription(),
                resultTask.isCompleted(), resultTask.getWrittenDate().toString());

        if (addMode) {
            Timber.d("[ 새로운 Task를 DB에 삽입. ]");
            viewModel.insertTask(resultTask);
        }
        else {
            viewModel.updateTask(resultTask);
        }

        return resultTask.getUuid();
    }

    private void putTaskDataToView(Task task) {
        // 기존 데이터를 AddEditTaskFragment에 입력하는 과정.
        titleEditText.setText(task.getTitle());
        descriptionEditText.setText(task.getDescription());
        switchOfCompleteButton.setChecked(task.isCompleted());

        // color , due date 나중에 대입하기.
    }

    private void observeTask() {
        viewModel.getTask().observe(this, task -> {
            // set Task data values to the appropriate each views.
            Timber.d("[Check the Single Task value ] - %s, %s", task.getUuid(), task.getTitle());
            cachedTask = task;
            putTaskDataToView(task);

        });

    }

    private NavController.OnDestinationChangedListener onDestinationChangedListener = new NavController.OnDestinationChangedListener() {
        @Override
        public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
            Timber.d("\n   Destination : %s" , destination.getLabel() + ",   arguments : " + arguments);
            if (destination.getLabel().equals("fragment_add_edit_task")) {
                Timber.d("ADD_EDIT_TASK_FRAGMENT in ADD_EDIT_FRAGMENT");

                AnchoringFab anchoringFab = new AnchoringFab(fab.getLayoutParams(), fab);
                anchoringFab.addAnchor(R.id.appbar, contextCompatUtils.getDrawable(R.drawable.ic_menu_send));
            }
        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.add_item_color_switch_layout_linear:
                switchOfColorLabelButton.performClick();
                break;
            case R.id.add_item_reminder_switch_layout_linear:

                break;
            case R.id.add_item_complete_switch_layout_linear:
                switchOfCompleteButton.performClick();
                break;
        }

    }

    /** Listener for SwitchCompat Buttons */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // switch가 On/Off 됐을때의 동작
        int viewId = buttonView.getId();

        switch (viewId) {
            case R.id.add_item_switch_color_label:
                setLayoutVisibleWithAnimations(radioGroup,700, 500, isChecked);

                if (!isChecked) {
                    if (radioGroup.getCheckedRadioButtonId() != -1) {
                        RadioButton rb = getActivity().findViewById(radioGroup.getCheckedRadioButtonId());
                        rb.setChecked(false);
                    }
                    toolbarController.setToolbarColor(TOOLBAR_DEFAULT_COLOR);
                } else {
                    //hideKeyboard
                }
                break;

            case R.id.add_item_switch_reminder:
                setLayoutVisibleWithAnimations(reminderLayout,500,500,isChecked);
                break;
        }

    }

    private void setLayoutVisibleWithAnimations(View view, long showUpDuration, long disappearDuration, boolean isChecked) {
        float showUpAlpha = 1.0f;
        float disappearAlpha = 0.0f;
        LayoutAnim layoutAnim = new LayoutAnim(view, isChecked);

        if (isChecked) {
            view.animate().alpha(showUpAlpha)
                    .setDuration(showUpDuration)
                    .setListener(layoutAnim);
        } else {
            view.animate().alpha(disappearAlpha)
                    .setDuration(disappearDuration)
                    .setListener(layoutAnim);
        }

    }

    private void setupFab(@NonNull int viewId) {
        fab = getActivity().findViewById(viewId);

        fab.setOnClickListener(v -> {
            Timber.d("[ Fab.onClick in AddEditTaskFragment ]");
            String resultTaskId = saveTaskInDatabase(addMode);

            if (!resultTaskId.equals(ERR_TITLE_EMPTY)) {
                // saveTaskInDatabase 에서 addMode true / false 다 처리하므로
                // bundle에 추가할 값은 동일하다.
                Bundle bundle = new Bundle();
                bundle.putBoolean("ADD_MODE", addMode);
                Timber.d("전송하는 NEW_TASK_ID 값 : %s", resultTaskId);
                bundle.putString(TaskListFragment.NEW_TASK_ID, resultTaskId);
                NavHostFragment.findNavController(this).navigate(R.id.action_addEditTaskFragment_to_task_list, bundle);
            }

        });
    }


    private void setupDrawerAndToolbar() {
        toolbarController.setTitle("");
        TOOLBAR_DEFAULT_COLOR = contextCompatUtils.getColor(R.color.addItemToolbar);
        toolbarController.setToolbarColor(TOOLBAR_DEFAULT_COLOR);
        toolbarController.setupTitleLayout(View.VISIBLE, null, null);

        toolbarController.setDrawerIndicatorEnabled(false);
        toolbarController.addToolbarOnClickListener(v -> {
            navController.navigateUp();
            toolbarController.setDrawerIndicatorEnabled(true);
        });
        toolbarController.addDrawerListener(toggle);
    }

    private void setupColorLabelArea() {
        if (getActivity() != null) {
            // Color Label Things
            radioGroup = getActivity().findViewById(R.id.add_item_radio_group);
            radioGroup.getLayoutTransition()
                    .enableTransitionType(LayoutTransition.CHANGE_APPEARING);
            switchOfColorLabelButton = getActivity().findViewById(R.id.add_item_switch_color_label);
            switchOfColorLabelButton.setOnCheckedChangeListener(this);
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                    final int color = contextCompatUtils.convertButtonBackgroundColorToColorInteger(checkedId);
                    //toolbarColorChange(ctl, toolbar, color);
                    toolbarController.setToolbarColor(color);
                }
            });

            // EDIT 모드인 경우 Task의 colorLabel 값에 맞게 설정값 변경.
        }
    }

    private void setupReminderArea() {
        if (getActivity() != null) {
            // Reminder Things
            reminderLayout = getActivity().findViewById(R.id.add_item_reminder_layout_linear);

            switchOfReminderButton = getActivity().findViewById(R.id.add_item_switch_reminder);
            switchOfReminderButton.setOnCheckedChangeListener(this);
        }
    }

    private void setupCompleteArea() {
        if (getActivity() == null)
            return;
        // Switch Button to complete a task.
        switchOfCompleteButton = getActivity().findViewById(R.id.add_item_switch_complete);
        switchOfCompleteButton.setOnCheckedChangeListener(this);
    }

    private void setupSwitchLayout() {
        if (getActivity() != null) {
            // Switch Layout
            switchLayout[0] = getActivity().findViewById(R.id.add_item_color_switch_layout_linear);
            switchLayout[1] = getActivity().findViewById(R.id.add_item_reminder_switch_layout_linear);
            switchLayout[2] = getActivity().findViewById(R.id.add_item_complete_switch_layout_linear);
            for (int i = 0; i < switchLayout.length; i++)
                switchLayout[i].setOnClickListener(this);
        }
    }
}
