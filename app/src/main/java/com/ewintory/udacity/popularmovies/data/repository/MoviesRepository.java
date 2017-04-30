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

package com.ewintory.udacity.popularmovies.data.repository;


import android.content.Context;

import com.ewintory.udacity.popularmovies.data.api.MoviesApi;
import com.ewintory.udacity.popularmovies.data.api.Sort;
import com.ewintory.udacity.popularmovies.data.model.Movie;
import com.ewintory.udacity.popularmovies.data.repository.action.PageAction;
import com.ewintory.udacity.popularmovies.data.repository.result.PageResult;
import com.squareup.sqlbrite.BriteDatabase;

import java.net.UnknownHostException;
import java.util.ArrayList;

import rx.Observable.Transformer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * @author Emin Yahyayev
 */
public final class MoviesRepository extends BaseRepository {

    public static final int FIRST_PAGE = 1;

    public MoviesRepository(Context appContext, MoviesApi api, BriteDatabase database) {
        super(appContext, api, database);
    }

    public Transformer<PageAction, PageResult<Movie>> discover() {
        return actions -> actions.doOnNext(action -> Timber.d("discover > action: %s", action))
                .flatMap(action -> api.discoverMovies(Sort.POPULARITY, action.page)
                        .map(response -> {
                            if (response.isSuccessful()) {
                                if (action.page > 2)
                                    return PageResult.success(action.page, new ArrayList<Movie>());
                                else
                                    return PageResult.success(action.page, response.body().results);
                            } else {
                                Timber.e("Failed to load movies. Message: %s", response.message());
                                return PageResult.<Movie>failure(action.page,
                                        "Failed to load movies.\nTry again later.");
                            }
                        })
                        .onErrorReturn(t -> {
                            String errorMessage;
                            if (t instanceof UnknownHostException) {
                                errorMessage = "Failed to load movies.\nPlease check your network connection.";
                            } else {
                                Timber.e(t);
                                errorMessage = "Failed to load movies.\nTry again later.";
                            }
                            return PageResult.failure(action.page, errorMessage);
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .startWith(PageResult.inFlight(action.page)))
                .doOnNext(result -> Timber.d("discover > result: %s", result));
    }

}
