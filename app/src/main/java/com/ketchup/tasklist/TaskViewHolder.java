package com.ketchup.tasklist;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.ketchup.R;
import com.ketchup.model.task.Task;

public class TaskViewHolder extends BaseViewHolder<Task> {
    private TextView titleTextView;
    private TextView dueDateTextView;
    private TextView descTextView;
    private LinearLayout colorLabel;
    private String taskId;

    private TaskItemOnClick taskItemOnClick;

    public TaskViewHolder(@NonNull View itemView) {
        super(itemView);
        titleTextView = itemView.findViewById(R.id.task_item_title);
        dueDateTextView = itemView.findViewById(R.id.task_item_due_date);
        descTextView = itemView.findViewById(R.id.task_item_description);
        colorLabel = itemView.findViewById(R.id.task_item_layout_linear_color_label);
    }

    public TaskViewHolder(@NonNull View itemView, final TaskItemOnClick taskItemOnClick) {
        super(itemView);
        titleTextView = itemView.findViewById(R.id.task_item_title);
        dueDateTextView = itemView.findViewById(R.id.task_item_due_date);
        descTextView = itemView.findViewById(R.id.task_item_description);
        colorLabel = itemView.findViewById(R.id.task_item_layout_linear_color_label);

        // 분리해서 initViewHolder 메소드에서 처리한다.
        itemView.setOnClickListener(v -> taskItemOnClick.navigateToAddEditTaskFragment(taskId));
    }

    // binder가 생성자 뒤에 바로 이어서 호출할 initViewHolder에서 호출되도록 해야한다.
    public void initTaskItemOnClick(View itemView) {
        //taskItemOnClick = getAdapter().getTaskItemOnClickHelper();
        //itemView.setOnClickListener(v -> taskItemOnClick.navigateToAddEditTaskFragment(taskId));
        itemView.setOnClickListener(v -> {
            super.onItemClick();
        });
    }

    public TextView getTitleTextView() {
        return titleTextView;
    }

    public void setTitleTextView(TextView titleTextView) {
        this.titleTextView = titleTextView;
    }

    public TextView getDueDateTextView() {
        return dueDateTextView;
    }

    public void setDueDateTextView(TextView dueDateTextView) {
        this.dueDateTextView = dueDateTextView;
    }

    public TextView getDescTextView() {
        return descTextView;
    }

    public void setDescTextView(TextView descTextView) {
        this.descTextView = descTextView;
    }

    public LinearLayout getColorLabel() {
        return colorLabel;
    }

    public void setColorLabel(LinearLayout colorLabel) {
        this.colorLabel = colorLabel;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}

