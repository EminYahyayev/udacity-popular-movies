package com.ewintory.udacity.popularmovies.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ewintory.udacity.popularmovies.R;
import com.ewintory.udacity.popularmovies.data.model.Movie;
import com.ewintory.udacity.popularmovies.ui.module.MoviesModule;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import timber.log.Timber;

public final class MovieFragment extends BaseFragment {
    public static final String TAG = SortedMoviesFragment.class.getSimpleName();

    @Inject Picasso mPicasso;

    @Bind(R.id.movie_poster) ImageView mPoster;
    @Bind(R.id.movie_overview) TextView mOverview;
    @Bind(R.id.movie_average_rating) TextView mRating;

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movie, container, false);
    }

    public void setMovie(Movie movie) {
        Timber.v("setMovie: movie=" + movie);
        mOverview.setText(movie.getOverview());
        mRating.setText(getString(R.string.movie_details_rating, movie.getVoteAverage()));

        mPicasso.load(movie.getPosterPath())
                .fit()
                .into(mPoster);
    }

    @Override protected List<Object> getModules() {
        return Collections.<Object>singletonList(new MoviesModule());
    }
}
