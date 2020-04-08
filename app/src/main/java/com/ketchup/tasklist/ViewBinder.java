package com.ketchup.tasklist;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ketchup.section.NewAdapter;


/**
 * Adapter 에게 어떤 형태의 ViewHolder 제공할지 중간에서 상호연결하는 다리 역할이다.
 * Adapter는 ViewBinder를 통해서 ViewHolder를 제공받는다.
 *
 * 1. Create ViewHolder
 * 2. Bind ViewHolder
 * 3. Judge this object can be bound to the viewHolder.
 * */
public abstract class ViewBinder<M, VH extends BaseViewHolder<M>> {

    public ViewBinder() {

    }

    public View inflate(ViewGroup parent, int layoutResId) {
        return LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
    }


    // Right place to set OnClickListener to holder or to update contents.
    // abstract 여야되지 않을까
    public abstract void initViewHolder(VH holder);

    public abstract VH createViewHolder(ViewGroup parent);
    public abstract void bindViewHolder(VH holder, M item);
    public abstract boolean canBindData(Object item);


    // Adapter에서 바인더르 사용해서 뷰홀더를 얻기 위해서는 이 메소드를 사용해서 adapter와 커넥션을 만들어줘야만한다.
    public VH createViewHolder(ViewGroup parent, NewAdapter adapter) {
        VH viewHolder = createViewHolder(parent);
        viewHolder.setAdapter(adapter);
        initViewHolder(viewHolder);
        return viewHolder;
    }

}
