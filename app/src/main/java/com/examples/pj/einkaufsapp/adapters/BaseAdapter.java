package com.examples.pj.einkaufsapp.adapters;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Base Adapter class
 *
 * @param <T>
 */
public abstract class BaseAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<T> dataset = new ArrayList<>();

    /**
     * delete existing dataset from adapter
     */
    public void clearDataset() {
        dataset.clear();
    }

    public List<T> getDataset() {
        return dataset;
    }

    public void setData(List<T> data) {
        clearDataset();
        dataset.addAll(data);
    }

    /**
     * add data to adapter
     *
     * @param data
     */
    public void addData(List<T> data) {
        dataset.addAll(data);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }


}