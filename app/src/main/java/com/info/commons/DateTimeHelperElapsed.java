package com.info.commons;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTimeHelperElapsed {


    public static long ONE_DAY_TIME = 1000 * 60 * 60 * 24;

    private static long _getTodayMidnightTime() {
        return org.apache.commons.lang3.time.DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH).getTime();
    }

    public static boolean isToday(Date date) {
        long todayMidnight = _getTodayMidnightTime();
        return todayMidnight <= date.getTime() && date.getTime() < todayMidnight + ONE_DAY_TIME;
    }


    public static boolean isYesterday(Date date) {
        long todayMidnight = _getTodayMidnightTime();
        return todayMidnight - ONE_DAY_TIME <= date.getTime() && date.getTime() < todayMidnight;
    }


    public static boolean isInLastWeek(Date date) {
        long todayMidnight = _getTodayMidnightTime();
        return todayMidnight - ONE_DAY_TIME * 6 < date.getTime() && date.getTime() < todayMidnight;
    }


    public static String getDateOfWeek(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
            case Calendar.SUNDAY:
                return "Sunday";
            case Calendar.MONDAY:
                return "Monday";
            case Calendar.TUESDAY:
                return "Tuesday";
            case Calendar.WEDNESDAY:
                return "Wednesday";
            case Calendar.THURSDAY:
                return "Thursday";
            case Calendar.FRIDAY:
                return "Friday";
            case Calendar.SATURDAY:
                return "Saturday";

        }
        return "";
    }


    public static String toString(long milli, String timeFormat) {
        Date date = new Date(milli);
        SimpleDateFormat simpleDateFormat = getSimpleDateFormat(timeFormat);
        DateFormatSymbols symbols = new DateFormatSymbols(Locale.getDefault());
        symbols.setAmPmStrings(new String[]{"AM", "PM"});
        simpleDateFormat.setDateFormatSymbols(symbols);
        return simpleDateFormat.format(date);
    }

    public static String toStringNYTtime(long milli, String timeFormat) {
        Date date = new Date(milli);
        SimpleDateFormat simpleDateFormat = getSimpleDateFormat(timeFormat);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(Constants.NY_ZONE));

        String finalDate = simpleDateFormat.format(date).replace("am", "AM").replace("pm", "PM");
        return finalDate;
    }

    public static String convertTime(long time) { // DEC 01, 00:00 AM convert for long to string
        Date date = new Date(time);
        Format format = new SimpleDateFormat("MMM dd, hh:mm a");
        DateFormatSymbols symbols = new DateFormatSymbols(Locale.getDefault());
        symbols.setAmPmStrings(new String[]{"AM", "PM"});
        ((SimpleDateFormat) format).setDateFormatSymbols(symbols);
        return format.format(date);
    }
    public static String convertTimeTwo(long time) { // DEC 01, 00:00 AM convert for long to string
        Date date = new Date(time);
        Format format = new SimpleDateFormat(" yyyy MMM,hh:mm a");
        DateFormatSymbols symbols = new DateFormatSymbols(Locale.getDefault());
        symbols.setAmPmStrings(new String[]{"AM", "PM"});
        ((SimpleDateFormat) format).setDateFormatSymbols(symbols);
        return format.format(date);
    }

    public static long changeTimeToEstTime(String dateTime)
    {
        SimpleDateFormat sdfSource = getSimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        sdfSource.setTimeZone(TimeZone.getTimeZone(Constants.NYT));
        try {
            Date date = sdfSource.parse(dateTime);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    public static String changeDateFormat(String date) {
        String finalDate = null;
        try {
            //current date format
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            Date objDate = dateFormat.parse(date);

            //Expected date format
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("MMM dd, hh:mm a");
            String convertDate = dateFormat2.format(objDate);

            if (isToday(objDate)) {
                SimpleDateFormat dateFormat3 = new SimpleDateFormat("hh:mm a");
                finalDate = "Today " + dateFormat3.format(objDate);
                finalDate = finalDate.replace("am", "AM").replace("pm", "PM");
            } else if (isYesterday(objDate)) {
                SimpleDateFormat dateFormat3 = new SimpleDateFormat("hh:mm a");
                finalDate = "Yesterday " + dateFormat3.format(objDate);
                finalDate = finalDate.replace("am", "AM").replace("pm", "PM");
            } else {
                finalDate = convertDate.replace("am", "AM").replace("pm", "PM");
            }

            Log.d("Date Format:", "Final Date:" + finalDate);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return finalDate;
    }

    public static String changeDateWithCheckTodayYesterday(String date) {
        String finalDate = null;
        try {
            //current date format
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            dateFormat.setTimeZone(TimeZone.getTimeZone(Constants.NY_ZONE));
            Date objDate = dateFormat.parse(date);

            // Expected date format
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("MMM dd");
            String convertDate = dateFormat2.format(objDate);

            if (isToday(objDate)) {
                finalDate = "Today";
            } else if (isYesterday(objDate)) {
                finalDate = "Yesterday";
            } else {
                finalDate = convertDate;
            }

            Log.d("Date Format:", "Final Date:" + finalDate);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return finalDate;
    }

    public static String getElapsedTime(long from) {
        StringBuilder result = new StringBuilder("");

        Date date = new Date(from);//convertFromUTCToLocaltimezone(new Date(from));
        if (isToday(date)) {
            long elapsedMillis = System.currentTimeMillis() - date.getTime();
            long millisPerMinute = 1000 * 60;
            long millisPerHour = millisPerMinute * 60;
            long elapsedHours = elapsedMillis / millisPerHour;
            long elapsedMinutes = elapsedMillis / millisPerMinute;
            if (elapsedHours > 0)
                result.append(elapsedHours).append(" hr").append(elapsedHours > 1 ? "s" : "").append(" ago");
            else if (elapsedMinutes > 0)
                result.append(elapsedMinutes).append(" min").append(elapsedMinutes > 1 ? "s" : "").append(" ago");
            else
                result.append("Just now");
        } else if (isYesterday(date)) {
            result.append("Yesterday");
            result.append(" at ").append(getSimpleDateFormat("hh:mm a").format(date));
        } else if (isInLastWeek(date)) {
            result.append(getDateOfWeek(date));
            result.append(" at ").append(getSimpleDateFormat("hh:mm a").format(date));
        } else {
            result.append(getSimpleDateFormat("MMM dd, hh:mm a").format(date));
        }

        return result.toString();
    }

    public static String getElapsedTimeForNotification(long from) {
        StringBuilder result = new StringBuilder("");

        Date date = new Date(from);//convertFromUTCToLocaltimezone(new Date(from));
        if (isToday(date)) {
            result.append("Today");
            result.append(", ").append(getSimpleDateFormat("hh:mm a").format(date));
        } else if (isYesterday(date)) {
            result.append("Yesterday");
            result.append(", ").append(getSimpleDateFormat("hh:mm a").format(date));
        } else {
            result.append(getSimpleDateFormat("MMM dd, hh:mm a").format(date));
        }

        return result.toString();
    }

    public static String formatDate(String strDate) {           //Nov 01, 2019 07:00 PM EST-0500
        SimpleDateFormat sdfSource = getSimpleDateFormat("MMM dd, yyyy hh:mm a Zz");
        //parse the string into Date object
        Date date = null;
        try {
            date = sdfSource.parse(strDate);
            SimpleDateFormat sdfDestination = getSimpleDateFormat("MMM dd, yyyy hh:mm a Zz");
            return sdfDestination.format(date);
        } catch (ParseException e) {
            Log.e("DateParseException", e.toString() + " --- " + strDate);
        }
        return strDate;
    }

    public static String formatDateForAI(String s) {           //Nov 01, 2019 07:00 PM EST-0500
        // Remove the last 9 digit of a string -> EST-0500.
        String strDate = s.substring(0, s.length() - 9);

        // Not convert new string to desired format date.
        SimpleDateFormat sdfSource = getSimpleDateFormat("MMM dd, yyyy hh:mm a");
        //parse the string into Date object
        Date date = null;
        try {
            date = sdfSource.parse(strDate);
            SimpleDateFormat sdfDestination = getSimpleDateFormat("MMM dd, hh:mm a");
            String convertedDate = sdfDestination.format(date).replace("am", "AM").replace("pm", "PM");
            return convertedDate;
        } catch (ParseException e) {
            Log.e("DateParseException", e.toString() + " --- " + strDate);
        }
        return strDate;
    }

    public static Long getPublishDate(String publishDate) {
        SimpleDateFormat sdfSource = getSimpleDateFormat("MMM dd, yyyy hh:mm a Zz");
        try {
            Date date = sdfSource.parse(publishDate);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    public static Long getTimeInMiliSeconds(String publishDate) {
        SimpleDateFormat sdfSource = getSimpleDateFormatInUTC("yyyy-MM-dd HH:mm:ss.SSSSSS");
        try {
            Date date = sdfSource.parse(publishDate);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    public static long formatDateLong(String strDate) {           //Nov 01, 2019 07:00 PM EST-0500
        SimpleDateFormat sdfSource = getSimpleDateFormat("MMM dd, yyyy hh:mm a Zz");
        //parse the string into Date object
        Date date = null;
        try {
            date = sdfSource.parse(strDate);
            SimpleDateFormat sdfDestination = getSimpleDateFormat("MMM dd, yyyy hh:mm a Zz");
            return date.getTime();
        } catch (ParseException e) {
            Log.e("DateParseException", e.toString() + "---" + strDate);
        }
        return 0;
    }

    public static long getNewYorkCurrentTimeInMiliSec() {
        TimeZone tz = TimeZone.getTimeZone("GMT+05:30");
        Calendar c = Calendar.getInstance(tz);
        return c.getTimeInMillis();
    }

    public static SimpleDateFormat getSimpleDateFormat(String dateFormat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        TimeZone timeZone = TimeZone.getTimeZone(Constants.NY_ZONE);
        simpleDateFormat.setTimeZone(timeZone);
        return simpleDateFormat;
    }

    public static SimpleDateFormat getSimpleDateFormatInUTC(String dateFormat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        simpleDateFormat.setTimeZone(timeZone);
        return simpleDateFormat;
    }

    public String firstTwo(String str) {
        return str.length() < 2 ? str : str.substring(0, 2);
    }
}
