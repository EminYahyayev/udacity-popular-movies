package com.ewintory.udacity.popularmovies.ui.module;


import com.ewintory.udacity.popularmovies.AppModule;
import com.ewintory.udacity.popularmovies.ui.fragment.MovieFragment;
import com.ewintory.udacity.popularmovies.ui.fragment.MoviesFragment;

import dagger.Module;

@Module(
        injects = {
                MoviesFragment.class,
                MovieFragment.class
        },
        addsTo = AppModule.class
)
public final class MoviesModule {}
