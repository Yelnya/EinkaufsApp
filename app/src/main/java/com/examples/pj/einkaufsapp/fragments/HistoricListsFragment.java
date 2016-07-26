package com.examples.pj.einkaufsapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.examples.pj.einkaufsapp.R;
import com.examples.pj.einkaufsapp.adapters.HistoricListsAdapter;
import com.examples.pj.einkaufsapp.dbentities.ProductItem;
import com.examples.pj.einkaufsapp.dbentities.ShoppingTrip;
import com.examples.pj.einkaufsapp.interfaces.ChangeToolbarInterface;
import com.examples.pj.einkaufsapp.util.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

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
    RecyclerView historicListsRv;
    @Bind(R.id.toolbarDeleteIv)
    ImageView toolbarDeleteIv;
    @Bind(R.id.toolbarEditIv)
    ImageView toolbarEditIv;
    @Bind(R.id.historiclist_top_hint)
    TextView topHint;

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

    @Override
    protected void onCleanUp() {

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
        showEditAndDeleteIconInToolbar = false;

        initializeRvAdapter();
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

        if (historicShoppingTripsList == null || historicShoppingTripsList.isEmpty()) {
            topHint.setVisibility(View.VISIBLE);
            topHint.setText(context.getResources().getString(R.string.top_hint_no_shopping_trips));
        } else {
            topHint.setVisibility(View.GONE);
        }
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

    private void initializeRvAdapter() {

        //make List with ShoppingTrip Objects and referring ProductItemObjects
        List<Object> parentAndChildrenList = new ArrayList<>();
        for (ShoppingTrip shoppingTrip : historicShoppingTripsList) {
            parentAndChildrenList.add(shoppingTrip);
            for (ProductItem productItem : shoppingTrip.getBoughtProducts()) {
                parentAndChildrenList.add(productItem);
                System.out.println("BoughtProduct: " + productItem.toString());
            }
        }

        //Adapter setup
        historicListsAdapter = new HistoricListsAdapter(context, parentAndChildrenList);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        historicListsRv.setLayoutManager(linearLayoutManager);
        historicListsRv.setAdapter(historicListsAdapter);
        historicListsRv.setHasFixedSize(true);
        historicListsRv.setVisibility(View.VISIBLE);
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
