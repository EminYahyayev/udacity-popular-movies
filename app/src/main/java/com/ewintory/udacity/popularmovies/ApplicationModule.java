/*
 * Copyright 2015.  Emin Yahyayev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ewintory.udacity.popularmovies;

import android.app.Application;

import com.ewintory.udacity.popularmovies.data.DataModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        includes = DataModule.class,
        injects = {
                PopularMoviesApplication.class
        },
        library = true
)
public final class ApplicationModule {
    private final PopularMoviesApplication application;

    public ApplicationModule(PopularMoviesApplication application) {
        this.application = application;
    }

    @Provides @Singleton Application provideApplication() {
        return application;
    }

}

