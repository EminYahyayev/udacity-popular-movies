package com.ewintory.udacity.popularmovies.ui.fragment;

import android.support.annotation.NonNull;

import com.ewintory.udacity.popularmovies.data.api.Sort;
import com.ewintory.udacity.popularmovies.data.model.Movie;

import rx.Observable;
import timber.log.Timber;

public final class SortedMoviesFragment extends BaseMoviesFragment {

    private Sort mSort;

    @Override public void onRefresh() {
        if (mSort != null) reloadFromSort(mSort);
    }

    public void reloadFromSort(@NonNull Sort sort) {
        Timber.d("reloadFromSort: sort=" + sort);
        mSort = sort;
        reloadContent();
    }

    @Override protected Observable<Movie.Response> buildContentObservable() {
        return Observable.concat(
                mMoviesService.discoverMovies(mSort, 1),
                mMoviesService.discoverMovies(mSort, 2),
                mMoviesService.discoverMovies(mSort, 3),
                mMoviesService.discoverMovies(mSort, 4),
                mMoviesService.discoverMovies(mSort, 5),
                mMoviesService.discoverMovies(mSort, 6));
    }
}

