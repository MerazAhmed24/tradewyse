package com.info.sendbird.utils;

import com.info.commons.DateTimeHelperElapsed;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A class with static util methods.
 */

public class DateUtils {

    // This class should not be initialized
    private DateUtils() {

    }


    /**
     * Gets timestamp in millis and converts it to HH:mm (e.g. 16:44).
     */
    public static String formatTime(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return dateFormat.format(timeInMillis);
    }

    public static String formatTimeWithMarker(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM D, hh:mm a", Locale.getDefault());
        return dateFormat.format(timeInMillis);
    }

    public static int getHourOfDay(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("H", Locale.getDefault());
        return Integer.valueOf(dateFormat.format(timeInMillis));
    }

    public static int getMinute(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("m", Locale.getDefault());
        return Integer.valueOf(dateFormat.format(timeInMillis));
    }

    /**
     * If the given time is of a different date, display the date.
     * If it is of the same date, display the time.
     *
     * @param timeInMillis The time to convert, in milliseconds.
     * @return The time or date.
     */
    public static String formatDateTime(long timeInMillis) {
        if (isToday(timeInMillis)) {
            return formatTime(timeInMillis);
        } else {
            return formatDate(timeInMillis);
        }
    }

    public static String getChatMessageFormatDate(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy 'at' h:mm:ss a zzz", Locale.getDefault());
        return dateFormat.format(timeInMillis);
    }

    public static String getChatDateForDateChange(long dateValue) {
        DateFormat originalFormat = new SimpleDateFormat("MMM dd, yyyy 'at' h:mm:ss a zzz", Locale.getDefault());
        DateFormat targetFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        String formattedDate = "";
         /*try {
           Date date = originalFormat.parse(dateValue);
            formattedDate = targetFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }*/

        formattedDate = targetFormat.format(dateValue);

        return formattedDate;
    }
    public static String convertTime(long time) { //00:00 AM convert for long to string
        Date date = new Date(time);
        Format format = new SimpleDateFormat("hh:mm a");
        DateFormatSymbols symbols = new DateFormatSymbols(Locale.getDefault());
        symbols.setAmPmStrings(new String[]{"AM", "PM"});
        ((SimpleDateFormat) format).setDateFormatSymbols(symbols);
        return format.format(date);
    }

    public static String getChatTimeForMessage(long dateValue) {
        DateFormat originalFormat = new SimpleDateFormat("MMM dd, yyyy 'at' h:mm:ss a zzz", Locale.getDefault());
        DateFormat targetFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        String formattedDate = "";
        /*try {
            Date date = originalFormat.parse(dateValue);
            formattedDate = targetFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
        formattedDate = targetFormat.format(dateValue);
        return formattedDate;
    }

    public static String getChatReplyTimeForMessage(long dateValue) {
        DateFormat originalFormat = new SimpleDateFormat("MMM dd, yyyy 'at' h:mm:ss a zzz", Locale.getDefault());
        DateFormat targetFormat = new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault());
        String formattedDate = "";
        /*try {
            Date date = originalFormat.parse(dateValue);
            formattedDate = targetFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
        formattedDate = targetFormat.format(dateValue);
        return formattedDate;
    }

    public static long getChatMessageFormatDateToMiliseconds(String formatedDate) {
        long timeInMilliseconds = -1;
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy 'at' h:mm:ss a zzz", Locale.getDefault());
        try {
            Date mDate = sdf.parse(formatedDate);
            timeInMilliseconds = mDate.getTime();
            System.out.println("Date in milli :: " + timeInMilliseconds);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeInMilliseconds;
    }

    /**
     * Formats timestamp to 'date month' format (e.g. 'February 3').
     */
    public static String formatDate(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd", Locale.getDefault());
        return dateFormat.format(timeInMillis);
    }

    /**
     * Returns whether the given date is today, based on the user's current locale.
     */
    public static boolean isToday(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String date = dateFormat.format(timeInMillis);
        return date.equals(dateFormat.format(System.currentTimeMillis()));
    }

    /**
     * Checks if two dates are of the same day.
     *
     * @param millisFirst  The time in milliseconds of the first date.
     * @param millisSecond The time in milliseconds of the second date.
     * @return Whether {@param millisFirst} and {@param millisSecond} are off the same day.
     */
    public static boolean hasSameDate(long millisFirst, long millisSecond) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return dateFormat.format(millisFirst).equals(dateFormat.format(millisSecond));
    }
    public static boolean isYesterday(long timeInMillis) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            String date = dateFormat.format(timeInMillis);
            Date dateYesterday = dateFormat.parse(date);
            return DateTimeHelperElapsed.isYesterday(dateYesterday);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}
