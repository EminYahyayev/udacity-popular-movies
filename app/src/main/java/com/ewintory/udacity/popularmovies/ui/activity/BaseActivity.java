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

package com.ewintory.udacity.popularmovies.ui.activity;

import android.app.Activity;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.ewintory.udacity.popularmovies.MoviesApplication;
import com.ewintory.udacity.popularmovies.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.app.AppObservable;
import timber.log.Timber;

/**
 * Base class for all activities.
 * Binds Views
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Nullable @Bind(R.id.toolbar) Toolbar mToolbar;

    @CallSuper
    @Override protected void onDestroy() {
        super.onDestroy();
        MoviesApplication.get(this).getRefWatcher().watch(this);
    }

    @CallSuper
    @Override public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
        setupToolbar();
    }

    @Nullable
    public final Toolbar getToolbar() {
        return mToolbar;
    }

    private void setupToolbar() {
        if (mToolbar == null) {
            Timber.w("Didn't find a toolbar");
            return;
        }

        ViewCompat.setElevation(mToolbar, getResources().getDimension(R.dimen.toolbar_elevation));
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) return;
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(false);
    }

    /**
     * Binds the given source sequence to an Activity.
     *
     * @see AppObservable#bindActivity(Activity, Observable)
     */
    protected final <T> Observable<T> bind(Observable<T> source) {
        return AppObservable.bindActivity(this, source);
    }
}
