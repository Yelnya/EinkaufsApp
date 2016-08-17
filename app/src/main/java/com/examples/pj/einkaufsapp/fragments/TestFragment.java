package com.examples.pj.einkaufsapp.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.examples.pj.einkaufsapp.R;
import com.examples.pj.einkaufsapp.dbentities.ProductItem;
import com.examples.pj.einkaufsapp.dbentities.ProductItemDataSource;
import com.examples.pj.einkaufsapp.formatters.MyValueFormatter;
import com.examples.pj.einkaufsapp.util.StringUtils;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Class as Container for Test Information
 */
public class TestFragment extends BaseFragment {
    public static final String LOG_TAG = TestFragment.class.getSimpleName();

    Context context;
    private String toolbarTitle = "";
    private static final String TOOLBAR_TITLE_FRAGMENT = "Statistik";
    private int maxBought;
    private boolean showEditAndDeleteIconInToolbar;
    private boolean showShoppingCartIconInToolbar;

    private ProductItemDataSource dataSource;
    private List<ProductItem> generalShoppingList;

    @Bind(R.id.chart)
    HorizontalBarChart mChart;

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
     * createInstance as container for bundle arguments
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
        getAttachedActivity().setToolbar(toolbar, true, toolbarTitle, showEditAndDeleteIconInToolbar, showShoppingCartIconInToolbar); //Icon displayed, Titel of Toolbar
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

        showShoppingCartIconInToolbar = false;
        showEditAndDeleteIconInToolbar = false;
        toolbarTitle = TOOLBAR_TITLE_FRAGMENT;
        context = this.getActivity();
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

        setToolbar();
        toolbarTv.setText(toolbarTitle);
        dataSource.open();  //open db connection

        generalShoppingList = new ArrayList<>();
        generalShoppingList = dataSource.getAllProductItems();

        if (generalShoppingList == null) {
            generalShoppingList = new ArrayList<>();
        }

        //sort List referring to max bought value
        //determine max bought = max x axis value
        generalShoppingList = sortListAfterBoughtValue(generalShoppingList);
        Collections.reverse(generalShoppingList);   //descending sort method
        //only the first 30 entries shall be shown, therefore the others are being cut
        List<ProductItem> cutList = new ArrayList<>();
        cutList.addAll(generalShoppingList);
        generalShoppingList.clear();
        int i = 1;
        for (ProductItem productItem : cutList) {
            if (i < 31) {
                generalShoppingList.add(productItem);
            }
            i++;
        }

        for (ProductItem productItem : generalShoppingList) {
            System.out.println("Product: " + productItem.getProduct() + ", bought: " + productItem.getBought());
        }


        //Draw Chart
        mChart.setDrawBarShadow(false);
        mChart.setDescription("");
        // if more than 31 entries are displayed in the chart, no values will be drawn
        mChart.setMaxVisibleValueCount(31);
        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);
        mChart.setDrawGridBackground(false);

        XAxis xl = mChart.getXAxis();
        xl.setEnabled(false);

        YAxis yl = mChart.getAxisLeft();
        yl.setDrawAxisLine(true);
        yl.setDrawGridLines(true);
        yl.setAxisMinValue(0f);

        YAxis yr = mChart.getAxisRight();
        yr.setDrawAxisLine(true);
        yr.setDrawGridLines(false);
        yr.setAxisMinValue(0f);


        //TODO: if bought is less than half of max bought, values are drawn OUTSIDE the bar
        maxBought = generalShoppingList.get(0).getBought();
//            if (maxBought / 2)
//
//                mChart.setDrawValueAboveBar(false); //where the value is drawn


        setData(50);

        mChart.setFitBars(true);
        mChart.animateY(1500);

//        Legend l = mChart.getLegend();
//        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
//        l.setFormSize(8f);
//        l.setXEntrySpace(4f);
    }


    //================================================================================
    // Other Methods
    //================================================================================

    private void setData(float range) {

        float barWidth = 0.9f;
        float spaceForBar = 0.1f;
        List<BarEntry> yVals1 = new ArrayList<BarEntry>();

        int i = 1;
        for (ProductItem productItem : generalShoppingList) {
            float val = (float) productItem.getBought();
            BarEntry bar = new BarEntry(i * spaceForBar * 10, val, productItem);
            yVals1.add(bar);
            i++;
        }
        BarDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setColor(Color.CYAN);
            MyValueFormatter formatter = new MyValueFormatter(generalShoppingList);
            set1.setValueFormatter(formatter);
            set1.setValues(yVals1);

            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "Gekaufte Produkte");

            MyValueFormatter formatter = new MyValueFormatter(generalShoppingList);
            set1.setValueFormatter(formatter);

            List<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            // TEST

//            HorizontalViewPortHandler horizontalViewPortHandler = new HorizontalViewPortHandler();
//
//            for (int y = 0; y < yVals1.size(); y++) {
//                float val = yVals1.get(y).getX();
//                ProductItem currentProductItem = (ProductItem) yVals1.get(y).getData();
//                if (currentProductItem.getBought() <= maxBought / 2) {
//                    Entry entry = (Entry) yVals1.get(y);
//                    String valueText = formatter.getFormattedValue(val, entry, 0, horizontalViewPortHandler);
//                    System.out.println(valueText);
//                    mChart.setDrawValueAboveBar(true);
//                } else {
////                    mChart.setDrawValueAboveBar(false);
//                }
//            }

            // TEST END

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setBarWidth(barWidth);
            mChart.setData(data);
        }
    }

    /**
     * method to sort list referring to bought value
     *
     * @param listToSort list
     * @return list
     */
    public List<ProductItem> sortListAfterBoughtValue(List<ProductItem> listToSort) {

        Collections.sort(listToSort, new StringUtils.GeneralListBoughtComparator());
        return listToSort;
    }
}
