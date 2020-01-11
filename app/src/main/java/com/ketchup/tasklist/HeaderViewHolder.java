package com.ketchup.tasklist;


import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.ketchup.AdapterType;
import com.ketchup.R;
import com.ketchup.model.CategoryWithTasks;



public class HeaderViewHolder extends RecyclerView.ViewHolder {
    public TextView header_title;
    public ImageView header_icon;
    public AdapterType headerPosition;
    public LinearLayout linearLayout;

    public CategoryWithTasks header;

    private HeaderItemOnClick headerItemOnClick;

    public HeaderViewHolder(@NonNull View itemView, View rootView, HeaderItemOnClick headerItemOnClick) {
        super(itemView);

        linearLayout = itemView.findViewById(R.id.linear_layout_viewholder_header);
        header_title = itemView.findViewById(R.id.header_title);
        header_icon = itemView.findViewById(R.id.header_icon);
        this.headerItemOnClick = headerItemOnClick;

        linearLayout.setOnClickListener(v -> {
            if (header.getCount() == 0) {
                Snackbar.make(rootView, "No Items", Snackbar.LENGTH_SHORT).show();
                return;
            }

            if (header.isFolded())
                this.headerItemOnClick.insertChildrenOfHeader(header);
            else
                this.headerItemOnClick.removeChildrenOfHeader(header);

            initHeaderIcon(header.isFolded());
        });
    }

    public void initHeaderIcon(boolean folded) {
        if (folded)
            header_icon.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
        else
            header_icon.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
    }
}
