package com.example.projectcalendarapp.ViewModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.projectcalendarapp.R;
import com.example.projectcalendarapp.model.MyCalendarEvent;
import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
    /*
    This will handle the communication to the recycler views
    is used by DisplayTasksView
     */
    private final Context context;
    private final ArrayList<MyCalendarEvent> events;

    public RecyclerViewAdapter(Context cont, ArrayList<MyCalendarEvent> ev){
        context = cont;
        events = ev;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(context).inflate(
                R.layout.display_tasks_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        /*
        set text, time and icon to the event
         */
        MyCalendarEvent event = events.get(position);

        setIcon(holder, event.getIconId());
        holder.titleTextView.setText(event.getTitle());
        holder.timeTextView.setText(event.getTime());
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    private void setIcon(RecyclerViewHolder holder, int icon_id){
        /*
        set the icon in the layout
         */
        if (icon_id == 0){
            holder.imageView.setImageResource(R.drawable.exclamation);
        }
        else if (icon_id == 1){
            holder.imageView.setImageResource(R.drawable.birthday);
        }
        else if (icon_id == 2){
            holder.imageView.setImageResource(R.drawable.meeting_icon);
        }
        else if (icon_id == 3){
            holder.imageView.setImageResource(R.drawable.email_icon);
        }
        else {
            System.out.println("Unknown Icon id in RecyclerViewAdapter, setIcon. got icon id: " + icon_id);
            System.exit(-1);
        }
    }
}
