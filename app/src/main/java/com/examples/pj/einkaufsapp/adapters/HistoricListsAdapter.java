package com.examples.pj.einkaufsapp.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.examples.pj.einkaufsapp.R;
import com.examples.pj.einkaufsapp.dbentities.ProductItem;
import com.examples.pj.einkaufsapp.dbentities.ShoppingTrip;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HistoricListsAdapter extends BaseAdapter<HistoricListsAdapter.ListHeaderViewHolder> {

    public static final int HEADER = 0;
    public static final int CHILD = 1;

    private List<Object> parentAndChildrenListOriginal;
    private Context context;
    private boolean editDeleteToolbarActive;

    public HistoricListsAdapter(Context context, List<Object> parentAndChildrenList) {
        this.context = context;
        this.parentAndChildrenListOriginal = parentAndChildrenList;
    }

    //---------------------------------------------------------------
    // Create new views (invoked by the layout manager)
    //---------------------------------------------------------------

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View view = null;
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        switch (type) {
            case HEADER:
                view = inflater.inflate(R.layout.list_header, parent, false);
                ListHeaderViewHolder header = new ListHeaderViewHolder(view);
                return header;
            case CHILD:
                view = inflater.inflate(R.layout.list_child, parent, false);
                ListChildViewHolder child = new ListChildViewHolder(view);
                return child;
        }
        return null;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (!parentAndChildrenListOriginal.isEmpty()) {
            //get all List Data (Shopping Trips AND ProductItems)
            final Object object = parentAndChildrenListOriginal.get(position);

            //current position is header object -> ShoppingTrip
            if (object instanceof ShoppingTrip) {
                final ListHeaderViewHolder parentViewHolder = (ListHeaderViewHolder) holder;
                final ShoppingTrip item = (ShoppingTrip) object;
                parentViewHolder.header_title.setText(context.getResources().getString(R.string.header_title_date, item.getDateCompleted()));

                //On Click Behaviour of plus minus Icon
                parentViewHolder.headerContainerLl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println("Entered OnClickListener");
                        item.setExpanded(!item.isExpanded()); //toggle expanded status

                        //if view is now expanded
                        if (item.isExpanded()) {
                            parentViewHolder.btn_expand_toggle.setImageResource(R.drawable.circle_minus);
                            parentViewHolder.headerContainerLl.setBackgroundColor(ContextCompat.getColor(context, R.color.light_purple));
                            parentViewHolder.header_title.setTextColor(ContextCompat.getColor(context, R.color.black));
                            int pos = parentAndChildrenListOriginal.indexOf(item);
                            //for all ProductItems of current ShoppingTrip
                            while (parentAndChildrenListOriginal.size() > pos + 1 && parentAndChildrenListOriginal.get(pos + 1) instanceof ProductItem) {
                                pos++;
                                Object currentObject = parentAndChildrenListOriginal.get(pos);
                                ProductItem currentProductItem = (ProductItem) currentObject;
                                currentProductItem.setInvisible(false);
                                ((ProductItem) parentAndChildrenListOriginal.get(pos)).setInvisible(false);
                                notifyItemChanged(pos);
                            }
                        } else {    //if view is now not expanded
                            parentViewHolder.btn_expand_toggle.setImageResource(R.drawable.circle_plus);
                            parentViewHolder.headerContainerLl.setBackgroundColor(ContextCompat.getColor(context, R.color.grey));
                            parentViewHolder.header_title.setTextColor(ContextCompat.getColor(context, R.color.grey_dark));

                            int pos = parentAndChildrenListOriginal.indexOf(item);
                            //for all ProductItems of current ShoppingTrip
                            while (parentAndChildrenListOriginal.size() > pos + 1 && parentAndChildrenListOriginal.get(pos + 1) instanceof ProductItem) {
                                pos++;
                                Object currentObject = parentAndChildrenListOriginal.get(pos);
                                ProductItem currentProductItem = (ProductItem) currentObject;
                                currentProductItem.setInvisible(true);
                                ProductItem productItem = (ProductItem) parentAndChildrenListOriginal.get(pos);
                                productItem.setInvisible(true);
                                notifyItemChanged(pos);
                            }
                        }
                    }
                });
            } else {
                final ListChildViewHolder childViewHolder = (ListChildViewHolder) holder;
                //current Position is child object -> ProductItem
                ProductItem productItem = (ProductItem) parentAndChildrenListOriginal.get(position);
                if (productItem.isInvisible()) {
                    childViewHolder.listChildTv.setVisibility(View.GONE);
                } else {
                    childViewHolder.listChildTv.setVisibility(View.VISIBLE);
                    childViewHolder.listChildTv.setText(productItem.getProduct() + ", Is Invisible: " + productItem.isInvisible());
                }
            }
        } else {
            //if there are no shopping list entries at all:
            Toast.makeText(context, "Es sind noch keine Einkäufe abgeschlossen worden", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (parentAndChildrenListOriginal.get(position) instanceof ShoppingTrip) {
            return HEADER;
        } else {
            return CHILD;
        }
    }

    @Override
    public int getItemCount() {
        return parentAndChildrenListOriginal.size();
    }

//---------------------------------------------------------------
// INNER CLASSES
//---------------------------------------------------------------

    public static class ListHeaderViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.header_title)
        TextView header_title;
        @Bind(R.id.btn_expand_toggle)
        ImageView btn_expand_toggle;
        @Bind(R.id.historiclist_header_container)
        LinearLayout headerContainerLl;

        public ListHeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class ListChildViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.list_child_container)
        LinearLayout listChildLl;
        @Bind(R.id.list_child_text_tv)
        TextView listChildTv;

        public ListChildViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    //---------------------------------------------------------------
    // GETTER AND SETTER
    //---------------------------------------------------------------

    public void setEditDeleteToolbarActive(boolean editDeleteToolbarActive) {
        this.editDeleteToolbarActive = editDeleteToolbarActive;
    }
}