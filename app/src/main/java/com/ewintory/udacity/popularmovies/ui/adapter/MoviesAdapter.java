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
import com.ewintory.udacity.popularmovies.ui.listener.MovieClickListener;
import com.ewintory.udacity.popularmovies.ui.widget.AspectLockedFrameLayout;
import com.ewintory.udacity.popularmovies.utils.Lists;
import com.ewintory.udacity.popularmovies.utils.MoviesHelper;
import com.ewintory.udacity.popularmovies.utils.ResourceUtils;
import com.ewintory.udacity.popularmovies.utils.StringUtils;
import com.github.florent37.glidepalette.BitmapPalette;
import com.github.florent37.glidepalette.GlidePalette;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;

public final class MoviesAdapter extends EndlessAdapter<Movie, MoviesAdapter.MovieHolder> {

    @NonNull private final MoviesHelper mMoviesHelper;
    @NonNull private MovieClickListener mListener = MovieClickListener.DUMMY;

    public MoviesAdapter(Fragment fragment) {
        this(fragment, new ArrayList<Movie>());
    }

    public MoviesAdapter(Fragment fragment, List<Movie> movies) {
        super(fragment, movies);
        mMoviesHelper = new MoviesHelper(fragment.getActivity());
    }

    public void setListener(@NonNull MovieClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_ITEM) {
            ((MovieHolder) holder).bind(mItems.get(position));
        }
    }

    @Override
    protected MovieHolder onCreateItemHolder(ViewGroup parent, int viewType) {
        return new MovieHolder(mInflater.inflate(R.layout.item_movie, parent, false));
    }

    final class MovieHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.movie_item_container) View mContentContainer;
        @Bind(R.id.movie_item_image) ImageView mImageView;
        @Bind(R.id.movie_item_image_container) AspectLockedFrameLayout mImageContainer;
        @Bind(R.id.movie_item_title) TextView mTitle;
        @Bind(R.id.movie_item_genre) TextView mGenre;
        @Bind(R.id.movie_item_footer) View mFooter;
        @Bind(R.id.movie_item_btn_favorite) ImageButton mFavoriteButton;

        @BindColor(R.color.theme_primary) int mColorThemePrimary;
        @BindColor(R.color.body_text_1_inverse) int mColorBodyText1;
        @BindColor(R.color.body_text_2_inverse) int mColorBodyText2;

        public MovieHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bind(@NonNull final Movie movie) {
            mImageContainer.setAspectRatio(ResourceUtils.getFloatDimension(mFragment.getResources(), R.dimen.movie_item_image_aspect_ratio));

            mContentContainer.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    mListener.onContentClicked(movie, view);
                }
            });

            mFavoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    mListener.onFavoredClicked(movie, v);
                }
            });

            mTitle.setText(movie.getTitle());

            List<Integer> genreIds = movie.getGenreIds();
            if (!Lists.isEmpty(genreIds))
                mGenre.setText(StringUtils.join(mMoviesHelper.getGenreNames(genreIds), ", "));

            resetColors();
            Glide.with(mFragment)
                    .load(movie.getPosterPath())
                    .load(movie.getPosterPath())
                    .placeholder(R.color.movie_image_placeholder)
                    .crossFade()
                    .listener(GlidePalette.with(movie.getPosterPath()).intoCallBack(new BitmapPalette.CallBack() {
                        @Override public void onPaletteLoaded(Palette palette) {
                            applyColors(palette.getVibrantSwatch());
                        }
                    }))
                    .into(mImageView);
        }

        private void resetColors() {
            mFooter.setBackgroundColor(mColorThemePrimary);
            mTitle.setTextColor(mColorBodyText1);
            mGenre.setTextColor(mColorBodyText2);
            mFavoriteButton.setColorFilter(mColorBodyText1, PorterDuff.Mode.MULTIPLY);
        }

        private void applyColors(Palette.Swatch swatch) {
            if (swatch != null) {
                mFooter.setBackgroundColor(swatch.getRgb());
                mTitle.setTextColor(swatch.getBodyTextColor());
                mGenre.setTextColor(swatch.getTitleTextColor());
                mFavoriteButton.setColorFilter(swatch.getBodyTextColor(), PorterDuff.Mode.MULTIPLY);
            }
        }
    }
}