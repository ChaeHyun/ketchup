package com.ketchup.tasklist;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ketchup.R;
import com.ketchup.addedit.AddEditTaskFragment;

import timber.log.Timber;

public class TaskViewHolder extends RecyclerView.ViewHolder {
    private TextView titleTextView;
    private TextView dueDateTextView;
    private TextView descTextView;
    private LinearLayout colorLabel;
    private String taskId;


    public TaskViewHolder(@NonNull View itemView) {
        super(itemView);
        titleTextView = itemView.findViewById(R.id.task_item_title);
        dueDateTextView = itemView.findViewById(R.id.task_item_due_date);
        descTextView = itemView.findViewById(R.id.task_item_description);
        colorLabel = itemView.findViewById(R.id.task_item_layout_linear_color_label);

        // For Testing : On-Click Method
        itemView.setOnClickListener((v) -> {
            Bundle bundle = new Bundle();
            bundle.putString(AddEditTaskFragment.TASK_ID, taskId);

            //navController.navigate(R.id.action_task_list_to_addEditTaskFragment, bundle);

            String title = titleTextView.getText().toString();
            Toast.makeText(v.getRootView().getContext(), title + " Clicked!\ntaskId: " + taskId, Toast.LENGTH_LONG).show();
            Timber.d("[Check] - %s, %s", taskId, title);
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

