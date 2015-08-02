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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.ewintory.udacity.popularmovies.data.model.Genre;

import static com.ewintory.udacity.popularmovies.data.provider.MoviesContract.*;
import static com.ewintory.udacity.popularmovies.data.provider.MoviesContract.Genres;
import static com.ewintory.udacity.popularmovies.data.provider.MoviesContract.Movies;

final class MoviesDatabase extends SQLiteOpenHelper {
    private static final String DB_NAME = "movies.db";
    private static final int DB_VERSION = 1;

    private final Context mContext;

    interface Tables {
        String MOVIES = "movies";
        String GENRES = "genres";
        String MOVIES_GENRES = "movies_genres";

        String MOVIES_JOIN_GENRES = "movies "
                + "LEFT OUTER JOIN movies_genres ON movies.movie_id=movies_genres.genre_id";

        String MOVIES_GENRES_JOIN_GENRES = "movies_genres "
                + "LEFT OUTER JOIN genres ON movies_genres.genre_id=genres.genre_id";
    }

    public interface MoviesGenres {
        String MOVIE_ID = "movie_id";
        String GENRE_ID = "genre_id";
    }

    private interface Qualified {
        String MOVIES_GENRES_MOVIE_ID = Tables.MOVIES_GENRES + "." + MoviesGenres.MOVIE_ID;
    }

    private interface References {
        String GENRE_ID = "REFERENCES " + Tables.GENRES + "(" + Genres.GENRE_ID + ")";
        String MOVIE_ID = "REFERENCES " + Tables.MOVIES + "(" + Movies.MOVIE_ID + ")";
    }

    public MoviesDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Tables.GENRES + "("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + GenresColumns.GENRE_ID + " INTEGER NOT NULL,"
                + GenresColumns.GENRE_NAME + " TEXT NOT NULL,"
                + "UNIQUE (" + GenresColumns.GENRE_ID + ") ON CONFLICT REPLACE)");

        db.execSQL("CREATE TABLE " + Tables.MOVIES + "("
                + BaseColumns._ID + " INTEGER NOT NULL PRIMARY KEY,"
                + MoviesColumns.MOVIE_ID + " TEXT NOT NULL,"
                + MoviesColumns.MOVIE_TITLE + " TEXT NOT NULL,"
                + MoviesColumns.MOVIE_OVERVIEW + " TEXT,"
                + MoviesColumns.MOVIE_GENRE_IDS + " TEXT,"
                + MoviesColumns.MOVIE_POPULARITY + " REAL,"
                + MoviesColumns.MOVIE_VOTE_AVERAGE + " REAL,"
                + MoviesColumns.MOVIE_VOTE_COUNT + " INTEGER,"
                + MoviesColumns.MOVIE_BACKDROP_PATH + " TEXT,"
                + MoviesColumns.MOVIE_POSTER_PATH + " TEXT,"
                + MoviesColumns.MOVIE_RELEASE_DATE + " TEXT,"
                + MoviesColumns.MOVIE_FAVORED + " INTEGER NOT NULL DEFAULT 0,"
                + "UNIQUE (" + MoviesColumns.MOVIE_ID + ") ON CONFLICT REPLACE)");

        db.execSQL("CREATE TABLE " + Tables.MOVIES_GENRES + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + MoviesGenres.MOVIE_ID + " TEXT NOT NULL " + References.MOVIE_ID + ","
                + MoviesGenres.GENRE_ID + " TEXT NOT NULL " + References.GENRE_ID + ","
                + "UNIQUE (" + MoviesGenres.MOVIE_ID + "," + MoviesGenres.GENRE_ID + ") ON CONFLICT REPLACE)");

        insertGenres(db);
    }

    /**
     * Inserts predefined list of movie genres taken from Movie Database.
     * Ideally we should fetch them from api on first launch.
     */
    private void insertGenres(SQLiteDatabase db) {
        db.insert(Tables.GENRES, null, new Genre.Builder().id(28).name("Action").build());
        db.insert(Tables.GENRES, null, new Genre.Builder().id(12).name("Adventure").build());
        db.insert(Tables.GENRES, null, new Genre.Builder().id(16).name("Animation").build());
        db.insert(Tables.GENRES, null, new Genre.Builder().id(35).name("Comedy").build());
        db.insert(Tables.GENRES, null, new Genre.Builder().id(80).name("Crime").build());
        db.insert(Tables.GENRES, null, new Genre.Builder().id(99).name("Documentary").build());
        db.insert(Tables.GENRES, null, new Genre.Builder().id(18).name("Drama").build());
        db.insert(Tables.GENRES, null, new Genre.Builder().id(10751).name("Family").build());
        db.insert(Tables.GENRES, null, new Genre.Builder().id(14).name("Fantasy").build());
        db.insert(Tables.GENRES, null, new Genre.Builder().id(10765).name("Foreign").build());
        db.insert(Tables.GENRES, null, new Genre.Builder().id(36).name("History").build());
        db.insert(Tables.GENRES, null, new Genre.Builder().id(27).name("Horror").build());
        db.insert(Tables.GENRES, null, new Genre.Builder().id(10402).name("Music").build());
        db.insert(Tables.GENRES, null, new Genre.Builder().id(9648).name("Mystery").build());
        db.insert(Tables.GENRES, null, new Genre.Builder().id(10749).name("Romance").build());
        db.insert(Tables.GENRES, null, new Genre.Builder().id(878).name("Science Fiction").build());
        db.insert(Tables.GENRES, null, new Genre.Builder().id(10770).name("TV Movie").build());
        db.insert(Tables.GENRES, null, new Genre.Builder().id(53).name("Thriller").build());
        db.insert(Tables.GENRES, null, new Genre.Builder().id(10752).name("War").build());
        db.insert(Tables.GENRES, null, new Genre.Builder().id(37).name("Western").build());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }

    public static void deleteDatabase(Context context) {
        context.deleteDatabase(DB_NAME);
    }
}