package com.ewintory.udacity.popularmovies.ui.fragment;


import android.support.annotation.IdRes;

import com.ewintory.udacity.popularmovies.data.model.Movie;

import java.util.concurrent.atomic.AtomicInteger;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public final class FavoredMoviesFragment extends MoviesFragment {

    private static final String FAVORED_QUERY = "SELECT * FROM " + Movie.TABLE
            + " WHERE " + Movie.FAVORED + " = ?";

    private Subscription mMoviesSubscription = Subscriptions.empty();
    final AtomicInteger mQueriesCount = new AtomicInteger();

    @Override
    public void onResume() {
        super.onResume();
        loadMovies();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMoviesSubscription.unsubscribe();
    }

    @Override
    public void onRefresh() {
        loadMovies();
    }

//    @Override
//    public boolean onFavoredClicked(@NonNull final Movie movie) {
//        Observable.timer(1, TimeUnit.SECONDS)
//                .subscribeOn(AndroidSchedulers.mainThread())
//                .subscribe(aLong -> {
//                    super.onFavoredClicked(movie);
//                });
//        return true;
//    }

    private void loadMovies() {
        if (!mSwipeRefreshLayout.isRefreshing())
            mViewAnimator.setDisplayedChildId(ANIMATOR_VIEW_LOADING);

        mQueriesCount.set(0);
        mMoviesSubscription.unsubscribe();
        mMoviesSubscription = bind(db.createQuery(Movie.TABLE, FAVORED_QUERY, "1")
                .map(Movie.MAP)
                .subscribeOn(Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
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
