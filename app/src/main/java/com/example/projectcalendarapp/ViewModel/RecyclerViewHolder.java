package com.example.projectcalendarapp.ViewModel;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectcalendarapp.R;

public class RecyclerViewHolder extends RecyclerView.ViewHolder {
    /*
    this initializes the different view found in the recycler view layout
     */
    ImageView imageView;
    TextView titleTextView, timeTextView;

    public RecyclerViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView = itemView.findViewById(R.id.display_tasks_item_icon);
        titleTextView = itemView.findViewById(R.id.display_tasks_item_title);
        timeTextView = itemView.findViewById(R.id.display_tasks_item_time);
    }
}
