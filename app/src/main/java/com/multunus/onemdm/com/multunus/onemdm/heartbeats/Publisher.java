package com.multunus.onemdm.com.multunus.onemdm.heartbeats;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.multunus.onemdm.com.multunus.onemdm.communication.GatewayService;
import com.multunus.onemdm.com.multunus.onemdm.communication.PublisherService;

import org.json.JSONException;
import org.json.JSONObject;

public class Publisher extends BroadcastReceiver {
    private static String TAG = "HeartbeatPublisher";
    private static final String TOPIC = "devices.heartbeats";//"devices.hearbeats";
    private static final long FREQUENCY = 1000; //AlarmManager.INTERVAL_FIFTEEN_MINUTES;
    private boolean mbound = false;
    private GatewayService gatewayService;
    public Publisher() {
    }


    @Override
    public void onReceive(Context context, Intent intent){
        Log.d(TAG,"publishing heartbeat");
        publishHeartbeat(context);
    }

    private void publishHeartbeat(Context context) {
        Intent i = new Intent(context.getApplicationContext(), PublisherService.class);
        i.putExtra("TOPIC", TOPIC);
        i.putExtra("PAYLOAD", getPayload().toString());
        context.startService(i);
    }

    private JSONObject getPayload() {
        JSONObject payload = new JSONObject();
        try {
            payload.put("time", System.currentTimeMillis());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return payload;
    }


    public static void schedule(Context context){

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, Publisher.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0,
                intent, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),FREQUENCY,sender);

    }




}
