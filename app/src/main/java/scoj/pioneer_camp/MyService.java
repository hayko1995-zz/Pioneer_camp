package scoj.pioneer_camp;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public class MyService extends Service {
    int i = 0;
    String lesttime;


    public MyService() {
    }


    String saved_room_number_str = "saved_room_number";

    Integer room_number;
    JSONObject jObject;
    String game, time, dt;

    private void SetAlarm(int min) {
        Log.i("server", "set");
        AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
        i++;

        Calendar cal = Calendar.getInstance();
        //cal.add(Calendar.MINUTE, sec);
        cal.set(Calendar.HOUR_OF_DAY, 21);
        cal.set(Calendar.MINUTE, min);
        cal.set(Calendar.SECOND, 0);

        long time = cal.getTimeInMillis();
        Intent notificationIntent = new Intent(this, NotificationService.class);
        notificationIntent.putExtra("roomNumber", room_number);
        PendingIntent alarmIntent = PendingIntent.getService(this, i, notificationIntent, 0);
        alarmMgr.set(AlarmManager.RTC_WAKEUP, time, alarmIntent);


    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate() {
        super.onCreate();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    } // inet conection chack

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public int onStartCommand(Intent arg, int flags, int startId) {


        Log.i("service", "Service starting ");

        if (isNetworkAvailable() == true) {
            Toast.makeText(getApplicationContext(), "inet on",
                    Toast.LENGTH_LONG).show();
            SetAlarm(15);
            SetAlarm(17);

        } else {
            Toast.makeText(getApplicationContext(), "inet off",
                    Toast.LENGTH_LONG).show();
        }

        new SendRequest().execute();

        SharedPreferences prefs = getSharedPreferences(saved_room_number_str, MODE_PRIVATE);

        room_number = prefs.getInt("room_number", 0);
        //Intent intent = new Intent(this, NotificationService.class);


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


        //Calendar calendar = Calendar.getInstance();
        //calendar.setTimeInMillis(System.currentTimeMillis());
        //calendar.set(Calendar.HOUR_OF_DAY, 15);
        //calendar.set(Calendar.MINUTE, 45);


        // alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),alarmIntent);
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


    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while (itr.hasNext()) {

            String key = itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    } //post

    public class SendRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {
        }
        protected String doInBackground(String... arg0) {
            try {
                URL url = new URL("http://192.168.0.103:3000/get_json");
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("room", room_number);
                //Log.e("params",postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while ((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }
                    in.close();
                    jObject = new JSONObject(sb.toString());
                    String room = jObject.optString("room", "");
                    game = jObject.optString("game", "");
                    time = jObject.optString("time", "");
                    lesttime = jObject.optString("lesttime", "");

                    // to do

                    //jObject = new JSONObject(sb.toString());
                    return sb.toString();
                } else {
                    return new String("false : " + responseCode);
                }
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
        }

        /*
                public Bread fromJson(final JSONObject object) {
                    final String image = object.optString("object", "");
                    final int price= object.optInt("price", 0);
                    final int weight= object.optInt("weight", 0);
                    final int kkal= object.optInt("kkal", 0);
                    final String description= object.optString("description", "");
                    return new Bread(image,price,weight,kkal,description);
                }
        */
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), result,
                    Toast.LENGTH_LONG).show();
        }
    } // post
}