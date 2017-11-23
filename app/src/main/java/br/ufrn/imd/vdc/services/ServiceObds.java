package br.ufrn.imd.vdc.services;

import android.content.Context;
import android.net.Uri;
import android.os.StrictMode;
import android.util.Log;


/**
 * Created by johnnylee on 22/11/17.
 */

public class ServiceObds {

    public static void sendJson(String obdJson, Context context) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("vehicleId", "123")
                    .appendQueryParameter("altitude", "0")
                    .appendQueryParameter("longitude", "0")
                    .appendQueryParameter("latitude", "0")
                    .appendQueryParameter("timestamp", "0")
                    .appendQueryParameter("readings", obdJson);
            Log.d("TESTE: ", WebServiceImpl.sendPost("/obds", builder.build().getEncodedQuery(), context));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
