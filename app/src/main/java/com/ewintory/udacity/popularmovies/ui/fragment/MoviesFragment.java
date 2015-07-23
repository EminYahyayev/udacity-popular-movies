package com.ewintory.udacity.popularmovies.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ewintory.udacity.popularmovies.R;
import com.ewintory.udacity.popularmovies.data.api.MovieDB;
import com.ewintory.udacity.popularmovies.data.model.Movie;
import com.ewintory.udacity.popularmovies.ui.adapter.MoviesAdapter;
import com.ewintory.udacity.popularmovies.ui.listener.MovieClickListener;
import com.ewintory.udacity.popularmovies.ui.module.MoviesModule;
import com.ewintory.udacity.popularmovies.ui.widget.BetterViewAnimator;
import com.ewintory.udacity.popularmovies.ui.widget.MultiSwipeRefreshLayout;
import com.squareup.sqlbrite.BriteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public abstract class MoviesFragment extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener, MultiSwipeRefreshLayout.CanChildScrollUpCallback, MovieClickListener {

    public interface Listener {
        void onMovieSelected(Movie movie, View view);
    }

    private static final String STATE_MOVIES = "state_movies";

    protected static final int ANIMATOR_VIEW_LOADING = R.id.view_loading;
    protected static final int ANIMATOR_VIEW_CONTENT = R.id.movies_recycler_view;
    protected static final int ANIMATOR_VIEW_ERROR = R.id.view_error;
    protected static final int ANIMATOR_VIEW_EMPTY = R.id.view_empty;

    @Inject BriteDatabase db;
    @Inject MovieDB movieDB;

    @Bind(R.id.multi_swipe_refresh_layout) MultiSwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.movies_animator) BetterViewAnimator mViewAnimator;
    @Bind(R.id.movies_recycler_view) RecyclerView mRecyclerView;

    protected Listener listener;
    protected MoviesAdapter mMoviesAdapter;
    protected GridLayoutManager mGridLayoutManager;
    protected Subscription mGenresSubscription = Subscriptions.empty();

    @Override
    public void onAttach(Activity activity) {
        if (!(activity instanceof Listener)) {
            throw new IllegalStateException("Activity must implement MoviesFragment.Listener.");
        }

        super.onAttach(activity);
        listener = (Listener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movies, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<Movie> restoredMovies = savedInstanceState != null
                ? savedInstanceState.<Movie>getParcelableArrayList(STATE_MOVIES)
                : new ArrayList<Movie>();

        mMoviesAdapter = new MoviesAdapter(this, restoredMovies);
        mMoviesAdapter.setListener(this);

        initSwipeRefreshLayout();
        initRecyclerView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_MOVIES, new ArrayList<>(mMoviesAdapter.getItems()));
    }

    @Override public void onDestroyView() {
        mGenresSubscription.unsubscribe();
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        listener = (movie, view) -> {};
        mMoviesAdapter.setListener(MovieClickListener.DUMMY);
        super.onDetach();
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
    public abstract void onRefresh();

    @Override
    public void onContentClicked(Movie movie, View view) {
        listener.onMovieSelected(movie, view);
    }

    @Override
    public boolean onFavoredClicked(@NonNull final Movie movie) {
        boolean wasFavored = movie.isFavored();
        Timber.d("onFavoredClicked: wasFavored=" + wasFavored);

        movie.setFavored(!wasFavored);
        if (wasFavored) {
            db.delete(Movie.TABLE, "_ID=?", movie.getId() + "");
        } else {
            db.insert(Movie.TABLE, new Movie.Builder()
                    .movie(movie)
                    .build());
        }
        return true;
    }

    @CallSuper
    protected void initRecyclerView() {
        mGridLayoutManager = new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.movies_columns));
        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override public int getSpanSize(int position) {
                int spanCount = mGridLayoutManager.getSpanCount();
                return (mMoviesAdapter.isLoadMore(position) /* && (position % spanCount == 0) */) ? spanCount : 1;
            }
        });

        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setAdapter(mMoviesAdapter);
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.swipe_progress_colors));
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setCanChildScrollUpCallback(this);
    }


    @Override
    protected List<Object> getModules() {
        return Collections.<Object>singletonList(new MoviesModule());
    }
}
