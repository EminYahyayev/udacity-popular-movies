package com.ewintory.udacity.popularmovies.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ewintory.udacity.popularmovies.R;
import com.ewintory.udacity.popularmovies.data.api.MoviesApi;
import com.ewintory.udacity.popularmovies.data.model.Movie;
import com.ewintory.udacity.popularmovies.ui.activity.MovieDetailsActivity;
import com.ewintory.udacity.popularmovies.ui.module.MoviesModule;
import com.ewintory.udacity.popularmovies.utils.PrefUtils;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.squareup.sqlbrite.BriteDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.OnClick;
import timber.log.Timber;

public final class MovieFragment extends BaseFragment implements ObservableScrollViewCallbacks {
    public static final String ARG_MOVIE = "arg_movie";

    @Nullable @Bind(R.id.toolbar) Toolbar mToolbar;

    @Bind(R.id.movie_details_favorite_button) ImageButton mFavoritButton;
    @Bind(R.id.movie_details_title) TextView mTitle;
    @Bind(R.id.movie_details_release_date) TextView mReleaseDate;
    @Bind(R.id.movie_details_average_rating) TextView mRating;
    @Bind(R.id.movie_details_overview) TextView mOverview;

    @Bind(R.id.movie_details_poster) ImageView mPosterImage;
    @Bind(R.id.movie_details_cover) ImageView mCoverImage;
    @Bind(R.id.movie_details_cover_container) FrameLayout mCoverContainer;

    @Bind(R.id.scroll) ObservableScrollView mScrollView;

    @BindColor(R.color.theme_primary) int mColorThemePrimary;
    @BindColor(R.color.body_text_white) int mColorTextWhite;

    @Inject MoviesApi moviesApi;
    @Inject BriteDatabase db;

    private Movie mMovie;

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

        if (mToolbar != null) {
            ViewCompat.setElevation(mToolbar, getResources().getDimension(R.dimen.toolbar_elevation));
            mToolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, getResources().getColor(R.color.theme_primary)));
            mToolbar.setTitleTextColor(getResources().getColor(R.color.transparent));
            trySetupActionBar();
        }

        mScrollView.setScrollViewCallbacks(this);

        onMovieLoaded(getArguments().getParcelable(ARG_MOVIE));
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        ViewCompat.setTranslationY(mCoverContainer, scrollY / 2);

        if (mToolbar != null) {
            int parallaxImageHeight = mCoverContainer.getMeasuredHeight();
            float alpha = Math.min(1, (float) scrollY / parallaxImageHeight);
            mToolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, mColorThemePrimary));
            mToolbar.setTitleTextColor(ScrollUtils.getColorWithAlpha(alpha, mColorTextWhite));
        }
    }

    @Override
    public void onDownMotionEvent() { /** ignore */}

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) { /** ignore */}

    @OnClick(R.id.movie_details_favorite_button) void onFavored(ImageButton button) {
        if (mMovie == null) return;

        boolean wasFavored = mMovie.isFavored();
        Timber.d("onFavored: wasFavored=" + wasFavored);

        mMovie.setFavored(!wasFavored);
        if (wasFavored) {
            PrefUtils.removeFromFavorites(getActivity(), mMovie.getId());
            db.delete(Movie.TABLE, "_ID=?", mMovie.getId() + "");
        } else {
            PrefUtils.addToFavorites(getActivity(), mMovie.getId());
            db.insert(Movie.TABLE, new Movie.Builder()
                    .movie(mMovie)
                    .build());
        }
    }

    private void trySetupActionBar() {
        if (getActivity() instanceof MovieDetailsActivity) {
            MovieDetailsActivity activity = ((MovieDetailsActivity) getActivity());
            activity.setSupportActionBar(mToolbar);
            ActionBar ab = activity.getSupportActionBar();
            if (ab != null) {
                ab.setDisplayHomeAsUpEnabled(true);
                ab.setDisplayShowHomeEnabled(true);
            }
        }
    }

    private void onMovieLoaded(Movie movie) {
        Timber.v("onMovieLoaded: movie=" + movie);
        mMovie = movie;

        if (mToolbar != null) {
            mToolbar.setTitle(mMovie.getTitle());
        }

        mTitle.setText(movie.getTitle());
        mRating.setText(getString(R.string.movie_details_rating, movie.getVoteAverage()));
        mReleaseDate.setText(getDisplayReleaseDate(movie.getReleaseDate()));
        mOverview.setText(movie.getOverview());
        mFavoritButton.setSelected(movie.isFavored());

        Glide.with(getActivity())
                .load(movie.getBackdropPath())
                .centerCrop()
                .crossFade()
                .placeholder(R.color.movie_image_placeholder)
                .into(mCoverImage);

        Glide.with(getActivity())
                .load(movie.getPosterPath())
                .centerCrop()
                .crossFade()
                .into(mPosterImage);
    }

    private String getDisplayReleaseDate(String releaseDate) {
        if (TextUtils.isEmpty(releaseDate)) return "";

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(df.parse(releaseDate));
            return calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH) + " " + calendar.get(Calendar.YEAR);
        } catch (ParseException e) {
            Timber.e(e, "Failed to parse release date.");
            return "";
        }
    }

    @Override
    protected List<Object> getModules() {
        return Collections.<Object>singletonList(new MoviesModule());
    }

}
