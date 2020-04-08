package com.ketchup.section;

public class ItemSection<M> extends Section {
    private M item;


    public ItemSection() {

    }

    public ItemSection(M item) {
        this.item = item;
    }

    public M getItem() {
        return item;
    }

    public void setItem(M item) {
        if (item == null)
            return;

        this.item = item;
        // when newly added
        onInserted(0, 1);
    }

    public void replaceItem(M item) {
        if (item == null)
            return;

        this.item = item;
        // when previous value exists.
        onChanged(0, 1, null);
    }

    public void removeItem() {
        this.item = null;
        onRemoved(0, 1);
    }

    private boolean isItemShowing() {
        return isSectionVisible() && item != null;
    }



    /** Implementations : Section's Methods */
    @Override
    int getCount() {
        return isItemShowing() ? 1 : 0;
    }

    @Override
    Object getItem(int position) {
        return item;
    }

    @Override
    boolean isItemSelected(int position) {
        // later
        return false;
    }

    @Override
    void onItemDismiss(int position) {
        this.item = null;
        onRemoved(position, 1);
    }

    /** There is no expansion of section in ItemSection. */
    @Override
    int onSectionExpansionToggle(int itemPosition) {
        return itemPosition - getCount();
    }

    @Override
    void collapseAllItems() {
        if (isItemShowing())
            onChanged(0, 1, null);
    }
}
