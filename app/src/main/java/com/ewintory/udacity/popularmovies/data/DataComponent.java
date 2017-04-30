package com.ewintory.udacity.popularmovies.data;


import com.ewintory.udacity.popularmovies.AppModule;
import com.ewintory.udacity.popularmovies.ui.movies.MoviesEndlessFragment;
import com.ewintory.udacity.popularmovies.ui.movies.MoviesFilterFragment;
import com.ewintory.udacity.popularmovies.ui.movies.MoviesFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Инджектор зависимостей для {@link DataModule}
 *
 * @author Emin Yahyayev (yahyayev@iteratia.com)
 */
@Singleton
@Component(modules = {AppModule.class, DataModule.class})
public interface DataComponent {

    void inject(GlideSetup glideSetup);

    void inject(MoviesFragment fragment);

    void inject(MoviesEndlessFragment fragment);

    void inject(MoviesFilterFragment fragment);
}
