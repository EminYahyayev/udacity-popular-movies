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
import com.ewintory.udacity.popularmovies.ui.widget.AspectLockedImageView;
import com.ewintory.udacity.popularmovies.utils.Lists;
import com.ewintory.udacity.popularmovies.utils.MoviesHelper;
import com.ewintory.udacity.popularmovies.utils.StringUtils;
import com.github.florent37.glidepalette.GlidePalette;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;

public final class MoviesAdapter extends EndlessAdapter<Movie, MoviesAdapter.MovieHolder> {

    @NonNull private final Fragment mFragment;
    @NonNull private final MoviesHelper mMoviesHelper;
    @NonNull private MovieClickListener mListener = MovieClickListener.DUMMY;

    public MoviesAdapter(@NonNull Fragment fragment) {
        this(fragment, new ArrayList<>());
    }

    public MoviesAdapter(@NonNull Fragment fragment, @NonNull List<Movie> movies) {
        super(fragment.getActivity(), movies);
        mMoviesHelper = new MoviesHelper(fragment.getActivity());
        mFragment = fragment;
        setHasStableIds(true);
    }

    public void setListener(@NonNull MovieClickListener listener) {
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
        @Bind(R.id.movie_item_title) TextView mTitle;
        @Bind(R.id.movie_item_genre) TextView mGenre;
        @Bind(R.id.movie_item_footer) View mFooter;
        @Bind(R.id.movie_item_btn_favorite) ImageButton mFavoriteButton;

        @BindColor(R.color.theme_primary) int mColorBackground;
        @BindColor(R.color.body_text_white) int mColorTextTitle;
        @BindColor(R.color.body_text_1_inverse) int mColorTextSubtitle;

        private long mMovieId;
        private Palette.Swatch mSwatch;

        public MovieHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bind(@NonNull final Movie movie) {
            mContentContainer.setOnClickListener(view -> mListener.onContentClicked(movie, view, mSwatch));

            mFavoriteButton.setSelected(movie.isFavored());
            mFavoriteButton.setOnClickListener(view -> {
                mFavoriteButton.setSelected(!movie.isFavored());
                mListener.onFavoredClicked(movie);
            });

            mTitle.setText(movie.getTitle());

            List<Integer> genreIds = movie.getGenreIds();
            if (!Lists.isEmpty(genreIds))
                mGenre.setText(StringUtils.join(mMoviesHelper.getGenreNames(genreIds), ", "));

            // prevents unnecessary color blinking
            if (mMovieId != movie.getId()) {
                resetColors();
                mMovieId = movie.getId();
            }

            Glide.with(mFragment)
                    .load(movie.getPosterPath())
                    .placeholder(R.color.movie_image_placeholder)
                    .crossFade()
                    .listener(GlidePalette.with(movie.getPosterPath())
                            .intoCallBack(palette -> applyColors(palette.getVibrantSwatch())))
                    .into(mImageView);
        }

        private void resetColors() {
            mFooter.setBackgroundColor(mColorBackground);
            mTitle.setTextColor(mColorTextTitle);
            mGenre.setTextColor(mColorTextSubtitle);
            mFavoriteButton.setColorFilter(mColorTextTitle, PorterDuff.Mode.MULTIPLY);
        }

        private void applyColors(Palette.Swatch swatch) {
            if (swatch != null) {
                mSwatch = swatch;
                mFooter.setBackgroundColor(swatch.getRgb());
                mTitle.setTextColor(swatch.getBodyTextColor());
                mGenre.setTextColor(swatch.getTitleTextColor());
                mFavoriteButton.setColorFilter(swatch.getBodyTextColor(), PorterDuff.Mode.MULTIPLY);
            }
        }
    }
}