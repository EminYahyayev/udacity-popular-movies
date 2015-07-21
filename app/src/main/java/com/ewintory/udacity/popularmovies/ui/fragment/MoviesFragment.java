package com.ewintory.udacity.popularmovies.ui.fragment;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ewintory.udacity.popularmovies.R;
import com.ewintory.udacity.popularmovies.data.api.MoviesService;
import com.ewintory.udacity.popularmovies.data.api.Sort;
import com.ewintory.udacity.popularmovies.data.model.Movie;
import com.ewintory.udacity.popularmovies.ui.adapter.MoviesAdapter;
import com.ewintory.udacity.popularmovies.ui.listener.EndlessScrollListener;
import com.ewintory.udacity.popularmovies.ui.listener.MovieClickListener;
import com.ewintory.udacity.popularmovies.ui.module.MoviesModule;
import com.ewintory.udacity.popularmovies.ui.widget.BetterViewAnimator;
import com.ewintory.udacity.popularmovies.ui.widget.MultiSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public final class MoviesFragment extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener, MultiSwipeRefreshLayout.CanChildScrollUpCallback, EndlessScrollListener.OnLoadMoreCallback {

    private static final String STATE_CURRENT_PAGE = "state_current_page";
    private static final String STATE_SORT = "state_sort";
    private static final String STATE_MOVIES = "state_movies";

    protected static final int ANIMATOR_VIEW_LOADING = R.id.view_loading;
    protected static final int ANIMATOR_VIEW_ERROR = R.id.view_error;
    protected static final int ANIMATOR_VIEW_CONTENT = R.id.movies_recycler_view;

    @Inject MoviesService mMoviesService;

    @Bind(R.id.multi_swipe_refresh_layout) MultiSwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.movies_animator) BetterViewAnimator mViewAnimator;
    @Bind(R.id.movies_recycler_view) RecyclerView mRecyclerView;

    protected MoviesAdapter mMoviesAdapter;
    protected GridLayoutManager mGridLayoutManager;
    protected EndlessScrollListener mScrollListener;
    protected Subscription mContentSubscription = Subscriptions.empty();

    private PublishSubject<Observable<Movie.Response>> mMoviesObservableSubject = PublishSubject.create();
    private Sort mSort;
    private int mCurrentPage = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movies, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<Movie> restoredMovies;
        if (savedInstanceState != null) {
            mCurrentPage = savedInstanceState.getInt(STATE_CURRENT_PAGE, 1);
            mSort = (Sort) savedInstanceState.getSerializable(STATE_SORT);
            restoredMovies = savedInstanceState.getParcelableArrayList(STATE_MOVIES);
            Timber.d(String.format("Restoring state: pages 1-%d, %d restored item", mCurrentPage, restoredMovies.size()));
        } else {
            restoredMovies = new ArrayList<>();
        }

        initRecyclerView(mCurrentPage - 1, restoredMovies);
        initSwipeRefreshLayout();

        mViewAnimator.setDisplayedChildId(restoredMovies.isEmpty() ? ANIMATOR_VIEW_LOADING : ANIMATOR_VIEW_CONTENT);

        subscribeToContent();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_CURRENT_PAGE, mCurrentPage);
        outState.putSerializable(STATE_SORT, mSort);
        outState.putParcelableArrayList(STATE_MOVIES, new ArrayList<>(mMoviesAdapter.getItems()));
    }

    @Override
    public void onDestroyView() {
        mContentSubscription.unsubscribe();
        mMoviesAdapter.setListener(MovieClickListener.DUMMY);
        super.onDestroyView();
    }

    @Override
    public boolean canSwipeRefreshChildScrollUp() {
        return mRecyclerView != null && ViewCompat.canScrollVertically(mRecyclerView, -1)
                || mViewAnimator.getDisplayedChildId() == ANIMATOR_VIEW_LOADING;
    }

    public void scrollToTop(boolean smooth) {
        if (smooth)
            mRecyclerView.smoothScrollToPosition(0);
        else
            mRecyclerView.scrollToPosition(0);
    }

    @Override
    public void onRefresh() {
        if (mSort != null) reloadContent();
    }

    public void reloadFromSort(@NonNull Sort sort) {
        Timber.d("Reloading from sort=" + sort);
        mSort = sort;
        mCurrentPage = 1;
        reloadContent();
    }

    protected final void reloadContent() {
        if (!mSwipeRefreshLayout.isRefreshing())
            mViewAnimator.setDisplayedChildId(ANIMATOR_VIEW_LOADING);

        mRecyclerView.clearOnScrollListeners();
        pullToPage(mCurrentPage);
        mRecyclerView.addOnScrollListener(buildOnScrollListener(mGridLayoutManager, mCurrentPage - 1));
    }

    private void subscribeToContent() {
        mContentSubscription.unsubscribe();
        mContentSubscription = bind(Observable.concat(mMoviesObservableSubject))
                .subscribe(new Action1<Movie.Response>() {
                    @Override public void call(Movie.Response moviesResponse) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        if (moviesResponse.getPage().equals(1))
                            mMoviesAdapter.clear();

                        List<Movie> newMovies = moviesResponse.getMovies();
                        Timber.d(String.format("Page %d is loaded.", moviesResponse.getPage()));
                        mCurrentPage = moviesResponse.getPage();

                        mMoviesAdapter.add(newMovies);
                        mMoviesAdapter.setLoadMore(!moviesResponse.getTotalPages().equals(moviesResponse.getPage()));
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

    @Override
    public void onLoadMore(int page, int totalItemsCount) {
        if (!mMoviesAdapter.isLoadMore())
            pullPage(page);
    }

    private void pullPage(int page) {
        Timber.d(String.format("Page %d is loading.", page));
        mMoviesObservableSubject.onNext(mMoviesService.discoverMovies(mSort, page));
    }

    private void pullToPage(int page) {
        Timber.d(String.format("Page 1 - %d is loading.", page));
        mMoviesObservableSubject.onNext(Observable.range(1, mCurrentPage).concatMap(new Func1<Integer, Observable<? extends Movie.Response>>() {
            @Override public Observable<? extends Movie.Response> call(Integer page) {
                return mMoviesService.discoverMovies(mSort, page);
            }
        }));
    }


    @CallSuper
    protected void initRecyclerView(int startPage, @NonNull List<Movie> restoredMovies) {
        if (!(getActivity() instanceof MovieClickListener)) {
            throw new ClassCastException("Activity must implement MovieClickListener.");
        }

        mMoviesAdapter = new MoviesAdapter(this, restoredMovies);
        mMoviesAdapter.setListener((MovieClickListener) getActivity());
        mMoviesAdapter.setLoadMore(true);

        mGridLayoutManager = new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.movies_columns));
        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override public int getSpanSize(int position) {
                int spanCount = mGridLayoutManager.getSpanCount();
                return (mMoviesAdapter.isLoadMore(position) /* && (position % spanCount == 0) */) ? spanCount : 1;
            }
        });

        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setAdapter(mMoviesAdapter);
        mRecyclerView.addOnScrollListener(buildOnScrollListener(mGridLayoutManager, startPage));
    }

    private EndlessScrollListener buildOnScrollListener(GridLayoutManager layoutManager, int startPage) {
        return EndlessScrollListener.fromGridLayoutManager(layoutManager, startPage, 4, this);
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.swipe_progress_colors));
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setCanChildScrollUpCallback(this);
    }

    @Override protected List<Object> getModules() {
        return Collections.<Object>singletonList(new MoviesModule());
    }
}

