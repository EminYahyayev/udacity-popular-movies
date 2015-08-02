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

package com.ewintory.udacity.popularmovies.data.provider;


import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract class for interacting with {@link MoviesProvider}.
 */
public final class MoviesContract {

    public static final String QUERY_PARAMETER_DISTINCT = "distinct";

    public interface GenresColumns {
        String GENRE_ID = "genre_id";
        String GENRE_NAME = "genre_name";
    }

    public interface MoviesColumns {
        String MOVIE_ID = "movie_id";
        String MOVIE_TITLE = "movie_title";
        String MOVIE_OVERVIEW = "movie_overview";
        String MOVIE_POPULARITY = "movie_popularity";
        String MOVIE_GENRE_IDS = "movie_genre_ids"; //This is a comma-separated list of genre ids.
        String MOVIE_VOTE_COUNT = "movie_vote_count";
        String MOVIE_VOTE_AVERAGE = "movie_vote_average";
        String MOVIE_RELEASE_DATE = "movie_release_date";
        String MOVIE_FAVORED = "movie_favored";
        String MOVIE_POSTER_PATH = "movie_poster_path";
        String MOVIE_BACKDROP_PATH = "movie_backdrop_path";
    }

    public static final String CONTENT_AUTHORITY = "com.ewintory.udacity.popularmovies.provider";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final String PATH_GENRES = "genres";
    private static final String PATH_MOVIES = "movies";

    /**
     * Genres represent Movie classifications. A movie can have many genres.
     */
    public static class Genres implements GenresColumns, BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_GENRES).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.popularmovies.genre";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.popularmovies.genre";

        /**
         * Build {@link Uri} that references all genres.
         */
        public static Uri buildGenresUri() {
            return CONTENT_URI;
        }

        /** Build a {@link Uri} that references a given genre. */
        public static Uri buildGenreUri(String genreId) {
            return CONTENT_URI.buildUpon().appendPath(genreId).build();
        }
    }

    /**
     * Each movie has zero or more {@link Genres}.
     */
    public static class Movies implements MoviesColumns, BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.popularmovies.movie";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.popularmovies.movie";

        /** Default "ORDER BY" clause. */
        public static final String DEFAULT_SORT = BaseColumns._ID + " DESC";

        /** Build {@link Uri} for requested {@link #MOVIE_ID}. */
        public static Uri buildMovieUri(String movieId) {
            return CONTENT_URI.buildUpon().appendPath(movieId).build();
        }

        /**
         * Build {@link Uri} that references any {@link Genres} associated with
         * the requested {@link #MOVIE_ID}.
         */
        public static Uri buildGenresDirUri(String movieId) {
            return CONTENT_URI.buildUpon().appendPath(movieId).appendPath(PATH_GENRES).build();
        }

        /** Read {@link #MOVIE_ID} from {@link Movies} {@link Uri}. */
        public static String getMovieId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    private MoviesContract() {
        throw new AssertionError("No instances.");
    }
}
