package com.ewintory.udacity.popularmovies.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.ewintory.udacity.popularmovies.R;
import com.ewintory.udacity.popularmovies.data.api.Sort;
import com.ewintory.udacity.popularmovies.data.model.Movie;
import com.ewintory.udacity.popularmovies.data.model.MoviesResponse;
import com.ewintory.udacity.popularmovies.ui.listener.EndlessScrollListener;

import java.util.List;
import java.util.Set;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public final class SortedMoviesFragment extends MoviesFragment implements EndlessScrollListener.OnLoadMoreCallback {
    private static final String ARG_SORT = "state_sort";

    private static final String STATE_CURRENT_PAGE = "state_current_page";
    private static final String STATE_IS_LOADING = "state_is_loading";

    private static final String FAVORED_IDS_QUERY = "SELECT _ID FROM " + Movie.TABLE
            + " WHERE " + Movie.FAVORED + " = ?";

    private static final int VISIBLE_THRESHOLD = 10;

    protected Subscription mItemsSubscription = Subscriptions.empty();
    protected CompositeSubscription mSubscriptions = new CompositeSubscription();

    private BehaviorSubject<Set<Long>> mFavoredMovieIdsSubject = BehaviorSubject.create();
    private BehaviorSubject<Observable<MoviesResponse>> mItemsObservableSubject = BehaviorSubject.create();
    private Sort mSort;
    private int mCurrentPage = 0;
    private boolean mIsLoading = false;

    private boolean mIncludeAdult = false;

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

        mSubscriptions.add(db.createQuery(Movie.TABLE, FAVORED_IDS_QUERY, "1")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(Movie.ID_MAP)
                .doOnNext(set -> Timber.d(String.format("Favored ids loaded, %d items", set.size())))
                .subscribe(mFavoredMovieIdsSubject));

        subscribeToContent();
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
        mItemsSubscription.unsubscribe();
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

        mRecyclerView.clearOnScrollListeners();
        mRecyclerView.addOnScrollListener(buildOnScrollListener(mGridLayoutManager, mCurrentPage));
        pullPage(1);
    }

    private void subscribeToContent() {
        Timber.d("Subscribing to items");
        mItemsSubscription.unsubscribe();
        mItemsSubscription = bind(Observable.concat(mItemsObservableSubject))
                .map(response -> {
                    mSwipeRefreshLayout.setRefreshing(false);

                    mCurrentPage = response.getPage();
                    if (mCurrentPage == 1) mMoviesAdapter.clear();
                    mMoviesAdapter.setLoadMore(!response.getTotalPages().equals(mCurrentPage));

                    List<Movie> movies = response.getMovies();
                    Timber.d(String.format("Page %d is loaded, %d new items", mCurrentPage, movies.size()));
                    return movies;
                }).withLatestFrom(mFavoredMovieIdsSubject, (movies, favoredIds) -> {
                    for (Movie movie : movies)
                        movie.setFavored(favoredIds.contains(movie.getId()));
                    return movies;
                }).subscribe(movies -> {
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
                });

    }

    private void pullPage(int page) {
        Timber.d(String.format("Page %d is loading.", page));
        mItemsObservableSubject.onNext(movieDB.discoverMovies(mSort, page, mIncludeAdult));
    }


    @Override
    protected void initRecyclerView() {
        super.initRecyclerView();
        mRecyclerView.addOnScrollListener(buildOnScrollListener(mGridLayoutManager, mCurrentPage));
    }

    private EndlessScrollListener buildOnScrollListener(GridLayoutManager layoutManager, int startPage) {
        return EndlessScrollListener.fromGridLayoutManager(layoutManager, VISIBLE_THRESHOLD, startPage).setCallback(this);
    }


}

