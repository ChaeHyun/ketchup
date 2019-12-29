package com.ketchup.tasklist;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.ketchup.AdapterType;
import com.ketchup.R;
import com.ketchup.model.CategoryWithTasks;
import com.ketchup.model.task.ItemType;
import com.ketchup.model.task.Task;
import com.ketchup.utils.DateManipulator;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;


public class TaskAdapterRenewal extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private View view = null;
    private List<AdapterType> data;

    private NavController navController;

    public TaskAdapterRenewal(NavController navController) {
        super();
        //this.data = data;
        this.navController = navController;
    }

    public void setData(List<AdapterType> data) {
        /* HEADER일때 folded == false이면 Child Item 추가시켜준다. */
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getItemType() == ItemType.HEADER) {
                addChildTasks(data, i);
            }
        }
        this.data = data;
        notifyDataSetChanged();
    }

    private void addChildTasks(final List<AdapterType> data, int pos) {
        CategoryWithTasks header = (CategoryWithTasks) data.get(pos);
        Timber.d("HEADER : %s", header.category.isFolded());
        if (!header.category.isFolded()) {
            data.addAll(pos+1, header.tasks);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemType type = ItemType.getItemType(viewType);

        switch (type) {
            case HEADER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.header_item, parent, false);
                return new HeaderViewHolder(view);
            case CHILD:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.task_item, parent, false);
                return new TaskViewHolder(view, navController);
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

                // folded == true -> 접혀있는 상태이다
                if (header.category.isFolded())
                    headerViewHolder.header_icon.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
                else
                    headerViewHolder.header_icon.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);

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
                TextView titleTextView = childViewHolder.getTitleTextView();
                TextView descTextView = childViewHolder.getDescTextView();
                TextView dueDateTextView = childViewHolder.getDueDateTextView();

                childViewHolder.setTaskId(task.getUuid());
                titleTextView.setText(task.getTitle());
                descTextView.setText(task.getDescription());
                childViewHolder.getColorLabel().setBackgroundColor(task.getColorLabel());

                if (task.getDueDate() != null) {
                    DateManipulator dm = new DateManipulator(task.getDueDate(), Locale.KOREA);
                    dueDateTextView.setText(dm.getDateString(task.getDueDate()));
                    dueDateTextView.setVisibility(View.VISIBLE);
                } else {
                    dueDateTextView.setText(null);
                    dueDateTextView.setVisibility(View.GONE);
                }

                List<TextView> textViews = Arrays.asList(titleTextView, descTextView, dueDateTextView);
                strikeOut(textViews, task.isCompleted());
        }
    }

    // To use strike-out for the text views which it's tasks are completed.
    private void strikeOut(List<TextView> textViews, boolean strikeout) {
        for (TextView textView : textViews) {
            if (textView == null)
                continue;

            int paintFlag = textView.getPaintFlags();
            if (strikeout)
                textView.setPaintFlags(paintFlag| Paint.STRIKE_THRU_TEXT_FLAG);
            else
                textView.setPaintFlags(paintFlag & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).getItemType().getCode();
    }
}
