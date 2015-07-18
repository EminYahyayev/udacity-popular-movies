package com.ewintory.udacity.popularmovies;

import android.app.Application;

import com.ewintory.udacity.popularmovies.data.DataModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        includes = DataModule.class,
        injects = {
                MoviesApp.class
        },
        library = true
)
public final class AppModule {
    private final MoviesApp application;

    public AppModule(MoviesApp application) {
        this.application = application;
    }

    @Provides @Singleton Application provideApplication() {
        return application;
    }

}

