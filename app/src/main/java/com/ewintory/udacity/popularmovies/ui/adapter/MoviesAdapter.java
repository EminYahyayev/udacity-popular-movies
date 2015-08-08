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

package com.ewintory.udacity.popularmovies.ui.adapter;

import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ewintory.udacity.popularmovies.R;
import com.ewintory.udacity.popularmovies.data.model.Movie;
import com.ewintory.udacity.popularmovies.utils.ResourceUtils;
import com.ewintory.udacity.popularmovies.utils.UiUtils;
import com.github.florent37.glidepalette.GlidePalette;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;
import timber.log.Timber;

public final class MoviesAdapter extends EndlessAdapter<Movie, MoviesAdapter.MovieHolder> {

    public interface OnMovieClickListener {
        void onContentClicked(@NonNull final Movie movie, View view, int position);

        void onFavoredClicked(@NonNull final Movie movie, int position);

        OnMovieClickListener DUMMY = new OnMovieClickListener() {
            @Override public void onContentClicked(@NonNull Movie movie, View view, int position) {}

            @Override public void onFavoredClicked(@NonNull Movie movie, int position) { }
        };
    }

    @NonNull private final Fragment mFragment;
    @NonNull private OnMovieClickListener mListener = OnMovieClickListener.DUMMY;

    public MoviesAdapter(@NonNull Fragment fragment, List<Movie> movies) {
        super(fragment.getActivity(), movies == null ? new ArrayList<>() : movies);
        mFragment = fragment;
        setHasStableIds(true);
    }

    public void setListener(@NonNull OnMovieClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public long getItemId(int position) {
        return (!isLoadMore(position)) ? mItems.get(position).getId() : -1;
    }

    @Override
    protected MovieHolder onCreateItemHolder(ViewGroup parent, int viewType) {
        return new MovieHolder(mInflater.inflate(R.layout.item_movie, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_ITEM) {
            ((MovieHolder) holder).bind(mItems.get(position));
        }
    }

    final class MovieHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.movie_item_container) View mContentContainer;
        @Bind(R.id.movie_item_image) ImageView mImageView;
        @Bind(R.id.movie_item_title) TextView mTitleView;
        @Bind(R.id.movie_item_genres) TextView mGenresView;
        @Bind(R.id.movie_item_footer) View mFooterView;
        @Bind(R.id.movie_item_btn_favorite) ImageButton mFavoriteButton;

        @BindColor(R.color.theme_primary) int mColorBackground;
        @BindColor(R.color.body_text_white) int mColorTitle;
        @BindColor(R.color.body_text_1_inverse) int mColorSubtitle;

        private final StringBuilder mBuilder = new StringBuilder(30);
        private long mMovieId;

        public MovieHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bind(@NonNull final Movie movie) {
            mContentContainer.setOnClickListener(view -> mListener.onContentClicked(movie, view, getAdapterPosition()));

            mFavoriteButton.setSelected(movie.isFavored());
            mFavoriteButton.setOnClickListener(view -> {
                mFavoriteButton.setSelected(!movie.isFavored());
                mListener.onFavoredClicked(movie, getAdapterPosition());
            });

            mTitleView.setText(movie.getTitle());
            mGenresView.setText(UiUtils.joinGenres(movie.getGenres(), ", ", mBuilder));

            // prevents unnecessary color blinking
            if (mMovieId != movie.getId()) {
                resetColors();
                mMovieId = movie.getId();
            }

            Glide.with(mFragment)
                    .load(movie.getPosterPath())
                    .crossFade()
                    .placeholder(R.color.movie_poster_placeholder)
                    .listener(GlidePalette.with(movie.getPosterPath())
                            .intoCallBack(palette -> applyColors(palette.getVibrantSwatch())))
                    .into(mImageView);
        }

        private void resetColors() {
            mFooterView.setBackgroundColor(mColorBackground);
            mTitleView.setTextColor(mColorTitle);
            mGenresView.setTextColor(mColorSubtitle);
            mFavoriteButton.setColorFilter(mColorTitle, PorterDuff.Mode.MULTIPLY);
        }

        private void applyColors(Palette.Swatch swatch) {
            if (swatch != null) {
                mFooterView.setBackgroundColor(swatch.getRgb());
                mTitleView.setTextColor(swatch.getBodyTextColor());
                mGenresView.setTextColor(swatch.getTitleTextColor());
                mFavoriteButton.setColorFilter(swatch.getBodyTextColor(), PorterDuff.Mode.MULTIPLY);
            }
        }
    }
}