package com.ewintory.udacity.popularmovies;

import android.app.Application;
import android.content.Context;

import com.ewintory.udacity.popularmovies.dagger.AppModule;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import dagger.ObjectGraph;

public final class App extends Application {

    private ObjectGraph objectGraph;
    private RefWatcher refWatcher;

    public static App get(Context context) {
        return (App) context.getApplicationContext();
    }

    @Override public void onCreate() {
        super.onCreate();

        refWatcher = LeakCanary.install(this);
        initializeObjectGraph();
    }

    public RefWatcher getRefWatcher() {
        return refWatcher;
    }

    public void inject(Object o) {
        objectGraph.inject(o);
    }

    private void initializeObjectGraph() {
        objectGraph = buildInitialObjectGraph(new AppModule(this));
    }

    private ObjectGraph buildInitialObjectGraph(Object... modules) {
        return ObjectGraph.create(modules);
    }

    public ObjectGraph buildScopedObjectGraph(Object... modules) {
        return objectGraph.plus(modules);
    }
}
