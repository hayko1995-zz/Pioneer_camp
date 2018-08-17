package scoj.pioneer_camp;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import static android.app.NotificationManager.IMPORTANCE_DEFAULT;


public class NotificationService extends IntentService {

    String saved_room_number_str = "saved_room_number";
    private static final String CHANNEL_ID = "com.singhajit.notificationDemo.channelId";
    private static final String NOTIFICATION_TAG = "NewMessage";
    int room_number;

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
                .setContentTitle(exampleString)
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


        //Integer room_number = intent.getIntExtra("number", 0);
        //SharedPreferences prefs = getSharedPreferences(saved_room_number_str, MODE_PRIVATE);

        SharedPreferences prefs = getSharedPreferences(saved_room_number_str, MODE_PRIVATE);

        room_number = prefs.getInt("room_number", 0);
        //Intent intent = new Intent(this, NotificationService.class);
        room_number++;
        SharedPreferences.Editor editor = getSharedPreferences(saved_room_number_str, MODE_PRIVATE).edit();
        editor.putInt("room_number", room_number);
        editor.apply();
        Log.i("server", "notify");
        notify(this, "hello", room_number);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

