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

package com.ewintory.udacity.popularmovies.ui;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ewintory.udacity.popularmovies.R;
import com.ewintory.udacity.popularmovies.ui.widget.BetterViewAnimator;
import com.jakewharton.rxrelay.PublishRelay;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.functions.Action1;

public abstract class EndlessStateAdapter<T, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements Action1<List<T>> {

    public static final int LOAD_MORE_STATE_NONE = 1;
    public static final int LOAD_MORE_STATE_PROGRESS = 2;
    public static final int LOAD_MORE_STATE_ACTION = 3;

    @IntDef({LOAD_MORE_STATE_NONE,
            LOAD_MORE_STATE_ACTION,
            LOAD_MORE_STATE_PROGRESS})
    @interface LoadMoreState {}

    protected static final int LOAD_MORE_ID = -10;

    protected static final int VIEW_TYPE_LOAD_MORE = 1;
    protected static final int VIEW_TYPE_ITEM = 2;

    private final PublishRelay<Void> loadMoreClickRelay = PublishRelay.create();

    @NonNull protected final LayoutInflater inflater;
    @NonNull protected List<T> items = new ArrayList<>();

    @LoadMoreState
    protected int loadMoreState = LOAD_MORE_STATE_NONE;

    public EndlessStateAdapter(@NonNull Context context) {
        inflater = LayoutInflater.from(context);
    }

    public Observable<Void> getLoadMoreClickObservable() {
        return loadMoreClickRelay.asObservable();
    }

    @LoadMoreState
    public int getLoadMoreState() {
        return loadMoreState;
    }

    public void setLoadMoreState(@LoadMoreState int state) {
        if (state == loadMoreState)
            return;

        loadMoreState = state;
        notifyDataSetChanged();

        // TOTO: optimize
        /*
        switch (state) {
            case LOAD_MORE_STATE_NONE:
                loadMoreState = state;
                notifyItemRemoved(getItemCount() - 1);
                break;
            case LOAD_MORE_STATE_ACTION:
            case LOAD_MORE_STATE_PROGRESS:
                loadMoreState = state;
                notifyItemInserted(getItemCount() - 1);
                break;
            default:
        }
        */
    }

    protected int countLoadMore() {
        return loadMoreState == LOAD_MORE_STATE_NONE ? 0 : 1;
    }

    public boolean isLoadMorePosition(int position) {
        return loadMoreState != LOAD_MORE_STATE_NONE && (position == (getItemCount() - 1));
    }

    public boolean hasItems() {
        return getItemCount() > 0;
    }

    @Override
    public int getItemCount() {
        return items.size() + countLoadMore();
    }

    @Override
    public int getItemViewType(int position) {
        return isLoadMorePosition(position) ? VIEW_TYPE_LOAD_MORE : VIEW_TYPE_ITEM;
    }

    @CallSuper
    @Override
    public long getItemId(int position) {
        return isLoadMorePosition(position) ? LOAD_MORE_ID : super.getItemId(position);
    }

    @Override
    public void call(@NonNull List<T> newItems) {
        setItems(newItems);
    }

    public void setItems(@Nullable List<T> items) {
        if (items == null) {
            this.items.clear();
            return;
        }

        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    public List<T> getItems() {
        return items;
    }

    @Nullable
    public T getItem(int position) {
        return !isLoadMorePosition(position) ? items.get(position) : null;
    }

    public void clear() {
        if (!items.isEmpty()) {
            items.clear();
            notifyDataSetChanged();
        }
    }

    @CallSuper
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder.getItemViewType() == VIEW_TYPE_LOAD_MORE) {
            final LoadMoreHolder holder = (LoadMoreHolder) viewHolder;
            holder.bind(loadMoreState);
        } else {
            onBindItemHolder((VH) viewHolder, getItem(position));
        }
    }

    protected abstract void onBindItemHolder(VH holder, T item);

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return viewType == VIEW_TYPE_LOAD_MORE
                ? new LoadMoreHolder(inflater.inflate(R.layout.item_load_more_state, parent, false))
                : onCreateItemHolder(parent, viewType);
    }

    protected abstract VH onCreateItemHolder(ViewGroup parent, int viewType);

    protected final class LoadMoreHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.view_animator) BetterViewAnimator viewAnimator;
        @BindView(R.id.view_action) View loadMoreButton;

        public LoadMoreHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            loadMoreButton.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    loadMoreClickRelay.call(null);
                }
            });
        }

        public void bind(@LoadMoreState int loadMoreState) {
            switch (loadMoreState) {
                case LOAD_MORE_STATE_PROGRESS:
                    viewAnimator.setDisplayedChildId(R.id.view_progress);
                    break;
                case LOAD_MORE_STATE_ACTION:
                    viewAnimator.setDisplayedChildId(R.id.view_action);
                    break;
                case LOAD_MORE_STATE_NONE:
                    viewAnimator.setDisplayedChildId(R.id.view_none);
                    break;
            }
        }
    }

}
