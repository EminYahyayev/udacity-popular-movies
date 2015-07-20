package com.ewintory.udacity.popularmovies.ui.fragment;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
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
import com.ewintory.udacity.popularmovies.ui.listener.PicassoScrollListener;
import com.ewintory.udacity.popularmovies.ui.module.MoviesModule;
import com.ewintory.udacity.popularmovies.ui.widget.BetterViewAnimator;
import com.ewintory.udacity.popularmovies.ui.widget.MultiSwipeRefreshLayout;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public final class MoviesFragment extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener, MultiSwipeRefreshLayout.CanChildScrollUpCallback {

    private static final String STATE_CURRENT_PAGE = "state_current_page";

    protected static final int ANIMATOR_VIEW_LOADING = R.id.view_loading;
    protected static final int ANIMATOR_VIEW_ERROR = R.id.view_error;
    protected static final int ANIMATOR_VIEW_CONTENT = R.id.movies_recycler_view;

    @Inject Picasso mPicasso;
    @Inject MoviesService mMoviesService;

    @Bind(R.id.coordinator_layout) CoordinatorLayout mCoordinatorLayout;
    @Bind(R.id.multi_swipe_refresh_layout) MultiSwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.movies_animator) BetterViewAnimator mViewAnimator;
    @Bind(R.id.movies_recycler_view) RecyclerView mRecyclerView;

    protected MoviesAdapter mMoviesAdapter;
    protected Subscription mContentSubscription = Subscriptions.empty();

    private PublishSubject<Observable<Movie.Response>> mMovieObservableSubject = PublishSubject.create();
    private Sort mSort;
    private int mCurrentPage = 1;

    @Nullable
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movies, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerView();
        initSwipeRefreshLayout();

        mCurrentPage = (savedInstanceState != null) ? savedInstanceState.getInt(STATE_CURRENT_PAGE) : 1;
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_CURRENT_PAGE, mCurrentPage);
    }

    @Override public void onDestroyView() {
        mContentSubscription.unsubscribe();
        mMoviesAdapter.setListener(MovieClickListener.DUMMY);
        super.onDestroyView();
    }

    @Override public boolean canSwipeRefreshChildScrollUp() {
        return mRecyclerView != null && ViewCompat.canScrollVertically(mRecyclerView, -1)
                || mViewAnimator.getDisplayedChildId() == ANIMATOR_VIEW_LOADING;
    }

    @OnClick(R.id.view_error)
    @Override public void onRefresh() {
        if (mSort != null) reloadFromSort(mSort);
    }

    public void scrollToTop(boolean smooth) {
        if (smooth)
            mRecyclerView.smoothScrollToPosition(0);
        else
            mRecyclerView.scrollToPosition(0);
    }

    public void reloadFromSort(@NonNull Sort sort) {
        Timber.d("reloadFromSort: sort=" + sort);
        mSort = sort;
        reloadContent();
    }

    protected final void reloadContent() {
        mViewAnimator.setDisplayedChildId(ANIMATOR_VIEW_LOADING);
        mMoviesAdapter.clear();
        mMoviesAdapter.setLoadMore(true);
        subscribeToContent();
        pullPage(1);
    }

    private void subscribeToContent() {
        mContentSubscription.unsubscribe();
        mContentSubscription = bind(Observable.concat(mMovieObservableSubject))
                .subscribe(new Action1<Movie.Response>() {
                    @Override public void call(Movie.Response moviesResponse) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        List<Movie> newMovies = moviesResponse.getMovies();
                        Timber.d(String.format("Page %d is loaded, out of %d.", moviesResponse.getPage(), moviesResponse.getTotalPages()));
                        mMoviesAdapter.add(newMovies);
                        mMoviesAdapter.setLoadMore(!moviesResponse.getTotalPages().equals(moviesResponse.getPage()));
                        mViewAnimator.setDisplayedChildId(ANIMATOR_VIEW_CONTENT);
                    }
                }, new Action1<Throwable>() {
                    @Override public void call(Throwable throwable) {
                        Timber.e("Movies loading failed.", throwable);
                        mSwipeRefreshLayout.setRefreshing(false);
                        if (mViewAnimator.getDisplayedChildId() == ANIMATOR_VIEW_CONTENT) {
                            mMoviesAdapter.setLoadMore(false);
                            Toast.makeText(getActivity(), R.string.view_empty_message, Toast.LENGTH_SHORT).show();
                        } else
                            mViewAnimator.setDisplayedChildId(ANIMATOR_VIEW_ERROR);
                    }
                });
    }

    private void pullPage(int page) {
        mMovieObservableSubject.onNext(mMoviesService.discoverMovies(mSort, page));
    }

    @CallSuper
    protected void initRecyclerView() {
        if (!(getActivity() instanceof MovieClickListener)) {
            throw new ClassCastException("Activity must implement MovieClickListener.");
        }
        mMoviesAdapter = new MoviesAdapter(this, (MovieClickListener) getActivity(), mPicasso);
        mMoviesAdapter.setLoadMore(true);

        final GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.movies_columns));
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override public int getSpanSize(int position) {
                int spanCount = layoutManager.getSpanCount();
//                return (mMoviesAdapter.isLoadMore(position) && (position % spanCount == 0)) ? spanCount : 1;
                return (mMoviesAdapter.isLoadMore(position)) ? spanCount : 1;
            }
        });

        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setVerticalScrollBarEnabled(false);
        mRecyclerView.setFocusable(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mMoviesAdapter);
        mRecyclerView.addOnScrollListener(new PicassoScrollListener(mPicasso, MoviesAdapter.PICASSO_TAG, 140));
        mRecyclerView.addOnScrollListener(new EndlessScrollListener(2) {
            @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                final int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                final int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                final int totalItemCount = layoutManager.getItemCount();

                onScrolled(firstVisibleItem, lastVisibleItem - firstVisibleItem, totalItemCount);
            }

            @Override public void onLoadMore(int page, int totalItemsCount) {
                Timber.d(String.format("Page %d is loading.", page));
                pullPage(page);
            }
        });
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

