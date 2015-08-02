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

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;

import com.ewintory.udacity.popularmovies.data.api.MoviesApi;
import com.ewintory.udacity.popularmovies.data.api.Sort;
import com.ewintory.udacity.popularmovies.data.model.Genre;
import com.ewintory.udacity.popularmovies.data.model.Movie;
import com.ewintory.udacity.popularmovies.data.model.Review;
import com.ewintory.udacity.popularmovies.data.model.Video;
import com.ewintory.udacity.popularmovies.utils.Lists;
import com.squareup.sqlbrite.BriteContentResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

import static com.ewintory.udacity.popularmovies.data.provider.MoviesContract.Movies;

final class MoviesRepositoryImpl implements MoviesRepository {

    private final MoviesApi mMoviesApi;
    private final ContentResolver mContentResolver;
    private final BriteContentResolver mBriteContentResolver;
    private final GenresRepository mGenresRepository;

    private BehaviorSubject<Set<Long>> mSavedMovieIdsSubject;
    private BehaviorSubject<Map<Integer, Genre>> mGenresSubject;

    public MoviesRepositoryImpl(MoviesApi moviesApi, ContentResolver contentResolver,
                                BriteContentResolver briteContentResolver, GenresRepository genresRepository) {
        mGenresRepository = genresRepository;
        mMoviesApi = moviesApi;
        mContentResolver = contentResolver;
        mBriteContentResolver = briteContentResolver;
    }

    @Override
    public Observable<List<Movie>> discoverMovies(Sort sort, int page) {
        return mMoviesApi.discoverMovies(sort, page)
                .timeout(5, TimeUnit.SECONDS)
                .retry(2)
                .map(response -> response.movies)
                .withLatestFrom(getSavedMovieIds(), (movies, favoredIds) -> {
                    for (Movie movie : movies)
                        movie.setFavored(favoredIds.contains(movie.getId()));
                    return movies;
                })
                .withLatestFrom(getGenresMap(), GENRES_MAPPER)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<Movie>> savedMovies() {
        return mBriteContentResolver.createQuery(Movies.CONTENT_URI, Movie.PROJECTION, null, null, Movies.DEFAULT_SORT, true)
                .map(Movie.PROJECTION_MAP)
                .withLatestFrom(getGenresMap(), GENRES_MAPPER)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<Set<Long>> savedMovieIds() {
        return mBriteContentResolver.createQuery(Movies.CONTENT_URI, Movie.ID_PROJECTION, null, null, null, true)
                .map(Movie.ID_PROJECTION_MAP)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public void saveMovie(Movie movie) {
        AsyncQueryHandler handler = new AsyncQueryHandler(mContentResolver) {};
        handler.startInsert(-1, null, Movies.CONTENT_URI, new Movie.Builder()
                .movie(movie)
                .build());
    }

    @Override
    public void deleteMovie(Movie movie) {
        String where = Movies.MOVIE_ID + "=?";
        String[] args = new String[]{String.valueOf(movie.getId())};

        AsyncQueryHandler handler = new AsyncQueryHandler(mContentResolver) {};
        handler.startDelete(-1, null, Movies.CONTENT_URI, where, args);
    }

    @Override
    public Observable<List<Review>> reviews(long movieId) {
        return mMoviesApi.reviews(movieId, 1)
                .timeout(5, TimeUnit.SECONDS)
                .retry(2)
                .map(response -> response.reviews);
    }

    @Override
    public Observable<List<Video>> videos(long movieId) {
        return mMoviesApi.videos(movieId)
                .timeout(2, TimeUnit.SECONDS)
                .retry(2)
                .map(response -> response.videos);
    }

    private Observable<Set<Long>> getSavedMovieIds() {
        if (mSavedMovieIdsSubject == null) {
            mSavedMovieIdsSubject = BehaviorSubject.create();
            savedMovieIds().subscribe(mSavedMovieIdsSubject);
        }
        return mSavedMovieIdsSubject.asObservable();
    }

    private Observable<Map<Integer, Genre>> getGenresMap() {
        if (mGenresSubject == null) {
            mGenresSubject = BehaviorSubject.create();
            mGenresRepository.genres().subscribe(mGenresSubject);
        }
        return mGenresSubject.asObservable();
    }

    private static Func2<List<Movie>, Map<Integer, Genre>, List<Movie>> GENRES_MAPPER = (movies, genreMap) -> {
        for (Movie movie : movies) {
            List<Integer> genreIds = movie.getGenreIds();
            if (Lists.isEmpty(genreIds)) continue;

            List<Genre> genres = new ArrayList<>(genreIds.size());
            for (Integer id : genreIds)
                genres.add(genreMap.get(id));
            movie.setGenres(genres);
        }
        return movies;
    };
}
