package com.examples.pj.einkaufsapp.dbentities;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ShoppingTrip {

    private String dateCompleted;
    private String boughtProductsJsonList;
    private List<ProductItem> boughtProducts;

    public ShoppingTrip(String dateCompleted, String boughtProductsJsonList) {
        this.dateCompleted = dateCompleted;
        this.boughtProductsJsonList = boughtProductsJsonList;
    }

    public String getDateCompleted() {
        return dateCompleted;
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

    public List<ProductItem> getBoughtProducts() {

        Gson gson = new Gson();
        Type type = new TypeToken<List<ProductItem>>() {
        }.getType();

        return gson.fromJson(boughtProductsJsonList, type);
    }

    public void setBoughtProducts(List<ProductItem> boughtProducts) {
        this.boughtProducts = boughtProducts;
    }

    @Override
    public String toString() {
        return "ShoppingTrip{" +
                "dateCompleted='" + dateCompleted + '\'' +
                ", boughtProductsJsonList='" + boughtProductsJsonList + '\'' +
                '}';
    }
}