package com.ewintory.udacity.popularmovies.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ewintory.udacity.popularmovies.R;
import com.ewintory.udacity.popularmovies.data.model.Genre;
import com.ewintory.udacity.popularmovies.data.model.Movie;

import java.util.ArrayList;
import java.util.List;

final class DbOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "movie.db";
    private static final int VERSION = 1;

    private static final String CREATE_GENRE = ""
            + "CREATE TABLE " + Genre.TABLE + "("
            + Genre.ID + " INTEGER NOT NULL PRIMARY KEY,"
            + Genre.NAME + " TEXT NOT NULL"
            + ")";
    private static final String CREATE_MOVIE = ""
            + "CREATE TABLE " + Movie.TABLE + "("
            + Movie.ID + " INTEGER NOT NULL PRIMARY KEY,"
            + Movie.GENRE_IDS + " INTEGER REFERENCES " + Genre.TABLE + "(" + Genre.ID + "),"
            + Movie.TITLE + " TEXT NOT NULL,"
            + Movie.OVERVIEW + " TEXT,"
            + Movie.POPULARITY + " REAL,"
            + Movie.VOTE_AVERAGE + " REAL,"
            + Movie.VOTE_COUNT + " INTEGER,"
            + Movie.BACKDROP_PATH + " TEXT,"
            + Movie.POSTER_PATH + " TEXT,"
            + Movie.FAVORED + " INTEGER NOT NULL DEFAULT 0"
            + ")";
    private static final String CREATE_MOVIE_GENRE_ID_INDEX =
            "CREATE INDEX movie_genre_id ON " + Movie.TABLE + " (" + Movie.GENRE_IDS + ")";

    private final Context mContext;

    public DbOpenHelper(Context context) {
        super(context, DB_NAME, null /* factory */, VERSION);
        mContext = context;
    }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_GENRE);
        db.execSQL(CREATE_MOVIE);
        db.execSQL(CREATE_MOVIE_GENRE_ID_INDEX);

        List<Genre> genres = getGenresList();
        for (Genre genre : genres)
            db.insert(Genre.TABLE, null, new Genre.Builder()
                    .id(genre.getId())
                    .name(genre.getName())
                    .build());
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }

    private List<Genre> getGenresList() {
        final int[] ids = mContext.getResources().getIntArray(R.array.genre_ids);
        final String[] names = mContext.getResources().getStringArray(R.array.genre_names);

        ArrayList<Genre> genres = new ArrayList<>(ids.length);
        for (int i = 0; i < ids.length; i++)
            genres.add(new Genre(ids[i], names[i]));

        return genres;
    }
}