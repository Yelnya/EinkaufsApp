package com.examples.pj.einkaufsapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.examples.pj.einkaufsapp.R;
import com.examples.pj.einkaufsapp.dbentities.ProductItem;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Adapter for CurrentListFragment
 */
public class StatisticAdapter extends BaseAdapter<StatisticAdapter.ArraylistViewHolder> {

    public static final String LOG_TAG = StatisticAdapter.class.getSimpleName();

    private static final int LIST_ITEMS = 0;
    private static final int BOTTOM_ELEMENT = 1;

    private final Context context;
    Activity contextActivity;
    private boolean editDeleteToolbarActive;
    private List<ProductItem> generalList;
    private int numberHighestBought;
    float dpWidthOfScreen;
    int pixelWidthOfScreen;

    /**
     * Constructor
     *
     * @param context                  from MainActivity
     * @param generalList              currentList from Fragment
     */
    public StatisticAdapter(List<ProductItem> generalList, Context context, int numberHighestBought) {
        this.generalList = generalList;
        this.context = context;
        this.numberHighestBought = numberHighestBought;
        contextActivity = (Activity) context;

        //Window size width with and without padding
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        pixelWidthOfScreen = metrics.widthPixels;
        dpWidthOfScreen = convertPixelsToDp(pixelWidthOfScreen); //max width size
        System.out.println(pixelWidthOfScreen);
        System.out.println(dpWidthOfScreen);

    }

    //---------------------------------------------------------------
    // Create new views (invoked by the layout manager)
    //---------------------------------------------------------------
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == LIST_ITEMS) {
            return new ArraylistViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item_statistic, parent, false));
        } else {
            return new BottomElementViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_segment_statisticlist_bottom, parent, false));
        }
    }

    //---------------------------------------------------------------
    // Replace the contents of a view (invoked by the layout manager)
    //---------------------------------------------------------------
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);

        if (viewType == LIST_ITEMS) {
            ArraylistViewHolder viewHolder = (ArraylistViewHolder) holder;
            ProductItem item = generalList.get(position);

            //width of percent tv -> -1 constant stands for MATCH_PARENT
            //numberHighestBought = 100%
            int numberBought = item.getBought();
            float widthInvisible = (float) numberBought / numberHighestBought;
            float widthVisible = 1 - widthInvisible;

            LinearLayout.LayoutParams llPercentTvParams = new LinearLayout.LayoutParams(-1, -1, widthVisible);
            LinearLayout.LayoutParams llPercentInvisibleTvParams = new LinearLayout.LayoutParams(-1, -1, widthInvisible);

            viewHolder.productPercentTv.setLayoutParams(llPercentTvParams);
            viewHolder.productPercentInvisibleTv.setLayoutParams(llPercentInvisibleTvParams);

            //set and place Text on percentage bar
            //TODO offset of text is percentage of bar in relation to dpWidthOfScreenWithoutLeftPadding = 100%

            if (item.getBought() < numberHighestBought/2) {
                //TEXT RIGHT OF BAR
                int offset = Math.round(pixelWidthOfScreen * widthInvisible);
                viewHolder.productNameTv.setPadding(offset, -1, 0, -1);
            } else {
                //TEXT LEFT OF BAR
                int offset = Math.round(pixelWidthOfScreen * widthInvisible-500);
                viewHolder.productNameTv.setPadding(offset, -1, 0, -1);
            }


            String bought = String.valueOf(item.getBought());
            viewHolder.productNameTv.setText(item.getProduct() + " (" + bought + ")");


        } else {
            BottomElementViewHolder viewHolder = (BottomElementViewHolder) holder;

            if (generalList == null || generalList.isEmpty()) {
                viewHolder.noProductInListTv.setVisibility(View.VISIBLE);
            } else {
                viewHolder.noProductInListTv.setVisibility(View.GONE);
            }
        }
    }

    //---------------------------------------------------------------
    // ADAPTER METHODS
    //---------------------------------------------------------------

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (generalList != null) {
            return generalList.size() + 1; //plus one for bottom element
        } else {
            return 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        int viewType;
        if (generalList != null) {
            if (position < generalList.size()) {
                viewType = LIST_ITEMS; // as long as it is an array list
            } else {
                viewType = BOTTOM_ELEMENT; //if it is no array list anymore
            }
        } else {
            viewType = BOTTOM_ELEMENT;
        }
        return viewType;
    }

    //---------------------------------------------------------------
    // INNER CLASSES
    //---------------------------------------------------------------

    /**
     * ArrayListViewHolder
     */
    public class ArraylistViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.product_percent_tv)
        TextView productPercentTv;
        @Bind(R.id.product_percent_invisible_tv)
        TextView productPercentInvisibleTv;
        @Bind(R.id.product_name_tv)
        TextView productNameTv;

        /**
         * Constructor
         *
         * @param view
         */
        public ArraylistViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        /**
         * Simple Click Behaviour of Recycler View Item
         */
//        @OnClick(R.id.item_container)
//        public void onProductClick() {
//            int pos = getAdapterPosition();
//            ProductItem product = generalList.get(pos);
////            changeProductChecked(product);
//        }
    }

    /**
     * ViewHolder for TextView and Button below RecyclerView
     */
    public class BottomElementViewHolder extends BaseViewHolder {

        @Bind(R.id.generallist_noproductinlist_tv)
        TextView noProductInListTv;

        /**
         * Constructor
         *
         * @param view
         */
        public BottomElementViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    //---------------------------------------------------------------
    // OTHER METHODS
    //---------------------------------------------------------------

    public float convertPixelsToDp(float px){
        DisplayMetrics metrics = context.getResources().getSystem().getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return Math.round(dp);
    }


}
