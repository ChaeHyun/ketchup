package com.ketchup.tasklist;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ketchup.AdapterType;
import com.ketchup.R;
import com.ketchup.model.CategoryWithTasks;


public class HeaderViewHolder extends RecyclerView.ViewHolder {
    public TextView header_title;
    public ImageView header_icon;
    public AdapterType headerPosition;


    public CategoryWithTasks header;

    private HeaderItemOnClick headerItemOnClick;

    public HeaderViewHolder(@NonNull View itemView, HeaderItemOnClick headerItemOnClick) {
        super(itemView);

        header_title = itemView.findViewById(R.id.header_title);
        header_icon = itemView.findViewById(R.id.header_icon);
        this.headerItemOnClick = headerItemOnClick;

        header_icon.setOnClickListener(v -> {
            if (header.category.isFolded())
                this.headerItemOnClick.insertChildrenOfHeader(header);
            else
                this.headerItemOnClick.removeChildrenOfHeader(header);

            initHeaderIcon(header.category.isFolded());
        });
    }

    public void initHeaderIcon(boolean folded) {
        if (header.getCount() == 0)
            header_icon.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);

        if (folded)
            header_icon.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
        else
            header_icon.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
    }
}
