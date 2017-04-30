package com.ewintory.udacity.popularmovies;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import timber.log.Timber;

/**
 * @author Emin Yahyayev
 */
public abstract class BaseApp extends Application {

    protected void setupTimber() {
        Timber.plant(new Timber.DebugTree() {
            // Adds the line number to the tag
            @Override protected String createStackElementTag(StackTraceElement element) {
                return super.createStackElementTag(element) + ":" + element.getLineNumber();
            }
        });
    }

    protected RefWatcher installRefWatcher(){
        return LeakCanary.install(this);
    }

}
