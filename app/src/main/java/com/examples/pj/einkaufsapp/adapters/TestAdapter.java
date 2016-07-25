package com.examples.pj.einkaufsapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.examples.pj.einkaufsapp.R;
import com.examples.pj.einkaufsapp.dbentities.ProductItem;
import com.examples.pj.einkaufsapp.dbentities.ShoppingTrip;

import java.util.List;

import butterknife.ButterKnife;

public class TestAdapter extends BaseAdapter<TestAdapter.ListHeaderViewHolder> {

    public static final int HEADER = 0;
    public static final int CHILD = 1;

    private List<Item> data;
    private List<Object> parentAndChildrenListOriginal;
    private Context context;
    private boolean editDeleteToolbarActive;

    public TestAdapter(Context context, List<Item> data, List<Object> parentAndChildrenList) {
        this.data = data;
        this.context = context;
        this.parentAndChildrenListOriginal = parentAndChildrenList;
    }

    //---------------------------------------------------------------
    // Create new views (invoked by the layout manager)
    //---------------------------------------------------------------

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View view = null;
        float dp = context.getResources().getDisplayMetrics().density;
        int subItemPaddingLeft = (int) (18 * dp);
        int subItemPaddingTopAndBottom = (int) (5 * dp);
        switch (type) {
            case HEADER:
                LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.list_header, parent, false);
                ListHeaderViewHolder header = new ListHeaderViewHolder(view);
                return header;
            case CHILD:
                TextView itemTextView = new TextView(context);
                itemTextView.setPadding(subItemPaddingLeft, subItemPaddingTopAndBottom, 0, subItemPaddingTopAndBottom);
                itemTextView.setTextColor(0x88000000);
                itemTextView.setLayoutParams(
                        new ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));
                return new RecyclerView.ViewHolder(itemTextView) {
                };
        }
        return null;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (!parentAndChildrenListOriginal.isEmpty()) {
            //get all List Data (Shopping Trips AND ProductItems)
            final Object object = parentAndChildrenListOriginal.get(position);
            //current position is header object -> ShoppingTrip
            if (object instanceof ShoppingTrip) {
                final ListHeaderViewHolder viewHolder = (ListHeaderViewHolder) holder;
                final ShoppingTrip item = (ShoppingTrip) object;
                viewHolder.refferalItem = item; //current ShoppingTrip
                viewHolder.header_title.setText("Einkauf vom " + item.getDateCompleted());

                if (!viewHolder.isExpanded) { //if there are ProductItem Entries for current ShoppingTrip
                    viewHolder.btn_expand_toggle.setImageResource(R.drawable.circle_plus);
                } else {
                    viewHolder.btn_expand_toggle.setImageResource(R.drawable.circle_minus);
                }
                //On Click Behaviour
                viewHolder.btn_expand_toggle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewHolder.isExpanded = !viewHolder.isExpanded; //toggle expanded status

                        //if view is now expanded
                        if (viewHolder.isExpanded) {
                            viewHolder.btn_expand_toggle.setImageResource(R.drawable.circle_minus);

                            int count = 0;
                            int pos = parentAndChildrenListOriginal.indexOf(viewHolder.refferalItem);
                            //for all ProductItems of current ShoppingTrip
                            while (parentAndChildrenListOriginal.size() > pos + 1 && parentAndChildrenListOriginal.get(pos + 1 + count) instanceof ProductItem) {
                                Object currentObject = parentAndChildrenListOriginal.get(pos + 1 + count);
                                ProductItem currentProductItem = (ProductItem) currentObject;
                                currentProductItem.setInvisible(false);
                                count++;
                            }
//                            notifyItemRangeRemoved(pos + 1, count);
                        } else {    //if view is now not expanded
                            viewHolder.btn_expand_toggle.setImageResource(R.drawable.circle_plus);
                            int count = 0;
                            int pos = parentAndChildrenListOriginal.indexOf(viewHolder.refferalItem);
                            //for all ProductItems of current ShoppingTrip
                            while (parentAndChildrenListOriginal.size() > pos + 1 && parentAndChildrenListOriginal.get(pos + 1 + count) instanceof ProductItem) {
                                Object currentObject = parentAndChildrenListOriginal.get(pos + 1 + count);
                                ProductItem currentProductItem = (ProductItem) currentObject;
                                currentProductItem.setInvisible(true);
                                count++;
                            }
                        }
//                            notifyItemRangeRemoved(pos + 1, count);
                        notifyDataSetChanged();
                    }
                });
            } else {
                //current Position is child object -> ProductItem
                TextView itemTextView = (TextView) holder.itemView;
                ProductItem productItem = (ProductItem) parentAndChildrenListOriginal.get(position);
                if (!productItem.isInvisible()) {
                    itemTextView.setVisibility(View.GONE);
                } else {
                    itemTextView.setVisibility(View.VISIBLE);
                    itemTextView.setText(productItem.getProduct() + ", Visibility: " + productItem.isInvisible());
                }
            }
        } else {
            //if there are no shopping list entries at all:
            Toast.makeText(context, "Es sind noch keine Einkäufe abgeschlossen worden", Toast.LENGTH_SHORT).show();
        }
    }
//
//        final Item item = data.get(position);
//        switch (item.type) {
//            case HEADER:
//                final ListHeaderViewHolder itemController = (ListHeaderViewHolder) holder;
//                itemController.refferalItem = item;
//                itemController.header_title.setText(item.text);
//                if (item.invisibleChildren == null) {
//                    itemController.btn_expand_toggle.setImageResource(R.drawable.circle_minus);
//                } else {
//                    itemController.btn_expand_toggle.setImageResource(R.drawable.circle_plus);
//                }
//                itemController.btn_expand_toggle.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (item.invisibleChildren == null) {
//                            item.invisibleChildren = new ArrayList<Item>();
//                            int count = 0;
//                            int pos = data.indexOf(itemController.refferalItem);
//                            while (data.size() > pos + 1 && data.get(pos + 1).type == CHILD) {
//                                item.invisibleChildren.add(data.remove(pos + 1));
//                                count++;
//                            }
//                            notifyItemRangeRemoved(pos + 1, count);
//                            itemController.btn_expand_toggle.setImageResource(R.drawable.circle_plus);
//                        } else {
//                            int pos = data.indexOf(itemController.refferalItem);
//                            int index = pos + 1;
//                            for (Item i : item.invisibleChildren) {
//                                data.add(index, i);
//                                index++;
//                            }
//                            notifyItemRangeInserted(pos + 1, index - pos - 1);
//                            itemController.btn_expand_toggle.setImageResource(R.drawable.circle_minus);
//                            item.invisibleChildren = null;
//                        }
//                    }
//                });
//                break;
//            case CHILD:
//                TextView itemTextView = (TextView) holder.itemView;
//                itemTextView.setText(data.get(position).text);
//                break;
//        }

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
//        return data.size();
    }

//---------------------------------------------------------------
// INNER CLASSES
//---------------------------------------------------------------

    public static class ListHeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView header_title;
        public ImageView btn_expand_toggle;
        //        public Item refferalItem;
        public Object refferalItem;
        public boolean isExpanded;
        public List<ProductItem> invisibleChildrenForShoppingTrip;

        public ListHeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            header_title = (TextView) itemView.findViewById(R.id.header_title);
            btn_expand_toggle = (ImageView) itemView.findViewById(R.id.btn_expand_toggle);
        }
    }

    public static class Item {
        public int type;
        public String text;
        public List<Item> invisibleChildren;

        public Item() {
        }

        public Item(int type, String text) {
            this.type = type;
            this.text = text;
        }
    }
    //---------------------------------------------------------------
    // GETTER AND SETTER
    //---------------------------------------------------------------

    public void setEditDeleteToolbarActive(boolean editDeleteToolbarActive) {
        this.editDeleteToolbarActive = editDeleteToolbarActive;
    }
}