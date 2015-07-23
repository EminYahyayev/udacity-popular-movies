package com.ewintory.udacity.popularmovies.data;

import android.app.Application;

import com.ewintory.udacity.popularmovies.data.api.ApiModule;
import com.ewintory.udacity.popularmovies.data.api.MockMovieDB;
import com.ewintory.udacity.popularmovies.data.api.MovieDB;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;

@Module(
        addsTo = ApiModule.class,
        complete = false,
        library = true,
        overrides = true
)
public final class MockApiModule {

    @Provides @Singleton MovieDB provideMoviesService(RestAdapter restAdapter, Application application) {
        return new MockMovieDB(application);
    }
}
