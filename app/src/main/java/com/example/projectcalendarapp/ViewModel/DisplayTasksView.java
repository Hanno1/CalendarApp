package com.example.projectcalendarapp.ViewModel;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.PopupMenu;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectcalendarapp.Constants;
import com.example.projectcalendarapp.R;
import com.example.projectcalendarapp.model.MyCalendarEvent;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;

public class DisplayTasksView extends AppCompatActivity implements View.OnClickListener {
    /*
    This View is used for displaying the tasks from the database
     */
    // to start another intent we need a global activityResultLauncher
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private RecyclerView recyclerView;

    private Dialog edit;
    private Dialog remove;
    private Dialog chooseDateWithCalendar;
    private LocalDate calendarSelectedDate;
    private CalendarView calendarView;

    private MyCalendarEvent event;

    private boolean actionAllEvents = true;

    // init a recyclerview Adapter for displaying the events
    private RecyclerViewAdapter recyclerViewAdapter;

    // keep track of the selected date and the current events
    private LocalDate selectedDate;
    private  ArrayList<MyCalendarEvent> eventsThisDay;

    // save which event is selected for edit or remove
    int editPosition = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        /*
        init the view and the events...
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_display_tasks);

        activityResultLauncher = initActivityResultLauncher();
        init();
    }

    private void init(){
        /*
        init the recycler view, the buttons, set the listener and so on
         */
        initDialogs();
        initCalendarDialog();
        // get selected date
        selectedDate = (LocalDate) getIntent().getExtras().get("Date");
        // get events this day
        eventsThisDay = Constants.mainController.getEventsThisDay(selectedDate);

        // init textview
        setButtonText();

        // set on click listener
        findViewById(R.id.display_tasks_close_btn).setOnClickListener(this);
        findViewById(R.id.display_tasks_add_task_btn).setOnClickListener(this);
        findViewById(R.id.display_tasks_btn_selected_date).setOnClickListener(this);

        recyclerView = findViewById(R.id.display_tasks_recycler_view);
        setRecyclerView();

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener
                (this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
            @Override public void onItemClick(View view, int position) {
                PopupMenu popupMenu = new PopupMenu(DisplayTasksView.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(item -> {
                    int id = item.getItemId();
                    event = eventsThisDay.get(position);
                    editPosition = position;
                    if (id == R.id.dropdown_menu_change_event){
                        if (event.getRepeatMode().equals("-")){
                            edit.show();
                        }
                        else {
                            Intent intent = new Intent(DisplayTasksView.this, AddTaskView.class);
                            intent.putExtra("Date", selectedDate);
                            intent.putExtra("MyCalendarEvent", event);
                            activityResultLauncher.launch(intent);
                            return true;
                        }
                    }
                    else if (id == R.id.dropdown_menu_delete_event){
                        if (event.getRepeatMode().equals("-")){
                            remove.show();
                        }
                        else {
                            Constants.mainController.removeEvent(selectedDate, event);
                            eventsThisDay.remove(editPosition);
                            recyclerViewAdapter.notifyItemRemoved(editPosition);
                        }
                    }
                    else {
                        System.out.println("Unknown Id in DisplayTasksView, init(), onMenuClick: " + id);
                    }
                    return false;
                });
                popupMenu.show();
            }

            @Override public void onLongItemClick(View view, int position) {}
        }));
    }

    private ActivityResultLauncher<Intent> initActivityResultLauncher(){
        /*
        used to launch an intent... addTaskView f.e.
         */
        // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
        return registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == 1) {
                        assert result.getData() != null;
                        Bundle data = result.getData().getExtras();
                        MyCalendarEvent editedEvent = (MyCalendarEvent) data.get("event");

                        if (editPosition != -1){
                            if (actionAllEvents){
                                LocalDate originalDate = event.getLocalDate();
                                LocalDate newDate = editedEvent.getLocalDate();

                                // get Difference of dates
                                int difYears = newDate.getYear() - originalDate.getYear();
                                int difMonths = newDate.getMonthValue() - originalDate.getMonthValue();
                                int difDays = newDate.getDayOfMonth() - originalDate.getDayOfMonth();

                                ArrayList<MyCalendarEvent> events = Constants.mainController.
                                        getEventsWithId(event.getUniqueEventId());

                                Constants.mainController.removeEvent(selectedDate,
                                        eventsThisDay.get(editPosition));
                                eventsThisDay.remove(editPosition);
                                recyclerViewAdapter.notifyItemRemoved(editPosition);

                                String title = editedEvent.getTitle();
                                String time = editedEvent.getTime();
                                int icon = editedEvent.getIconId();
                                String reminder = editedEvent.getReminder();

                                for (MyCalendarEvent eventInList : events){
                                    eventInList = eventInList.getEventPlusYears(difYears).
                                            getEventPlusMonths(difMonths).getEventPlusDay(difDays);

                                    eventInList.setTitle(title);
                                    eventInList.setTime(time);
                                    eventInList.setIconId(icon);
                                    eventInList.setReminder(reminder);

                                    long id = Constants.mainController.addEvent(eventInList);
                                    eventInList.setId(id);

                                    if (eventInList.isEventToday(selectedDate)){
                                        eventsThisDay.add(eventInList);
                                        recyclerViewAdapter.notifyItemInserted(eventsThisDay.size() - 1);
                                    }
                                }
                            }
                            else {
                                MyCalendarEvent currentEvent = eventsThisDay.get(editPosition);
                                Constants.mainController.removeSpecificEvent(selectedDate, currentEvent);
                                eventsThisDay.set(editPosition, editedEvent);
                                recyclerViewAdapter.notifyItemChanged(editPosition);

                                long id = Constants.mainController.addEvent(editedEvent);
                                editedEvent.setId(id);

                                editPosition = -1;
                            }
                        }
                        else {
                            long id = Constants.mainController.addEvent(editedEvent);
                            editedEvent.setId(id);
                            eventsThisDay.add(editedEvent);
                            recyclerViewAdapter.notifyItemInserted(-1);

                            editPosition = -1;
                        }
                        selectedDate = (LocalDate) data.get("selectedDate");
                        setButtonText();
                        setRecyclerView();
                    }
                });
    }

    private void initDialogs(){
        edit = new Dialog(this);
        edit.requestWindowFeature(Window.FEATURE_NO_TITLE);
        edit.setCancelable(true);

        edit.setContentView(R.layout.display_tasks_edit_dialog);

        edit.findViewById(R.id.edit_all_events_btn).setOnClickListener(this);
        edit.findViewById(R.id.edit_this_event_btn).setOnClickListener(this);

        remove = new Dialog(this);
        remove.requestWindowFeature(Window.FEATURE_NO_TITLE);
        remove.setCancelable(true);

        remove.setContentView(R.layout.display_tasks_remove_dialog);

        remove.findViewById(R.id.remove_this_event_btn).setOnClickListener(this);
        remove.findViewById(R.id.remove_all_events_btn).setOnClickListener(this);
    }

    private void initCalendarDialog(){
        chooseDateWithCalendar = new Dialog(this);
        chooseDateWithCalendar.requestWindowFeature(Window.FEATURE_NO_TITLE);
        chooseDateWithCalendar.setCancelable(true);
        chooseDateWithCalendar.setCanceledOnTouchOutside(true);

        chooseDateWithCalendar.setContentView(R.layout.layout_main_choose_date);

        calendarView = chooseDateWithCalendar.findViewById(R.id.cv_choose_date);
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) ->
                calendarSelectedDate = LocalDate.of(year, month + 1, dayOfMonth));

        chooseDateWithCalendar.findViewById(R.id.main_date_chooser_cancel_btn).setOnClickListener(v ->
                chooseDateWithCalendar.dismiss());

        chooseDateWithCalendar.findViewById(R.id.main_date_chooser_submit_btn).setOnClickListener(v -> {
            LocalDate currentDate = LocalDate.now();
            // check if date is before the date today
            boolean after = false;
            if (calendarSelectedDate.getYear() > currentDate.getYear()) {
                after = true;
            }
            else if (calendarSelectedDate.getYear() == currentDate.getYear() &&
                    calendarSelectedDate.getMonth().getValue() > currentDate.getMonth().getValue()) {
                after = true;
            }
            else if (calendarSelectedDate.getYear() == currentDate.getYear() &&
                    calendarSelectedDate.getMonth().getValue() == currentDate.getMonth().getValue() &&
                    calendarSelectedDate.getDayOfMonth() >= currentDate.getDayOfMonth()) {
                after = true;
            }
            if (after){
                selectedDate = calendarSelectedDate;
                eventsThisDay = Constants.mainController.getEventsThisDay(selectedDate);
                setButtonText();
                setRecyclerView();
            }
            chooseDateWithCalendar.dismiss();
        });
    }

    private void setButtonText(){
        Button displayDate = findViewById(R.id.display_tasks_btn_selected_date);
        String message = selectedDate.getDayOfMonth() + "." + selectedDate.getMonth().toString() +
                "." + selectedDate.getYear();
        displayDate.setText(message);
    }

    private void setRecyclerView(){
        // init recycler view and display events
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAdapter = new RecyclerViewAdapter(getApplicationContext(), eventsThisDay);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @Override
    public void onClick(View v) {
        /*
        on click method
        is implementing the click listener for all the buttons
         */
        int id = v.getId();
        if (id == R.id.display_tasks_close_btn){
            Intent returnIntent = new Intent();
            returnIntent.putExtra("selectedDate", selectedDate);
            setResult(2, returnIntent);
            super.finish();
        } else if (id == R.id.display_tasks_add_task_btn){
            Intent intent = new Intent(DisplayTasksView.this, AddTaskView.class);
            intent.putExtra("Date", selectedDate);
            activityResultLauncher.launch(intent);
        }
        else if (id == R.id.edit_all_events_btn){
            edit.dismiss();
            actionAllEvents = true;

            Intent intent = new Intent(DisplayTasksView.this, AddTaskView.class);
            intent.putExtra("Date", selectedDate);
            intent.putExtra("MyCalendarEvent", event);
            activityResultLauncher.launch(intent);
        }
        else if (id == R.id.edit_this_event_btn){
            edit.dismiss();
            actionAllEvents = false;

            Intent intent = new Intent(DisplayTasksView.this, AddTaskView.class);
            intent.putExtra("Date", selectedDate);
            intent.putExtra("MyCalendarEvent", event);
            activityResultLauncher.launch(intent);
        }
        else if (id == R.id.remove_all_events_btn){
            remove.dismiss();
            actionAllEvents = true;

            Constants.mainController.removeEvent(selectedDate, event);
            eventsThisDay.remove(editPosition);
            recyclerViewAdapter.notifyItemRemoved(editPosition);
        }
        else if (id == R.id.remove_this_event_btn){
            remove.dismiss();
            actionAllEvents = false;

            Constants.mainController.removeSpecificEvent(selectedDate, event);
            eventsThisDay.remove(editPosition);
            recyclerViewAdapter.notifyItemRemoved(editPosition);
        }
        else if (id == R.id.display_tasks_btn_selected_date){
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, selectedDate.getYear());
            calendar.set(Calendar.MONTH, selectedDate.getMonthValue() - 1);
            calendar.set(Calendar.DAY_OF_MONTH, selectedDate.getDayOfMonth());

            long milliTime = calendar.getTimeInMillis();

            calendarView.setDate(milliTime, true, true);

            chooseDateWithCalendar.show();
        }
    }
}
