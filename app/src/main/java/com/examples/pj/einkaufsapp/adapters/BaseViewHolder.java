package com.examples.pj.einkaufsapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

/**
 * Helper for Adapters to set up the recycler view
 */
public abstract class BaseViewHolder extends RecyclerView.ViewHolder {

    /**
     * constructor
     *
     * @param itemView
     */
    public BaseViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}