package br.ufrn.imd.vdc.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.imd.vdc.domain.ObdReading;
import br.ufrn.imd.vdc.helpers.ObdReadingDbHelper;

public class ObdReadingDAO extends GenericDAO<ObdReading> {
    private static final String[] columns = new String[]{
        ObdReadingDbHelper.READINGS_COLUMN_ID,
        ObdReadingDbHelper.READINGS_COLUMN_VEHICLE,
        ObdReadingDbHelper.READINGS_COLUMN_TIMESTAMP,
        ObdReadingDbHelper.READINGS_COLUMN_LATITUDE,
        ObdReadingDbHelper.READINGS_COLUMN_LONGITUDE,
        ObdReadingDbHelper.READINGS_COLUMN_ALTITUDE,
        ObdReadingDbHelper.READINGS_COLUMN_READINGS
    };

    public ObdReadingDAO(Context context) {
        dbHelper = new ObdReadingDbHelper(context);
    }

    @Override
    public ObdReading insert(ObdReading reading) {
        ContentValues values = new ContentValues();
        values.put(ObdReadingDbHelper.READINGS_COLUMN_VEHICLE, reading.getVehicleId());
        values.put(ObdReadingDbHelper.READINGS_COLUMN_TIMESTAMP, reading.getTimestamp());
        values.put(ObdReadingDbHelper.READINGS_COLUMN_LATITUDE, reading.getLatitude());
        values.put(ObdReadingDbHelper.READINGS_COLUMN_LONGITUDE, reading.getLongitude());
        values.put(ObdReadingDbHelper.READINGS_COLUMN_ALTITUDE, reading.getAltitude());
        values.put(ObdReadingDbHelper.READINGS_COLUMN_READINGS, reading.getReadings());

        long insertedId = database.insert(ObdReadingDbHelper.TABLE_READINGS, null, values);
        Cursor cursor = database.query(ObdReadingDbHelper.TABLE_READINGS, columns,
            ObdReadingDbHelper.READINGS_COLUMN_ID + " = " + insertedId, null, null, null, null);
        cursor.moveToFirst();
        ObdReading insertedReading = cursorToReading(cursor);
        cursor.close();
        return insertedReading;
    }

    @Override
    public void delete(ObdReading reading) {
        String[] id = {String.valueOf(reading.getId())};
        database.delete(ObdReadingDbHelper.TABLE_READINGS,
            ObdReadingDbHelper.READINGS_COLUMN_ID + " = ?", id);
    }

    @Override
    public List<ObdReading> list() {
        List<ObdReading> obdReadings = new ArrayList<>();
        Cursor cursor = database.query(ObdReadingDbHelper.TABLE_READINGS, columns, null, null, null,
            null, ObdReadingDbHelper.READINGS_COLUMN_TIMESTAMP);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            obdReadings.add(cursorToReading(cursor));
            cursor.moveToNext();
        }
        cursor.close();

        return obdReadings;
    }

    @Override
    public ObdReading findById(int id) {
        ObdReading obdReading = null;

        Cursor cursor = database.query(ObdReadingDbHelper.TABLE_READINGS, columns, "id=" + id, null,
            null, null, null);
        if (cursor.moveToFirst()) {
            obdReading = cursorToReading(cursor);
        }
        cursor.close();

        return obdReading;
    }

    private ObdReading cursorToReading(Cursor cursor) {
        ObdReading obdReading = new ObdReading();
        obdReading.setId(cursor.getInt(0));
        obdReading.setVehicleId(cursor.getString(1));
        obdReading.setTimestamp(cursor.getLong(2));
        obdReading.setLatitude(cursor.getDouble(3));
        obdReading.setLongitude(cursor.getDouble(4));
        obdReading.setAltitude(cursor.getDouble(5));
        obdReading.setReadings(cursor.getString(6));

        return obdReading;
    }
}
