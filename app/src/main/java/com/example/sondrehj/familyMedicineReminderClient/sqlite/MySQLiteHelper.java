package com.example.sondrehj.familymedicinereminderclient.sqlite;

/**
 * Created by sondre on 31/03/2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.sondrehj.familymedicinereminderclient.models.Medication;

import java.util.ArrayList;

public class MySQLiteHelper extends SQLiteOpenHelper {

    SQLiteDatabase db;
    public static final String TABLE_MEDICATION = "medication";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "medication_name";
    public static final String COLUMN_COUNT = "count";
    public static final String COLUMN_UNIT = "unit";


    private static final String DATABASE_NAME = "medication.db";
    private static final int DATABASE_VERSION = 2;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_MEDICATION + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_NAME
            + " text not null, " + COLUMN_COUNT
            + " real, " + COLUMN_UNIT
            + " text not null);";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = this.getWritableDatabase();

    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
        System.out.println("DATABASE CREATED");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDICATION);
        onCreate(db);
    }

    // Adding new medication
    public void addMedication(Medication medication) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, medication.getName());
        values.put(COLUMN_COUNT, medication.getCount());
        values.put(COLUMN_UNIT, medication.getUnit());

        // Inserting Row
        db.insert(TABLE_MEDICATION, null, values);
        db.close(); // Closing database connection
    }

    public ArrayList<Medication> getMedications() {

        String selectQuery = "SELECT  * FROM " + TABLE_MEDICATION;
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);
        ArrayList<Medication> data = new ArrayList<Medication>();

        if (cursor.moveToFirst()) {
            do {

                String ownerId = "test";
                String name = cursor.getString(1);
                Double count = cursor.getDouble(2);
                String unit = cursor.getString(3);
                Medication m = new Medication(ownerId, name, count, unit);

                data.add(m);
                // get the data into array, or class variable
            } while (cursor.moveToNext());
        }
        cursor.close();
        return data;
    }

}
