package com.ketchup.section;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.ketchup.R;
import com.ketchup.addedit.AddEditTaskFragment;
import com.ketchup.model.category.CategoryWithTasks;
import com.ketchup.model.task.Task;
import com.ketchup.tasklist.BaseViewHolder;
import com.ketchup.tasklist.ViewBinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;

public class NewAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private View rootView;
    private NavController navController;

    private final NestedSection nestedSection = new NestedSection();
    private final List<ViewBinder> viewBinders = new ArrayList<>();
    //private ItemTouchHelper itemTouchHelper;      // Darg & Swipe
    private boolean isInActionMode = false;
    // Adapter sets it's notifier to Section.
    private final Notifier notifier = new Notifier() {
        @Override
        public void notifySectionItemMoved(Section section, int fromPosition, int toPosition) {
            //onDataSetChanged();
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void notifySectionRangeChanged(Section section, int positionStart, int itemCount, Object payload) {
            notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void notifySectionRangeInserted(Section section, int positionStart, int itemCount) {
            notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void notifySectionRangeRemoved(Section section, int positionStart, int itemCount) {
            notifyItemRangeRemoved(positionStart, itemCount);
        }
    };

    // Header - ItemSection 에 추가할 리스너 ( 헤더의 nestedSection을 expansionToggle 하는 동작을 구현해야한다. )
    public OnItemClickListener<CategoryWithTasks> headerOnClickListener = new OnItemClickListener<CategoryWithTasks>() {
        @Override
        public void onItemClicked(int position, CategoryWithTasks item) {
            Timber.d(" Header OnClick Listener 메소드, %d, %s ", position, item.category.getName());
            if (item.getCount() == 0) {
                Timber.d(" Message [ No Items ]");
                Snackbar.make(rootView, "No Items", Snackbar.LENGTH_SHORT).show();
                return;
            }
            // boolean folded 값을 변경하지 않으므로 icon도 바뀌지 않게 된다.
            item.setFolded(!item.isFolded());
            onSectionExpansionToggle(position);
        }
    };

    // ListSection 의 아이템에 추가할 리스너.
    public OnItemClickListener<Task> taskOnClickListener = new OnItemClickListener<Task>() {
        @Override
        public void onItemClicked(int position, Task item) {
            Bundle bundle = new Bundle();
            bundle.putString(AddEditTaskFragment.TASK_ID, item.getUuid());

            Timber.d("taskID: [ %s ] is selected.", item.getUuid());
            navController.navigate(R.id.action_task_list_to_addEditTaskFragment, bundle);
        }
    };


    public NewAdapter(final Fragment fragment) {
        nestedSection.setNotifier(notifier);
        this.navController = NavHostFragment.findNavController(fragment);
        this.rootView = fragment.getView();

    }

    public void addSection(Section section) {
        Timber.d("[ Add Section ] -> isNull : %s", section == null);
        if (section == null)
            return;

        if (section.getNotifier() != null) {
            throw new IllegalStateException("It already has a parent.");
        }

        nestedSection.addSection(section);
    }

    public void collapseAllItems() {
        nestedSection.collapseAllItems();
    }

    public void collapseAllSections() {
        nestedSection.collapseAllSections();
    }

    public void registerBinders(ViewBinder... viewBinders) {
        Collections.addAll(this.viewBinders, viewBinders);
    }

    public void unregisterAllBinders() {
        viewBinders.clear();
    }

    public void removeAllSections() {
        int oldCount = getItemCount();
        nestedSection.removeAllSections();
        notifier.notifySectionRangeRemoved(null, 0, oldCount);
    }

    public void startActionMode() {
        isInActionMode = true;
    }

    public void stopActionMode() {
        isInActionMode = false;
    }

    public boolean isInActionMode() {
        return isInActionMode;
    }

    // --------------

    public void onItemDismiss(int adapterPosition) {
        nestedSection.onItemDismiss(adapterPosition);
    }

    public void onItemClicked(int adapterPosition) {
        Timber.d("[ NewAdapter . onItemClicked(int pos) ] : %d", adapterPosition);
        nestedSection.onItemClicked(adapterPosition);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return viewBinders.get(viewType).createViewHolder(parent, this);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        ViewBinder viewBinder = viewBinders.get(holder.getItemViewType());
        holder.setItem(getItem(position));

        viewBinder.bindViewHolder(holder, holder.getItem());
    }

    @Override
    public int getItemViewType(int position) {
        Object item = getItem(position);
        return getViewBinderPositionForItem(item);
    }

    @Override
    public int getItemCount() {
        return nestedSection.getCount();
    }

    public boolean isAdapterInActionMode() {
        return isInActionMode;
    }

    private Object getItem(int adapterPosition) {
        return nestedSection.getItem(adapterPosition);
    }

    // viewBinders 리스트에서 파라미터 item 을 담을 수 있는 binder 의 인덱스값을 반환한다.
    // 음식(item)에 맞는 그릇(ViewBinder)를 찾을 수 있어야 한다.
    private int getViewBinderPositionForItem(Object item) {
        int binderPosition = 0;
        // Adapter 가 가지고 있는 그릇의 리스트 : viewBinders
        for (ViewBinder viewBinder : viewBinders) {
            if (viewBinder.canBindData(item)) {
                return binderPosition;
            }
            binderPosition++;
        }
        throw new IllegalStateException("No ViewBinder match for this item.");
    }

    private ViewBinder getViewBinder(int position) {
        return viewBinders.get(getItemViewType(position));
    }

    public void onSectionExpansionToggle(int adapterPosition) {
        Timber.d("Adapter 의 onSectionExpansionToggle() %d", adapterPosition);
        nestedSection.onSectionExpansionToggle(adapterPosition);
    }

    public boolean isSectionExpanded(int adapterPosition) {
        Timber.d(" is Section Expanded() : ");
        return nestedSection.isSectionExpanded(adapterPosition);
    }
}
