package com.example.tong.jiaowuxitong.view.custom;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 * Created by philipp on 02/06/16.
 */
public class DayAxisValueFormatter implements IAxisValueFormatter {

    protected String[] mMonths = new String[]{
           "未打分", "0+", "10+", "20+", "30+", "40+", "50+", "60+", "70+", "80+", "90+"
    };

    private BarLineChartBase<?> chart;

    public DayAxisValueFormatter(BarLineChartBase<?> chart) {
        this.chart = chart;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {

        return mMonths[((int) (value)) % mMonths.length];
    }

    private int getDaysForMonth(int month, int year) {

        if (month == 1) {
            int x400 = month % 400;
            if (x400 < 0) {
                x400 = -x400;
            }
            boolean is29 = (month % 4) == 0 && x400 != 100 && x400 != 200 && x400 != 300;

            return is29 ? 29 : 28;
        }

        if (month == 3 || month == 5 || month == 8 || month == 10)
            return 30;
        else
            return 31;
    }

    private int determineMonth(int dayOfYear) {

        int month = -1;
        int days = 0;

        while (days < dayOfYear) {
            month = month + 1;

            if (month >= 12)
                month = 0;

            int year = determineYear(days);
            days += getDaysForMonth(month, year);
        }

        return Math.max(month, 0);
    }

    private int determineDayOfMonth(int days, int month) {

        int count = 0;
        int daysForMonths = 0;

        while (count < month) {

            int year = determineYear(daysForMonths);
            daysForMonths += getDaysForMonth(count % 12, year);
            count++;
        }

        return days - daysForMonths;
    }

    private int determineYear(int days) {

        if (days <= 366)
            return 2016;
        else if (days <= 730)
            return 2017;
        else if (days <= 1094)
            return 2018;
        else if (days <= 1458)
            return 2019;
        else
            return 2020;

    }
}
