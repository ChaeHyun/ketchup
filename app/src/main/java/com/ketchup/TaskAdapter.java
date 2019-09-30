package com.ketchup;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ketchup.model.ColorLabel;
import com.ketchup.model.task.Task;

import java.util.List;

import timber.log.Timber;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> itemList;
    private View itemView;


    public TaskAdapter() {

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

        Timber.d("item : %s", item.getTitle());

        holder.titleTextView.setText(item.getTitle());
        holder.descTextView.setText(item.getDescription());
        holder.colorLabel.setBackgroundColor(ColorLabel.DEFAULT.getColor());

        // set dueDate Value later
        //holder.dueDateTextView.setText("due date");
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


        private TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.task_item_title);
            dueDateTextView = itemView.findViewById(R.id.task_item_due_date);
            descTextView = itemView.findViewById(R.id.task_item_description);
            colorLabel = itemView.findViewById(R.id.task_item_layout_linear_color_label);

            // For Testing : On-Click Method
            itemView.setOnClickListener((v) -> {
                String title = titleTextView.getText().toString();
                Toast.makeText(v.getRootView().getContext(), title + " Clicked!", Toast.LENGTH_LONG).show();
            });
        }
    }
}
