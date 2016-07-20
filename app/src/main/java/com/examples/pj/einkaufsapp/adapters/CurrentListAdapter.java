package com.examples.pj.einkaufsapp.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.examples.pj.einkaufsapp.R;
import com.examples.pj.einkaufsapp.dbentities.ProductItem;
import com.examples.pj.einkaufsapp.dbentities.ProductItemDataSource;
import com.examples.pj.einkaufsapp.interfaces.ChangeToolbarInterface;
import com.examples.pj.einkaufsapp.util.SharedPreferencesManager;
import com.examples.pj.einkaufsapp.util.ViewUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * Adapter for CurrentListFragment
 */
public class CurrentListAdapter extends BaseAdapter<CurrentListAdapter.ArraylistViewHolder> {

    public static final String LOG_TAG = CurrentListAdapter.class.getSimpleName();

    private static final int LIST_ITEMS = 0;
    private static final int BOTTOM_ELEMENT = 1;

    private final Context context;
    Activity contextActivity;
    private final ProductItemDataSource dataSource;
    private final SharedPreferencesManager sharedPreferencesManager;
    private final ChangeToolbarInterface changeToolbarInterface;
    private boolean editDeleteToolbarActive;
    private List<ProductItem> currentList;
    private ProductItem itemClicked;

    /**
     * Constructor
     *
     * @param context                  from MainActivity
     * @param changeToolbarInterface   for Edit / Delete Icons to show or not
     * @param dataSource               db
     * @param editDeleteToolbarActive  boolean -> edit / delete icons currently shown or not
     * @param currentList              currentList from Fragment
     * @param sharedPreferencesManager to store data locally
     */
    public CurrentListAdapter(List<ProductItem> currentList, Context context, ProductItemDataSource dataSource, SharedPreferencesManager sharedPreferencesManager, ChangeToolbarInterface changeToolbarInterface, boolean editDeleteToolbarActive) {
        this.currentList = currentList;
        this.context = context;
        this.dataSource = dataSource;
        this.sharedPreferencesManager = sharedPreferencesManager;
        this.changeToolbarInterface = changeToolbarInterface;
        this.editDeleteToolbarActive = editDeleteToolbarActive;

        contextActivity = (Activity) context;
    }

    //---------------------------------------------------------------
    // Create new views (invoked by the layout manager)
    //---------------------------------------------------------------
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == LIST_ITEMS) {
            return new ArraylistViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item_currentlist, parent, false));
        } else {
            return new BottomElementViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_segment_currentlistadapter_bottom, parent, false));
        }
    }

    //---------------------------------------------------------------
    // Replace the contents of a view (invoked by the layout manager)
    //---------------------------------------------------------------
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);

        if (currentList != null) {
            currentList.clear();
            currentList = sharedPreferencesManager.loadCurrentShoppingListFromLocalStore();
        } else {
            currentList = new ArrayList<>();
        }

        if (viewType == LIST_ITEMS) {
            ArraylistViewHolder viewHolder = (ArraylistViewHolder) holder;
            ProductItem item = currentList.get(position);
            getIconMatchingCategory(item, viewHolder.categoryIconIv, viewHolder.itemContainerLl);
            viewHolder.productNameTv.setText(item.getProduct());
            // Hier prüfen, ob Eintrag abgehakt ist. Falls ja, Text durchstreichen
            if (item.isDone()) {
                viewHolder.productNameTv.setPaintFlags(viewHolder.productNameTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                viewHolder.productNameTv.setTextColor(Color.rgb(175, 175, 175));
            } else {
                viewHolder.productNameTv.setPaintFlags(viewHolder.productNameTv.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                viewHolder.productNameTv.setTextColor(Color.DKGRAY);
            }
        } else {
            BottomElementViewHolder viewHolder = (BottomElementViewHolder) holder;

            if (currentList != null) {
                if (!currentList.isEmpty()) {
                    viewHolder.noProductInListTv.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.noProductInListTv.setVisibility(View.GONE);
                }
            } else {
                viewHolder.noProductInListTv.setVisibility(View.GONE);
            }
        }
    }

    //---------------------------------------------------------------
    // ADAPTER METHODS
    //---------------------------------------------------------------

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (currentList != null) {
            return currentList.size() + 1; //plus one for bottom element
        } else {
            return 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        int viewType;
        if (currentList != null) {
            if (position < currentList.size()) {
                viewType = LIST_ITEMS; // as long as it is an array list
            } else {
                viewType = BOTTOM_ELEMENT; //if it is no array list anymore
            }
        } else {
            viewType = BOTTOM_ELEMENT;
        }
        return viewType;
    }

    //---------------------------------------------------------------
    // OTHER METHODS
    //---------------------------------------------------------------

    /**
     * method to sort list alphabetically
     */
    public void sortListCategoryAndAlphabetical() {
        Collections.sort(currentList, new CurrentListAlphabeticalComparator());
        Collections.sort(currentList, new CurrentListCategoryComparator());

        Log.d(LOG_TAG, "----------------- SORTED LIST ------------------");
        for (ProductItem product : currentList) {
            Log.d(LOG_TAG, "Sorted: " + product.toNiceString());
        }
    }

    /**
     * method to toggle the "done" status for productItem
     *
     * @param item productItem
     */

    public void changeProductChecked(ProductItem item) {
        ProductItem updatedItem = dataSource.updateProductItem(item.getId(), item.getProduct(), item.getCategory(), item.getBought(), !item.isDone(), item.isFavourite());
        currentList.remove(item);
        currentList.add(updatedItem);
        sortListCategoryAndAlphabetical();
        sharedPreferencesManager.saveCurrentShoppingListToLocalStore(currentList);
        Log.d(LOG_TAG, "-----------------------Geänderter Eintrag:-------------------------------");
        Log.d(LOG_TAG, "Eintrag: " + updatedItem.toString());
        notifyDataSetChanged(); //refresh List View
    }

    /**
     * method for mapping icon to category of product
     *
     * @param item productItem
     * @param iv   ImageView Icon to change
     * @param ll   LinearLayout Background Color to change
     */
    public void getIconMatchingCategory(ProductItem item, ImageView iv, LinearLayout ll) {
        String[] stringArray = context.getResources().getStringArray(R.array.categories_array);

        if (item.getCategory().equals(stringArray[0])) {    //Fisch
            iv.setImageResource(R.drawable.fish);
            ll.setBackgroundColor(ContextCompat.getColor(context, R.color.fish));
        } else if (item.getCategory().equals(stringArray[1])) { //Fleisch und Wurst
            iv.setImageResource(R.drawable.year_of_the_pig);
            ll.setBackgroundColor(ContextCompat.getColor(context, R.color.year_of_the_pig));
        } else if (item.getCategory().equals(stringArray[2])) {  //Getränke
            iv.setImageResource(R.drawable.soda_bottle);
            ll.setBackgroundColor(ContextCompat.getColor(context, R.color.soda_bottle));
        } else if (item.getCategory().equals(stringArray[3])) {  //Gewürze und Saucen
            iv.setImageResource(R.drawable.natural_food);
            ll.setBackgroundColor(ContextCompat.getColor(context, R.color.natural_food));
        } else if (item.getCategory().equals(stringArray[4])) {  //Grundnahrungsmittel
            iv.setImageResource(R.drawable.bread);
            ll.setBackgroundColor(ContextCompat.getColor(context, R.color.bread));
        } else if (item.getCategory().equals(stringArray[5])) {  //Konserven
            iv.setImageResource(R.drawable.tin_can);
            ll.setBackgroundColor(ContextCompat.getColor(context, R.color.tin_can));
        } else if (item.getCategory().equals(stringArray[6])) {  //Milchprodukte
            iv.setImageResource(R.drawable.cheese);
            ll.setBackgroundColor(ContextCompat.getColor(context, R.color.cheese));
        } else if (item.getCategory().equals(stringArray[7])) {  //Obst und Gemüse
            iv.setImageResource(R.drawable.strawberry);
            ll.setBackgroundColor(ContextCompat.getColor(context, R.color.strawberry));
        } else if (item.getCategory().equals(stringArray[8])) {  //Tiefkühlwaren
            iv.setImageResource(R.drawable.winter);
            ll.setBackgroundColor(ContextCompat.getColor(context, R.color.winter));
        } else {                                                   //Sonstiges
            iv.setImageResource(R.drawable.foundation);
            ll.setBackgroundColor(ContextCompat.getColor(context, R.color.foundation));
        }
    }

    //---------------------------------------------------------------
    // INNER CLASSES
    //---------------------------------------------------------------

    /**
     * ArrayListViewHolder
     */
    public class ArraylistViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.item_container)
        LinearLayout itemContainerLl;
        @Bind(R.id.category_icon)
        ImageView categoryIconIv;
        @Bind(R.id.product_name)
        TextView productNameTv;

        /**
         * Constructor
         *
         * @param view
         */
        public ArraylistViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        /**
         * Simple Click Behaviour of Recycler View Item
         */
        @OnClick(R.id.item_container)
        public void onProductClick() {
            int pos = getAdapterPosition();
            ProductItem product = currentList.get(pos);
            changeProductChecked(product);
        }

        /**
         * Long Click Behaviour of Recycler View Item
         *
         * @return needed by OnLongClick
         */
        @OnLongClick(R.id.item_container)
        public boolean onProductLongClick() {

            int pos = getAdapterPosition();
            ProductItem selectedItem = currentList.get(pos);
            editDeleteToolbarActive = !editDeleteToolbarActive;   //toggle if EditDelete Toolbar is active or not
            changeToolbarInterface.showEditAndDeleteIcon(editDeleteToolbarActive); //only show EditDelete Toolbar if it is not active right now. Otherwise change back to normal Toolbar

            setItemClicked(selectedItem);

            return true;
        }
    }

    /**
     * ViewHolder for TextView and Button below RecyclerView
     */
    public class BottomElementViewHolder extends BaseViewHolder {

        @Bind(R.id.currentlist_noproductinlist_tv)
        TextView noProductInListTv;

        /**
         * Constructor
         *
         * @param view
         */
        public BottomElementViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        /**
         * Click Behaviour of "Einkauf abschliessen" Button
         */
        @OnClick(R.id.currentlist_Btn)
        public void onFinishButtonClick() {

            makeFinishAlertDialog();
        }
    }

    //---------------------------------------------------------------
    // COMPARATORS FOR LIST SORTING
    //---------------------------------------------------------------

    /**
     * Helper Class for Sorting List alphabetically
     */
    public class CurrentListAlphabeticalComparator implements Comparator<ProductItem> {
        @Override
        public int compare(ProductItem left, ProductItem right) {
            return left.getProduct().compareTo(right.getProduct());
        }
    }

    /**
     * Helper Class for Sorting List referring to Categories
     */
    public class CurrentListCategoryComparator implements Comparator<ProductItem> {
        @Override
        public int compare(ProductItem left, ProductItem right) {
            return left.getCategory().compareTo(right.getCategory());
        }
    }

    //---------------------------------------------------------------
    // GETTER AND SETTER
    //---------------------------------------------------------------

    public ProductItem getItemClicked() {
        return itemClicked;
    }

    public void setItemClicked(ProductItem itemClicked) {
        this.itemClicked = itemClicked;
    }

    public void setEditDeleteToolbarActive(boolean editDeleteToolbarActive) {
        this.editDeleteToolbarActive = editDeleteToolbarActive;
    }


    private void makeFinishAlertDialog() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        // set title and message
        alertDialogBuilder.setTitle("Einkauf abschliessen?")
                .setMessage("Die als erledigt markierten Produkte werden aus der Liste entfernt und in der Einkaufs-Historie abgespeichert. Die unerledigten Produkte verbleiben in der Liste.");
        //set yes no fields
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_button_finish_shoppingtrip, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        //make new ProductItem List "doneProductItemList" -> get all as done selected items from currentList
                        List<ProductItem> doneProductItemList = new ArrayList<>();
                        for (ProductItem product : currentList) {
                            if (product.isDone()) {
                                doneProductItemList.add(product);
                            }
                        }
                        //TODO error handling if there are no as "done" marked items
                        if (!doneProductItemList.isEmpty()) {
                            //TODO sort "doneProductItemList" list
                        }


                        //TODO store sorted "doneProductItemList" SP

                        //TODO delete all as done selected items from current list

                        //TODO sort current List

                        //TODO error handling if current List now is empty

                        //TODO store current list without done to SP

                        //TODO create Shopping Trip object, add current date

                        //TODO create Shopping Trip list, get all current entries from SP, write to list

                        //TODO add current Shopping Trip object to this list

                        //TODO write finished Shopping Trip List to SP

                        //TODO Toast "Einkauf abgeschlossen"

                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.dialog_button_negative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        ViewUtils.hideKeyboard((Activity) context);
    }


}
