package com.info.commons;

import android.util.Log;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;
import java.util.Arrays;

public class ClaimsXAxisValueFormatter extends ValueFormatter {

    String[] datesList;

    public ClaimsXAxisValueFormatter(String[] arrayOfDates) {
       this.datesList = arrayOfDates;
    }


    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        Integer position = Math.round(value);

        if (position!=0&&position < datesList.length)
            return DateTimeHelper.parseDateFloat(DateTimeHelper.parseDate(datesList[position]).getTime());
        return "";
    }
}