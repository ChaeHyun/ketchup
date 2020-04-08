package com.ketchup.tasklist;

import android.view.ViewGroup;

import com.ketchup.R;
import com.ketchup.model.category.CategoryWithTasks;

import timber.log.Timber;

public class HeaderViewBinder extends ViewBinder<CategoryWithTasks, HeaderViewHolder> {

    @Override
    public HeaderViewHolder createViewHolder(ViewGroup parent) {
        return new HeaderViewHolder(inflate(parent, R.layout.header_item));
    }

    @Override
    public void bindViewHolder(HeaderViewHolder holder, CategoryWithTasks item) {
        Timber.d(" bindViewHolder()");
        holder.header_title.setText(item.category.getName());
        holder.header = item;
        holder.initHeaderIcon(item.isFolded());
        //holder.initHeaderIcon(!holder.isSectionExpanded());

    }

    @Override
    public boolean canBindData(Object item) {
        return item instanceof CategoryWithTasks;
    }

    @Override
    public void initViewHolder(HeaderViewHolder holder) {

    }
}
