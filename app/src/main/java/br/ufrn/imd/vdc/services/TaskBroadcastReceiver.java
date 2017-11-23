package br.ufrn.imd.vdc.services;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

import br.ufrn.imd.vdc.R;
import br.ufrn.imd.vdc.obd.ObdReading;

public class TaskBroadcastReceiver extends BroadcastReceiver {
    public static final String TASK_RESPONSE = "br.ufrn.imd.vdc.action.TASK_RESPONSE";
    public static final String TASK_STRING = "taskString";
    public static final String OBD_READING = "obdReading";
    private static final String TAG = TaskBroadcastReceiver.class.getName();

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.d(TAG, "onReceive: Received something...");
        ObdReading reading = (ObdReading) intent.getSerializableExtra(OBD_READING);
        String readingJson;
        if (reading != null) {
            readingJson = reading.toJSON();
            ServiceObds.sendJson(readingJson, context);
        }

        if (context instanceof Activity) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView tvResultsLog = (TextView) ((Activity) context).findViewById(R.id.tv_results);
                    tvResultsLog.append(intent.getStringExtra(TASK_STRING));
                }
            });
        }
    }
}
