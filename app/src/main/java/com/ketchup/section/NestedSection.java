package com.ketchup.section;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;


/** NestedSection can have multiple different sections in it.
 * The adapter has a NestedSection as primitive and it is the root node for all other sections. */
public class NestedSection extends Section implements Notifier {

    final List<Section> sections = new ArrayList<>();
    private int count = -1;


    // 파라미터 Section의 인덱스가 전체 어댑터의 기준에서 몇번째 position 값을 가지고 있는지 계산한다.
    private int getAdapterPosition(Section section, int position) {
        int sectionIndex = sections.indexOf(section);
        if (sectionIndex < 0) {
            // Error
            throw new IllegalStateException("Section does not exist in parent!");
        }

        for (int i = 0; i < sectionIndex; i++) {
            position += sections.get(i).getCount();
        }
        return position;
    }

    public void addSection(Section section) {
        section.setNotifier(this);

        sections.add(section);
        section.onInserted(0, section.getCount());
    }

    void removeAllSections() {
        for (Section section: sections) {
           section.setNotifier(null);
        }
        sections.clear();
        count = -1;
    }

    @Override
    public void showSection() {
        if (isSectionHidden()) {
            for (Section section : sections)
                section.showSection(false);
        }
        super.showSection();
    }

    @Override
    public void hideSection() {
        if (isSectionVisible()) {
            for (Section section : sections)
                section.hideSection(false);
        }
        super.hideSection();
    }

    @Override
    void onItemClicked(int position) {
        for (Section section : sections) {
            // position 값이 알맞은 섹션을 가리킬때까지 탐색한다.
            if (position >= section.getCount()) {
                position -= section.getCount();
            } else {
                section.onItemClicked(position);
                Timber.d("[ NestedSection . onItemClicked() ] : %d", position);
                return;
            }
        }
    }

    @Override
    void collapseSection() {
        for (Section section: sections) {
            section.collapseSection();
        }
    }

    @Override
    void onDataSetChanged() {
        super.onDataSetChanged();
        // 갱신되었다는 의미 - 하위 Section 들의 정보가 바뀌었으므로 다시 계산해야한다.
        count = -1;
    }


    /** Implementations of Section's abstract methods */
    // Adapter 내의 position 값을 어떤 Section 의 몇 번째 position 값인지 계산하는 과정.
    @Override
    int getCount() {
        // 하위 sections 가 변화되고 계산한 적 없다.
        if (count < 0) {
            if (isSectionVisible()) {
                int itemCount = 0;
                for (Section section : sections) {
                    itemCount += section.getCount();
                }
                count = itemCount;
            } else {
                count = 0;
            }
        }

        return count;
    }

    @Override
    Object getItem(int position) {
        for (Section section : sections) {
            if (position >= section.getCount()) {
                position -= section.getCount();
            } else {
                return section.getItem(position);
            }
        }
        throw new IllegalStateException("There is no item at the position");
    }

    @Override
    boolean isItemSelected(int position) {
        for (Section section : sections) {
            if (position >= section.getCount()) {
                position -= section.getCount();
            } else {
                return section.isItemSelected(position);
            }
        }
        return false;
    }

    @Override
    void onItemDismiss(int position) {
        for (Section section : sections) {
            if (position >= section.getCount()) {
                position -= section.getCount();
            } else {
                section.onItemDismiss(position);
                return;
            }
        }
    }

    // onChildSectionExpansionToggle 을 실행한다.
    @Override
    int onSectionExpansionToggle(int itemPosition) {
        onChildSectionExpansionToggle(itemPosition);
        return itemPosition - getCount();
    }

    /** NestedSection의 자식들을 순회하면서 itemPosition 값에 해당하는 Section 일 경우 Expansion 하도록 지시하는 역할. */
    void onChildSectionExpansionToggle(int itemPosition) {
        for (Section section : sections) {
            itemPosition = section.onSectionExpansionToggle(itemPosition);

            if (itemPosition < 0) {
                return;
            }
        }
    }

    @Override
    public boolean isSectionExpanded(int itemPosition) {
        Timber.d("isSectionExpanded()");
        for (Section section : sections) {
            if (itemPosition >= section.getCount()) {
                itemPosition -= section.getCount();
            } else {
                return section.isSectionExpanded(itemPosition);
            }
        }
        return false;
    }

    @Override
    void collapseAllItems() {
        for (Section section : sections) {
            section.collapseAllItems();
        }
    }

    void collapseAllSections() {
        for (Section section : sections)
            section.collapseSection();
    }

    /** Implementations of Notifier interface */
    @Override
    public void notifySectionItemMoved(Section section, int fromPosition, int toPosition) {
        int fromPositionInNestedSection = getAdapterPosition(section, fromPosition);
        int toPositionInNestedSection = getAdapterPosition(section, toPosition);
        onMoved(fromPositionInNestedSection, toPositionInNestedSection);
    }

    @Override
    public void notifySectionRangeChanged(Section section, int positionStart, int itemCount, Object payload) {
        Timber.d(" #notifty#onChanged(%d, %d) , from index: %d", positionStart, itemCount, getAdapterPosition(section, positionStart));
        onChanged(getAdapterPosition(section, positionStart), itemCount, payload);
    }

    @Override
    public void notifySectionRangeInserted(Section section, int positionStart, int itemCount) {
        Timber.d(" #notifty#onInserted(%d, %d) , from index: %d", positionStart, itemCount, getAdapterPosition(section, positionStart));
        onInserted(getAdapterPosition(section, positionStart), itemCount);
    }

    @Override
    public void notifySectionRangeRemoved(Section section, int positionStart, int itemCount) {
        Timber.d(" #notifty#onRemoved(%d, %d) , from index: %d", positionStart, itemCount, getAdapterPosition(section, positionStart));
        onRemoved(getAdapterPosition(section, positionStart), itemCount);
    }
}
