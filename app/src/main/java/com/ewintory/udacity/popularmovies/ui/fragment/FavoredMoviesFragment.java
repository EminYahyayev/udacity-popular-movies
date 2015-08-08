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


import android.support.annotation.IdRes;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
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
        mFavoritesSubscription = mMoviesRepository.savedMovies()
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
