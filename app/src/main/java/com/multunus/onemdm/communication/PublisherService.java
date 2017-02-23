package com.multunus.onemdm.communication;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * * helper methods.
 */
public class PublisherService extends IntentService {

    private final String TAG = "PublisherService";
    private JSONObject payload;
    private String topic;


    public PublisherService() {
        super("PublisherService");
    }

    /**
     * Starts this service to perform action Publish with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startPublishAction(Context context, String TOPIC, JSONObject payload) {
        Intent intent = new Intent(context, PublisherService.class);
        intent.putExtra("TOPIC", TOPIC);
        intent.putExtra("PAYLOAD", payload.toString());
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Starting publisher service");
        if (intent != null) {
            try {
                this.payload = new JSONObject(intent.getStringExtra("PAYLOAD"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            this.topic = intent.getStringExtra("TOPIC");
            Log.d(TAG, "Trying to bind to gateway service");
            Intent i = new Intent(this.getApplicationContext(),GatewayService.class);
            this.getApplicationContext().bindService(i, gatewayConnection, Context.BIND_AUTO_CREATE );
        }
    }

    private ServiceConnection gatewayConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG,"Connected to Gateway service");
            GatewayService.GatewayBinder binder = (GatewayService.GatewayBinder) service;
            GatewayService gatewayService = binder.getService();
            Log.d(TAG,"Exectuting publish on gateway");
            gatewayService.publish(topic, payload);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "Disconnected from publisher service");
        }
    };

}

