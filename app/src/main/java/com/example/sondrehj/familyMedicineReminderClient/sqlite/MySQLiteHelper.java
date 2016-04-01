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
import com.example.sondrehj.familymedicinereminderclient.models.Reminder;

import java.security.acl.LastOwnerException;
import java.util.ArrayList;

public class MySQLiteHelper extends SQLiteOpenHelper {

    //Database information
    SQLiteDatabase db;
    private static final String DATABASE_NAME = "client.db";
    private static final int DATABASE_VERSION = 2;

    //Tables

    //Medication table
    public static final String TABLE_MEDICATION = "medication";
    public static final String COLUMN_MED_ID = "med_id";
    public static final String COLUMN_OWNER_ID = "owner_id";
    public static final String COLUMN_MED_NAME = "medication_name";
    public static final String COLUMN_MED_COUNT = "count";
    public static final String COLUMN_MED_UNIT = "unit";
    //Medication table creation statement
    private static final String CREATE_TABLE_MEDICATION = "create table "
            + TABLE_MEDICATION + "(" + COLUMN_MED_ID
            + " integer primary key autoincrement, " + COLUMN_OWNER_ID
            + " text not null, " + COLUMN_MED_NAME
            + " text not null, " + COLUMN_MED_COUNT
            + " real, " + COLUMN_MED_UNIT
            + " text not null);";

    //Reminder table
    public static final String TABLE_REMINDER = "reminder";
    public static final String COLUMN_REMINDER_ID = "reminder_id";
    public static final String COLUMN_REMINDER_NAME = "medication_name";
    public static final String COLUMN_REMINDER_TIME = "unit";
    //Reminder table creation statement
    private static final String CREATE_TABLE_REMINDER = "create table "
            + TABLE_REMINDER + "(" + COLUMN_REMINDER_ID
            + " integer primary key autoincrement, " + COLUMN_OWNER_ID
            + " text not null, " + COLUMN_REMINDER_NAME
            + " text not null, " + COLUMN_REMINDER_TIME
            + " text not null);";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = this.getWritableDatabase();

    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_MEDICATION);
        database.execSQL(CREATE_TABLE_REMINDER);
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

    //Queries, flyttes?

    public void addMedication(Medication medication) {
        // Add new medication
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_OWNER_ID, medication.getOwnerId());
        values.put(COLUMN_MED_NAME, medication.getName());
        values.put(COLUMN_MED_COUNT, medication.getCount());
        values.put(COLUMN_MED_UNIT, medication.getUnit());

        // Inserting Row
        long insertId = db.insert(TABLE_MEDICATION, null, values);
        medication.setmedId(safeLongToInt(insertId));

        db.close(); // Closing database connection
    }

    public void updateMedication(Medication medication) {
        // Update existing medication
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_MED_NAME, medication.getName());
        values.put(COLUMN_MED_COUNT, medication.getCount());
        values.put(COLUMN_MED_UNIT, medication.getUnit());

        db.update(TABLE_MEDICATION, values, "med_id=" + medication.getMedId(), null);
        db.close(); // Closing database connection
    }

    public ArrayList<Medication> getMedications() {
        //Retrieve medications
        String selectQuery = "SELECT  * FROM " + TABLE_MEDICATION;
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);
        ArrayList<Medication> data = new ArrayList<Medication>();

        if (cursor.moveToFirst()) {
            do {

                int id = cursor.getInt(0);
                String ownerId = "test";
                String name = cursor.getString(2);
                Double count = cursor.getDouble(3);
                String unit = cursor.getString(4);
                Medication m = new Medication(id, ownerId, name, count, unit);

                data.add(m);
                // get the data into array, or class variable
            } while (cursor.moveToNext());
        }
        cursor.close();
        return data;
    }

    public void addReminder(Reminder reminder) {
        // Add new reminder
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_OWNER_ID, reminder.getOwnerId());
        values.put(COLUMN_REMINDER_NAME, reminder.getName());
        values.put(COLUMN_REMINDER_TIME, reminder.getTime());

        // Inserting Row
        long insertId = db.insert(TABLE_REMINDER, null, values);
        reminder.setReminderId(safeLongToInt(insertId));

        db.close(); // Closing database connection
    }

    public void updateReminder(Reminder reminder) {
        // Update existing medication
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_REMINDER_NAME, reminder.getName());
        values.put(COLUMN_REMINDER_TIME, reminder.getTime());

        db.update(TABLE_REMINDER, values, "reminder_id=" + reminder.getReminderId(), null);
        db.close(); // Closing database connection
    }

    public ArrayList<Reminder> getReminders() {
        //Retrieve Reminders
        String selectQuery = "SELECT  * FROM " + TABLE_REMINDER;
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);
        ArrayList<Reminder> data = new ArrayList<Reminder>();

        if (cursor.moveToFirst()) {
            do {

                int id = cursor.getInt(0);
                String ownerId = "test";
                String name = cursor.getString(2);
                String time = cursor.getString(3);
                Reminder r = new Reminder(id, ownerId, name, time);

                data.add(r);
                // get the data into array, or class variable
            } while (cursor.moveToNext());
        }
        cursor.close();
        return data;
    }


    public static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }
}
