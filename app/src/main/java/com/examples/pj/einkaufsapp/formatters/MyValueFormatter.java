package com.examples.pj.einkaufsapp.formatters;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

public class MyValueFormatter implements ValueFormatter {

    private DecimalFormat mFormat;
    private String label;

    public MyValueFormatter() {
        mFormat = new DecimalFormat("###,###,##0.0"); // use one decimal
    }

    public MyValueFormatter(String label) {
        mFormat = new DecimalFormat("###,###,##0"); // use no decimal
        this.label = label;
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return label + " (" + mFormat.format(value) + ")";
    }
}
