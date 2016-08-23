package com.examples.pj.einkaufsapp.dbentities;

import com.examples.pj.einkaufsapp.util.DateUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

public class ShoppingTrip {

    private String dateCompleted;
    private String boughtProductsJsonList;
    private List<ProductItem> boughtProductsList;
    private boolean isExpanded;

    public ShoppingTrip(String dateCompleted, String boughtProductsJsonList) {
        this.dateCompleted = dateCompleted;
        this.boughtProductsJsonList = boughtProductsJsonList;

        setBoughtProductsList(convertJsonToList(boughtProductsJsonList));
    }

    public String getDateCompleted() {
        return dateCompleted;
    }

    public String getNiceDateCompleted() {
        return DateUtils.stringDateToHourMinuteString(dateCompleted);
    }

    public String getNiceDateCompletedENG() {
        return DateUtils.stringDateToHourMinuteStringENG(dateCompleted);
    }

    public void setDateCompleted(String dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

    public String getBoughtProductsJsonList() {
        return boughtProductsJsonList;
    }

    public void setBoughtProductsJsonList(String boughtProductsJsonList) {
        this.boughtProductsJsonList = boughtProductsJsonList;
    }

    public List<ProductItem> getBoughtProductsList() {
        this.boughtProductsList = convertJsonToList(getBoughtProductsJsonList());
        return boughtProductsList;
    }

    public void setBoughtProductsList(List<ProductItem> boughtProductsList) {
        setBoughtProductsJsonList(convertListToJson(boughtProductsList));
        this.boughtProductsList = boughtProductsList;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    @Override
    public String toString() {
        return "ShoppingTrip{" +
                "dateCompleted='" + dateCompleted + '\'' +
                ", boughtProductsJsonList='" + boughtProductsJsonList + '\'' +
                ", boughtProductsList=" + boughtProductsList +
                ", isExpanded=" + isExpanded +
                '}';
    }

    //---------------------------------------------------------------
    // OTHER METHODS
    //---------------------------------------------------------------

    private List<ProductItem> convertJsonToList(String jsonString) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<ProductItem>>() {
        }.getType();
        return gson.fromJson(jsonString, type);
    }

    private String convertListToJson(List<ProductItem> list) {
        Gson gson = new Gson();
        return gson.toJson(list, LinkedList.class);
    }
}
