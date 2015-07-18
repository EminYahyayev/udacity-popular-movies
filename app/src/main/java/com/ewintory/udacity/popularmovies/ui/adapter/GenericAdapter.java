package com.ewintory.udacity.popularmovies.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;


public abstract class GenericAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    protected final Fragment mFragment;
    protected final LayoutInflater mInflater;

    // the main data list to save loaded data
    protected List<T> mItems;

    // the serverListSize is the total number of items on the server side,
    // which should be returned from the web request results
    protected int mServerListSize = -1;

    // Two view types which will be used to determine whether a row should be displaying
    // data or a Progressbar
    public static final int VIEW_TYPE_LOADING = 0;
    public static final int VIEW_TYPE_ITEM = 1;

    public GenericAdapter(Fragment fragment, List<T> items) {
        mInflater = LayoutInflater.from(fragment.getActivity());
        mFragment = fragment;
        mItems = items;
    }

    public void setServerListSize(int serverListSize) {
        mServerListSize = serverListSize;
    }

    /**
     * return the type of the row,
     * the last row indicates the user that the RecyclerView is loading more data
     */
    @Override public int getItemViewType(int position) {
        return (position >= mItems.size())
                ? VIEW_TYPE_LOADING
                : VIEW_TYPE_ITEM;
    }

    @Override public long getItemId(int position) {
        return (getItemViewType(position) == VIEW_TYPE_ITEM) ? position : -1;
    }

    @Override public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override public abstract void onBindViewHolder(VH holder, int position);

    @Override public int getItemCount() {
        return 0;
    }
}
