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

import android.content.Context;

import com.ewintory.udacity.popularmovies.data.DaggerDataComponent;
import com.ewintory.udacity.popularmovies.data.DataComponent;
import com.ewintory.udacity.popularmovies.data.DataModule;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public final class MoviesApp extends BaseApp {

    private DataComponent dataComponent;
    private RefWatcher refWatcher;

    public static MoviesApp get(Context context) {
        return (MoviesApp) context.getApplicationContext();
    }

    @Override public void onCreate() {
        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }

        refWatcher = installRefWatcher();

        setupTimber();
        setupDagger();
    }

    public RefWatcher getRefWatcher() {
        return refWatcher;
    }

    public DataComponent getDataComponent() {
        return dataComponent;
    }

    private void setupDagger() {
        final AppModule appModule = new AppModule(this);
        final DataModule dataModule = new DataModule();

        dataComponent = DaggerDataComponent.builder()
                .appModule(appModule)
                .dataModule(dataModule)
                .build();
    }
}
