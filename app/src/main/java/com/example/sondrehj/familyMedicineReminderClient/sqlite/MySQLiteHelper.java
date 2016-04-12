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

import com.example.sondrehj.familymedicinereminderclient.dummy.MedicationListContent;
import com.example.sondrehj.familymedicinereminderclient.dummy.ReminderListContent;
import com.example.sondrehj.familymedicinereminderclient.models.Medication;
import com.example.sondrehj.familymedicinereminderclient.models.Reminder;

import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

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
    public static final String COLUMN_REMINDER_OWNER_ID = "owner_id";
    public static final String COLUMN_REMINDER_ID = "reminder_id";
    public static final String COLUMN_REMINDER_NAME = "reminder_name";
    public static final String COLUMN_REMINDER_DATE = "date";
    public static final String COLUMN_REMINDER_ACTIVE = "active";
    public static final String COLUMN_REMINDER_DAYS = "days";
    public static final String COLUMN_REMINDER_END_DATE = "end_date";
    public static final String COLUMN_REMINDER_SERVER_ID = "reminder_server_id";
    //Reminder table creation statement
    private static final String CREATE_TABLE_REMINDER = "create table "
            + TABLE_REMINDER + "(" + COLUMN_REMINDER_ID
            + " integer primary key autoincrement, " + COLUMN_REMINDER_OWNER_ID
            + " text not null, " + COLUMN_REMINDER_NAME
            + " text not null, " + COLUMN_REMINDER_DATE
            + " text not null, " + COLUMN_REMINDER_ACTIVE
            + " boolean, " + COLUMN_REMINDER_DAYS
            + " text, " + COLUMN_REMINDER_END_DATE
            + " text, " + COLUMN_REMINDER_SERVER_ID
            + " integer);";

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

        //Prepares the statement
        ContentValues values = new ContentValues();
        values.put(COLUMN_MED_NAME, medication.getName());
        values.put(COLUMN_MED_COUNT, medication.getCount());
        values.put(COLUMN_MED_UNIT, medication.getUnit());

        db.update(TABLE_MEDICATION, values, "med_id=" + medication.getMedId(), null);
        db.close(); // Closing database connection
    }

    public void deleteMedication(Medication medication) {
        //Deletes a medication
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MEDICATION, "med_id=" + medication.getMedId(), null);

        //Removes the medication from the list content
        MedicationListContent.ITEMS.remove(medication);
        db.close();
    }


    public ArrayList<Medication> getMedications() {
        //Retrieve medications
        String selectQuery = "SELECT  * FROM " + TABLE_MEDICATION;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
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
            } while (cursor.moveToNext());
        }
        cursor.close();
        return data;
    }

    public void addReminder(Reminder reminder) {
        // Add new reminder
        SQLiteDatabase db = this.getWritableDatabase();

        // Converts the reminder date to a string on the format year;month;date;hour;min
        GregorianCalendar cal = reminder.getDate();
        String year = Integer.toString(cal.get(Calendar.YEAR));
        String month = Integer.toString(cal.get(Calendar.MONTH));
        String date = Integer.toString(cal.get(Calendar.DATE));
        String hour = Integer.toString(cal.get(Calendar.HOUR_OF_DAY));
        String min = Integer.toString(cal.get(Calendar.MINUTE));
        String dateString = year + ";" + month + ";" + date + ";" + hour + ";" + min;

        // Converts the reminder end date to a string on the format year;month;date;hour;min
        String endDateString = "0";
        if(reminder.getEndDate() != null) {
            GregorianCalendar endCal = reminder.getEndDate();
            String endYear = Integer.toString(endCal.get(Calendar.YEAR));
            String endMonth = Integer.toString(endCal.get(Calendar.MONTH));
            String endDate = Integer.toString(endCal.get(Calendar.DATE));
            String endHour = Integer.toString(endCal.get(Calendar.HOUR_OF_DAY));
            String endMin = Integer.toString(endCal.get(Calendar.MINUTE));
            endDateString = endYear + ";" + endMonth + ";" + endDate + ";" + endHour + ";" + endMin;
        }

        // Converts the reminder days array to a string on the format day1;day2;..;
        String dayString = "";
        for (int day : reminder.getDays()) {
            dayString += day + ";";
        }

        // Prepares the statement
        ContentValues values = new ContentValues();
        values.put(COLUMN_REMINDER_OWNER_ID, reminder.getOwnerId());
        values.put(COLUMN_REMINDER_NAME, reminder.getName());
        values.put(COLUMN_REMINDER_DATE, dateString);
        values.put(COLUMN_REMINDER_ACTIVE, reminder.getIsActive());
        values.put(COLUMN_REMINDER_DAYS, dayString);
        values.put(COLUMN_REMINDER_END_DATE, endDateString);
        values.put(COLUMN_REMINDER_SERVER_ID, reminder.getReminderServerId());

        // Inserting Row
        long insertId = db.insert(TABLE_REMINDER, null, values);
        reminder.setReminderId(safeLongToInt(insertId));
        System.out.println("New reminder: " + insertId + " scheduled for days: " + Arrays.toString(reminder.getDays()));

        db.close(); // Closing database connection
    }

    public void updateReminder(Reminder reminder) {

        SQLiteDatabase db = this.getWritableDatabase();

        // Converts the reminder date to a string on the format year;month;day;hour;min
        GregorianCalendar cal = reminder.getDate();
        String year = Integer.toString(cal.get(Calendar.YEAR));
        String month = Integer.toString(cal.get(Calendar.MONTH));
        String date = Integer.toString(cal.get(Calendar.DATE));
        String hour = Integer.toString(cal.get(Calendar.HOUR_OF_DAY));
        String min = Integer.toString(cal.get(Calendar.MINUTE));
        String dateString = year + ";" + month + ";" + date + ";" + hour + ";" + min;

        // Converts the reminder int[] days to a string on the format day1;day2;..;
        String dayString = "";
        for (int day : reminder.getDays()) {
            dayString += day + ";";
        }

        // Converts the reminder endDate to a string on the format year;month;day;hour;min
        String endDateString = "0";
        if(reminder.getEndDate() != null) {
            GregorianCalendar endCal = reminder.getEndDate();
            String endYear = Integer.toString(endCal.get(Calendar.YEAR));
            String endMonth = Integer.toString(endCal.get(Calendar.MONTH));
            String endDate = Integer.toString(endCal.get(Calendar.DATE));
            String endHour = Integer.toString(endCal.get(Calendar.HOUR_OF_DAY));
            String endMin = Integer.toString(endCal.get(Calendar.MINUTE));
            endDateString = endYear + ";" + endMonth + ";" + endDate + ";" + endHour + ";" + endMin;
        }

        // Prepares the statement
        ContentValues values = new ContentValues();
        values.put(COLUMN_REMINDER_NAME, reminder.getName());
        values.put(COLUMN_REMINDER_DATE, dateString);
        values.put(COLUMN_REMINDER_ACTIVE, reminder.getIsActive());
        values.put(COLUMN_REMINDER_DAYS, dayString);
        values.put(COLUMN_REMINDER_END_DATE, endDateString);

        System.out.println("Reminder: " + reminder.getReminderId() + " was updated");

        // Executes the query
        db.update(TABLE_REMINDER, values, "reminder_id=" + reminder.getReminderId(), null);
        db.close(); // Closing database connection
    }

    public ArrayList<Reminder> getReminders() {

        // Retrieve Reminders
        String selectQuery = "SELECT  * FROM " + TABLE_REMINDER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<Reminder> data = new ArrayList<Reminder>();

        // Loop through the retrieved data. Generates instances of the the reminder class.
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String ownerId = "test";
                String name = cursor.getString(2);
                String dateString = cursor.getString(3);
                boolean isActive = cursor.getInt(4) > 0;
                String dayString = cursor.getString(5);
                String endDateString = cursor.getString(6);
                int serverId = cursor.getInt(7);

                // Converting daysString to an int[] containing all the days.
                int[] days;
                if (dayString.length() > 0) {
                    String[] daysStringArray = dayString.split(";");
                    days = new int[daysStringArray.length];
                    for (int i = 0; i < daysStringArray.length; i++) {
                        days[i] = Integer.parseInt(daysStringArray[i]);
                    }
                } else {
                    days = new int[]{};
                }

                // Converting dateString to GregorianCalendar
                String[] dateArray = dateString.split(";");
                GregorianCalendar date = new GregorianCalendar(
                        Integer.parseInt(dateArray[0]), //Year
                        Integer.parseInt(dateArray[1]), //Month
                        Integer.parseInt(dateArray[2]), //Date
                        Integer.parseInt(dateArray[3]), //Hour
                        Integer.parseInt(dateArray[4])  //Minute
                );

                // Converting endDateString to GregorianCalendar
                GregorianCalendar endCal = new GregorianCalendar();
                if(!endDateString.equals("0")) {
                    String[] endDateArray = endDateString.split(";");
                        endCal = new GregorianCalendar(
                            Integer.parseInt(endDateArray[0]), //Year
                            Integer.parseInt(endDateArray[1]), //Month
                            Integer.parseInt(endDateArray[2]), //Date
                            Integer.parseInt(endDateArray[3]), //Hour
                            Integer.parseInt(endDateArray[4])  //Minute
                    );
                }
                Reminder reminder = new Reminder();
                reminder.setReminderId(id);
                reminder.setOwnerId(ownerId);
                reminder.setName(name);
                reminder.setDate(date);
                reminder.setIsActive(isActive);
                reminder.setDays(days);
                reminder.setEndDate(endCal);
                reminder.setReminderServerId(serverId);
                data.add(reminder);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return data;
    }

    public void deleteReminder(Reminder reminder) {
        // Deletes a reminder
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_REMINDER, "reminder_id=" + reminder.getReminderId(), null);
        //Removes the reminder from the list content
        ReminderListContent.ITEMS.remove(reminder);
        db.close();
    }


    public static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }
}
