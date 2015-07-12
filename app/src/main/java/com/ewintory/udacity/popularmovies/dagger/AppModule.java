package com.ewintory.udacity.popularmovies.dagger;

import android.app.Application;

import com.ewintory.udacity.popularmovies.App;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        includes = DataModule.class,
        injects = {
                App.class
        },
        library = true
)
public final class AppModule {
    private final App application;

    public AppModule(App application) {
        this.application = application;
    }

    @Provides @Singleton Application provideApplication() {
        return application;
    }

}

