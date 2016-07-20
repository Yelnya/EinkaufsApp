package com.examples.pj.einkaufsapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.examples.pj.einkaufsapp.R;
import com.examples.pj.einkaufsapp.dbentities.ProductItem;
import com.examples.pj.einkaufsapp.dbentities.ProductItemDataSource;
import com.examples.pj.einkaufsapp.interfaces.ChangeToolbarInterface;
import com.examples.pj.einkaufsapp.util.SharedPreferencesManager;

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
    private List<ProductItem> itemsList;
    private ProductItem itemClicked;

    /**
     * Constructor
     *
     * @param context                  from MainActivity
     * @param changeToolbarInterface   for Edit / Delete Icons to show or not
     * @param dataSource               db
     * @param editDeleteToolbarActive  boolean -> edit / delete icons currently shown or not
     * @param itemsList                currentList from Fragment
     * @param sharedPreferencesManager to store data locally
     */
    public CurrentListAdapter(List<ProductItem> itemsList, Context context, ProductItemDataSource dataSource, SharedPreferencesManager sharedPreferencesManager, ChangeToolbarInterface changeToolbarInterface, boolean editDeleteToolbarActive) {
        this.itemsList = itemsList;
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

        if (itemsList != null) {
            itemsList.clear();
            itemsList = sharedPreferencesManager.loadCurrentShoppingListFromLocalStore();
        } else {
            itemsList = new ArrayList<>();
        }

        if (viewType == LIST_ITEMS) {
            ArraylistViewHolder viewHolder = (ArraylistViewHolder) holder;
            ProductItem item = itemsList.get(position);
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

            if (itemsList != null) {
                if (!itemsList.isEmpty()) {
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
        if (itemsList != null) {
            return itemsList.size() + 1; //plus one for bottom element
        } else {
            return 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        int viewType;
        if (itemsList != null) {
            if (position < itemsList.size()) {
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
        Collections.sort(itemsList, new CurrentListAlphabeticalComparator());
        Collections.sort(itemsList, new CurrentListCategoryComparator());

        Log.d(LOG_TAG, "----------------- SORTED LIST ------------------");
        for (ProductItem product : itemsList) {
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
        itemsList.remove(item);
        itemsList.add(updatedItem);
        sortListCategoryAndAlphabetical();
        sharedPreferencesManager.saveCurrentShoppingListToLocalStore(itemsList);
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
            ProductItem product = itemsList.get(pos);
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
            ProductItem selectedItem = itemsList.get(pos);
            editDeleteToolbarActive = !editDeleteToolbarActive;   //toggle if EditDelete Toolbar is active or not
            changeToolbarInterface.showEditAndDeleteIcon(editDeleteToolbarActive); //only show EditDelete Toolbar if it is not active right now. Otherwise change back to normal Toolbar

            setItemClicked(selectedItem);

            //Overlay für Löschen von ListItems: ContextualActionBar
//            final ListView productItemListView = (ListView) getActivity().findViewById(R.id.currentlist_recycler_view);
//            productItemsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
//
//            productItemsListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
//
//                int selCount = 0;
//
//                // In dieser Callback-Methode zählen wir die ausgewählen Listeneinträge mit
//                // und fordern ein Aktualisieren der Contextual Action Bar mit invalidate() an
//                @Override
//                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
//                    if (checked) {
//                        selCount++;
//                    } else {
//                        selCount--;
//                    }
//                    ProductItem productItem = (ProductItem) currentShoppingListView.getItemAtPosition(position);
//                    String selectedItem = productItem.getProduct();
//                    String cabTitle = selectedItem + " " + getString(R.string.cab_checked_string);
//                    mode.setTitle(cabTitle);
//                    mode.invalidate();
//                }
//
//                // In dieser Callback-Methode legen wir die CAB-Menüeinträge an
//                @Override
//                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//                    toolbar.setVisibility(View.GONE);
//                    getActivity().getMenuInflater().inflate(R.menu.menu_contextual_action_bar, menu);
//                    return true;
//                }
//
//                // In dieser Callback-Methode reagieren wir auf den invalidate() Aufruf
//                // Wir lassen das Edit-Symbol verschwinden, wenn mehr als 1 Eintrag ausgewählt ist
//                @Override
//                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//                    MenuItem item = menu.findItem(R.id.cab_change);
//                    if (selCount == 1) {
//                        item.setVisible(true);
//                    } else {
//                        item.setVisible(false);
//                    }
//                    return true;
//                }
//
//                // In dieser Callback-Methode reagieren wir auf Action Item-Klicks
//                // Je nachdem ob das Löschen- oder Ändern-Symbol angeklickt wurde
//                @Override
//                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//                    boolean returnValue = true;
//                    SparseBooleanArray touchedProductItemsPositions = productItemsListView.getCheckedItemPositions();
//
//                    switch (item.getItemId()) {
//                        case R.id.cab_delete:
//                            for (int i = 0; i < touchedProductItemsPositions.size(); i++) {
//                                boolean isDone = touchedProductItemsPositions.valueAt(i);
//                                if (isDone) {
//                                    int postitionInListView = touchedProductItemsPositions.keyAt(i);
//                                    ProductItem productItem = (ProductItem) productItemsListView.getItemAtPosition(postitionInListView);
//                                    Log.d(LOG_TAG, "Position im ListView: " + postitionInListView + " Inhalt: " + productItem.toString());
//                                    currentList.remove(productItem);
//                                    sortListCategoryAndAlphabetical();
//                                    sharedPreferencesManager.saveCurrentShoppingListToLocalStore(currentList);
//
////                                dataSource.deleteProductItem(productItem);
//                                }
//                            }
//                            showAllListEntries();
//                            mode.finish();
//                            break;
//
//                        case R.id.cab_change:
//                            Log.d(LOG_TAG, "Eintrag ändern");
//                            for (int i = 0; i < touchedProductItemsPositions.size(); i++) {
//                                boolean isDone = touchedProductItemsPositions.valueAt(i);
//                                if (isDone) {
//                                    int postitionInListView = touchedProductItemsPositions.keyAt(i);
//                                    ProductItem productItem = (ProductItem) productItemsListView.getItemAtPosition(postitionInListView);
//                                    Log.d(LOG_TAG, "Position im ListView: " + postitionInListView + " Inhalt: " + productItem.toString());
//
//                                    AlertDialog editProductItemDialog = createEditProductItemDialog(productItem);
//                                    editProductItemDialog.show();
//                                }
//                            }
//                            mode.finish();
//                            break;
//
//                        default:
//                            returnValue = false;
//                            break;
//                    }
//                    return returnValue;
//                }
//
//                // In dieser Callback-Methode reagieren wir auf das Schließen der CAB
//                // Wir setzen den Zähler auf 0 zurück
//                @Override
//                public void onDestroyActionMode(ActionMode mode) {
//                    selCount = 0;
//                    toolbar.setVisibility(View.VISIBLE);
//                }
//
//            });

            return true;
        }
    }

    /**
     * ViewHolder for TextView and Button below RecyclerView
     */
    public class BottomElementViewHolder extends BaseViewHolder {

        @Bind(R.id.currentlist_Btn)
        Button currentlistBtn;
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
}
