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
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
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
    String saved_room_number_str = "saved_room_number";
    Integer room_number;
    JSONObject jObject;
    long interval;
    String game, time;
    String post_request;
    StringBuffer sb = new StringBuffer("");
    boolean repeat_state = false;
    Handler mHandler = new Handler();

    Runnable mHandlerTask = new Runnable() {
        @Override
        public void run() {
            // if(repeat_state == true)
            main_func();
            // Toast.makeText(getApplicationContext(), "10 Sec", Toast.LENGTH_LONG).show();

        }
    };

    public MyService() {
    }

    void startRepeatingTask() {
        repeat_state = true;
        mHandlerTask.run();
        Log.i("state", "Start");
    }

    void stopRepeatingTask() {
        repeat_state = false;
        mHandler.removeCallbacksAndMessages(mHandlerTask);
        Log.i("state", "Stop");
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

    void main_func() {
        if (isNetworkAvailable() == true) {
            stopRepeatingTask();
            // change whait intervat whait to next day at 9:30 o'clock
            Calendar cc = Calendar.getInstance();
            cc.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + 1);
            cc.set(Calendar.HOUR_OF_DAY, 9);
            cc.set(Calendar.MINUTE, 30);
            cc.set(Calendar.SECOND, 0);
            interval = cc.getTimeInMillis();

            //mHandler.postDelayed(mHandlerTask, interval);

            Toast.makeText(getApplicationContext(), "inet on", Toast.LENGTH_LONG).show();
            //get room number
            SharedPreferences prefs = getSharedPreferences(saved_room_number_str, MODE_PRIVATE);
            room_number = prefs.getInt("room_number", 0);
            //send post (room number) request
            new SendRequest().execute();
            if (post_request != null) {
                //interval = 1000 * 10; // 10 sec //todo: change
                try {
                    jObject = new JSONObject(post_request);
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "post request = null", Toast.LENGTH_LONG).show();

                    // if (repeat_state == false) startRepeatingTask();
                }

                String room = jObject.optString("room", "");
                game = jObject.optString("game", "");
                time = jObject.optString("time", "");
                lesttime = jObject.optString("lesttime", "");  // TODO: get data and for each data for make notifications
                SetAlarm(1); // ToDO: need to change whit for_each
                SetAlarm(17);// ToDO: need to change


            } else {
                Toast.makeText(getApplicationContext(), "post data eror 1", Toast.LENGTH_LONG).show();
                interval = 1000 * 10 * 60;
            } // 60 sec



        } else {


            Toast.makeText(getApplicationContext(), "inet off",
                    Toast.LENGTH_LONG).show();  // TODO: make chake internet connection every hour
            interval = 1000 * 10; // 10 sec
            //if (repeat_state == false) mHandler.postDelayed(mHandlerTask, interval);;
        }
        stopRepeatingTask();
        mHandler.postDelayed(mHandlerTask, interval);
        //startRepeatingTask();
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
        main_func();
        Log.i("service", "Service starting ");
        // if (repeat_state == false) startRepeatingTask();
        return super.onStartCommand(arg, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

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
                conn.setReadTimeout(2000 /* milliseconds */);
                conn.setConnectTimeout(2000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
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


                    return sb.toString();
                } else {
                    return null;
                }
            } catch (Exception e) {
                return null;
            }
        }


        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), result,
                    Toast.LENGTH_LONG).show();
            post_request = result;
        }
    } // post


}