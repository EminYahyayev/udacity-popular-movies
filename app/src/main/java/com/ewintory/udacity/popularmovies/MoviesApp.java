package com.ewintory.udacity.popularmovies;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import dagger.ObjectGraph;
import timber.log.Timber;

public final class MoviesApp extends Application {

    private ObjectGraph objectGraph;
    private RefWatcher refWatcher;

    public static MoviesApp get(Context context) {
        return (MoviesApp) context.getApplicationContext();
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
        return LeakCanary.install(this);
    }

    private ObjectGraph initializeObjectGraph() {
        return buildInitialObjectGraph(new AppModule(this));
    }

    private ObjectGraph buildInitialObjectGraph(Object... modules) {
        return ObjectGraph.create(modules);
    }
}
