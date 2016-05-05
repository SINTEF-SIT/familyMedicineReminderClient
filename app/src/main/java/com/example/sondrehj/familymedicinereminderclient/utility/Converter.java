package com.example.sondrehj.familymedicinereminderclient.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by sondre on 20/04/2016.
 */
public final class Converter {


    public static GregorianCalendar dateStringToCalendar(String date, String time) {

        String[] dateArray = date.split("\\W+");
        String[] timeArray = time.split(":");

        System.out.println(Arrays.toString(dateArray));
        System.out.println(Arrays.toString(timeArray));

        // Set start date and time
        GregorianCalendar cal = new GregorianCalendar(
                Integer.parseInt(dateArray[2]),      //Year
                Integer.parseInt(dateArray[1]) - 1,  //Month
                Integer.parseInt(dateArray[0]),      //Date
                Integer.parseInt(timeArray[0]),      //Hour
                Integer.parseInt(timeArray[1]));     //Minute

        return cal;
    }

    public static GregorianCalendar databaseDateStringToCalendar(String dateString) {

        System.out.println(dateString);
        String[] dateArray = dateString.split(";");
        System.out.println(Arrays.toString(dateArray));
        GregorianCalendar cal = new GregorianCalendar(
                Integer.parseInt(dateArray[0]), //Year
                Integer.parseInt(dateArray[1]), //Month
                Integer.parseInt(dateArray[2]), //Date
                Integer.parseInt(dateArray[3]), //Hour
                Integer.parseInt(dateArray[4]));  //Minute
        return cal;
    }

    public static String calendarToDatabaseString(GregorianCalendar cal) {
        String year = Integer.toString(cal.get(Calendar.YEAR));
        String month = Integer.toString(cal.get(Calendar.MONTH));
        String date = Integer.toString(cal.get(Calendar.DATE));
        String hour = Integer.toString(cal.get(Calendar.HOUR_OF_DAY));
        String min = Integer.toString(cal.get(Calendar.MINUTE));
        return year + ";" + month + ";" + date + ";" + hour + ";" + min;
    }

    public static int[] databaseDayStringToArray(String dayString){
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
        return days;
    }

    public static String daysArrayToDatabaseString(int[] days){
        String dayString = "";
        for (int day : days) {
            dayString += day + ";";
        }
        return dayString;
    }

    public static int[] serverDayStringToDayArray(String dayString) {
        char[] splitString = dayString.toCharArray();
        int daysCount = 0;
        for (char c : splitString) {
            if (c == '1') daysCount++;
        }
        int[] returnArray = new int[daysCount];
        int currentIndex = 0;
        for (int i = 0; i <= splitString.length-1; i++) {
            if (splitString[i] == '1') returnArray[currentIndex++] = i;
        }
        return returnArray;
    }

    public static String dayArrayToServerDayString(int[] dayArray) {
        StringBuilder stringBuilder = new StringBuilder("0000000");
        for (int i : dayArray) {
            stringBuilder.replace(i,i+1,"1");
            System.out.println(stringBuilder.toString());
        }
        return stringBuilder.toString();
    }

    public static int[] selectedDaysToSelectedItems(int[] selectedDays) {
        int[] selectedItems = new int[selectedDays.length];
        // Convert to the expected format for SelectDaysDialogFragment
        for (int i = 0; i < selectedDays.length; i++) {
            switch (selectedDays[i]) {
                case 0:
                    selectedItems[i] = 6;
                    break;
                case 1:
                    selectedItems[i] = 0;
                    break;
                case 2:
                    selectedItems[i] = 1;
                    break;
                case 3:
                    selectedItems[i] = 2;
                    break;
                case 4:
                    selectedItems[i] = 3;
                    break;
                case 5:
                    selectedItems[i] = 4;
                    break;
                case 6:
                    selectedItems[i] = 5;
                    break;
            }
        }
        return selectedItems;
    }

    public static String daysArrayToSelectedDaysText(int[] reminder_days) {
        String[] days_abb = new String[]{"Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"};
        String[] reminder_days_abb = new String[reminder_days.length];
        String s = "";
        if (reminder_days.length >= 1) {
            for (int i = 0; i < reminder_days.length; i++) {
                reminder_days_abb[i] = days_abb[reminder_days[i]];
            }
            for (String day_abb : days_abb) {
                if (!day_abb.equals("Sa") && !day_abb.equals("Su")) {
                    if (Arrays.asList(reminder_days_abb).contains(day_abb)) {
                        s += "<b>" + day_abb + ", </b>";
                    } else {
                        s += "<font color=\"#c5c5c5\">" + day_abb + ", " + "</font>";
                    }
                }
            }
            if (Arrays.asList(reminder_days_abb).contains("Sa")) {
                s += "<b>Sa, </b>";
            } else {
                s += "<font color=\"#c5c5c5\">" + "Sa, " + "</font>";
            }
            if (Arrays.asList(reminder_days_abb).contains("Su")) {
                s += "<b>Su</b>";
            } else {
                s += "<font color=\"#c5c5c5\">" + "Su" + "</font>";
            }
        } else {
            s = "<font color=\"#c5c5c5\">" + "Mo, Tu, We, Th, Fr, Sa, Su" + "</font>";
            //s = "Mo, Tu, We, Th, Fr, Sa, Su";
        }
        return s;
    }

    public static String dayIndexToDayString(int i) {
        //TODO: Index out of bounds exception here
        String[] days = {"sun", "mon", "tue", "wed", "thu", "fri", "sat"};
        return days[i - 1];
    }

    public static String monthIndexToMonthString(int i){
        String[] months = {"jan", "feb", "mar", "apr", "may", "june", "jul", "aug", "sep", "oct", "nov", "des"};
        return months[i];
    }

    public static Boolean isSameDate (GregorianCalendar cal, GregorianCalendar cal2){
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int date = cal.get(Calendar.DATE);
        int year2 = cal2.get(Calendar.YEAR);
        int month2 = cal2.get(Calendar.MONTH);
        int date2 = cal2.get(Calendar.DATE);

        return year == year2 && month == month2 && date == date2;
    }





}
