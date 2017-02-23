package com.multunus.onemdm.heartbeats;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.multunus.onemdm.communication.GatewayService;
import com.multunus.onemdm.communication.PublisherService;
import com.multunus.onemdm.config.Config;

import org.json.JSONException;
import org.json.JSONObject;

public class Publisher extends BroadcastReceiver {
    private static String TAG = "HeartbeatPublisher";
    private boolean mbound = false;
    private GatewayService gatewayService;
    public Publisher() {
    }


    @Override
    public void onReceive(Context context, Intent intent){
        Log.d(TAG,"publishing heartbeat");
        try {
            publishHeartbeat(context);
        } catch (JSONException e) {
            Log.e(TAG,e.getMessage());
        }
    }

    private void publishHeartbeat(Context context) throws JSONException {
        Intent i = new Intent(context.getApplicationContext(), PublisherService.class);
        i.putExtra("TOPIC", Config.TOPIC);
        i.putExtra("PAYLOAD", getEvent(context).toString());
        context.startService(i);
    }

    private JSONObject getEvent(Context context) throws JSONException {
        String accessToken = Config.getSharedPreference(context,Config.ACCESS_TOKEN);
        HeartbeatEvent event = new HeartbeatEvent(accessToken);
        return event.getEvent();
    }


    public static void schedule(Context context){

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, Publisher.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0,
                intent, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), Config.HEARTBEAT_FREQUENCY,sender);

    }




}
