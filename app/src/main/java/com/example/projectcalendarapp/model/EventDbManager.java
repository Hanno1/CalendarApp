package com.example.projectcalendarapp.model;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class EventDbManager extends SQLiteOpenHelper {
    private static final String DB_EVENTS = "tbl_events";

    public EventDbManager(Context context){
        super(context, DB_EVENTS, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        /*
        create database if it does not already exist
         */
        String query = "create table " + DB_EVENTS + "(id integer primary key autoincrement, title text, " +
                "date text, time text, day integer, icon integer, repeatMode text, " +
                "uniqueEventId integer, reminder text)";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String query = "DROP TABLE IF EXISTS " + DB_EVENTS;
        sqLiteDatabase.execSQL(query);
        onCreate(sqLiteDatabase);
    }

    public long addEvent(MyCalendarEvent event) {
        /*
        add a event to the database -> first prep entry, then insert
         */
        String title = event.getTitle();
        String date = event.getDate();
        String time = event.getTime();
        int day = event.getDay();
        String repeatMode = event.getRepeatMode();
        int icon = event.getIconId();

        SQLiteDatabase database = this.getReadableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("date", date);
        contentValues.put("time", time);
        contentValues.put("day", day);
        contentValues.put("icon", icon);
        contentValues.put("repeatMode", repeatMode);

        contentValues.put("uniqueEventId", event.getUniqueEventId());
        contentValues.put("reminder", event.getReminder());

        return database.insert(DB_EVENTS, null, contentValues);
    }

    public Cursor readAllEvents() {
        /*
        read all events from the database and return a cursor to iterate over them
         */
        SQLiteDatabase database = this.getWritableDatabase();
        String query = "select * from " + DB_EVENTS + " order by id desc";
        return database.rawQuery(query, null);
    }

    public Cursor readAllEventsWithId(int uniqueId) {
        /*
        read all events from the database with the unique Id = unique Id
        (EventId! not the primary key)
         */
        SQLiteDatabase database = this.getWritableDatabase();
        String query = "select * from " + DB_EVENTS + " where uniqueEventId=?";
        return database.rawQuery(query, new String[] {Integer.toString(uniqueId)});
    }

    public void removeEvent(int uniqueEventId){
        /*
        remove an event from the database -> get event by the unique Event id
        which is not actual unique -> unique for one type of events (repeating or one single)
         */
        SQLiteDatabase database = this.getWritableDatabase();
        String query = "delete from " + DB_EVENTS +
                " where uniqueEventId=?";
        database.execSQL(query, new String[] {Integer.toString(uniqueEventId)});
    }

    public void removeSpecificEvent(long id){
        /*
        remove an event from the database -> get event by the unique id
         */
        SQLiteDatabase database = this.getWritableDatabase();
        String query = "delete from " + DB_EVENTS +
                " where id=?";
        database.execSQL(query, new String[] {Long.toString(id)});
    }

    public void deleteAllEvents() {
        /*
        delete the entire table... just for testing purposes, not really needed
         */
        SQLiteDatabase database = this.getWritableDatabase();
        String query = "DROP TABLE IF EXISTS " + DB_EVENTS;
        database.execSQL(query);
    }
}
