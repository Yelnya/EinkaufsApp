package com.examples.pj.einkaufsapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.examples.pj.einkaufsapp.R;
import com.examples.pj.einkaufsapp.dbentities.ProductItem;
import com.examples.pj.einkaufsapp.dbentities.ShoppingTrip;
import com.examples.pj.einkaufsapp.util.SharedPreferencesManager;

import java.util.List;

/**
 * Fore already done shopping events
 */
public class HistoricListsFragment extends BaseFragment {
    public static final String LOG_TAG = HistoricListsFragment.class.getSimpleName();

    private static final String TOOLBAR_TITLE = "Erledigte Einkäufe";
    private boolean showEditAndDeleteIconInToolbar;

    Context context;
    SharedPreferencesManager sharedPreferencesManager;
    private List<ShoppingTrip> historicShoppingTripsList;

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
     * createInstance for getting arguments in bundle
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
        getAttachedActivity().setToolbar(toolbar, true, TOOLBAR_TITLE, showEditAndDeleteIconInToolbar); //Icon displayed, Titel of Toolbar
    }

    @Override
    protected void setToolbarEditAndDeleteIcon(boolean showEditAndDeleteIconInToolbar) {
        toolbarTv.setText(TOOLBAR_TITLE);
    }

    @Override
    public boolean canGoBack() {
        return true;
    }

    //================================================================================
    // Fragment Lifecycle
    //================================================================================

    @Override
    public void onResume() {
        super.onResume();

        showEditAndDeleteIconInToolbar = false;
        setToolbarEditAndDeleteIcon(showEditAndDeleteIconInToolbar);

        context = getActivity();
        if (sharedPreferencesManager == null) {
            sharedPreferencesManager = SharedPreferencesManager.initSharedPreferences((Activity) context);
        }
        historicShoppingTripsList = sharedPreferencesManager.loadHistoricShoppingTripsListFromLocalStore();

        showHistoricShoppingTripsListLog();
    }

    //================================================================================
    // Other Methods
    //================================================================================

    private void showHistoricShoppingTripsListLog() {
        int i = 0;
        Log.d(LOG_TAG, "-------------------LOCAL LIST ENTRIES -----------------------");
        for (ShoppingTrip trip : historicShoppingTripsList) {
            i++;
            Log.d(LOG_TAG, "Shopping Trip " + i + ": " + trip.getDateCompleted());
            List<ProductItem> boughtProductsList = trip.getBoughtProducts();
            int j = 0;

            for (ProductItem productItem : boughtProductsList) {
                j++;
                Log.d(LOG_TAG, "Produkt " + j + ": " + productItem.getProduct());
            }
        }
    }
}