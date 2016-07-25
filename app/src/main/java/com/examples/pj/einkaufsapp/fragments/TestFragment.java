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
import com.examples.pj.einkaufsapp.adapters.TestAdapter;
import com.examples.pj.einkaufsapp.dbentities.ProductItem;
import com.examples.pj.einkaufsapp.dbentities.ShoppingTrip;
import com.examples.pj.einkaufsapp.interfaces.ChangeToolbarInterface;
import com.examples.pj.einkaufsapp.util.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class TestFragment extends BaseFragment implements ChangeToolbarInterface {
    public static final String LOG_TAG = TestFragment.class.getSimpleName();

    Context context;
    SharedPreferencesManager sharedPreferencesManager;
    TestAdapter testAdapter;
    private List<ShoppingTrip> historicShoppingTripsList;
    private boolean showEditAndDeleteIconInToolbar;
    private String toolbarTitle = "";
    private static final String TOOLBAR_TITLE_FRAGMENT = "Test";
    private static final String TOOLBAR_TITLE_EDIT = "Löschen/Ändern";

    @Bind(R.id.test_recycler_view)
    RecyclerView testRv;
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
    public TestFragment() {
        super(LOG_TAG, true);   //influences Hamburger Icon HomeUp in Toolbar
    }

    /**
     * createInstance for getting arguments in bundle
     *
     * @return fragment
     */
    public static BaseFragment createInstance() {
        final BaseFragment fragment = new TestFragment();
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
    }

    //---------------------------------------------------------------
    // Other Methods
    //---------------------------------------------------------------

    private void toolbarBackToNormal() {
        showEditAndDeleteIconInToolbar = false;
        toolbarTitle = TOOLBAR_TITLE_FRAGMENT;
        setToolbarEditAndDeleteIcon(showEditAndDeleteIconInToolbar);
        testAdapter.setEditDeleteToolbarActive(false);
    }

    private void initializeRvAdapter() {
        List<TestAdapter.Item> data = new ArrayList<>();

//        data.add(new TestAdapter.Item(TestAdapter.HEADER, "Fruits"));
//        data.add(new TestAdapter.Item(TestAdapter.CHILD, "Apple"));
//        data.add(new TestAdapter.Item(TestAdapter.CHILD, "Orange"));
//        data.add(new TestAdapter.Item(TestAdapter.CHILD, "Banana"));
//        data.add(new TestAdapter.Item(TestAdapter.HEADER, "Cars"));
//        data.add(new TestAdapter.Item(TestAdapter.CHILD, "Audi"));
//        data.add(new TestAdapter.Item(TestAdapter.CHILD, "Aston Martin"));
//        data.add(new TestAdapter.Item(TestAdapter.CHILD, "BMW"));
//        data.add(new TestAdapter.Item(TestAdapter.CHILD, "Cadillac"));
//
//        TestAdapter.Item places = new TestAdapter.Item(TestAdapter.HEADER, "Places");
//        places.invisibleChildren = new ArrayList<>();
//        places.invisibleChildren.add(new TestAdapter.Item(TestAdapter.CHILD, "Kerala"));
//        places.invisibleChildren.add(new TestAdapter.Item(TestAdapter.CHILD, "Tamil Nadu"));
//        places.invisibleChildren.add(new TestAdapter.Item(TestAdapter.CHILD, "Karnataka"));
//        places.invisibleChildren.add(new TestAdapter.Item(TestAdapter.CHILD, "Maharashtra"));
//        data.add(places);

        //make List with only Headers
        List<Object> onlyHeadersList = new ArrayList<>();
        for (ShoppingTrip shoppingTrip : historicShoppingTripsList) {
            onlyHeadersList.add(shoppingTrip);
        }

        //make List with ShoppingTrip Objects and referring ProductItemObjects
        List<Object> parentAndChildrenList = new ArrayList<>();
        for (ShoppingTrip shoppingTrip : historicShoppingTripsList) {
            parentAndChildrenList.add(shoppingTrip);
            for (ProductItem productItem : shoppingTrip.getBoughtProducts()) {
                parentAndChildrenList.add(productItem);
            }
        }

        //Adapter setup
        testAdapter = new TestAdapter(context, data, parentAndChildrenList);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        testRv.setLayoutManager(linearLayoutManager);
        testRv.setAdapter(testAdapter);
        testRv.setHasFixedSize(true);
        testRv.setVisibility(View.VISIBLE);
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
