/*
 * Copyright 2015.  Emin Yahyayev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ewintory.udacity.popularmovies.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.ewintory.udacity.popularmovies.R;

import java.util.List;

import rx.functions.Action1;

public abstract class EndlessAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements Action1<List<T>> {

    protected static final int VIEW_TYPE_LOAD_MORE = 1;
    protected static final int VIEW_TYPE_ITEM = 2;

    @NonNull protected final LayoutInflater mInflater;
    @NonNull protected List<T> mItems;

    protected boolean showLoadMore = false;

    public EndlessAdapter(@NonNull Context context, @NonNull List<T> items) {
        mInflater = LayoutInflater.from(context);
        mItems = items;
    }

    public void setLoadMore(boolean enabled) {
        if (showLoadMore != enabled) {
            if (showLoadMore) {
                notifyItemRemoved(getItemCount());
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
        return showLoadMore && (position == (getItemCount() - 1));
    }

    protected int countLoadMore() {
        return showLoadMore ? 1 : 0;
    }

    @Override
    public int getItemCount() {
        return mItems.size() + countLoadMore();
    }

    @Override
    public int getItemViewType(int position) {
        return isLoadMore(position) ? VIEW_TYPE_LOAD_MORE : VIEW_TYPE_ITEM;
    }

    @Override
    public void call(@NonNull List<T> newItems) {
        add(newItems);
    }

    public void set(@NonNull List<T> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    public void add(@NonNull List<T> newItems) {
        if (!newItems.isEmpty()) {
            int currentSize = mItems.size();
            int amountInserted = newItems.size();

            mItems.addAll(newItems);
            notifyItemRangeInserted(currentSize, amountInserted);
        }
    }

    @NonNull
    public List<T> getItems() {
        return mItems;
    }

    public T getItem(int position) {
        return !isLoadMore(position) ? mItems.get(position) : null;
    }

    public void clear() {
        if (!mItems.isEmpty()) {
            mItems.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return viewType == VIEW_TYPE_LOAD_MORE
                ? new RecyclerView.ViewHolder(mInflater.inflate(R.layout.item_load_more, parent, false)) {}
                : onCreateItemHolder(parent, viewType);
    }

    protected abstract VH onCreateItemHolder(ViewGroup parent, int viewType);

}
