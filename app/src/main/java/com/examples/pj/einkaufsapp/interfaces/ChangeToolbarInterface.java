package com.examples.pj.einkaufsapp.interfaces;

import com.examples.pj.einkaufsapp.dbentities.ProductItem;

import java.util.List;

/**
 * Interface is listening if toolbar should be changed
 */
public interface ChangeToolbarInterface {

    /**
     * Boolean method if edit and delete icon should be shown
     */
    void showEditAndDeleteIcon(boolean show);

    void showShoppingCartIcon(boolean show);

    void handOverProductsToAddToCurrentShoppingList(List<ProductItem> productsToAddToCurrentShoppingList);
}
