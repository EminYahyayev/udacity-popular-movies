package com.ewintory.udacity.popularmovies;

import com.ewintory.udacity.popularmovies.data.MockApiModule;

import dagger.Module;

@Module(
        addsTo = AppModule.class,
        includes = {
//                MockApiModule.class
        },
        overrides = true
)
public final class DebugAppModule {}
