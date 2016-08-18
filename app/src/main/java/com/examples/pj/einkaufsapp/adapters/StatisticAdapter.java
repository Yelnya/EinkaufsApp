package com.examples.pj.einkaufsapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.examples.pj.einkaufsapp.R;
import com.examples.pj.einkaufsapp.dbentities.ProductItem;
import com.examples.pj.einkaufsapp.interfaces.ChangeToolbarInterface;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Adapter for CurrentListFragment
 */
public class StatisticAdapter extends BaseAdapter<StatisticAdapter.ArraylistViewHolder> {

    public static final String LOG_TAG = StatisticAdapter.class.getSimpleName();

    private static final int LIST_ITEMS = 0;
    private static final int BOTTOM_ELEMENT = 1;

    private final Context context;
    Activity contextActivity;
    private List<ProductItem> generalList;
    private int numberHighestBought;
    protected List<ProductItem> productsToAddToCurrentList;
    ChangeToolbarInterface changeToolbarInterface;

    /**
     * Constructor
     *
     * @param context     from MainActivity
     * @param generalList currentList from Fragment
     */
    public StatisticAdapter(List<ProductItem> generalList, Context context, ChangeToolbarInterface changeToolbarInterface, int numberHighestBought) {
        this.generalList = generalList;
        this.context = context;
        this.changeToolbarInterface = changeToolbarInterface;
        this.numberHighestBought = numberHighestBought;
        contextActivity = (Activity) context;
        productsToAddToCurrentList = new ArrayList<>();
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

            String bought = String.valueOf(item.getBought());
            if (item.getBought() < numberHighestBought / 2) {
                //TEXT RIGHT OF BAR
                viewHolder.productPercentInvisibleTv.setText(" " + item.getProduct() + " (" + bought + ")");
                runAnimationTextViews(viewHolder.productPercentInvisibleTv);
            } else {
                //TEXT LEFT OF BAR
                viewHolder.productPercentTv.setText(item.getProduct() + " (" + bought + ")" + " ");
                runAnimationTextViews(viewHolder.productPercentTv);
            }
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

//        List<ProductItem> productsToAddToCurrentList = new ArrayList<>();

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
        @OnClick(R.id.item_container)
        public void onProductClick() {
            int pos = getAdapterPosition();
            ProductItem product = generalList.get(pos);

            if (productsToAddToCurrentList.size() > 0) {
                boolean foundInList = false;
                for (ProductItem productItem : productsToAddToCurrentList) {
                    if (productItem.getId() == product.getId()) {
                        foundInList = true;
                        break;
                    }
                }
                if (foundInList) {
                    productPercentTv.setBackgroundColor(ContextCompat.getColor(context, R.color.dark_purple));
                    //if in list, remove
                    List<ProductItem> copyList = new ArrayList<>();
                    copyList.addAll(productsToAddToCurrentList);
                    productsToAddToCurrentList.clear();
                    for (ProductItem productItem : copyList) {
                        if (product.getId() != productItem.getId()) {
                            productsToAddToCurrentList.add(productItem);
                        }
                    }
                    //if not in list, add
                } else {
                    productPercentTv.setBackgroundColor(ContextCompat.getColor(context, R.color.purple));
                    productsToAddToCurrentList.add(product);
                }
            } else {
                productPercentTv.setBackgroundColor(ContextCompat.getColor(context, R.color.purple));
                productsToAddToCurrentList.add(product);
            }

            //output
//            System.out.println("------ PRODUCTS IN LIST TO ADD --------");
//            for (ProductItem productItem : productsToAddToCurrentList) {
//                System.out.println(productItem.getProduct());
//            }
            //Show Shopping Cart in Toolbar if > 0 products in productsToAddToCurrentList, otherwise hide
            changeToolbarInterface.showShoppingCartIcon(productsToAddToCurrentList.size() > 0);
            changeToolbarInterface.handOverProductsToAddToCurrentShoppingList(productsToAddToCurrentList);

            //TODO: Click on Shopping Cart should place items from productsToAddToCurrentList into Current Fragment

        }
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

    private void runAnimationTextViews(TextView tv) {
        Animation a = AnimationUtils.loadAnimation(context, R.anim.scale);
        a.reset();
        tv.clearAnimation();
        tv.startAnimation(a);

    }


}
