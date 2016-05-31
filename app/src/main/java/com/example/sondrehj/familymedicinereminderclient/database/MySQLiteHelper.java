package com.example.sondrehj.familymedicinereminderclient.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.sondrehj.familymedicinereminderclient.models.Medication;
import com.example.sondrehj.familymedicinereminderclient.models.Reminder;
import com.example.sondrehj.familymedicinereminderclient.models.User;
import com.example.sondrehj.familymedicinereminderclient.models.User2;
import com.example.sondrehj.familymedicinereminderclient.utility.Converter;
import com.example.sondrehj.familymedicinereminderclient.utility.ReminderComparator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;

public class MySQLiteHelper extends SQLiteOpenHelper {

    //Database information
    SQLiteDatabase db;
    private static final String DATABASE_NAME = "familymedicinereminderclient.db";
    private static final int DATABASE_VERSION = 2;

    //Tables

    //Medication table
    public static final String TABLE_MEDICATION = "medication";
    public static final String COLUMN_MED_ID = "med_id";
    public static final String COLUMN_MED_SERVER_ID = "server_id";
    public static final String COLUMN_OWNER_ID = "owner_id";
    public static final String COLUMN_MED_NAME = "medication_name";
    public static final String COLUMN_MED_COUNT = "count";
    public static final String COLUMN_MED_UNIT = "unit";
    //Medication table creation statement
    private static final String CREATE_TABLE_MEDICATION = "create table "
            + TABLE_MEDICATION + "(" + COLUMN_MED_ID
            + " integer primary key autoincrement, " + COLUMN_MED_SERVER_ID
            + " integer not null, " + COLUMN_OWNER_ID
            + " text not null, " + COLUMN_MED_NAME
            + " text not null, " + COLUMN_MED_COUNT
            + " real, " + COLUMN_MED_UNIT
            + " text not null);";

    // Reminder table
    public static final String TABLE_REMINDER = "reminder";
    public static final String COLUMN_REMINDER_OWNER_ID = "owner_id";
    public static final String COLUMN_REMINDER_ID = "reminder_id";
    public static final String COLUMN_REMINDER_NAME = "reminder_name";
    public static final String COLUMN_REMINDER_DATE = "date";
    public static final String COLUMN_REMINDER_ACTIVE = "active";
    public static final String COLUMN_REMINDER_DAYS = "days";
    public static final String COLUMN_REMINDER_END_DATE = "end_date";
    public static final String COLUMN_REMINDER_SERVER_ID = "reminder_server_id";
    public static final String COLUMN_REM_MEDICATION_ID = "reminder_medication_id";
    public static final String COLUMN_REM_MEDICATION_DOSAGE = "medication_dosage";
    public static final String COLUMN_REMINDER_TIME_TAKEN = "time_taken";
    // Reminder table creation statement
    private static final String CREATE_TABLE_REMINDER = "create table "
            + TABLE_REMINDER + "(" + COLUMN_REMINDER_ID
            + " integer primary key autoincrement, " + COLUMN_REMINDER_OWNER_ID
            + " text not null, " + COLUMN_REMINDER_NAME
            + " text not null, " + COLUMN_REMINDER_DATE
            + " text not null, " + COLUMN_REMINDER_ACTIVE
            + " boolean, " + COLUMN_REMINDER_DAYS
            + " text, " + COLUMN_REMINDER_END_DATE
            + " text, " + COLUMN_REMINDER_SERVER_ID
            + " integer, " + COLUMN_REM_MEDICATION_ID
            + " integer, " + COLUMN_REM_MEDICATION_DOSAGE
            + " real, " + COLUMN_REMINDER_TIME_TAKEN
            + " string);";

    // User table
    public static final String TABLE_USER = "user";
    public static final String COLUMN_USER_USER_ID = "user_id";
    public static final String COLUMN_USER_ALIAS = "alias";

    private static final String CREATE_TABLE_USER = "create table "
            + TABLE_USER + "(" + COLUMN_USER_USER_ID
            + " text primary key, " + COLUMN_USER_ALIAS
            + " text not null)";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_MEDICATION);
        database.execSQL(CREATE_TABLE_REMINDER);
        database.execSQL(CREATE_TABLE_USER);
        Log.d("MySQLiteHelper", "DATABASE CREATED");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDICATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REMINDER);
        db.execSQL("DOP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    // ----- MEDICATIONS ----- //

    /**
     * Adds a new {@link Medication} to the database.
     *
     * @param medication the medication to be added
     */
    public void addMedication(Medication medication) {
        // Add new medication
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_MED_SERVER_ID, medication.getServerId());
        values.put(COLUMN_OWNER_ID, medication.getOwnerId());
        values.put(COLUMN_MED_NAME, medication.getName());
        values.put(COLUMN_MED_COUNT, medication.getCount());
        values.put(COLUMN_MED_UNIT, medication.getUnit());

        // Inserting Row
        long insertId = db.insert(TABLE_MEDICATION, null, values);
        medication.setmedId(safeLongToInt(insertId));

        db.close(); // Closing database connection
    }

    /**
     * Updates a {@link Medication} in the database.
     *
     * @param medication the medication to be updated.
     */
    public void updateMedication(Medication medication) {
        // Update existing medication
        SQLiteDatabase db = this.getWritableDatabase();

        //Prepares the statement
        ContentValues values = new ContentValues();
        values.put(COLUMN_MED_SERVER_ID, medication.getServerId());
        values.put(COLUMN_MED_NAME, medication.getName());
        values.put(COLUMN_MED_COUNT, medication.getCount());
        values.put(COLUMN_MED_UNIT, medication.getUnit());

        db.update(TABLE_MEDICATION, values, "med_id=" + medication.getMedId(), null);
        db.close(); // Closing database connection
    }

    /**
     * Retrieves all the {@link Medication} contained in the database.
     *
     * @return an {@link ArrayList} of {@link Medication}
     */
    public ArrayList<Medication> getMedications() {
        //Retrieve medications
        String selectQuery = "SELECT  * FROM " + TABLE_MEDICATION;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<Medication> data = new ArrayList<Medication>();

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                int serverId = cursor.getInt(1);
                String ownerId = cursor.getString(2);
                String name = cursor.getString(3);
                Double count = cursor.getDouble(4);
                String unit = cursor.getString(5);
                Medication m = new Medication(id, serverId, ownerId, name, count, unit);
                data.add(m);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return data;
    }

    /**
     * Retrieves all the {@link Medication} for a user, given their id.
     *
     * @param ownerId the id of the user.
     * @return an {@link ArrayList} of {@link Medication}
     */
    public ArrayList<Medication> getMedicationsByOwnerId(String ownerId) {
        //Retrieve medications
        String selectQuery =
                "SELECT  *" +
                        " FROM " + TABLE_MEDICATION +
                        " WHERE " + COLUMN_OWNER_ID + "='" + ownerId + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<Medication> data = new ArrayList<Medication>();

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                int serverId = cursor.getInt(1);
                String owner = cursor.getString(2);
                String name = cursor.getString(3);
                Double count = cursor.getDouble(4);
                String unit = cursor.getString(5);
                Medication m = new Medication(id, serverId, owner, name, count, unit);
                data.add(m);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return data;
    }

    /**
     * Deletes a {@link Medication} from the database.
     *
     * @param medication the medication to be deleted.
     */
    public void deleteMedication(Medication medication) {
        //Deletes a medication
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MEDICATION, "med_id=" + medication.getMedId(), null);

        //Removes the medication from the list content

        //MedicationListFragment.medications.remove(medication);   //TODO: Fix deletion now that MedicationListCOntent is removed.
        db.close();
    }

    /**
     * Updates the amount of a given {@link Medication}.
     *
     * @param medication the medication to be updated.
     */
    public void updateAmountMedication(Medication medication) {
        // Update existing medication
        SQLiteDatabase db = this.getWritableDatabase();

        //Prepares the statement
        ContentValues values = new ContentValues();
        values.put(COLUMN_MED_COUNT, medication.getCount());
        db.update(TABLE_MEDICATION, values, "med_id=" + medication.getMedId(), null);
        db.close(); // Closing database connection
    }

    /**
     * Retrieves a {@link Medication} by the serverId
     *
     * @param medId the serverId.
     */
    public Medication getSingleMedicationByServerID(int medId) {
        String selectQuery = "SELECT  * FROM " + TABLE_MEDICATION + " WHERE " + COLUMN_MED_SERVER_ID + " = " + medId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            int serverId = cursor.getInt(1);
            String ownerId = cursor.getString(2);
            String name = cursor.getString(3);
            Double count = cursor.getDouble(4);
            String unit = cursor.getString(5);
            return new Medication(id, serverId, ownerId, name, count, unit);
        }
        return null;
    }

    // ----- REMINDERS ----- //

    /**
     * Adds a new {@link Reminder} to the database.
     *
     * @param reminder the reminder to be added
     */
    public Reminder addReminder(Reminder reminder) {
        // Add new reminder
        SQLiteDatabase db = this.getWritableDatabase();

        // Converts the reminder date to a string on the format year;month;date;hour;min
        String dateString = Converter.calendarToDatabaseString(reminder.getDate());

        // Converts the reminder end date to a string on the format year;month;date;hour;min
        String endDateString = "0";
        if (reminder.getEndDate() != null) {
            endDateString = Converter.calendarToDatabaseString(reminder.getEndDate());
        }

        // Converts the reminder timeTaken to a string on the format year;month;day;hour;min
        String timeTakenString = "0";
        if (reminder.getTimeTaken() != null) {
            timeTakenString = Converter.calendarToDatabaseString(reminder.getTimeTaken());
        }

        // Converts the reminder days array to a string on the format day1;day2;..;
        String dayString = Converter.daysArrayToDatabaseString(reminder.getDays());

        // Prepares the statement
        ContentValues values = new ContentValues();
        values.put(COLUMN_REMINDER_OWNER_ID, reminder.getOwnerId());
        values.put(COLUMN_REMINDER_NAME, reminder.getName());
        values.put(COLUMN_REMINDER_DATE, dateString);
        values.put(COLUMN_REMINDER_ACTIVE, reminder.getIsActive());
        values.put(COLUMN_REMINDER_DAYS, dayString);
        values.put(COLUMN_REMINDER_END_DATE, endDateString);
        values.put(COLUMN_REMINDER_SERVER_ID, reminder.getServerId());
        values.put(COLUMN_REMINDER_TIME_TAKEN, timeTakenString);
        // We store the medicationId as a reference if a medication is attached.
        if (reminder.getMedicine() != null) {
            values.put(COLUMN_REM_MEDICATION_ID, reminder.getMedicine().getMedId());
            values.put(COLUMN_REM_MEDICATION_DOSAGE, reminder.getDosage());
        }

        // Inserting Row
        long insertId = db.insert(TABLE_REMINDER, null, values);
        reminder.setReminderId(safeLongToInt(insertId));
        db.close(); // Closing database connection
        return reminder;
    }

    /**
     * Updates a {@link Reminder} in the database.
     *
     * @param reminder the medication to be updated.
     */
    public Reminder updateReminder(Reminder reminder) {

        SQLiteDatabase db = this.getWritableDatabase();

        // Converts the reminder date to a string on the format year;month;day;hour;min
        String dateString = Converter.calendarToDatabaseString(reminder.getDate());

        // Converts the reminder int[] days to a string on the format day1;day2;..;
        String dayString = Converter.daysArrayToDatabaseString(reminder.getDays());

        // Converts the reminder endDate to a string on the format year;month;day;hour;min
        String endDateString = "0";
        if (reminder.getEndDate() != null) {
            endDateString = Converter.calendarToDatabaseString(reminder.getEndDate());
        }

        // Converts the reminder timeTaken to a string on the format year;month;day;hour;min
        String timeTakenString = "0";
        if (reminder.getTimeTaken() != null) {
            timeTakenString = Converter.calendarToDatabaseString(reminder.getTimeTaken());
        }

        // Prepares the statement
        ContentValues values = new ContentValues();
        values.put(COLUMN_REMINDER_SERVER_ID, reminder.getServerId());
        values.put(COLUMN_REMINDER_NAME, reminder.getName());
        values.put(COLUMN_REMINDER_DATE, dateString);
        values.put(COLUMN_REMINDER_ACTIVE, reminder.getIsActive());
        values.put(COLUMN_REMINDER_DAYS, dayString);
        values.put(COLUMN_REMINDER_END_DATE, endDateString);
        values.put(COLUMN_REMINDER_TIME_TAKEN, timeTakenString);
        // We store the medicationId as a reference if a medication is attached.
        if (reminder.getMedicine() != null) {
            values.put(COLUMN_REM_MEDICATION_ID, reminder.getMedicine().getMedId());
            values.put(COLUMN_REM_MEDICATION_DOSAGE, reminder.getDosage());
        } else {
            values.putNull(COLUMN_REM_MEDICATION_ID);
            values.putNull(COLUMN_REM_MEDICATION_DOSAGE);
        }

        // Executes the query
        db.update(TABLE_REMINDER, values, "reminder_id=" + reminder.getReminderId(), null);
        db.close(); // Closing database
        return reminder;
    }

    /**
     * Retrieves all the {@link Reminder} contained in the database.
     *
     * @return an {@link ArrayList} of {@link Reminder}
     */
    public ArrayList<Reminder> getReminders() {

        // Retrieve Reminders
        String selectQuery = "SELECT  * FROM " + TABLE_REMINDER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<Reminder> data = new ArrayList<>();

        // Loop through the retrieved data. Generates instances of the the reminder class.
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String ownerId = cursor.getString(1);
                String name = cursor.getString(2);
                String dateString = cursor.getString(3);
                boolean isActive = cursor.getInt(4) > 0;
                String dayString = cursor.getString(5);
                String endDateString = cursor.getString(6);
                int serverId = cursor.getInt(7);
                int medicationId = cursor.getInt(8);
                Double dosage = cursor.getDouble(9);
                String timeTakenString = cursor.getString(10);

                // Converting daysString to an int[] containing all the days.
                int[] days = Converter.databaseDayStringToArray(dayString);

                // Converting dateString to GregorianCalendar
                GregorianCalendar date = Converter.databaseDateStringToCalendar(dateString);

                // Converting endDateString to GregorianCalendar
                GregorianCalendar endCal = new GregorianCalendar();
                if (!endDateString.equals("0")) {
                    endCal = Converter.databaseDateStringToCalendar(endDateString);
                }

                // Converting timeTakenString to GregorianCalendar
                GregorianCalendar timeTaken = null;
                if (!timeTakenString.equals("0")) {
                    timeTaken = Converter.databaseDateStringToCalendar(timeTakenString);
                }

                Reminder reminder = new Reminder();
                reminder.setReminderId(id);
                reminder.setOwnerId(ownerId);
                reminder.setName(name);
                reminder.setDate(date);
                reminder.setIsActive(isActive);
                reminder.setDays(days);
                reminder.setEndDate(endCal);
                reminder.setServerId(serverId);
                reminder.setTimeTaken(timeTaken);
                // Attaches a referenced medication to the reminder object if set.
                // "Join"-operation
                if (medicationId != 0) {
                    for (Medication med : getMedications())//MedicationListFragment.medications){
                        if (med.getMedId() == medicationId) {
                            reminder.setMedicine(med);
                            reminder.setDosage(dosage);
                        }
                }

                data.add(reminder);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return data;
    }

    // Could possibly be integrated with getReminders()

    /**
     * Retrieves all the {@link Reminder} for a user, given their id.
     *
     * @param ownerId the id of the user.
     * @return an {@link ArrayList} of {@link Reminder}
     */
    public ArrayList<Reminder> getRemindersByOwnerId(String ownerId) {

        // Retrieve Reminders
        String selectQuery =
                "SELECT  *" +
                        " FROM " + TABLE_REMINDER +
                        " WHERE " + COLUMN_REMINDER_OWNER_ID + "='" + ownerId + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<Reminder> data = new ArrayList<>();

        // Loop through the retrieved data. Generates instances of the the reminder class.
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String owner = cursor.getString(1);
                String name = cursor.getString(2);
                String dateString = cursor.getString(3);
                boolean isActive = cursor.getInt(4) > 0;
                String dayString = cursor.getString(5);
                String endDateString = cursor.getString(6);
                int serverId = cursor.getInt(7);
                int medicationId = cursor.getInt(8);
                Double dosage = cursor.getDouble(9);
                String timeTakenString = cursor.getString(10);

                // Converting daysString to an int[] containing all the days.
                int[] days = Converter.databaseDayStringToArray(dayString);

                // Converting dateString to GregorianCalendar
                GregorianCalendar date = Converter.databaseDateStringToCalendar(dateString);

                // Converting endDateString to GregorianCalendar
                GregorianCalendar endCal = new GregorianCalendar();
                if (!endDateString.equals("0")) {
                    endCal = Converter.databaseDateStringToCalendar(endDateString);
                }

                // Converting timeTakenString to GregorianCalendar
                GregorianCalendar timeTaken = new GregorianCalendar();
                if (!timeTakenString.equals("0")) {
                    timeTaken = Converter.databaseDateStringToCalendar(timeTakenString);
                }

                Reminder reminder = new Reminder();
                reminder.setReminderId(id);
                reminder.setOwnerId(owner);
                reminder.setName(name);
                reminder.setDate(date);
                reminder.setIsActive(isActive);
                reminder.setDays(days);
                reminder.setEndDate(endCal);
                reminder.setServerId(serverId);
                reminder.setTimeTaken(timeTaken);
                // Attaches a referenced medication to the reminder object if set.
                // "Join"-operation
                if (medicationId != 0) {
                    for (Medication med : getMedications())//MedicationListFragment.medications){
                        if (med.getMedId() == medicationId) {
                            reminder.setMedicine(med);
                            reminder.setDosage(dosage);
                        }
                }
                data.add(reminder);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return data;
    }

    /**
     * Retrieves a {@link Reminder} by the localId.
     *
     * @param localId the local id of the Reminder
     * @return the retrieved reminder
     */
    public Reminder getReminderByLocalId(int localId) {

        // Retrieve Reminders
        String selectQuery =
                "SELECT  *" +
                        " FROM " + TABLE_REMINDER +
                        " WHERE " + COLUMN_REMINDER_ID + "=" + localId;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Loop through the retrieved data. Generates instances of the the reminder class.
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            String owner = cursor.getString(1);
            String name = cursor.getString(2);
            String dateString = cursor.getString(3);
            boolean isActive = cursor.getInt(4) > 0;
            String dayString = cursor.getString(5);
            String endDateString = cursor.getString(6);
            int serverId = cursor.getInt(7);
            int medicationId = cursor.getInt(8);
            Double dosage = cursor.getDouble(9);
            String timeTakenString = cursor.getString(10);

            // Converting daysString to an int[] containing all the days.
            int[] days = Converter.databaseDayStringToArray(dayString);

            // Converting dateString to GregorianCalendar
            GregorianCalendar date = Converter.databaseDateStringToCalendar(dateString);

            // Converting endDateString to GregorianCalendar
            GregorianCalendar endCal = new GregorianCalendar();
            if (!endDateString.equals("0")) {
                endCal = Converter.databaseDateStringToCalendar(endDateString);
            }

            // Converting timeTakenString to GregorianCalendar
            GregorianCalendar timeTaken = new GregorianCalendar();
            if (!timeTakenString.equals("0")) {
                timeTaken = Converter.databaseDateStringToCalendar(timeTakenString);
            }

            Reminder reminder = new Reminder();
            reminder.setReminderId(id);
            reminder.setOwnerId(owner);
            reminder.setName(name);
            reminder.setDate(date);
            reminder.setIsActive(isActive);
            reminder.setDays(days);
            reminder.setEndDate(endCal);
            reminder.setServerId(serverId);
            reminder.setTimeTaken(timeTaken);
            // Attaches a referenced medication to the reminder object if set.
            // "Join"-operation
            if (medicationId != 0) {
                for (Medication med : getMedications())//MedicationListFragment.medications){
                    if (med.getMedId() == medicationId) {
                        reminder.setMedicine(med);
                        reminder.setDosage(dosage);
                    }
            }
            cursor.close();
            db.close();
            return reminder;
        }
        cursor.close();
        db.close();
        return null;
    }

    /**
     * Sets the timeTaken for a {@link Reminder}.
     *
     * @param reminder the reminder to be updated.
     */
    public void setReminderTimeTaken(Reminder reminder) {

        // Update existing medication
        SQLiteDatabase db = this.getWritableDatabase();

        // Converts the reminder timeTaken to a string on the format year;month;day;hour;min
        String timeTakenString = "0";
        if (reminder.getTimeTaken() != null) {
            timeTakenString = Converter.calendarToDatabaseString(reminder.getTimeTaken());
        }

        //Prepares the statement
        ContentValues values = new ContentValues();
        values.put(COLUMN_REMINDER_TIME_TAKEN, timeTakenString);

        db.update(TABLE_REMINDER, values, "reminder_id=" + reminder.getReminderId(), null);
        db.close(); // Closing database connection
    }

    /**
     * Resets the timeTaken field of all reminders to 0.
     */
    public void resetTimeTaken() {

        // Update existing medication
        SQLiteDatabase db = this.getWritableDatabase();

        //Prepares the statement
        ContentValues values = new ContentValues();
        values.put(COLUMN_REMINDER_TIME_TAKEN, "0");

        db.update(TABLE_REMINDER, values, COLUMN_REMINDER_TIME_TAKEN, null);
        db.close(); // Closing database connection
    }

    /**
     * Retrieves all the {@link Reminder} that are scheduled for the current date.
     *
     * @return an {@link ArrayList} of {@link Reminder}
     */
    public ArrayList<Reminder> getTodaysReminders() {

        GregorianCalendar todaysDate = new GregorianCalendar();
        int currentDay = todaysDate.get(Calendar.DAY_OF_WEEK) - 1;
        ArrayList<Reminder> allReminders = getReminders();
        ArrayList<Reminder> activeReminders = new ArrayList<>();
        ArrayList<Reminder> todaysReminders = new ArrayList<>();

        for (Reminder r : allReminders) {
            if (r.getIsActive()) {
                activeReminders.add(r);
            }
        }

        outerLoop:
        for (Reminder reminder : activeReminders) {
            // Repeating
            if (reminder.getDays().length > 0 && todaysDate.before(reminder.getEndDate())) {
                for (int day : reminder.getDays()) {
                    if (day == currentDay) {
                        todaysReminders.add(reminder);
                        continue outerLoop;
                    }
                }
                // Non-repeating
            } else if (reminder.getDays().length == 0 && Converter.isSameDate(todaysDate, reminder.getDate())) {
                todaysReminders.add(reminder);
            }
        }

        Collections.sort(todaysReminders, new ReminderComparator());

        return todaysReminders;
    }

    /**
     * Deletes a {@link Reminder} from the database.
     *
     * @param reminder the reminder to be deleted.
     */
    public void deleteReminder(Reminder reminder) {
        // Deletes a reminder
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_REMINDER, "reminder_id=" + reminder.getReminderId(), null);
        db.close();
    }

    // ----- USERS ----- //

    /**
     * Retrieves all the {@link User2} contained in the database.
     *
     * @return an {@link ArrayList} of {@link User2}
     */
    public ArrayList<User2> getUsers() {

        // Retrieve Users
        String selectQuery = "SELECT  * FROM " + TABLE_USER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<User2> data = new ArrayList<>();
        // Loop through the retrieved data. Generates instances of the the user class.
        if (cursor.moveToFirst()) {
            do {
                String userId = cursor.getString(0);
                String alias = cursor.getString(1);
                User2 user = new User2(userId, alias);
                data.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return data;
    }

    /**
     * Adds a new {@link User2} to the database.
     *
     * @param user the user to be added
     */
    public void addUser(User2 user) {
        // Add new user
        SQLiteDatabase db = this.getWritableDatabase();
        // Prepares the statement
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_USER_ID, user.getUserId());
        values.put(COLUMN_USER_ALIAS, user.getAlias());
        // Inserting Row
        db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection
    }

    /**
     * Updates a {@link User2} in the database.
     *
     * @param user the user to be updated.
     */
    public void updateUser(User2 user) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Prepares the statement
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_USER_ID, user.getUserId());
        values.put(COLUMN_USER_ALIAS, user.getAlias());
        // Executes the query
        db.update(TABLE_USER, values, "user_id='" + user.getUserId() + "'", null);
        db.close(); // Closing database connection
    }

    /**
     * Deletes a {@link User2} from the database.
     *
     * @param userId the id of the user to be deleted.
     */
    public void deleteUser(String userId) {
        //Deletes a medication
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER, "user_id='" + userId + "'", null);
        db.close();
    }

    /**
     * Safely converts a long to an int.
     *
     * @param l the long to be converted.
     * @return an int (The converted long)
     */
    public static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }
}
