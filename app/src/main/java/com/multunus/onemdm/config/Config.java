package com.multunus.onemdm.config;

import android.content.Context;


public class Config {
    public static final String TOPIC = "devices.heartbeats";//"devices.hearbeats";
    public static final long HEARTBEAT_FREQUENCY = 1000; //AlarmManager.INTERVAL_FIFTEEN_MINUTES;
    public static String PREFERENCE_TAG = "onemdm";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String REGISTRATION_URL ="http://192.168.2.207:3000/api/register";

    public static String getSharedPreference(Context context, String key){
        String token =  context.getSharedPreferences(Config.PREFERENCE_TAG,
                Context.MODE_PRIVATE).getString(key, "");
        return token;
    }
}
