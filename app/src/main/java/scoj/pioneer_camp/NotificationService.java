package scoj.pioneer_camp;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import static android.app.NotificationManager.IMPORTANCE_DEFAULT;


public class NotificationService extends IntentService {


    private static final String CHANNEL_ID = "com.singhajit.notificationDemo.channelId";
    private static final String NOTIFICATION_TAG = "NewMessage";

    public NotificationService() {

        super("NotificationService");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void notify(final Context context, final String exampleString, final int number) {

        Intent intent = new Intent(context, Main.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        Notification.Builder builder = new Notification.Builder(context);

        Notification notification = builder.setContentTitle("Demo App Notification")
                .setContentText(String.valueOf(number))
                .setTicker(String.valueOf(number))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent).build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID);
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    exampleString,
                    IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notification);
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        Integer room_number = intent.getIntExtra("number", 0);
        notify(this, "hello", 5);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

