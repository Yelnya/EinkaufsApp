package com.examples.pj.einkaufsapp.dbentities;

// DAS OBJEKT
// -> ShoppingMemo – Instanzen dieser Klasse können die Daten eines SQLite-Datensatzes aufnehmen. Sie repräsentieren die Datensätze im Code. Wir werden mit Objekten dieser Klasse den ListView füllen .
public class ShoppingMemo {

    private long id;
    private String product;
    private String category;
    private int bought;
    private boolean done;
    private boolean favourite;


    public ShoppingMemo(long id, String product, String category, int bought, boolean done, boolean favourite) {
        this.id = id;
        this.product = product;
        this.category = category;
        this.bought = bought;
        this.done = done;
        this.favourite = favourite;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getBought() {
        return bought;
    }

    public void setBought(int bought) {
        this.bought = bought;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    @Override
    public String toString() {
        return "ShoppingMemo{" +
                "id=" + id +
                ", product='" + product + '\'' +
                ", category='" + category + '\'' +
                ", bought=" + bought +
                ", done=" + done +
                ", favourite=" + favourite +
                '}';
    }

    public String toNiceString() {
        return product + " (" + category + ")";
    }
}
