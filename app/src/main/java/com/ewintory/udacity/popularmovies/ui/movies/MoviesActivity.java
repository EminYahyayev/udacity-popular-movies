package com.ewintory.udacity.popularmovies.ui.movies;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ewintory.udacity.popularmovies.R;
import com.ewintory.udacity.popularmovies.ui.BaseActivity;

import butterknife.BindView;

/**
 * @author Emin Yahyayev
 */
public final class MoviesActivity extends BaseActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.main_content) CoordinatorLayout coordinatorLayout;

    @NonNull @Override
    protected View getSnackbarView() {
        return coordinatorLayout;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        setSupportActionBar(toolbar);

        final FragmentManager fm = getSupportFragmentManager();

        MoviesFragment moviesFragment = (MoviesFragment) fm.findFragmentById(R.id.fragment_container);
        if (moviesFragment == null) {
            moviesFragment = new MoviesFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, moviesFragment, "fragment_movies")
                    .commit();
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.movies_title);
        }
    }
}
