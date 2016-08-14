package com.examples.pj.einkaufsapp.formatters;

import com.examples.pj.einkaufsapp.dbentities.ProductItem;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.List;

public class MyValueFormatter implements ValueFormatter {

    private DecimalFormat mFormat;
    List<ProductItem> productItemsList;

    public MyValueFormatter() {
        mFormat = new DecimalFormat("###,###,##0.0"); // use one decimal
    }

    public MyValueFormatter(List<ProductItem> productItemsList) {
        mFormat = new DecimalFormat("###,###,##0"); // use no decimal
        this.productItemsList = productItemsList;
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        Integer x = Math.round(entry.getX()) / 10 ;  //Position ob Object

        ProductItem currentProductItem = productItemsList.get(x-1);
        return currentProductItem.getProduct() + "(" + currentProductItem.getBought() + ")";
    }
}
