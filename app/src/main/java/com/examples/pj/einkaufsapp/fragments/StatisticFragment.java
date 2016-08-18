package com.examples.pj.einkaufsapp.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.examples.pj.einkaufsapp.R;
import com.examples.pj.einkaufsapp.adapters.StatisticAdapter;
import com.examples.pj.einkaufsapp.dbentities.ProductItem;
import com.examples.pj.einkaufsapp.dbentities.ProductItemDataSource;
import com.examples.pj.einkaufsapp.interfaces.ChangeToolbarInterface;
import com.examples.pj.einkaufsapp.util.SharedPreferencesManager;
import com.examples.pj.einkaufsapp.util.StringUtils;
import com.examples.pj.einkaufsapp.util.ViewUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Class as Container for Statistic Information
 */
public class StatisticFragment extends BaseFragment implements ChangeToolbarInterface{
    public static final String LOG_TAG = StatisticFragment.class.getSimpleName();

    @Bind(R.id.generallist_recycler_view)
    RecyclerView generalItemsRv;
    @Bind(R.id.toolbarShoppingCartIv)
    ImageView toolbarShoppingCartIv;

    private Context context;
    private SharedPreferencesManager sharedPreferencesManager;
    private static final String TOOLBAR_TITLE = "Statistik";
    private ProductItemDataSource dataSource;
    StatisticAdapter statisticAdapter;
    private List<ProductItem> generalShoppingList;
    private List<ProductItem> productsToAddToCurrentShoppingList;
    private boolean showEditAndDeleteIconInToolbar;
    private boolean showShoppingCartIconInToolbar;

    //================================================================================
    // Fragment Instantiation
    //================================================================================

    /**
     * Constructor
     */
    public StatisticFragment() {
        super(LOG_TAG, true);   //influences Hamburger Icon HomeUp in Toolbar
    }

    /**
     * createInstance as container for bundle arguments
     *
     * @return fragment
     */
    public static BaseFragment createInstance() {
        final BaseFragment fragment = new StatisticFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    //================================================================================
    // Base Methods
    //================================================================================

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_statistic;
    }

    @Override
    protected void setToolbar() {
        getAttachedActivity().setToolbar(toolbar, true, TOOLBAR_TITLE, showEditAndDeleteIconInToolbar, showShoppingCartIconInToolbar); //Icon displayed, Titel of Toolbar
    }

    @Override
    protected void setToolbarEditAndDeleteIcon(boolean showEditAndDeleteIconInToolbar) {
        if (toolbarTv != null) {
            toolbarTv.setText(TOOLBAR_TITLE);
        }
    }

    @Override
    protected void setToolbarShoppingCartIcon(boolean showShoppingCartIconInToolbar) {
        toolbarShoppingCartIv.setVisibility(showShoppingCartIconInToolbar ? View.VISIBLE : View.GONE);
        if (toolbarTv != null) {
            toolbarTv.setText(TOOLBAR_TITLE);
        }
    }

    @Override
    public boolean canGoBack() {
        return true;
    }

    @Override
    protected void onCleanUp() {
        //not needed
    }

    //================================================================================
    // Fragment Lifecycle
    //================================================================================

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = this.getActivity();
        dataSource = new ProductItemDataSource(context);

        if (sharedPreferencesManager == null) {
            sharedPreferencesManager = SharedPreferencesManager.initSharedPreferences((Activity) context);
        }

        showShoppingCartIconInToolbar = false;
        showEditAndDeleteIconInToolbar = false;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        dataSource.close(); //close db connection
    }

    @Override
    public void onResume() {
        super.onResume();

        setToolbarEditAndDeleteIcon(showEditAndDeleteIconInToolbar);
        setToolbarShoppingCartIcon(showShoppingCartIconInToolbar);
        setToolbar();

        dataSource.open();  //open db connection

        generalShoppingList = new ArrayList<>();
        generalShoppingList = dataSource.getAllProductItems();

        //sort List referring to number bought
        if (generalShoppingList != null && !generalShoppingList.isEmpty()) {
            generalShoppingList = sortListNumberBought(generalShoppingList);
        }

        printItemsInList();
        drawItemsInList();
    }

    //================================================================================
    // ChangeToolbarInterface Methods
    //================================================================================

    @Override
    public void showEditAndDeleteIcon(boolean show) {
        //Edit and Delete Icons are never shown in this view
    }

    @Override
    public void showShoppingCartIcon(boolean show) {
        setToolbarShoppingCartIcon(show);
        showShoppingCartIconInToolbar = show;
    }

    @Override
    public void handOverProductsToAddToCurrentShoppingList(List<ProductItem> productsToAdd) {
        productsToAddToCurrentShoppingList = new ArrayList<>();
        productsToAddToCurrentShoppingList.addAll(productsToAdd);
    }

    //================================================================================
    // Butter Knife Events
    //================================================================================

    @OnClick(R.id.toolbarShoppingCartIv)
    public void onShoppingCartClick() {
        makeFinishAlertDialog();
    }

    //================================================================================
    // ALERT DIALOGS
    //================================================================================

    private void makeFinishAlertDialog() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        // set title and message
        alertDialogBuilder.setTitle(context.getResources().getText(R.string.dialog_button_historic_finish_title))
                .setMessage(context.getResources().getText(R.string.dialog_button_historic_finish_message));
        //set yes no fields
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_button_historic_add_positive, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        //transfer of selected List to currentList
                        List<ProductItem> currentList = sharedPreferencesManager.loadCurrentShoppingListFromLocalStore();
                        List<ProductItem> productsToAdd = new ArrayList<ProductItem>();

                        for (ProductItem selectedProduct : productsToAddToCurrentShoppingList) {
                            boolean productFound = false;
                            for (ProductItem currentListProduct : currentList) {
                                if (selectedProduct.getId() == currentListProduct.getId()) {
                                    productFound = true;
                                    break;
                                }
                            }
                            if (!productFound) {
                                productsToAdd.add(selectedProduct);
                            }
                        }
                        for (ProductItem productToAdd : productsToAdd) {
                            productToAdd.setCurrentClicked(false);
                            currentList.add(productToAdd);
                        }
                        currentList = sortListCategoryAndAlphabetical(currentList);
                        sharedPreferencesManager.saveCurrentShoppingListToLocalStore(currentList);

                        //reset all clicked states
                        for (ProductItem productItem : generalShoppingList) {
                            productItem.setCurrentClicked(false);
                        }
                        statisticAdapter.notifyDataSetChanged();
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

    //================================================================================
    // Other Methods
    //================================================================================

    public void printItemsInList() {
        System.out.println("-------- ITEMS IN GENERAL LIST -----------");
        if (generalShoppingList.size() > 0) {
            for (ProductItem productItem : generalShoppingList) {
                System.out.println("Produkt: " + productItem.getProduct() + ", " + productItem.getBought());
            }
        } else {
            System.out.println("Keine Einträge vorhanden");
        }
    }

    public void drawItemsInList() {

        int numberHighestBought = 0;
        //sort List
        generalShoppingList = sortListNumberBought(generalShoppingList);

        if (generalShoppingList != null && !generalShoppingList.isEmpty()) {
            numberHighestBought = generalShoppingList.get(0).getBought();
        }

        statisticAdapter = new StatisticAdapter(generalShoppingList, context, this, numberHighestBought);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        generalItemsRv.setLayoutManager(linearLayoutManager);
        generalItemsRv.setAdapter(statisticAdapter);
        generalItemsRv.setHasFixedSize(true);
        generalItemsRv.setVisibility(View.VISIBLE);
    }

    /**
     * method to sort list alphabetically and referring to category
     *
     * @param listToSort list
     * @return list
     */

    //TODO must be sorted for integers, not Strings! -> 9 is higher than 10 otherwise
    public List<ProductItem> sortListNumberBought(List<ProductItem> listToSort) {
        Collections.sort(listToSort, Collections.reverseOrder(new StringUtils.GeneralListBoughtComparator()));
        return listToSort;
    }

    /**
     * sortListCategoryAndAlphabetical: sort items from currentList alphabetically
     *
     * @param list: currentList
     * @return currentList, sorted alphabetically and referring to categories
     */
    public List<ProductItem> sortListCategoryAndAlphabetical(List<ProductItem> list) {
        Collections.sort(list, new StringUtils.CurrentListAlphabeticalComparator());
        Collections.sort(list, new StringUtils.CurrentListCategoryComparator());
        return list;
    }

}