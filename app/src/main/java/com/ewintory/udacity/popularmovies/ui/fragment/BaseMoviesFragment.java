package com.ewintory.udacity.popularmovies.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ewintory.udacity.popularmovies.R;
import com.ewintory.udacity.popularmovies.data.api.MoviesService;
import com.ewintory.udacity.popularmovies.data.model.Movie;
import com.ewintory.udacity.popularmovies.ui.adapter.MoviesAdapter;
import com.ewintory.udacity.popularmovies.ui.dagger.MoviesModule;
import com.ewintory.udacity.popularmovies.ui.listener.MovieClickListener;
import com.ewintory.udacity.popularmovies.ui.widget.BetterViewAnimator;
import com.ewintory.udacity.popularmovies.ui.widget.MultiSwipeRefreshLayout;
import com.ewintory.udacity.popularmovies.utils.PicassoScrollListener;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public abstract class BaseMoviesFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, MultiSwipeRefreshLayout.CanChildScrollUpCallback {
    protected static final int ANIMATOR_VIEW_LOADING = R.id.view_loading;
    protected static final int ANIMATOR_VIEW_ERROR = R.id.view_error;
    protected static final int ANIMATOR_VIEW_CONTENT = R.id.movies_recycler_view;

    @Inject Picasso mPicasso;
    @Inject MoviesService mMoviesService;

    @Bind(R.id.multi_swipe_refresh_layout) MultiSwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.movies_animator) BetterViewAnimator mViewAnimator;
    @Bind(R.id.movies_recycler_view) RecyclerView mRecyclerView;

    protected MoviesAdapter mMoviesAdapter;
    protected Subscription mContentSubscription = Subscriptions.empty();

    @Nullable
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movies, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerView();
        initSwipeRefreshLayout();
    }

    @Override public void onDestroyView() {
        mContentSubscription.unsubscribe();
        mMoviesAdapter.setListener(MovieClickListener.DUMMY);
        super.onDestroyView();
    }

    @Override public boolean canSwipeRefreshChildScrollUp() {
        return ViewCompat.canScrollVertically(mRecyclerView, -1)
                || mViewAnimator.getDisplayedChildId() == ANIMATOR_VIEW_LOADING;
    }

    @Override public void onRefresh() {/** ignore */}

    public void scrollToTop(boolean smooth) {
        if (smooth)
            mRecyclerView.smoothScrollToPosition(0);
        else
            mRecyclerView.scrollToPosition(0);
    }

    protected final void reloadContent() {
        mViewAnimator.setDisplayedChildId(ANIMATOR_VIEW_LOADING);
        mMoviesAdapter.clear();

        mContentSubscription.unsubscribe();
        mContentSubscription = bind(buildContentObservable())
                .finallyDo(new Action0() {
                    @Override public void call() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                })
                .subscribe(new Action1<Movie.Response>() {
                    @Override public void call(Movie.Response moviesResponse) {
                        List<Movie> newMovies = moviesResponse.getMovies();
                        Timber.v(String.format("Movies loaded: page %d, %d items.", moviesResponse.getPage(), newMovies.size()));
                        mMoviesAdapter.add(newMovies);
                        mViewAnimator.setDisplayedChildId(ANIMATOR_VIEW_CONTENT);
                    }
                }, new Action1<Throwable>() {
                    @Override public void call(Throwable throwable) {
                        Timber.e("Movies loading failed.", throwable);
                        mViewAnimator.setDisplayedChildId(ANIMATOR_VIEW_ERROR);
                    }
                });
    }

    protected abstract Observable<Movie.Response> buildContentObservable();

    private void initRecyclerView() {
        int spanCount = getResources().getInteger(R.integer.movies_columns);
        final GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), spanCount);

        if (!(getActivity() instanceof MovieClickListener)) {
            throw new ClassCastException("Activity must implement MovieClickListener.");
        }
        mMoviesAdapter = new MoviesAdapter(this, (MovieClickListener) getActivity(), mPicasso);

        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setVerticalScrollBarEnabled(false);
        mRecyclerView.setFocusable(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mMoviesAdapter);
        mRecyclerView.addOnScrollListener(new PicassoScrollListener(mPicasso, MoviesAdapter.PICASSO_TAG, 80));
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
