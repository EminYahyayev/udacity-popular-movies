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

package com.ewintory.udacity.popularmovies.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.ewintory.udacity.popularmovies.R;
import com.ewintory.udacity.popularmovies.data.api.Sort;
import com.ewintory.udacity.popularmovies.data.model.Movie;
import com.ewintory.udacity.popularmovies.ui.listener.EndlessScrollListener;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.BehaviorSubject;
import timber.log.Timber;

public final class SortedMoviesFragment extends MoviesFragment implements EndlessScrollListener.OnLoadMoreCallback {
    private static final String ARG_SORT = "state_sort";

    private static final String STATE_CURRENT_PAGE = "state_current_page";
    private static final String STATE_IS_LOADING = "state_is_loading";

    private static final int VISIBLE_THRESHOLD = 10;

    private EndlessScrollListener mEndlessScrollListener;
    private BehaviorSubject<Observable<List<Movie>>> mItemsObservableSubject = BehaviorSubject.create();

    private Sort mSort;
    private int mCurrentPage = 0;
    private boolean mIsLoading = false;

    public static SortedMoviesFragment newInstance(@NonNull Sort sort) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_SORT, sort);

        SortedMoviesFragment fragment = new SortedMoviesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSort = (Sort) getArguments().getSerializable(ARG_SORT);

        if (savedInstanceState != null) {
            mCurrentPage = savedInstanceState.getInt(STATE_CURRENT_PAGE, 0);
            mIsLoading = savedInstanceState.getBoolean(STATE_IS_LOADING, true);
            Timber.d(String.format("Restoring state: pages 1-%d, was loading - %s", mCurrentPage, mIsLoading));
        }

        mMoviesAdapter.setLoadMore(true);
        mViewAnimator.setDisplayedChildId((mCurrentPage == 0) ? ANIMATOR_VIEW_LOADING : ANIMATOR_VIEW_CONTENT);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // subscribe to global favored changes in order to synchronise movies from different views
        mSubscriptions.add(mHelper.getFavoredObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(event -> {
                    int count = mMoviesAdapter.getItemCount();
                    for (int position = 0; position < count; position++) {
                        if (mMoviesAdapter.getItemId(position) == event.movieId) {
                            mMoviesAdapter.notifyItemChanged(position);
                        }
                    }
                }));

        subscribeToMovies();
        if (savedInstanceState == null)
            reloadContent();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_CURRENT_PAGE, mCurrentPage);
        outState.putBoolean(STATE_IS_LOADING, mIsLoading);
        outState.putSerializable(ARG_SORT, mSort);
    }

    @Override
    public void onDestroyView() {
        mSubscriptions.unsubscribe();
        super.onDestroyView();
    }

    @Override
    public void onRefresh() {
        if (mSort != null) reloadContent();
    }

    @Override
    public void onLoadMore(int page, int totalItemsCount) {
        if (mMoviesAdapter.isLoadMore())
            pullPage(page);
    }

    protected final void reloadContent() {
        if (!mSwipeRefreshLayout.isRefreshing())
            mViewAnimator.setDisplayedChildId(ANIMATOR_VIEW_LOADING);

        mSelectedPosition = -1;
        reAddOnScrollListener(mGridLayoutManager, mCurrentPage = 0);
        pullPage(1);
    }

    private void subscribeToMovies() {
        Timber.d("Subscribing to items");
        mSubscriptions.add(Observable.concat(mItemsObservableSubject)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movies -> {
                    mSwipeRefreshLayout.setRefreshing(false);
                    mCurrentPage++;

                    Timber.d(String.format("Page %d is loaded, %d new items", mCurrentPage, movies.size()));
                    if (mCurrentPage == 1) mMoviesAdapter.clear();

                    mMoviesAdapter.setLoadMore(!movies.isEmpty());
                    mMoviesAdapter.add(movies);
                    mViewAnimator.setDisplayedChildId(ANIMATOR_VIEW_CONTENT);
                }, throwable -> {
                    Timber.e(throwable, "Movies loading failed.");
                    mSwipeRefreshLayout.setRefreshing(false);
                    if (mViewAnimator.getDisplayedChildId() == ANIMATOR_VIEW_CONTENT) {
                        mMoviesAdapter.setLoadMore(false);
                        Toast.makeText(getActivity(), R.string.view_error_message, Toast.LENGTH_SHORT).show();
                    } else
                        mViewAnimator.setDisplayedChildId(ANIMATOR_VIEW_ERROR);
                }));
    }

    private void pullPage(int page) {
        Timber.d(String.format("Page %d is loading.", page));
        mItemsObservableSubject.onNext(mMoviesRepository.discoverMovies(mSort, page));
    }


    @Override
    protected void initRecyclerView() {
        super.initRecyclerView();
        reAddOnScrollListener(mGridLayoutManager, mCurrentPage);
    }

    private void reAddOnScrollListener(GridLayoutManager layoutManager, int startPage) {
        if (mEndlessScrollListener != null)
            mRecyclerView.removeOnScrollListener(mEndlessScrollListener);

        mEndlessScrollListener = EndlessScrollListener.fromGridLayoutManager(layoutManager, VISIBLE_THRESHOLD, startPage).setCallback(this);
        mRecyclerView.addOnScrollListener(mEndlessScrollListener);
    }

}

