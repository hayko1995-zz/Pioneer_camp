package scoj.pioneer_camp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Startup extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        context.startService(new Intent(context, MyService.class));


    }
}