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
import com.ewintory.udacity.popularmovies.ui.listener.MovieClickListener;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public final class SortedMoviesFragment extends MoviesFragment implements EndlessScrollListener.OnLoadMoreCallback {
    private static final String STATE_CURRENT_PAGE = "state_current_page";
    private static final String STATE_SORT = "state_sort";

    private static final int VISIBLE_THRESHOLD = 10;

    protected Subscription mItemsSubscription = Subscriptions.empty();

    private PublishSubject<Observable<Movie.Response>> mItemsObservableSubject = PublishSubject.create();
    private Sort mSort;
    private int mCurrentPage = 0;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            mCurrentPage = savedInstanceState.getInt(STATE_CURRENT_PAGE, 0);
            mSort = (Sort) savedInstanceState.getSerializable(STATE_SORT);
            Timber.d(String.format("Restoring state: pages 1-%d", mCurrentPage));
        }

        mViewAnimator.setDisplayedChildId((mCurrentPage == 0) ? ANIMATOR_VIEW_LOADING : ANIMATOR_VIEW_CONTENT);
        subscribeToContent();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_CURRENT_PAGE, mCurrentPage);
        outState.putSerializable(STATE_SORT, mSort);
    }

    @Override
    public void onDestroyView() {
        mItemsSubscription.unsubscribe();
        mMoviesAdapter.setListener(MovieClickListener.DUMMY);
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

    public void reloadFromSort(@NonNull Sort sort) {
        Timber.d("Reloading from sort=" + sort);
        mSort = sort;
        mCurrentPage = 0;
        reloadContent();
    }

    protected final void reloadContent() {
        if (!mSwipeRefreshLayout.isRefreshing())
            mViewAnimator.setDisplayedChildId(ANIMATOR_VIEW_LOADING);

        mRecyclerView.clearOnScrollListeners();
        mRecyclerView.addOnScrollListener(buildOnScrollListener(mGridLayoutManager, mCurrentPage));
        pullPage(1);
    }

    private void subscribeToContent() {
        mItemsSubscription.unsubscribe();
        mItemsSubscription = bind(Observable.concat(mItemsObservableSubject))
                .subscribe(new Action1<Movie.Response>() {
                    @Override public void call(Movie.Response response) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        if (response.getPage().equals(1))
                            mMoviesAdapter.clear();

                        List<Movie> newItems = response.getMovies();
                        Timber.d(String.format("Page %d is loaded, %d new items", response.getPage(), newItems.size()));
                        mCurrentPage = response.getPage();

                        mMoviesAdapter.add(newItems);
                        mMoviesAdapter.setLoadMore(!response.getTotalPages().equals(response.getPage()));
                        mViewAnimator.setDisplayedChildId(ANIMATOR_VIEW_CONTENT);
                    }
                }, new Action1<Throwable>() {
                    @Override public void call(Throwable throwable) {
                        Timber.e(throwable, "Movies loading failed.");
                        mSwipeRefreshLayout.setRefreshing(false);
                        if (mViewAnimator.getDisplayedChildId() == ANIMATOR_VIEW_CONTENT) {
                            mMoviesAdapter.setLoadMore(false);
                            Toast.makeText(getActivity(), R.string.view_error_message, Toast.LENGTH_SHORT).show();
                        } else
                            mViewAnimator.setDisplayedChildId(ANIMATOR_VIEW_ERROR);
                    }
                });
    }

    private void pullPage(int page) {
        Timber.d(String.format("Page %d is loading.", page));
        mItemsObservableSubject.onNext(mMovieDB.discoverMovies(mSort, page, true));
    }

    private void pullToPage(int page) {
        Timber.d(String.format("Page 1 - %d is loading.", page));
        mItemsObservableSubject.onNext(Observable.range(1, page).concatMap(new Func1<Integer, Observable<? extends Movie.Response>>() {
            @Override public Observable<? extends Movie.Response> call(Integer page) {
                return mMovieDB.discoverMovies(mSort, page);
            }
        }));
    }


    @Override
    protected void initRecyclerView(@NonNull List<Movie> restoredMovies) {
        mMoviesAdapter.setLoadMore(true);
        if (!restoredMovies.isEmpty())
            mRecyclerView.addOnScrollListener(buildOnScrollListener(mGridLayoutManager, mCurrentPage));
    }

    private EndlessScrollListener buildOnScrollListener(GridLayoutManager layoutManager, int startPage) {
        return EndlessScrollListener.fromGridLayoutManager(layoutManager, VISIBLE_THRESHOLD, startPage).setCallback(this);
    }


}

