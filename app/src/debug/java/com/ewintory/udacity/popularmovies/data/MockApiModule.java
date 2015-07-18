package com.ewintory.udacity.popularmovies.data;

import android.app.Application;

import com.ewintory.udacity.popularmovies.data.api.ApiModule;
import com.ewintory.udacity.popularmovies.data.api.MockMoviesService;
import com.ewintory.udacity.popularmovies.data.api.MoviesService;

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

    @Provides @Singleton MoviesService provideMoviesService(RestAdapter restAdapter, Application application) {
        return new MockMoviesService(application);
    }
}
