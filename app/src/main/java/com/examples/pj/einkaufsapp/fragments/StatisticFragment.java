package com.examples.pj.einkaufsapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.examples.pj.einkaufsapp.R;
import com.examples.pj.einkaufsapp.adapters.StatisticAdapter;
import com.examples.pj.einkaufsapp.dbentities.ProductItem;
import com.examples.pj.einkaufsapp.dbentities.ProductItemDataSource;
import com.examples.pj.einkaufsapp.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Class as Container for Test Information
 */
public class StatisticFragment extends BaseFragment {
    public static final String LOG_TAG = StatisticFragment.class.getSimpleName();

    @Bind(R.id.generallist_recycler_view)
    RecyclerView generalItemsRv;

    private Context context;
    private static final String TOOLBAR_TITLE = "Statistik";
    private ProductItemDataSource dataSource;
    private List<ProductItem> generalShoppingList;
    private boolean showEditAndDeleteIconInToolbar;
    private boolean showShoppingCartIconInToolbar;
    private StatisticAdapter statisticAdapter;

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
        toolbarTv.setText(TOOLBAR_TITLE);
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
        showShoppingCartIconInToolbar = false;
        showEditAndDeleteIconInToolbar = false;
        setToolbarEditAndDeleteIcon(showEditAndDeleteIconInToolbar);
        dataSource = new ProductItemDataSource(context);
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
        //add numberHighestBought to Adapter
        if (generalShoppingList != null && !generalShoppingList.isEmpty()) {
            numberHighestBought = generalShoppingList.get(0).getBought();
        }

        statisticAdapter = new StatisticAdapter(generalShoppingList, context, numberHighestBought);
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
    public List<ProductItem> sortListNumberBought(List<ProductItem> listToSort) {
        Collections.sort(listToSort, Collections.reverseOrder(new StringUtils.GeneralListBoughtComparator()));
        return listToSort;
    }

}