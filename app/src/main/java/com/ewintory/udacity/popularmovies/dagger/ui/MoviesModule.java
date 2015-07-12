package com.ewintory.udacity.popularmovies.dagger.ui;


import com.ewintory.udacity.popularmovies.dagger.AppModule;
import com.ewintory.udacity.popularmovies.ui.fragment.MoviesFragment;

import dagger.Module;

@Module(
        injects = {
                MoviesFragment.class
        },
        addsTo = AppModule.class
)
public final class MoviesModule {}
