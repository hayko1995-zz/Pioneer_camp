package scoj.pioneer_camp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;

import static android.app.NotificationManager.IMPORTANCE_DEFAULT;

public class MyService extends Service {
    public MyService() {
    }

    private static final String CHANNEL_ID = "com.singhajit.notificationDemo.channelId";

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate() {
        super.onCreate();



        int NOTIFICATION_ID = 234;

        Intent intent = new Intent(this, Main.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Notification.Builder builder = new Notification.Builder(this);

        Notification notification = builder.setContentTitle("Demo App Notification")
                .setContentText("New Notification From Demo App..")
                .setTicker("New Message Alert!")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent).build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

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


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}