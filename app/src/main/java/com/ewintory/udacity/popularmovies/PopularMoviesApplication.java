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
import android.content.Context;

import com.squareup.leakcanary.RefWatcher;

import dagger.ObjectGraph;
import timber.log.Timber;

public final class PopularMoviesApplication extends Application {

    private ObjectGraph objectGraph;
    private RefWatcher refWatcher;

    public static PopularMoviesApplication get(Context context) {
        return (PopularMoviesApplication) context.getApplicationContext();
    }

    @Override public void onCreate() {
        super.onCreate();

        refWatcher = installLeakCanary();
        objectGraph = initializeObjectGraph();

        Timber.plant(new Timber.DebugTree());
    }

    public RefWatcher getRefWatcher() {
        return refWatcher;
    }

    public ObjectGraph buildScopedObjectGraph(Object... modules) {
        return objectGraph.plus(modules);
    }

    public void inject(Object o) {
        objectGraph.inject(o);
    }

    protected RefWatcher installLeakCanary() {
        //return LeakCanary.install(this);
        return RefWatcher.DISABLED;
    }

    private ObjectGraph initializeObjectGraph() {
        return buildInitialObjectGraph(new ApplicationModule(this));
    }

    private ObjectGraph buildInitialObjectGraph(Object... modules) {
        return ObjectGraph.create(modules);
    }
}
