package com.example.projectcalendarapp.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class NotificationDbManager extends SQLiteOpenHelper {
    private static final String DB_NOTIFICATIONS = "tbl_notifications";

    public NotificationDbManager(Context context){
        super(context, DB_NOTIFICATIONS, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /*
        title equals the title of the event
        description will be the date and time
        eventId is a reference to the event -> for removing notifications
         */
        String query = "create table " + DB_NOTIFICATIONS + "(id integer primary key autoincrement, " +
                "description text, title text, eventId integer, alarmInMillis integer, iconId integer)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXISTS " + DB_NOTIFICATIONS;
        db.execSQL(query);
        onCreate(db);
    }

    public ArrayList<Integer> addNotification(MyCalendarEvent event){
        /*
        add notifications for the current event to the database
        might be more than one notification -> get the list of milliseconds from event.getAlarmTime...
         */
        SQLiteDatabase database = this.getReadableDatabase();
        ArrayList<Long> allNotifications = event.getAlarmTimeInMillis();

        String description = event.dateToString();
        String title = event.getTitle();
        long eventId = event.getUniqueEventId();
        int iconId = event.getIconId();

        for (long millis : allNotifications){
            ContentValues contentValues = new ContentValues();

            contentValues.put("description", description);
            contentValues.put("title", title);
            contentValues.put("eventId", eventId);
            contentValues.put("alarmInMillis", millis);
            contentValues.put("iconId", iconId);

            database.insert(DB_NOTIFICATIONS, null, contentValues);
        }
        return getPosition(event.getUniqueEventId());
    }

    public Cursor readAllNotifications(){
        /*
        read all notification from the database
        and return a cursor for iterating over the elements
         */
        SQLiteDatabase database = this.getWritableDatabase();
        String query = "select * from " + DB_NOTIFICATIONS + " order by id desc";
        return database.rawQuery(query, null);
    }

    public ArrayList<Integer> removeNotification(int eventId){
        /*
        remove all notifications from the database with the eventId
         */
        ArrayList<Integer> removedIds = getPosition(eventId);

        SQLiteDatabase database = this.getWritableDatabase();
        String query = "delete from " + DB_NOTIFICATIONS + " where eventId=?";
        database.execSQL(query, new String[] {Integer.toString(eventId)});

        return removedIds;
    }

    public ArrayList<Integer> removeSpecificNotificationMillis(MyCalendarEvent event){
        /*
        remove only one notification from the database with the eventId and the description
         */
        ArrayList<Long> times = event.getAlarmTimeInMillis();
        int uniqueId = event.getUniqueEventId();
        ArrayList<Integer> removedIds = getSpecificPosition(uniqueId, times);

        SQLiteDatabase database = this.getWritableDatabase();
        for (int id: removedIds){
            String query = "delete from " + DB_NOTIFICATIONS + " where id=? and eventId=?";
            database.execSQL(query, new String[] {Integer.toString(id), Integer.toString(uniqueId)});
        }
        return removedIds;
    }

    public void removeSpecificNotificationId(int id){
        /*
        remove only one notification from the database with the eventId and the description
         */
        SQLiteDatabase database = this.getWritableDatabase();
        String query = "delete from " + DB_NOTIFICATIONS + " where id=?";
        database.execSQL(query, new String[] {Integer.toString(id)});
    }

    public ArrayList<Integer> getPosition(int eventId){
        /*
        get all Ids there eventId == eventId as arraylist
         */
        SQLiteDatabase database = this.getWritableDatabase();
        String query = "select id from " + DB_NOTIFICATIONS + " where eventId=?";
        Cursor cursor = database.rawQuery(query, new String[] {Integer.toString(eventId)});

        ArrayList<Integer> keys = new ArrayList<>();
        while (cursor.moveToNext()) {
            keys.add(cursor.getInt(0));
        }
        cursor.close();
        return keys;
    }

    public ArrayList<Integer> getSpecificPosition(int eventId, ArrayList<Long> timeInMillis){
        /*
        get all Ids there eventId == eventId as arraylist and alarm time in millis == time in millis
         */
        SQLiteDatabase database = this.getWritableDatabase();
        ArrayList<Integer> keys = new ArrayList<>();
        for (Long i : timeInMillis){
            String query = "select id from " + DB_NOTIFICATIONS + " where eventId=? and alarmInMillis=?";
            Cursor cursor = database.rawQuery(query, new String[] {Integer.toString(eventId), Long.toString(i)});

            while (cursor.moveToNext()) {
                keys.add(cursor.getInt(0));
            }
            cursor.close();
        }
        return keys;
    }
}
