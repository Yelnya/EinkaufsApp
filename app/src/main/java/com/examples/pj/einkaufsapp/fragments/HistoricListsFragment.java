package com.examples.pj.einkaufsapp.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.examples.pj.einkaufsapp.R;
import com.examples.pj.einkaufsapp.adapters.HistoricListsAdapter;
import com.examples.pj.einkaufsapp.dbentities.ProductItem;
import com.examples.pj.einkaufsapp.dbentities.ShoppingTrip;
import com.examples.pj.einkaufsapp.util.SharedPreferencesManager;
import com.examples.pj.einkaufsapp.util.StringUtils;
import com.examples.pj.einkaufsapp.util.ViewUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Class as Container for Test Information
 */
public class HistoricListsFragment extends BaseFragment {
    public static final String LOG_TAG = HistoricListsFragment.class.getSimpleName();

    private boolean showShoppingCartIconInToolbar;
    private boolean showEditAndDeleteIconInToolbar;
    private String toolbarTitle = "";
    private String toolbarTitleFragment;

    @Bind(R.id.expandable_list)
    ExpandableListView expandableListView;
    @Bind(R.id.toolbarShoppingCartIv)
    ImageView toolbarShoppingCartIv;
    @Bind(R.id.toolbarArrowUpIv)
    ImageView toolbarArrowUpIv;
    @Bind(R.id.toolbarArrowDownIv)
    ImageView toolbarArrowDownIv;
    @Bind(R.id.historiclist_top_hint)
    TextView topHint;

    private Context context;
    private SharedPreferencesManager sharedPreferencesManager;
    private List<ShoppingTrip> historicShoppingTripsList;
    private HistoricListsAdapter listAdapter;
    private List<ProductItem> selectedProductItemsList;
    private int noSelected;

    //================================================================================
    // Fragment Instantiation
    //================================================================================

    /**
     * Constructor
     */
    public HistoricListsFragment() {
        super(LOG_TAG, true);   //influences Hamburger Icon HomeUp in Toolbar
    }

    /**
     * createInstance as container for bundle arguments
     *
     * @return fragment
     */
    public static BaseFragment createInstance() {
        final BaseFragment fragment = new HistoricListsFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    //================================================================================
    // Base Methods
    //================================================================================

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_historic_lists;
    }

    @Override
    protected void setToolbar() {
        getAttachedActivity().setToolbar(toolbar, true, toolbarTitle, showEditAndDeleteIconInToolbar, showShoppingCartIconInToolbar); //Icon displayed, Titel of Toolbar
    }

    @Override
    protected void setToolbarShoppingCartIcon(boolean showShoppingCartIconInToolbar) {
        toolbarShoppingCartIv.setVisibility(showShoppingCartIconInToolbar ? View.VISIBLE : View.GONE);
        toolbarTv.setText(toolbarTitle);
    }

    @Override
    protected void setToolbarArrowUpIcon(boolean showArrowUpIconInToolbar) {
        toolbarArrowUpIv.setVisibility(showArrowUpIconInToolbar ? View.VISIBLE : View.GONE);
        toolbarTv.setText(toolbarTitle);
    }

    @Override
    protected void setToolbarArrowDownIcon(boolean showArrowDownIconInToolbar) {
        toolbarArrowDownIv.setVisibility(showArrowDownIconInToolbar ? View.VISIBLE : View.GONE);
        toolbarTv.setText(toolbarTitle);
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

        context = super.getActivity();
        toolbarTitleFragment = context.getResources().getString(R.string.toolbar_title_historic_list);
        noSelected = 0;

        showEditAndDeleteIconInToolbar = false;
        showShoppingCartIconInToolbar = false;
        setToolbarArrowUpIcon(true);
        setToolbarArrowDownIcon(true);

        if (sharedPreferencesManager == null) {
            sharedPreferencesManager = SharedPreferencesManager.initSharedPreferences((Activity) context);
        }
        historicShoppingTripsList = sharedPreferencesManager.loadHistoricShoppingTripsListFromLocalStore();
        if (historicShoppingTripsList == null) {
            historicShoppingTripsList = new ArrayList<>();
        }
        listAdapter = new HistoricListsAdapter(context, historicShoppingTripsList);
        expandableListView.setAdapter(listAdapter);
        collapseAll(); //collapse all Groups
        expandableListView.setOnChildClickListener(myListItemClicked); //listener for child row click
        expandableListView.setOnGroupClickListener(myListGroupClicked); //listener for group heading click


    }

    @Override
    public void onResume() {
        super.onResume();
        toolbarTitle = toolbarTitleFragment;
        setToolbarEditAndDeleteIcon(showEditAndDeleteIconInToolbar);
        setToolbarShoppingCartIcon(showShoppingCartIconInToolbar);

        if (historicShoppingTripsList == null || historicShoppingTripsList.isEmpty()) {
            topHint.setVisibility(View.VISIBLE);
            topHint.setText(context.getResources().getString(R.string.top_hint_no_shopping_trips));
        } else {
            topHint.setVisibility(View.GONE);
        }

        if (selectedProductItemsList == null) {
            selectedProductItemsList = new ArrayList<>();
        }
    }

    //================================================================================
    // OnClickListeners
    //================================================================================

    //child click listener
    private OnChildClickListener myListItemClicked = new OnChildClickListener() {
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            ShoppingTrip shoppingTrip = historicShoppingTripsList.get(groupPosition); //get the group header
            List<ProductItem> productItemList = historicShoppingTripsList.get(groupPosition).getBoughtProductsList();   //get children list of group header
            ProductItem productItem = productItemList.get(childPosition);//get the child info

            productItem.setCurrentClicked(!productItem.isCurrentClicked());
            shoppingTrip.setBoughtProductsList(productItemList);    //refresh product list of shoppingtrip
            listAdapter.notifyDataSetChanged();

            //add product to Selected List if not already present
            if (productItem.isCurrentClicked()) {
                addItemToSelectedList(productItem);
            } else {
                removeItemFromSelectedList(productItem); //remove deselected product if it is in list
            }
            return false;
        }
    };

    //group click listener
    private ExpandableListView.OnGroupClickListener myListGroupClicked = new ExpandableListView.OnGroupClickListener() {
        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
            ShoppingTrip shoppingTrip = historicShoppingTripsList.get(groupPosition); //get the group header
            shoppingTrip.setExpanded(!shoppingTrip.isExpanded());
            listAdapter.notifyDataSetChanged();
            return false;
        }
    };

    @OnClick(R.id.toolbarArrowDownIv)
    public void onArrowDownClick() {
        expandAll();
    }

    @OnClick(R.id.toolbarArrowUpIv)
    public void onArrowUpClick() {
        collapseAll();
    }

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
                        List<ProductItem> productsToAdd = new ArrayList<>();

                        for (ProductItem selectedProduct : selectedProductItemsList) {
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
                            currentList.add(productToAdd);
                        }
                        currentList = sortListCategoryAndAlphabetical(currentList);
                        sharedPreferencesManager.saveCurrentShoppingListToLocalStore(currentList);

                        //set all clicked children to un-selected
                        for (ShoppingTrip historicShoppingTrip : historicShoppingTripsList) {
                            List<ProductItem> childItemsList = historicShoppingTrip.getBoughtProductsList();
                            for (ProductItem childItem : childItemsList) {
                                childItem.setCurrentClicked(false);
                            }
                            historicShoppingTrip.setBoughtProductsList(childItemsList); //refresh product list of shoppingtrip
                        }
                        listAdapter.notifyDataSetChanged();
                        setToolbarShoppingCartIcon(false);
                        noSelected = 0;

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

    //method to expand all groups
    private void expandAll() {
        int count = listAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            expandableListView.expandGroup(i);
        }
    }

    //method to collapse all groups
    private void collapseAll() {
        int count = listAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            expandableListView.collapseGroup(i);
        }
    }

    private void addItemToSelectedList(ProductItem productItem) {
        boolean alreadyInList = false;
        for (ProductItem product : selectedProductItemsList) {
            if (productItem.getId() == product.getId()) {
                alreadyInList = true;
                break;
            }
        }
        if (!alreadyInList) {
            selectedProductItemsList.add(productItem);
            noSelected++;
        }
        toolbarShoppingCartIv.setVisibility(noSelected == 0 ? View.GONE : View.VISIBLE);
        showShoppingCartIconInToolbar = noSelected != 0;
    }

    private void removeItemFromSelectedList(ProductItem productItem) {
        List<ProductItem> copySelectedProductItemsList = new ArrayList<>();

        for (ProductItem product : selectedProductItemsList) {
            if (productItem.getId() != product.getId()) {
                copySelectedProductItemsList.add(product);
            }
        }
        if (selectedProductItemsList.size() > copySelectedProductItemsList.size()) {
            noSelected--;
        }
        selectedProductItemsList.clear();
        selectedProductItemsList.addAll(copySelectedProductItemsList);
        toolbarShoppingCartIv.setVisibility(noSelected == 0 ? View.GONE : View.VISIBLE);
        showShoppingCartIconInToolbar = noSelected != 0;
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