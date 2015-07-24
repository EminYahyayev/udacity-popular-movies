package com.ewintory.udacity.popularmovies.data;

import android.app.Application;

import com.ewintory.udacity.popularmovies.data.api.ApiModule;
import com.ewintory.udacity.popularmovies.data.api.MockMoviesApi;
import com.ewintory.udacity.popularmovies.data.api.MoviesApi;

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

    @Provides @Singleton MoviesApi provideMoviesService(RestAdapter restAdapter, Application application) {
        return new MockMoviesApi(application);
    }
}
