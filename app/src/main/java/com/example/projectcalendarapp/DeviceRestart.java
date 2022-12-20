package com.example.projectcalendarapp;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.Toast;

import com.example.projectcalendarapp.ViewModel.MainView;
import com.example.projectcalendarapp.model.NotificationDbManager;

public class DeviceRestart extends BroadcastReceiver {
    /*
    this will be called then the device was restarted,
    the notifications saved in a database will be given to the alarm manager
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // what to do on device restart
        // context.startForegroundService(intent);
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            getAllNotificationsFromDatabase(context);
        }
    }

    private void getAllNotificationsFromDatabase(Context context){
        /*
        schedule all alarms found in database. even the notifications that are overdone
         */
        Cursor cursor = new NotificationDbManager(context).readAllNotifications();
        while (cursor.moveToNext()) {
            int key = cursor.getInt(0);
            String description = cursor.getString(1);
            String title = cursor.getString(2);
            long alarm = cursor.getLong(4);
            int iconId = cursor.getInt(5);

            Notification notification = MyNotification.getNotification(context, description, title,
                    iconId);
            MyNotification.scheduleNotificationAbsolute(context, notification, alarm, key);
        }
    }
}
