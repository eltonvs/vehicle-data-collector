package br.ufrn.imd.vdc.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmManagerReceiver extends BroadcastReceiver {

    private static String TAG = AlarmManagerReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: Alarm Received, executing task...");
        ObdGatewayServiceManager.getInstance().enqueueDefaultCommands(context);
    }
}
