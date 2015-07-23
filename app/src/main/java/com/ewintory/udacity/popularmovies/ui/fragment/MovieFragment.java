package com.ewintory.udacity.popularmovies.ui.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ewintory.udacity.popularmovies.R;
import com.ewintory.udacity.popularmovies.data.model.Movie;
import com.ewintory.udacity.popularmovies.ui.module.MoviesModule;
import com.ewintory.udacity.popularmovies.ui.widget.AspectLockedFrameLayout;
import com.ewintory.udacity.popularmovies.utils.ResourceUtils;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import timber.log.Timber;

public final class MovieFragment extends BaseFragment {
    public static final String ARG_MOVIE = "arg_movie";

    @Bind(R.id.movie_details_image) ImageView mImageView;
    @Bind(R.id.movie_details_image_container) AspectLockedFrameLayout mImageContainer;
    @Bind(R.id.movie_details_title) TextView mTitle;
    @Bind(R.id.movie_details_overview) TextView mOverview;
    @Bind(R.id.movie_details_average_rating) TextView mRating;

    public static MovieFragment newInstance(Movie movie) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_MOVIE, movie);

        MovieFragment fragment = new MovieFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movie, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        applyMovie(getArguments().getParcelable(ARG_MOVIE));
    }

    private void applyMovie(Movie movie) {
        Timber.v("applyMovie: movie=" + movie);

        mTitle.setText(movie.getTitle());
        mOverview.setText(movie.getOverview());
        mRating.setText(getString(R.string.movie_details_rating, movie.getVoteAverage()));

        mImageContainer.setAspectRatio(ResourceUtils.getFloatDimension(getResources(), R.dimen.movie_details_image_aspect_ratio));
        Glide.with(getActivity())
                .load(movie.getBackdropPath())
                .fitCenter()
                .crossFade()
                .into(mImageView);
    }

    @Override
    protected List<Object> getModules() {
        return Collections.<Object>singletonList(new MoviesModule());
    }
}
