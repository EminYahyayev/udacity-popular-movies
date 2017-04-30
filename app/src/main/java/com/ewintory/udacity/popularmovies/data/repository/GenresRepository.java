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
import com.ewintory.udacity.popularmovies.data.model.Genre;
import com.ewintory.udacity.popularmovies.data.repository.action.Action;
import com.ewintory.udacity.popularmovies.data.repository.result.CollectionResult;
import com.squareup.sqlbrite.BriteDatabase;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;


/**
 * @author Emin Yahyayev
 */
public final class GenresRepository extends BaseRepository {

    public GenresRepository(Context appContext, MoviesApi api, BriteDatabase database) {
        super(appContext, api, database);
    }

    public Observable.Transformer<Action, CollectionResult<Genre>> genres() {
        return actions -> actions.doOnNext(action -> Timber.d("genres > action: %s", action))
                .flatMap(action -> api.genres()
                        .map(response -> {
                            if (response.isSuccessful()) {
                                return CollectionResult.success(response.body().genres);
                            } else {
                                return CollectionResult.<Genre>failure(response.message());
                            }
                        })
                        .onErrorReturn(t -> CollectionResult.failure(t.getMessage()))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .startWith(CollectionResult.inFlight()))
                .doOnNext(result -> Timber.d("genres > result: %s", result));
    }

}
