package com.examples.pj.einkaufsapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.examples.pj.einkaufsapp.R;
import com.examples.pj.einkaufsapp.dbentities.ShoppingTrip;
import com.examples.pj.einkaufsapp.interfaces.ChangeToolbarInterface;
import com.examples.pj.einkaufsapp.util.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HistoricListsAdapter extends BaseAdapter<HistoricListsAdapter.ArraylistViewHolder> {
    public static final String LOG_TAG = HistoricListsAdapter.class.getSimpleName();

    private static final int LIST_ITEMS = 0;
    private static final int BOTTOM_ELEMENT = 1;

    Context context;
    SharedPreferencesManager sharedPreferencesManager;
    ChangeToolbarInterface changeToolbarInterface;
    List<ShoppingTrip> historicShoppingTripsList;
    private ShoppingTrip itemClicked;
    private boolean editDeleteToolbarActive;

    public HistoricListsAdapter(Context context, SharedPreferencesManager sharedPreferencesManager, ChangeToolbarInterface changeToolbarInterface, List<ShoppingTrip> historicShoppingTripsList, boolean editDeleteToolbarActive) {
        this.context = context;
        this.sharedPreferencesManager = sharedPreferencesManager;
        this.changeToolbarInterface = changeToolbarInterface;
        this.historicShoppingTripsList = historicShoppingTripsList;
        this.editDeleteToolbarActive = editDeleteToolbarActive;
    }

    //---------------------------------------------------------------
    // Create new views (invoked by the layout manager)
    //---------------------------------------------------------------
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == LIST_ITEMS) {
            return new ArraylistViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item_historiclist, parent, false));
        } else {
            return new BottomElementViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_segment_historiclistadapter_bottom, parent, false));
        }
    }

    //---------------------------------------------------------------
    // Replace the contents of a view (invoked by the layout manager)
    //---------------------------------------------------------------
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);

        if (historicShoppingTripsList != null) {
            historicShoppingTripsList.clear();
            historicShoppingTripsList = sharedPreferencesManager.loadHistoricShoppingTripsListFromLocalStore();
        } else {
            historicShoppingTripsList = new ArrayList<>();
        }

        if (viewType == LIST_ITEMS) {
            ArraylistViewHolder viewHolder = (ArraylistViewHolder) holder;
            ShoppingTrip trip = historicShoppingTripsList.get(position);
            int numberProducts = trip.getBoughtProducts().size();


            viewHolder.shoppingTripDateTv.setText(trip.getDateCompleted());
            viewHolder.shoppingTripNumberProductsTv.setText("Anzahl gekaufter Produkte: " + numberProducts);

        } else {
            BottomElementViewHolder viewHolder = (BottomElementViewHolder) holder;
            viewHolder.noShoppingTripsInListTv.setVisibility((historicShoppingTripsList.size() == 0) ? View.VISIBLE : View.GONE);
        }
    }

    //---------------------------------------------------------------
    // ADAPTER METHODS
    //---------------------------------------------------------------

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (historicShoppingTripsList != null) {
            return historicShoppingTripsList.size() + 1; //plus one for bottom element
        } else {
            return 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        int viewType;
        if (historicShoppingTripsList != null) {
            if (position < historicShoppingTripsList.size()) {
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
        @Bind(R.id.historic_shopping_trip_date)
        TextView shoppingTripDateTv;
        @Bind(R.id.historic_shopping_trip_items)
        TextView shoppingTripNumberProductsTv;

        /**
         * Constructor
         *
         * @param view
         */
        public ArraylistViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    /**
     * ViewHolder for TextView and Button below RecyclerView
     */
    public class BottomElementViewHolder extends BaseViewHolder {
        @Bind(R.id.historiclist_noshoppingtripsinlist_tv)
        TextView noShoppingTripsInListTv;

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
    // GETTER AND SETTER
    //---------------------------------------------------------------

    public ShoppingTrip getItemClicked() {
        return itemClicked;
    }

    public void setItemClicked(ShoppingTrip itemClicked) {
        this.itemClicked = itemClicked;
    }

    public void setEditDeleteToolbarActive(boolean editDeleteToolbarActive) {
        this.editDeleteToolbarActive = editDeleteToolbarActive;
    }
}
