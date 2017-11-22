package br.ufrn.imd.vdc.services;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import br.ufrn.imd.vdc.obd.ObdReading;


/**
 * Created by johnnylee on 22/11/17.
 */

public class ServiceObds {

    public static void sendObd(ObdReading obdReading, Context context) {
        try {
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("vehicleId", obdReading.getVehicleId())
                    .appendQueryParameter("altitude", String.valueOf(obdReading.getAltitude()))
                    .appendQueryParameter("longitude", String.valueOf(obdReading.getLongitude()))
                    .appendQueryParameter("latitude", String.valueOf(obdReading.getLatitude()))
                    .appendQueryParameter("timestamp", String.valueOf(obdReading.getTimestamp()))
                    .appendQueryParameter("readings", new JSONObject(obdReading.getReadings()).toString());
            Log.d("TESTE: ", WebServiceImpl.sendPost("obds", builder.build().getEncodedQuery(), context));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendJson(String obdJson, Context context) {
        try {
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("readings", obdJson);
            Log.d("TESTE: ", WebServiceImpl.sendPost("obds", builder.build().getEncodedQuery(), context));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
