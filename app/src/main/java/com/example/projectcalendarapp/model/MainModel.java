package com.example.projectcalendarapp.model;
import android.app.Notification;
import android.content.Context;
import android.database.Cursor;
import com.example.projectcalendarapp.Constants;
import com.example.projectcalendarapp.MyNotification;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;

public class MainModel {
    /*
    This is the MainModel - Its the main interface between the datatables and the view
    it adds tasks and notification and handles the logic
     */
    // all events from the database
    private ArrayList<MyCalendarEvent> events = new ArrayList<>();
    // events in the current month
    private ArrayList<MyCalendarEvent> eventsInCurrentMonth = new ArrayList<>();
    // context from the mainView
    private final Context context;
    // selected Date, from the main view as well
    private LocalDate selectedDate;

    public MainModel(Context cont){
        /*
        init the Model - get the context from the mainView
         */
        context = cont;
        // read all events in the database and store them in eventsInCurrentMonth
        getAllEventsFromDatabase();
    }

    public ArrayList<int[]> getMonthValues(LocalDate selectedDate)
    {
        /*
        returns a list of the font colors, border colors, dates
        events are considered as well
         */
        this.selectedDate = selectedDate;

        // return list of colors and dates (color, date)
        ArrayList<int[]> returnList = new ArrayList<>();
        ArrayList<Integer> daysInMonth = getDateListForMonth(selectedDate);

        // init events in current month
        setEventsThisMonth(selectedDate.getMonth().getValue(), selectedDate.getYear());

        // search if the currentDate is in the Month
        LocalDate currentDate = LocalDate.now();
        boolean now = (currentDate.getYear() == selectedDate.getYear() &&
                currentDate.getMonth() == selectedDate.getMonth());

        // The beginning of the font color has to be gray, since the days are from the previous month
        // the end of the font colors have to be gray as well
        // One major problem is, that, if the selected date is in the current month we need the
        // font color of all days before today to be gray as well
        boolean gray = true;
        boolean firstSwitch = false;
        boolean stillGray = false;
        for (int i = 0; i < daysInMonth.size(); i++){
            int currentDay = daysInMonth.get(i);
            if (daysInMonth.get(i) == 1){
                gray = !gray;
                firstSwitch = !firstSwitch;
            }
            if (now && firstSwitch && currentDay < currentDate.getDayOfMonth()){
                gray = true;
                stillGray = true;
            }
            if (now && currentDay == currentDate.getDayOfMonth() && firstSwitch){
                gray = false;
                stillGray = false;
            }
            int[] currentList;
            if (gray)
            {
                if (stillGray){
                    // if this is the current month -> all days in the past have to be gray
                    // we actually use another Constant for this, to differentiate
                    currentList = new int[]{Constants.THISMONTHBUTGRAYCOLOR, Constants.NORMALBORDER,
                            currentDay};
                }
                else {
                    currentList = new int[]{Constants.NOTTHISMONTHCOLOR, Constants.NORMALBORDER,
                            currentDay};
                }

            }
            else {
                // select border color
                if (now && currentDay == currentDate.getDayOfMonth()){
                    if (selectedDate.getDayOfMonth() == daysInMonth.get(i)){
                        if (isEventToday(currentDay)){
                            currentList = new int[]{Constants.THISMONTHCOLOR,
                                    Constants.TODAYSELECTEDEVENTBORDER, currentDay};
                        }
                        else {
                            currentList = new int[]{Constants.THISMONTHCOLOR,
                                    Constants.TODAYSELECTEDBORDER, currentDay};
                        }
                    }
                    else {
                        if (isEventToday(currentDay)){
                            currentList = new int[]{Constants.THISMONTHCOLOR,
                                    Constants.TODAYEVENTBORDER, currentDay};
                        }
                        else {
                            currentList = new int[]{Constants.THISMONTHCOLOR,
                                    Constants.TODAYBORDER, currentDay};
                        }
                    }
                }
                else {
                    if (selectedDate.getDayOfMonth() == currentDay){
                        if (isEventToday(currentDay)){
                            currentList = new int[]{Constants.THISMONTHCOLOR,
                                    Constants.NORMALSELECTEDEVENTBORDER, currentDay};
                        }
                        else {
                            currentList = new int[]{Constants.THISMONTHCOLOR,
                                    Constants.NORMALSELECTEDBORDER, currentDay};
                        }
                    }
                    else {
                        if (isEventToday(currentDay)){
                            currentList = new int[]{Constants.THISMONTHCOLOR,
                                    Constants.NORMALEVENTBORDER, currentDay};
                        }
                        else {
                            currentList = new int[]{Constants.THISMONTHCOLOR,
                                    Constants.NORMALBORDER, currentDay};
                        }
                    }
                }
            }
            returnList.add(currentList);
        }
        return returnList;
    }

    private void setEventsThisMonth(int month, int year) {
        /*
        returns a list of events this month
        we just go through all events in the database and look if the event is in the current month
        */
        eventsInCurrentMonth = new ArrayList<>();
        for (MyCalendarEvent event : events){
            String date = event.getDate();
            int event_month = Integer.parseInt(date.split("-")[1]);
            int event_year = Integer.parseInt(date.split("-")[0]);

            if (event_year == year && event_month == month){
                eventsInCurrentMonth.add(event);
            }
        }
    }

    private void getAllEventsFromDatabase(){
        /*
        init total list of events
         */
        LocalDate todate = LocalDate.now();
        int year = todate.getYear();
        int day = todate.getDayOfMonth();
        int month = todate.getMonth().getValue();

        Cursor cursor = new EventDbManager(context).readAllEvents();
        while (cursor.moveToNext()) {
            int key = cursor.getInt(0);
            MyCalendarEvent model = new MyCalendarEvent(key, cursor.getString(1),
                    cursor.getString(2), cursor.getString(3),
                    cursor.getInt(4), cursor.getInt(5),
                    cursor.getString(6), cursor.getInt(7),
                    cursor.getString(8));

            String[] eventDate = model.getDate().split("-");
            int eventYear = Integer.parseInt(eventDate[0]);
            int eventMonth = Integer.parseInt(eventDate[1]);
            int eventDay = model.getDay();

            if (eventYear < year) {
                new EventDbManager(context).removeSpecificEvent(key);
            }
            else if (eventYear == year && eventMonth < month) {
                new EventDbManager(context).removeSpecificEvent(key);
            }
            else if (eventYear == year && eventMonth == month && eventDay < day) {
                new EventDbManager(context).removeSpecificEvent(key);
            }
            else {
                events.add(model);
            }
        }
        cursor.close();
    }

    public ArrayList<MyCalendarEvent> getAllEventsFromDatabaseWithId(int id){
        /*
        get all events from the database with a specific Id, Id here means the EventId obviously
         */
        ArrayList<MyCalendarEvent> returnList = new ArrayList<>();
        Cursor cursor = new EventDbManager(context).readAllEventsWithId(id);
        while (cursor.moveToNext()) {
            int key = cursor.getInt(0);
            MyCalendarEvent model = new MyCalendarEvent(key, cursor.getString(1),
                    cursor.getString(2), cursor.getString(3),
                    cursor.getInt(4), cursor.getInt(5),
                    cursor.getString(6), cursor.getInt(7),
                    cursor.getString(8));
            returnList.add(model);
        }
        cursor.close();
        return returnList;
    }

    public long addEvent(MyCalendarEvent event){
        /*
        Add Event to the database
        uniqueEventId will be unique for the event or for the chain of events
         */
        // repeat Event
        String repeatString = event.getRepeatMode();
        if (repeatString.equals("-")){
            events.add(event);
            eventsInCurrentMonth.add(event);
            addNotifications(event);
            return new EventDbManager(context).addEvent(event);
        }
        String[] repeat = repeatString.split(":");

        // handle all different cases for repetitions (days, weeks, months, years)
        int value;
        if (!repeat[0].equals("0")){
            value = Integer.parseInt(repeat[0]);
            event.setRepeatMode("-");
            for (int i = 1; i <= value; i++){
                MyCalendarEvent newEvent = event.getEventPlusDay(i);
                events.add(newEvent);
                new EventDbManager(context).addEvent(newEvent);
                addNotifications(newEvent);
                if (isEventThisMonth(newEvent)){
                    eventsInCurrentMonth.add(newEvent);
                }
            }
        }
        else if (!repeat[1].equals("0")){
            value = Integer.parseInt(repeat[1]);
            event.setRepeatMode("-");
            for (int i = 1; i <= value; i++){
                MyCalendarEvent newEvent = event.getEventPlusWeeks(i);
                events.add(newEvent);
                new EventDbManager(context).addEvent(newEvent);
                addNotifications(newEvent);
                if (isEventThisMonth(newEvent)){
                    eventsInCurrentMonth.add(newEvent);
                }
            }
        }
        else if (!repeat[2].equals("0")){
            value = Integer.parseInt(repeat[2]);
            event.setRepeatMode("-");
            for (int i = 1; i <= value; i++){
                MyCalendarEvent newEvent = event.getEventPlusMonths(i);
                if (newEvent != null){
                    events.add(newEvent);
                    addNotifications(newEvent);
                    new EventDbManager(context).addEvent(newEvent);
                }
            }
        }
        else if (!repeat[3].equals("0")){
            value = Integer.parseInt(repeat[3]);
            event.setRepeatMode("-");
            for (int i = 1; i <= value; i++){
                MyCalendarEvent newEvent = event.getEventPlusYears(i);
                if (newEvent != null){
                    events.add(newEvent);
                    addNotifications(newEvent);
                    new EventDbManager(context).addEvent(newEvent);
                }
            }
        }
        // add event to current local lists
        events.add(event);
        eventsInCurrentMonth.add(event);
        // add notification and notification to the database
        addNotifications(event);
        // add Event to the database
        return new EventDbManager(context).addEvent(event);
    }

    private boolean isEventToday(int day){
        /*
        return true, if there is at least one event on this day
         */
        for (MyCalendarEvent event : eventsInCurrentMonth){
            if (event.getDay() == day){
                return true;
            }
        }
        return false;
    }

    private boolean isEventThisMonth(MyCalendarEvent event){
        /*
        return true, if the event is this month (the month of the selected date)
         */
        String[] date = event.getDate().split("-");

        // check year
        return Integer.parseInt(date[0]) == selectedDate.getYear() &&
                Integer.parseInt(date[0]) == selectedDate.getMonth().getValue();
    }


    public ArrayList<Integer> getDateListForMonth(LocalDate date) {
        /*
        returns a list of all dates in the month.
        Additionally we fill all spaces up to 42 with the last month and the next one
        this is needed in the mainView -> 42 table entries have t be filled
         */
        ArrayList<Integer> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);

        int daysInMonth = yearMonth.lengthOfMonth();
        LocalDate firstOfMonth = date.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        YearMonth yearPreviousMonth = YearMonth.from(date.minusMonths(1));
        int daysPrevMonth = yearPreviousMonth.lengthOfMonth();

        // init numbers on the panels
        for(int i = 1; i <= 42; i++) {
            if(i < dayOfWeek) {
                daysInMonthArray.add(daysPrevMonth - (dayOfWeek - i) + 1);
            }
            else if (i >= daysInMonth + dayOfWeek){
                daysInMonthArray.add(1 + i - (daysInMonth + dayOfWeek));
            }
            else {
                daysInMonthArray.add(i - dayOfWeek + 1);
            }
        }
        return daysInMonthArray;
    }

    public ArrayList<MyCalendarEvent> getEventsThisDay(LocalDate date) {
        /*
        returns all the events which occur on date = date
         */
        ArrayList<MyCalendarEvent> returnEvents = new ArrayList<>();
        for (MyCalendarEvent event : eventsInCurrentMonth){
            if (event.getDay() == date.getDayOfMonth()){
                returnEvents.add(event);
            }
        }
        return returnEvents;
    }

    public void removeEvent(LocalDate date, MyCalendarEvent event) {
        /*
        remove an Event from the Database
        therefore we override event as well as eventsInCurrentMonth
         */
        selectedDate = date;

        ArrayList<MyCalendarEvent> newEvents = new ArrayList<>();
        ArrayList<MyCalendarEvent> newEventsInCurrentMonth = new ArrayList<>();
        for (MyCalendarEvent tmp : events){
            if (tmp.getUniqueEventId() != event.getUniqueEventId()){
                newEvents.add(tmp);
                if (isEventThisMonth(tmp)){
                    newEventsInCurrentMonth.add(tmp);
                }
            }
        }
        events = newEvents;
        eventsInCurrentMonth = newEventsInCurrentMonth;
        new EventDbManager(context).removeEvent(event.getUniqueEventId());
        removeNotifications(event.getUniqueEventId());
    }

    public void removeSpecificEvent(LocalDate date, MyCalendarEvent event) {
        /*
        remove an Event from the Database
        if its a repeating event only remove one
         */
        selectedDate = date;

        ArrayList<MyCalendarEvent> newEvents = new ArrayList<>();
        ArrayList<MyCalendarEvent> newEventsInCurrentMonth = new ArrayList<>();
        for (MyCalendarEvent tmp : events){
            if (tmp.getUniqueEventId() != event.getUniqueEventId() ||
                    tmp.getId() != event.getId()){
                newEvents.add(tmp);
                if (isEventThisMonth(tmp)){
                    newEventsInCurrentMonth.add(tmp);
                }
            }
        }
        events = newEvents;
        eventsInCurrentMonth = newEventsInCurrentMonth;
        new EventDbManager(context).removeSpecificEvent(event.getId());
        removeSpecificNotifications(event);
    }

    private void addNotifications(MyCalendarEvent event){
        /*
        add Notifications for the current event
        we need to add a notification for all alarms chosen by the user
         */
        // add them to the database
        ArrayList<Integer> keys = new NotificationDbManager(context).addNotification(event);
        // add them to current queue
        ArrayList<Long> times = event.getAlarmTimeInMillis();
        String description = event.dateToString();
        String title = event.getTitle();

        int count = 0;
        for (long time : times){
            Notification notification = MyNotification.getNotification(context, description, title,
                    event.getIconId());
            MyNotification.scheduleNotificationAbsolute(context, notification, time, keys.get(count));
            count += 1;
        }
    }

    private void removeNotifications(int uniqueId){
        /*
        remove Notification from the database as well al the current queue
        uniqueId means the eventId
         */
        ArrayList<Integer> keys = new NotificationDbManager(context).removeNotification(uniqueId);
        for (int key : keys){
            MyNotification.removeNotification(context, key);
        }
    }

    private void removeSpecificNotifications(MyCalendarEvent event){
        /*
        remove Notification from the database as well al the current queue
        we use the millis for this - since we don't know the unique Notification Id
         */
        ArrayList<Integer> keys = new NotificationDbManager(context).removeSpecificNotificationMillis(event);
        for (int key : keys){
            MyNotification.removeNotification(context, key);
        }
    }
}
