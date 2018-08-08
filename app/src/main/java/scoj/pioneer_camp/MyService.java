package scoj.pioneer_camp;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.Calendar;

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

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public int onStartCommand(Intent arg, int flags, int startId) {

        Log.i("aa", "start ");
        SharedPreferences prefs = getSharedPreferences(saved_room_number_str, MODE_PRIVATE);

        room_number = prefs.getInt("room_number", 0);
        Intent intent = new Intent(this, NotificationService.class);
        intent.putExtra("roomNumber", room_number);

        /*
         AlarmManager mgrAlarm = (AlarmManager) context.getSystemService(ALARM_SERVICE);
         ArrayList<PendingIntent> intentArray = new ArrayList<PendingIntent>();

        for(i = 0; i < 10; ++i)
        {
           Intent intent = new Intent(context, OnAlarmReceiver.class);
           // Loop counter `i` is used as a `requestCode`
           PendingIntent pendingIntent = PendingIntent.getBroadcast(context, i, intent, 0);
           // Single alarms in 1, 2, ..., 10 minutes (in `i` minutes)
           mgrAlarm.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() + 60000 * i,
                        pendingIntent);

           intentArray.add(pendingIntent);
        }
        * */

        //AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent notificationIntent = new Intent(this, NotificationService.class);
        PendingIntent alarmIntent = PendingIntent.getService(this, 0, notificationIntent, 0);


        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 15);
        calendar.set(Calendar.MINUTE, 45);


        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                1000 * 60 * 5, alarmIntent);
        Log.i("aa", "start ");

        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, broadcast);

        //startForeground(0,null);
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