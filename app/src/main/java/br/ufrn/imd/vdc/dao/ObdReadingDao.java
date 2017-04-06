package br.ufrn.imd.vdc.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.imd.vdc.domain.ObdReading;
import br.ufrn.imd.vdc.helpers.ObdReadingDbHelper;

/**
 * Created by Cephas on 29/03/2017.
 */

public class ObdReadingDao {

    private SQLiteDatabase bd;

    public ObdReadingDao(Context context) {
        ObdReadingDbHelper obdReadingDbHelper = new ObdReadingDbHelper(context);
        bd = obdReadingDbHelper.getWritableDatabase();
    }

    public void insert(ObdReading o) {
        ContentValues values = new ContentValues(6);
        values.put("VEHICLE_ID", o.getVehicleId());
        values.put("TIMESTAMP", o.getTimestamp());
        values.put("LATITUDE", o.getLatitude());
        values.put("LONGITUDE", o.getLongitude());
        values.put("ALTITUDE", o.getAltitude());
        values.put("READINGS", (o.getReadings()));

        bd.insert("OBD_READING", null, values);
    }

    public void delete(ObdReading o) {
        String[] id = {String.valueOf(o.getId())};
        bd.delete("OBD_READING", "ID=?", id);
    }

    public List<ObdReading> list() {
        List<ObdReading> obdReadings = new ArrayList<>();
        Cursor c = bd.query("OBD_READING", ObdReading.COLUNAS,
                null, null, null, null, "TIMESTAMP");
        if (c.moveToFirst()) {
            do {
                ObdReading obdReading = new ObdReading();
                obdReading.setId(c.getInt(0));
                obdReading.setVehicleId(c.getString(1));
                obdReading.setTimestamp(c.getLong(2));
                obdReading.setLatitude(c.getDouble(3));
                obdReading.setLongitude(c.getDouble(4));
                obdReading.setAltitude(c.getDouble(5));
                obdReading.setReadings(c.getString(6));
                obdReadings.add(obdReading);
            } while (c.moveToNext());
        }
        c.close();
        return obdReadings;
    }

    public ObdReading findById(int id) {
        ObdReading obdReading = new ObdReading();

        Cursor c = bd.query("OBD_READING", ObdReading.COLUNAS,
                "id=" + id, null, null, null, null);

        if (c.moveToFirst()) {
            obdReading.setId(c.getInt(0));
            obdReading.setVehicleId(c.getString(1));
            obdReading.setTimestamp(c.getLong(2));
            obdReading.setLatitude(c.getDouble(3));
            obdReading.setLongitude(c.getDouble(4));
            obdReading.setAltitude(c.getDouble(5));
            obdReading.setReadings(c.getString(6));

        }
        c.close();
        return obdReading;
    }

}

