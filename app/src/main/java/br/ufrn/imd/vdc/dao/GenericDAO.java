package br.ufrn.imd.vdc.dao;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

/**
 * Created by elton on 22/06/17.
 */

public abstract class GenericDAO<T> {
    protected SQLiteOpenHelper dbHelper;
    protected SQLiteDatabase database;

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    abstract T insert(T o);

    abstract void delete(T o);

    abstract List<T> list();

    abstract T findById(int id);
}
