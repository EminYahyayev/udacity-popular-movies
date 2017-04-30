package com.ewintory.udacity.popularmovies.data.repository;

import android.content.Context;

import com.ewintory.udacity.popularmovies.data.api.MoviesApi;
import com.squareup.sqlbrite.BriteDatabase;

/**
 * Базовый класс для реализации репозитория
 *
 * @author Emin Yahyayev
 */
abstract class BaseRepository {

    protected final Context appContext;
    protected final MoviesApi api;
    protected final BriteDatabase database;

    BaseRepository(Context appContext, MoviesApi api, BriteDatabase database) {
        this.appContext = appContext;
        this.api = api;
        this.database = database;
    }
}
