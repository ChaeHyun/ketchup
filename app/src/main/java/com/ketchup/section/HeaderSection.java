package com.ketchup.section;

import androidx.annotation.NonNull;

import timber.log.Timber;

public class HeaderSection<H> extends NestedSection implements Notifier {

    private final ItemSection<H> itemSection;
    private final NestedSection nestedSection;

    //private OnItemClickListener<H> onItemClickListener;

    public HeaderSection() {
        super();

        itemSection = new ItemSection<>();
        nestedSection = new NestedSection();

        super.addSection(itemSection);
        super.addSection(nestedSection);
    }

    public HeaderSection(H header) {
        this();
        itemSection.setItem(header);
    }

    public H getHeader() {
        return itemSection.getItem();
    }

    public void setHeader(@NonNull H header) {
        itemSection.setItem(header);
    }

    @Override
    public void addSection(Section section) {
        nestedSection.addSection(section);
    }


    @Override
    void collapseSection() {
        Timber.d("   collapseSection()");
        if (nestedSection.isSectionVisible()) {
            nestedSection.hideSection();
            onChanged(0, 1, null);
        }
    }

    @Override
    public boolean isSectionExpanded(int itemPosition) {
        return nestedSection.isSectionVisible();
    }

//    public void setOnItemClickListener(OnItemClickListener<H> onItemClickListener) {
//        this.onItemClickListener = onItemClickListener;
//    }

    @Override
    int onSectionExpansionToggle(int itemPosition) {
        // 여기 headerSection에 해당하는 expansion 을 처리한 후 나머지 itemPosition 값을 계산해서 리턴한다.
        Timber.d("   onSectionExpansionToggle() - itemPos : %d,", itemPosition);
        int prevCount = getCount();

        // prevCount = 3, itemPosition 0
        if (itemPosition < getCount() && itemPosition >= 0) {
            if (!nestedSection.isSectionVisible()) {
                Timber.d("   Header's NestedSection.showSection()");
                nestedSection.showSection();
            } else {
                Timber.d("   Header's NestedSection.hideSection()");
                nestedSection.hideSection();
            }

            Timber.d("   onChanged(0 , 1) run");
            onChanged(0, 1, null);
        }
        return itemPosition - prevCount;
    }
}
