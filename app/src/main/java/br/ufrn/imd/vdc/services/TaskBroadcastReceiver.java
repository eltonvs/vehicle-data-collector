package br.ufrn.imd.vdc.services;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

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
        String readingJson = reading.toJSON();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        HttpURLConnection urlConnection = null;
        StringBuilder result = new StringBuilder();
        String line;

        try {
            URL url = new URL("https://behere-api-eltonvs1.c9users.io?dadosVeiculoJSON=" +
                              URLEncoder.encode(readingJson, "UTF-8"));
            urlConnection = (HttpURLConnection) url.openConnection();
            if (urlConnection.getResponseCode() >= 400) {
                Log.e(TAG, "doInBackground: Error");
            }

            urlConnection.setRequestMethod("GET");

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            Log.d(TAG, "onReceive: Result = " + result.toString());
        } catch (Exception e) {
            Log.e(TAG, "onReceive: Error sending GET Request", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        if (context instanceof Activity) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView tvResultsLog = (TextView) ((Activity) context)
                        .findViewById(R.id.tv_results);
                    tvResultsLog.append(intent.getStringExtra(TASK_STRING));
                }
            });
        }
    }
}
