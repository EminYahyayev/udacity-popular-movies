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

package com.ewintory.udacity.popularmovies.ui.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ewintory.udacity.popularmovies.R;
import com.ewintory.udacity.popularmovies.data.model.Movie;
import com.ewintory.udacity.popularmovies.data.model.Review;
import com.ewintory.udacity.popularmovies.data.model.Video;
import com.ewintory.udacity.popularmovies.data.repository.MoviesRepository;
import com.ewintory.udacity.popularmovies.ui.activity.MovieDetailsActivity;
import com.ewintory.udacity.popularmovies.ui.helper.MoviesHelper;
import com.ewintory.udacity.popularmovies.ui.module.MoviesModule;
import com.ewintory.udacity.popularmovies.utils.Lists;
import com.ewintory.udacity.popularmovies.utils.UiUtils;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.OnClick;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static butterknife.ButterKnife.findById;

public final class MovieFragment extends BaseFragment implements ObservableScrollViewCallbacks {
    public static final String ARG_MOVIE = "arg_movie";

    private static final String STATE_SCROLL_VIEW = "state_scroll_view";
    private static final String STATE_REVIEWS = "state_reviews";
    private static final String STATE_VIDEOS = "state_trailers";

    @Nullable Toolbar mToolbar;

    @Bind(R.id.movie_scroll_view) ObservableScrollView mScrollView;
    @Bind(R.id.movie_poster) ImageView mPosterImage;
    @Bind(R.id.movie_poster_play) ImageView mPosterPlayImage;
    @Bind(R.id.movie_cover) ImageView mCoverImage;
    @Bind(R.id.movie_cover_container) FrameLayout mCoverContainer;

    @Bind(R.id.movie_favorite_button) ImageButton mFavoriteButton;
    @Bind(R.id.movie_title) TextView mTitle;
    @Bind(R.id.movie_release_date) TextView mReleaseDate;
    @Bind(R.id.movie_average_rating) TextView mRating;
    @Bind(R.id.movie_overview) TextView mOverview;
    @Bind(R.id.movie_reviews_container) ViewGroup mReviewsGroup;
    @Bind(R.id.movie_videos_container) ViewGroup mVideosGroup;

    @BindColor(R.color.theme_primary) int mColorThemePrimary;
    @BindColor(R.color.body_text_white) int mColorTextWhite;

    @Inject MoviesRepository mMoviesRepository;

    private MoviesHelper mHelper;
    private CompositeSubscription mSubscriptions;
    private List<Runnable> mDeferredUiOperations = new ArrayList<>();

    private Movie mMovie;
    private List<Review> mReviews;
    private List<Video> mVideos;
    private Video mTrailer;
    private MenuItem mMenuItemShare;

    public static MovieFragment newInstance(Movie movie) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_MOVIE, movie);

        MovieFragment fragment = new MovieFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setHasOptionsMenu(true);
        mHelper = new MoviesHelper(activity, mMoviesRepository);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movie, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        trySetupToolbar();
        mScrollView.setScrollViewCallbacks(this);

        if (savedInstanceState != null) {
            mVideos = savedInstanceState.getParcelableArrayList(STATE_VIDEOS);
            mReviews = savedInstanceState.getParcelableArrayList(STATE_REVIEWS);
            mScrollView.onRestoreInstanceState(savedInstanceState.getParcelable(STATE_SCROLL_VIEW));
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSubscriptions = new CompositeSubscription();

        onScrollChanged(mScrollView.getCurrentScrollY(), false, false);

        onMovieLoaded(getArguments().getParcelable(ARG_MOVIE));

        if (mReviews != null) onReviewsLoaded(mReviews);
        else loadReviews();

        if (mVideos != null) onVideosLoaded(mVideos);
        else loadVideos();

        // subscribe to global favored changes in order to synchronise movies from different views
        mSubscriptions.add(mHelper.getFavoredObservable()
                .filter(event -> ((mMovie != null)
                        && (mMovie.getId() == event.movieId)))
                .subscribe(movie -> {
                    mMovie.setFavored(movie.favored);
                    mFavoriteButton.setSelected(movie.favored);
                }));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_movie, menu);
        mMenuItemShare = menu.findItem(R.id.menu_share);
        tryExecuteDeferredUiOperations();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_share) {
            if (mTrailer != null)
                mHelper.shareTrailer(R.string.share_template, mTrailer);
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_SCROLL_VIEW, mScrollView.onSaveInstanceState());
        if (mReviews != null) outState.putParcelableArrayList(STATE_REVIEWS, new ArrayList<>(mReviews));
        if (mVideos != null) outState.putParcelableArrayList(STATE_VIDEOS, new ArrayList<>(mVideos));
    }

    @Override
    public void onDestroyView() {
        mSubscriptions.unsubscribe();
        super.onDestroyView();
    }

    @OnClick(R.id.movie_favorite_button)
    public void onFavored(ImageButton button) {
        if (mMovie == null) return;

        boolean favored = !mMovie.isFavored();
        button.setSelected(favored);
        mHelper.setMovieFavored(mMovie, favored);
        if (favored) showToast(R.string.message_movie_favored);
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

    private void trySetupToolbar() {
        if (getActivity() instanceof MovieDetailsActivity) {
            MovieDetailsActivity activity = ((MovieDetailsActivity) getActivity());
            mToolbar = activity.getToolbar();
        }
    }

    private void onMovieLoaded(Movie movie) {
        mMovie = movie;

        if (mToolbar != null) {
            mToolbar.setTitle(mMovie.getTitle());
        }

        mTitle.setText(movie.getTitle());
        mRating.setText(getString(R.string.movie_details_rating, movie.getVoteAverage()));
        mReleaseDate.setText(UiUtils.getDisplayReleaseDate(movie.getReleaseDate()));
        mOverview.setText(movie.getOverview());
        mFavoriteButton.setSelected(movie.isFavored());

        // Cover image
        Glide.with(this).load(movie.getBackdropPath())
                .placeholder(R.color.movie_cover_placeholder)
                .centerCrop()
                .crossFade()
                .into(mCoverImage);

        // Poster image
        Glide.with(this).load(movie.getPosterPath())
                .centerCrop()
                .crossFade()
                .into(mPosterImage);
    }

    private void loadReviews() {
        mSubscriptions.add(mMoviesRepository.reviews(mMovie.getId())
                .subscribe(reviews -> {
                    Timber.d(String.format("Reviews loaded, %d items.", reviews.size()));
                    onReviewsLoaded(reviews);
                }, throwable -> {
                    Timber.e(throwable, "Reviews loading failed.");
                    onReviewsLoaded(null);
                }));
    }

    private void onReviewsLoaded(List<Review> reviews) {
        mReviews = reviews;
        // Remove all existing reviews (everything but first two children)
        for (int i = mReviewsGroup.getChildCount() - 1; i >= 2; i--) {
            mReviewsGroup.removeViewAt(i);
        }

        final LayoutInflater inflater = LayoutInflater.from(getActivity());
        boolean hasReviews = false;

        if (!Lists.isEmpty(reviews)) {
            for (Review review : reviews) {
                if (TextUtils.isEmpty(review.getAuthor())) {
                    continue;
                }

                final View reviewView = inflater.inflate(R.layout.item_review_detail, mReviewsGroup, false);
                final TextView reviewAuthorView = findById(reviewView, R.id.review_author);
                final TextView reviewContentView = findById(reviewView, R.id.review_content);

                reviewAuthorView.setText(review.getAuthor());
                reviewContentView.setText(review.getContent());

                mReviewsGroup.addView(reviewView);
                hasReviews = true;
            }
        }

        mReviewsGroup.setVisibility(hasReviews ? View.VISIBLE : View.GONE);
    }

    private void loadVideos() {
        mSubscriptions.add(mMoviesRepository.videos(mMovie.getId()).subscribe(videos -> {
            Timber.d(String.format("Videos loaded, %d items.", videos.size()));
            Timber.d("Videos: " + videos);
            onVideosLoaded(videos);
        }, throwable -> {
            Timber.e(throwable, "Videos loading failed.");
            onVideosLoaded(null);
        }));
    }

    private void onVideosLoaded(List<Video> videos) {
        mVideos = videos;

        // Remove all existing videos (everything but first two children)
        for (int i = mVideosGroup.getChildCount() - 1; i >= 2; i--) {
            mVideosGroup.removeViewAt(i);
        }

        final LayoutInflater inflater = LayoutInflater.from(getActivity());

        boolean hasVideos = false;
        if (!Lists.isEmpty(videos)) {
            for (Video video : mVideos)
                if (video.getType().equals(Video.TYPE_TRAILER)) {
                    Timber.d("Found trailer!");
                    mTrailer = video;

                    mCoverContainer.setTag(video);
                    mCoverContainer.setOnClickListener(view -> mHelper.playVideo((Video) view.getTag()));
                    break;
                }

            for (Video video : videos) {
                final View videoView = inflater.inflate(R.layout.item_video, mVideosGroup, false);
                final TextView videoNameView = findById(videoView, R.id.video_name);

                videoNameView.setText(video.getSite() + ": " + video.getName());
                videoView.setTag(video);
                videoView.setOnClickListener(v -> {
                    mHelper.playVideo((Video) v.getTag());
                });

                mVideosGroup.addView(videoView);
                hasVideos = true;
            }
        }

        showShareMenuItemDeferred(mTrailer != null);
        mCoverContainer.setClickable(mTrailer != null);
        mPosterPlayImage.setVisibility(mTrailer != null ? View.VISIBLE : View.GONE);
        mVideosGroup.setVisibility(hasVideos ? View.VISIBLE : View.GONE);
    }

    private void showShareMenuItemDeferred(boolean visible) {
        mDeferredUiOperations.add(() -> mMenuItemShare.setVisible(visible));
        tryExecuteDeferredUiOperations();
    }

    private void tryExecuteDeferredUiOperations() {
        if (mMenuItemShare != null) {
            for (Runnable r : mDeferredUiOperations) {
                r.run();
            }
            mDeferredUiOperations.clear();
        }
    }


    @Override
    protected List<Object> getModules() {
        return Collections.<Object>singletonList(new MoviesModule());
    }

}
