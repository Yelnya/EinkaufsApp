package com.examples.pj.einkaufsapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.examples.pj.einkaufsapp.R;
import com.examples.pj.einkaufsapp.dbentities.ProductItem;
import com.examples.pj.einkaufsapp.dbentities.ProductItemDataSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Overview for all done shopping events with statistic function
 */
public class StatisticFragment extends BaseFragment {

    public static final String LOG_TAG = StatisticFragment.class.getSimpleName();

    private Context context;
    private static final String TOOLBAR_TITLE = "Statistiken";
    private ProductItemDataSource dataSource;
    private ListView generalShoppingListLv;
    List<ProductItem> generalShoppingList;
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
     * createInstance contains arguments in bundle
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

        initializeProductItemsListView();

    }

    //initialization of list view
    private void initializeProductItemsListView() {

        generalShoppingListLv = (ListView) getActivity().findViewById(R.id.listview_product_items);

        // create array adapter for list view
        ArrayAdapter<ProductItem> productItemArrayAdapter = new ArrayAdapter<ProductItem>(
                getActivity(),
                android.R.layout.simple_list_item_multiple_choice,
                generalShoppingList) {

            // call if line has to be drawn new
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                ProductItem productItem = (ProductItem) generalShoppingListLv.getItemAtPosition(position);
                textView.setText(productItem.toNiceString());
                return view;
            }
        };
        generalShoppingListLv.setAdapter(productItemArrayAdapter);
    }
}