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


import com.ewintory.udacity.popularmovies.data.api.MoviesApi;
import com.ewintory.udacity.popularmovies.data.model.Genre;
import com.ewintory.udacity.popularmovies.data.provider.MoviesContract;
import com.squareup.sqlbrite.BriteContentResolver;

import java.util.Map;

import rx.Observable;
import rx.schedulers.Schedulers;

final class GenresRepositoryImpl implements GenresRepository {

    private final MoviesApi mMoviesApi;
    private final BriteContentResolver mBriteContentResolver;

    public GenresRepositoryImpl(MoviesApi moviesApi, BriteContentResolver briteContentResolver) {
        mMoviesApi = moviesApi;
        mBriteContentResolver = briteContentResolver;
    }

    @Override
    public Observable<Map<Integer, Genre>> genres() {
        return mBriteContentResolver.createQuery(MoviesContract.Genres.CONTENT_URI, Genre.PROJECTION, null, null, null, true)
                .map(Genre.PROJECTION_MAP)
                .subscribeOn(Schedulers.io());
    }
}
