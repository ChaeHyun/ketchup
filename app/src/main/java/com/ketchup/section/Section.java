package com.ketchup.section;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ListUpdateCallback;

import timber.log.Timber;

public abstract class Section implements ListUpdateCallback {
    // private Mode selectionMode = DEFAULT;
    private boolean isSectionHidden = false;
    private Notifier notifier;

    public void showSection() {
        showSection(true);
    }

    public void hideSection() {
        hideSection(true);
    }

    public boolean isSectionHidden() {
        return isSectionHidden;
    }

    public boolean isSectionVisible() {
        return !isSectionHidden;
    }

    public boolean isSectionExpanded(int itemPosition) {
        return false;
    }

    void collapseSection() {

    }


    // Notifier 는 NestedSection#addSection() 에서 처음으로 set 된다.
    // 그리고 이미 notifier 를 가지고 있는 Section 은 추가할 수 없다.
    void setNotifier(Notifier notifier) {
        this.notifier = notifier;
    }

    Notifier getNotifier() {
        return notifier;
    }

    void onItemClicked(int position) {

    }

    void onDataSetChanged() {

    }

    void showSection(boolean notify) {
        if (isSectionHidden) {
            isSectionHidden = false;
            onDataSetChanged();

            if (notify) {
                onInserted(0, getCount());
            }
        }
    }

    void hideSection(boolean notify) {
        if (!isSectionHidden) {
            int count = getCount(); // if (hidden == true) getCount -> 0
            isSectionHidden = true;

            if (notify) {
                onRemoved(0, count);
            }
        }

    }

    abstract int getCount();

    abstract Object getItem(int position);

    abstract boolean isItemSelected(int position);

    abstract void onItemDismiss(int position);

    abstract int onSectionExpansionToggle(int itemPosition);

    abstract void collapseAllItems();


/** ListUpdateCallback
 * 리스트에 적용된 업데이트가 있을 때 콜백을 받을 수 있다.
 * 변화에 따라서 Section 내의 아이템에 변화를 적용할때 사용하는 것이 Notifier 이다.*/
    @Override
    public final void onInserted(int position, int count) {
        onDataSetChanged();
        if (notifier != null && isSectionVisible()) {
            Timber.d("onInserted() 실행");
            notifier.notifySectionRangeInserted(this, position, count);
        }
    }

    @Override
    public final void onRemoved(int position, int count) {
        onDataSetChanged();
        if (notifier != null) {
            Timber.d("onRemoved() 실행");
            notifier.notifySectionRangeRemoved(this, position, count);
        }
    }

    @Override
    public final void onMoved(int fromPosition, int toPosition) {
        onDataSetChanged();
        if (notifier != null && isSectionVisible()) {
            notifier.notifySectionItemMoved(this, fromPosition, toPosition);
        }
    }

    @Override
    public final void onChanged(int position, int count, @Nullable Object payload) {
        onDataSetChanged();
        if (notifier != null && isSectionVisible()) {
            Timber.d("onChanged() 실행 s:%d, c:%d", position, count);
            notifier.notifySectionRangeChanged(this, position, count, payload);
        }
    }
}
