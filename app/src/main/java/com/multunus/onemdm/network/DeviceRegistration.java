package com.multunus.onemdm.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rollbar.android.Rollbar;
import com.multunus.onemdm.config.Config;
import com.multunus.onemdm.model.Device;

import org.json.JSONException;
import org.json.JSONObject;

public class DeviceRegistration {
    private static String TAG = "DeviceRegistration";
    public void sendRegistrationRequestToServer(final Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest deviceRegistrationRequest = new JsonObjectRequest(
                Request.Method.POST,
                Config.REGISTRATION_URL,
                getJsonPayload(context),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG,"Successfully registered");
                        saveSettings(context,response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        if(error.networkResponse!=null && error.networkResponse.data!=null){
                            Log.e(TAG, new String(error.networkResponse.data));
                        }

                    }
                }
        );
        requestQueue.add(deviceRegistrationRequest);
    }

    private void saveSettings(Context context,JSONObject response) {
        String accessToken = "";
        SharedPreferences.Editor editor = context.getSharedPreferences(
                Config.PREFERENCE_TAG, Context.MODE_PRIVATE).edit();
        try {
            accessToken = response.getString(Config.ACCESS_TOKEN);
        } catch (JSONException e) {
            Log.e(TAG,"Exception while registering",e);
        }
        editor.putString(Config.ACCESS_TOKEN, accessToken);
        editor.apply();
    }

    private JSONObject getJsonPayload(Context context) {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        JSONObject deviceData = new JSONObject();
        try {
            deviceData.put("device", new JSONObject(gson.toJson(getDevice(context))));
            Log.d(TAG,deviceData.toString());
        } catch (Exception e) {
            Log.e(TAG,e.getMessage());
            Rollbar.reportException(e);
        }
        Log.d(TAG,"Device data to be send " + deviceData);
        return deviceData;
    }

    private Device getDevice(Context context) throws Exception{
        Device device = new Device();
        device.setModel(getDeviceModel());
        device.setImeiNumber(getImeiNumber(context));
        device.setUniqueId(getAndroidId(context));
        device.setClientVersion(getAppVersion(context));
        device.setOsVersion(Build.VERSION.RELEASE);
        return device;
    }

    private String getDeviceModel() {
        return Build.MANUFACTURER + " - " + Build.MODEL;
    }

    private String getAppVersion(Context context) {
        try{
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            return info.versionName + " - " + info.versionCode;
        }
        catch (Exception ex){
            Rollbar.reportException(ex);
            Log.e(TAG,ex.getMessage());
        }
        return "";
    }
    private String getImeiNumber(Context context) {
        try {
            TelephonyManager telephonyManager =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return telephonyManager.getDeviceId();
        }
        catch (Exception ex){
            //Ignoring as its a known bug in Marshmellow
        }
        return "";
    }

    private String getAndroidId(Context context) {
        return Settings.Secure.getString(
                context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
