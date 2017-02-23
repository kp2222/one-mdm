package com.multunus.onemdm.communication;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.multunus.onemdm.heartbeats.Publisher;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

public class GatewayService extends Service {

    private static final String TAG = "GatewayService";
    private static final String BROKER_URI = "tcp://192.168.2.207:1883";
    private final GatewayBinder binder = new GatewayBinder();
    private MqttAndroidClient client;


    public GatewayService() {
    }

    @Override
    public void onCreate() {
        Publisher.schedule(this.getApplicationContext());

        Log.d(TAG, "Initializing Communication gateway service");

        super.onCreate();
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), BROKER_URI , clientId);
        MqttConnectOptions options = getMqttOptions();

        try {
            Log.d(TAG, "Trying to Connect to MQTT Broker at: " + BROKER_URI);
            client.connect(this.getApplicationContext(), new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "Successfully Connected to MQTT Broker at: " + BROKER_URI);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG, "Failed to connect to MQTT Broker at " + BROKER_URI);
                }
            });
        }catch(MqttException e){
            Log.e(TAG, "Exception initializing connection to MQTT Broker: " +e.getMessage());
        }

    }


    private MqttConnectOptions getMqttOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setConnectionTimeout(1); // Connection time out set to 1 second
        return options;

    }

    public void publish(String topic, JSONObject payload){
        if (client != null && client.isConnected()){
            mqTTpublish(topic,payload);

        }
    }

    private void mqTTpublish(String topic, JSONObject payload){
        Log.d(TAG,"Publishing to: "+ topic + " with: " + payload);
        MqttMessage message = new MqttMessage();
        message.setPayload(payload.toString().getBytes());
        try {
            client.publish(topic, message);
        } catch (MqttException e) {
            Log.e(TAG, "Failed to publish message "+ e.getMessage());
            e.printStackTrace();
        }
    }



    @Override
    public IBinder onBind(Intent intent) {
        return this.binder;
    }

    public class GatewayBinder extends Binder {
        public GatewayService getService(){
            return  GatewayService.this;
        }
    }
}
