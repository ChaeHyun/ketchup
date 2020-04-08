package com.ketchup.tasklist;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.ketchup.AdapterType;
import com.ketchup.R;
import com.ketchup.addedit.AddEditTaskFragment;
import com.ketchup.model.category.CategoryWithTasks;
import com.ketchup.model.task.ItemType;
import com.ketchup.model.task.Task;
import com.ketchup.utils.DateManipulator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;


public class TaskAdapterRenewal extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements TaskItemOnClick, HeaderItemOnClick {

    private List<AdapterType> data;
    private NavController navController;
    private View rootView;

    public TaskAdapterRenewal(final Fragment fragment) {
        super();
        //this.data = data;
        this.navController = NavHostFragment.findNavController(fragment);
        this.rootView = fragment.getView();
    }

    public void setData(final List<AdapterType> data) {
        if (data == null) {
            this.data = new ArrayList<>();
            notifyDataSetChanged();
            return;
        }

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
        if (header.getCount() == 0)
            return;

        Timber.d("HEADER : %s", header.isFolded());
        if (!header.isFolded()) {
            data.addAll(pos+1, header.tasks);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemType type = ItemType.getItemType(viewType);

        switch (type) {
            case HEADER:
//                View view = LayoutInflater.from(parent.getContext())
//                        .inflate(R.layout.header_item, parent, false);
//                return new HeaderViewHolder(view, rootView, this);
            case CHILD:
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.task_item, parent, false);
                return new TaskViewHolder(view, this);
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
                headerViewHolder.header = header;
                headerViewHolder.headerPosition = item;
                headerViewHolder.header_title.setText(header.category.getName());
                headerViewHolder.initHeaderIcon(header.isFolded());

                break;
            case CHILD:
                /** onBindViewHolder 부분이 타입에 맞는 ViewBinder 로 옮겨져야할 로직이다.
                 * Task 를 다루고 있으니까 TaskViewBinder 에서 처리되어야한다. **/
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
    private void strikeOut(final List<TextView> textViews, final boolean strikeout) {
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

    @Override
    public void navigateToAddEditTaskFragment(final String taskId) {
        Bundle bundle = new Bundle();
        bundle.putString(AddEditTaskFragment.TASK_ID, taskId);

        Timber.d("taskID: [ %s ] is selected.", taskId);
        navController.navigate(R.id.action_task_list_to_addEditTaskFragment, bundle);
    }


    // 필요한것, Header, data list 내에 header 가 위치한 index
    // header는 children을 갖고있고 tasks.size도 알고 있다.
    @Override
    public void insertChildrenOfHeader(CategoryWithTasks header) {
        Timber.d("current state : folded / Action : To unfold the list.");
        int pos = data.indexOf(header);
        int index = pos + 1;
        for (Task task : header.tasks)
            data.add(index++, task);

        notifyItemRangeInserted(pos + 1, header.getCount());
        header.setFolded(false);
    }

    @Override
    public void removeChildrenOfHeader(CategoryWithTasks header) {
        Timber.d("current state: unfolded / Action : To fold the list.");
        int pos = data.indexOf(header);

        data.removeAll(header.tasks);
        notifyItemRangeRemoved(pos + 1, header.getCount());
        header.setFolded(true);
    }

    public TaskItemOnClick getTaskItemOnClickHelper() {
        return this;
    }
}
