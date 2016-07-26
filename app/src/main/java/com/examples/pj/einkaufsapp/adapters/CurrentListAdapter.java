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
import android.widget.Toast;

import com.examples.pj.einkaufsapp.R;
import com.examples.pj.einkaufsapp.dbentities.ProductItem;
import com.examples.pj.einkaufsapp.dbentities.ProductItemDataSource;
import com.examples.pj.einkaufsapp.dbentities.ShoppingTrip;
import com.examples.pj.einkaufsapp.interfaces.ChangeToolbarInterface;
import com.examples.pj.einkaufsapp.util.DateUtils;
import com.examples.pj.einkaufsapp.util.SharedPreferencesManager;
import com.examples.pj.einkaufsapp.util.StringUtils;
import com.examples.pj.einkaufsapp.util.ViewUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
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

            if (currentList == null || currentList.isEmpty()) {
                viewHolder.noProductInListTv.setVisibility(View.VISIBLE);
                viewHolder.howToUseTv.setVisibility(View.GONE);
            } else {
                viewHolder.noProductInListTv.setVisibility(View.GONE);
                viewHolder.howToUseTv.setVisibility(View.VISIBLE);
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
     * method to sort list alphabetically and referring to category
     *
     * @param listToSort list
     * @return list
     */
    public List<ProductItem> sortListCategoryAndAlphabetical(List<ProductItem> listToSort) {

        Collections.sort(listToSort, new StringUtils.CurrentListAlphabeticalComparator());
        Collections.sort(listToSort, new StringUtils.CurrentListCategoryComparator());

        Log.d(LOG_TAG, "----------------- SORTED LIST ------------------");
        for (ProductItem product : listToSort) {
            Log.d(LOG_TAG, "Sorted: " + product.toNiceString());
        }
        return listToSort;
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
        currentList = sortListCategoryAndAlphabetical(currentList);
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
        @Bind(R.id.currentlist_how_tu_use_tv)
        TextView howToUseTv;

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


        private void makeFinishAlertDialog() {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            // set title and message
            alertDialogBuilder.setTitle(context.getResources().getText(R.string.dialog_button_finish_title))
                    .setMessage(context.getResources().getText(R.string.dialog_button_finish_message));
            //set yes no fields
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton(R.string.dialog_button_finish_shoppingtrip, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            //make two new ProductItem List "doneProductItemList" and "notDoneProductItemsList -> currentList split to done and not done products
                            List<ProductItem> doneProductItemList = new ArrayList<>();
                            List<ProductItem> notDoneItemsList = new ArrayList<>();

                            for (ProductItem product : currentList) {
                                if (product.isDone()) {
                                    product.setDone(false);
                                    doneProductItemList.add(product);
                                } else {
                                    notDoneItemsList.add(product);
                                }
                            }
                            //notDoneItemsList copy to currentList
                            currentList.clear();
                            currentList.addAll(notDoneItemsList);
                            Log.d(LOG_TAG, "-------------- REFRESHED CURRENT LIST ---------------");
                            for (ProductItem productItem : currentList) {
                                Log.d(LOG_TAG, "ID: " + productItem.getId() + ", Produkt: " + productItem.getProduct() + ", Done: " + productItem.isDone());
                            }
                            //store current list without done to SP
                            sharedPreferencesManager.saveCurrentShoppingListToLocalStore(currentList);
                            //create Shopping Trip object, add current date
                            Date now = DateUtils.getCurrentDate();
                            String date = DateUtils.dateToString(now);
                            Gson gson = new Gson();
                            String doneProductsListAsJson = gson.toJson(doneProductItemList, LinkedList.class);
                            ShoppingTrip shoppingTrip = new ShoppingTrip(date, doneProductsListAsJson);
                            doneProductItemList.clear();
                            //create Shopping Trip list, get all current entries from SP, write to list
                            List<ShoppingTrip> shoppingTripList = sharedPreferencesManager.loadHistoricShoppingTripsListFromLocalStore();
                            //add current Shopping Trip object to this list and save tp SP
                            if (shoppingTripList == null) {
                                shoppingTripList = new ArrayList<>();
                            }
                            shoppingTripList.add(shoppingTrip);
                            sharedPreferencesManager.saveHistoricShoppingTripsListToLocalStore(shoppingTripList);
                            Toast.makeText(context, context.getResources().getText(R.string.toast_shopping_trip_closed), Toast.LENGTH_SHORT).show();
                            notifyDataSetChanged();
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
}
