package com.ewintory.udacity.popularmovies;

import java.util.Arrays;
import java.util.List;

final class Modules {
    public static final String TAG = Modules.class.getSimpleName();

    private Modules() {
        throw new AssertionError("Modules cannot be initialized.");
    }

    public static List<Object> getModules(MoviesApp application) {
        return Arrays.asList(
                new AppModule(application),
                new DebugAppModule()
        );
    }
}
