package com.ketchup.tasklist;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.ketchup.section.NewAdapter;

import timber.log.Timber;

/**
 * adapter 와 상호작용하는 메소드들을 추가해야한다.
 * getAdapterPosition()메소드를 통해서 Adapter 로부터 어느 아이템에 포커스가
 * 맞춰져있는지 알 수 있다.
 * */
public class BaseViewHolder<M> extends RecyclerView.ViewHolder {
    private M item;
    //private TaskAdapterRenewal adapter;
    private NewAdapter adapter;

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public void setAdapter(NewAdapter adapter) {
        this.adapter = adapter;
    }

    public void setItem(M item) {
        this.item = item;
    }

    public M getItem() {
        return item;
    }

    public NewAdapter getAdapter() { return adapter; }

    public boolean isInActionMode() {
        return adapter.isInActionMode();
    }

    public void onItemClick() {
        Timber.d("[BaseViewHolder . onItemClick() ] adapter == null : %s", adapter == null);
        if (adapter != null)
            adapter.onItemClicked(getAdapterPosition());
    }

    public void toggleSectionExpansion() {
        adapter.onSectionExpansionToggle(getAdapterPosition());
    }

    public boolean isSectionExpanded() {
        return adapter.isSectionExpanded(getAdapterPosition());
    }
}
