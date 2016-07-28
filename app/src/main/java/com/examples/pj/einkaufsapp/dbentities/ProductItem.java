package com.examples.pj.einkaufsapp.dbentities;

/**
 * Object class for ProductItem -> Entity
 */
public class ProductItem {

    private long id;
    private String product;
    private String category;
    private int bought;
    private boolean done;
    private boolean favourite;
    private boolean currentClicked;

    /**
     * Constructor
     *
     * @param id
     * @param product
     * @param category
     * @param bought
     * @param done
     * @param favourite
     */
    public ProductItem(long id, String product, String category, int bought, boolean done, boolean favourite, boolean currentClicked) {
        this.id = id;
        this.product = product;
        this.category = category;
        this.bought = bought;
        this.done = done;
        this.favourite = favourite;
        this.currentClicked = currentClicked;
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

    public boolean isCurrentClicked() {
        return currentClicked;
    }

    public void setCurrentClicked(boolean currentClicked) {
        this.currentClicked = currentClicked;
    }

    @Override
    public String toString() {
        return "ProductItem{" +
                "id=" + id +
                ", product='" + product + '\'' +
                ", category='" + category + '\'' +
                ", bought=" + bought +
                ", done=" + done +
                ", favourite=" + favourite +
                ", currentClicked=" + currentClicked +
                '}';
    }

    /**
     * returns Information of productItem in nicely readable format
     *
     * @return String
     */
    public String toNiceString() {
        return product + " (gekauft: " + bought + "x)";
    }
}
