package scoj.pioneer_camp;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;

import static android.app.NotificationManager.IMPORTANCE_DEFAULT;

/**
 * Helper class for showing and canceling new message
 * notifications.
 * <p>
 * This class makes heavy use of the {@link NotificationCompat.Builder} helper
 * class to create notifications in a backward-compatible way.
 */
public class MyNotification {

    private static final String CHANNEL_ID = "com.singhajit.notificationDemo.channelId";

    private static final String NOTIFICATION_TAG = "NewMessage";


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void notify(final Context context,
                              final String exampleString, final int number) {


        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(context, MyService.class);
        PendingIntent broadcast = PendingIntent.getService(context, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 5);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);

        Intent intent = new Intent(context, Main.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        Notification.Builder builder = new Notification.Builder(context);

        Notification notification = builder.setContentTitle("Demo App Notification")
                .setContentText("New Notification From Demo App..")
                .setTicker("New Message Alert!")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent).build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID);
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "NotificationDemo",
                    IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notification);
    }
}
