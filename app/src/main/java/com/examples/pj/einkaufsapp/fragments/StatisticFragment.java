package com.examples.pj.einkaufsapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
    private ListView mProductItemListView;
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
        dataSource = new ProductItemDataSource(context);

        initializeProductItemsListView();
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
    }

    //
    private void showAllListEntries() {
        List<ProductItem> productItemList = dataSource.getAllProductItems();

        ArrayAdapter<ProductItem> adapter = (ArrayAdapter<ProductItem>) mProductItemListView.getAdapter();

        adapter.clear();
        adapter.addAll(productItemList);
        adapter.notifyDataSetChanged();
    }


    //initialization of list view
    private void initializeProductItemsListView() {
        List<ProductItem> emptyListForInitialization = new ArrayList<>();

        mProductItemListView = (ListView) getActivity().findViewById(R.id.listview_product_items);

        // create array adapter for list view
        ArrayAdapter<ProductItem> productItemArrayAdapter = new ArrayAdapter<ProductItem>(
                getActivity(),
                android.R.layout.simple_list_item_multiple_choice,
                emptyListForInitialization) {

            // call if line has to be drawn new
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                ProductItem productItem = (ProductItem) mProductItemListView.getItemAtPosition(position);

                textView.setText(productItem.toNiceString());

                return view;
            }
        };

        mProductItemListView.setAdapter(productItemArrayAdapter);

        mProductItemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ProductItem productItem = (ProductItem) adapterView.getItemAtPosition(position);

                // invert checked value for productItem, then draw list new
                dataSource.updateProductItem(productItem.getId(), productItem.getProduct(), productItem.getCategory(), productItem.getBought(), !productItem.isDone(), productItem.isFavourite());
                showAllListEntries();
            }
        });
    }
}