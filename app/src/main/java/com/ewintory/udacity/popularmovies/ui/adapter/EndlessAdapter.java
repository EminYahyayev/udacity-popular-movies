package com.ewintory.udacity.popularmovies.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.ewintory.udacity.popularmovies.R;

import java.util.ArrayList;
import java.util.List;

public abstract class EndlessAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected static final int VIEW_TYPE_LOAD_MORE = 1;
    protected static final int VIEW_TYPE_ITEM = 2;

    @NonNull protected final Fragment mFragment;
    @NonNull protected final LayoutInflater mInflater;
    @NonNull protected List<T> mItems;

    protected boolean showLoadMore = false;
    protected boolean clearOnNextAdd = false;

    public EndlessAdapter(@NonNull Fragment fragment) {
        this(fragment, new ArrayList<T>());
    }

    public EndlessAdapter(@NonNull Fragment fragment, @NonNull List<T> items) {
        mInflater = LayoutInflater.from(fragment.getActivity());
        mFragment = fragment;
        mItems = items;
    }

    public void setLoadMore(boolean enabled) {
        if (showLoadMore != enabled) {
            if (showLoadMore) {
                notifyItemRemoved(getItemCount()); // Remove last position
                showLoadMore = false;
            } else {
                notifyItemInserted(getItemCount());
                showLoadMore = true;
            }
        }
    }

    public boolean isLoadMore() {
        return showLoadMore;
    }

    public boolean isLoadMore(int position) {
        return showLoadMore && (position == (getItemCount() - 1)); // At last position add one
    }

    public int countLoadMore() {
        return showLoadMore ? 1 : 0;
    }

    @Override
    public int getItemCount() {
        return mItems.size() + countLoadMore();
    }

    @Override public int getItemViewType(int position) {
        return isLoadMore(position) ? VIEW_TYPE_LOAD_MORE : VIEW_TYPE_ITEM;
    }

    public void clearOnNextAdd() {
        clearOnNextAdd = true;
    }

    public void add(@NonNull List<T> newItems) {
        int currentSize = mItems.size();
        int amountInserted = newItems.size();

        if (clearOnNextAdd && !mItems.isEmpty()) {
            mItems.clear();
            mItems.addAll(newItems);
            notifyDataSetChanged();
        } else {
            mItems.addAll(newItems);
            notifyItemRangeInserted(currentSize, amountInserted);
        }
    }

    public List<T> getItems() {
        return mItems;
    }

    public void clear() {
        if (!mItems.isEmpty()) {
            mItems.clear();
            notifyDataSetChanged();
        }
    }

    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_LOAD_MORE)
            return new RecyclerView.ViewHolder(mInflater.inflate(R.layout.item_load_more, parent, false)) {};
        else
            return onCreateItemHolder(parent, viewType);
    }

    protected abstract VH onCreateItemHolder(ViewGroup parent, int viewType);

}
