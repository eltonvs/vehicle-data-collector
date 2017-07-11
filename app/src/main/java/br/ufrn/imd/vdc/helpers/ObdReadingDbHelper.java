package br.ufrn.imd.vdc.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Cephas on 29/03/2017.
 * Updated by Elton Viana on 22/06/2017.
 */

public class ObdReadingDbHelper extends SQLiteOpenHelper {

    public static final String TABLE_READINGS = "readings";
    public static final String READINGS_COLUMN_ID = "_id";
    public static final String READINGS_COLUMN_VEHICLE = "vehicle_id";
    public static final String READINGS_COLUMN_TIMESTAMP = "timestamp";
    public static final String READINGS_COLUMN_LATITUDE = "latitude";
    public static final String READINGS_COLUMN_LONGITUDE = "longitude";
    public static final String READINGS_COLUMN_ALTITUDE = "altitude";
    public static final String READINGS_COLUMN_READINGS = "readings";

    // Database creation sql statement
    private static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_READINGS + "(" +
            READINGS_COLUMN_ID + " integer primary key not null autoincrement," +
            READINGS_COLUMN_VEHICLE + " text," + READINGS_COLUMN_TIMESTAMP + " text," +
            READINGS_COLUMN_LATITUDE + " real," + READINGS_COLUMN_LONGITUDE + " real," +
            READINGS_COLUMN_ALTITUDE + " real," + READINGS_COLUMN_READINGS + " text);";

    private static final String DATABASE_NAME = "obdCollector.db";
    private static final int DATABASE_VERSION = 1;

    public ObdReadingDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(ObdReadingDbHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to " + newVersion + ", which " +
                        "will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_READINGS);
        onCreate(db);
    }

}
