package com.ewintory.udacity.popularmovies.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ewintory.udacity.popularmovies.R;
import com.ewintory.udacity.popularmovies.data.model.Movie;

import butterknife.Bind;

public final class MovieFragment extends BaseFragment {
    public static final String TAG = MoviesFragment.class.getSimpleName();

    @Bind(R.id.movie_poster) ImageView mPoster;
    @Bind(R.id.movie_overview) TextView mOverview;
    @Bind(R.id.movie_average_rating) TextView mRating;

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movie, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void setMovie(Movie movie) {
        mOverview.setText(movie.getOverview());
        mRating.setText(getString(R.string.movie_details_rating, movie.getVoteAverage()));

        Glide.with(getActivity())
                .load(movie.getPosterPath())
                .into(mPoster);
    }
}
