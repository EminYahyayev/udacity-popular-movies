package com.ewintory.udacity.popularmovies.ui.fragment;


import android.support.annotation.IdRes;

import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public final class FavoredMoviesFragment extends MoviesFragment {

    private Subscription mFavoritesSubscription = Subscriptions.empty();

    @Override
    public void onStart() {
        super.onStart();
        subscribeToMovies();
    }

    @Override
    public void onStop() {
        super.onStop();
        mFavoritesSubscription.unsubscribe();
    }

    @Override
    public void onRefresh() {
        subscribeToMovies();
    }

    private void subscribeToMovies() {
        if (!mSwipeRefreshLayout.isRefreshing())
            mViewAnimator.setDisplayedChildId(ANIMATOR_VIEW_LOADING);

        mFavoritesSubscription.unsubscribe();
        mFavoritesSubscription = bind(mMoviesRepository.savedMovies())
                .subscribe(movies -> {
                    Timber.d(String.format("Favored movies loaded, %d items", movies.size()));
                    mSwipeRefreshLayout.setRefreshing(false);
                    mMoviesAdapter.set(movies);
                    mViewAnimator.setDisplayedChildId(getContentView());
                }, throwable -> {
                    Timber.e(throwable, "Favored movies loading failed");
                    mViewAnimator.setDisplayedChildId(ANIMATOR_VIEW_ERROR);
                });
    }

    @IdRes
    protected final int getContentView() {
        return mMoviesAdapter.getItemCount() > 0 ? ANIMATOR_VIEW_CONTENT : ANIMATOR_VIEW_EMPTY;
    }
}
