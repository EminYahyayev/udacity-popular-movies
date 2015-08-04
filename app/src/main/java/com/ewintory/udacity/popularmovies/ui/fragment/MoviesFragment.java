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
import com.ewintory.udacity.popularmovies.data.model.Movie;
import com.ewintory.udacity.popularmovies.data.repository.MoviesRepository;
import com.ewintory.udacity.popularmovies.ui.adapter.MoviesAdapter;
import com.ewintory.udacity.popularmovies.ui.helper.MoviesHelper;
import com.ewintory.udacity.popularmovies.ui.module.MoviesModule;
import com.ewintory.udacity.popularmovies.ui.widget.BetterViewAnimator;
import com.ewintory.udacity.popularmovies.ui.widget.MultiSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public abstract class MoviesFragment extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener, MultiSwipeRefreshLayout.CanChildScrollUpCallback, MoviesAdapter.OnMovieClickListener {

    public interface Listener {
        void onMovieSelected(Movie movie, View view);
    }

    private static final String STATE_MOVIES = "state_movies";
    private static final String STATE_SELECTED_POSITION = "state_selected_position";

    protected static final int ANIMATOR_VIEW_LOADING = R.id.view_loading;
    protected static final int ANIMATOR_VIEW_CONTENT = R.id.movies_recycler_view;
    protected static final int ANIMATOR_VIEW_ERROR = R.id.view_error;
    protected static final int ANIMATOR_VIEW_EMPTY = R.id.view_empty;

    @Inject MoviesRepository mMoviesRepository;

    @Bind(R.id.multi_swipe_refresh_layout) MultiSwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.movies_animator) BetterViewAnimator mViewAnimator;
    @Bind(R.id.movies_recycler_view) RecyclerView mRecyclerView;

    protected MoviesHelper mHelper;
    protected Listener listener;
    protected MoviesAdapter mMoviesAdapter;
    protected GridLayoutManager mGridLayoutManager;
    protected CompositeSubscription mSubscriptions;
    protected int mSelectedPosition = -1;

    @Override
    public void onAttach(Activity activity) {
        if (!(activity instanceof Listener)) {
            throw new IllegalStateException("Activity must implement MoviesFragment.Listener.");
        }

        super.onAttach(activity);
        listener = (Listener) activity;
        mHelper = new MoviesHelper(activity, mMoviesRepository);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movies, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSubscriptions = new CompositeSubscription();

        mSelectedPosition = savedInstanceState != null
                ? savedInstanceState.getInt(STATE_SELECTED_POSITION, -1)
                : -1;

        List<Movie> restoredMovies = savedInstanceState != null
                ? savedInstanceState.getParcelableArrayList(STATE_MOVIES)
                : new ArrayList<>();

        mMoviesAdapter = new MoviesAdapter(this, restoredMovies);
        mMoviesAdapter.setListener(this);

        initSwipeRefreshLayout();
        initRecyclerView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_MOVIES, new ArrayList<>(mMoviesAdapter.getItems()));
        outState.putInt(STATE_SELECTED_POSITION, mSelectedPosition);
    }

    @Override
    public void onDestroyView() {
        mSubscriptions.unsubscribe();
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        listener = (movie, view) -> {};
        mMoviesAdapter.setListener(MoviesAdapter.OnMovieClickListener.DUMMY);
        super.onDetach();
    }

    @Override
    public boolean canSwipeRefreshChildScrollUp() {
        return mRecyclerView != null && ViewCompat.canScrollVertically(mRecyclerView, -1)
                || mViewAnimator != null && mViewAnimator.getDisplayedChildId() == ANIMATOR_VIEW_LOADING;
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
    public void onContentClicked(@NonNull Movie movie, View view, int position) {
        mSelectedPosition = position;
        listener.onMovieSelected(movie, view);
    }

    @Override
    public void onFavoredClicked(@NonNull final Movie movie, int position) {
        boolean favored = !movie.isFavored();
        Timber.v("onFavoredClicked: favored=" + favored);

        mHelper.setMovieFavored(movie, favored);
        if (favored) showToast(R.string.message_movie_favored);
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
        if (mSelectedPosition != -1) mRecyclerView.scrollToPosition(mSelectedPosition);
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
