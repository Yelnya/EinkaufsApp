package com.examples.pj.einkaufsapp.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.examples.pj.einkaufsapp.R;
import com.examples.pj.einkaufsapp.dbentities.ProductItem;
import com.examples.pj.einkaufsapp.dbentities.ShoppingTrip;

import java.util.List;

public class HistoricListsAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<ShoppingTrip> historicShoppingTrips;

    public HistoricListsAdapter(Context context, List<ShoppingTrip> historicShoppingTrips) {
        this.context = context;
        this.historicShoppingTrips = historicShoppingTrips;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isLastChild, View view, ViewGroup parent) {
        ShoppingTrip shoppingTrip = (ShoppingTrip) getGroup(groupPosition);
        if (view == null) {
            LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inf.inflate(R.layout.list_header, parent, false);
        }
        final TextView heading = (TextView) view.findViewById(R.id.header_title);
        final LinearLayout headingLl = (LinearLayout) view.findViewById(R.id.historiclist_header_container);
        heading.setText("Einkauf vom " + shoppingTrip.getDateCompleted().trim());

        headingLl.setBackgroundColor(ContextCompat.getColor(context, shoppingTrip.isExpanded() ? R.color.light_purple : R.color.grey));
        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {
        ProductItem productItem = (ProductItem) getChild(groupPosition, childPosition);
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_child, parent, false);
        }
        final TextView childItem = (TextView) view.findViewById(R.id.list_child_text_tv);
        final LinearLayout childItemLl = (LinearLayout) view.findViewById(R.id.list_child_container);
        childItem.setText(productItem.getProduct().trim());

        childItemLl.setBackgroundColor(ContextCompat.getColor(context, productItem.isCurrentClicked() ? R.color.cheese : R.color.grey));
        return view;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        List<ProductItem> productItemList = historicShoppingTrips.get(groupPosition).getBoughtProductsList();
        return productItemList.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        List<ProductItem> productItemList = historicShoppingTrips.get(groupPosition).getBoughtProductsList();
        return productItemList.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return historicShoppingTrips.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return historicShoppingTrips.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
