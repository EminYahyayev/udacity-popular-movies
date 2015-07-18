package com.ewintory.udacity.popularmovies.ui.dagger;


import com.ewintory.udacity.popularmovies.AppModule;
import com.ewintory.udacity.popularmovies.ui.fragment.MovieFragment;
import com.ewintory.udacity.popularmovies.ui.fragment.SortedMoviesFragment;

import dagger.Module;

@Module(
        injects = {
                SortedMoviesFragment.class,
                MovieFragment.class
        },
        addsTo = AppModule.class
)
public final class MoviesModule {}
