package com.examples.pj.einkaufsapp.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.examples.pj.einkaufsapp.R;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import butterknife.ButterKnife;

/**
 * Class as Container for Test Information
 */
public class TestFragment extends BaseFragment {
    public static final String LOG_TAG = TestFragment.class.getSimpleName();

    Context context;
    private boolean showEditAndDeleteIconInToolbar;
    private boolean showShoppingCartIconInToolbar;
    private static final String TOOLBAR_TITLE = "Test";

    private View mChart;
    private String[] mMonth = new String[]{
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        ButterKnife.bind(this, view);
        setToolbar();
        context = super.getActivity();

        openChart(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        showShoppingCartIconInToolbar = false;
        showEditAndDeleteIconInToolbar = false;
        setToolbarEditAndDeleteIcon(showEditAndDeleteIconInToolbar);
    }

    private void openChart(View view) {
        int[] x = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        int[] income = {2000, 2500, 2700, 3000, 2800, 3500, 3700, 4500, 0, 0, 0, 0};
//        int[] expense = {2200, 2700, 2900, 2800, 2600, 3000, 3300, 3400, 0, 0, 0, 0};

        //Creating an XYSeries for Income
        XYSeries incomeSeries = new XYSeries("Income");
        //Creating an XYSeries for Expense
        XYSeries expenseSeries = new XYSeries("Expense");
        //Adding data to Income and Expense Series
        for (int i = 0; i < x.length; i++) {
            incomeSeries.add(i, income[i]);
//            expenseSeries.add(i, expense[i]);
        }

        //creating a dataset to hold each series
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(incomeSeries); //adding income series t the dataset
//        dataset.addSeries(expenseSeries); //adding expense series to dataset

        //creating XYSeriesRenderer to customize incomeSeries
        XYSeriesRenderer incomeRenderer = new XYSeriesRenderer();
        incomeRenderer.setColor(Color.CYAN);    //color of the graph set to cyan
        incomeRenderer.setFillPoints(true);
        incomeRenderer.setLineWidth(2);
        incomeRenderer.setDisplayChartValues(true);
        incomeRenderer.setDisplayChartValuesDistance(10);   //setting chart value distance

        //creating XYSeriesRenderer to customize expenseSeries
//        XYSeriesRenderer expenseRenderer = new XYSeriesRenderer();
//        expenseRenderer.setColor(Color.GREEN);
//        expenseRenderer.setFillPoints(true);
//        expenseRenderer.setLineWidth(2);
//        expenseRenderer.setDisplayChartValues(true);

        //creating a XYMultipleSeriesRenderer to customize the whole chart
        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
        multiRenderer.setOrientation(XYMultipleSeriesRenderer.Orientation.VERTICAL);  //how the bars are displayed
        multiRenderer.setXLabels(0);
        multiRenderer.setChartTitle("Income vx Expense Chart");
//        multiRenderer.setXTitle("Year 2014");
        multiRenderer.setYTitle("Amount in Dollars");

        /***
         * Customizing graphs
         */

        multiRenderer.setChartTitleTextSize(56);    //text size of title
        multiRenderer.setAxisTitleTextSize(48);     //setting text size of the axis title
        multiRenderer.setLabelsTextSize(48);        //setting text size of the graph lable
        multiRenderer.setZoomButtonsVisible(false); //setting zomm buttons visibility
        multiRenderer.setPanEnabled(false, false);  //setting pan enality which uses graph to move on both axis
        multiRenderer.setClickEnabled(false);       //setting click false on graph
        multiRenderer.setZoomEnabled(false, false); //setting zoom to false on both axis
        multiRenderer.setShowGridY(false);          //setting lines to display on y axis
        multiRenderer.setShowGridX(false);          //setting lines to display on x axis
        multiRenderer.setFitLegend(true);           //setting legend to fit the screen size
        multiRenderer.setShowGrid(false);           //setting displaying line on grid
        multiRenderer.setZoomEnabled(false);        //setting zoom to false
        multiRenderer.setExternalZoomEnabled(false); //setting external zoom functions to false
        multiRenderer.setAntialiasing(true);        //setting displaying lines on graph to be formatted (like using graphics)
        multiRenderer.setInScroll(false);           //setting to in scroll to false
        multiRenderer.setLegendHeight(30);          //setting to set legend height of the graph
        multiRenderer.setXLabelsAlign(Paint.Align.CENTER);  //setting x axis label align
        multiRenderer.setYLabelsAlign(Paint.Align.LEFT);  //setting y axis label align
        multiRenderer.setTextTypeface("sans_serif", Typeface.NORMAL);   //setting text style
        multiRenderer.setYLabels(10);               //setting no of values to display in y axis
        multiRenderer.setYAxisMax(5000);            //setting y axis max value (static or dynamic)
        multiRenderer.setXAxisMin(-0.5);            //setting used to move the graph on x axis to .5 to the right
        multiRenderer.setXAxisMax(11);              //setting max values to be displayed in x axis
        multiRenderer.setBarSpacing(0.5);           //setting bar size or space between two bars
        multiRenderer.setBackgroundColor(Color.TRANSPARENT);    //setting background color of graph
        multiRenderer.setMarginsColor(getResources().getColor(R.color.transparent_background)); //setting margin color of the graph
        multiRenderer.setApplyBackgroundColor(true);

        //setting the margin size for the graph in the order top, left, bottom, right
        multiRenderer.setMargins(new int[]{30, 30, 30, 30});
        for (int i = 0; i < x.length; i++) {
            multiRenderer.addXTextLabel(i, mMonth[i]);
        }

        //adding incomeRenderer and expenseRenderer to multipleRenderer -> the order of adding dataseries to dataset and renderers to multipleRenderer should be the same
        multiRenderer.addSeriesRenderer(incomeRenderer);
//        multiRenderer.addSeriesRenderer(expenseRenderer);

        //this part is used to display graph on the xml
        LinearLayout chartContainer = (LinearLayout) view.findViewById(R.id.chart);
        //remove any views before u paint the chart
        chartContainer.removeAllViews();
        //drawing bar chart
        mChart = ChartFactory.getBarChartView(context, dataset, multiRenderer, BarChart.Type.DEFAULT);
        //adding the view to the linearlayout
        chartContainer.addView(mChart);
    }
}