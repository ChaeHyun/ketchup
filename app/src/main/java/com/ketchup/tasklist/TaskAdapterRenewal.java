package com.ketchup.tasklist;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ketchup.AdapterType;
import com.ketchup.R;
import com.ketchup.model.CategoryWithTasks;
import com.ketchup.model.task.ItemType;
import com.ketchup.model.task.Task;
import com.ketchup.utils.DateManipulator;

import java.util.List;
import java.util.Locale;

import timber.log.Timber;


public class TaskAdapterRenewal extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<AdapterType> data;

    public TaskAdapterRenewal() {
        super();
        //this.data = data;
    }

    public void setData(List<AdapterType> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void setTasks(List<Task> tasks) {

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        ItemType type = ItemType.getItemType(viewType);

        switch (type) {
            case HEADER:
                LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.header_item, parent, false);

                return new HeaderViewHolder(view);
            case CHILD:
                inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.task_item, parent, false);
                return new TaskViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final AdapterType item = data.get(position);

        switch(item.getItemType()) {
            case HEADER:
                final CategoryWithTasks header = (CategoryWithTasks) item;
                final HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                headerViewHolder.headerPosition = item;
                headerViewHolder.header_title.setText(header.category.getName());

                if (header.category.isFolded())
                    headerViewHolder.header_icon.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
                else
                    headerViewHolder.header_icon.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);

                // onClick Listener for the icon  - fold / unfold
                headerViewHolder.header_icon.setOnClickListener(view -> {
                    // unfold == expand
                    if (header.category.isFolded()) {
                        Timber.d("current state : folded / Action : To unfold the list.");
                        int pos = data.indexOf(headerViewHolder.headerPosition);
                        int index = pos + 1;
                        for (Task task : header.tasks) {
                            data.add(index++, task);
                        }

                        notifyItemRangeInserted(pos + 1, header.getCount());
                        headerViewHolder.header_icon.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
                        header.category.setFolded(false);
                    }
                    // fold
                    else {
                        Timber.d("current state: unfolded / Action : To fold the list.");
                        int pos = data.indexOf(headerViewHolder.headerPosition);

                        data.removeAll(header.tasks);
                        notifyItemRangeRemoved(pos + 1, header.getCount());
                        headerViewHolder.header_icon.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
                        header.category.setFolded(true);
                    }
                });
                break;
            case CHILD:
                final Task task = (Task) item;
                final TaskViewHolder childViewHolder = (TaskViewHolder) holder;

                childViewHolder.setTaskId(task.getUuid());
                childViewHolder.getTitleTextView().setText(task.getTitle());
                childViewHolder.getDescTextView().setText(task.getDescription());
                childViewHolder.getColorLabel().setBackgroundColor(task.getColorLabel());

                if (task.getDueDate() != null) {
                    DateManipulator dm = new DateManipulator(task.getDueDate(), Locale.KOREA);
                    childViewHolder.getDueDateTextView().setText(dm.getDateString(task.getDueDate()));
                    childViewHolder.getDueDateTextView().setVisibility(View.VISIBLE);
                } else {
                    childViewHolder.getDueDateTextView().setText(null);
                    childViewHolder.getDueDateTextView().setVisibility(View.GONE);
                }

                if (task.isCompleted()) {
                    childViewHolder.getTitleTextView().setPaintFlags(childViewHolder.getTitleTextView().getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    childViewHolder.getDescTextView().setPaintFlags(childViewHolder.getDescTextView().getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    childViewHolder.getDueDateTextView().setPaintFlags((childViewHolder.getDueDateTextView().getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG));
                } else {
                    childViewHolder.getTitleTextView().setPaintFlags(childViewHolder.getTitleTextView().getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    childViewHolder.getDescTextView().setPaintFlags(childViewHolder.getDescTextView().getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    childViewHolder.getDueDateTextView().setPaintFlags((childViewHolder.getDueDateTextView().getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG)));
                }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).getItemType().getCode();
    }
}
