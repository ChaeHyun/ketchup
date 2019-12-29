package com.ketchup.tasklist;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ketchup.AdapterType;
import com.ketchup.R;

public class HeaderViewHolder extends RecyclerView.ViewHolder {
    public TextView header_title;
    public ImageView header_icon;
    public AdapterType headerPosition;

    public HeaderViewHolder(@NonNull View itemView) {
        super(itemView);

        header_title = itemView.findViewById(R.id.header_title);
        header_icon = itemView.findViewById(R.id.header_icon);
    }
}
