package com.examples.pj.einkaufsapp.formatters;

import com.examples.pj.einkaufsapp.dbentities.ProductItem;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.List;

public class MyValueFormatter implements ValueFormatter {

    private DecimalFormat mFormat;
    private List<ProductItem> productItemList;

    public MyValueFormatter() {
        mFormat = new DecimalFormat("###,###,##0.0"); // use one decimal
    }

    public MyValueFormatter(List<ProductItem> productItemList) {
        mFormat = new DecimalFormat("###,###,##0"); // use no decimal
        this.productItemList = productItemList;
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {

        int entryInt = Math.round(entry.getX());
        int i = 1;
        ProductItem current = null;

        for (ProductItem productItem : productItemList) {
            if (entryInt/10 == i) {
                current = productItem;
            }
            i++;
        }
        return current.getProduct() + " (" + current.getBought() + ")";
    }
}
