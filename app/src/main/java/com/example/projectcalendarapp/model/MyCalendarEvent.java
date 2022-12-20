package com.example.projectcalendarapp.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MyCalendarEvent implements Serializable {
    // Title is the Name of the Event
    // Time is the time like this: 01:12
    // date is saved as Year-Month f.e. 2021-12
    private long id;

    private String title;
    private String time;
    private final String date;
    private String repeatTimes;
    private final int day;
    private int icon;
    private int uniqueEventId;

    // reminder saves the Minutes, separated with a :
    // f.e. 10:30 -> alarm will go off 10 min before and 30 min before
    private String reminder;

    public MyCalendarEvent(long id, String title, String date, String time,
                           int day, int icon, String repeatTimes, int uniqueEventId, String reminder){
        /*
        all important information for a calendar event
         */
        this.id = id;
        this.title = title;
        this.date = date;
        this.time = time;
        this.day = day;
        this.repeatTimes = repeatTimes;
        this.icon = icon;
        this.uniqueEventId = uniqueEventId;
        this.reminder = reminder;
    }

    /*
    getters and setters which are important since the variables are defined as private
     */
    public String getTitle(){
        return this.title;
    }

    public void setTitle(String title){ this.title = title; }

    public String getTime(){
        return this.time;
    }

    public void setTime(String time){
        this.time = time;
    }

    public String getDate(){
        return this.date;
    }

    public int getDay() { return this.day; }

    public String getRepeatMode() { return this.repeatTimes; }

    public void setRepeatMode(String repeatMode) { this.repeatTimes = repeatMode; }

    public int getIconId() { return this.icon; }

    public void setIconId(int icon_id) { this.icon = icon_id; }

    public long getId() { return this.id; }

    public void setId(long newId) { this.id = newId; }

    public int getUniqueEventId(){return this.uniqueEventId;}

    public void setUniqueEventId(int uniqueId){ this.uniqueEventId = uniqueId; }

    public String getReminder(){return this.reminder;}

    public void setReminder(String reminder){this.reminder = reminder;}

    public LocalDate getLocalDate(){
        int month = Integer.parseInt(this.getDate().split("-")[1]);
        int year = Integer.parseInt(this.getDate().split("-")[0]);

        return LocalDate.of(year, month, day);
    }

    public MyCalendarEvent getEventPlusDay(int addDays){
        /*
        returns a new event which is exactly addDays Days later then this (current Event)
        used to repeat events
         */
        int month = Integer.parseInt(this.getDate().split("-")[1]);
        int year = Integer.parseInt(this.getDate().split("-")[0]);

        LocalDate date = LocalDate.of(year, month, this.getDay()).plusDays(addDays);

        String newDate = date.getYear() + "-" + date.getMonth().getValue();

        return new MyCalendarEvent(this.id, this.getTitle(), newDate,
                this.getTime(), date.getDayOfMonth(), this.getIconId(),
                "-", this.getUniqueEventId(), this.reminder);
    }

    public MyCalendarEvent getEventPlusWeeks(int addWeeks){
        /*
        returns a new event which is exactly addWeeks Weeks later then this (current Event)
        used to repeat events
         */
        int month = Integer.parseInt(this.getDate().split("-")[1]);
        int year = Integer.parseInt(this.getDate().split("-")[0]);

        LocalDate date = LocalDate.of(year, month, this.getDay()).plusDays(7L * addWeeks);

        String newDate = date.getYear() + "-" + date.getMonth().getValue();

        return new MyCalendarEvent(this.id, this.getTitle(), newDate,
                this.getTime(), date.getDayOfMonth(), this.getIconId(),
                "-", this.getUniqueEventId(), this.reminder);
    }

    public MyCalendarEvent getEventPlusMonths(int addMonths){
        /*
        returns a new event which is exactly addMonths Months later then this (current Event)
        used to repeat events
         */
        int month = Integer.parseInt(this.getDate().split("-")[1]);
        int year = Integer.parseInt(this.getDate().split("-")[0]);

        LocalDate date = LocalDate.of(year, month, this.getDay()).plusMonths(addMonths);
        if (date.getDayOfMonth() != this.getDay()){
            return null;
        }

        String newDate = date.getYear() + "-" + date.getMonth().getValue();

        return new MyCalendarEvent(this.id, this.getTitle(), newDate,
                this.getTime(), date.getDayOfMonth(), this.getIconId(),
                "-", this.getUniqueEventId(), this.reminder);
    }

    public MyCalendarEvent getEventPlusYears(int addYears){
        /*
        returns a new event which is exactly addYears Years later then this (current Event)
        used to repeat events
         */
        int month = Integer.parseInt(this.getDate().split("-")[1]);
        int year = Integer.parseInt(this.getDate().split("-")[0]);

        LocalDate date = LocalDate.of(year, month, this.getDay()).plusYears(addYears);
        if (date.getDayOfMonth() != this.getDay() || date.getMonth().getValue() != month){
            return null;
        }

        String newDate = date.getYear() + "-" + date.getMonth().getValue();

        return new MyCalendarEvent(this.id, this.getTitle(), newDate,
                this.getTime(), date.getDayOfMonth(), this.getIconId(),
                "-", this.getUniqueEventId(), this.reminder);
    }

    public String dateToString(){
         /*
         turns the date to a string which will be displayed in a notification
          */
         return "Date: " + this.date + ", Time: " + this.time;
    }

    public String getYear(){
        /*
        just return the year of this
         */
        return this.getDate().split("-")[0];
    }

    public String getMonth(){
        /*
        returns the month of this as String
         */
        return this.getDate().split("-")[1];
    }

    public String getHour(){
        /*
        return the hour of this
        Time is the time like this: 01:12
         */
        return this.getTime().split(":")[0];
    }

    public String getMinute(){
        /*
        return the minute of this
        Time is the time like this: 01:12
         */
        return this.getTime().split(":")[1];
    }

    public String getDayAsTwoDigits(){
        /*
        get day as two digits since we might need a uniform representation in a string
        since day is a integer
         */
        if (this.day >= 1 && this.day < 10){
            return "0" + this.day;
        }
        return String.valueOf(this.day);
    }

    public ArrayList<Long> getAlarmTimeInMillis(){
        /*
        returns a list containing all milliseconds for the alarms which have to be displayed
         */
        ArrayList<Long> returnList = new ArrayList<>();

        if (this.getReminder().equals("")){
            return returnList;
        }
        String[] remindersSplit = this.getReminder().split(":");

        String myDate = this.getYear() + "/" + this.getMonth() + "/" + this.getDayAsTwoDigits()
                + " " + this.getHour() + ":" + this.getMinute() + ":00";

        LocalDateTime localDateTime = LocalDateTime.parse(myDate,
                DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss") );

        for (String el : remindersSplit){
            long millis = localDateTime.minusMinutes(Integer.parseInt(el))
                    .atZone(ZoneId.systemDefault())
                    .toInstant().toEpochMilli();
            returnList.add(millis);
        }
        return returnList;
    }

    public boolean isEventToday(LocalDate selectedDate){
        return (this.getDay() == selectedDate.getDayOfMonth() &&
                Integer.parseInt(this.getYear()) == selectedDate.getYear() &&
                Integer.parseInt(this.getMonth()) == selectedDate.getMonthValue());
    }
}
