package com.ewintory.udacity.popularmovies.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ewintory.udacity.popularmovies.R;
import com.ewintory.udacity.popularmovies.data.api.Sort;
import com.ewintory.udacity.popularmovies.data.model.Movie;
import com.ewintory.udacity.popularmovies.ui.fragment.SortedMoviesFragment;
import com.ewintory.udacity.popularmovies.ui.listener.MovieClickListener;
import com.ewintory.udacity.popularmovies.utils.PrefUtils;

import timber.log.Timber;

public final class BrowseMoviesActivity extends BaseActivity implements MovieClickListener {
    private static final String STATE_SORT = "STATE_SORT";

    private SortedMoviesFragment mSortedMoviesFragment;
    private Sort mSort;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        mSort = (savedInstanceState != null) ?
                (Sort) savedInstanceState.getSerializable(STATE_SORT)
                : PrefUtils.getMoviesSort(this);

        Timber.d("onPostCreate: mSort=" + mSort);
    }

    @Override protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mSortedMoviesFragment = (SortedMoviesFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_movies);
        mSortedMoviesFragment.reloadFromSort(mSort);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_browse_movies, menu);
        return true;
    }

    @Override public boolean onPrepareOptionsMenu(Menu menu) {
        switch (mSort) {
            case POPULARITY:
                menu.findItem(R.id.menu_sort_popularity).setChecked(true);
                break;
            case VOTE_AVERAGE:
                menu.findItem(R.id.menu_sort_vote_average).setChecked(true);
                break;
            case VOTE_COUNT:
                menu.findItem(R.id.menu_sort_vote_count).setChecked(true);
                break;
        }
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort_popularity:
                onSortSelected(item, Sort.POPULARITY);
                break;
            case R.id.menu_sort_vote_average:
                onSortSelected(item, Sort.VOTE_AVERAGE);
                break;
            case R.id.menu_sort_vote_count:
                onSortSelected(item, Sort.VOTE_COUNT);
                break;
            case R.id.menu_refresh:
                mSortedMoviesFragment.reloadFromSort(mSort);
                break;
            case R.id.menu_scroll_to_top:
                mSortedMoviesFragment.scrollToTop(true);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_SORT, mSort);
    }

    @Override protected void onPause() {
        PrefUtils.setMoviesSort(this, mSort);
        super.onPause();
    }

    @Override public void onContentClicked(Movie movie, View view) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra(MovieDetailsActivity.EXTRA_MOVIE, movie);
        startActivity(intent);
    }

    @Override public void onFavoredClicked(Movie movie, View view) {
        Toast.makeText(this, "Favored!", Toast.LENGTH_SHORT).show();
    }

    private void onSortSelected(MenuItem item, Sort sort) {
        if (!item.isChecked()) {
            item.setChecked(true);
            onSortChanged(sort);
        }
    }

    private void onSortChanged(@NonNull Sort sort) {
        mSortedMoviesFragment.reloadFromSort(mSort = sort);
        mSortedMoviesFragment.scrollToTop(false);
    }
}
