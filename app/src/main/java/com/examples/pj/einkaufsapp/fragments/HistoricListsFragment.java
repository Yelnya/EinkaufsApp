package com.examples.pj.einkaufsapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.examples.pj.einkaufsapp.R;
import com.examples.pj.einkaufsapp.adapters.HistoricListsAdapter;
import com.examples.pj.einkaufsapp.dbentities.ProductItem;
import com.examples.pj.einkaufsapp.dbentities.ShoppingTrip;
import com.examples.pj.einkaufsapp.interfaces.ChangeToolbarInterface;
import com.examples.pj.einkaufsapp.util.SharedPreferencesManager;

import java.util.List;

import butterknife.Bind;

/**
 * Fore already done shopping events
 */
public class HistoricListsFragment extends BaseFragment implements ChangeToolbarInterface {
    public static final String LOG_TAG = HistoricListsFragment.class.getSimpleName();

    Context context;
    SharedPreferencesManager sharedPreferencesManager;
    HistoricListsAdapter historicListsAdapter;
    private List<ShoppingTrip> historicShoppingTripsList;
    private boolean showEditAndDeleteIconInToolbar;
    private String toolbarTitle = "";
    private static final String TOOLBAR_TITLE_FRAGMENT = "Erledigte Einkäufe";
    private static final String TOOLBAR_TITLE_EDIT = "Löschen/Ändern";

    @Bind(R.id.historiclist_recycler_view)
    RecyclerView historicListRv;
    @Bind(R.id.toolbarDeleteIv)
    ImageView toolbarDeleteIv;
    @Bind(R.id.toolbarEditIv)
    ImageView toolbarEditIv;

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
        getAttachedActivity().setToolbar(toolbar, true, toolbarTitle, showEditAndDeleteIconInToolbar); //Icon displayed, Titel of Toolbar
    }

    @Override
    protected void setToolbarEditAndDeleteIcon(boolean showEditAndDeleteIconInToolbar) {
        toolbarEditIv.setVisibility(showEditAndDeleteIconInToolbar ? View.VISIBLE : View.INVISIBLE);
        toolbarDeleteIv.setVisibility(showEditAndDeleteIconInToolbar ? View.VISIBLE : View.INVISIBLE);
        toolbarTv.setText(toolbarTitle);
    }

    @Override
    public boolean canGoBack() {
        return true;
    }

    //================================================================================
    // Fragment Lifecycle
    //================================================================================

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        showEditAndDeleteIconInToolbar = false;

        context = getActivity();
        if (sharedPreferencesManager == null) {
            sharedPreferencesManager = SharedPreferencesManager.initSharedPreferences((Activity) context);
        }
        historicShoppingTripsList = sharedPreferencesManager.loadHistoricShoppingTripsListFromLocalStore();

        initializeProductItemsListView();
        showEditAndDeleteIconInToolbar = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbarTitle = TOOLBAR_TITLE_FRAGMENT;
        setToolbarEditAndDeleteIcon(showEditAndDeleteIconInToolbar);

        context = getActivity();
        if (sharedPreferencesManager == null) {
            sharedPreferencesManager = SharedPreferencesManager.initSharedPreferences((Activity) context);
        }
        historicShoppingTripsList = sharedPreferencesManager.loadHistoricShoppingTripsListFromLocalStore();

        showHistoricShoppingTripsListLog();
    }

    //---------------------------------------------------------------
    // Other Methods
    //---------------------------------------------------------------

    private void toolbarBackToNormal() {
        showEditAndDeleteIconInToolbar = false;
        toolbarTitle = TOOLBAR_TITLE_FRAGMENT;
        setToolbarEditAndDeleteIcon(showEditAndDeleteIconInToolbar);
        historicListsAdapter.setEditDeleteToolbarActive(false);
    }

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

    private void initializeProductItemsListView() {
        historicListsAdapter = new HistoricListsAdapter(context, sharedPreferencesManager, this, historicShoppingTripsList, showEditAndDeleteIconInToolbar);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        historicListRv.setLayoutManager(linearLayoutManager);
        historicListRv.setAdapter(historicListsAdapter);
        historicListRv.setHasFixedSize(true);
        historicListRv.setVisibility(View.VISIBLE);
    }

    //---------------------------------------------------------------
    // ChangeToolbarInterface Methods
    //---------------------------------------------------------------

    @Override
    public void showEditAndDeleteIcon(boolean show) {
        // change Toolbar
        Log.d(LOG_TAG, "Show Edit and Delete Icons in Toolbar: " + show);
        toolbarTitle = show ? TOOLBAR_TITLE_EDIT : TOOLBAR_TITLE_FRAGMENT;
        showEditAndDeleteIconInToolbar = show;
        setToolbarEditAndDeleteIcon(show);
        setToolbar();
    }
}