package com.info.commons;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.util.Log;

public class DateTimeHelper {

    private static final SimpleDateFormat TIME_FORMATTER_RESPONSE =
            new SimpleDateFormat("HH:mm");

    private static final SimpleDateFormat TIME_FORMATTER_VIEW =
            new SimpleDateFormat("hh:mm aa");

    private static final SimpleDateFormat DATE_FORMATTER_VIEW =
            new SimpleDateFormat("MM/dd");

    private static final SimpleDateFormat MONTH_DAY_YEAR_FORMAT =
            new SimpleDateFormat("MMMM dd, yyyy");

    private static final String TAG = "DateTime";

    public static Date parseTimeResponse(String time) {
        try {
            return TIME_FORMATTER_RESPONSE.parse(time);
        } catch (ParseException e) {
            Log.d(TAG, "parseTime() caught ParseException", e);
            e.printStackTrace();
            return null;
        }
    }

    public static String formatTime(Date date) {
        return MONTH_DAY_YEAR_FORMAT.format(date);
    }

    public static Date parseDate(String date) {
        try {
            return DateTimeHelperElapsed.getSimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (ParseException e) {
            Log.d(TAG, "parseDate() caught ParseException", e);
            e.printStackTrace();
            return null;
        }
    }

    public static boolean between(Date date, Date dateStart, Date dateEnd) {
        if (date != null && dateStart != null && dateEnd != null) {

            if ((date.before(dateStart) && date.after(dateEnd)) || date.toString().equals(dateEnd.toString()) || date.toString().equals(dateStart.toString())) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }


    public static Calendar workingDaysBack(final Calendar from, final int count) {
        for (int daysBack = 0; daysBack < count; ++daysBack) {
            do {
                from.add(Calendar.DAY_OF_YEAR, -1);
            } while (isWeekend(from));
        }
        return from;
    }

    public static boolean isWeekend() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(Constants.NY_ZONE));
        return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
    }

    public static boolean isMarketHours() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(Constants.NY_ZONE));
        Log.e("hours_of_day",calendar.get(Calendar.HOUR_OF_DAY)+"");
        return calendar.get(Calendar.HOUR_OF_DAY) <16;
    }

    public static boolean isWeekend(Calendar cal) {
        return cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
    }

    public static String parseTimeFloat(float value) {
        Date date = new Date(Math.round(value));
        String finalDate = TIME_FORMATTER_VIEW.format(date).replace("am", "AM").replace("pm", "PM");
        return finalDate;
    }

    public static String parseDateFloat(float value) {
        Date date = new Date((long) value);
        return DATE_FORMATTER_VIEW.format(date);
    }


    public static String getNYTime() {
        Calendar calNewYork = Calendar.getInstance();
        SimpleDateFormat dateFormat = DateTimeHelperElapsed.getSimpleDateFormat("hh:mm a");
        String nyTime = dateFormat.format(calNewYork.getTime());
        nyTime = nyTime.replace("am", "AM").replace("pm", "PM");
        return nyTime;
    }

}
