package com.examples.pj.einkaufsapp;


import java.util.ArrayList;

public class HeaderInfo {

    private String name;
    private ArrayList<ChildInfo> productList = new ArrayList<ChildInfo>();;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public ArrayList<ChildInfo> getProductList() {
        return productList;
    }
    public void setProductList(ArrayList<ChildInfo> productList) {
        this.productList = productList;
    }

}
