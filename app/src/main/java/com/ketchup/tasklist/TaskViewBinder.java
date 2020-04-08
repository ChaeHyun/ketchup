package com.ketchup.tasklist;

import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ketchup.R;
import com.ketchup.model.task.Task;
import com.ketchup.utils.DateManipulator;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class TaskViewBinder extends ViewBinder<Task, TaskViewHolder> {


    /** 파라미터가 더 존재하는 ViewHolder 의 경우 어떻게 handling 할 것인가? */
    @Override
    public TaskViewHolder createViewHolder(ViewGroup parent) {
        return new TaskViewHolder(inflate(parent, R.layout.task_item));
    }

    @Override
    public void initViewHolder(TaskViewHolder holder) {
        holder.initTaskItemOnClick(holder.itemView);

        // 이럴꺼면 holder에서 init할꺼 메소드 만들고 binder에서 호출해야하지 않나?
    }

    @Override
    public void bindViewHolder(TaskViewHolder holder, Task item) {
        holder.setTaskId(item.getUuid());
        holder.getTitleTextView().setText(item.getTitle());
        holder.getDescTextView().setText(item.getDescription());
        holder.getColorLabel().setBackgroundColor(item.getColorLabel());

        // set the value of dueDate.
        TextView dueDateTextView = holder.getDueDateTextView();
        if (item.getDueDate() != null) {
            DateManipulator dm = new DateManipulator(item.getDueDate(), Locale.KOREA);
            dueDateTextView.setText(dm.getDateString(item.getDueDate()));
            dueDateTextView.setVisibility(View.VISIBLE);
        } else {
            dueDateTextView.setText(null);
            dueDateTextView.setVisibility(View.GONE);
        }

        List<TextView> textViews = Arrays.asList(holder.getTitleTextView(), holder.getDescTextView(), dueDateTextView);
        strikeOut(textViews, item.isCompleted());

    }

    @Override
    public boolean canBindData(Object item) {
        return item instanceof Task;
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
}
