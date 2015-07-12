package com.ewintory.udacity.popularmovies.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ewintory.udacity.popularmovies.R;
import com.ewintory.udacity.popularmovies.dagger.ui.MoviesModule;
import com.ewintory.udacity.popularmovies.data.model.Movie;
import com.ewintory.udacity.popularmovies.data.model.MoviesResponse;
import com.ewintory.udacity.popularmovies.data.model.Sort;
import com.ewintory.udacity.popularmovies.data.service.MoviesService;
import com.ewintory.udacity.popularmovies.ui.adapter.MoviesAdapter;
import com.ewintory.udacity.popularmovies.ui.listener.MovieClickListener;
import com.ewintory.udacity.popularmovies.ui.widget.BetterViewAnimator;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.Subscriptions;

public final class MoviesFragment extends BaseFragment {
    public static final String TAG = MoviesFragment.class.getSimpleName();

    private static final int ANIMATOR_VIEW_LOADING = R.id.view_loading;
    private static final int ANIMATOR_VIEW_ERROR = R.id.view_error;
    private static final int ANIMATOR_VIEW_CONTENT = R.id.movies_recycler_view;

    private static MovieClickListener sDummyListener = new MovieClickListener.SimpleListener();

    @Inject MoviesService mMoviesService;

    @Bind(R.id.movies_animator) BetterViewAnimator mViewAnimator;
    @Bind(R.id.movies_recycler_view) RecyclerView mRecyclerView;

    private MoviesAdapter mMoviesAdapter;
    private Subscription mSubscription = Subscriptions.empty();

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movies, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeRecyclerView();
    }

    @Override public void onDestroyView() {
        mSubscription.unsubscribe();
        mMoviesAdapter.setListener(sDummyListener);
        super.onDestroyView();
    }

    public void scrollToTop() {
        mRecyclerView.scrollToPosition(0);
    }

    public void reloadMovies(@NonNull Sort sort) {
        mViewAnimator.setDisplayedChildId(ANIMATOR_VIEW_LOADING);
        mMoviesAdapter.clear();

        mSubscription.unsubscribe();
        mSubscription = bind(mMoviesService.discoverMovies(sort, 1))
                .subscribe(new Action1<MoviesResponse>() {
                    @Override public void call(MoviesResponse moviesResponse) {
                        List<Movie> newMovies = moviesResponse.getMovies();
                        Log.v(TAG, "New movies loaded, page=" + moviesResponse.getPage() + " size=" + newMovies.size());
                        mMoviesAdapter.add(newMovies);
                        mViewAnimator.setDisplayedChildId(ANIMATOR_VIEW_CONTENT);
                    }
                }, new Action1<Throwable>() {
                    @Override public void call(Throwable throwable) {
                        Log.e(TAG, "Movies loading failed", throwable);
                        mViewAnimator.setDisplayedChildId(ANIMATOR_VIEW_ERROR);
                    }
                });
    }

    private void initializeRecyclerView() {
        int spanCount = getResources().getInteger(R.integer.movies_columns);

        final GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), spanCount);

        if (!(getActivity() instanceof MovieClickListener)) {
            throw new ClassCastException("Activity must implement MovieClickListener.");
        }
        mMoviesAdapter = new MoviesAdapter(this, (MovieClickListener) getActivity());

        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setVerticalScrollBarEnabled(false);
        mRecyclerView.setFocusable(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mMoviesAdapter);
    }

    @Override protected List<Object> getModules() {
        return Collections.<Object>singletonList(new MoviesModule());
    }
}

