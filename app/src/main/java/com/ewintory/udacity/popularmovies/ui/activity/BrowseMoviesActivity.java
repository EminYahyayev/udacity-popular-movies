package com.ewintory.udacity.popularmovies.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ewintory.udacity.popularmovies.R;
import com.ewintory.udacity.popularmovies.data.model.Movie;
import com.ewintory.udacity.popularmovies.data.model.Sort;
import com.ewintory.udacity.popularmovies.ui.fragment.MoviesFragment;
import com.ewintory.udacity.popularmovies.ui.listener.MovieClickListener;

public final class BrowseMoviesActivity extends BaseActivity implements MovieClickListener {
    private static final String STATE_SORT = "STATE_SORT";

    private MoviesFragment mMoviesFragment;
    private Sort mSort = Sort.POPULARITY_DESC;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        mMoviesFragment = (MoviesFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_movies);
    }

    @Override protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (savedInstanceState != null)
            mSort = (Sort) savedInstanceState.getSerializable(STATE_SORT);

        Log.d(TAG, "Sort=" + mSort);

        mMoviesFragment.reloadMovies(mSort);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_browse_movies, menu);
        return true;
    }

    @Override public boolean onPrepareOptionsMenu(Menu menu) {
        switch (mSort) {
            case POPULARITY_DESC:
                menu.findItem(R.id.menu_sort_popularity).setChecked(true);
                break;
            case VOTE_AVERAGE_DESC:
                menu.findItem(R.id.menu_sort_rating).setChecked(true);
        }
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort_popularity:
                item.setChecked(!item.isChecked());
                onSortChanged(Sort.POPULARITY_DESC);
                break;
            case R.id.menu_sort_rating:
                item.setChecked(!item.isChecked());
                onSortChanged(Sort.VOTE_AVERAGE_DESC);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_SORT, mSort);
    }

    @Override public void onContentClicked(Movie movie, View view) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra(MovieDetailsActivity.EXTRA_MOVIE, movie);
        startActivity(intent);
    }

    private void onSortChanged(@NonNull Sort sort) {
        mSort = sort;
        mMoviesFragment.reloadMovies(mSort);
        mMoviesFragment.scrollToTop();
    }
}
