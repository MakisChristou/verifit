package com.example.verifit;


import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.HashSet;
import java.util.List;

public class MonthXAxisFormatter extends ValueFormatter {
    private List<String> months;
    private HashSet<String> uniqueMonths;

    public MonthXAxisFormatter(List<String> months) {
        this.months = months;
        this.uniqueMonths = new HashSet<>();
    }

    @Override
    public String getFormattedValue(float value) {
        int index = (int) value;
        if (index < 0 || index >= months.size()) {
            return "";
        }

        String month = months.get(index);

//        if (uniqueMonths.contains(month)) {
//            return "";
//        }

        uniqueMonths.add(month);
        return month;
    }
}