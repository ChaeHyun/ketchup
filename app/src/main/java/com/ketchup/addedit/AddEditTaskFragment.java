package com.ketchup.addedit;


import android.animation.LayoutTransition;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ketchup.MainActivity;
import com.ketchup.utils.AnchoringFab;
import com.ketchup.utils.ContextCompatUtils;
import com.ketchup.DaggerViewModelFactory;
import com.ketchup.R;
import com.ketchup.utils.DateManipulator;
import com.ketchup.utils.KeypadUtils;
import com.ketchup.utils.ToolbarController;
import com.ketchup.model.task.Task;
import com.ketchup.tasklist.TaskListFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import timber.log.Timber;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddEditTaskFragment extends DaggerFragment
        implements View.OnClickListener, CompoundButton.OnCheckedChangeListener,
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

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
    @Inject
    KeypadUtils keypadUtils;

    private Date keepDueDate;

    private FloatingActionButton fab;

    // Task Info
    private EditText titleEditText;
    private EditText descriptionEditText;
    private EditText dueDatePickEditText;
    private EditText dueTimePickEditText;

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
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.d("[ onDestroy() - DestinationChangedListener Remove ]");
        toolbarController.setupTitleLayout(View.GONE, null, null);
        NavHostFragment.findNavController(this).removeOnDestinationChangedListener(onDestinationChangedListener);

        keypadUtils.hideKeypad(fab);
        toolbarController.setDrawerIndicatorEnabled(true);
        toolbarController.removeDrawerListener(toggle);

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.add_edit_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:

                break;
            case R.id.delete:
                if (!addMode) {
                    viewModel.deleteTask(taskId);
                    Timber.d("삭제 - task delete");
                    //navController.navigateUp();
                    navigateToTaskListFragment(addMode, taskId);
                } else {
                    Toast.makeText(getActivity(), "삭제할 Task가 없습니다.", Toast.LENGTH_LONG).show();
                }

                break;
        }
        return super.onOptionsItemSelected(item);
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

        navController = NavHostFragment.findNavController(this);
        navController.addOnDestinationChangedListener(onDestinationChangedListener);
        setupColorLabelArea();
        setupReminderArea();
        setupCompleteArea();


        // Check Bundle
        if (getArguments() != null) {
            addMode = getArguments().getBoolean(NEWLY_ADD);
            taskId = getArguments().getString(TASK_ID);

            Timber.d("[ Check Args. Values ]\nMode -> %s\ntaskId -> %s", addMode, taskId);
        }

        // Task 정보 관련 뷰 초기화
        if (getActivity() != null) {
            titleEditText = getActivity().findViewById(R.id.add_item_edit_text_title);
            descriptionEditText = getActivity().findViewById(R.id.add_item_edit_text_description);
        }

        if (addMode) {
            // Task 새로 추가
            keypadUtils.showKeypad();
            keepDueDate = null;
        } else {
            // 기존 Task 정보 불러오기
            Timber.d("DB에서 Task 불러오기 : %s", taskId);
            viewModel.loadTaskByUuid(taskId);
        }

    }

    private boolean isTitleEmpty(EditText titleEditText) {
        return (titleEditText.length() <= 0);
    }

    /** Make Object things to save */
    private Task wrapupUserInputToObject(Task inputTask) {
        // title, desc, completed , id
        inputTask.setTitle(titleEditText.getText().toString());
        inputTask.setDescription(descriptionEditText.getText().toString());
        inputTask.setCompleted(switchOfCompleteButton.isChecked());
        int colorLabelCheckedId = radioGroup.getCheckedRadioButtonId();
        int color = colorLabelCheckedId == -1 ? TOOLBAR_DEFAULT_COLOR : contextCompatUtils.convertButtonBackgroundColorToColorId(colorLabelCheckedId);
        inputTask.setColorLabel(color);

        // DueDate part later
        inputTask.setWrittenDate(new Date());
        inputTask.setDueDate(keepDueDate);

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
        // Check the object is created correctly before put it to the Database.
        String dueDateCheck = resultTask.getDueDate() == null ? "null" : resultTask.getDueDate().toString();
        Timber.d("[만들어진 Task Obj값 확인]\nid : %s\ntitle : %s\ndesc : %s\ncomp : %s\nwrittenDate : %s\ndueDate : %s",
                resultTask.getUuid(), resultTask.getTitle(), resultTask.getDescription(),
                resultTask.isCompleted(), resultTask.getWrittenDate().toString(), dueDateCheck);

        if (addMode) {
            Timber.d("[ 새로운 Task를 DB에 삽입. ]");
            viewModel.insertTask(resultTask);
        }
        else {
            viewModel.updateTask(resultTask);
        }

        return resultTask.getUuid();
    }


    /** Methods to set dueDate values */
    private Date setCurrentDueDate() {
        Timber.d("KeepDueDate == null : 지금 시간 기준으로 디폴트값 세팅");
        dueDatePickEditText.setText(getResources().getString(R.string.today));

        DateManipulator dm = new DateManipulator(null, MainActivity.DEVICE_LOCALE);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, dm.getDate());

        String formatHourOfDay = dm.get24HourFormatString(getActivity());
        if (DateFormat.is24HourFormat(getActivity())) {
            cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) + 1);
        } else {
            cal.set(Calendar.HOUR, cal.get(Calendar.HOUR) + 1);
        }

        dm.setCalendar(cal);
        dueTimePickEditText.setText(dm.getDateString(cal.getTime(), formatHourOfDay));
        return cal.getTime();
    }


    private Date setExistingDateValue(Date date) {
        if (date == null) {
            return setCurrentDueDate();
        }
        DateManipulator dm = new DateManipulator(date, MainActivity.DEVICE_LOCALE);

        String dueDateString = dm.getDateString(date, DateManipulator.DATE_FORMAT_DATE_PICKER);
        String dueTimeString = dm.getDateString(date, dm.get24HourFormatString(getActivity()));
        dueDatePickEditText.setText(dueDateString);
        dueTimePickEditText.setText(dueTimeString);

        return date;
    }

    /** This method will be called by Callback of Observer.
     * to restore Task Object Data to its View. */
    private void restoreTaskDataToView(Task task) {
        // 기존 데이터를 AddEditTaskFragment에 입력하는 과정.
        Timber.d("기존 Task를 Restore 하기");
        titleEditText.setText(task.getTitle());
        descriptionEditText.setText(task.getDescription());
        switchOfCompleteButton.setChecked(task.isCompleted());

        int color = task.getColorLabel();
        if (color == Task.DEFAULT_COLOR) {
            setLayoutVisibleWithAnimations(radioGroup,700, 500, false);
        } else {
            setLayoutVisibleWithAnimations(radioGroup,700, 500, true);
            switchOfColorLabelButton.setChecked(true);
            radioGroup.check(contextCompatUtils.convertButtonColorToButtonId(color));
            toolbarController.setToolbarColor(color);
        }

        // 기존에 DueDate값이 있다면
        if (task.getDueDate() == null) {
            setLayoutVisibleWithAnimations(reminderLayout, 500, 500, false);
        } else {
            // 레이아웃을 VISIBLE 하게 변경하고 데이터를 입력한다.
            setLayoutVisibleWithAnimations(reminderLayout, 500, 500, true);
            switchOfReminderButton.setChecked(true);
            keepDueDate = task.getDueDate();
        }
    }

    private void observeTask() {
        viewModel.getTask().observe(this, task -> {
            // set Task data values to the appropriate each views.
            if (task != null) {
                Timber.d("[Check the Single Task value ] - %s, %s", task.getUuid(), task.getTitle());
                cachedTask = task;
                restoreTaskDataToView(task);
            } else {
                Timber.d("Observer에서 Task 읽기 실패.");
            }
        });
    }

    private NavController.OnDestinationChangedListener onDestinationChangedListener = new NavController.OnDestinationChangedListener() {
        @Override
        public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
            Timber.d("\n   Destination : %s" , destination.getLabel() + ",   arguments : " + arguments);
            if (destination.getLabel().equals("fragment_add_edit_task")) {
                Timber.d("ADD_EDIT_TASK_FRAGMENT in ADD_EDIT_FRAGMENT");
                setupFab(R.id.fab);
            }
        }
    };

    /** Listener for View.onClick*/
    @Override
    public void onClick(View v) {
        DateManipulator currentTimeStandard = new DateManipulator(new Date(), MainActivity.DEVICE_LOCALE);

        switch (v.getId()) {
            case R.id.add_item_color_switch_layout_linear:
                keypadUtils.hideKeypad(switchOfCompleteButton);
                switchOfColorLabelButton.performClick();
                break;
            case R.id.add_item_reminder_switch_layout_linear:
                keypadUtils.hideKeypad(switchOfReminderButton);
                switchOfReminderButton.performClick();

                break;
            case R.id.add_item_complete_switch_layout_linear:
                keypadUtils.hideKeypad(switchOfCompleteButton);
                switchOfCompleteButton.performClick();
                break;

            case R.id.add_item_due_date_reminder:
                // 오늘 날짜 기준.
                int year = currentTimeStandard.getYear();
                int month = currentTimeStandard.getMonth();
                int day = currentTimeStandard.getDate();

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month-1, day);
                datePickerDialog.show();

                break;
            case R.id.add_item_due_time_reminder:
                int hour = currentTimeStandard.getHour() + 1;
                int minute = currentTimeStandard.getMinute();

                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
                timePickerDialog.show();
                break;
        }
    }

    /** Listener for SwitchCompat Buttons */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int viewId = buttonView.getId();
        switch (viewId) {
            case R.id.add_item_switch_color_label:
                keypadUtils.hideKeypad(switchOfCompleteButton);
                setLayoutVisibleWithAnimations(radioGroup,700, 500, isChecked);

                if (!isChecked) {
                    if (radioGroup.getCheckedRadioButtonId() != -1) {
                        RadioButton rb = getActivity().findViewById(radioGroup.getCheckedRadioButtonId());
                        rb.setChecked(false);
                    }
                    toolbarController.setToolbarColor(TOOLBAR_DEFAULT_COLOR);
                }
                break;

            case R.id.add_item_switch_reminder:
                keypadUtils.hideKeypad(switchOfReminderButton);
                setLayoutVisibleWithAnimations(reminderLayout,500,500,isChecked);
                keepDueDate = setExistingDateValue(keepDueDate);
                break;

            case R.id.add_item_switch_complete:
                keypadUtils.hideKeypad(switchOfCompleteButton);
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
        if (getActivity() == null)
            return;

        fab = getActivity().findViewById(viewId);

        fab.setOnClickListener(v -> {
            Timber.d("[ Fab.onClick in AddEditTaskFragment ]");
            keypadUtils.hideKeypad(fab);
            String resultTaskId = saveTaskInDatabase(addMode);

            if (!resultTaskId.equals(ERR_TITLE_EMPTY)) {
                // saveTaskInDatabase 에서 addMode true / false 다 처리하므로
                // bundle에 추가할 값은 동일하다.
                navigateToTaskListFragment(addMode, resultTaskId);
            }
        });

        AnchoringFab anchoringFab = new AnchoringFab(fab.getLayoutParams(), fab);
        anchoringFab.addAnchor(R.id.appbar, contextCompatUtils.getDrawable(R.drawable.ic_menu_send));
    }

    private void navigateToTaskListFragment(boolean addMode, String taskId) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("ADD_MODE", addMode);
        Timber.d("전송하는 NEW_TASK_ID 값 : %s", taskId);
        bundle.putString(TaskListFragment.NEW_TASK_ID, taskId);
        NavHostFragment.findNavController(this).navigate(R.id.action_addEditTaskFragment_to_task_list, bundle);
    }

    private void setupDrawerAndToolbar() {
        toolbarController.setAppbarExpanded(true);
        toolbarController.setTitle("");
        TOOLBAR_DEFAULT_COLOR = contextCompatUtils.getColor(R.color.addItemToolbar);
        toolbarController.setToolbarColor(TOOLBAR_DEFAULT_COLOR);
        toolbarController.setupTitleLayout(View.VISIBLE, null, null);

        toolbarController.setDrawerIndicatorEnabled(false);
        toolbarController.addToolbarOnClickListener(v -> {
            navController.navigateUp();
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
                    final int color = contextCompatUtils.convertButtonBackgroundColorToColorId(checkedId);
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

            dueDatePickEditText = getActivity().findViewById(R.id.add_item_due_date_reminder);
            dueTimePickEditText = getActivity().findViewById(R.id.add_item_due_time_reminder);
            dueDatePickEditText.setOnClickListener(this);
            dueTimePickEditText.setOnClickListener(this);
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


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        DateManipulator dm = new DateManipulator(keepDueDate, MainActivity.DEVICE_LOCALE);

        Calendar calendarNow = Calendar.getInstance();
        Calendar reminderCalendar = Calendar.getInstance();
        reminderCalendar.set(year, month, dayOfMonth);

        if (dm.compareCalendar(calendarNow, reminderCalendar) < 0) {
            Toast.makeText(getActivity(), getString(R.string.before_today), Toast.LENGTH_LONG).show();
            return;
        }

        keepDueDate = dm.setDate(reminderCalendar, keepDueDate, year, month, dayOfMonth);
        dueDatePickEditText.setText(dm.getDateString(keepDueDate, DateManipulator.DATE_FORMAT_DATE_PICKER));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        DateManipulator dm = new DateManipulator(keepDueDate, MainActivity.DEVICE_LOCALE);
        Calendar calendarNow = Calendar.getInstance();
        Calendar reminderCalendar = Calendar.getInstance();

        reminderCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        reminderCalendar.set(Calendar.MINUTE, minute);

        if (reminderCalendar.before(calendarNow)) {
            Toast.makeText(getActivity(), getString(R.string.before_today), Toast.LENGTH_LONG).show();
            return;
        }

        keepDueDate = dm.setTime(reminderCalendar, keepDueDate, hourOfDay, minute);
        dueTimePickEditText.setText(dm.getDateString(keepDueDate, dm.get24HourFormatString(getActivity())));
    }
}
