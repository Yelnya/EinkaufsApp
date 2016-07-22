package com.examples.pj.einkaufsapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.examples.pj.einkaufsapp.R;
import com.examples.pj.einkaufsapp.dbentities.ProductItem;
import com.examples.pj.einkaufsapp.dbentities.ShoppingTrip;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class TestAdapter extends BaseAdapter<TestAdapter.ListHeaderViewHolder> {

    public static final int HEADER = 0;
    public static final int CHILD = 1;

    private List<Item> data;
    private List<Object> parentAndChildrenList;
    public List<Object> invisibleChildren;
    private Context context;
    private boolean editDeleteToolbarActive;

    public TestAdapter(Context context, List<Item> data, List<Object> parentAndChildrenList) {
        this.data = data;
        this.context = context;
        this.parentAndChildrenList = parentAndChildrenList;
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

        //current position header object
        final Object object = parentAndChildrenList.get(position);
        if (object instanceof ShoppingTrip) {
            final ListHeaderViewHolder viewHolder = (ListHeaderViewHolder) holder;
            //get header object
            ShoppingTrip item = (ShoppingTrip) object;
            //treat invisibleChildrenList
                if (invisibleChildren == null) {
                    invisibleChildren = new ArrayList<>();
                }
                if (!invisibleChildren.isEmpty()) {
                    invisibleChildren.clear();
                }

//            for (ProductItem boughtItem : item.getBoughtProducts()) {
//                invisibleChildren.add(boughtItem);
//            }
            viewHolder.refferalItem = item;
            viewHolder.header_title.setText("Einkauf vom " + item.getDateCompleted());
            if (invisibleChildren != null) {
                viewHolder.btn_expand_toggle.setImageResource(R.drawable.circle_minus);
            } else {
                viewHolder.btn_expand_toggle.setImageResource(R.drawable.circle_plus);
            }
            viewHolder.btn_expand_toggle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (invisibleChildren == null) {
                        invisibleChildren = new ArrayList<>();
                        int count = 0;
                        int pos = parentAndChildrenList.indexOf(viewHolder.refferalItem);
                        while (parentAndChildrenList.size() > pos + 1 && parentAndChildrenList.get(pos + 1) instanceof ProductItem) {
                            invisibleChildren.add(parentAndChildrenList.remove(pos + 1));
                            count++;
                        }
                        notifyItemRangeRemoved(pos + 1, count);
                        viewHolder.btn_expand_toggle.setImageResource(R.drawable.circle_plus);
                    } else {
                        int pos = parentAndChildrenList.indexOf(viewHolder.refferalItem);
                        int index = pos + 1;
                        for (Object i : invisibleChildren) {
                            parentAndChildrenList.add(index, i);
                            index++;
                        }
                        notifyItemRangeInserted(pos + 1, index - pos - 1);
                        viewHolder.btn_expand_toggle.setImageResource(R.drawable.circle_minus);
                        invisibleChildren = null;
                    }
                }
            });
        } else {
            //current Position child object
            TextView itemTextView = (TextView) holder.itemView;
            ProductItem productItem = (ProductItem) parentAndChildrenList.get(position);
            itemTextView.setText(productItem.getProduct());
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
        if (parentAndChildrenList.get(position) instanceof ShoppingTrip) {
            return HEADER;
        } else {
            return CHILD;
        }
    }

    @Override
    public int getItemCount() {
        return parentAndChildrenList.size();
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