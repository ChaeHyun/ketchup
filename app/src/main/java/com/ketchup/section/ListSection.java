package com.ketchup.section;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ListUpdateCallback;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import timber.log.Timber;

public class ListSection<M> extends Section {

    private List<M> itemList;
    private OnItemClickListener<M> onItemClickListener;
    // Update Callback 을 받았을 때 ListSection 에서 처리해야하는 업데이트를 구현한다.
    private final ListUpdateCallback listUpdateCallback = new ListUpdateCallback() {
        @Override
        public void onInserted(int position, int count) {
            for (int i = position; i < position + count; i++) {
                // 변경되는 아이템의 수
            }
            ListSection.super.onInserted(position, count);
        }

        @Override
        public void onRemoved(int position, int count) {
            ListSection.super.onRemoved(position, count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            ListSection.super.onMoved(fromPosition, toPosition);
        }

        @Override
        public void onChanged(int position, int count, @Nullable Object payload) {
            ListSection.super.onChanged(position, count, payload);
        }
    };

    //DiffUtil


    public ListSection() {
        itemList = new ArrayList<>();

    }

    public void add(M item) {
        add(itemList.size(), item);
    }

    public void add(int index, M item) {
        add(index, item, isSectionVisible());
    }

    private void add(int index, M item, boolean notify) {
        this.itemList.add(index, item);
        if (notify) {
            onInserted(index, 1);
        }
    }

    public boolean addAll(Collection<? extends M> items) {
        return this.itemList.addAll(this.itemList.size(), items);
    }

    public boolean addAll(int index, Collection<? extends M> items) {
        return addAll(index, items, isSectionVisible());
    }

    private boolean addAll(int index, Collection<? extends M> items, boolean notify) {
        boolean result = this.itemList.addAll(index, items);

        if (result) {
            if (notify) {
                onInserted(index, items.size());
            }
        }

        return result;
    }

    public void clear() {
        if (itemList.size() <= 0) {
            return;
        }

        int oldSize = itemList.size();
        itemList.clear();

        if (isSectionVisible()) {
            onRemoved(0, oldSize);
        }
    }

    public M get(int index) {
        return itemList.get(index);
    }

    public List<M> getItemList() {
        return new ArrayList<>(itemList);
    }

    public void remove(int index) {
        itemList.remove(index);
        if (isSectionVisible())
            onRemoved(index, 1);
    }

    public void set(int index, M item) {
        M oldItem = this.itemList.get(index);
        this.itemList.set(index, item);
        onChanged(index, 1, null);
    }

    public void set(List<M> items) {
        List<M> oldItems = new ArrayList<>(this.itemList);
        List<M> newItems = new ArrayList<>(items);
        this.itemList = new ArrayList<>(items);

        // 추후에 DiffUtil 사용해서 바뀐것만 선별해서 업데이트 하기
        if (isSectionVisible()) {
//            diffUtil.calculateDiff(listUpdateCallback, oldItems, newItems);
        }
    }

    public void setOnItemClickListener(OnItemClickListener<M> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    /** Implementations : Methods of Sections */

    // ListSection 이 넘겨받는 position 값은 항상 NestedSection 에 의해 계산되어진 후 넘겨진다.
    @Override
    void onItemClicked(int position) {
        if (onItemClickListener == null) {
            Timber.d(" 현재 ListSection 의 onItemClickListener 가 Null 입니다.");
        }
        if (onItemClickListener != null && itemList.size() > position) {
            Timber.d("[ ListSection. onItemClicked() 실행 ! ] : %d", position);
            onItemClickListener.onItemClicked(position, get(position));
        }
    }

    @Override
    int getCount() {
        //return (isSectionVisible() && this.itemList != null) ? itemList.size() : 0;
        return isSectionVisible() ? itemList.size() : 0;
    }

    @Override
    Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    boolean isItemSelected(int position) {
        // later
        return false;
    }

    @Override
    void onItemDismiss(int position) {
        M data = itemList.remove(position);
        onRemoved(position, 1);
    }

    @Override
    void collapseAllItems() {
        int itemPosition = 0;
    }


    /** There is no expansion of Section in ListSection. */
    @Override
    int onSectionExpansionToggle(int itemPosition) {
        return itemPosition - getCount();
    }
}
