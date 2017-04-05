package br.ufrn.imd.vdc.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Cephas on 29/03/2017.
 */

public class ObdReadingDbHelper  extends SQLiteOpenHelper {

    public static final String BANK_NAME = "ObdProject";
    public static final int BANK_VERSION = 1;
    public static final String OBD_READINGS_TABLE =
            "CREATE TABLE OBD_READINGS ( " +
            "ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
            "VEHICLE_ID TEXT, " +
            "LATITUDE REAL, " +
            "LONGITUDE REAL, " +
            "ALTITUDE REAL, " +
            "TIMESTAMP TEXT, " +
            "READINGS TEXT);";

    public ObdReadingDbHelper(Context context) {
        super(context, BANK_NAME, null, BANK_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(OBD_READINGS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS OBD_READINGS");
        onCreate(sqLiteDatabase);
    }


}
