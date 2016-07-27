package com.examples.pj.einkaufsapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.examples.pj.einkaufsapp.R;
import com.examples.pj.einkaufsapp.adapters.HistoricListsAdapter;
import com.examples.pj.einkaufsapp.dbentities.ProductItem;
import com.examples.pj.einkaufsapp.dbentities.ShoppingTrip;
import com.examples.pj.einkaufsapp.util.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Class as Container for Test Information
 */
public class HistoricListsFragment extends BaseFragment {
    public static final String LOG_TAG = HistoricListsFragment.class.getSimpleName();

    private boolean showEditAndDeleteIconInToolbar;
    private String toolbarTitle = "";
    private static final String TOOLBAR_TITLE_FRAGMENT = "Einkauf vom ...";

    @Bind(R.id.expandable_list)
    ExpandableListView expandableListView;
    @Bind(R.id.toolbarDeleteIv)
    ImageView toolbarDeleteIv;
    @Bind(R.id.toolbarEditIv)
    ImageView toolbarEditIv;
    @Bind(R.id.historiclist_top_hint)
    TextView topHint;

    Context context;
    SharedPreferencesManager sharedPreferencesManager;
    private List<ShoppingTrip> historicShoppingTripsList;
    private HistoricListsAdapter listAdapter;

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
        return R.layout.fragment_test;
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

        context = super.getActivity();

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
        toolbarTitle = TOOLBAR_TITLE_FRAGMENT;
        showEditAndDeleteIconInToolbar = false;
        setToolbarEditAndDeleteIcon(showEditAndDeleteIconInToolbar);

        if (historicShoppingTripsList == null || historicShoppingTripsList.isEmpty()) {
            topHint.setVisibility(View.VISIBLE);
            topHint.setText(context.getResources().getString(R.string.top_hint_no_shopping_trips));
        } else {
            topHint.setVisibility(View.GONE);
        }
    }

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

    //child click listener
    private OnChildClickListener myListItemClicked = new OnChildClickListener() {
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            ShoppingTrip shoppingTrip = historicShoppingTripsList.get(groupPosition); //get the group header
            List<ProductItem> productItemList = historicShoppingTripsList.get(groupPosition).getBoughtProductsList();   //get children list of group header
            ProductItem productItem = productItemList.get(childPosition);//get the child info
            Toast.makeText(context, "Click on " + productItem.getProduct(), Toast.LENGTH_LONG).show();

            productItem.setCurrentClicked(!productItem.isCurrentClicked());
            shoppingTrip.setBoughtProductsList(productItemList);    //refresh product list of shoppingtrip
            listAdapter.notifyDataSetChanged();
            return false;
        }
    };

    //group click listener
    private ExpandableListView.OnGroupClickListener myListGroupClicked = new ExpandableListView.OnGroupClickListener() {
        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
            ShoppingTrip shoppingTrip = historicShoppingTripsList.get(groupPosition); //get the group header
            Toast.makeText(context, "Click on Einkauf: " + shoppingTrip.getDateCompleted(), Toast.LENGTH_LONG).show();

            shoppingTrip.setExpanded(!shoppingTrip.isExpanded());
            listAdapter.notifyDataSetChanged();
            return false;
        }
    };
}