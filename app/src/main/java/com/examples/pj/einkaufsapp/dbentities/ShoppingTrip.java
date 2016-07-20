package com.examples.pj.einkaufsapp.dbentities;

import java.util.Date;
import java.util.List;

public class ShoppingTrip {

    private Date dateCompleted;
    private List<ProductItem> boughtProducts;

    public ShoppingTrip(Date dateCompleted, List<ProductItem> boughtProducts) {
        this.dateCompleted = dateCompleted;
        this.boughtProducts = boughtProducts;
    }

    public Date getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(Date dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

    public List<ProductItem> getBoughtProducts() {
        return boughtProducts;
    }

    public void setBoughtProducts(List<ProductItem> boughtProducts) {
        this.boughtProducts = boughtProducts;
    }

    @Override
    public String toString() {
        return "ShoppingTrip{" +
                "dateCompleted=" + dateCompleted +
                ", boughtProducts=" + boughtProducts +
                '}';
    }
}
