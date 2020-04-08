package com.ketchup.tasklist;


import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;
import com.ketchup.AdapterType;
import com.ketchup.R;
import com.ketchup.model.category.CategoryWithTasks;

import timber.log.Timber;


public class HeaderViewHolder extends BaseViewHolder<CategoryWithTasks> {
    public TextView header_title;
    public ImageView header_icon;
    public AdapterType headerPosition;
    public LinearLayout linearLayout;

    public CategoryWithTasks header;

    public HeaderViewHolder(@NonNull View itemView) {
        super(itemView);

        linearLayout = itemView.findViewById(R.id.linear_layout_viewholder_header);
        header_title = itemView.findViewById(R.id.header_title);
        header_icon = itemView.findViewById(R.id.header_icon);

        itemView.setOnClickListener(v -> {
            super.onItemClick();
            if (header.getCount() == 0) {
                Timber.d(" [No Items in Header]");
                Snackbar.make(itemView.getRootView(), "No Items", Snackbar.LENGTH_SHORT).show();
                return;
            }
            getItem().setFolded(!getItem().isFolded());
            toggleSectionExpansion();
        });
    }


    public void initHeaderIcon(boolean folded) {
        Timber.d("InitHeader Icon [%s]", folded ? "Folded" : "Unfolded");
        if (folded)
            header_icon.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
        else
            header_icon.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
    }
}
