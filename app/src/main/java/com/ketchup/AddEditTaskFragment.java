package com.ketchup;


import android.animation.Animator;
import android.animation.LayoutTransition;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.ketchup.tasklist.TaskListFragment;

import java.util.Date;

import dagger.android.support.DaggerFragment;
import timber.log.Timber;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddEditTaskFragment extends DaggerFragment
        implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static int TOOLBAR_DEFAULT_COLOR;

    public static final String MODE = "mode";
    public static final String TASK_ID = "taskId";

    private FloatingActionButton fab;

    // Toolbar
    private CollapsingToolbarLayout ctl;
    private Toolbar toolbar;

    // Task Info
    private EditText titleEditText;
    private EditText descriptionEditText;
    private LinearLayout colorLabelLayout;
    private Date writtenDate;
    private int colorLabel;
    private Date dueDate;
    private boolean complete;

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

    // Title Layout
    private TextInputLayout titleLayout;

    private ActionBarDrawerToggle toggle;       // DrawerLayout Listener
    private DrawerLayout drawer;

    private NavController navController;

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
        Timber.d("[ onViewCreated ]");


        setupDrawerAndToolbar();
        setupSwitchLayout();
        setupFab(R.id.fab);
        // **This must be called after initializing fab.
        navController = NavHostFragment.findNavController(this);
        navController.addOnDestinationChangedListener(onDestinationChangedListener);
        setupColorLabelArea();
        setupReminderArea();
        setupCompleteArea();

        // Check Bundle
        if (getArguments() != null) {
            String mode = getArguments().getString(MODE);
            String taskId = getArguments().getString(TASK_ID);

            Timber.d("[ Check Args. Values ]\nMode -> %s\ntaskId -> %s", mode, taskId);
        }

        // Task 정보 관련 뷰 초기화
        if (getActivity() != null) {
            titleEditText = getActivity().findViewById(R.id.add_item_edit_text_title);
            descriptionEditText = getActivity().findViewById(R.id.add_item_edit_text_description);
        }

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

                // method로 만들기
                fab.hide();
                fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_menu_send));
                fab.show();
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Timber.d("[ onCreate() ]");
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
        titleLayout.setVisibility(View.GONE);
        NavHostFragment.findNavController(this).removeOnDestinationChangedListener(onDestinationChangedListener);

        drawer.removeDrawerListener(toggle);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Timber.d(" HOME icon Selected.");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

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
                    toolbarColorChange(ctl, toolbar, TOOLBAR_DEFAULT_COLOR);
                } else {
                    //hideKeyboard
                }
                break;

            case R.id.add_item_switch_reminder:
                setLayoutVisibleWithAnimations(reminderLayout,500,500,isChecked);
                break;

            case R.id.add_item_switch_complete:
                complete = isChecked;
                break;
        }

    }

    private void setLayoutVisibleWithAnimations(View view, long showUpDuration, long disappearDuration, boolean isChecked) {
        float showUpAlpha = 1.0f;
        float disappearAlpha = 0.0f;
        if (isChecked) {
            view.animate().alpha(showUpAlpha)
                    .setDuration(showUpDuration)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            view.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {

                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
        } else {
            view.animate().alpha(disappearAlpha)
                    .setDuration(disappearDuration)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            view.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
        }

    }

    private void setupFab(@NonNull int viewId) {
        fab = getActivity().findViewById(viewId);

        fab.setOnClickListener(v -> {
            Timber.d("[ Fab.onClick in AddEditTaskFragment ]");

            Bundle bundle = new Bundle();
            bundle.putInt(TaskListFragment.TASK_FILTER, 100);
            NavHostFragment.findNavController(this).navigate(R.id.action_addEditTaskFragment_to_task_list, bundle);
        });
    }

    private void setupDrawerAndToolbar() {
        if (getActivity() == null)
            return;

        // Toolbar findViewById - it contains in MainActivity.
        ctl = getActivity().findViewById(R.id.activity_main_collapsing_toolbar);
        toolbar = getActivity().findViewById(R.id.toolbar);

        ctl.setTitle("");
        toolbar.setTitle("");


        TOOLBAR_DEFAULT_COLOR = ContextCompat.getColor(getActivity(), R.color.addItemToolbar);
        toolbarColorChange(ctl, toolbar, TOOLBAR_DEFAULT_COLOR);

        titleLayout = getActivity().findViewById(R.id.add_item_hint_title);
        titleLayout.setVisibility(View.VISIBLE);


        drawer = getActivity().findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        toggle.setDrawerIndicatorEnabled(false);
        toggle.setToolbarNavigationClickListener(v -> {
            navController.navigateUp();
            toggle.setDrawerIndicatorEnabled(true);

        });
        drawer.addDrawerListener(toggle);
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
                    final int color = convertButtonBackgroundColorToColorInteger(checkedId);
                    toolbarColorChange(ctl, toolbar, color);
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

    private void toolbarColorChange(CollapsingToolbarLayout ctl, Toolbar toolbar, int color) {
        ctl.setBackgroundColor(color);
        toolbar.setBackgroundColor(color);
    }

    private int convertButtonBackgroundColorToColorInteger(int checkedButtonId) {
        if (getActivity() == null)
            return -1;
        // Default Color
        int color = ContextCompat.getColor(getActivity(), R.color.addItemToolbar);

        switch (checkedButtonId) {
            case R.id.label_red :
                color = ContextCompat.getColor(getActivity(), R.color.labelRed);
                break;
            case R.id.label_blue:
                color = ContextCompat.getColor(getActivity(), R.color.labelBlue);
                break;
            case R.id.label_green:
                color = ContextCompat.getColor(getActivity(), R.color.labelGreen);
                break;
            case R.id.label_yellow:
                color = ContextCompat.getColor(getActivity(), R.color.labelYellow);
                break;
            case R.id.label_purple:
                color = ContextCompat.getColor(getActivity(), R.color.labelPurple);
                break;
        }

        return color;
    }



}
