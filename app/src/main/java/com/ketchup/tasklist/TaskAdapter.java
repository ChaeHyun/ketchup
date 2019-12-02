package com.ketchup.tasklist;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.ketchup.R;
import com.ketchup.addedit.AddEditTaskFragment;
import com.ketchup.model.task.Task;
import com.ketchup.utils.DateManipulator;

import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> itemList;
    private View itemView;

    private NavController navController;

    public TaskAdapter(NavController navController) {
        this.navController = navController;
    }

    public void setTasks(List<Task> tasks) {
        Timber.i("setTasks( tasks )");
        itemList = null;
        itemList = tasks;
        notifyDataSetChanged();
    }

    public void appendTasks(List<Task> tasks) {
        Timber.i("appendTasks( tasks )");
        itemList.addAll(tasks);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);

        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task item = itemList.get(position);

//        Timber.d("item : %s", item.getTitle());
//        Timber.d("taskId : %s", item.getUuid());
        holder.taskId = item.getUuid();
        holder.titleTextView.setText(item.getTitle());
        holder.descTextView.setText(item.getDescription());
        holder.colorLabel.setBackgroundColor(item.getColorLabel());

        // set dueDate Value later
        if (item.getDueDate() != null) {
            DateManipulator dm = new DateManipulator(item.getDueDate(), Locale.KOREA);
            holder.dueDateTextView.setText(dm.getDateString(item.getDueDate()));
            holder.dueDateTextView.setVisibility(View.VISIBLE);
        } else {
            holder.dueDateTextView.setText(null);
            holder.dueDateTextView.setVisibility(View.GONE);

        }

        // Complete Item - Strike through
        if (item.isCompleted()) {
            holder.titleTextView.setPaintFlags(holder.titleTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.descTextView.setPaintFlags(holder.descTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.dueDateTextView.setPaintFlags(holder.dueDateTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.titleTextView.setPaintFlags(holder.titleTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.descTextView.setPaintFlags(holder.descTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.dueDateTextView.setPaintFlags(holder.dueDateTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {

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

                navController.navigate(R.id.action_task_list_to_addEditTaskFragment, bundle);

                String title = titleTextView.getText().toString();
                Toast.makeText(v.getRootView().getContext(), title + " Clicked!\ntaskId: " + taskId, Toast.LENGTH_LONG).show();
                Timber.d("[Check] - %s, %s", taskId, title);
            });
        }
    }
}
