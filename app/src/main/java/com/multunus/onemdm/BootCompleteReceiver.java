package com.multunus.onemdm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.multunus.onemdm.com.multunus.onemdm.communication.GatewayService;

public class BootCompleteReceiver extends BroadcastReceiver {
    public BootCompleteReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, GatewayService.class));
    }
}
