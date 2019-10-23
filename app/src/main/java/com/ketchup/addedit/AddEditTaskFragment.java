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

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import timber.log.Timber;


/**
 * A simple {@link Fragment} subclass.
 * before refactoring : 600 lines. Too many logic in View area. Lets move some logic to viewModel.
 */
public class AddEditTaskFragment extends DaggerFragment
        implements View.OnClickListener, CompoundButton.OnCheckedChangeListener,
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    public static final String TASK_ID = "taskId";

    public static final String SAVED_OK = "task_save_ok";
    public static final String SAVED_FAIL = "task_save_fail";

    private String taskId = null;

    @Inject
    DaggerViewModelFactory viewModelFactory;
    private AddEditTaskViewModel viewModel;

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
        if (getActivity() != null)
            getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.d("[ onDestroy() - DestinationChangedListener Remove ]");
        NavHostFragment.findNavController(this).removeOnDestinationChangedListener(onDestinationChangedListener);
        toolbarController.setupTitleLayout(View.GONE, null, null);
        toolbarController.setDrawerIndicatorEnabled(true);
        toolbarController.removeDrawerListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        Timber.d("[ onPause() - DestinationChangedListener Remove ]");
        keypadUtils.hideKeypad(fab);
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
                if (taskId == null) {
                    Toast.makeText(getActivity(), getString(R.string.err_no_task_to_delete), Toast.LENGTH_LONG).show();
                    keypadUtils.hideKeypad(fab);
                }
                else
                    viewModel.deleteTask(taskId);
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

        setupDrawerAndToolbar();
        setupSwitchLayout();
        navController = NavHostFragment.findNavController(this);
        navController.addOnDestinationChangedListener(onDestinationChangedListener);    // setupFab()
        setupColorLabelArea();
        setupReminderArea();
        setupCompleteArea();

        // Check Bundle
        if (getArguments() != null) {
            taskId = getArguments().getString(TASK_ID);
            if (taskId == null)
                keypadUtils.showKeypad();
            Timber.d("[ Check Args. Values ]\ntaskId -> %s", taskId);
        }

        // add observers
        observeTitle();
        observeDescription();
        observeCompleted();
        observeColor();
        observeDueDate();
        observeSaved();

        viewModel.getLoading().observe(this, loading -> {
            Timber.d(" #### Check loading. : %s  ####", loading);
        });

        viewModel.load(taskId);
    }

    private void setupDrawerAndToolbar() {
        toolbarController.setAppbarExpanded(true);
        toolbarController.setTitle("");
        toolbarController.setToolbarColor(Task.DEFAULT_COLOR);
        toolbarController.setupTitleLayout(View.VISIBLE, null, null);

        // nav icon disable 작업.
        toolbarController.setDrawerIndicatorEnabled(false);
        toolbarController.addToolbarOnClickListener(v -> {
            navController.navigateUp();
        });
        toolbarController.addDrawerListener();

        if (getActivity() != null) {
            titleEditText = getActivity().findViewById(R.id.add_item_edit_text_title);
            descriptionEditText = getActivity().findViewById(R.id.add_item_edit_text_description);
        }
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

    private void setupFab(@NonNull int viewId) {
        if (getActivity() == null)
            return;

        fab = getActivity().findViewById(viewId);
        fab.setOnClickListener(this);

        AnchoringFab anchoringFab = new AnchoringFab(fab.getLayoutParams(), fab);
        anchoringFab.addAnchor(R.id.appbar, contextCompatUtils.getDrawable(R.drawable.ic_menu_send));
    }

    /** Adding Observers */
    private void observeSaved() {
        viewModel.getSaved().observe(this, state -> {
            if (state.equals(AddEditTaskFragment.SAVED_OK)) {
                Timber.d("Success to save a Task to DB");
                // navigate to task list fragment
                Bundle bundle = new Bundle();
                Timber.d("전송하는 NEW_TASK_ID 값 : %s", taskId);
                bundle.putString(TaskListFragment.NEW_TASK_ID, taskId);
                navController.navigate(R.id.action_addEditTaskFragment_to_task_list, bundle);
            }

            if (state.equals(SAVED_FAIL)) {
                Timber.d("Error occurred while saving a task to DB");
                navController.navigateUp();
            }
        });
    }

    private void observeTitle() {
        viewModel.getTitle().observe(this, title -> {
            if (title == null) {
                Timber.d("title 읽기 실패.");
                return;
            }
            titleEditText.setText(title);
        });
    }

    private void observeDescription() {
        viewModel.getDescription().observe(this, desc -> {
           if (desc == null) {
               return;
           }
           descriptionEditText.setText(desc);
        });
    }

    private void observeCompleted() {
        viewModel.getCompleted().observe(this, isCompleted -> {
            if (isCompleted == null)
                return;
           switchOfCompleteButton.setChecked(isCompleted);
        });
    }

    private void observeColor() {
        viewModel.getColor().observe(this, color -> {
            if (color == null)  // error
                return;

            if (color == Task.DEFAULT_COLOR) {
                switchOfColorLabelButton.setChecked(false);
                toolbarController.setToolbarColor(Task.DEFAULT_COLOR);
                // switch uncheck
                if (radioGroup.getCheckedRadioButtonId() != -1 && getActivity() != null) {
                    RadioButton rb = getActivity().findViewById(radioGroup.getCheckedRadioButtonId());
                    rb.setChecked(false);
                }
            } else {
                switchOfColorLabelButton.setChecked(true);
                toolbarController.setToolbarColor(color);
                radioGroup.check(contextCompatUtils.convertButtonColorToButtonId(color));
                setLayoutVisibleWithAnimations(radioGroup,700, 500, true);
            }
        });
    }

    private void observeDueDate() {
        viewModel.getDueDate().observe(this, dueDate -> {
            if (dueDate == null) {
                keepDueDate = null;
            }
            else  {
                keepDueDate = dueDate;
                switchOfReminderButton.setChecked(true);
            }
        });
    }

    private boolean isTitleEmpty(EditText titleEditText) {
        return (titleEditText.length() <= 0);
    }

    private Date setCurrentDueDate() {
        dueDatePickEditText.setText(getResources().getString(R.string.today));

        DateManipulator dm = new DateManipulator(null, MainActivity.DEVICE_LOCALE);
        Calendar cal = dm.setTodayPlus1Hour(getActivity());
        String formatHourOfDay = dm.get24HourFormatString(getActivity());
        dueTimePickEditText.setText(dm.getDateString(cal.getTime(), formatHourOfDay));
        return cal.getTime();
    }

    private Date setExistingDateValue(Date date) {
        if (date == null)
            return setCurrentDueDate();

        DateManipulator dm = new DateManipulator(date, MainActivity.DEVICE_LOCALE);
        String dueDateString = dm.getDateString(date, DateManipulator.DATE_FORMAT_DATE_PICKER);
        String dueTimeString = dm.getDateString(date, dm.get24HourFormatString(getActivity()));
        dueDatePickEditText.setText(dueDateString);
        dueTimePickEditText.setText(dueTimeString);

        return date;
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
                    // color 선택되었을 때
                    final int color = contextCompatUtils.convertButtonBackgroundColorToColorId(checkedId);
                    viewModel.setColor(color);
                }
            });
        }
    }

    private void setupReminderArea() {
        if (getActivity() != null) {
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
        switchOfCompleteButton = getActivity().findViewById(R.id.add_item_switch_complete);
        switchOfCompleteButton.setOnCheckedChangeListener(this);
    }


    /** Animation Handling for making layout visible */
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

    /** Listener for View.onClick*/
    @Override
    public void onClick(View v) {
        DateManipulator currentTimeStandard = new DateManipulator(new Date(), MainActivity.DEVICE_LOCALE);

        switch (v.getId()) {
            case R.id.add_item_color_switch_layout_linear:
                switchOfColorLabelButton.performClick();
                break;
            case R.id.add_item_reminder_switch_layout_linear:
                switchOfReminderButton.performClick();
                break;
            case R.id.add_item_complete_switch_layout_linear:
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
            case R.id.fab:
                Timber.d("[ Fab.onClick in AddEditTaskFragment ]");
                keypadUtils.hideKeypad(fab);
                if (isTitleEmpty(titleEditText)) {
                    titleEditText.setError(getResources().getString(R.string.title_error));
                    return;
                }
                /* Title, Description, DueDate는 OnChangedListener로 변화마다 ViewModel에 Post하는 것이 아니기 때문에 저장하기 전에 최종적으로 post 해준다. */
                viewModel.setTitle(titleEditText.getText().toString());
                viewModel.setDescription(descriptionEditText.getText().toString());
                viewModel.setDueDate(keepDueDate);
                viewModel.saveTask();
                break;
        }
    }

    /** Listener for SwitchCompat Buttons */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int viewId = buttonView.getId();
        keypadUtils.hideKeypad(fab);
        switch (viewId) {
            case R.id.add_item_switch_color_label:
                if (!isChecked)
                    viewModel.setColor(Task.DEFAULT_COLOR);
                setLayoutVisibleWithAnimations(radioGroup,700, 500, isChecked);
                break;

            case R.id.add_item_switch_reminder:
                if (!isChecked)
                    keepDueDate = null;
                else
                    keepDueDate = setExistingDateValue(keepDueDate);

                viewModel.setDueDate(keepDueDate);
                setLayoutVisibleWithAnimations(reminderLayout,500, 500, isChecked);
                break;

            case R.id.add_item_switch_complete:
                viewModel.setCompleted(isChecked);
                break;
        }

    }

    /** Date & Time Picker */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        DateManipulator dm = new DateManipulator(keepDueDate, MainActivity.DEVICE_LOCALE);

        Calendar reminderCalendar = dm.getCalendar();
        reminderCalendar.set(year, month, dayOfMonth);

        if (dm.compareCalendar(Calendar.getInstance(), reminderCalendar) < 0) {
            Toast.makeText(getActivity(), getString(R.string.err_before_today), Toast.LENGTH_LONG).show();
            return;
        }

        keepDueDate = dm.setDate(reminderCalendar, keepDueDate, year, month, dayOfMonth);
        dueDatePickEditText.setText(dm.getDateString(keepDueDate, DateManipulator.DATE_FORMAT_DATE_PICKER));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        DateManipulator dm = new DateManipulator(keepDueDate, MainActivity.DEVICE_LOCALE);

        Calendar reminderCalendar = dm.getCalendar();
        reminderCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        reminderCalendar.set(Calendar.MINUTE, minute);

        if (dm.compareCalendar(Calendar.getInstance(), reminderCalendar) < 0) {
            Toast.makeText(getActivity(), getString(R.string.err_before_today), Toast.LENGTH_LONG).show();
            return;
        }

        keepDueDate = dm.setTime(reminderCalendar, keepDueDate, hourOfDay, minute);
        dueTimePickEditText.setText(dm.getDateString(keepDueDate, dm.get24HourFormatString(getActivity())));
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
}
