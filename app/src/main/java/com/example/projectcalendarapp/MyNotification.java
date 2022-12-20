package com.example.projectcalendarapp;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import com.example.projectcalendarapp.model.NotificationDbManager;
import java.util.Objects;

public class MyNotification extends BroadcastReceiver {
    /*
    This class will handle all Notifications
    scheduling, removing, displaying, adding and so on
     */
    // all notification settings
    // what do we need from the intent
    public static final String NOTIFICATION_ID = "notification-id";
    public static final String NOTIFICATION = "notification";
    // channel settings
    private static final String NOTIFICATION_CHANNEL_ID = "10001";
    private static final String default_notification_channel_id = "default";

    @Override
    public void onReceive(Context context, Intent intent) {
        /*
        what to do then receiving a notification
        basically display the notification
         */
        Notification notification = intent.getParcelableExtra(NOTIFICATION) ;
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);

        CharSequence name = "This is the title of channel 1";
        String description = "This is channel 1.";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name,
                importance);
        channel.setDescription(description);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        notificationManager.notify(id, notification);

        new NotificationDbManager(context).removeSpecificNotificationId(id);
    }

    public static void scheduleNotificationDelay(Context context, Notification notification, long delay,
                                            int notificationId){
        /*
        set a notification from now + delay
        delay is given in milliseconds
         */
        Intent notificationIntent = new Intent( context, MyNotification. class ) ;
        notificationIntent.putExtra(MyNotification.NOTIFICATION_ID, notificationId ) ;
        notificationIntent.putExtra(MyNotification.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId,
                notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC,
                System.currentTimeMillis() + delay, pendingIntent);
    }

    public static void scheduleNotificationAbsolute(Context context, Notification notification,
                                                    long millis, int notificationId){
        /*
        set alarm to millis
        millis is given in milliseconds
         */
        Intent notificationIntent = new Intent(context, MyNotification. class ) ;
        notificationIntent.putExtra(MyNotification.NOTIFICATION_ID, notificationId ) ;
        notificationIntent.putExtra(MyNotification.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId,
                notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC, millis, pendingIntent);
    }

    public static void removeNotification(Context context, int id){
        /*
        remove a notification from the alarmManager
        the removing from the database is happening in the MainModel
         */
        Intent notificationIntent = new Intent(context, MyNotification.class);
        PendingIntent.getBroadcast(context, id, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    public static Notification getNotification (Context context, String description, String title,
                                                int iconId) {
        /*
        creates a notification from some values
         */
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,
                default_notification_channel_id ) ;
        builder.setContentTitle(title) ;
        builder.setContentText(description) ;
        builder.setAutoCancel(true) ;
        builder.setChannelId(NOTIFICATION_CHANNEL_ID);
        builder.setSmallIcon(R.drawable.calendar_icon);
        builder.setColor(ContextCompat.getColor(context, R.color.light_red));
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        // set the icon of the notification
        if (iconId == Constants.ICON_ID_NORMAL){
            builder.setLargeIcon(((BitmapDrawable) Objects.requireNonNull(ContextCompat.getDrawable(context,
                    R.drawable.exclamation))).getBitmap());
        }
        else if (iconId == Constants.ICON_ID_BIRTHDAY){
            builder.setLargeIcon(((BitmapDrawable) Objects.requireNonNull(ContextCompat.getDrawable(context,
                    R.drawable.birthday))).getBitmap());
        }
        else if (iconId == Constants.ICON_ID_MEETING){
            builder.setLargeIcon(((BitmapDrawable) Objects.requireNonNull(ContextCompat.getDrawable(context,
                    R.drawable.meeting_icon))).getBitmap());
        }
        else if (iconId == Constants.ICON_ID_MAIL){
            builder.setLargeIcon(((BitmapDrawable) Objects.requireNonNull(ContextCompat.getDrawable(context,
                    R.drawable.email_icon))).getBitmap());
        }
        return builder.build() ;
    }
}
