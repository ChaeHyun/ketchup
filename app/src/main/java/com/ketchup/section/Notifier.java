package com.ketchup.section;

import com.ketchup.section.Section;

// ListUpdateCallback 을 받을 때 실행할 메소드를 인터페이스로 제공.
public interface Notifier {
    void notifySectionItemMoved(Section section, int fromPosition, int toPosition);

    void notifySectionRangeChanged(Section section, int positionStart, int itemCount, Object payload);

    void notifySectionRangeInserted(Section section, int positionStart, int itemCount);

    void notifySectionRangeRemoved(Section section, int positionStart, int itemCount);
}
