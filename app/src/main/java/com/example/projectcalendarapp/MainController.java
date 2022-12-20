package com.example.projectcalendarapp;

import android.content.Context;

import com.example.projectcalendarapp.model.MainModel;
import com.example.projectcalendarapp.model.MyCalendarEvent;

import java.time.LocalDate;
import java.util.ArrayList;

public class MainController {
    /*
    Controller. is used for communication between the model and the view.
    We don't know the view though
     */
    private final MainModel mainModel;

    public MainController(Context context)
    {
        mainModel = new MainModel(context);
    }

    public ArrayList<int[]> getMonthValues(LocalDate selectedDate){
        return mainModel.getMonthValues(selectedDate);
    }

    public long addEvent(MyCalendarEvent event){
        return mainModel.addEvent(event);
    }

    public void removeEvent(LocalDate selectedDate, MyCalendarEvent event){
        /*
        remove event with the uniqueEventId
         */
        mainModel.removeEvent(selectedDate, event);
    }

    public void removeSpecificEvent(LocalDate selectedDate, MyCalendarEvent event){
        /*
        remove Event with the id in the database -> this is realy only one event
         */
        mainModel.removeSpecificEvent(selectedDate, event);
    }

    public ArrayList<MyCalendarEvent> getEventsThisDay(LocalDate date){
        /*
        returns a list of all events this day
         */
        return mainModel.getEventsThisDay(date);
    }

    public ArrayList<MyCalendarEvent> getEventsWithId(int eventId){
        /*
        returns a list with all events with the eventId
         */
        return mainModel.getAllEventsFromDatabaseWithId(eventId);
    }
}
