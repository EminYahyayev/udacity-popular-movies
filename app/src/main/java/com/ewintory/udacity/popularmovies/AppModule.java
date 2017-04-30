package com.ewintory.udacity.popularmovies;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author Emin Yahyayev (yahyayev@iteratia.com)
 */
@Module
public final class AppModule {

    private Application application;

    AppModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton Application application() {
        return application;
    }
}
