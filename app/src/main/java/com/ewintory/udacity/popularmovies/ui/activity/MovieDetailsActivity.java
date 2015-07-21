package com.ewintory.udacity.popularmovies.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.ewintory.udacity.popularmovies.R;
import com.ewintory.udacity.popularmovies.data.model.Movie;
import com.ewintory.udacity.popularmovies.data.model.Video;
import com.ewintory.udacity.popularmovies.ui.fragment.MovieFragment;

import butterknife.Bind;

public final class MovieDetailsActivity extends BaseActivity {
    public static final String EXTRA_MOVIE = "com.ewintory.udacity.popularmovies.extras.EXTRA_MOVIE";

    @Bind(R.id.movie_title) TextView mMovieTitle;
    @Bind(R.id.movie_release_year) TextView mReleaseYear;

    private MovieFragment mMovieFragment;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowHomeEnabled(true);
            ab.setTitle("");
        }

        final Toolbar toolbar = getToolbar();
        if (toolbar != null) {
            ViewCompat.setElevation(toolbar, 0);
        }

        Movie movie = getIntent().getParcelableExtra(EXTRA_MOVIE);
        setMovie(movie);

        mMovieFragment = (MovieFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_movie);
        mMovieFragment.setMovie(movie);
    }

    private void setMovie(Movie movie) {
        mMovieTitle.setText(movie.getTitle());

        String date = movie.getReleaseDate();
        if (date != null)
            mReleaseYear.setText(date.substring(0, 4));
    }

}
