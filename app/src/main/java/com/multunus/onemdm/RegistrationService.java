package com.multunus.onemdm;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.multunus.onemdm.communication.GatewayService;
import com.multunus.onemdm.network.DeviceRegistration;

public class RegistrationService extends IntentService {

    private static String TAG = "RegistrationService";

    private final DeviceRegistration deviceRegistration;

    public RegistrationService() {
        super("RegistrationService");
        this.deviceRegistration = new DeviceRegistration();
    }

    RegistrationService(DeviceRegistration deviceRegistration) {
        super("RegistrationService");
        this.deviceRegistration = deviceRegistration;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        deviceRegistration.sendRegistrationRequestToServer(getApplicationContext());
        Log.d(TAG, "inside RegistrationService.onHandleIntent");
        startService(new Intent(this.getApplicationContext(), GatewayService.class));
    }

}
