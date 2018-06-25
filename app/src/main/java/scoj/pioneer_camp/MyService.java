package scoj.pioneer_camp;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;

public class MyService extends Service {
    public MyService() {
    }

    String saved_room_number_str = "saved_room_number";

    Integer room_number;



    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent arg, int flags, int startId) {

        SharedPreferences prefs = getSharedPreferences(saved_room_number_str, MODE_PRIVATE);

        room_number = prefs.getInt("room_number", 0);
        Intent intent = new Intent(this, NotificationService.class);
        intent.putExtra("roomNumber", room_number);
        startService(intent);
        return super.onStartCommand(arg, flags, startId);
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